package com.scxj.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

public class RetDefectDao extends DBHelper {

	public RetDefectDao(Context context) {
		super(context);
	}

	public List<TB_TASK_DEFECT> getAllList(String userName){
		List <TB_TASK_DEFECT> taskDefectList=new ArrayList<TB_TASK_DEFECT>();
		Cursor cursor = null;
		try {
			List<TB_TASK_DEFECT> taskList = new ArrayList<TB_TASK_DEFECT>();
			open(Const.DB_NAME.TRNTASKDEFECT);
			String tasksql = "select * from TB_TASK_DEFECT";
			cursor = rawQuery(tasksql,null);
			if(cursor != null ){
				while(cursor.moveToNext()){
					TB_TASK_DEFECT taskItem = (TB_TASK_DEFECT)DBUtil.cursorToObject(cursor, TB_TASK_DEFECT.class);
					taskList.add(taskItem);
				}
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			close();
			
			for(TB_TASK_DEFECT taskDefect : taskList){
				String dbPath = userName + "/d" + taskDefect.getDEFECTTASKID()+"/TRNTASKDEFECTRET.DB";
				File dbFile = new File(DB_PATH+"/"+dbPath);
				if(!dbFile.exists()){
					continue;
				}
				open(dbPath);
				String sql = "select * from TB_TASK_DEFECT group by TOWERID order by DEFECTTASKID,TOWERID";
				cursor = rawQuery(sql, null);
				if (cursor != null) {
					while (cursor.moveToNext()) {
						TB_TASK_DEFECT item = (TB_TASK_DEFECT) DBUtil
								.cursorToObject(cursor, TB_TASK_DEFECT.class);
						taskDefectList.add(item);
					}
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
		open(Const.DB_NAME.TRNTASKDEFECTRET);
	
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
		open(Const.DB_NAME.TRNTASKDEFECTRET);
	
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
		ContentValues values = DBUtil.objectToCV(item);
		open(Const.DB_NAME.TRNTASKDEFECTRET);
	
		try {
			delete("TB_TASK_DEFECT", "ID=?", new String[]{item.getDEFECTTASKID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public TB_TASK_DEFECT getTaskRetByTaskId(String parentid) {
		TB_TASK_DEFECT  item = null;
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKDEFECTRET);
			String sql = "select * from TB_TASK_DEFECT where DEFECTTASKID='"+parentid+"'";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToNext()) {
					  item = (TB_TASK_DEFECT) DBUtil
							.cursorToObject(cursor, TB_TASK_DEFECT.class);
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
	
	
	
	
	
}
