package com.scxj.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.dao.AttachDefectDao;
import com.scxj.dao.AttachTaskDao;
import com.scxj.dao.RetDefectDao;
import com.scxj.dao.RetTaskDao;
import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_TASK_RET;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.Const;
import com.scxj.utils.CustomTouchListener;
import com.scxj.utils.FileUtil;
import com.scxj.utils.StringUtils;

public class FragmentPictureDetails extends BaseFragment implements OnTouchListener {
	private List<TB_ATTACH> attachs;
	private Bitmap bitmap;
	private boolean isTaskAttach = false;
	private String userName="";
	private String taskId = "";
	private String taskDefectId="";
	private int selectInd = 0 ;
	private OnDataRefreshListener onDataRefreshListener;
	TextView detail1;
	TextView detail2;
	TextView detail3;
	TextView detail4;
	TextView detail5;
	TextView detail6;
	TextView detail7;
	
	public void setOnDataRefreshListener(OnDataRefreshListener onDataRefreshListener) {
		this.onDataRefreshListener = onDataRefreshListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		if (container == null) 
		{
            return null;
        }
	    View layout = inflater.inflate(R.layout.photo_info, container, false); 
	    layout.setOnTouchListener(this);
		return layout;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
		menu.add("删除").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 点击使图片能删除
        if("删除".equals(item.getTitle())){

			new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.dialog)
					.setTitle("请确认是否删除此图片,删除后将不可恢复！")
					.setPositiveButton(
							"删除",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									if(isTaskAttach){
									
									// 删除图片 删除数据库中的数据
									new AttachTaskDao(getActivity())
											.delItem(userName,taskId,attachs.get(selectInd).getATTACHID());
									}else{
										new AttachDefectDao(getActivity()).delItem(userName,taskDefectId,attachs.get(selectInd).getATTACHID());
									}
									FileUtil.deleteFile(attachs.get(selectInd).getATTACHCONTENT());
									onDataRefreshListener.refreshDatas();
									getFragmentManager().popBackStack();
								}
							})
					.setNegativeButton(
							"取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									dialog.dismiss();
									dialog.cancel();
								}
							}).create().show();
		}
        
        
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		Bundle b = getArguments();
		
		isTaskAttach = b.getBoolean("isTask",false);
		attachs = (List<TB_ATTACH>)b.getSerializable("items");
		taskId = b.getString("taskId");
		taskDefectId = b.getString("taskDefectId");
		
		  detail1=(TextView) getView().findViewById(R.id.photo_detail_tv01);
		  detail2=(TextView) getView().findViewById(R.id.photo_detail_tv02);
		  detail3=(TextView) getView().findViewById(R.id.photo_detail_tv03);
		  detail4=(TextView) getView().findViewById(R.id.photo_detail_tv04);
		  detail5=(TextView) getView().findViewById(R.id.photo_detail_tv05);
		  detail6=(TextView) getView().findViewById(R.id.photo_detail_tv06);
		  detail7=(TextView) getView().findViewById(R.id.photo_detail_tv07);
		  userName = MyApplication.getInstance().loginUser.getUSERNAME();
		if(attachs!=null && !attachs.isEmpty()){
			getViewInfo(0);
			
		}else{
			//提示用户此条线路下没有图片
		}
		
		ImageView prevIv = (ImageView) getView().findViewById(R.id.photo_pre);
		ImageView prevNext = (ImageView) getView().findViewById(R.id.photo_next);
		
		prevIv.setOnTouchListener(new CustomTouchListener() {
			
			@Override
			public void eventAction(View arg0) {
				getViewInfo(--selectInd);
			}
		});
		
		prevNext.setOnTouchListener(new CustomTouchListener() {
			
			@Override
			public void eventAction(View arg0) {
				getViewInfo(++selectInd);
			}
		});
	}
	
	
	/**
	 * 根据序列号获取图片
	 * @param i
	 */
	private void  getViewInfo(int i) {
		if(i < 0){
			selectInd = 0;
		}else if(i >= attachs.size()){
			selectInd = 0;
		}else{
			selectInd = i;
		}
		
		TB_ATTACH attach =  attachs.get(selectInd);
		
		
		getBitmap(attach.getATTACHCONTENT());
		ImageView iv = (ImageView) getView().findViewById(R.id.photo_detail_iv);
		iv.setImageBitmap(this.bitmap);
		
		detail1.setText("照片名：\n"+attach.getATTACHNAME());
		detail6.setText("照片拍摄时间：\n"+attach.getCREATETIME());
		if(StringUtils.isNull(attach.getCREATEADDRESS())){
			attach.setCREATEADDRESS("GPS信息为空");
		}
		detail7.setText("照片拍摄地点：\n"+attach.getCREATEADDRESS());
		
		
		if(isTaskAttach){
			//注意巡检任务0与缺陷任务1的区分
			Const.DB_NAME.setUserTaskDirectory(userName, taskId, false, 0);
			TB_TASK_RET taskRet = new RetTaskDao(getActivity()).getTaskRetById(attach.getPARENTID());
			taskId = taskRet.getTASKID();
			detail2.setText("所属线路：\n"+taskRet.getLINENAME());
			detail3.setText("任务：\n"+taskRet.getTASKNAME());
			detail4.setText("所属杆塔：\n"+taskRet.getTOWERNAME());
			detail5.setText("杆塔电压等级：\n"+taskRet.getVOTALLEVEL());
			detail5.setVisibility(View.VISIBLE);
		}else{
			Const.DB_NAME.setUserTaskDirectory(userName, taskDefectId, false, 1);
			TB_TASK_DEFECT taskDefectRet = new RetDefectDao(getActivity()).getTaskRetByTaskId(attach.getPARENTID());
			taskDefectId = taskDefectRet.getDEFECTTASKID();
			detail2.setText("所属线路：\n"+taskDefectRet.getLINENAME());
			detail3.setText("任务：\n"+taskDefectRet.getDEFECTTASKNAME());
			detail4.setText("所属杆塔：\n"+taskDefectRet.getTOWERNAME());
			detail5.setText("杆塔电压等级：\n");
			detail5.setVisibility(View.GONE);
		}
	}

	/**
	 * 前面那个类EditPicture也需要用到
	 * 
	 * @return
	 */
	private void getBitmap(String photoPath) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		this.bitmap = BitmapFactory.decodeFile(photoPath, options);

		if (this.bitmap == null) {
			this.bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.pic_has_del);
		}
	}

	//屏蔽一下
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
	 
		return true;
	}
	
	
	
}
