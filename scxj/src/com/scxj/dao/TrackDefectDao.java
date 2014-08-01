package com.scxj.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_TRACK;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;
import com.scxj.utils.StringUtils;

public class TrackDefectDao extends DBHelper {
	public TrackDefectDao(Context context) {
		super(context);
	}

	/**
	 * 通过TRNTASK.db库里面依次查找 
	 * 1、查询出所有的任务
	 * 2、找出每个任务文件夹下面的轨迹数据
	 * @param status
	 * @return
	 */
	public List<TB_TRACK> getAllTrackListByUserId(String userName) {
		List<TB_TRACK> list = new ArrayList<TB_TRACK>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKDEFECT);
			String taskSql = "select * from TB_TASK_DEFECT";
			cursor = rawQuery(taskSql, null);
			List<TB_TASK_DEFECT> taskList  = new ArrayList<TB_TASK_DEFECT>();
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TASK_DEFECT item = (TB_TASK_DEFECT) DBUtil
							.cursorToObject(cursor, TB_TASK_DEFECT.class);
					taskList.add(item);
				}
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
				close();
			}
			
			
			for(TB_TASK_DEFECT task : taskList){
				String dbPath = userName + "/d" + task.getDEFECTTASKID() + "/TRNTASKDEFECTRET.DB";
				File dbFile = new File(DB_PATH+"/"+dbPath);
				if(!dbFile.exists()){
					continue;
				}
				open(dbPath);
				String sql = "select * from TB_TRACK group by TASKID";
				cursor = rawQuery(sql, null);
				if (cursor != null) {
					while (cursor.moveToNext()) {
						TB_TRACK item = (TB_TRACK) DBUtil
								.cursorToObject(cursor, TB_TRACK.class);
						int totalCnt = getAllCnt(item.getTASKNAME(),item.getTASKID(),userName);
						
						TB_TRACK firstItem = getFirstItem(item.getTASKNAME(),item.getTASKID(),userName);
						TB_TRACK lastItem = getLastItem(item.getTASKNAME(),item.getTASKID(),userName);
						String firstDesc = "("+firstItem.getLAT()+","+firstItem.getLNG()+")\n";
						if(!StringUtils.isNull(lastItem.getSTARTADDRESS())){
							firstDesc += firstItem.getSTARTADDRESS()+"\n";
						}
						firstDesc += firstItem.getCREATETIME();
						
						String lastDesc = "("+lastItem.getLAT()+","+lastItem.getLNG()+")\n";
						if(!StringUtils.isNull(lastItem.getENDADDRESS())){
							lastDesc += lastItem.getENDADDRESS()+"\n";
						}
						lastDesc += lastItem.getCREATETIME();
						
						item.setSTARTADDRESS(firstDesc);
						item.setENDADDRESS(lastDesc);
						String trackName = item.getTASKNAME() + "(共"+totalCnt+"个点)";
						item.setTASKNAME(trackName);
						list.add(item);
					}
					cursor.close();
					cursor = null;
					close();
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

	
	private int getAllCnt(String taskname, String taskId, String userName) {
		Cursor cursor = null;
		try {
			String dbPath = userName + "/d" + taskId + "/TRNTASKDEFECTRET.DB";
			open(dbPath);
			String sql = "select count(*) as cnt from TB_TRACK where TASKID='"+taskId+"' and TASKNAME='"+taskname+"'";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToNext()) {
					return cursor.getInt(0);
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
		return 0;
	}

	private TB_TRACK getLastItem(String taskname,String taskId,String userId) {
		TB_TRACK  item = new  TB_TRACK();
		Cursor cursor = null;
		try {
			String dbPath = userId + "/d" + taskId + "/TRNTASKDEFECTRET.DB";
			open(dbPath);
			String sql = "select * from TB_TRACK where TASKID='"+taskId+"' and TASKNAME='"+taskname+"' order by TRACKID desc limit 1";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					  item = (TB_TRACK) DBUtil
							.cursorToObject(cursor, TB_TRACK.class);
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

	private TB_TRACK getFirstItem(String taskname,String taskId,String userId) {
		TB_TRACK  item = new  TB_TRACK();
		Cursor cursor = null;
		try {
			String dbPath = userId + "/d" + taskId + "/TRNTASKDEFECTRET.DB";
			open(dbPath);
			String sql = "select * from TB_TRACK where TASKID='"+taskId+"' and TASKNAME='"+taskname+"' order by TRACKID asc limit 1";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					  item = (TB_TRACK) DBUtil
							.cursorToObject(cursor, TB_TRACK.class);
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
	public void updateItem(TB_TRACK item,String userId) {
		ContentValues values = DBUtil.objectToCV(item);
		
		String dbPath = userId + "/d" + item.getTASKID() + "/TRNTASKDEFECTRET.DB";
		open(dbPath);
	
		try {
			update("TB_TRACK", values, "ID = ?",
					new String[] { item.getTRACKID() });
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
	public void insertItem(TB_TRACK task,String userId) {
		ContentValues values = DBUtil.objectToCV(task);
		
		Log.i("insertItem", values.toString());
		String dbpath = userId+"/d"+task.getTASKID()+"/TRNTASKDEFECTRET.DB";
		open(dbpath);
	
		try {
			insert("TB_TRACK", null,values);
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
	public void delItem(TB_TRACK item,String userId) {
		String dbPath = userId + "/d" + item.getTASKID() + "/TRNTASKDEFECTRET.DB";
		open(dbPath);
	
		try {
			delete("TB_TRACK", "ID=?", new String[]{item.getTRACKID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * 根据轨迹名称获取所有的轨迹点, 升序排列
	 * @param trackName
	 * @return
	 */
	public List<TB_TRACK> getTrackPointsByTaskName(String taskName,String taskId,String userId) {
		List<TB_TRACK> list = new ArrayList<TB_TRACK>();
		Cursor cursor = null;
		try {
			String dbPath = userId + "/d" + taskId + "/TRNTASKDEFECTRET.DB";
			open(dbPath);
			String sql = "select * from TB_TRACK  where TASKID='"+taskId+"' and TASKNAME = '"+taskName+"' order by TRACKID";
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TRACK item = (TB_TRACK) DBUtil
							.cursorToObject(cursor, TB_TRACK.class);
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
	
	
	public List<TB_TRACK> getTrackPointsByTrackId(String taskId,String userId,String trackId) {
		List<TB_TRACK> list = new ArrayList<TB_TRACK>();
		Cursor cursor = null;
		try {
			String dbPath = userId + "/d" + taskId + "/TRNTASKDEFECTRET.DB";
			open(dbPath);
			String sql = "select * from TB_TRACK  where TRACKID = '"+trackId+"'";
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TRACK item = (TB_TRACK) DBUtil
							.cursorToObject(cursor, TB_TRACK.class);
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
