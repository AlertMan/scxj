package com.scxj.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.model.TB_TOWER;

public class MapUtil {

	private List<OverlayItem> geoList;
	private List<List<List<GeoPoint>>> allLineList;
	private List<String> linesNameList;
	public List<TB_TOWER> allDeviceLists;
//	public DevicePointLayer pointLayer;
//	public static List<MapDevice> mapDevices;
	public static int winWidth ;
	public static int winHeight ;
	public static GeoPoint minXY = null , maxXY = null;

	/**
	 * 转换为高德使用的geoPoint
	 * 
	 * @param mLat
	 * @param mLng
	 * @return
	 */
	public static GeoPoint getGeoPoint(double mLat, double mLng) {
		
		GeoPoint geoPoint = new GeoPoint((int) (mLat * 1E6), (int) (mLng * 1E6));
	 
		return geoPoint;
	}
	
	/**
	 * 转换为高德使用的geoPoint
	 * 
	 * @param mLat
	 * @param mLng
	 * @return
	 */
	public static GeoPoint getGeoPoint(long mLat, long mLng) {
		
		GeoPoint geoPoint = new GeoPoint((int) (mLat ), (int) (mLng ));
		return geoPoint;
	}
	
	/**
	 * 转换为高德使用的geoPoint
	 * 
	 * @param mLat
	 * @param mLng
	 * @return
	 */
	public static GeoPoint getGeoPoint(int mLat, int mLng) {
		
		GeoPoint geoPoint = new GeoPoint((mLat ), (mLng ));
		return geoPoint;
	}
	
	
	/**
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static GeoPoint getGeoPoint(String lat, String lng) {
		
		double latX = 0;
		double lngY = 0;
		try {
			latX = Double.valueOf(lat);
			lngY = Double.valueOf(lng);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			latX = 0;
			lngY = 0;
			Log.e("getGeoPoint", "lat="+lat+"|lng="+lng);
		}
		
		return getGeoPoint(latX,lngY);
	 
	}

	/**
	 * 根据查询规则返回相应的显示图片
	 * 
	 * @param device
	 * @param where
	 * @return
	 */
	public static String getImageName(TB_TOWER device, String where) {
		String imageName = null;
		// String str =
		// "(VOLTAGELEVEL LIKE '%0.4kV%' AND (ASSETOWNER = '用户' OR ASSETOWNER  = '专变'))  ";
		String[] andArr = where.split("AND");// 分割and
		for (String andStr : andArr) {
			String[] orArr = andStr.split("OR");

		}

		return imageName;
	}

	// 缓存图片名字类
	private static HashMap<String, String> imageNameMap = new HashMap<String, String>();

	/**
	 * 设置点图层中的图标
	 * 
	 * @param context
	 * @return
	 */
	public static Drawable getPointMarker(Context context, TB_TOWER device) {
		MyApplication tapp = MyApplication.getInstance();
		Drawable marker = tapp.imageCache.get(device.getTOWERID());
		if (null == marker) {
			marker = context.getResources().getDrawable(R.drawable.diangan);
			tapp.imageCache.put(device.getTOWERID(), marker);
		}  
		// marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
		// .getIntrinsicHeight());
		// marker.setBounds(100,100,100,100);
		return marker;
	}


	/**
	 * 线中的coord坐标转换为偏转后的高德坐标
	 * 
	 * @param towerList
	 * @return
	 */
	public static List<List<GeoPoint>> towerToGeoPoint(
			List<List<TB_TOWER>> towerList) {
		List<List<GeoPoint>> lineGeoList = new ArrayList<List<GeoPoint>>();
//		for (List<Coord> pointList : coordList) {
		for(int x = 0;x<towerList.size();x++){
			List<TB_TOWER> pointList = towerList.get(x);
			List<GeoPoint> geoPointList = new ArrayList<GeoPoint>();
			for (int i = 0; i < pointList.size() - 1; i++) {
				TB_TOWER tower = pointList.get(i); 
				GeoPoint geoPoint = MapUtil.getGeoPoint(
						tower.getTOWERLAT(), tower.getTOWERLNG());
				geoPointList.add(geoPoint);
			}
			lineGeoList.add(geoPointList);
		}
		return lineGeoList;
	}

	/**
	 * 根据数据不同构成不同的线
	 * 
	 * @param lineGeoPointListl
	 */
	private List<GeoPoint> allPoints = new ArrayList<GeoPoint>();
	private List<List<GeoPoint>> geoPointsList;


	public static ProgressDialog progress;

	public static ProgressDialog showLoadMapData(Context context) {
		progress = ProgressDialog.show(context, "提示", "正在加载地图数据,请稍等！");
		progress.setCancelable(false);
		return progress;
	}


	public static OverlayItem getOverlayItem(Context context, TB_TOWER device,
			MapView mapView) {
		try {
			String towerName = device.getTOWERNAME();
			GeoPoint geoPoint = getGeoPoint(device.getTOWERLAT(), device.getTOWERLNG());
			OverlayItem overlayItems = new OverlayItem(geoPoint, device.getTOWERID(),towerName);
			 //System.out.println("voltagelevel"+device.voltagelevel+":type"+device.devicetype+":typename"+device.devicename);
			Drawable marker = MapUtil.getPointMarker(context, device);
			marker.setBounds(0 - marker.getIntrinsicWidth() / 2,
					0 - marker.getIntrinsicHeight() / 2,
					marker.getIntrinsicWidth() / 2,
					marker.getIntrinsicHeight() / 2);
			// ItemizedOverlay.boundCenterBottom(marker);
			overlayItems.setMarker(marker);
			if(mapView == null){
				return null;
			}
//			int zoom = mapView.getZoomLevel();
//			String deviceType = device.getTOWERMATERIAL();
			
			return overlayItems;
 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	 
}
