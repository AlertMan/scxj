package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

public class AssetLineDao extends DBHelper{
	
	public AssetLineDao(Context context) {
		super(context);
	}
	
	/**
	 * 根据任务id查询附件信息
	 * @param taskId
	 * @return
	 */
	public List  <TB_LINE> getAllList(){
		List  <TB_LINE> list=new ArrayList<TB_LINE>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNASSET);
			String sql = "select * from TB_LINE";
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_LINE item = (TB_LINE) DBUtil
							.cursorToObject(cursor, TB_LINE.class);
					TB_TOWER tower = getTowerByLineId(item.getLINEID());
					item.setTower(tower);
					list.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			close();
		}
		
		return list;
		
	}
	
	public TB_TOWER getTowerByLineId(String lineid) {
		TB_TOWER  item = new  TB_TOWER();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNASSET);
			String sql = "select * from TB_LINE where LINEID='" + lineid +"'";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					  item = (TB_TOWER) DBUtil
							.cursorToObject(cursor, TB_TOWER.class);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			close();
		}
		return item;
	}

	/**
	 * 更新一个对象示例
	 * @param inspectionID
	 * @param status
	 */
	public void updateItem(TB_LINE item) {
		ContentValues values = DBUtil.objectToCV(item);
		open(Const.DB_NAME.TRNASSET);
	
		try {
			update("TB_LINE", values, "LINEID = ?",
					new String[] { item.getLINEID() });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	/**
	 * 更新一个对象示例,ID一般自己生成，对于插入速度特别快的记录，ID必须由SQLITE自己管理，如：自增
	 * @param inspectionID
	 * @param status
	 */
	public void insertItem(TB_LINE line) {
		ContentValues values = DBUtil.objectToCV(line);
		
		Log.i("insertItem", values.toString());
		open(Const.DB_NAME.TRNASSET);
	
		try {
			insert("TB_LINE", null,values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	/**
	 * 删除一条记录
	 * @param item
	 */
	public void delItem(TB_LINE item) {
		open(Const.DB_NAME.TRNTASKRET);
		try {
			delete("TB_LINE", "LINEID=?", new String[]{item.getLINEID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	
	/**
	 * 删除N条检测相关的附件
	 * @param item
	 */
	public void delAttachItems(String taskId) {
		open(Const.DB_NAME.TRNASSET);
		try {
			delete("TB_ATTACH", "LINEID=?", new String[]{taskId});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
}
