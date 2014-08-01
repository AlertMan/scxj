package com.scxj.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import cn.pisoft.base.db.DBHelper;

import com.scxj.MyApplication;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TASK_RET;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.Const;
import com.scxj.utils.DBUtil;
import com.scxj.utils.StringUtils;

public class RetTaskDao extends DBHelper {

	public RetTaskDao(Context context) {
		super(context);
	}

	/**
	 * 更新一个对象示例
	 * @param inspectionID
	 * @param status
	 */
	public void updateItem(TB_TASK_RET item) {
		ContentValues values = DBUtil.objectToCV(item);
		open(Const.DB_NAME.TRNTASKRET);
		
		try {
			update("TB_TASK_RET", values, "ID = ?",
					new String[] { item.getID() });
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
	public void insertItem(TB_TASK_RET task) {
		ContentValues values = DBUtil.objectToCV(task);
		
		Log.i("insertItem", values.toString());
		open(Const.DB_NAME.TRNTASK);
	
		try {
			insert("TB_TASK_RET", null,values);
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
	public void delItem(TB_TASK_RET item) {
		open(Const.DB_NAME.TRNTASKRET);
	
		try {
			delete("TB_TASK_RET", "ID=?", new String[]{item.getTASKID()});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

		
	
	
	/**
	 * 查询检测项结果,按预先的检测项进行替换数据中的数据，比对检测项目名，保持一致。
	 * @param taskid
	 * @param lineid
	 * @param towerid
	 * @return
	 */
	public List<TB_TASK_RET> getTowersCheckListById(TB_TASK task,TB_TOWER tower) {
		List<TB_TASK_RET> list = new ArrayList<TB_TASK_RET>();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKRET);
			String sql = "select * from TB_TASK_RET  where TASKID = '"+task.getTASKID()+"' and LINEID='" + task.getLine().getLINEID() +"' and TOWERID='" + tower.getTOWERID() +"' and MEASURETYPE <>'金具及绝缘子'";//后面加的补丁 消除‘金具及绝缘子’选项
			cursor = rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {//如果有数据的话，就用数据库数据　
				do{
					TB_TASK_RET item = (TB_TASK_RET) DBUtil
							.cursorToObject(cursor, TB_TASK_RET.class);
					int imgCnt = getImgCnt(item);
					item.setImgCnt(imgCnt);
					list.add(item);
				}while(cursor.moveToNext());
			}else{//初始化数据
				List<TB_TASK_RET> initDatas = initTowerCheckItem(task,tower);
				for(int i=0; i<initDatas.size(); i++){
					insertTaskRetItem(initDatas.get(i));
				}
				return getTowersCheckListById(task, tower);
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
	 * 计算每一项测量的图片数
	 * @param item
	 * @return
	 */
	private int getImgCnt(TB_TASK_RET item) {
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKRET);
			String sql = "select count(*) as cnt from TB_ATTACH where PARENTID='"+item.getID()+"'";
			 
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
	
	
	/**
	 * 计算每一个杆塔的图片数
	 * @param item
	 * @return
	 */
	private int getTowerImgCnt(TB_TASK_RET item) {
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKRET);
			String sql = "select count(*) as cnt from TB_ATTACH where PARENTID in (select ID from TB_TASK_RET where TASKID='"+item.getTASKID()+"' and TOWERNAME='"+item.getTOWERNAME()+"')";
			 
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

	private void insertTaskRetItem(TB_TASK_RET taskRet) {
		ContentValues values = DBUtil.objectToCV(taskRet);
		
		Log.i("insertTaskRetItem", values.toString());
		open(Const.DB_NAME.TRNTASKRET);
	
		try {
			insert("TB_TASK_RET", null,values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	

	/**
	 * 初始化检测项目
	 * @param taskId
	 * @param lineId
	 * @param towerId
	 * @return
	 */
	private List<TB_TASK_RET> initTowerCheckItem(TB_TASK task,TB_TOWER tower){
		List<TB_TASK_RET> list = new ArrayList<TB_TASK_RET>();
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"基础及接地装置","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"接线","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"杆塔","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		//list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"金具及绝缘子","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"导地线","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"金具","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"绝缘子","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"交叉跨越","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"通道","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"标示牌及警示标志","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"防雷防鸟防污设施","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"其它附属设施","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		list.add(new TB_TASK_RET(null,task.getTASKID(),task.getTASKNAME(),task.getLine().getLINEID(),task.getLine().getLINENAME(),"外部隐患","未巡视","一般","","",tower.getTOWERID(),tower.getTOWERNAME(),task.getLine().getVOLTRANK()));
		return list;
	}

	/**
	 * 聚合各个杆塔照片数据
	 * @return
	 */
	public List<TB_TASK_RET> getAllList(String userName) {
		List<TB_TASK_RET> list = new ArrayList<TB_TASK_RET>();
		Cursor cursor = null;
		try {
			List<TB_TASK> taskList = new ArrayList<TB_TASK>();
			open(Const.DB_NAME.TRNTASK);
			String tasksql = "select * from TB_TASK";
			cursor = rawQuery(tasksql,null);
			if(cursor != null ){
				while(cursor.moveToNext()){
					TB_TASK taskItem = (TB_TASK)DBUtil.cursorToObject(cursor, TB_TASK.class);
					taskList.add(taskItem);
				}
				cursor.close();
				cursor = null;
				close();
			}
			
			for(TB_TASK task : taskList){
				Const.DB_NAME.setUserTaskDirectory(userName, task.getTASKID(), false, 0);
				String sql = "select * from TB_TASK_RET group by TOWERID order by TASKID,TOWERID";   // 
				open(Const.DB_NAME.TRNTASKRET);
				cursor = rawQuery(sql, null);
				if (cursor != null) {//如果有数据的话，就用数据库数据　
					while (cursor.moveToNext()) {
						TB_TASK_RET item = (TB_TASK_RET) DBUtil
								.cursorToObject(cursor, TB_TASK_RET.class);
						
						int imgCnt = getTowerImgCnt(item);
						if(imgCnt > 0){
							list.add(item);
						}
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

	 

	
	public TB_TASK_RET getTaskRetById(String parentid) {
		TB_TASK_RET  item = new  TB_TASK_RET();
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKRET);
			String sql = "select * from TB_TASK_RET where ID='"+parentid+"'";
			 
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					  item = (TB_TASK_RET) DBUtil
							.cursorToObject(cursor, TB_TASK_RET.class);
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
 * 
 * @param taskId
 * @param lineId
 * @param towerId
 * @param status 0未巡视　１正常　２异常
 * @return
 */
	public int cntTowerCheckDetail(String taskId,String lineId,String towerId,int status) {
		int result = 0;
		Cursor cursor = null;
		try {
			open(Const.DB_NAME.TRNTASKRET);
			String sql = "select count(*) as cnt from TB_TASK_RET  where TASKID = '"+taskId+"' and LINEID='" + lineId +"' ";
			if(!StringUtils.isNull(towerId)){
				sql += " and TOWERID='" + towerId +"'";
			}
			
			switch (status) {
			case 0:
				sql +=" and STATUS='未巡视'";
				break;
			case 1:
				sql +=" and STATUS='正常'";
				break;
			case 2:
				sql +=" and STATUS='异常'";
				break;
			default:
				break;
			}
			cursor = rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					result = cursor.getInt(0);
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
		return result;
	}

	/**
	 * 统计一个巡检任务的异常情况
	 * @param taskId
	 * @return
	 */
public int cntTowerCheckDetailByTaskId(String taskId) {
	int result = 0;
	Cursor cursor = null;
	try {
		String userName = MyApplication.getInstance().loginUser.getUSERNAME();
		String dbPath = "/"+userName+"/t"+taskId+"/TRNTASKRET.DB";
		open(dbPath);
		String sql = "select count(*) as cnt from TB_TASK_RET  where TASKID = '"+taskId+"' and STATUS='异常'";
		cursor = rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				result = cursor.getInt(0);
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
	return result;
}	
}
