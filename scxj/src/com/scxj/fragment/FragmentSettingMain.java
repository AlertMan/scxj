package com.scxj.fragment;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.main.LoginActivity;
import com.scxj.main.MainMenuActivity;
import com.scxj.utils.AppUtil;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.CopyFileUtil;
import com.scxj.utils.SharedPreferencesUtil;
import com.scxj.utils.StringUtils;

/*
 *系统设置
 * */
public class FragmentSettingMain extends BaseFragment {
	private int min = 0;
	private EditText spilt_min;
	private SharedPreferencesUtil spu;

	private MapView mMapView = null;
	private MKOfflineMap mOffline = null;
	private MapController mMapController = null;
	private TextView tipsTv;
	private Button mapDataImport;
	private TextView tracks;
	private ProgressDialog pd;
	private boolean isLive = true;
	private int sdCnt = 0;
	private Thread t = new Thread(new Runnable() {
		
		@Override
		public void run() {
				while(isLive){
					try {
						Thread.sleep(5*1000);
						mHandler.sendEmptyMessage(9);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
		}
	});
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 9:
				if (MyApplication.getInstance().isLocationServerStarted) {
					String str = spu.readData("TRACK_TASKNAME");
					String strId = spu.readData("TRACK_TASKID");
					if (!StringUtils.isNull(strId)) {
						tracks.setText(str);
						stopTracks.setEnabled(true);
					}
				} 
				break;

			default:
				break;
			}
			
		};
	};
	private String msg;
	private Button stopTracks;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		this.mFragmentManager = getChildFragmentManager();
		View layout = inflater.inflate(R.layout.sys_set, container, false);
		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.add("刷新").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 点击使图片能删除
	   if("刷新".equals(item.getTitle())){
			TextView tracks = (TextView) getView().findViewById(
					R.id.sys_set_tracks_content);
			if (MyApplication.getInstance().isLocationServerStarted) {
			 
				String str = spu.readData("TRACK_TASKNAME");
					tracks.setText(str);
					stopTracks.setEnabled(true);
			}else{
				tracks.setText("没有相关轨迹记录服务！");
				stopTracks.setEnabled(false);
				AudioTipsUtils.showMsg(getActivity(), "当前没有轨迹记录服务运行或者服务正在启动过程中!");
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		spu = new SharedPreferencesUtil(getActivity());
		
		MyApplication app = (MyApplication) getActivity().getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getActivity());
			app.mBMapManager.init(MyApplication.getInstance().bdMapKey,
					new MyApplication.MyGeneralListener());
		}
		mMapView = new MapView(getActivity());
		mMapController = mMapView.getController();

		mOffline = new MKOfflineMap();
		/**
		 * 初始化离线地图模块,MapControler可从MapView.getController()获取
		 */
		mOffline.init(mMapController, new MKOfflineMapListener() {

			@Override
			public void onGetOfflineMapState(int type, int state) {
				switch (type) {
				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
					MKOLUpdateElement update = mOffline.getUpdateInfo(state);
					// 处理下载进度更新提示
					if (update != null) {
						// stateView.setText(String.format("%s : %d%%",
						// update.cityName,
						// update.ratio));
					}
				}
					break;
				case MKOfflineMap.TYPE_NEW_OFFLINE:
					// 有新离线地图安装
					Log.d("OfflineDemo",
							String.format("add offlinemap num:%d", state));
					break;
				case MKOfflineMap.TYPE_VER_UPDATE:
					// 版本更新提示
					// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

					break;
				}

			}
		});

		tipsTv = (TextView) getView().findViewById(R.id.tips);

		/**
		 * 当前的轨迹为
		 ***/
		tracks = (TextView) getView().findViewById(
				R.id.sys_set_tracks_content);
		stopTracks = (Button) getView().findViewById(R.id.stopTracks);
		
		String str = spu.readData("TRACK_TASKNAME");
		String strId = spu.readData("TRACK_TASKID");
		if (MyApplication.getInstance().isLocationServerStarted) {
			tracks.setText(str);
			stopTracks.setEnabled(true);
		}else{
			stopTracks.setEnabled(false);
		}

		stopTracks.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AppUtil.isServiceRunning(getActivity()
						.getApplicationContext(),
						"com.scxj.service.LocationService")) {
					spu.writeData("TRACK_TASKNAME", "");
					spu.writeData("TRACK_TASKID", "");
						tracks.setText("没有相关轨迹记录服务！");
						((MainMenuActivity)getActivity()).getOnDataRefreshListener().refreshDatas();
						MyApplication.getInstance().isLocationServerStarted = false;
						AudioTipsUtils.showMsg(getActivity(), "轨迹记录已停止!");
						v.setEnabled(false);
				}
			}
		});

		min = spu.readSpiltTime();// 轨迹间隔时间
		spilt_min = (EditText) getView().findViewById(R.id.spilt_min);
		if (min < 1) {
			min = 5;
		}
		spilt_min.setText(min + "");
		ImageButton time_left = (ImageButton) getView().findViewById(
				R.id.time_left);
		time_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (min > 1) {
					min--;
					spilt_min.setText(min + "");
				}
			}
		});
		ImageButton time_right = (ImageButton) getView().findViewById(
				R.id.time_right);
		time_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (min < 59) {
					min++;
					spilt_min.setText(min + "");
				}
			}
		});

		Button btn = (Button) getView().findViewById(R.id.saveTrackSplitTime);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (min > 0 && min < 60) {
					spu.writeSpliteTime(min + "");
					MyApplication.getInstance().setAlarmManagerTime(min);
					AudioTipsUtils.showMsg(getActivity(), "保存成功，轨迹点间隔" + min
							+ "分钟记录一次");
				} else {
					AudioTipsUtils.showMsg(getActivity(), "请把轨迹点间隔时间设置在0到60之间");
				}
			}
		});

		Button userSignUp = (Button) getView().findViewById(R.id.userSignUp);
		userSignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), LoginActivity.class);
				((MyApplication) getActivity().getApplication()).loginUser = null;
				getActivity().startActivity(intent);
				getActivity().finish();

			}
		});

		Button userInfoUpdate = (Button) getView().findViewById(
				R.id.userInfoUpdate);
		userInfoUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentUserInfo bf = new FragmentUserInfo();
				FragmentTransaction fTransaction = mFragmentManager
						.beginTransaction();
				fTransaction.replace(R.id.sys_set_layout, bf);
				fTransaction.addToBackStack(null);
				fTransaction.commit();
			}
		});

		/**
		 * 离线地图数据检测
		 */
		Button mapDataCheck = (Button) getView()
				.findViewById(R.id.mapDataCheck);
		mapDataCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkMapDatas()) {
					mapDataImport.setEnabled(true);
				} else {
					mapDataImport.setEnabled(false);
				}
			}
		});

		/**
		 * 离线地图数据导入
		 */
		mapDataImport = (Button) getView().findViewById(R.id.mapDataImport);
		mapDataImport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pd = new ProgressDialog(getActivity()).show(getActivity(), "提示", "正在尝试导入离线地图数据包！请稍等……");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							importFromSDCard();
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									pd.cancel();
									pd.dismiss();
									pd = null;
								}
							});
						}
					}
				}).start();
				
			}
		});
		
		/**
		 * 循环线程
		 */
		isLive = true;
		t.start();
	}

	private boolean checkMapDatas() {
		
		 StorageManager sm = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
		 int sdCnt = 0;
		 try {
			String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
			
			sdCnt = paths.length;
			for(int i=0; i < paths.length; i++){
				String status = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, paths[i]);
				if(!status.equals(android.os.Environment.MEDIA_MOUNTED)){
					sdCnt--;//暂时这么处理
				}
			}
			
			String tips = "";
			switch (sdCnt) {
			case 0:
				tips = "注意:未检测到储存卡,需储存卡支持！";
				break;
			case 2:
				tips = "注意:检测到外置储存卡,存储路径为" + paths[1];
				String ext2Path = paths[1] +"/BaiduMapSDK";
				File ext2file = new File(ext2Path);
				if(ext2file.exists()){
					tips = "注意:检测到外置储存卡,存储路径为" + paths[0]+"/BaiduMapSDK地图数据文件夹";
				}else{
					tips = "注意:未检测到外置储存卡,存储路径为" + paths[0]+"/BaiduMapSDK地图数据文件夹";
				}
				
				AudioTipsUtils.showMsg(getActivity(), tips);
				tipsTv.setVisibility(View.VISIBLE);
				tipsTv.setText(tips);
				tipsTv.setTextColor(Color.RED);
				break;	
			case 1:
				tips = "注意:检测到内置储存卡,存储路径为" + paths[0];
				String ext1Path = paths[0] +"/BaiduMapSDK";
				File ext1file = new File(ext1Path);
				if(ext1file.exists()){
					tips = "注意:检测到内置储存卡,存储路径为" + paths[0]+"/BaiduMapSDK地图数据文件夹";
				}else{
					tips = "注意:未检测到内置储存卡,存储路径为" + paths[0]+"/BaiduMapSDK地图数据文件夹";
				}
				AudioTipsUtils.showMsg(getActivity(), tips);
				tipsTv.setVisibility(View.VISIBLE);
				tipsTv.setText(tips);
				tipsTv.setTextColor(Color.RED);
				break;
			default:
				break;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return sdCnt != 0;
	}
	
	
	@Override
	public void onStop() {
		isLive  = false;
		mHandler.removeCallbacks(t);
		super.onStop();
	}

	/**
	 * 从SD卡导入离线地图安装包
	 * 
	 * 从外置SD卡复制到内置SD卡中
	 * 
	 * @param view
	 */
	public void importFromSDCard() {
		 StorageManager sm = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
		 int sdCnt = 0;
		 String[] paths = new String[0];
		 try {
			 paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
			sdCnt = paths.length;
			
			 for(int i=0; i < paths.length; i++){
					String status = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, paths[i]);
					if(!status.equals(android.os.Environment.MEDIA_MOUNTED)){
						sdCnt--;//暂时这么处理
					}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		 
		 boolean ret = false;
		 if(sdCnt == 2){
			 String extPath = paths[0] + "/BaiduMapSDK";
			 String intPath = paths[1] + "/BaiduMapSDK";
			 CopyFileUtil cfu = new CopyFileUtil();
			 ret  = cfu.copyDirectory(extPath, intPath, true);
		 }else if(sdCnt == 1){
			 ret = true;
		 }else{
			 mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					tipsTv.setTextColor(Color.RED);
					tipsTv.setText("未检测到存储卡，此功能需要存储卡支持！");					
				}
			});
			 return;
		 }
		 msg = "";
		if (ret) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int num = mOffline.scan();

			if (num == 0) {
				msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";
			} else {
				msg = String.format("成功导入 %d 个离线包,现在可以在地图展示模块展示地图", num);
			}
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					tipsTv.setTextColor(Color.RED);
					tipsTv.setText(msg);					
				}
			});
			
		} else {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					msg = "文件拷贝出问题！";
					tipsTv.setTextColor(Color.RED);
					tipsTv.setText(msg);
					AudioTipsUtils.showMsg(getActivity(), msg);
				}
			});
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

}
