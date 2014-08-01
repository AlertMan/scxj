package com.scxj.fragment;

import java.util.List;

import android.os.Bundle;
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
import com.scxj.dao.AssetTowerDao;
import com.scxj.map.TowerLineLayer;
import com.scxj.map.TowerPointLayer;
import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.MapUtil;
/*
 * 任务轨迹查看
 * */
public class FragmentLineMapMain extends BaseFragment {
	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	/**
	 *  MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;
	
	/**
	 * 数据
	 */
	private TB_LINE line;
	private List<TB_TOWER> towers;
	private TowerPointLayer tpl;
	private TowerLineLayer tll;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) 
		{
            return null;
        }
	    View layout = inflater.inflate(R.layout.frag_asset_map_main, container, false); 
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		Log.d("FragmentTaskMain", "onActivityCreated");
		  /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        MyApplication app = (MyApplication)getActivity().getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getActivity());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(MyApplication.bdMapKey,new MyApplication.MyGeneralListener());
  	        }
        /**
          * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
          */
        mMapView = (MapView)getView().findViewById(R.id.assetBmapsView);
        /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(12);
       
        /**
         * 将地图移动至指定点
         */
        line = (TB_LINE)getArguments().getSerializable("item");
		towers = new AssetTowerDao(getActivity()).getAllList(line.getLINEID());
		 
		MyApplication.MapShowIndex = 2;
		if(towers.isEmpty()){
				AudioTipsUtils.showMsg(getActivity(), "未查询到杆塔数据！");
			  mMapController.setCenter(new GeoPoint((int)( 39.945 * 1E6), (int)(116.404 * 1E6)));
		}else{
			
	    	tpl = new TowerPointLayer(getActivity().getResources().getDrawable(R.drawable.icon_gcoding), mMapView, getActivity());
	    	tll = new TowerLineLayer(mMapView);
	        initOverlay();
	        
	        GeoPoint p  = MapUtil.getGeoPoint(towers.get(0).getTOWERLAT(),towers.get(0).getTOWERLNG());
	        mMapController.setCenter(p);
		}
	}
	
    
    private void initOverlay(){
    	
    	tpl.resetOverlayDatas(towers);
    	tll.resetOverlayDatas(towers);
    	mMapView.getOverlays().clear();
      	mMapView.getOverlays().add(tpl);
      	mMapView.getOverlays().add(tll);
      	mMapView.refresh();
      	
    }
	 
	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
		Log.d("FragmentLineMapMain", "onResume");
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		mMapView.destroy();
		mMapView = null;
		MyApplication.MapShowIndex = -1;
		super.onDestroyView();
		Log.d("FragmentLineMapMain", "onDestroyView");
	}
	 
	
	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}
	  
}
