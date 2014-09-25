package com.scxj.main;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.scxj.R;


/**
 * 选择是否任务与绑卡
 * @author Peter
 */
public class MainChoiceActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

		setContentView(R.layout.main_choice);

	}

	public void startToMain(View view){
		startActivity(new Intent(this, MainMenuActivity.class));
		finish();
	}
	
	public void startToBinding(View view){
		startActivity(new Intent(this, BindCardActivity.class));
		finish();
	}

}

