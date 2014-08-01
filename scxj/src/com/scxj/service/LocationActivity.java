package com.scxj.service;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

/**
 * 定位activity
 * @author think
 *
 */
public class LocationActivity extends Activity {
	private LocationClient mLocationClient;
	private boolean isStartLocation = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
				
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		MyApplication.getInstance().isLocationServerStarted = true;
		if(!AppUtil.isGpsOPen(this)){
			if(AppUtil.isRunningForeground(this)){
				 Intent it =new Intent(this,GpsAlertActivity.class); 
				 it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 startActivity(it);
			}
		}
		
		mLocationClient = new LocationClient(this);
		if(mLocationClient == null){
			initLocation(getApplication());
		}
		//重新注册监听函数 并启动定位 
		setLocationOption(mLocationClient);
		mLocationClient.registerLocationListener( new MyLocationListenner() );
		mLocationClient.start();
		isStartLocation = true;
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (mLocationClient != null && mLocationClient.isStarted()){
			mLocationClient.requestLocation();
		}else{
//			Toast.makeText(getApplication(), "注意定位服务：服务第一次定位启动，未成功！", 0).show();
//			mLocationClient.start();
		}
	}
	
	/**
	 * 初始化定位系统
	 * @param context
	 */
	private void initLocation(Context context) {
		if(mLocationClient == null){
			mLocationClient = new LocationClient( context );
			mLocationClient.setAK(MyApplication.getInstance().bdMapKey);
		}
	}
	
	/**
	 * GPS的定位时间比较长，打算预开启定位系统
	 * @param mLocationClient
	 */
	private void setLocationOption(LocationClient mLocationClient){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//打开gps
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(false);	//是否需要POI的电话和地址等详细信息  
		
		//在这儿是一次性定位，周期控制通过全局时钟
		if(AppUtil.isNetworkAvailable(getApplicationContext())){
			option.setPriority(LocationClientOption.NetWorkFirst);// 网络优先
			option.setAddrType("all");// 返回地址信息
		}else{
			option.setPriority(LocationClientOption.GpsFirst);// GPS优先
		}
		

		option.disableCache(true);	//禁止启用缓存定位	
		mLocationClient.setLocOption(option);
	}
		
	@Override
	public void onDestroy() {
		Log.d("定位服务", "onDestroy");
		if(mLocationClient != null){
			mLocationClient.stop();
		}
		super.onDestroy();
	}
	
	/**
	 * 监听函数，有更新位置的时候
	 */
	public class MyLocationListenner implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			if(isStartLocation){
				Log.d("定位服务", "onReceiveLocation");
				if(location == null	){
					Log.d("定位服务", "结果：失败。错误码："+location.getLocType());
					if(mLocationClient != null && mLocationClient.isStarted()){
						mLocationClient.requestLocation();
					}
					isStartLocation = true;
				}else{
					switch (location.getLocType()) {
					case 61://GPS
					case 161://网络定位
						StringBuffer sb = new StringBuffer(256);
						sb.append("time : ");
						sb.append(location.getTime());
						sb.append("\nerror code : ");
						sb.append(location.getLocType());
						sb.append("\nlatitude : ");
						sb.append(location.getLatitude());
						sb.append("\nlontitude : ");
						sb.append(location.getLongitude());
						sb.append("\nradius : ");
						sb.append(location.getRadius());
						sb.append("\naddress : ");
						sb.append(location.getAddrStr());
						if (location.getLocType() == BDLocation.TypeGpsLocation){
							sb.append("\nspeed : ");
							sb.append(location.getSpeed());
							sb.append("\nsatellite : ");
							sb.append(location.getSatelliteNumber());
						}
						MyApplication.getInstance().currentAddress = location.getAddrStr(); 
						Log.d("定位服务", "结果：成功。返回字符串："+sb.toString());
						SharedPreferencesUtil spu = new SharedPreferencesUtil(getApplicationContext());
						if(!StringUtils.isNull(spu.readData("TRACK_TASKID"))){
							String taskType = spu.readTaskType();
							Log.d("定位服务", taskType+"定位结果：成功。返回字符串时间："+DateUtil.dateToString(new Date()));
							TB_TRACK track = new TB_TRACK();
							track.setTRACKID(null);
							track.setLAT(location.getLatitude()+"");
							track.setLNG(location.getLongitude() + "");
							track.setCREATETIME(DateUtil.dateToString(new Date()));
							track.setSTARTADDRESS(location.getAddrStr());
							track.setTASKID(spu.readData("TRACK_TASKID"));
							track.setTASKNAME(spu.readData("TRACK_TASKNAME"));
							track.setENDADDRESS(location.getAddrStr());
							track.setNOTE(spu.readData("TRACK_NOTE"));
							String userId = spu.readData("TRACK_USERID");
							
								if("巡视任务".equals(spu.readData("TRACK_TYPE"))){
									new TrackTaskDao(getBaseContext()).insertItem(track,userId);
								}else if("消缺任务".equals(spu.readData("TRACK_TYPE"))){
									new TrackDefectDao(getBaseContext()).insertItem(track,userId);
								}else{
									Log.e("LocationService->onReceiveLocation", "轨迹数据存储出错！");
								}
							
							isStartLocation = false;
						}
						break;

					default:
						isStartLocation = true;
						Log.d("定位服务", "结果：失败。错误码："+location.getLocType());
						if(mLocationClient != null && mLocationClient.isStarted()){
							mLocationClient.requestLocation();
						}
						break;
					}
				}
				
				
			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
			Log.d("定位服务", "onReceivePoi");
		}
	}
	

}
