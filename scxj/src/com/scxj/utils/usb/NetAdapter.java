package com.scxj.utils.usb;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pisoft.base.db.DBHelper;

import com.scxj.MyApplication;
import com.scxj.utils.Const;
import com.scxj.utils.FileUtil;
import com.scxj.utils.StringUtils;
import com.scxj.utils.net.WSUtils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetAdapter {

	public static void startAdapter(final Context context) throws IOException {
		if (!isWifiAvailable(context)) {
			USBNetUtils.startUSBNet();
		} 
	}
	
	public static void stopAdapter() {
		if (USBNetUtils.isOnUSBNet() ) {
			USBNetUtils.stopUSBNet();
		}
	}
	
	
	public static String checkUser(String jh,String bz,String userName,String password){

		String returnStatus = "";
		if (USBNetUtils.isOnUSBNet()) {
			returnStatus = USBNetUtils.checkUser(jh,bz,userName, password);
		}
		else {
			returnStatus = WSUtils.checkUser(jh,bz,userName, password);
		}
		return returnStatus;
	}
	
	
	
	public static boolean isWifiAvailable(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean flag =  wifi.isAvailable() && wifi.isConnected();
		return flag;
	}

	/**
	 * 应用升级
	 * @param versionName
	 * @return
	 */
	public static String updateSoft(String versionName,String appName) {
		if(USBNetUtils.isOnUSBNet()){
			return USBNetUtils.checkVersion(versionName,appName);
		}else{
			return WSUtils.checkVersion(versionName,appName);
		}
	}

	/**
	 * 下载巡视任务
	 * @param userName
	 * @return
	 */
	public static boolean downLoadPatrolTaskDB(String userName) {
		try {
			if(USBNetUtils.isOnUSBNet()){
				String resultStr =USBNetUtils.downLoadPatrolTaskDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
				
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					return USBNetUtils.downloadFileAndSave(downloadUrl,"TRNTASK.DB",DBHelper.DB_PATH+"/"+userName+"/");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}else{
				String resultStr = WSUtils.downLoadPatrolTaskDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
					//			return "服务连接失败" ;
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					
					MyApplication.getInstance().threadFlag = true;
					return FileUtil.downFile(downloadUrl, DBHelper.DB_PATH+"/"+userName+"/"+"TRNTASK.DB");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 增量下载巡视任务
	 * @param userName
	 * @return
	 */
	public static boolean downLoadPatrolTaskDBADD(String userName) {
		try {
			if(USBNetUtils.isOnUSBNet()){
				String resultStr =USBNetUtils.downLoadPatrolTaskDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
				
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					boolean downloadFlag = USBNetUtils.downloadFileAndSave(downloadUrl,"TRNTASKBAK.DB",DBHelper.DB_PATH+"/"+userName+"/");
					return downloadFlag;
				}else{	//return "服务连接失败";
					return false;
				}
				
			}else{
				String resultStr = WSUtils.downLoadPatrolTaskDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
					//			return "服务连接失败" ;
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					
					MyApplication.getInstance().threadFlag = true;
					boolean downloadFlag = FileUtil.downFile(downloadUrl, DBHelper.DB_PATH+"/"+userName+"/"+"TRNTASKBAK.DB");
					 
						return downloadFlag;
				}else{	//return "服务连接失败";
					return false;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * 下载消缺任务库
	 * @param userName
	 * @return
	 */
	public static boolean downLoadPatrolDefectDB(String userName) {
		try {
			if(USBNetUtils.isOnUSBNet()){
				String resultStr =USBNetUtils.downLoadPatrolDefectDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
				
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					return USBNetUtils.downloadFileAndSave(downloadUrl,"TRNTASKDEFECT.DB",DBHelper.DB_PATH+"/"+userName+"/");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}else{
				String resultStr = WSUtils.downLoadPatrolDefectDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
					//			return "服务连接失败" ;
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					
					MyApplication.getInstance().threadFlag = true;
					return FileUtil.downFile(downloadUrl, DBHelper.DB_PATH+"/"+userName+"/"+"TRNTASKDEFECT.DB");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 增量下载消缺任务库
	 * @param userName
	 * @return
	 */
	public static boolean downLoadPatrolDefectDBADD(String userName) {
		try {
			if(USBNetUtils.isOnUSBNet()){
				String resultStr =USBNetUtils.downLoadPatrolDefectDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
				
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					return USBNetUtils.downloadFileAndSave(downloadUrl,"TRNTASKDEFECTBAK.DB",DBHelper.DB_PATH+"/"+userName+"/");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}else{
				String resultStr = WSUtils.downLoadPatrolDefectDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
					//			return "服务连接失败" ;
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					
					MyApplication.getInstance().threadFlag = true;
					return FileUtil.downFile(downloadUrl, DBHelper.DB_PATH+"/"+userName+"/"+"TRNTASKDEFECTBAK.DB");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 台账数据
	 * @param userName
	 * @return
	 */
	public static boolean downLoadPatrolAssetDB(String userName) {
		try {
			if(USBNetUtils.isOnUSBNet()){
				String resultStr =USBNetUtils.downLoadPatrolAssetDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
				
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					return USBNetUtils.downloadFileAndSave(downloadUrl,"TRNASSET.DB",DBHelper.DB_PATH+"/"+userName+"/");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}else{
				String resultStr = WSUtils.downLoadPatrolAssetDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
					//			return "服务连接失败" ;
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					
					MyApplication.getInstance().threadFlag = true;
					return FileUtil.downFile(downloadUrl, DBHelper.DB_PATH+"/"+userName+"/"+"TRNASSET.DB");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	/**
	 * 巡视点绑卡数据
	 * @param userName
	 * @return
	 */
	public static boolean downloadBindingInfo(String userName) {
		try {
			if(USBNetUtils.isOnUSBNet()){
				String resultStr =USBNetUtils.downloadBindingInfo(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
				
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					return USBNetUtils.downloadFileAndSave(downloadUrl,"TRNPOINT.DB",DBHelper.DB_PATH+"/");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}else{
				String resultStr = WSUtils.downLoadPatrolPointDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
					//			return "服务连接失败" ;
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					MyApplication.getInstance().threadFlag = true;
					return FileUtil.downFile(downloadUrl, DBHelper.DB_PATH+"/"+"TRNPOINT.DB");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 下载用户信息库
	 * @param userName
	 * @return
	 */
	public static boolean downLoadPatrolUserDB(String userName) {
		try {
			if(USBNetUtils.isOnUSBNet()){
				String resultStr =USBNetUtils.downLoadPatrolUserDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
				
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					return USBNetUtils.downloadFileAndSave(downloadUrl,"TRNCOMMON.DB",DBHelper.DB_PATH+"/");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}else{
				String resultStr = WSUtils.downLoadPatrolUserDB(userName);
				if (StringUtils.isNull(resultStr)) {// 服务连接失败
					//			return "服务连接失败" ;
					return false;
				}
				JSONObject jb = new JSONObject(resultStr);
				if("0".equals(jb.getString("resultCode"))){
					String downloadUrl = jb.getString("resultDesc");
					
					MyApplication.getInstance().threadFlag = true;
					return FileUtil.downFile(downloadUrl, DBHelper.DB_PATH+"/"+"TRNCOMMON.DB");
				}else{	//return "服务连接失败";
					return false;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 更新用户信息
	 * @param userName
	 * @param pw
	 * @param sex
	 * @param tel
	 * @return
	 */
	public static String updateUserInfo(String userName, String pw, String sex,
			String tel) {
		if(USBNetUtils.isOnUSBNet()){
			return USBNetUtils.updateUserInfo(userName,pw,sex,tel);
		}else{
			return WSUtils.updateUserInfo(userName,pw,sex,tel);
		}
	}

	public static String uploadTaskInfo(String userName, String taskId, byte[] data) {
		if(USBNetUtils.isOnUSBNet()){
			return USBNetUtils.uploadTaskInfo(userName,taskId,data);
		}else{
			return WSUtils.uploadTaskInfo(userName,taskId,data);
		}
	}

	public static String uploadTaskDefectInfo(String userName,
			String taskDefectId, byte[] data) {
		if(USBNetUtils.isOnUSBNet()){
			return USBNetUtils.uploadTaskDefectInfo(userName,taskDefectId,data);
		}else{
			return WSUtils.uploadTaskDefectInfo(userName,taskDefectId,data);
		}
	}

	/**
	 * 上传图片
	 */
	public static String uploadAttachInfo(String taskType,String taskId,String attachId, String assetId,
			byte[] data) {
		if(USBNetUtils.isOnUSBNet()){
			return USBNetUtils.uploadAttachInfo(taskType,taskId,attachId,assetId,data);
		}else{
			return WSUtils.uploadAttachInfo(taskType,taskId,attachId,assetId,data);
		}
	}

	public static String updateTaskStatus(String taskId, String taskType,
			String confirm) {
		if(USBNetUtils.isOnUSBNet()){
			return USBNetUtils.updateTaskStatus(taskId,taskType,confirm);
		}else{
			return WSUtils.updateTaskStatus(taskId,taskType,confirm);
		}
	}

	public static String uploadBindingInfo(String userId, String encode) {
		if(USBNetUtils.isOnUSBNet()){
			return USBNetUtils.uploadBindingInfo(userId,encode);
		}else{
			return WSUtils.uploadBindingInfo(userId,encode);
		}
	}
 

}
