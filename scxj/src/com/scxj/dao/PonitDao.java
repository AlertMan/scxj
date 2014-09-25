package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import com.scxj.model.TB_POINT;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

/**
 * 巡视点绑卡
 * @author Peter
 *
 */
public class PonitDao extends DBHelper {

	public PonitDao(Context context) {
		super(context);
	}
	
	public TB_POINT getPointById(String id) {
		TB_POINT item =null;
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNPOINT);
			String sql = "select * from TB_POINT where POINTID='"+id+"'";
			System.out.println("=====sql====="+sql);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					item= (TB_POINT) DBUtil
							.cursorToObject(cursor, TB_POINT.class);

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
	
	
	public TB_POINT getSiteByTagId(String epcCode) {
		TB_POINT item =null;
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNPOINT);
			String sql = "select * from TB_POINT where EPCCODE='"+epcCode+"'";
			System.out.println("=====sql====="+sql);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					item= (TB_POINT) DBUtil
							.cursorToObject(cursor, TB_POINT.class);

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
	
	public List<TB_POINT> getAllPoint() {
		List<TB_POINT> list = new ArrayList<TB_POINT>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNPOINT);
			String sql = "select * from TB_POINT ";
			System.out.println("=====sql====="+sql);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_POINT item = (TB_POINT) DBUtil
							.cursorToObject(cursor, TB_POINT.class);

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
	public void updateItem(TB_POINT item) {
		ContentValues values = DBUtil.objectToCV(item);
		open(Const.DB_NAME.TRNPOINT);
		
		try {
			update("TB_POINT", values, "POINTID = ?",
					new String[] { item.getPOINTID() });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * 更新一个对象示例,ID一般自己生成，对于插入速度特别快的记录，ID必须由SQLITE自己管理，如：自增
	 * @param item
	 */
	public void insertItem(TB_POINT item) {
		ContentValues values = DBUtil.objectToCV(item);

		Log.i("insertItem", values.toString());
		open(Const.DB_NAME.TRNPOINT);

		try {
			insert("TB_POINT", null,values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * 删除一条记录
	 * @param inspectionID
	 * @param status
	 */
	public void delItem(TB_POINT item) {
		open(Const.DB_NAME.TRNPOINT);

		try {
			delete("TB_POINT", "POINTID=?", new String[]{item.getPOINTID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public boolean isPonitAllDone(String taskid) {
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNPOINT);
			String sql = "select count(*) as cnt from TB_POINT where TASKID='"+taskid+"' and STATUS <> '已刷卡'";
			cursor = rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				int cnt = cursor.getInt(cursor.getColumnIndex("cnt"));
				return cnt == 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			close();
		}
		return false;
	}
	
/*	public boolean bindingCard(TB_POINT site){
		String sql = "UPDATE CONFIG_SITE SET RFID_CODE = ? , UPDATED = ? WHERE CONFIG_SITE_ID = ? AND PATROLSITE_CODE = ?";
		this.getDatabase(DBConst.DB_PATROL_BINDING).execSQL(sql, new Object[]{site.getRfidCode(),"1",site.getId(),site.getPatrolsiteCode()});
		Log.d(TAG, sql);
		this.closeDatabase();
		return true;
	}*/
	
	public void bindingCard(TB_POINT item) {
		ContentValues values = new ContentValues();
		
		values.put("EPCCODE", item.getEPCCODE());
		values.put("STATUS", "未上传");
		open(Const.DB_NAME.TRNPOINT);
		Log.d("bindingCard", values + "POINTID=" + item.getPOINTID());
		try {
			update("TB_POINT", values, "POINTID = ?",
					new String[] { item.getPOINTID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	public List<TB_POINT> getAllUpdatePoint() {
		List<TB_POINT> list = new ArrayList<TB_POINT>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNPOINT);
			String sql = "select * from TB_POINT　where STATUS='未上传' ";
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_POINT item = (TB_POINT) DBUtil
							.cursorToObject(cursor, TB_POINT.class);

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
}
