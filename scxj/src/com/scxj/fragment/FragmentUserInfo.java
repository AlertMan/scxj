package com.scxj.fragment;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.dao.UserDao;
import com.scxj.model.TB_USER;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.StringUtils;
import com.scxj.utils.usb.NetAdapter;

/*
 *用户信息设置
 * */
public class FragmentUserInfo extends BaseFragment {
	EditText et01;
	EditText et02;
	RadioButton rb01;
	RadioButton rb02;
	private ProgressDialog progress = null;
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
				if (progress != null) {
					progress.dismiss();
					progress = null;
				}
				if(msg.obj!=null && !StringUtils.isNull(msg.obj.toString())){
					AudioTipsUtils.showMsg(getActivity(), msg.obj.toString());
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
		View layout = inflater.inflate(R.layout.user_info, container, false);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		TB_USER user = MyApplication.getInstance().loginUser;
		
		et01 = (EditText)getView().findViewById(R.id.newPassword);
		 rb01 = (RadioButton)getView().findViewById(R.id.male);
		 rb02 = (RadioButton)getView().findViewById(R.id.female);
		if("男".equals(user.getSEX())){
			rb01.setChecked(true);
			rb02.setChecked(false);
		}else{
			rb01.setChecked(false);
			rb02.setChecked(true);
		}
		
		et02 = (EditText)getView().findViewById(R.id.phoneNumber);
		et02.setText(user.getTEL());
		
		Button asycBtn = (Button)getView().findViewById(R.id.userInfoAsyc);
		asycBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.setClickable(false);
				progress = ProgressDialog.show(getActivity(), "提示",
						"正在同步用户数据，请稍等……");
				v.setClickable(true);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String pw = et01.getText().toString();
						if(StringUtils.isNull(pw)){
							mHandler.obtainMessage(11, "新密码不能为空！").sendToTarget();
							return;
						}
						
						String sex = "男";
						if(rb02.isChecked()){
							sex = "女";
						}
						String tel = et02.getText().toString();
						String userName = MyApplication.getInstance().loginUser.getUSERNAME();
						String retStr = NetAdapter.updateUserInfo(userName,pw,sex,tel);
						if(!StringUtils.isNull(retStr)){
							try {
								JSONObject jo = new JSONObject(retStr);
								if("0".equals(jo.getString("resultCode"))){
									TB_USER user = MyApplication.getInstance().loginUser;
									user.setPASSWORD(pw);
									user.setSEX(sex);
									user.setTEL(tel);
									new UserDao(getActivity()).updateItem(user);
									mHandler.obtainMessage(11, "用户信息同步成功!").sendToTarget();
								}else{
									String resultDesc = jo.getString("resultDesc") + ",同步失败!";
									mHandler.obtainMessage(11, resultDesc).sendToTarget();
								}
								
							} catch (Exception e) {
								e.printStackTrace();
								mHandler.obtainMessage(11, "接口返回内容格式错误，同步失败!").sendToTarget();
							}
						}else{
							mHandler.obtainMessage(11, "接口返回失败，同步失败!").sendToTarget();
						}
					}
				}).start();
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
	}

}
