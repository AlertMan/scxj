package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.DBUtil;

import cn.pisoft.base.db.DBHelper;

import android.content.Context;
import android.database.Cursor;


public class InitTaskDao extends DBHelper {

	private String BakDbPath="";
	private Context mContext = null;
	private InitTaskDao(Context context) {
		super(context);
	}
	
	public InitTaskDao(Context mContext,String bakDbPath){
		this(mContext);
		this.mContext = mContext;
		this.BakDbPath = bakDbPath;
	}
	 
	/**
	 * 下载过来的ＩＤ必须保持唯一性。 
	 * @return
	 */
	public boolean initTargetDbData(){
		//任务表
		TaskDao taskDao = new TaskDao(mContext);
		List<TB_TASK> existsTasks = taskDao.getAllList(-1);//全部任务数据
		
		List<TB_TASK> bakTasks = getAllTasks();//全部任务数据
		for(TB_TASK task : bakTasks ){
			List<TB_TASK> items = new ArrayList<TB_TASK>();
			for(int i=0; i < existsTasks.size(); i++){//清除本地已上传的任务数据
				TB_TASK item  = existsTasks.get(i);
				if(item.getTASKID().equals(task.getTASKID()) && "已上传".equals(item.getSTATUS())){
					String taskId = item.getTASKID();
					taskDao.delItem(item);
					taskDao.delTaskLinesByTaskId(taskId);
					taskDao.delTaskTowersByTaskId(taskId);
					items.add(item);
				}
			}
			
			existsTasks.removeAll(items);
			
			if(!existsTasks.contains(task)){
				taskDao.insertItem(task);	//没在的话，就插入
				//作业线路
				List<TB_LINE> taskLines = getAllTaskLines(task.getTASKID());
				for(TB_LINE line : taskLines){
					taskDao.insertLineItem(line);
				}
				//作业杆塔
				List<TB_TOWER> taskTowers = getAllTaskLineTowers(task.getTASKID());
				for(TB_TOWER taskRisk : taskTowers){
					taskDao.insertTowerItem(taskRisk);
				}
			}
		}
		return true;
	}
	
	/**
	 * 获取副本库所有的任务数据
	 * @return
	 * TRNTASKBAK.DB
	 */
	private List<TB_TASK> getAllTasks(){
		List<TB_TASK> list = new ArrayList<TB_TASK>();
		Cursor cursor = null;
		try {
			String sql = "select * from TB_TASK";
			open(BakDbPath);
			cursor = rawQuery(sql,null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TASK item = (TB_TASK) DBUtil
							.cursorToObject(cursor, TB_TASK.class);
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
	 * 获取副本库所有的任务作业线路数据
	 * @return
	 */
	private List<TB_LINE> getAllTaskLines(String taskId){
		List<TB_LINE> list = new ArrayList<TB_LINE>();
		Cursor cursor = null;
		try {
			String sql = "select * from TB_LINE where TASKID='"+taskId+"'";
			open(BakDbPath);
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_LINE item = (TB_LINE) DBUtil
							.cursorToObject(cursor, TB_LINE.class);
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
	 * 获取副本库所有的任务杆塔数据
	 * @return
	 */
	private List<TB_TOWER> getAllTaskLineTowers(String taskId){
		List<TB_TOWER> list = new ArrayList<TB_TOWER>();
		Cursor cursor = null;
		try {
			String sql = "select * from TB_TOWER where TASKID='"+taskId+"'";
			open(BakDbPath);
			cursor = rawQuery(sql,null);
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
				cursor = null;
			}
			close();
		}
		return list;
	}
	
}
