package com.scxj.dao;

import java.util.ArrayList;
import java.util.List;

import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.DBUtil;

import cn.pisoft.base.db.DBHelper;

import android.content.Context;
import android.database.Cursor;


public class InitDefectTaskDao extends DBHelper {

	private String BakDbPath="";
	private Context mContext = null;
	private InitDefectTaskDao(Context context) {
		super(context);
	}
	
	public InitDefectTaskDao(Context mContext,String bakDbPath){
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
		TaskDefectDao taskDao = new TaskDefectDao(mContext);
		List<TB_TASK_DEFECT> existsTasks = taskDao.getAllTaskDefect(-1);//全部任务数据
		
		List<TB_TASK_DEFECT> bakTasks = getAllTasks();//全部任务数据
		
		for(TB_TASK_DEFECT task : bakTasks ){
			List<TB_TASK_DEFECT> items = new ArrayList<TB_TASK_DEFECT>();
			for(int i=0; i < existsTasks.size(); i++){//清除本地已上传的任务数据
				TB_TASK_DEFECT item  = existsTasks.get(i);
				if(item.getDEFECTTASKID().equals(task.getDEFECTTASKID()) && "已上传".equals(item.getSTATUS())){
					taskDao.delItem(item);
					items.add(item);
				}
			}
			
			existsTasks.removeAll(items);
			
			if(!existsTasks.contains(task)){
				taskDao.insertItem(task);	//没在的话，就插入
			}
		}
		return true;
	}
	
	/**
	 * 获取副本库所有的任务数据
	 * @return
	 * TRNTASKBAK.DB
	 */
	private List<TB_TASK_DEFECT> getAllTasks(){
		List<TB_TASK_DEFECT> list = new ArrayList<TB_TASK_DEFECT>();
		Cursor cursor = null;
		try {
			String sql = "select * from TB_TASK_DEFECT";
			open(BakDbPath);
			cursor = rawQuery(sql,null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					TB_TASK_DEFECT item = (TB_TASK_DEFECT) DBUtil
							.cursorToObject(cursor, TB_TASK_DEFECT.class);
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
