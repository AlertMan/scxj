package com.scxj.fragment;

import java.io.Serializable;
import java.util.List;

import android.R.color;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.adapter.ListAdapter;
import com.scxj.dao.TaskDao;
import com.scxj.dao.TaskDefectDao;
import com.scxj.main.MainMenuActivity;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.AppUtil;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.Const;
import com.scxj.utils.SharedPreferencesUtil;
import com.scxj.utils.StringUtils;
import com.scxj.utils.TextUtil;
/*
 * 巡视与消缺任务
 * */
public class FragmentTaskMain extends BaseFragment implements OnClickListener {
	private List<TB_TASK> taskList;
	private List<TB_TASK_DEFECT> taskDefectList;
	private ListView lv = null;
	private TaskMainAdapter adapter;
	private Button taskBtn;
	private 	Button defBtn;
	private boolean isTask = true,isNeedLoadData = false;
	private SharedPreferencesUtil spu;
	
	private OnDataRefreshListener onDataRefreshListener = new OnDataRefreshListener() {
		
		@Override
		public void refreshDatas() {
			if(isTask){
				taskBtn.setSelected(true);
				defBtn.setSelected(false);
				// 添加不同的数据源
				taskList = new TaskDao(getActivity()).getAllList(2);//2
				if (adapter == null ) {
					adapter = new TaskMainAdapter(getActivity(), taskList);
					lv.setAdapter(adapter);
				}else{
					adapter.setDatas(taskList);
					adapter.setTask(isTask);
					adapter.notifyDataSetChanged();
				}
			}else{
				taskBtn.setSelected(false);
				defBtn.setSelected(true);
				// 添加不同的数据源
				taskDefectList = new TaskDefectDao(getActivity()).getAllTaskDefect(2);
				
				if (adapter == null) {
					adapter = new TaskMainAdapter(getActivity(), taskDefectList);
					lv.setAdapter(adapter);
				}else{
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
		if (container == null) 
		{
			return null;
		}
		this.mFragmentManager = getChildFragmentManager();
		View layout = inflater.inflate(R.layout.task_main, container, false); 
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		Log.d("FragmentTaskMain", "onActivityCreated");
		lv = (ListView) getView().findViewById(R.id.task_main_lv);
		taskBtn = (Button)getView().findViewById(R.id.inspection_btn);
		taskBtn.setOnClickListener(this);
		defBtn = (Button)getView().findViewById(R.id.defect_btn);
		defBtn.setOnClickListener(this);
		taskBtn.setSelected(true);
		spu = new SharedPreferencesUtil(getActivity());
		
		onDataRefreshListener.refreshDatas();
		((MainMenuActivity)getActivity()).setOnDataRefreshListener(onDataRefreshListener);
	}
	
	@Override
	public void onClick(View v) {
		isNeedLoadData = false;
		switch (v.getId()) {
		case R.id.inspection_btn:
			isTask = true;
			onDataRefreshListener.refreshDatas();
			break;

		case R.id.defect_btn:
			isTask = false;
			onDataRefreshListener.refreshDatas();
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


	class TaskMainAdapter extends ListAdapter {
		private boolean isTask = true;
		public TaskMainAdapter(Context context, List<? extends Serializable> datas) {
			super(context, datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.task_main_item, null);
				vh = new ViewHolder();
				vh.tv01 = (TextView) convertView.findViewById(R.id.task_main_item0);
				vh.tv02 = (TextView) convertView.findViewById(R.id.task_main_item1);
				vh.tv03 = (TextView) convertView.findViewById(R.id.task_main_item2);
				vh.tv04 = (TextView) convertView.findViewById(R.id.task_main_item3);
				vh.tv05 = (TextView) convertView.findViewById(R.id.task_main_item4);
				vh.tv06 = (TextView) convertView.findViewById(R.id.task_main_item5);
				vh.tv07 = (TextView) convertView.findViewById(R.id.task_main_item6);
				vh.btn08 = (Button) convertView.findViewById(R.id.task_main_item8);
				vh.btn08.setVisibility(View.VISIBLE);
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
			
			if(isTask){//任务
					TaskDao taskDao = new TaskDao(mContext);
					final TB_TASK item = (TB_TASK) datas.get(position);
				 	TB_TOWER startTower = taskDao.getTowerByTowerId(item.getLine().getSTARTTOWERID());
				 	TB_TOWER endTower = taskDao.getTowerByTowerId( item.getLine().getENDTOWERID());
					vh.tv01.setText(StringUtils.getTwoLenNumic(position + 1) + "");
					vh.tv02.setText(item.getLine().getLINENAME());
					String towerDesc = startTower.getTOWERNAME() + "#-" + endTower.getTOWERNAME()+"#";
					vh.tv03.setText(towerDesc);//启始杆塔号与结束杆塔号
					vh.tv04.setText(item.getTASKNAME());
					String endTime = item.getENDDATE();
					int endIndex = endTime.lastIndexOf(".0") ;
					if(endIndex != -1){
						endTime = endTime.substring(0, endIndex);
					}
					vh.tv05.setText(endTime);
					
					String createTime = item.getCREATETIME();
					int createIndex = createTime.lastIndexOf(".0") ;
					if(createIndex != -1){
						createTime = createTime.substring(0, createIndex);
					}
					
					vh.tv06.setText(createTime);
					vh.tv07.setText(TextUtil.highlightKeywords(item.getSTATUS(), "\\d+", Color.RED));
					String taskName = spu.readData("TRACK_TASKNAME");
					String taskId = spu.readData("TRACK_TASKID");
					if(!StringUtils.isNull(taskId) && taskId.equals(item.getTASKID()) && taskName.equals(item.getTASKNAME())){
						vh.btn08.setSelected(true);
						vh.btn08.setClickable(false);
						vh.btn08.setEnabled(false);
						vh.btn08.setText("正在记录");
					}else{
						vh.btn08.setSelected(false);
						vh.btn08.setEnabled(true);
						vh.btn08.setClickable(true);
						vh.btn08.setText("启动轨迹");
					}
					//启动轨迹
					vh.btn08.setOnClickListener(new OnClickListener() {
		
						@Override
						public void onClick(View v) {
							
							final String userName = MyApplication.getInstance().loginUser.getUSERNAME();
							final String taskName = spu.readData("TRACK_TASKNAME");
							final String taskId = spu.readData("TRACK_TASKID");
							Const.DB_NAME.setUserTaskDirectory(userName, item.getTASKID(), false, 0);
							String itemTaskName  = item.getTASKNAME();
							if(!StringUtils.isNull(taskId) && AppUtil.isServiceRunning(mContext, "com.scxj.service.LocationService")){
								final String taskType = spu.readData("TRACK_TYPE");
								new AlertDialog.Builder(mContext)
								.setTitle("警告")
								.setMessage("["+taskType+"]中["+taskName+"]正在记录轨迹，是否停止,启动巡视任务中"+itemTaskName+"轨迹记录!")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										boolean flag = ((MyApplication)getActivity().getApplication()).startLocationService();
										if(flag){
											/**
											 * 考虑到设备会休眠待机，通过SharedPreferences传递参数，每一次启动都必须初始化参数
											 */
											spu.writeData("TRACK_TYPE", "巡视任务");
											spu.writeData("TRACK_TASKID", item.getTASKID());
											spu.writeData("TRACK_TASKNAME", item.getTASKNAME());
											spu.writeData("TRACK_STARTADDRESS", "开始地址");
											spu.writeData("TRACK_ENDADDRESS", "结束地址");
											spu.writeData("TRACK_NOTE", "备注");
											spu.writeData("TRACK_USERID", userName);
										
											AudioTipsUtils.showMsg(mContext, "巡检任务"+item.getTASKNAME()+"轨迹记录服务启动成功!");
										}else{
											spu.writeData("TRACK_TYPE", "");
											spu.writeData("TRACK_TASKID", "");
											spu.writeData("TRACK_TASKNAME", "");
											AudioTipsUtils.showMsg(mContext, "轨迹记录服务启动失败!请打开GPS或者保持GPS有信号.");
										}
										notifyDataSetChanged();
									}
								})
								.setNegativeButton("取消",null).show();
							}else{
//								AppUtil.stopRunningService(mContext, "com.scxj.service.LocationService");
								/**
								 * 考虑到设备会休眠待机，通过SharedPreferences传递参数，每一次启动都必须初始化参数
								 */
								boolean flag = ((MyApplication)getActivity().getApplication()).startLocationService();
								if(flag){
									spu.writeData("TRACK_TYPE", "巡视任务");
									spu.writeData("TRACK_TASKID", item.getTASKID());
									spu.writeData("TRACK_TASKNAME", item.getTASKNAME());
									spu.writeData("TRACK_STARTADDRESS", "开始地址");
									spu.writeData("TRACK_ENDADDRESS", "结束地址");
									spu.writeData("TRACK_NOTE", "备注");
									spu.writeData("TRACK_USERID", userName);
									AudioTipsUtils.showMsg(mContext, "巡检任务"+item.getTASKNAME()+"轨迹记录服务启动成功!");
								}else{
									spu.writeData("TRACK_TYPE", "");
									spu.writeData("TRACK_TASKID", "");
									spu.writeData("TRACK_TASKNAME", "");
									AudioTipsUtils.showMsg(mContext, "轨迹记录服务启动失败!请打开GPS或者保持GPS有信号.");
								}
								notifyDataSetChanged();
							}
		
						}
					});  
		
					//任务杆塔列表
					vh.tv02.setOnClickListener(new OnClickListener() {
		
						@Override
						public void onClick(View v) {
							isNeedLoadData = true;
							FragmentTaskMainTowers bf = new FragmentTaskMainTowers();
							Bundle b= new Bundle();
							b.putSerializable("item", item);
							bf.setArguments(b);
							bf.setOnPreDataRefresh(onDataRefreshListener);
							FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
							fTransaction.replace(R.id.task_main_layout, bf);
							fTransaction.addToBackStack(null);
							fTransaction.commit();
						}
					}); 
					
					
					//任务杆塔列表
					vh.tv03.setOnClickListener(new OnClickListener() {
		
						@Override
						public void onClick(View v) {
							isNeedLoadData = true;
							FragmentTaskMainTowers bf = new FragmentTaskMainTowers();
							Bundle b= new Bundle();
							b.putSerializable("item", item);
							bf.setArguments(b);
							bf.setOnPreDataRefresh(onDataRefreshListener);
							FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
							fTransaction.replace(R.id.task_main_layout, bf);
							fTransaction.addToBackStack(null);
							fTransaction.commit();
						}
					}); 
					
					//任务杆塔列表
					vh.tv04.setOnClickListener(new OnClickListener() {
		
						@Override
						public void onClick(View v) {
							isNeedLoadData = true;
							FragmentTaskMainTowers bf = new FragmentTaskMainTowers();
							Bundle b= new Bundle();
							b.putSerializable("item", item);
							bf.setArguments(b);
							bf.setOnPreDataRefresh(onDataRefreshListener);
							FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
							fTransaction.replace(R.id.task_main_layout, bf);
							fTransaction.addToBackStack(null);
							fTransaction.commit();
						}
					}); 
			}else {//缺陷
				final TB_TASK_DEFECT item = (TB_TASK_DEFECT) datas.get(position);
				vh.tv01.setText(StringUtils.getTwoLenNumic(position + 1) + "");
				vh.tv02.setText(item.getLINENAME());
				vh.tv03.setText(item.getTOWERNAME()+"#");//启始杆塔号与结束杆塔号
				vh.tv04.setText(item.getDEFECTTASKNAME());
				
				String endTime = item.getFINISHTIME();
				int endIndex = endTime.lastIndexOf(".0") ;
				if(endIndex != -1){
					endTime = endTime.substring(0, endIndex );
				}
				vh.tv05.setText(endTime);
				
				String createTime = item.getDOWNLOADTIME();
				int createIndex = createTime.lastIndexOf(".0") ;
				if(createIndex != -1){
					createTime = createTime.substring(0, createIndex);
				}
				
				vh.tv05.setText(endTime);
				vh.tv06.setText(createTime);
				String status = item.getSTATUS();
				if(StringUtils.isNull(status)){
					status = "未消缺";
				}
				vh.tv07.setText(status);
				
				String taskName = spu.readData("TRACK_TASKNAME");
				String taskId = spu.readData("TRACK_TASKID");
				if(!StringUtils.isNull(taskId) && taskId.equals(item.getDEFECTTASKID()) && taskName.equals(item.getDEFECTTASKNAME())){
					vh.btn08.setSelected(true);
					vh.btn08.setClickable(false);
					vh.btn08.setEnabled(false);
					vh.btn08.setText("正在记录");
				}else{
					vh.btn08.setSelected(false);
					vh.btn08.setEnabled(true);
					vh.btn08.setClickable(true);
					vh.btn08.setText("启动轨迹");
				}
				
				//启动轨迹
				vh.btn08.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						/**
						 * 考虑到设备会休眠待机，通过SharedPreferences传递参数，每一次启动都必须初始化参数
						 */
						final String userName = MyApplication.getInstance().loginUser.getUSERNAME();
						Const.DB_NAME.setUserTaskDirectory(userName, item.getDEFECTTASKID(), false, 1);
						String itemTaskName  = item.getDEFECTTASKNAME();
						final String taskName = spu.readData("TRACK_TASKNAME");
						final String taskId = spu.readData("TRACK_TASKID");
						if(!StringUtils.isNull(taskId) && AppUtil.isServiceRunning(mContext, "com.scxj.service.LocationService") ){
							final String taskType = spu.readData("TRACK_TYPE");
							
							new AlertDialog.Builder(mContext)
							.setTitle("警告")
							.setMessage("["+taskType+"]中["+taskName+"]正在记录轨迹，是否停止,启动巡视任务中"+itemTaskName+"轨迹记录!")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									/**
									 * 考虑到设备会休眠待机，通过SharedPreferences传递参数，每一次启动都必须初始化参数
									 */
									
									boolean flag = ((MyApplication)getActivity().getApplication()).startLocationService();
									if(flag){
										spu.writeData("TRACK_TYPE", "消缺任务");
										spu.writeData("TRACK_TASKID", item.getDEFECTTASKID());
										spu.writeData("TRACK_TASKNAME", item.getDEFECTTASKNAME());
										spu.writeData("TRACK_STARTADDRESS", "开始地址");
										spu.writeData("TRACK_ENDADDRESS", "结束地址");
										spu.writeData("TRACK_NOTE", "备注");
										spu.writeData("TRACK_USERID", userName);
										AudioTipsUtils.showMsg(mContext, "消缺任务"+item.getDEFECTTASKNAME()+"轨迹记录服务启动成功！");
									}else{
										spu.writeData("TRACK_TYPE", "");
										spu.writeData("TRACK_TASKID", "");
										spu.writeData("TRACK_TASKNAME", "");
										AudioTipsUtils.showMsg(mContext, "轨迹记录服务启动失败!请打开GPS或者保持GPS有信号.");
									}
									notifyDataSetChanged();
								}
							})
							.setNegativeButton("取消",null).show();
						}else{
//							AppUtil.stopRunningService(mContext, "com.scxj.service.LocationService");
							/**
							 * 考虑到设备会休眠待机，通过SharedPreferences传递参数，每一次启动都必须初始化参数
							 */
							
							
							boolean flag = ((MyApplication)getActivity().getApplication()).startLocationService();
							if(flag){
								spu.writeData("TRACK_TYPE", "消缺任务");
								spu.writeData("TRACK_TASKID", item.getDEFECTTASKID());
								spu.writeData("TRACK_TASKNAME", item.getDEFECTTASKNAME());
								spu.writeData("TRACK_STARTADDRESS", "开始地址");
								spu.writeData("TRACK_ENDADDRESS", "结束地址");
								spu.writeData("TRACK_NOTE", "备注");
								spu.writeData("TRACK_USERID", userName);
								AudioTipsUtils.showMsg(mContext, "消缺任务"+item.getDEFECTTASKNAME()+"轨迹记录服务启动成功!");
							}else{
								spu.writeData("TRACK_TYPE", "");
								spu.writeData("TRACK_TASKID", "");
								spu.writeData("TRACK_TASKNAME", "");
								AudioTipsUtils.showMsg(mContext, "轨迹记录服务启动失败!请打开GPS或者保持GPS有信号.");
							}
							notifyDataSetChanged();
						}
	
					}
				});  
	
				//任务杆塔列表
				vh.tv02.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						isNeedLoadData = true;
						Bundle b = new Bundle();
						b.putSerializable("item", item);
						FragmentTaskMainDefect bf = new FragmentTaskMainDefect();
						bf.setArguments(b);
						bf.setOnPreDataRefresh(onDataRefreshListener);
						FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
						fTransaction.replace(R.id.task_main_layout, bf);
						fTransaction.addToBackStack(null);
						fTransaction.commit();
					}
				}); 
				//任务杆塔列表
				vh.tv03.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						isNeedLoadData = true;
						Bundle b = new Bundle();
						b.putSerializable("item", item);
						FragmentTaskMainDefect bf = new FragmentTaskMainDefect();
						bf.setArguments(b);
						bf.setOnPreDataRefresh(onDataRefreshListener);
						FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
						fTransaction.replace(R.id.task_main_layout, bf);
						fTransaction.addToBackStack(null);
						fTransaction.commit();
					}
				}); 
				
				//任务杆塔列表
				vh.tv04.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						isNeedLoadData = true;
						Bundle b = new Bundle();
						b.putSerializable("item", item);
						FragmentTaskMainDefect bf = new FragmentTaskMainDefect();
						bf.setArguments(b);
						bf.setOnPreDataRefresh(onDataRefreshListener);
						FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
						fTransaction.replace(R.id.task_main_layout, bf);
						fTransaction.addToBackStack(null);
						fTransaction.commit();
					}
				}); 
			}

			return convertView;
		}


		public void setTask(boolean isTask) {
			this.isTask = isTask;
		}

	}

	static  class ViewHolder {
		public  TextView tv01;
		public  TextView tv02;
		public  TextView tv03;
		public  TextView tv04;
		public  TextView tv05;
		public  TextView tv06;
		public  TextView tv07;
		public  Button btn08;
	}

}
