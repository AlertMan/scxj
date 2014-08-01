package com.scxj.utils;

import android.os.Handler;

/**
 * 不用守护进程方式，在android中可以直接使用handler线程间交互
 * @author think
 *
 */
public class ThreadWaitUtil extends Thread {

	private static long TIME_OUT = 3000;
	private Handler mHandler =null;
	
	public ThreadWaitUtil(long millTimeOut,Handler handler) {
		// TODO Auto-generated constructor stub
		TIME_OUT = millTimeOut;
		mHandler = handler;
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		while(true){
			if(System.currentTimeMillis() - start >= TIME_OUT){
				mHandler.obtainMessage(999).sendToTarget();
				return;
			}
		}
	}
	
	
}
