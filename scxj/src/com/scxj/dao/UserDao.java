package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

import com.scxj.model.TB_USER;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

public class UserDao extends DBHelper {
	
	public UserDao(Context context) {
		super(context);
	}

	/**
	 * 更新一个对象示例
	 * @param inspectionID
	 * @param status
	 */
	public void updateItem(TB_USER item) {
		ContentValues values = DBUtil.objectToCV(item);
		Log.d("updateItem", values.toString());
		open(Const.DB_NAME.TRNCOMMON);
		try {
			update("TB_USER", values, "USERID = ?",
					new String[] { item.getUSERID() });
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
	public void insertItem(TB_USER user) {
		ContentValues values = DBUtil.objectToCV(user);
		open(Const.DB_NAME.TRNCOMMON);
	
		try {
			insert("TB_USER", null,values);
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
	public void delItem(TB_USER item) {
		ContentValues values = DBUtil.objectToCV(item);
		open(Const.DB_NAME.TRNCOMMON);
	
		try {
			delete("TB_USER", "USERID=?", new String[]{item.getUSERID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public TB_USER getUSER(String userName, String password) {
		TB_USER item  = null;
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNCOMMON);
			String sql = "select * from TB_USER where USERNAME='"+userName+"' or REALNAME='"+userName+"'";
			 Log.d("getUSER",sql);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					item = (TB_USER) DBUtil
							.cursorToObject(cursor, TB_USER.class);

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

	public TB_USER getUserByRfidCode(String epcCode) {
		TB_USER item  = null;
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNCOMMON);
			String sql = "select * from TB_USER where RFIDCODE='"+epcCode+"'";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					item = (TB_USER) DBUtil
							.cursorToObject(cursor, TB_USER.class);

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

	public List <TB_USER> getJHList() {
		List <TB_USER> jhList=new ArrayList<TB_USER>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNCOMMON);
			String sql = "select * from TB_USER GROUP BY GROUPNAME";
			 Log.d("getUSER",sql);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_USER item = (TB_USER) DBUtil
							.cursorToObject(cursor, TB_USER.class);
					jhList.add(item);
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
		return jhList;
	}

	/**
	 * 机号不用了
	 * @param jh
	 * @return
	 */
	public List<TB_USER> getBZListByJh(String jh) {
		List <TB_USER> bzList=new ArrayList<TB_USER>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNCOMMON);
		//	String sql = "select * from TB_USER where GROUPNAME='"+jh+"' GROUP BY CLASSNAME";
			String sql = "select * from TB_USER GROUP BY CLASSNAME";
			 Log.d("getBZList",sql);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_USER item = (TB_USER) DBUtil
							.cursorToObject(cursor, TB_USER.class);
					bzList.add(item);
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
		return bzList;
	}

	public List<TB_USER> getUserListByBz(String bz) {
		List <TB_USER> userList=new ArrayList<TB_USER>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNCOMMON);
			String sql = "select * from TB_USER where CLASSNAME='"+bz+"'";
			 Log.d("getUserList",sql);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_USER item = (TB_USER) DBUtil
							.cursorToObject(cursor, TB_USER.class);
					userList.add(item);
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
		return userList;
	}

	 
}

