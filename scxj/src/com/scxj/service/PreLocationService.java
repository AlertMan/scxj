package com.scxj.service;

import java.util.Date;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.scxj.MyApplication;
import com.scxj.dao.TrackDefectDao;
import com.scxj.dao.TrackTaskDao;
import com.scxj.model.TB_TRACK;
import com.scxj.utils.AppUtil;
import com.scxj.utils.DateUtil;
import com.scxj.utils.SharedPreferencesUtil;
import com.scxj.utils.StringUtils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * 长期存在的预定位服务
 * 
 * @author think
 */
public class PreLocationService extends Service {
	private LocationClient mLocationClient;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("定位服务", "onStartCommand");
		if(!AppUtil.isGpsOPen(getApplication())){
			if(AppUtil.isRunningForeground(getApplication())){
				 Intent it =new Intent(getApplication(),GpsAlertActivity.class); 
				 it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 startActivity(it);
			}
		}

		// mLocationClient = ((MyApplication)getApplication()).mLocationClient;
		mLocationClient = new LocationClient(getApplication());
		if (mLocationClient == null) {
			initLocation(getApplication());
		}
		// 重新注册监听函数 并启动定位
		setLocationOption(mLocationClient);
		mLocationClient.registerLocationListener(new MyLocationListenner());
		mLocationClient.start();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		} else {
			// Toast.makeText(getApplication(), "注意定位服务：服务第一次定位启动，未成功！",
			// 0).show();
			mLocationClient.start();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	//启用GPS
		public void openGPS() {
				new AlertDialog.Builder(getApplication()).setTitle("轨迹服务需要打开GPS")
				.setMessage("请您在室外时执行以下操作：\n在位置设置中打开GPS")
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).setPositiveButton("设置",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								getApplication().startActivity(intent);
							}
						}).show();
			}

	
	public  void openGPS(final Context context) {
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
		dlgBuilder.setTitle("轨迹服务需要打开GPS");
		dlgBuilder.setMessage("请您在室外时执行以下操作：\n在位置设置中打开GPS");
		dlgBuilder.setNegativeButton("否",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton("设置",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						context.startActivity(intent);
					}
				});
		dlgBuilder.create().show();
	}
	/**
	 * 初始化定位系统
	 * 
	 * @param context
	 */
	private void initLocation(Context context) {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(context);
			mLocationClient.setAK(((MyApplication) getApplication()).bdMapKey);
		}
	}

	/**
	 * GPS的定位时间比较长，打算预开启定位系统
	 * 
	 * @param mLocationClient
	 */
	private void setLocationOption(LocationClient mLocationClient) {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(false); // 是否需要POI的电话和地址等详细信息

		// 在这儿是一次性定位，周期控制通过全局时钟
		if (AppUtil.is3G(getApplicationContext())
				|| AppUtil.isNetworkAvailable(getApplicationContext())) {
			option.setPriority(LocationClientOption.NetWorkFirst);// 网络优先
			// 预定一次
			option.setScanSpan(15 * 1000);// 定位时间间隔5秒，后面会根据设置参数修改
			option.setAddrType("all");// 返回地址信息
		} else {
			option.setPriority(LocationClientOption.GpsFirst);// GPS优先
		}

		option.disableCache(true); // 禁止启用缓存定位
		mLocationClient.setLocOption(option);
	}

	@Override
	public void onCreate() {
		Log.d("定位服务", "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d("定位服务", "onDestroy");
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onDestroy();
	}

	/**
	 * 监听函数，有更新位置的时候
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.d("定位服务", "onReceiveLocation");
			Log.d("定位服务", "结果：失败。错误码：" + location.getLocType());
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.requestLocation();
			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
			Log.d("定位服务", "onReceivePoi");
		}
	}

}
