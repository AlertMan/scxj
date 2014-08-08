package com.scxj.utils;

import cn.pisoft.base.db.DBHelper;
import android.os.Environment;

/**
 * 全局参变量的key
 */
public class Const {
	public static final boolean DEBUG = true;
	
	/**
	 * message.what  参数定义
	 */
	private static int BEAR  = 1999;
	public static final int REMOVE_PROGRESS = 1997;
	public static final int SHOW_PROGRESS = 1998;
	public static final int DATA_DOWNLOAD_ALLSIZE = ++BEAR;
	public static final int DATA_DOWNLOAD_SIZE = ++BEAR;
	public static final int DATA_DOWNLOAD_COMPLETE = ++BEAR;
	public static final int UPDATEWAITING =  ++BEAR;
	public static final int GET_GPS_LOCATION = ++BEAR;
	
	  /**
	   * 初始化数据库路径
	   */
	public static final String PACKAGENAME = "com.scxj";
	static {
		DBHelper.PACKAGE_NAME = PACKAGENAME;
		DBHelper.DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+PACKAGENAME;
	}
	//存放其它程序文件
	public static final String FILEPATH = DBHelper.DB_PATH+"/files";
	public static final String ISFIRSTINSTALLED = "isFirstInstalled";

	/**
	 * 数据库名
	 */
	public final static String[] dbFileNames = {DB_NAME.TRNTASK,DB_NAME.TRNTASKRET,DB_NAME.TRNTASKDEFECT,DB_NAME.TRNTASKDEFECTRET,DB_NAME.TRNASSET,DB_NAME.TRNCOMMON};
	
	public  static class DB_NAME {
		public static String TRNTASK = "TRNTASK.DB";//任务数据库
		public static String TRNTASKRET = "TRNTASKRET.DB";//任务回传数据库
		public static String TRNTASKDEFECT = "TRNTASKDEFECT.DB";//消缺数据库
		public static String TRNTASKDEFECTRET = "TRNTASKDEFECTRET.DB";//消缺回传数据库
		public static String TRNASSET = "TRNASSET.DB";//设备台账
		public static String TRNCOMMON = "TRNCOMMON.DB";//用户信息
		public static String TRNPOINT = "TRNPOINT.DB";//绑卡信息
	
		/**
		 * 用户登录后 设置该用户数据目录
		 * @param userName
		 * @param flag  true表示强制覆盖 false表示文件存在则不覆盖
		 */
		public static boolean setUserDirectory(String userName,boolean flag) {
			TRNTASK = "TRNTASK.DB";//任务数据库
			TRNTASKRET = "TRNTASKRET.DB";//任务回传数据库
			TRNTASKDEFECT = "TRNTASKDEFECT.DB";//消缺数据库
			TRNTASKDEFECTRET = "TRNTASKDEFECTRET.DB";//消缺回传数据库
			TRNASSET = "TRNASSET.DB";//设备台账
			TRNCOMMON = "TRNCOMMON.DB";//用户信息
			TRNPOINT = "TRNPOINT.DB";//绑卡信息
			
			if (userName == null || "".equals(userName)) {
				return false;
			} else {
				String userDir = DBHelper.DB_PATH+"/"+userName + "/";
				copyFile(TRNTASK,DBHelper.DB_PATH,userDir,flag);
				copyFile(TRNTASKRET,DBHelper.DB_PATH,userDir,flag);
				copyFile(TRNTASKDEFECT,DBHelper.DB_PATH,userDir,flag);
				copyFile(TRNTASKDEFECTRET,DBHelper.DB_PATH,userDir,flag);
				copyFile(TRNASSET,DBHelper.DB_PATH,userDir,flag);
				copyFile(TRNPOINT,DBHelper.DB_PATH,userDir,flag);
				
				TRNTASK = userName + "/TRNTASK.DB";//任务数据库
				TRNTASKRET = userName + "/TRNTASKRET.DB";//任务回传数据库
				TRNTASKDEFECT = userName + "/TRNTASKDEFECT.DB";//消缺数据库
				TRNTASKDEFECTRET = userName + "/TRNTASKDEFECTRET.DB";//消缺回传数据库
				TRNASSET = userName + "/TRNASSET.DB";//设备台账
				TRNPOINT = userName +"/TRNPOINT.DB";//绑卡信息
				
				return true;
			}
		}
	
		/**
		 * @param userName    用户ID
		 * @param taskId    任务ID
		 * @param flag      true表示强制覆盖
		 * @param taskType  0表示巡视任务 1表示消缺任务
		 * @return
		 */
			public static boolean setUserTaskDirectory(String userName,String taskId,boolean flag,int taskType) {
				if (StringUtils.isNull(userName)) {
					return false;
				} else {
				
					if(0 == taskType){
						String userDir = DBHelper.DB_PATH+"/"+userName + "/";
						String userTaskDir = userDir +"t"+taskId + "/";
						String taskDbPath = userName+"/t"+taskId+"/";
						copyFile("TRNTASKRET.DB",userDir,userTaskDir,flag);
						TRNTASKRET = taskDbPath + "/TRNTASKRET.DB";//消缺数据库
						return true;
					}else{
						String userDir = DBHelper.DB_PATH+"/"+userName + "/";
						String userTaskDir = userDir+"d"+taskId + "/";
						String taskDbPath = userName+"/d"+taskId+"/";
						copyFile("TRNTASKDEFECTRET.DB",userDir,userTaskDir,flag);
						TRNTASKDEFECTRET = taskDbPath + "/TRNTASKDEFECTRET.DB";//消缺回传数据库
						return true;
					}
				}
			}
			
			/**
			 * 移动库文件
			 * @param fileName
			 * @param srcPath
			 * @param desPath
			 * @param flag 是否覆盖
			 */
			private static void copyFile(String fileName, String srcPath, String desPath,boolean flag) {
				FileUtil.copyFile( fileName,  srcPath,  desPath,flag);
			};

	}		

}
