package com.scxj.fragment;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.dao.TaskDao;
import com.scxj.dao.TaskDefectDao;
import com.scxj.dao.TrackDefectDao;
import com.scxj.dao.TrackTaskDao;
import com.scxj.map.TowerLineLayer;
import com.scxj.map.TowerPointLayer;
import com.scxj.map.TrackLineLayer;
import com.scxj.map.TrackPointLayer;
import com.scxj.model.TB_TOWER;
import com.scxj.model.TB_TRACK;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.MapUtil;

/*
 * 地图底图
 * */
public class FragmentTrackMapMain extends BaseFragment {
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;

	/**
	 * 数据
	 */
	private List<TB_TRACK> tracks;
	private TrackPointLayer tpl;
	private TrackLineLayer tll;

	private List<TB_TOWER> towers;// 杆塔
	private TowerLineLayer towerll;// 杆塔线
	private TowerPointLayer towerpl;// 杆塔点
	private GeoPoint centerPoint = null;
	
	
	private Handler mHandler = new Handler() ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		View layout = inflater
				.inflate(R.layout.frag_map_main, container, false);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		Log.d("FragmentTaskMain", "onActivityCreated");
		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		MyApplication app = (MyApplication) getActivity().getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getActivity());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(MyApplication.bdMapKey,
					new MyApplication.MyGeneralListener());
		}
		/**
		 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
		 */
		mMapView = (MapView) getView().findViewById(R.id.bmapsView);
		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(9);

		/**
		 * 将地图移动至指定点
		 */
		boolean isTaskTrack = getArguments().getBoolean("isTaskTrack");
		String taskName = getArguments().getString("TASKNAME");
		taskName = taskName.substring(0, taskName.lastIndexOf("(共"));
		String taskId = getArguments().getString("TASKID");
		String userName = MyApplication.getInstance().loginUser.getUSERNAME();
		if (isTaskTrack) {
			tracks = new TrackTaskDao(getActivity()).getTrackPointsByTaskName(
					taskName, taskId, userName);
		} else {
			tracks = new TrackDefectDao(getActivity()).getTrackPointsByTaskName(taskName, taskId, userName);
		}
		
		MyApplication.MapShowIndex = 3;
		GeoPoint p = null;
		if (tracks.isEmpty()) {
			AudioTipsUtils.showMsg(getActivity(), "未查询到轨迹数据！");
		} else {
			tpl = new TrackPointLayer(getActivity().getResources().getDrawable(
					R.drawable.icon_gcoding), mMapView, getActivity());
			tll = new TrackLineLayer(mMapView);
			initTrackOverlay();
			
			int cnt = tracks.size();
			tpl.onTap(cnt-1);
			
			centerPoint = MapUtil.getGeoPoint(tracks.get(cnt-1).getLAT(), tracks.get(cnt-1)
					.getLNG());
			mMapController.setZoom(12);
			mMapController.setCenter(centerPoint);
		}
		 
	}

	/**
	 * 多个图层覆盖数据赋值
	 */
	public void resetMapDatas(List<TB_TRACK> tracks) {

		this.tracks = tracks;

	}

	private void initTrackOverlay() {
		mMapView.getOverlays().clear();
		tpl.resetOverlayDatas(tracks);
		tll.resetOverlayDatas(tracks);
		mMapView.getOverlays().add(tpl);
		mMapView.getOverlays().add(tll);
		mMapView.refresh();
		
	}


	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
		
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		mMapView.destroy();
		mMapView = null;
		MyApplication.MapShowIndex = -1;
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

}
