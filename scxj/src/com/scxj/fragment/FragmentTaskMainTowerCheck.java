package com.scxj.fragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.R.color;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import cn.pisoft.base.db.DBHelper;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.adapter.ListAdapter;
import com.scxj.dao.RetTaskDao;
import com.scxj.dao.TaskDao;
import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_TASK_RET;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.Const;
import com.scxj.utils.CustomTouchListener;
import com.scxj.utils.DateUtil;
import com.scxj.utils.StringUtils;

/*
 * 任务杆塔检测记录
 * */
public class FragmentTaskMainTowerCheck extends BaseFragment {
	private TB_TOWER tower;// 杆塔数据
	private TB_TASK task;// 任务数据
	private ListView lv;// 界面展示列表
	private TaskMainTowerCheckAdapter adapter;
	private List<TB_TASK_RET> datas;// 所有数据
	private List<String> checkModDatas;//备用检查数据。
	private List<TB_TASK_RET> choiceTaskRetList;//选择的数据
	private OnDataRefreshListener onDataRefreshListener;
	private OnDataRefreshListener selfOnDataRefreshListener = new OnDataRefreshListener(){

		@Override
		public void refreshDatas() {
			datas = retTaskDao.getTowersCheckListById(task,
					tower);
			checkModDatas = new ArrayList<String>();
			for(int i=0; i < datas.size(); i++){
				checkModDatas.add(datas.get(i).getDEFECTCONTENT());
			}
			if(adapter == null){
				adapter = new TaskMainTowerCheckAdapter(getActivity(), datas);
				lv.setAdapter(adapter);
			}else{
				adapter.setDatas(datas);
				adapter.notifyDataSetChanged();
			}
			
		}
		
	};
	private RetTaskDao retTaskDao ;
	private boolean isVoice = true;

	private ProgressDialog pd;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 22:
				dealDataRet();
				break;

			default:
				break;
			}
			
		};
	};
	private String msg = "";

	public void setOnDataRefreshListener(
			OnDataRefreshListener onDataRefreshListener) {
		this.onDataRefreshListener = onDataRefreshListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		View layout = inflater.inflate(R.layout.task_main_tower_check,
				container, false);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		retTaskDao = new RetTaskDao(getActivity());
		Bundle b = getArguments();
		task = (TB_TASK) b.getSerializable("task");
		tower = (TB_TOWER) b.getSerializable("tower");
		String userName = MyApplication.getInstance().loginUser.getUSERNAME();
		String taskId = task.getTASKID();
		Const.DB_NAME.setUserTaskDirectory(userName, taskId, false, 0);

		lv = (ListView) getView().findViewById(R.id.tower_check_lv);
		
		selfOnDataRefreshListener.refreshDatas();

		TextView tv01 = (TextView) getView().findViewById(R.id.taskName);
		tv01.setText("任务名称：" + task.getTASKNAME() +"\t杆塔名称："+tower.getTOWERNAME());
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
		menu.add("保存").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("刷新").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 隐蔽软件键盘
		// ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);

		if ("保存".equals(item.getTitle())) {
			saveDatas();
		}
		
		if("刷新".equals(item.getTitle())){
			selfOnDataRefreshListener.refreshDatas();
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void saveDatas(){
		adapter.setSelIndex(-1);
		lv.clearFocus();
		final List<TB_TASK_RET> overTaskList = new ArrayList<TB_TASK_RET>();
		List<String> modifiedArr = new ArrayList<String>();
		for(int i=0 ; i < datas.size(); i++){
			TB_TASK_RET taskRet = datas.get(i);
			//原始数据不为空，并且与现值不一样。
			if(!StringUtils.isNull(checkModDatas.get(i)) && !checkModDatas.get(i).equals(taskRet.getDEFECTCONTENT()) && !"[]".equals(taskRet.getDEFECTCONTENT())){
				modifiedArr.add(taskRet.getMEASURETYPE()+"缺陷内容由["+checkModDatas.get(i) + "]修改为[" +taskRet.getDEFECTCONTENT()+"]");
				taskRet.setSelectIndex(i);
				try {
					TB_TASK_RET cloneObj = (TB_TASK_RET)taskRet.clone();
					overTaskList.add(cloneObj);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(!modifiedArr.isEmpty()){
			String[] strArr = (String[])modifiedArr.toArray(new String[modifiedArr.size()]);
			choiceTaskRetList = new ArrayList<TB_TASK_RET>();
			new AlertDialog.Builder(getActivity())
					.setTitle("选择差异保存项")
					.setMultiChoiceItems(strArr, null,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton, boolean isChecked) {
									TB_TASK_RET task = overTaskList.get(whichButton);
									// 点击某个区域
									if (isChecked) {
										if (!choiceTaskRetList.contains(task)) {
											choiceTaskRetList.add(task);
										}
									}else{
										if (choiceTaskRetList.contains(task)) {
											choiceTaskRetList.remove(task);
										}
									}

								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
									for(int i=0;i < datas.size(); i++){
										TB_TASK_RET ret = datas.get(i);
										ret.setDEFECTCONTENT(checkModDatas.get(i));
										for(int j=0; j<choiceTaskRetList.size(); j++){
											TB_TASK_RET tmpRet = choiceTaskRetList.get(j);
											if(tmpRet.equals(ret)){
												ret.setDEFECTCONTENT(tmpRet.getDEFECTCONTENT());
											}
										}
									}
									mHandler.obtainMessage(22).sendToTarget();
								}

							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create().show();
			
			
		}else{
			mHandler.obtainMessage(22).sendToTarget();
		}
	}
	
	
	private void dealDataRet(){
		pd = new ProgressDialog(getActivity()).show(getActivity(), "提示",
				"正在保存数据,原数据将被替换,请稍等……");
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isOk = true;
				int cnt = 0;
				for (int i = 0; i < datas.size(); i++) {
					TB_TASK_RET taskRet = datas.get(i);
					if ("异常".equals(taskRet.getSTATUS())) {// 异常
						taskRet.setTASKNAME(task.getTASKNAME());
						taskRet.setLINENAME(task.getLine().getLINENAME());
						taskRet.setTOWERNAME(tower.getTOWERNAME());
					} else if (StringUtils.isNull(taskRet.getSTATUS())) {
						taskRet.setDEFECTLEVEL("");
						taskRet.setDEFECTCONTENT("");
						// 删除一下附件表里面的数据
						// new
						// AttachTaskDao(getActivity()).delAttachItems(taskRet.getTASKID());
					}
					retTaskDao.updateItem(taskRet);
					if ("正常".equals(taskRet.getSTATUS())
							|| "异常".equals(taskRet.getSTATUS())) {
						cnt++;
					}
				}
				if (isOk) {
					if (!"已巡视".equals(tower.getSTATUS())) {
						if (datas != null && !datas.isEmpty()) {// 标识该 杆塔已巡
							TaskDao taskDao = new TaskDao(getActivity());
							if (cnt == datas.size()) {
								taskDao.updateTowerStatus(
										tower.getTOWERID(), "已巡视");
							} else {
								taskDao.updateTowerStatus(
										tower.getTOWERID(), "巡视中");
								taskDao.updateStatus(tower.getTASKID(),
										"巡视中");
							}
						}
					}
					datas = retTaskDao.getTowersCheckListById(task,
							tower);
					msg = "保存成功";
				} else {
					msg = "保存失败，请重试！";
				}

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if(isVoice){
							AudioTipsUtils.showMsg(getActivity(), msg);
						}else{
							isVoice = true; 
						}
						if (onDataRefreshListener != null) {
							onDataRefreshListener.refreshDatas();
						}
						adapter.setDatas(datas);
						adapter.notifyDataSetChanged();

						pd.dismiss();
						pd.cancel();
						pd = null;
					}
				});
			}
		}).start();
	}

	class TaskMainTowerCheckAdapter extends ListAdapter {
		private String[] defectLevel;
		private int index = -1;
		private int len = 0;

		public TaskMainTowerCheckAdapter(Context context,
				List<? extends Serializable> datas) {
			super(context, datas);
			defectLevel = context.getResources().getStringArray(
					R.array.defectLevel);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder vh;
//			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.task_main_tower_check_item, null);
				vh = new ViewHolder();
				vh.tv01 = (TextView) convertView
						.findViewById(R.id.tower_check_item0);
				vh.cb02 = (CheckBox) convertView
						.findViewById(R.id.tower_check_item1);
				vh.sp03 = (Spinner) convertView
						.findViewById(R.id.tower_check_item2);
				vh.et04 = (EditText) convertView
						.findViewById(R.id.tower_check_item3);
				vh.iv05 = (ImageView) convertView
						.findViewById(R.id.tower_check_item4);
				vh.iv07 = (ImageView)convertView.findViewById(R.id.tower_check_item5);
				vh.tv06 = (TextView)convertView.findViewById(R.id.imgCnt);
//				convertView.setTag(vh);
//			} else {
//				vh = (ViewHolder) convertView.getTag();
//			}

			final TB_TASK_RET item = (TB_TASK_RET) datas.get(position);
			vh.tv01.setText(item.getMEASURETYPE());
			int selLevIndex = 0;
			for (int i = 0; i < defectLevel.length; i++) {
				if (defectLevel[i].equals(item.getDEFECTLEVEL())) {
					selLevIndex = i;
				}
			}
			vh.sp03.setSelection(selLevIndex);

			vh.sp03.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					item.setDEFECTLEVEL(defectLevel[arg2]);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			vh.et04.setText(item.getDEFECTCONTENT());
			vh.et04.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			//			vh.et04.setInputType(InputType.TYPE_NULL); 
			vh.et04.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
							selIndex = position;
					}
				}
			});

			vh.et04.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(final Editable editable) {
					if(selIndex == position){
						item.setDEFECTCONTENT(editable.toString());
					}
				}

				public void beforeTextChanged(CharSequence text, int start,
						int count, int after) {
				}

				public void onTextChanged(CharSequence text, int start,
						int before, int count) {
					
				}

			});
			
			  vh.et04.clearFocus();
			  if (selIndex != -1 && selIndex == position)
			  {
				  vh.et04.requestFocus(); 
			  }
			  
			  vh.tv06.setText(item.getImgCnt()+"");
			// 检测拍照界面
			vh.iv05.setOnTouchListener(new CustomTouchListener() {
				
				@Override
				public void eventAction(View arg0) {
					isVoice = false;
					saveDatas();
					
					TB_ATTACH ath = new TB_ATTACH();
					String imageName = DateUtil.dateToString(new Date(),
							"yyyyMMddHHmmss") + ".jpg";
					String imageFilePath = DBHelper.DB_PATH + "/" + imageName;
					File imageFile = new File(imageFilePath);
					Uri imageFileUri = Uri.fromFile(imageFile);
					Intent i = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							imageFileUri);
					i.putExtra(android.provider.MediaStore.Images.Media.ORIENTATION,
							0);
					ath.setATTACHCONTENT(imageFilePath);
					ath.setATTACHID(item.getTASKID());
					ath.setATTACHNAME(imageName);
					ath.setCREATETIME(DateUtil.dateToString(new Date()));
					ath.setPARENTID(item.getID());// 关联任务回传表
					ath.setFile(imageFile);
					ath.setSelfOnDataRefreshListener(selfOnDataRefreshListener);
					((MyApplication) getActivity().getApplication()).attach = null;
					((MyApplication) getActivity().getApplication()).attach = ath;
					getActivity().startActivityForResult(i, 9);

				}
			});
			
			//查看照片详细情况
			vh.iv07.setOnTouchListener(new CustomTouchListener() {
				
				@Override
				public void eventAction(View arg0) {
					FragmentShowPictures bf;
					Bundle b = new Bundle();
						bf = new FragmentShowPictures();
						b.putSerializable("taskRet", datas.get(position));
						b.putSerializable("isTask", true);
					bf.setArguments(b);
					
					FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
					fTransaction.replace(R.id.tower_check_layout, bf);
					fTransaction.addToBackStack(null);
					fTransaction.commit();
				}
			});
			

			// 正常时不可输入异常可以输入。
			vh.cb02.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (!isChecked) {
						item.setSTATUS("正常");
						vh.sp03.setClickable(false);
						vh.sp03.setEnabled(false);

						vh.et04.setClickable(false);
						vh.et04.setEnabled(false);

						vh.iv05.setClickable(false);
						vh.iv05.setEnabled(false);
						item.setDEFECTCONTENT("");
						item.setDEFECTLEVEL(defectLevel[0]);

					} else {
						item.setSTATUS("异常");

						vh.sp03.setClickable(true);
						vh.sp03.setEnabled(true);

						vh.et04.setClickable(true);
						vh.et04.setEnabled(true);

						vh.iv05.setClickable(true);
						vh.iv05.setEnabled(true);
					}
					notifyDataSetChanged();
				}
			});

			if ("异常".equals(item.getSTATUS())) {
				vh.cb02.setButtonDrawable(mContext.getResources().getDrawable(
						R.drawable.button_switch_selector));
				vh.cb02.setChecked(true);

				vh.sp03.setClickable(true);
				vh.sp03.setEnabled(true);

				vh.et04.setClickable(true);
				vh.et04.setEnabled(true);
				
				vh.iv05.setClickable(true);
				vh.iv05.setEnabled(true);
			} else if ("正常".equals(item.getSTATUS())) {
				vh.cb02.setButtonDrawable(mContext.getResources().getDrawable(
						R.drawable.button_switch_selector));
				vh.cb02.setChecked(false);

				vh.sp03.setClickable(false);
				vh.sp03.setEnabled(false);

				vh.et04.setClickable(true);
				vh.et04.setEnabled(true);
				
				vh.iv05.setClickable(true);
				vh.iv05.setEnabled(true);
			} else {
				vh.cb02.setButtonDrawable(mContext.getResources().getDrawable(
						R.drawable.weizuo));
				vh.cb02.setClickable(true);

				vh.sp03.setClickable(false);
				vh.sp03.setEnabled(false);

				vh.et04.setClickable(false);
				vh.et04.setEnabled(false);

				vh.iv05.setClickable(true);
				vh.iv05.setEnabled(true);
			}

			setListViewBackGround(convertView,position);
			return convertView;
		}

	}

	static class ViewHolder {
		public TextView tv01;
		public CheckBox cb02;
		public Spinner sp03;
		public EditText et04;
		public ImageView iv05;
		public TextView tv06;
		public ImageView iv07;//照片详细查看
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

}
