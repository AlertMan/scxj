package com.scxj.utils;

import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;

/**
 *
 * @author jiuhua.song
 */
public class ActivityStackManage {
	private LinkedList<Activity> linkedStack = new LinkedList<Activity>();
	private static ActivityStackManage instance;
	
	private ActivityStackManage(){
		
	}
	
	public static ActivityStackManage getInstance(){
		if(instance == null){
			synchronized(ActivityStackManage.class) {
				instance = new ActivityStackManage();
			}
		}
		return instance;
	}
	/**
	 * 
	 * @param activity
	 */
	public void push(Activity activity){
		linkedStack.addFirst(activity);
	}
	
	public void cleanActivity(){
		Iterator<Activity> iter = linkedStack.iterator();
		while(iter.hasNext()){
			Activity act = iter.next();
			if(act == null){
				continue;
			}
			
			act.finish();
			act = null;
			iter.remove();
		}
	}
	
}
