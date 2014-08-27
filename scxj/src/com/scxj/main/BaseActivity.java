package com.scxj.main;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.scxj.R;
import com.scxj.utils.Const;
/**
 * Activity基类
 */
public class BaseActivity extends FragmentActivity {
	/**
	 * 上下文
	 */
	protected Context context;
	/**
	 * 进度条弹出框
	 */
	protected ProgressDialog progressDialog = null;
	 
	/**
	 * 主要Handler类，在线程中可用baseHandler
	 * 1、显示等待框 
	 * 2.移除等待框 
	 */
	protected Handler baseHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Const.SHOW_PROGRESS:
					hideProgressDialog();
					showProgressDialog((String)msg.obj);
					break;
				case Const.REMOVE_PROGRESS:
					hideProgressDialog();
					break;
				default:
					break;
			}
		}

		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//设置全屏
		super.onCreate(savedInstanceState);
		context = this;
		//注意这儿的设置， 设置全屏不能取到actionBar,坑我几个小时啊
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
	}
	 
	public void showProgressDialog(String text) {

		if (progressDialog==null) {
			progressDialog = ProgressDialog.show(context, "请稍等...",
					"正在登录...", true);
			progressDialog.setContentView(R.layout.custom_progress);
			progressDialog.setCancelable(false);
			progressDialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface arg0, int arg1,
						KeyEvent arg2) {
					if (arg1 == KeyEvent.KEYCODE_BACK && arg2.getRepeatCount() == 0 && arg2.getAction() == KeyEvent.ACTION_UP) {
						new AlertDialog.Builder(BaseActivity.this)
						.setTitle("警告")
						.setMessage("处理尚未完成，是否停止")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								hideProgressDialog();
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								return;
							}
						}).show();
					}
					return true;
				}
				
			});
			View v = progressDialog.getWindow().getDecorView();
			if (text==null) {
				text="请稍等...";
			}
			setProgressText(v,text);
		}
		
	}
	
	private void setProgressText(View v,String text) {

		if (v instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) v;
			int count = parent.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = parent.getChildAt(i);
				setProgressText(child,text);
			}
		} else if (v instanceof TextView) {
			LayoutParams params = v.getLayoutParams();
			params.width=300;
			v.setLayoutParams(params);
			((TextView) v).setWidth(300);
			((TextView) v).setTextSize(18);
			((TextView) v).setText(text);
			((TextView) v).setTextColor(Color.WHITE);
		}
	}
	
	protected void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public boolean handleMessage(Message message) {
		// TODO Auto-generated method stub
		return false;
	}
	
/*
	public void onAttachedToWindow() {  
		//屏蔽home键
	    this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);  
	    super.onAttachedToWindow();  
	} */
	
	/**
	 * 设置标题栏
	 * 
	 * @param navTitle
	 */
	protected void setNavTitleBar(String navTitle, boolean showBackButton) {
		if (findViewById(R.id.navTitleBar) != null && navTitle != null) {
			ImageButton navBackButton = (ImageButton) findViewById(R.id.navBackButton);
			TextView navTitleName = (TextView) findViewById(R.id.navTitleName);
			ImageButton setting = (ImageButton) findViewById(R.id.navSetting);
			if (showBackButton) {
				navBackButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						((Activity) context).finish();
					}
				});
			} else {
				navBackButton.setVisibility(View.GONE);
			}

			setting.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "点击了一下",0).show();
				}
			});

			navTitleName.setText(navTitle);
		}
	}

	protected void setNavTitleBar(String navTitle) {
		setNavTitleBar(navTitle, true);
	}
	
}
