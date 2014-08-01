package com.scxj.main;

import com.scxj.R;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;


public class SplashScreen extends BaseActivity {
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

		setContentView(R.layout.splashscreen);

	}

	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				Intent mainIntent = new Intent(SplashScreen.this,
						LoginActivity.class);
				SplashScreen.this.startActivity(mainIntent);
				// 测试动画
				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);

				SplashScreen.this.finish();
			}
		}, 1000);
	}

}

