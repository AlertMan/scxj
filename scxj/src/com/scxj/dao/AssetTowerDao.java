package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

import com.scxj.model.TB_TOWER;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

public class AssetTowerDao extends DBHelper{
	
	public AssetTowerDao(Context context) {
		super(context);
	}
	
	/**
	 * 根据任务id查询附件信息
	 * @param taskId
	 * @return
	 */
	public List  <TB_TOWER> getAllList(String lineId){
		List  <TB_TOWER> list=new ArrayList<TB_TOWER>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNASSET);
			String sql = "select * from TB_TOWER where LINEID='" + lineId +"'";
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TOWER item = (TB_TOWER) DBUtil
							.cursorToObject(cursor, TB_TOWER.class);
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
	
	/**
	 * 更新一个对象示例
	 * @param inspectionID
	 * @param status
	 */
	public void updateItem(TB_TOWER item) {
		ContentValues values = DBUtil.objectToCV(item);
		open(Const.DB_NAME.TRNASSET);
	
		try {
			update("TB_TOWER", values, "TOWERID = ?",
					new String[] { item.getTOWERID() });
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
	public void insertItem(TB_TOWER line) {
		ContentValues values = DBUtil.objectToCV(line);
		
		Log.i("insertItem", values.toString());
		open(Const.DB_NAME.TRNASSET);
	
		try {
			insert("TB_TOWER", null,values);
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
	public void delItem(TB_TOWER item) {
		open(Const.DB_NAME.TRNTASKRET);
		try {
			delete("TB_TOWER", "TOWERID=?", new String[]{item.getTOWERID()});
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
			delete("TB_TOWER", "TOWERID=?", new String[]{taskId});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	
}
