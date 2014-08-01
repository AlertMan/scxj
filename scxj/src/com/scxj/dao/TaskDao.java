package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TASK_RET;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;

public class TaskDao extends DBHelper {
	private Context mContext;

	public TaskDao(Context context) {
		super(context);
		mContext = context;
	}
	/**
	 * 
	 * @param status
	 * @return
	 */
	public List<TB_TASK> getAllList(int status) {
		List<TB_TASK> list = new ArrayList<TB_TASK>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASK);
			String sql = "select * from TB_TASK";
			switch (status) {
			case 1:
				sql += " where STATUS = '已巡视'";
				break;
			case 2:
				sql += " where STATUS <> '已上传'";
				break;
				
			case 3:
				sql += " where STATUS='已上传'";
				break;
			case 4:
				sql += " where STATUS in ('已上传','已巡视')";
				break;	
			default:
				break;
			}
			
			Log.w("WAR", sql);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TASK item = (TB_TASK) DBUtil
							.cursorToObject(cursor, TB_TASK.class);
					TB_LINE line = getLineByTaskId(item.getTASKID());
					item.setLine(line);
					if(!"已上传".equals(item.getSTATUS())){
						String taskStatus = getTaskStatus(item.getTASKID(),line.getLINEID());
						item.setSTATUS(taskStatus);
					}
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
	 * 根据杆塔 的状态 判断任务状态
	 * @param item
	 * @return
	 */
	public String getTaskStatus(String taskId,String lineId) {
		
		String totalSql = 	"select count(*) as cnt from TB_TOWER where TASKID='"+taskId+"' and LINEID='" +lineId +"' and (status is null or status <> '已上传')";
		String unCompleteSql = "select count(*) as cnt from TB_TOWER where TASKID='"+taskId+"' and  LINEID='" +lineId +"' and (status is null or status not in('已巡视','已上传'))";
		String completeSql = "select count(*) as cnt from TB_TOWER where TASKID='"+taskId+"' and  LINEID='" +lineId +"' and status in ('已巡视','巡视中')";
		int totalCnt = 0,unComCnt = 0,comCnt = 0;
		
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASK);
			cursor = rawQuery(totalSql, null);
			if (cursor != null && cursor.moveToFirst()) {
				totalCnt = cursor.getInt(cursor.getColumnIndex("cnt"));
				cursor.close();
				cursor = null;
			}
			
			cursor = rawQuery(unCompleteSql, null);
			if (cursor != null && cursor.moveToFirst()) {
				unComCnt = cursor.getInt(cursor.getColumnIndex("cnt"));
				cursor.close();
				cursor = null;
			}
			
			cursor = rawQuery(completeSql, null);
			if (cursor != null && cursor.moveToFirst()) {
				comCnt = cursor.getInt(cursor.getColumnIndex("cnt"));
				cursor.close();
				cursor = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			close();
		}
		
		if(totalCnt != 0 && comCnt == 0 && unComCnt == totalCnt){
			return "未巡视";
		}
		int expCnt = new RetTaskDao(mContext).cntTowerCheckDetailByTaskId(taskId);
		if(totalCnt != 0 && totalCnt == comCnt && unComCnt == 0){
			updateStatus(taskId, "已巡视");
			return "已巡视\n发现["+expCnt+"]异常项";
		}
		if(comCnt > 0){
			return "巡视中\n发现["+expCnt+"]异常项";
		}
		
		
		return "未巡视";
	}
	public void updateStatus(String taskId,String status) {
		ContentValues values = new ContentValues();
		values.put("STATUS", status);
		open(Const.DB_NAME.TRNTASK);
	
		try {
			update("TB_TASK", values, "TASKID = ?",
					new String[] { taskId });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	/**
	 * 更新一个对象示例
	 * @param inspectionID
	 * @param status
	 */
	public void updateItem(TB_TASK item) {
		ContentValues values = DBUtil.objectToCV(item);
		open(Const.DB_NAME.TRNTASK);
	
		try {
			update("TB_TASK", values, "ID = ?",
					new String[] { item.getTASKID() });
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
	public void insertItem(TB_TASK task) {
		ContentValues values = DBUtil.objectToCV(task);
		
		Log.i("insertItem", values.toString());
		open(Const.DB_NAME.TRNTASK);
	
		try {
			insert("TB_TASK", null,values);
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
	public void delItem(TB_TASK item) {
		open(Const.DB_NAME.TRNTASK);
	
		try {
			delete("TB_TASK", "TASKID=?", new String[]{item.getTASKID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	
	public TB_LINE getLineByTaskId(String taskid) {
		TB_LINE  item = new  TB_LINE();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASK);
			String sql = "select * from TB_LINE where TASKID='" + taskid +"'";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					  item = (TB_LINE) DBUtil
							.cursorToObject(cursor, TB_LINE.class);
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
	 * 项目初始库文件为空
	 * @param status
	 * @return
	 */
	public boolean isEmptyTask(String dbPath) {
		Cursor cursor = null;
		try {
			open(dbPath);
			String sql = "select count(*) from TB_TASK";
			 
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

	public TB_TASK getFirstPotintByTrackId(String trackId) {
		TB_TASK  item = new  TB_TASK();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKRET);
			String sql = "select * from TB_TASK where TRACKID='"+trackId+"'limit 1";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					  item = (TB_TASK) DBUtil
							.cursorToObject(cursor, TB_TASK.class);
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
	 * 取杆塔列表数据
	 * @param lineId
	 * @return
	 */
	public List<TB_TOWER> getTowersListByTaskId(String taskId) {
		List<TB_TOWER> list = new ArrayList<TB_TOWER>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASK);
			String sql = "select * from TB_TOWER  where TASKID='"+taskId+"'  order by SEQ";
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
	 * 取杆塔列表数据,注意这个方法里面　添加了巡检详情字段　
	 * @param lineId
	 * @param string 
	 * @return
	 */
	public List<TB_TOWER> getTowersListByLineId(String taskId, String lineId) {
		List<TB_TOWER> list = new ArrayList<TB_TOWER>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASK);
			String sql = "select * from TB_TOWER  where  TASKID='"+taskId+"' and LINEID = '"+lineId+"' order by SEQ";
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TOWER item = (TB_TOWER) DBUtil
							.cursorToObject(cursor, TB_TOWER.class);
					RetTaskDao retTaskDao = new RetTaskDao(mContext);
					int cnt0 = retTaskDao.cntTowerCheckDetail(taskId, lineId, item.getTOWERID(), 0);
					int cnt1 = retTaskDao.cntTowerCheckDetail(taskId, lineId, item.getTOWERID(), 1);
					int cnt2 = retTaskDao.cntTowerCheckDetail(taskId, lineId, item.getTOWERID(), 2);
					if(cnt0 == 0 && cnt1== 0 && cnt2 == 0){
						cnt0 = 13;
					}
					String detail = "未巡项["+cnt0+"]正常项["+cnt1+"]异常项["+cnt2+"]";
					item.setCHECKDETAIL(detail);
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
	 * 更新杆塔巡视结果状态
	 */
	public void updateTowerStatus(String towerId, String status) {
		ContentValues values = new ContentValues();
		values.put("STATUS", status);
		open(Const.DB_NAME.TRNTASK);
		try {
			update("TB_TOWER", values, "TOWERID = ?",
					new String[] { towerId });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	public boolean doAll() {
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASK);
			String sql = "select count(*) as cnt from TB_TASK where STATUS <>'已上传'";
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
	
	public TB_TOWER getTowerByTowerId(String towerId) {
		TB_TOWER tower = new TB_TOWER();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASK);
			String sql = "select * from TB_TOWER  where  TOWERID='"+towerId+"'";
			cursor = rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
					tower = (TB_TOWER) DBUtil
							.cursorToObject(cursor, TB_TOWER.class);
				}
 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			close();
		}
		return tower;
	}
	public void delTaskLinesByTaskId(String taskid) {
		open(Const.DB_NAME.TRNTASK);
	
		try {
			delete("TB_LINE", "TASKID=?", new String[]{taskid});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public void delTaskTowersByTaskId(String taskid) {
		open(Const.DB_NAME.TRNTASK);
	
		try {
			delete("TB_TOWER", "TASKID=?", new String[]{taskid});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	/**
	 * 插入线路 
	 * @param line
	 */
	public void insertLineItem(TB_LINE line) {
		ContentValues values = DBUtil.objectToCV(line);
		
		Log.i("insertItem", values.toString());
		open(Const.DB_NAME.TRNTASK);
	
		try {
			insert("TB_LINE", null,values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	
	/**
	 * 插入杆塔 
	 * @param tower
	 */
	public void insertTowerItem(TB_TOWER tower) {
		ContentValues values = DBUtil.objectToCV(tower);
		
		Log.i("insertItem", values.toString());
		open(Const.DB_NAME.TRNTASK);
	
		try {
			insert("TB_TOWER", null,values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
}
