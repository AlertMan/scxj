/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scxj;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.SynthesizerListener;
import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_USER;
import com.scxj.service.GpsAlertActivity;
import com.scxj.service.LocationService;
import com.scxj.utils.ApkInstaller;
import com.scxj.utils.AppUtil;
import com.scxj.utils.AutoBrightScreen;
import com.scxj.utils.CrashHandler;
import com.scxj.utils.SharedPreferencesUtil;
import com.scxj.utils.StringUtils;
import com.scxj.utils.usb.NetAdapter;

/**
 * Singleton with hooks to application 保存全局变量 初始各类组件 1、地图引擎 2、定位系统 3、语音引擎
 * 4、相关的全局缓存变量
 */
public class MyApplication extends Application {

	public static String TAG = "SCXJ";
	/**
	 * Singleton pattern
	 */
	private static MyApplication instance;
	public static TB_USER loginUser;
	/**
	 * 百度地图相关
	 */
	public final static String bdMapKey = "862a918d2ec517ce5b78920105f382fd";
	public BMapManager mBMapManager = null;
	public boolean m_bKeyRight = true;
	public static int MapShowIndex = -1;
	/**
	 * 定位相关
	 */
	public LocationClient mLocationClient = null;
	public GeofenceClient mGeofenceClient;
	private boolean isLocationOk = true;

	public AlarmManager alarmManager;
	public PendingIntent locationPendingIntent;
	/**
	 * 语音相关
	 */
	private boolean isInstalledService = false;// 标识是否安装语音服务
	private boolean isAudioPlayOk = true;// 是否能合成语音
	private final String subpatKeyAppid = "516e9461";// appid
	private SpeechSynthesizer mTts;// 合成器

	// 普通数据缓存
	public Hashtable<String, List<? extends Serializable>> dataCache;
	// 软件是否需要更新
	public volatile boolean isSoftwareNeedUpdate = false;

	// 线程终止标志,true表示可以进行更新,false表示取消更新
	public volatile boolean threadFlag = true;

	// 更新数据弹出框
	public volatile boolean hasShown = false;
	
	//定位服务开启情况
	public volatile boolean isLocationServerStarted = false;

	// 杆塔检测项目的附件信息
	public volatile TB_ATTACH attach;
	
	//当前地址信息
	public volatile String currentAddress = "";

	/*
	 * public static TB_INSPECTION inspection = null; public static TB_USER
	 * loginUser = null; public static TB_TOWER tower = null;
	 * 
	 * public static List<List<TB_TOWER>> allLines;//所有的塔信息
	 * 
	 * public static List<TB_TOWER> allTowers;//缓存所有的地理信息 ID 与名称
	 */
	// 缓存图片
	public Hashtable<String, Drawable> imageCache;

	public volatile boolean isPatrol = true;
	
	private SharedPreferencesUtil spu;

	public static MyApplication getInstance() {
		return instance;
	}

	/**
	 * 初期化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			if (code == ErrorCode.SUCCESS) {
				isAudioPlayOk = true;
			}
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
		@Override
		public void onBufferProgress(int progress) throws RemoteException {
			// Log.d(TAG, "onBufferProgress :" + progress);
		}

		@Override
		public void onCompleted(int code) throws RemoteException {
			// Log.d(TAG, "onCompleted code =" + code);
		}

		@Override
		public void onSpeakBegin() throws RemoteException {
			// Log.d(TAG, "onSpeakBegin");
		}

		@Override
		public void onSpeakPaused() throws RemoteException {
			// Log.d(TAG, "onSpeakPaused.");
		}

		@Override
		public void onSpeakProgress(int progress) throws RemoteException {
			Log.d(TAG, "onSpeakProgress :" + progress);
		}

		@Override
		public void onSpeakResumed() throws RemoteException {
			Log.d(TAG, "onSpeakResumed.");
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		threadFlag = true;
		// 初始化语音
		initTts();
		isLocationServerStarted = false;
		spu = new SharedPreferencesUtil(getApplicationContext());
		spu.writeData("TRACK_TASKNAME", "");
		spu.writeData("TRACK_TASKID", "");
		//异常收集器
		 CrashHandler crashHandler = CrashHandler.getInstance();  
         //注册crashHandler  
        crashHandler.init(getApplicationContext()); 
		// 初始化地图引擎
		initEngineManager(this);
		// 初始化定位引擎
		initLocation(this);
		// 稍微延迟一下定位操作
		startGpsLocation();
		//屏幕调整到自动亮度模式，后面可能会设置到最高亮度
		AutoBrightScreen.startAutoBrightness(getApplicationContext());
	}

	// 根据预定位成功情况开启后台定位
	public boolean startLocationService() {
		
		if(!AppUtil.isGpsEnabled(getApplicationContext()) && !AppUtil.isNetworkAvailable(getApplicationContext())){
			isLocationOk = false;
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.requestLocation();
			}
			if(AppUtil.isRunningForeground(getApplicationContext())){
				 Intent it =new Intent(getApplicationContext(),GpsAlertActivity.class); 
				 it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 startActivity(it);
			}
		}
			if (isLocationOk) {
				if (mLocationClient != null) {
					mLocationClient.stop();
					mLocationClient = null;
				}
				if(alarmManager == null){
					// 定时器
					alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				}
				isLocationServerStarted = false;//服务启动情况
				if(locationPendingIntent == null){
					locationPendingIntent = PendingIntent.getService(
							getApplicationContext(), 0, new Intent(
									getApplicationContext(), LocationService.class),
							PendingIntent.FLAG_CANCEL_CURRENT);
				}
				alarmManager.cancel(locationPendingIntent);
				int splitTime = spu.readSpiltTime();
				if(splitTime < 1){
					splitTime = 5;
					spu.writeSpliteTime(""+5);
				}
				alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime(), splitTime * 60 * 1000, locationPendingIntent);
				
				if (mLocationClient != null && mLocationClient.isStarted()) {
					mLocationClient.stop();
				}
				return true;
			} else {
				startGpsLocation();
				return false;
			}
	}
	
	public void startGpsLocation(){
		isLocationOk = false;
		if (mLocationClient == null ) {
			initLocation(this);
		}else{
			setLocationOption(mLocationClient);
		}
		if(!mLocationClient.isStarted()){
			mLocationClient.start();
		}else{
			mLocationClient.requestLocation();
		} 
	}
	
	
	public void setAlarmManagerTime(int minite){
		if(locationPendingIntent != null && alarmManager != null){
			alarmManager.cancel(locationPendingIntent);
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(), minite * 60 * 1000, locationPendingIntent);
		}
	}

	/**
	 * 初始化定位系统,并开启GPS定位。
	 * 
	 * @param context
	 */
	private void initLocation(Context context) {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(context);
			mLocationClient.setAK(bdMapKey);
			// 监听函数 在用的时候再注册。
			setLocationOption(mLocationClient);
			// 在后面不接收定位结果
			mLocationClient.registerLocationListener(new MyLocationListenner());
			mLocationClient.start();
		}
	}

	/**
	 * GPS的第一次定位时间1~2分钟不等，打算预开启GPS定位系统
	 * 
	 * @param mLocationClient
	 */
	private void setLocationOption(LocationClient mLocationClient) {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(false); // 是否需要POI的电话和地址等详细信息
		
		if(AppUtil.isNetworkAvailable(getApplicationContext())){
			option.setPriority(LocationClientOption.NetWorkFirst);// GPS优先
			// 预定一次
			option.setScanSpan(6 * 1000);// 定位时间间隔6秒
			option.setAddrType("all");// 返回地址信息
		}else{
			option.setScanSpan(6 * 1000);// 定位时间间隔6秒
			option.setPriority(LocationClientOption.GpsFirst);// GPS优先
		}
		option.disableCache(true); // 禁止启用缓存定位
		mLocationClient.setLocOption(option);
	}

	/**
	 * 初始化地图引擎
	 * 
	 * @param context
	 */
	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(bdMapKey, new MyGeneralListener())) {
			Toast.makeText(getInstance().getApplicationContext(),
					"地图参数 初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 监听函数，有更新位置的时候 getLocType() 返回值： 61 ： GPS定位结果 62 ： 扫描整合定位依据失败。此时定位结果无效。 63
	 * ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。 65 ： 定位缓存的结果。 66 ：
	 * 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果 67 ：
	 * 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
	 * 161： 表示网络定位结果 162~167： 服务端定位失败。
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				currentAddress = "";
				Log.d("地图预定位", "结果：失败。错误码：" + location.getLocType());
				if (mLocationClient != null && mLocationClient.isStarted()) {
					mLocationClient.requestLocation();
					Toast.makeText(getApplicationContext(),
							"GPS预定位失败，GPS信号接收失败.请移动设备位置!", 0).show();
				}
				isLocationOk = false;
			} else {
				switch (location.getLocType()) {
				case 61:// GPS
				case 161:// 网络定位
					StringBuffer sb = new StringBuffer();
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
					if (location.getLocType() == BDLocation.TypeGpsLocation) {
						sb.append("\nspeed : ");
						sb.append(location.getSpeed());
						sb.append("\nsatellite : ");
						sb.append(location.getSatelliteNumber());
					}
					Log.d("地图预定位", "结果：成功。返回字符串：" + sb.toString());
					isLocationOk = true;
					
					String add = location.getAddrStr();
					if(StringUtils.isNull(add)){
						add = "";
					}
					currentAddress = location.getAddrStr()+"(" +location.getLongitude()+","+location.getLatitude()+")";
				
					break;

				default:
					currentAddress = "";
					if (mLocationClient != null && mLocationClient.isStarted()) {
						/*if(isLocationOk){
							Toast.makeText(getApplicationContext(),
									"GPS预定位失败，GPS信号接收失败.请移动设备位置!", 0).show();
						}*/
						isLocationOk = false;
						mLocationClient.requestLocation();
					}
					break;
				}

			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
			Log.d("地图预POI定位", "结果：未检测.");
		}

	}

	/**
	 * 如果服务组件没有安装，有两种安装方式。 1.直接打开语音服务组件下载页面，进行下载后安装。
	 * 2.把服务组件apk安装包放在assets中，然后copy到SDcard中进行安装。
	 */
	public void processInstall(Context context, String url, String assetsApk) {
		// 直接下载方式
		// ApkInstaller.openDownloadWeb(context, url);
		// 本地安装方式
		if (!ApkInstaller.installFromAssets(context, assetsApk)) {
			isInstalledService = false;
			Toast.makeText(getApplicationContext(), "语音服务组件安装失败！",
					Toast.LENGTH_SHORT).show();
		} else {
			try {// 略微延迟一下
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setMessage("语音组件安装成功,现在重启应用将即可进行语音提示。");
			dialog.setNegativeButton("取消", null);
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
							isInstalledService = true;
							initTts();
							resetApp();
						}
					});
			dialog.show();

		}
	}

	/**
	 * 对合成初始化参数
	 */
	public void initTts() {
		if(mTts == null){
			SpeechUtility.getUtility(getApplicationContext()).setAppid(
					subpatKeyAppid);
			mTts = new SpeechSynthesizer(this, mTtsInitListener);
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME, "xiaoyan");
			mTts.setParameter(SpeechSynthesizer.SPEED, "50");
			mTts.setParameter(SpeechSynthesizer.PITCH, "50");
		}
	}

	/**
	 * 播放接口
	 * 
	 * @param content
	 */
	public void tipsVoicePlayer(String content) {
		try {
			if (isAudioPlayOk ) {
				if (mTts.isSpeaking()) {
					mTts.stopSpeaking(mTtsListener);
				}
				mTts.startSpeaking(content, mTtsListener);
			}
		} catch (Exception e) {
				mTts = null;
				initTts();
				mTts.startSpeaking(content, mTtsListener);
			e.printStackTrace();
		}
	}

	public void tipsVoicePlayerStop() {
		try {
			if (isAudioPlayOk && mTts != null) {
				if (mTts.isSpeaking()) {
					mTts.stopSpeaking(mTtsListener);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		
		System.out.println("应用app停止退出!");
		// 定位
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		
		isLocationServerStarted = false;
		// 清空应用数据
		spu.writeData("TRACK_TASKNAME", "");
		spu.writeData("TRACK_TASKID", "");
		if(alarmManager != null){
			alarmManager.cancel(locationPendingIntent);
		}
		
		// USB
		NetAdapter.stopAdapter();
		
		/*AppUtil.stopRunningService(getApplicationContext(),
				"com.scxj.service.LocationService");
		// 停止远程服务
		String baiduService = "com.baidu.location.f";
		if (AppUtil.isServiceRunning(getApplicationContext(), baiduService)) {
			AppUtil.stopRunningService(getApplicationContext(), baiduService);
		}*/
		// 停止本地服务
		stopService(new Intent(getApplicationContext(), LocationService.class));
		// 地图引擎
		if (mBMapManager != null) {
			mBMapManager.stop();
			try {
				mBMapManager.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mBMapManager = null;
		}
		// 语音
		if (mTts != null) {
			try {//停止ＡＰＩ有时候报错
				mTts.stopSpeaking(mTtsListener);
				// 退出时释放连接
				mTts.destory();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		//自杀一把
		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
		
	}

	// 低内存时删除缓存
	@Override
	public void onLowMemory() {
		clearCacheMem();
		super.onLowMemory();
	}

	public void clearCacheMem() {
		dataCache.clear();
	}

	/**
	 * Retrieves application's version number from the manifest
	 * 
	 * @return
	 */
	public String getVersion() {
		String version = "0.0.0.0";
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 重启应用
	 */
	public void resetApp() {
		Intent i = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	/**
	 * 描述：卸载程序.
	 * 
	 * @param context
	 *            the context
	 * @param packageName
	 *            包名
	 */
	public static void uninstallApk(Context context, String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				/*
				 * Toast.makeText(getInstance().getApplicationContext(),
				 * "地图：您的网络出错啦！", Toast.LENGTH_LONG).show();
				 */
				Log.e("应用程序", "地图：您的网络出错啦！");
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				/*
				 * Toast.makeText(getInstance().getApplicationContext(),
				 * "地图：输入正确的检索条件！", Toast.LENGTH_LONG).show();
				 */

				Log.e("应用程序", "地图：输入正确的检索条件！");
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				/*
				 * Toast.makeText(getInstance().getApplicationContext(),
				 * "地图：请配置正确的授权Key！", Toast.LENGTH_LONG).show();
				 */
				getInstance().m_bKeyRight = false;

				Log.e("应用程序", "地图：请配置正确的授权Key！");
			}
		}
	}

}
