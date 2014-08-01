package com.scxj.fragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.pisoft.base.db.DBHelper;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.adapter.ListAdapter;
import com.scxj.dao.AttachDefectDao;
import com.scxj.dao.AttachTaskDao;
import com.scxj.dao.InitDefectTaskDao;
import com.scxj.dao.InitTaskDao;
import com.scxj.dao.TaskDao;
import com.scxj.dao.TaskDefectDao;
import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.FileUtil;
import com.scxj.utils.StreamUtils;
import com.scxj.utils.StringUtils;
import com.scxj.utils.usb.NetAdapter;

/*
 * 数据同步
 * */
public class FragmentDataMain extends BaseFragment implements OnClickListener {
	private List<TB_TASK> taskList;
	private List<TB_TASK_DEFECT> taskDefectList;
	private ListView lv = null;
	private TaskMainAdapter adapter;
	private TextView titleTv01;
	private Button taskBtn;
	private Button defBtn;
	private Button uploadBtn;
	private Button downloadBtn;
	private boolean isTask = true, isNeedLoadData = true;
	private ProgressDialog progress = null;
	private TaskDao taskDao;
	private TaskDefectDao taskDefectDao;
	private AttachTaskDao attachTaskDao;
	private List<TB_TASK> choiceTaskList;
	private List<TB_TASK_DEFECT> choiceTaskDefectList;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (progress != null) {
				progress.dismiss();
				progress = null;
			}

			switch (msg.what) {
			case 1:
				if (progress != null) {
					progress.dismiss();
					progress = null;
				}
				progress = ProgressDialog.show(getActivity(), "提示",
						"正在上传任务结果数据……");
				progress.setCancelable(false);
				uploadTaskRet();
				break;
			case 2:
				if (progress != null) {
					progress.dismiss();
					progress = null;
				}
				progress = ProgressDialog.show(getActivity(), "提示",
						"正在上传消缺任务结果数据……");
				progress.setCancelable(false);
				uploadTaskDefectRet();
				break;
			case 3:
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
					AudioTipsUtils.showMsg(getActivity(), msg.obj.toString());
				}
				downloadTaskInfo();
				break;
			case 4:
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
					AudioTipsUtils.showMsg(getActivity(), msg.obj.toString());
				}
				downloadTaskDefectInfo();
				break;
			case 9:
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
					AudioTipsUtils.showMsg(getActivity(), msg.obj.toString());
				}
				break;
			case 11:
				if (progress != null) {
					progress.dismiss();
					progress = null;
				}
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
				AudioTipsUtils.showMsg(getActivity(), msg.obj.toString());
				}
				break;

			case 12:
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
					AudioTipsUtils.showMsg(getActivity(), msg.obj.toString());
				}
				if (progress != null) {
					progress.dismiss();
					progress = null;
				}
				progress = ProgressDialog.show(getActivity(), "提示",
						"正在处理任务结果数据……");
				progress.setCancelable(false);
				dealUploadTaskRet();
				break;
			case 13:
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
					AudioTipsUtils.showMsg(getActivity(), msg.obj.toString());
				}
				if (progress != null) {
					progress.dismiss();
					progress = null;
				}
				progress = ProgressDialog.show(getActivity(), "提示",
						"正在处理消缺任务结果数据……");
				progress.setCancelable(false);
				dealUploadTaskDefectRet();
				break;	
			case 20:
				if (progress != null) {
					progress.dismiss();
					progress = null;
				}
				progress = ProgressDialog.show(getActivity(), "提示", "正在闭环巡视任务……");
				progress.setCancelable(false);
				dealTaskStatus();
				break;
			case 22:
				if (progress != null) {
					progress.dismiss();
					progress = null;
				}
				progress = ProgressDialog.show(getActivity(), "提示", "正在闭环消缺任务……");
				progress.setCancelable(false);
				dealTaskDefectStatus();
				break;	
			case 21:
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
					AudioTipsUtils.showMsg(getActivity(), msg.obj.toString());
				}
				onDataRefreshListener.refreshDatas();
				break;
			default:
				break;
			}

		};
	};

	private OnDataRefreshListener onDataRefreshListener = new OnDataRefreshListener() {

		@Override
		public void refreshDatas() {
			if (isTask) {
				taskBtn.setSelected(true);
				defBtn.setSelected(false);
				titleTv01.setText("周期");
				// 添加不同的数据源
				if (isNeedLoadData || taskList == null) {
					taskList = taskDao.getAllList(2);
				}
				if (adapter == null) {
					adapter = new TaskMainAdapter(getActivity(), taskList);
					lv.setAdapter(adapter);
				} else {
					adapter.setDatas(taskList);
					adapter.setTask(isTask);
					adapter.notifyDataSetChanged();
				}
			} else {
				taskBtn.setSelected(false);
				defBtn.setSelected(true);
				titleTv01.setText("缺陷类别");
				// 添加不同的数据源
				if (isNeedLoadData || taskDefectList == null) {
					taskDefectList = new TaskDefectDao(getActivity())
							.getAllTaskDefect(2);//未上传的任务
				}

				if (adapter == null) {
					adapter = new TaskMainAdapter(getActivity(), taskDefectList);
					lv.setAdapter(adapter);
				} else {
					adapter.setDatas(taskDefectList);
					adapter.setTask(isTask);
					adapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		this.mFragmentManager = getChildFragmentManager();
		View layout = inflater.inflate(R.layout.task_upload, container, false);
		return layout;
	}

	protected void dealTaskDefectStatus() {
		final String userName = MyApplication.getInstance().loginUser
				.getUSERNAME();
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (TB_TASK_DEFECT item : choiceTaskDefectList) {
					String ret = NetAdapter.updateTaskStatus(item.getDEFECTTASKID(),
							"消缺任务", "任务闭环");
					if (!StringUtils.isNull(ret)) {
						try {
							JSONObject jb = new JSONObject(ret);
							if ("0".equals(jb.getString("resultCode"))) {
							//	taskDefectDao.updateStatus(item.getDEFECTTASKID(), "已上传");
								String filePath = DBHelper.DB_PATH + "/d"+item.getDEFECTTASKID()+"/";
								//删除任务相关的
								FileUtil.deleteFile(filePath);
								taskDefectDao.delItem(item);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

				if (taskDefectDao.doAll()) {// 删除文件
					List<TB_TASK_DEFECT> delTaskList = taskDefectDao.getAllTaskDefect(3);
					for (TB_TASK_DEFECT delTask : delTaskList) {// 删除回传库
						String filePath = DBHelper.DB_PATH + "/" + userName
								+ "/d" + delTask.getDEFECTTASKID() + "/";
						FileUtil.deleteFile(filePath);
					}
					String taskDbFilePath = DBHelper.DB_PATH + "/" + userName
							+ "/TRNTASKDEFECT.DB";
					FileUtil.deleteFile(taskDbFilePath);// 删除任务库文件
				}
				mHandler.obtainMessage(21, "消缺任务闭环操作完成！").sendToTarget();
			}
		}).start();
	}

	protected void dealUploadTaskDefectRet() {
		List<TB_TASK_DEFECT> uploadTaskList = taskDefectDao.getAllTaskDefect(1);
		final List<TB_TASK_DEFECT> overTaskList = new ArrayList<TB_TASK_DEFECT>();
		ArrayList<String> overTaskArr = new ArrayList<String>();
		List<Boolean> overTaskBol = new ArrayList<Boolean>();
		for (TB_TASK_DEFECT task : uploadTaskList) {//
			 
			if ("已消缺".equals(task.getSTATUS())) {
				overTaskList.add(task);
				overTaskArr.add(task.getDEFECTTASKNAME()+task.getDEFECTTASKID() + "[可闭环]");
				overTaskBol.add(false);
			}
		}

		choiceTaskDefectList = new ArrayList<TB_TASK_DEFECT>();
		if (!overTaskArr.isEmpty() && overTaskArr.size() > 0) {
			
			String[] strArr = (String[])overTaskArr.toArray(new String[overTaskArr.size()]);
			new AlertDialog.Builder(getActivity())
					.setTitle("选择可闭环任务")
					.setMultiChoiceItems(strArr, null,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton, boolean isChecked) {
									// 点击某个区域
									Log.w("DataMain", isChecked + "_"
											+ whichButton);
									TB_TASK_DEFECT task = overTaskList
											.get(whichButton);
									if (isChecked) {
										if (!choiceTaskDefectList.contains(task)) {
											choiceTaskDefectList.add(task);
										}
									}else{
										if (choiceTaskDefectList.contains(task)) {
											choiceTaskDefectList.remove(task);
										}
									}

								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
									mHandler.obtainMessage(22).sendToTarget();
								}

							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									mHandler.obtainMessage(11).sendToTarget();
								}
							}).create().show();
		}else{
			mHandler.obtainMessage(11).sendToTarget();
		}
		
	}

	protected void downloadTaskDefectInfo() {
		progress = ProgressDialog.show(getActivity(), "提示",
				"正在下载消缺任务数据……");
		progress.setCancelable(false);
		final String userName = MyApplication.getInstance().loginUser.getUSERNAME();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// 任务数据
				String taskPath = DBHelper.DB_PATH + "/" + userName
						+ "/TRNTASKDEFECT.DB";
				File taskFile = new File(taskPath);
				
				TaskDefectDao taskDefectDao = new TaskDefectDao(getActivity());
				
				if (taskFile.exists() && !taskDefectDao.isEmptyTask("/"+userName+ "/TRNTASKDEFECT.DB")) {
					boolean patrolTask = NetAdapter.downLoadPatrolDefectDBADD(userName);
					if (patrolTask) {
						String bakPath = "/" + userName + "/TRNTASKDEFECTBAK.DB";
						InitDefectTaskDao initTaskDao  = new InitDefectTaskDao(getActivity(),bakPath);
						initTaskDao.initTargetDbData();
						mHandler.obtainMessage(21, "消缺任务数据更新成功！").sendToTarget() ;
					} else {
						mHandler.obtainMessage(21, "消缺任务数据更新失败！").sendToTarget() ;
					}
					
					
					
				} else {
					boolean patrolTask = NetAdapter.downLoadPatrolDefectDB(userName);
					if (patrolTask) {
						mHandler.obtainMessage(21, "消缺任务数据更新成功！").sendToTarget() ;
					} else {
						mHandler.obtainMessage(21, "消缺任务数据更新失败！").sendToTarget() ;
					}
				}
			}
		}).start();
		
	}

	protected void downloadTaskInfo() {
		progress = ProgressDialog.show(getActivity(), "提示",
				"正在任务数据……");
		progress.setCancelable(false);
		final String userName = MyApplication.getInstance().loginUser.getUSERNAME();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// 任务数据
				String defectPath = DBHelper.DB_PATH + "/" + userName
						+ "/TRNTASK.DB";
				File defectFile = new File(defectPath);
				
				
				if (defectFile.exists() && !taskDao.isEmptyTask("/"+userName+ "/TRNTASK.DB")) {
					boolean patrolTask = NetAdapter.downLoadPatrolTaskDBADD(userName);
					if (patrolTask) {
						String bakPath = "/" + userName + "/TRNTASKBAK.DB";
						InitTaskDao initTaskDao  = new InitTaskDao(getActivity(),bakPath);
						initTaskDao.initTargetDbData();
						mHandler.obtainMessage(21, "巡视任务数据更新成功！").sendToTarget() ;
					} else {
						mHandler.obtainMessage(21, "巡视任务数据更新失败！").sendToTarget() ;
					}					
				} else {
					boolean patrolTask = NetAdapter.downLoadPatrolTaskDB(userName);
					if (patrolTask) {
						mHandler.obtainMessage(21, "巡视任务数据更新成功！").sendToTarget() ;
					} else {
						mHandler.obtainMessage(21, "巡视任务数据更新失败！").sendToTarget() ;
					}
				}
			}
		}).start();
		
	}

	protected void dealTaskStatus() {
		final String userName = MyApplication.getInstance().loginUser
				.getUSERNAME();
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (TB_TASK item : choiceTaskList) {
					String ret = NetAdapter.updateTaskStatus(item.getTASKID(),
							"巡视任务", "任务闭环");
					if (!StringUtils.isNull(ret)) {
						try {
							JSONObject jb = new JSONObject(ret);
							if ("0".equals(jb.getString("resultCode"))) {
								taskDao.updateStatus(item.getTASKID(), "已上传");
								String filePath = DBHelper.DB_PATH + "/t"+item.getTASKID()+"/";
								
								taskDao.delTaskLinesByTaskId(item.getTASKID());
								taskDao.delTaskTowersByTaskId(item.getTASKID());
								taskDao.delItem(item);
								//删除任务相关的
								FileUtil.deleteFile(filePath);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

				if (taskDao.doAll()) {// 删除文件
					List<TB_TASK> delTaskList = taskDao.getAllList(3);
					for (TB_TASK delTask : delTaskList) {// 删除回传库
						String filePath = DBHelper.DB_PATH + "/" + userName
								+ "/t" + delTask.getTASKID() + "/";
						FileUtil.deleteFile(filePath);
					}
					String taskDbFilePath = DBHelper.DB_PATH + "/" + userName
							+ "/TRNTASK.DB";
					FileUtil.deleteFile(taskDbFilePath);// 删除任务库文件
				}
				mHandler.obtainMessage(21, "上传操作完成！").sendToTarget();

			}
		}).start();
	}

	protected void uploadTaskDefectRet() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String userName = MyApplication.getInstance().loginUser
						.getUSERNAME();
				List<TB_TASK_DEFECT> uploadTaskDefectList = taskDefectDao
						.getAllTaskDefect(2);
				if (uploadTaskDefectList.isEmpty()) {
					mHandler.obtainMessage(11, "没有上传的消缺任务!").sendToTarget();
					return;
				}
				boolean isStarted = false;

				for (TB_TASK_DEFECT uploadTaskDefect : uploadTaskDefectList) {

					String dbPath = DBHelper.DB_PATH + "/" + userName + "/d"
							+ uploadTaskDefect.getDEFECTTASKID()
							+ "/TRNTASKDEFECTRET.DB";
					File dbFile = new File(dbPath);
					if (!dbFile.exists()) {
						if(!isStarted){
							isStarted = false;
						}
						continue;
					}else{
						isStarted = true;
					}
					try {
						byte data[] = StreamUtils.file2ByteArray(dbFile);
						String retStr = NetAdapter.uploadTaskDefectInfo(
								userName, uploadTaskDefect.getDEFECTTASKID(),
								data);
						if (StringUtils.isNull(retStr)) {
							mHandler.obtainMessage(11, "消缺任务上传接口返回失败，上传失败！")
									.sendToTarget();
							return;
						}
						JSONObject jo = new JSONObject(retStr);
						if ("0".equals(jo.getString("resultCode"))) {
							AttachDefectDao attachDefectDao = new AttachDefectDao(getActivity());
							List<TB_ATTACH> list = attachDefectDao.getAllList(userName,uploadTaskDefect
									.getDEFECTTASKID());
							for (TB_ATTACH attach : list) {
								byte imgData[] = StreamUtils
										.file2ByteArray(new File(attach
												.getATTACHCONTENT()));
								String tmpRet = NetAdapter.uploadAttachInfo(
										"消缺任务",
										uploadTaskDefect.getDEFECTTASKID(),
										attach.getATTACHID(),attach.getASSETID(),imgData);
								try {
									imgData = null;
									System.gc();
									System.gc();
									Thread.sleep(300);
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (!StringUtils.isNull(tmpRet)) {
									JSONObject jjo = new JSONObject(tmpRet);
									if ("0".equals(jjo.getString("resultCode"))) {
										attachDefectDao.updateAttachStatus(userName,uploadTaskDefect.getDEFECTTASKID(),attach.getATTACHID(),"已上传");
									}
								}else{
									mHandler.obtainMessage(11, "附件上传接口返回为空！").sendToTarget();
									return;
								}
							}
							
						} else {
							mHandler.obtainMessage(
									11,
									uploadTaskDefect.getDEFECTTASKNAME()
											+ jo.getString("resultDesc"))
									.sendToTarget();
						}

					} catch (Exception e) {
						e.printStackTrace();
						mHandler.obtainMessage(11, "消缺任务结果文件解析失败！")
								.sendToTarget();
					}

				}
				if(isStarted){//注意是消缺任务
					mHandler.obtainMessage(13, "上传操作完成").sendToTarget();
				}else{
					mHandler.obtainMessage(9, "任务未开始执行！不上传数据！").sendToTarget();
				}
			}
		}).start();

	}

	protected void uploadTaskRet() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final String userName = MyApplication.getInstance().loginUser
						.getUSERNAME();
				List<TB_TASK> uploadTaskList = taskDao.getAllList(2);
				if (uploadTaskList.isEmpty()) {
					mHandler.obtainMessage(11, "没有可上传的巡视任务！").sendToTarget();
					return;
				}
				boolean taskStarted = false;
				for (TB_TASK uploadTask : uploadTaskList) {
					
					String dbPath = DBHelper.DB_PATH + "/" + userName + "/t"
							+ uploadTask.getTASKID() + "/TRNTASKRET.DB";
					File dbFile = new File(dbPath);
					if (!dbFile.exists()) {
						if(!taskStarted){
							taskStarted = false;
						}
						continue;
					}
					taskStarted  = true;
					try {
						byte data[] = StreamUtils.file2ByteArray(dbFile);
						String retStr = NetAdapter.uploadTaskInfo(userName,
								uploadTask.getTASKID(), data);
						data = null;
						System.gc();
						System.gc();
						if (StringUtils.isNull(retStr)) {
							mHandler.obtainMessage(11,
									uploadTask.getTASKNAME() + "上传，接口返回失败！")
									.sendToTarget();
							return;
						}
						JSONObject jo = new JSONObject(retStr);
						if ("0".equals(jo.getString("resultCode"))) {// 任务上传成功，上传图片
							List<TB_ATTACH> list = attachTaskDao
									.getAllList(userName,uploadTask.getTASKID());
							for (TB_ATTACH attach : list) {
								byte imgData[] = StreamUtils
										.file2ByteArray(new File(attach
												.getATTACHCONTENT()));
								String tmpRet = NetAdapter.uploadAttachInfo(
										"巡视任务", uploadTask.getTASKID(),
										attach.getATTACHID(), attach.getASSETID(),imgData);
								try {
									imgData = null;
									System.gc();
									System.gc();
									Thread.sleep(300);
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								if (!StringUtils.isNull(tmpRet)) {
									JSONObject jjo = new JSONObject(tmpRet);
									if ("0".equals(jjo.getString("resultCode"))) {
										// 更新附件　TODO
										attachTaskDao.updateAttachStatus(userName,uploadTask.getTASKID(),
												attach.getATTACHID(), "已上传");
									}
								}else{
									mHandler.obtainMessage(11, "附件上传接口返回为空！").sendToTarget();
									return;
								}
							}

						} else {
							mHandler.obtainMessage(
									11,
									uploadTask.getTASKNAME()
											+ jo.getString("resultDesc"))
									.sendToTarget();
						}

					} catch (Exception e) {
						e.printStackTrace();
						mHandler.obtainMessage(11, "任务结果文件解析失败！").sendToTarget();
						return;
					}
				}
				if(taskStarted){
					mHandler.obtainMessage(12, "上传操作完成").sendToTarget();
				}else{
					mHandler.obtainMessage(9, "任务未开始执行！不上传数据！").sendToTarget();
				}
			}
		}).start();

	}

	private void dealUploadTaskRet() {
		List<TB_TASK> uploadTaskList = taskDao.getAllList(2);
		final List<TB_TASK> overTaskList = new ArrayList<TB_TASK>();
		ArrayList<String> overTaskArr = new ArrayList<String>();
		List<Boolean> overTaskBol = new ArrayList<Boolean>();
		for (TB_TASK task : uploadTaskList) {//
			String status = task.getSTATUS();
			if (status.startsWith("已巡视")) {
				overTaskList.add(task);
				overTaskArr.add(task.getTASKNAME() + "[可闭环]");
				overTaskBol.add(false);
			}
		}

		choiceTaskList = new ArrayList<TB_TASK>();
		if (!overTaskArr.isEmpty() && overTaskArr.size() > 0) {
			
			String[] strArr = (String[])overTaskArr.toArray(new String[overTaskArr.size()]);
			new AlertDialog.Builder(getActivity())
					.setTitle("选择可闭环任务")
					.setMultiChoiceItems(strArr, null,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton, boolean isChecked) {
									// 点击某个区域
									Log.w("DataMain", isChecked + "_"
											+ whichButton);
									if (isChecked) {
										TB_TASK task = overTaskList
												.get(whichButton);
										if (!choiceTaskList.contains(task)) {
											choiceTaskList.add(task);
										}
									}

								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
									mHandler.obtainMessage(20).sendToTarget();
								}

							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									mHandler.obtainMessage(11).sendToTarget();
								}
							}).create().show();
		}else{
			mHandler.obtainMessage(11).sendToTarget();
		}
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		Log.d("FragmentTaskMain", "onActivityCreated");
		lv = (ListView) getView().findViewById(R.id.task_main_lv);
		titleTv01 = (TextView) getView().findViewById(R.id.task_main_item3);
		taskBtn = (Button) getView().findViewById(R.id.inspection_btn);
		taskBtn.setOnClickListener(this);
		defBtn = (Button) getView().findViewById(R.id.defect_btn);
		defBtn.setOnClickListener(this);
		uploadBtn = (Button) getView().findViewById(R.id.uploadBtn);
		uploadBtn.setOnClickListener(this);

		downloadBtn = (Button) getView().findViewById(R.id.downloadBtn);
		downloadBtn.setOnClickListener(this);

		uploadBtn.setText("巡视任务一键上传");
		downloadBtn.setText("巡视任务一键下载");

		taskBtn.setSelected(true);
		taskDao = new TaskDao(getActivity());
		taskDefectDao = new TaskDefectDao(getActivity());
		onDataRefreshListener.refreshDatas();
		attachTaskDao = new AttachTaskDao(getActivity());
	}

	@Override
	public void onClick(View v) {
		isNeedLoadData = true;
		switch (v.getId()) {
		case R.id.inspection_btn:
			isTask = true;
			uploadBtn.setText("巡视任务一键上传");
			downloadBtn.setText("巡视任务一键下载");
			onDataRefreshListener.refreshDatas();
			break;

		case R.id.defect_btn:
			isTask = false;
			uploadBtn.setText("消缺任务一键上传");
			downloadBtn.setText("消缺任务一键下载");
			onDataRefreshListener.refreshDatas();
			break;

		case R.id.uploadBtn:
			if (isTask) {
				mHandler.obtainMessage(1).sendToTarget();
			} else {
				mHandler.obtainMessage(2).sendToTarget();
			}
			break;
		case R.id.downloadBtn:
			if (isTask) {
				mHandler.obtainMessage(3).sendToTarget();
			} else {
				mHandler.obtainMessage(4).sendToTarget();
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("FragmentTaskMain", "onStart");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("FragmentTaskMain", "onResume");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.d("FragmentTaskMain", "onDestroyView");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("FragmentTaskMain", "onStop");
	}

	class TaskMainAdapter extends ListAdapter {
		private boolean isTask = true;

		public TaskMainAdapter(Context context,
				List<? extends Serializable> datas) {
			super(context, datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.task_upload_item, null);
				vh = new ViewHolder();
				vh.tv01 = (TextView) convertView
						.findViewById(R.id.task_main_item0);

				vh.tv02 = (TextView) convertView
						.findViewById(R.id.task_main_item1);
				vh.tv03 = (TextView) convertView
						.findViewById(R.id.task_main_item2);
				vh.tv04 = (TextView) convertView
						.findViewById(R.id.task_main_item3);
				vh.tv05 = (TextView) convertView
						.findViewById(R.id.task_main_item4);
				vh.tv06 = (TextView) convertView
						.findViewById(R.id.task_main_item5);
				vh.tv07 = (TextView) convertView
						.findViewById(R.id.task_main_item6);
				// vh.btn08 = (Button)
				// convertView.findViewById(R.id.task_main_item8);
				// vh.btn08.setVisibility(View.VISIBLE);
				int color = mContext.getResources().getColor(R.color.textcolor);
				vh.tv01.setTextColor(color);
				vh.tv02.setTextColor(color);
				vh.tv03.setTextColor(color);
				vh.tv04.setTextColor(color);
				vh.tv05.setTextColor(color);
				vh.tv06.setTextColor(color);
				vh.tv07.setTextColor(color);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			setListViewBackGround(convertView,position);

			if (isTask) {// 任务
				final TB_TASK item = (TB_TASK) datas.get(position);
				vh.tv01.setText(StringUtils.getTwoLenNumic(position + 1) + "");
				vh.tv02.setText(item.getLine().getLINENAME());
				
				TaskDao taskDao = new TaskDao(mContext);
			 	TB_TOWER startTower = taskDao.getTowerByTowerId(item.getLine().getSTARTTOWERID());
			 	TB_TOWER endTower = taskDao.getTowerByTowerId( item.getLine().getENDTOWERID());
			 	
				String towerDesc =  startTower.getTOWERNAME()+ "#-"
						+ endTower.getTOWERNAME() + "#";
				vh.tv03.setText(towerDesc);// 启始杆塔号与结束杆塔号
				vh.tv04.setText(item.getWORKDATE());
				vh.tv05.setText(item.getENDDATE());
				vh.tv06.setText(item.getCREATETIME());
				vh.tv07.setText(item.getSTATUS());

			} else {// 缺陷
				final TB_TASK_DEFECT item = (TB_TASK_DEFECT) datas
						.get(position);
				vh.tv01.setText(StringUtils.getTwoLenNumic(position + 1) + "");
				vh.tv02.setText(item.getLINENAME());
				TaskDao taskDao = new TaskDao(mContext);
			 	TB_TOWER startTower = taskDao.getTowerByTowerId(item.getTOWERID());
				vh.tv03.setText(startTower.getTOWERNAME() + "#");// 启始杆塔号与结束杆塔号
				
				vh.tv04.setText(item.getDEFECTTYPE());
				vh.tv05.setText(item.getFINISHTIME());
				vh.tv06.setText(item.getDOWNLOADTIME());
				String status = item.getSTATUS();
				if (StringUtils.isNull(status)) {
					status = "未消缺";
				}
				vh.tv07.setText(status);
			}

			return convertView;
		}

		public void setTask(boolean isTask) {
			this.isTask = isTask;
		}

	}

	static class ViewHolder {
		public TextView tv01;
		public TextView tv02;
		public TextView tv03;
		public TextView tv04;
		public TextView tv05;
		public TextView tv06;
		public TextView tv07;
		public Button btn08;
	}

}
