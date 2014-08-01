package com.scxj.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * 自定义监听器
 * @author zhudameng
 *
 */
public abstract class CustomTouchListener implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v instanceof ImageView) {
				((ImageView) v).setAlpha(50);
			}else{
				v.getBackground().setAlpha(50);
			}
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			
			if (v instanceof ImageView) {
				((ImageView) v).setAlpha(255);
			}else{
				v.getBackground().setAlpha(255);
			}
			eventAction(v);
		}
		return false;
	}
	
	
	public abstract void eventAction(View arg0);

}
