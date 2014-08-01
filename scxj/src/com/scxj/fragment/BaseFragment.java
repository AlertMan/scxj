package com.scxj.fragment;

import com.scxj.R;

import android.R.color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;



/**
 * @author think
 *
 */
public  class BaseFragment extends Fragment {
	
	protected FragmentManager mFragmentManager;
	public FragmentManager getmFragmentManager() {
		return mFragmentManager;
	}

	public void setmFragmentManager(FragmentManager mFragmentManager) {
		this.mFragmentManager = mFragmentManager;
	}

	boolean mCanBack = false;
	
	/**
	 * 是否到了最上层
	 * @return
	 */
	public boolean canBack() {
		return (mFragmentManager.getBackStackEntryCount() > 0);
	}
	
	public void onBackPressed() {
		mFragmentManager.popBackStack();
	}
	
	/**
	 * 设置单元背景色
	 * @param itemView
	 * @param pos
	 */
	public void setListViewBackGround(View itemView ,int pos){
		if (pos%2==0) {
			itemView.setBackgroundResource(color.transparent);
		}else{
			itemView.setBackgroundResource(R.color.gray_white);
		}
		
	}
	
}
