package com.scxj.map;

import java.util.List;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.scxj.model.TB_TOWER;
import com.scxj.model.TB_TRACK;
import com.scxj.utils.MapUtil;
import com.scxj.utils.StringUtils;

/**
 * 杆塔连接线图层
 */
public class TowerLineLayer extends GraphicsOverlay {

	public TowerLineLayer(MapView arg1) {
		super(arg1);
	}

	/**
	 * 把数据转换成图层
	 */
	public void resetOverlayDatas(List<TB_TOWER> datas) {
		if (datas == null) {
			return;
		}
		int pointsCnt = datas.size();
		// 构建线
		Geometry lineGeometry = new Geometry();
		// 设定折线点坐标
		GeoPoint[] linePoints = new GeoPoint[pointsCnt];
		for (int i = 0; i < pointsCnt; i++) {
			TB_TOWER tower = datas.get(i);
			if (!StringUtils.isNull(tower.getTOWERLAT())) {
				GeoPoint p1 = MapUtil.getGeoPoint(tower.getTOWERLAT(),
						tower.getTOWERLNG());
				linePoints[i] = p1;
			}
		}

		lineGeometry.setPolyLine(linePoints);
		// 设定样式
		Symbol lineSymbol = new Symbol();
		Symbol.Color lineColor = lineSymbol.new Color();
		lineColor.red = 0;
		lineColor.green = 0;
		lineColor.blue = 222;
		lineColor.alpha = 255;
		lineSymbol.setLineSymbol(lineColor, 3);
		// 生成Graphic对象
		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);

		setData(lineGraphic);
	}

}
