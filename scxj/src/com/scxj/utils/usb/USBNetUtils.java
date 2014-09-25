package com.scxj.utils.usb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import com.scxj.utils.net.Base64;
import com.scxj.utils.net.WSUtils;
@SuppressLint("NewApi")
public class USBNetUtils {

	private static SocketListenerThread socketListenerThread = null;
	
	public static void startUSBNet() throws IOException {
		stopUSBNet();
		socketListenerThread = new SocketListenerThread();
		socketListenerThread.startListen();
	}
	
	public static void stopUSBNet() {
		if (socketListenerThread != null ) {
			if (socketListenerThread.isListening()) {
				try {
					socketListenerThread.stopListen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socketListenerThread = null;
		}
	}
	public static boolean isOnUSBNet() {
		return socketListenerThread != null && socketListenerThread.isListening();
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getConnStatus(String wsdl){
		String results = "";
		if (isOnUSBNet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "getConnStatus");
			map.put("wsdl", wsdl);
			map.put("currTimeOut", "5000");
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}
	
	
	public static String checkUser(String jh,String bz,String userName,String password){
		String returnResult="";
		if (isOnUSBNet()) {
			//return "OK";
			String methodName = "checkUser";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", methodName);
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", Integer.toString(WSUtils.timeout));

			map.put("USERNAME", userName);
			map.put("PASSWORD", password); 
			map.put("JH", jh);
			map.put("BZ", bz);
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				returnResult = (String) obj;
			}
		}
		return returnResult;
	}
	
	
	/**
	 * ���ر�׼��
	 * @param localFilePath 
	 * @param fileName 
	 * @param urlObj
	 * @return
	 */
	public static boolean downloadFileAndSave(String url, String fileName, String localFilePath){
		byte[] b = new byte[0];
		if(isOnUSBNet()){
			Object obj = socketListenerThread.callHttp(url,fileName,localFilePath);
			if(obj != null){
				String ret = (String) obj;
				 return true;
			}	
			
		}
		return false;
	}

	public static byte[] downloadVersonFile(String urls) {
		byte[] b = new byte[0];
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("methodName", "downloadVersonFile");
			map.put("currTimeOut", "3000");
			map.put("status", "0");
			map.put("urls", urls);
			Object obj = socketListenerThread.callWS(map);
			if(obj != null){
				String ret = (String) obj;
				if (ret.length() > 0) {
					try {
						b = ret.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return b;
	}

	public static byte[] downloadApkFile(String urls) {
		byte[] b = new byte[0];
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("methodName", "downloadApkFile");
			map.put("currTimeOut", "3000");
			map.put("status", "0");
			map.put("urls", urls);
			Object obj = socketListenerThread.callWS(map);
			if(obj != null){
				String ret = (String) obj;
				if (ret.length() > 0) {
					try {
						b = ret.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return b;
	}

	public static String downLoadPatrolTaskDB(String userName) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "downloadTaskInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}
	
	public static String downLoadPatrolDefectDB(String userName) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "downloadTaskDefectInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}

	public static String downLoadPatrolAssetDB(String userName) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "downloadAssetInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}
	/**
	 * 下载用户绑卡数据库 接口名称downloadPointsInfo
	 * @param userName
	 * @return
	 */
	public static String downloadBindingInfo(String userName) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "downloadBindingInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}
	 
	
	/**
	 * 下载用户信息库
	 * @param userName
	 * @return
	 */
	public static String downLoadPatrolUserDB(String userName) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "getUserInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}

	public static String setPatrRresultResponse(String userName, byte[] fileData,
			String taskId, String methodName,String orgId,String siteId) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", methodName);
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("taskId", taskId);
			map.put("orgId", orgId);
			map.put("siteId", siteId);
			map.put("fileData", Base64.encode(fileData));
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}

	public static String uploadBugInfo(String content, String userName,
			String methodName) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName",methodName);
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("content", content);
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}

	/**
	 * 版本升级
	 * @param versionName
	 * @param appName
	 * @return
	 */
	public static String checkVersion(String versionName,String appName) {
		String returnResult="";
			//return "OK";
			String methodName = "updateSoftInfo";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", methodName);
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", Integer.toString(WSUtils.timeout));

			map.put("versionName", versionName);
			map.put("appName", appName); 
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				returnResult = (String) obj;
			}
			return returnResult;
	}

	public static String updateUserInfo(String userName, String pw, String sex,
			String tel) {
		String returnResult="";
		String methodName = "updateUserInfo";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nameSpace", WSUtils.nameSpace);
		map.put("methodName", methodName);
		map.put("wsdl", WSUtils.wsdl);
		map.put("currTimeOut", Integer.toString(WSUtils.timeout));

		map.put("userName", userName);
		map.put("password", pw);
		map.put("sex", sex);
		map.put("phone", tel);
		
		map.put("status", "0");
		Object obj = socketListenerThread.callWS(map);
		if (obj != null) {
			returnResult = (String) obj;
		}
		return returnResult;
}

	/**
	 * 上传任务结果
	 * @param userName
	 * @param taskId
	 * @param data
	 * @return
	 */
	public static String uploadTaskInfo(String userName, String taskId,
			byte[] data) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "uploadTaskInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("taskid", taskId);
			map.put("fileData", Base64.encode(data));
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}

	/**
	 * 上传消缺任务结果
	 * @param userName
	 * @param taskDefectId
	 * @param data
	 * @return
	 */
	public static String uploadTaskDefectInfo(String userName,
			String taskDefectId, byte[] data) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "uploadTaskDefectInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("taskdefectid", taskDefectId);
			map.put("fileData", Base64.encode(data));
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}

	/**
	 * 上传附件
	 * @param taskType
	 * @param taskId
	 * @param attachId
	 * @param data
	 * @return
	 */
	public static String uploadAttachInfo(String taskType, String taskId,
			String attachId,String assetId, byte[] data) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "uploadAttachInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("taskType", taskType);
			map.put("taskId", taskId);
			map.put("attachId", attachId);
			map.put("assetId", assetId);
			map.put("fileData", Base64.encode(data));
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}

	public static String updateTaskStatus(String taskId, String taskType,
			String taskConfirm) {
		String returnResult="";
		String methodName = "updateTaskStatus";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nameSpace", WSUtils.nameSpace);
		map.put("methodName", methodName);
		map.put("wsdl", WSUtils.wsdl);
		map.put("currTimeOut", Integer.toString(WSUtils.timeout));

		map.put("taskId", taskId);
		map.put("taskType", taskType);
		map.put("taskConfirm", taskConfirm);
		map.put("status", "0");
		Object obj = socketListenerThread.callWS(map);
		if (obj != null) {
			returnResult = (String) obj;
		}
		return returnResult;
}

	public static String uploadBindingInfo(String userName, String encode) {
		String results = "";
		if(isOnUSBNet()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nameSpace", WSUtils.nameSpace);
			map.put("methodName", "uploadBindingInfo");
			map.put("wsdl", WSUtils.wsdl);
			map.put("currTimeOut", "30000");
			map.put("userName", userName);
			map.put("pointFile", encode);
			map.put("status", "0");
			Object obj = socketListenerThread.callWS(map);
			if (obj != null) {
				results = (String) obj;
			}
		}
		return results;
	}
	
	
}
