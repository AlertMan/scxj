package com.scxj.fragment;

import java.io.File;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import cn.pisoft.base.db.DBHelper;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.dao.RetDefectDao;
import com.scxj.dao.TaskDefectDao;
import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_USER;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.Const;
import com.scxj.utils.DateUtil;

/*
 * 任务杆塔检测记录
 * */
public class FragmentTaskMainDefect extends BaseFragment {
	private TB_TASK_DEFECT defitem;//消制任务数据
	private TB_TASK_DEFECT retItem;//回传数据
	private RetDefectDao rdd;
	private OnDataRefreshListener onPreDataRefresh;
	
	EditText et01 ;
	EditText et02;
	EditText et03 ;
	EditText et04;
	EditText et05 ;
	EditText et06 ;
	EditText et07 ;
	EditText et08 ;
	EditText et09 ;
	EditText et10 ;
	EditText et11 ;
	EditText et12 ;
	EditText et13 ;
	EditText et14 ;
	
	
	private ProgressDialog pd;
	private Handler mHandler = new Handler();
	private String msg = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		View layout = inflater.inflate(R.layout.defect_task,
				container, false);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		defitem = (TB_TASK_DEFECT)getArguments().getSerializable("item");
		String userName = MyApplication.getInstance().loginUser.getUSERNAME();
		String taskId = defitem.getDEFECTTASKID();
		Const.DB_NAME.setUserTaskDirectory(userName, taskId, false, 1);
		
		rdd = new RetDefectDao(getActivity());
		retItem = rdd.getTaskRetByTaskId(defitem.getDEFECTTASKID());
		if(retItem == null){//回传库里面没有数据　则插入此条数据
			rdd.insertItem(defitem);
			retItem = defitem;
		}
		
		  et01 = (EditText)getView().findViewById(R.id.defect_task_id);
		  et02 = (EditText)getView().findViewById(R.id.classname);
		  et03 = (EditText)getView().findViewById(R.id.start_class_name);
		  et04 = (EditText)getView().findViewById(R.id.sign_person);
		  et05 = (EditText)getView().findViewById(R.id.find_time);
		  et06 = (EditText)getView().findViewById(R.id.defect_type);
		  et07 = (EditText)getView().findViewById(R.id.finish_time);
		  et08 = (EditText)getView().findViewById(R.id.defect_desc);
		  et09 = (EditText)getView().findViewById(R.id.action_class_name);
		  et10 = (EditText)getView().findViewById(R.id.login_user_name);
		  et11 = (EditText)getView().findViewById(R.id.download_time);
		  et12 = (EditText)getView().findViewById(R.id.retdesc);
		  et13 = (EditText)getView().findViewById(R.id.login_user);
		  et14 = (EditText)getView().findViewById(R.id.sucess_time);
		
		  
		TB_USER user = MyApplication.getInstance().loginUser;
		et01.setText(retItem.getDEFECTTASKID());
		et02.setText(retItem.getCLASSNAME());
		et03.setText(retItem.getSTARTCLASSNAME());
		et04.setText(retItem.getSIGNPERSON());
		String findTime = retItem.getFINDTIME();
		int findTimeIndex = findTime.lastIndexOf(".0");
		if(findTimeIndex != -1){
			findTime = findTime.substring(0,findTimeIndex);
		}
		et05.setText(findTime);
		et06.setText(retItem.getDEFECTTYPE());
		String finishTime = retItem.getFINISHTIME();
		int finishTimeIndex = finishTime.lastIndexOf(".0");
		if(finishTimeIndex != -1){
			finishTime = finishTime.substring(0,finishTimeIndex);
		}
		et07.setText(finishTime);
		et08.setText(retItem.getDEFECTDESC());
		et09.setText(user.getCLASSNAME());
		et10.setText(user.getREALNAME());
		
		String downloadTime = retItem.getDOWNLOADTIME();
		int downloadTimeIndex = downloadTime.lastIndexOf(".0");
		if(downloadTimeIndex != -1){
			downloadTime = downloadTime.substring(0,finishTimeIndex);
		}
		et11.setText(downloadTime);
		et12.setText(retItem.getRETDESC());
		et13.setText(user.getREALNAME());
		et14.setText(DateUtil.dateToString(new Date()));
		
	}
	
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("拍照").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
         menu.add("保存").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
      
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//隐蔽软件键盘
    //	((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
    	if("拍照".equals(item.getTitle())){//中转到拍照
    		TB_ATTACH ath  = new TB_ATTACH();
			String imageName = DateUtil.dateToString(new Date(),
					"yyyyMMddHHmmss") + ".jpg";
			String imageFilePath = DBHelper.DB_PATH+"/" + imageName;
			File imageFile = new File(imageFilePath);
			Uri imageFileUri = Uri.fromFile(imageFile);
			Intent i = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						imageFileUri);
				i.putExtra(android.provider.MediaStore.Images.Media.ORIENTATION,
						0);
			ath.setATTACHCONTENT(imageFilePath);	
			ath.setATTACHID(retItem.getDEFECTTASKID());
			ath.setATTACHNAME(imageName);
			ath.setCREATETIME(DateUtil.dateToString(new Date()));
			ath.setPARENTID(retItem.getDEFECTTASKID());//关联任务回传表
			ath.setFile(imageFile);
			((MyApplication)getActivity().getApplication()).attach = null;
			((MyApplication)getActivity().getApplication()).attach = ath;
			getActivity().startActivityForResult(i,10);
    	}
        if("保存".equals(item.getTitle())){//直接更新数据库
        	if(pd != null){
        		pd.dismiss();
        		pd = null;
        	}
        	pd = new ProgressDialog(getActivity()).show(getActivity(), "提示",
					"正在保存数据,原数据将被替换,请稍等……");
        	new Thread(new Runnable() {
				@Override
				public void run() {
					retItem.setDEFECTTASKID(et01.getText().toString());
		          	retItem.setCLASSNAME(et02.getText().toString());
		          	retItem.setSTARTCLASSNAME(et03.getText().toString());
		          	retItem.setSIGNPERSON(et04.getText().toString());
		          	retItem.setFINDTIME(et05.getText().toString());
		          	retItem.setDEFECTTYPE(et06.getText().toString());
		          	retItem.setFINISHTIME(et07.getText().toString());
		          	retItem.setDEFECTDESC(et08.getText().toString());
		          	retItem.setACTIONCLASSNAME(et09.getText().toString());
		          	retItem.setSIGNPERSON(et10.getText().toString());
		          	retItem.setDOWNLOADTIME(et11.getText().toString());
		          	retItem.setRETDESC(et12.getText().toString());
		          	retItem.setLEADUSERNAME(et13.getText().toString());
		          	retItem.setSUCESSTIME(et14.getText().toString());
		        	rdd.updateItem(retItem);
		        	
		        	if(!"已消缺".equals(defitem.getSTATUS())){
		        	
			        	mHandler.post(new Runnable() {
							@Override
							public void run() {
								new AlertDialog.Builder(getActivity())
								.setIcon(R.drawable.dialog)
								.setTitle("提示")
								.setMessage("缺陷是否关闭,任务状态将更新为已消缺!")
								.setPositiveButton(
										"立即关闭",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												if(!"已消缺".equals(defitem.getSTATUS())){
									        		defitem.setSTATUS("已消缺");
									        		new TaskDefectDao(getActivity()).updateItem(defitem);
									        		if(onPreDataRefresh != null){
									        			onPreDataRefresh.refreshDatas();
									        		}
									        	}
												AudioTipsUtils.showMsg(getActivity(), "保存成功");
											}
										})
								.setNegativeButton(
										"暂不关闭",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
									        		defitem.setSTATUS("消缺中");
									        		new TaskDefectDao(getActivity()).updateItem(defitem);
									        		if(onPreDataRefresh != null){
									        			onPreDataRefresh.refreshDatas();
									        		}
												AudioTipsUtils.showMsg(getActivity(), "保存成功");
												dialog.dismiss();
												dialog.cancel();
											}
										}).create().show();
								pd.cancel();
								pd = null;
							}
						});
		        	
				}else{
	        		mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							if(onPreDataRefresh != null){
			        			onPreDataRefresh.refreshDatas();
			        		}
							AudioTipsUtils.showMsg(getActivity(), "保存成功");
			        		pd.dismiss();
							pd = null;
						}
					});
	        		
				}
			}
        	}).start();
        	
        	
        	
        	
        	
        }
        return super.onOptionsItemSelected(item);
    }


	public void setOnPreDataRefresh(OnDataRefreshListener onPreDataRefresh) {
		this.onPreDataRefresh = onPreDataRefresh;
	}
    
}
