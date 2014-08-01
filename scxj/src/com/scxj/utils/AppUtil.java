/*
 * Copyright (C) 2013 www.418log.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scxj.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * The Class AppUtil.
 */
public class AppUtil {
	public static boolean DEBUG = false;

	/**
	 * �������򿪲���װ�ļ�.
	 *
	 * @param context the context
	 * @param file apk�ļ�·��
	 */
	public static void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	/**
	 * ������ж�س���.
	 *
	 * @param context the context
	 * @param packageName ����
	 */
	public static void uninstallApk(Context context,String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}


	/**
	 * �����жϷ����Ƿ�����.
	 *
	 * @param ctx the ctx
	 * @param className �жϵķ�������
	 * @return true ������ false ��������
	 */
	public static boolean isServiceRunning(Context ctx, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
		Iterator<RunningServiceInfo> l = servicesList.iterator();
		while (l.hasNext()) {
			RunningServiceInfo si = (RunningServiceInfo) l.next();
			String serName = si.service.getClassName();
			Log.d("isServiceRunning", serName);
			if (className.equals(serName)) {
				isRunning = true;
			}
		}
		return isRunning;
	}

	/**
	 * ֹͣ����.
	 *
	 * @param ctx the ctx
	 * @param className the class name
	 * @return true, if successful
	 */
	public static boolean stopRunningService(Context ctx, String className) {
		Intent intent_service = null;
		boolean ret = false;
		try {
			intent_service = new Intent(ctx, Class.forName(className));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intent_service != null) {
			ret = ctx.stopService(intent_service);
		}
		return ret;
	}
	

	/** 
	 * Gets the number of cores available in this device, across all processors. 
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu" 
	 * @return The number of cores, or 1 if failed to get result 
	 */ 
	public static int getNumCores() { 
		try { 
			//Get directory containing CPU info 
			File dir = new File("/sys/devices/system/cpu/"); 
			//Filter to only list the devices we care about 
			File[] files = dir.listFiles(new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					//Check if filename is "cpu", followed by a single digit number 
					if(Pattern.matches("cpu[0-9]", pathname.getName())) { 
					   return true; 
				    } 
				    return false; 
				}
				
			}); 
			//Return the number of cores (virtual CPU devices) 
			return files.length; 
		} catch(Exception e) { 
			//Default to return 1 core 
			return 1; 
		} 
	} 
	
	
	/**
	 * �������ж������Ƿ���Ч.
	 *
	 * @param context the context
	 * @return true, if is network available
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Gps�Ƿ��
	 * ��Ҫ<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />Ȩ��
	 *
	 * @param context the context
	 * @return true, if is gps enabled
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
	    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * wifi�Ƿ��.
	 *
	 * @param context the context
	 * @return true, if is wifi enabled
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * �жϵ�ǰ�����Ƿ���wifi����.
	 *
	 * @param context the context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * �жϵ�ǰ�����Ƿ���3G����.
	 *
	 * @param context the context
	 * @return boolean
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
	
	/**
	 * ����������Ϊ����ģʽ.
	 *
	 * @param debug the new debug
	 */
	public static void setDebug(boolean debug){
		DEBUG = debug;
	}
	
	
	/**
	 *  
	 * @param componentName
	 * @param context
	 * @return
	 */
    public static boolean isProcessRunning(String componentName,Context context){  
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
      
        //获取正在运行的应用   
        List<RunningAppProcessInfo> run = am.getRunningAppProcesses();  
        for(RunningAppProcessInfo ra : run){  
            if(ra.processName.equals(componentName)){  
                return true;  
            }  
        }  
        return false;  
    }  
    
    public static void stopRunningProcess(String componentName,Context context){
    	ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);  
    	am.killBackgroundProcesses(componentName);
    }
    
    
    /**
     * 获取设备ＩＤ
     * @return
     */
    public static String getDeviceId(Context context) {
        String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }
  
    /**
     * 判断是否打开
     * @param context
     * @return
     */
public static  boolean isGpsOPen( Context context) { 
   LocationManager locationManager  
                            = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
   // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快） 
   boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
   // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位） 
  /*  boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); 
  if (gps || network) { 
       return true; 
   } */
   return gps; 
}

/**
 * 
 * @return
 */
public static boolean isRunningForeground(Context mContext){
    String packageName=mContext.getPackageName();
    String topActivityClassName=getTopActivityName(mContext);
    System.out.println("packageName="+packageName+",topActivityClassName="+topActivityClassName);
    if (packageName!=null&&topActivityClassName!=null&&topActivityClassName.startsWith(packageName)) {
        return true;
    } else {
        return false;
    }
}
 
 
	private static  String getTopActivityName(Context context){
	    String topActivityClassName=null;
	     ActivityManager activityManager =
	    (ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE )) ;
	     List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;
	     if(runningTaskInfos != null){
	         ComponentName f=runningTaskInfos.get(0).topActivity;
	         topActivityClassName=f.getClassName();
	     }
	     return topActivityClassName;
	}
 
}
