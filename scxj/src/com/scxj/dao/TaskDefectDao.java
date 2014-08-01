package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

public class TaskDefectDao extends DBHelper {

	public TaskDefectDao(Context context) {
		super(context);
	}

	public List<TB_TASK_DEFECT> getAllTaskDefect(int status){
		List <TB_TASK_DEFECT> taskDefectList=new ArrayList<TB_TASK_DEFECT>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKDEFECT);
			String sql = "select * from TB_TASK_DEFECT";
			switch (status) {
			case 1:
				sql += " where STATUS = '已消缺'";
				break;
			case 2:
				sql += " where STATUS <> '已上传'";
				break;
			case 3:
				sql += " where STATUS='已上传'";
				break;
			case 4:
				sql += " where STATUS in ('已上传','已消缺')";
				break;	
			default:
				break;
			}
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TASK_DEFECT item = (TB_TASK_DEFECT) DBUtil
							.cursorToObject(cursor, TB_TASK_DEFECT.class);
					taskDefectList.add(item);
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
		return taskDefectList;
	}
	
	
	/**
	 * 更新一个对象示例
	 * @param inspectionID
	 * @param status
	 */
	public void updateItem(TB_TASK_DEFECT item) {
		ContentValues values = DBUtil.objectToCV(item);
		open(Const.DB_NAME.TRNTASKDEFECT);
	
		try {
			update("TB_TASK_DEFECT", values, "DEFECTTASKID = ?",
					new String[] { item.getDEFECTTASKID() });
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
	public void insertItem(TB_TASK_DEFECT task) {
		ContentValues values = DBUtil.objectToCV(task);
		
		Log.i("insertItem", values.toString());
		open(Const.DB_NAME.TRNTASKDEFECT);
	
		try {
			insert("TB_TASK_DEFECT", null,values);
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
	public void delItem(TB_TASK_DEFECT item) {
		open(Const.DB_NAME.TRNTASKDEFECT);
		try {
			delete("TB_TASK_DEFECT", "DEFECTTASKID=?", new String[]{item.getDEFECTTASKID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void updateStatus(String taskDefectId, String status) {
		ContentValues values = new ContentValues();
		values.put("STATUS", status);
		open(Const.DB_NAME.TRNTASKDEFECT);
	
		try {
			update("TB_TASK_DEFECT", values, "DEFECTTASKID = ?",
					new String[] { taskDefectId });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * 是否全部都已经上传
	 * @return
	 */
	public boolean doAll() {
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKDEFECT);
			String sql = "select count(*) as cnt from TB_TASK_DEFECT where STATUS <> '已上传'";
			cursor = rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				 return cursor.getInt(0) == 0;
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

	public boolean isEmptyTask(String dbPath) {
		Cursor cursor = null;
		try {
			open(dbPath);
			String sql = "select count(*) from TB_TASK_DEFECT ";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					   int cnt = cursor.getInt(0);
					   return cnt == 0;
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
		return false;
	}
	
	
	
	
	
}
