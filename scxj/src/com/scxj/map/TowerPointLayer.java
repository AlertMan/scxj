package com.scxj.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.scxj.R;
import com.scxj.model.TB_TOWER;
import com.scxj.model.TB_TRACK;
import com.scxj.utils.BMapUtil;
import com.scxj.utils.MapUtil;
import com.scxj.utils.StringUtils;

/**
 * 轨迹点图层
 */
public class TowerPointLayer extends ItemizedOverlay<OverlayItem> {


	private Context mContext;
	private PopupOverlay   pop  = null;
	private ArrayList<OverlayItem>  mItems = null; 
	private TextView  popupText = null;
	private View viewCache = null;
	private View popupInfo = null;
	private OverlayItem mCurItem = null;
 
	
	private TowerPointLayer(Drawable arg0, MapView arg1) {
		super(arg0, arg1);
	}
	
	public TowerPointLayer(Drawable arg0, MapView arg1,Context mContext) {
		super(arg0, arg1);
		this.mContext = mContext;
		 viewCache = ((Activity) mContext).getLayoutInflater().inflate(R.layout.custom_text_view, null);
         popupInfo = (View) viewCache.findViewById(R.id.popinfo);
         popupText =(TextView) viewCache.findViewById(R.id.textcache);
         
         /**
          * 创建一个popupoverlay
          */
         PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
				 Log.d("TAG", index+"");
			}
         };
         pop = new PopupOverlay(mMapView,popListener);
	}
    
  
	/**
	 * 把数据转换成图层
	 */
    public void resetOverlayDatas(List<TB_TOWER> datas){
    	mItems = new ArrayList<OverlayItem>();
    	for(int i=0; datas != null && i< datas.size(); i++){
    		TB_TOWER tower = datas.get(i);
    		if(!StringUtils.isNull(tower.getTOWERLAT())){
	    		GeoPoint p1 = MapUtil.getGeoPoint(tower.getTOWERLAT(), tower.getTOWERLNG());
	    		OverlayItem item = new OverlayItem(p1,tower.getTOWERNAME(),tower.getTOWERID()+"\n"+tower.getTOWERMODEL());
	    		item.setMarker(mContext.getResources().getDrawable(R.drawable.icon_gcoding));
	    		mItems.add(item);
    		}
    	}
    	addItem(mItems);
    }
		
		@Override
		public boolean onTap(int index){
			OverlayItem item = getItem(index);
			mCurItem = item ;
		   popupText.setText(getItem(index).getTitle());
		   Bitmap[] bitMaps={
			    BMapUtil.getBitmapFromView(popupInfo) 		
		    };
		    pop.showPopup(bitMaps,item.getPoint(),32);
			return true;
		}
		
		@Override
		public boolean onTap(GeoPoint pt , MapView mMapView){
			if (pop != null){
                pop.hidePop();
			}
			return false;
		}
    	
    }
    
