package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

import cn.pisoft.base.db.DBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class AttachDefectDao extends DBHelper{
	
	public AttachDefectDao(Context context) {
		super(context);
	}
	
	public List  <TB_ATTACH> getAllList(String userName,String defectTaskId){
		List  <TB_ATTACH> list=new ArrayList<TB_ATTACH>();
		Cursor cursor = null;
		try {
			String dbPath = userName + "/d" + defectTaskId + "/TRNTASKDEFECTRET.DB";
			open(dbPath);
			 String sql = "select * from TB_ATTACH where PARENTID='"+defectTaskId+"' and UPLOADSTATUS<>'已上传' order by CREATETIME desc";
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_ATTACH item = (TB_ATTACH) DBUtil
							.cursorToObject(cursor, TB_ATTACH.class);
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
	public void updateItem(String userName,String defectTaskId,TB_ATTACH item) {
		ContentValues values = DBUtil.objectToCV(item);
	
		try {
			String dbPath = userName + "/d" + defectTaskId + "/TRNTASKDEFECTRET.DB";
			open(dbPath);
			update("TB_ATTACH", values, "ATTACHID = ?",
					new String[] { item.getATTACHID() });
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
	public void insertItem(String userName,String defectTaskId,TB_ATTACH task) {
		ContentValues values = DBUtil.objectToCV(task);
		
		Log.i("insertItem", values.toString());
		String dbPath = userName + "/d" + defectTaskId + "/TRNTASKDEFECTRET.DB";
		try {
			open(dbPath);
			insert("TB_ATTACH", null,values);
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
	public void delItem(String userName,String defectTaskId,String attachId) {
		String dbPath = userName + "/d" + defectTaskId + "/TRNTASKDEFECTRET.DB";
		try {
			open(dbPath);
			delete("TB_ATTACH", "ATTACHID=?", new String[]{attachId});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void updateAttachStatus(String userName, String defectTaskId, String attachId, String status) {
		String dbPath = userName + "/d" + defectTaskId + "/TRNTASKDEFECTRET.DB";
		ContentValues cv = new ContentValues();
		cv.put("UPLOADSTATUS", status);
		
		try {
			open(dbPath);
			 update("TB_ATTACH", cv, "ATTACHID=?", new String[]{attachId});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	
	
	
}
