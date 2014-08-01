package com.scxj.map;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.scxj.MyApplication;
import com.scxj.R;

/**
 * 构建基本的底图
 */
public class BaseMapActivity extends Activity {

	final static String TAG = "BaseMapActivity";
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
	 * 各种图层
	 */
	private List<Overlay> overlays;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        MyApplication app = (MyApplication)getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(MyApplication.bdMapKey,new MyApplication.MyGeneralListener());
  	        }
        /**
          * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
          */
        setContentView(R.layout.activity_task_show);
        mMapView = (MapView)findViewById(R.id.bmapsView);
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
        GeoPoint p ;
        double cLat = 39.945 ;
        double cLon = 116.404 ;
        Intent  intent = getIntent();
        if ( intent.hasExtra("x") && intent.hasExtra("y") ){
        	//当用intent参数时，设置中心点为指定点
        	Bundle b = intent.getExtras();
        	p = new GeoPoint(b.getInt("y"), b.getInt("x"));
        }else{
        	//设置中心点为天安门
        	 p = new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6));
        }
        
        mMapController.setCenter(p);
        
   }
    
    
    
    /**
     * 多个图层覆盖数据赋值
     */
    public void resetMapDatas(List<Overlay> overlays){
    	this.overlays = overlays;
        mMapView.getOverlays().clear();
    	mMapView.getOverlays().addAll(overlays);
    	mMapView.refresh();
    }
    
    
    
    
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    
    /**
     * 重新绘制
     */
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
}
