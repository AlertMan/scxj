package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_TASK_RET;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

public class AttachTaskDao  extends DBHelper{
	
	public AttachTaskDao(Context context) {
		super(context);
	}
	
	/**
	 * 根据任务id查询附件信息
	 * @param taskId
	 * @return
	 */
	public List<TB_ATTACH> getAllList(String userName,String taskId){
		List  <TB_ATTACH> list=new ArrayList<TB_ATTACH>();
		Cursor cursor = null;
		String dbPath = userName + "/t" + taskId + "/TRNTASKRET.DB";
		try {
			open(dbPath);
			String sql = "select * from TB_ATTACH where PARENTID in (select ID from TB_TASK_RET where TASKID='"+taskId+"') and UPLOADSTATUS <> '已上传' order by CREATETIME desc";
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
	 * 根据任务id查询附件信息
	 * @param taskId
	 * @return
	 */
	public List<TB_ATTACH> getAllList(String userName,TB_TASK_RET taskRet){
		List  <TB_ATTACH> list=new ArrayList<TB_ATTACH>();
		Cursor cursor = null;
		String dbPath = userName + "/t" + taskRet.getTASKID() + "/TRNTASKRET.DB";
		try {
			open(dbPath);
			String sql = "select * from TB_ATTACH where PARENTID in (select ID from TB_TASK_RET where TASKID='"+taskRet.getTASKID()+"' and TOWERNAME='"+taskRet.getTOWERNAME()+"') and UPLOADSTATUS <> '已上传' order by CREATETIME desc";
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
	public void updateItem(String userName,String taskId,TB_ATTACH item) {
		ContentValues values = DBUtil.objectToCV(item);
		String dbPath = userName + "/t" + taskId + "/TRNTASKRET.DB";
		try {
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
	public void insertItem(String userName,String taskId,TB_ATTACH attach) {
		ContentValues values = DBUtil.objectToCV(attach);
		
		Log.i("insertItem", values.toString());
		String dbPath = userName + "/t" + taskId + "/TRNTASKRET.DB";
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
	 * 删除N条检测相关的附件
	 * @param item
	 */
	public void delAttachItems(String userName,String taskId,String id) {
		String dbPath = userName + "/t" + taskId + "/TRNTASKRET.DB";
		try {
			open(dbPath);
			delete("TB_ATTACH", "PARENTID=?", new String[]{id});
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
	public void delItem(String userName,String taskId,String attachId) {
		String dbPath = userName + "/t" + taskId + "/TRNTASKRET.DB";
		try {
			open(dbPath);
			delete("TB_ATTACH", "ATTACHID=?", new String[]{attachId});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public void updateAttachStatus(String userName,String taskId,String attachid, String status) {
		ContentValues values = new ContentValues();
		values.put("UPLOADSTATUS", status);
		
		String dbPath = userName + "/t" + taskId + "/TRNTASKRET.DB";
		try {
			open(dbPath);
			update("TB_ATTACH", values, "ATTACHID = ?",
					new String[] { attachid });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	
	
}
