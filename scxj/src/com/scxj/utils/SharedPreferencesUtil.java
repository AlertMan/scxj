package com.scxj.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * @author kegui.zeng
 */
public class SharedPreferencesUtil {
	private Context _context;
	private SharedPreferences sharedPreferences;
	
	public static final String LOGIN_USERID = "LOGIN_USERID";
	public static final String MOBILE_VALUE = "MOBILE_VALUE"; 
	public static final String LOGIN_PASSWORD = "LOGIN_PASSWORD";
	public static final String IS_AUTO_LOGIN = "IS_AUTO_LOGIN";
	public static final String DEVICE_DOWN_TIMESTAMP = "DEVICE_DOWN_TIMESTAMP";
	
	public static final String SERVER_SET_DATA = "SERVER_SET_DATA";
	public static final String SERVER_ACTIVE_IDX_WIFI = "SERVER_ACTIVE_IDX_WIFI";
	public static final String SERVER_ACTIVE_IDX_3G = "SERVER_ACTIVE_IDX_3G";
	
	public static final String TRACK_SPLIT_MIN = "TRACK_SPLIT_MIN";
	public static final String TRACK_SPLIT_DISTANCE = "TRACK_SPLIT_DISTANCE";

	public static final String IMEI_DATA = "IMEI_DATA";
	public static final String RECENT_LOGIN_ID = "RECENT_LOGIN_ID";
	public static final String RECENT_LOGIN_NAME = "RECENT_LOGIN_NAME";
	public static final String SPILT_DISTANCE = "spilt_distance";
	public static final String PATROL_SPILT_DISTANCE = "patrol_spilt_distance";
	public static final String SPILT_TIME = "spilt_distance";
	public static final String REGISTER_DEVICE = "REGISTER_DEVICE";

	public static final String LOGIN_ID = "LOGIN_ID";
	public static final String SUPER_ORG_ID = "SUPER_ORG_ID";
	public static final String RECENT_LOGIN_ID_QX = "RECENT_LOGIN_ID_QX";
	@SuppressWarnings("deprecation")
	public SharedPreferencesUtil(Context context) {
		_context = context;
		sharedPreferences = _context.getSharedPreferences("scxj", Context.MODE_PRIVATE);
	}

	/**
	 * 写数�?
	 * @param name
	 * @param value
	 */
	public void writeData(String key,String value){
	    Editor prefsPrivateEditor = sharedPreferences.edit();  
	    prefsPrivateEditor.putString(key, value); 
	    prefsPrivateEditor.commit();  
	}
	
	/**
	 * 读数据�?
	 * @param key
	 */
	public String readData(String key){
		return sharedPreferences.getString(key, "");
	}

	/**
	 * 写服务器设置数据
	 * @param name
	 * @param value
	 */
	public void writeServerSetData(String value){
		writeData(SERVER_SET_DATA, value);
	}

	/**
	 * 读服务器设置数据ֵ
	 * @param key
	 */
	public String readServerSetData(){
		return readData(SERVER_SET_DATA);
	}

	 //读最近登录用户ID
	public String readRecentLoginId(){
		return readData(RECENT_LOGIN_ID);
	}

	//写最近登录用户ID
	public void writeRecentLoginId(String value){
		writeData(RECENT_LOGIN_ID, value);
	}

	 //读最近登录用户名�?
	public String readRecentLoginName(){
		return readData(RECENT_LOGIN_NAME);
	}

	//写最近登录用户名�?
	public void writeRecentLoginName(String value){
		writeData(RECENT_LOGIN_NAME, value);
	}

	 //读IMEI数据
	public String readImeiData(){
		return readData(IMEI_DATA);
	}

	//写IMEI数据
	public void writeImeiData(String value){
		writeData(IMEI_DATA, value);
	}


	 //读自动记录轨迹间隔时�?
	public String readTrackSplitMinData(){
		return readData(TRACK_SPLIT_MIN);
	}

	//写自动记录轨迹间隔时�?
	public void writeTrackSplitMinData(String value){
		writeData(TRACK_SPLIT_MIN, value);
	}

	 //读自动记录轨迹间隔距�?
	public String readTrackSplitDistanceData(){
		return readData(TRACK_SPLIT_DISTANCE);
	}

	//写自动记录轨迹间隔距�?
	public void writeTrackSplitDistanceData(String value){
		writeData(TRACK_SPLIT_DISTANCE, value);
	}

	/**
	 * 写激活服务器的索�?
	 * @param name
	 * @param value
	 */
	public void writeServerActiveIdx(String type,String value){
		if("WIFI".equals(type)){
			writeData(SERVER_ACTIVE_IDX_WIFI, value);
		}else if("APN".equals(type)){
			writeData(SERVER_ACTIVE_IDX_3G, value);
		}
	}

	/**
	 * 读WIFI�?��服务器的索引
	 * @param key
	 */
	public int readServerActiveIdxWIFI(){
		int idx = 0;
		String idxStr = readData(SERVER_ACTIVE_IDX_WIFI);
		if(idxStr!=null && !"".equals(idxStr)){
			idx = Integer.valueOf(idxStr).intValue();
		}else{
			return -1;
		}
		return idx;
	}
	/**
	 * �?G�?��服务器的索引
	 * @param key
	 */
	public int readServerActiveIdx3G(){
		int idx = 0;
		String idxStr = readData(SERVER_ACTIVE_IDX_3G);
		if(idxStr!=null && !"".equals(idxStr)){
			idx = Integer.valueOf(idxStr).intValue();
		}else{
			return -1;
		}
		return idx;
	}
	/**
	 * 读WIFI�?��服务器信�?
	 * @param key
	 */
	public String[] readServerActiveDataWIFI(){
		int idx = readServerActiveIdxWIFI();
		if(idx == -1){
			return null;
		}
		String[] serverAtts = null;
		String serversDataStr = readServerSetData();
        if(serversDataStr!=null && !"".equals(serversDataStr)){
        	String[] servers = serversDataStr.split("#");
        	String[] serverAttsTmp = servers[idx].split(";");
        	if(serverAttsTmp.length == 4){
        		serverAtts = new String[5];
        		serverAtts[0] = serverAttsTmp[0];
        		serverAtts[1] = serverAttsTmp[1];
        		serverAtts[2] = serverAttsTmp[2];
        		serverAtts[3] = "WIFI";
        		serverAtts[4] = serverAttsTmp[3];
        	}else if(serverAttsTmp.length == 5){
            	serverAtts = serverAttsTmp;
        	}
        }else{
        	//serverAtts = HttpDataService.HOST_URL_COMMON_YDYYSERVICE.split(";");
        }
		return serverAtts;
	}

	/**
	 * �?G�?��服务器信�?
	 * @param key
	 */
	public String[] readServerActiveData3G(){
		int idx = readServerActiveIdx3G();
		if(idx == -1){
			return null;
		}
		String[] serverAtts = null;
		String serversDataStr = readServerSetData();
        if(serversDataStr!=null && !"".equals(serversDataStr)){
        	String[] servers = serversDataStr.split("#");
        	String[] serverAttsTmp = servers[idx].split(";");
        	if(serverAttsTmp.length == 4){
        		serverAtts = new String[5];
        		serverAtts[0] = serverAttsTmp[0];
        		serverAtts[1] = serverAttsTmp[1];
        		serverAtts[2] = serverAttsTmp[2];
        		serverAtts[3] = "WIFI";
        		serverAtts[4] = serverAttsTmp[3];
        	}else if(serverAttsTmp.length == 5){
            	serverAtts = serverAttsTmp;
        	}
        }else{
//        	serverAtts = HttpDataService.HOST_URL_COMMON_YDYYSERVICE.split(";");
        }
		return serverAtts;
	}

	/**
	 * 根据索引读服务器信息
	 * @param key
	 */
	public String[] readServerActiveDataByIdx(int idx){
		String[] serverAtts = null;
		String serversDataStr = readServerSetData();
        if(serversDataStr!=null && !"".equals(serversDataStr)){
        	String[] servers = serversDataStr.split("#");
        	serverAtts = servers[idx].split(";");
        }else{
//        	serverAtts = HttpDataService.HOST_URL_COMMON_YDYYSERVICE.split(";");
        }
		return serverAtts;
	}
	
	/**
	 * 写设备下载时间戳
	 * @param value
	 */
	public void writeDeviceDownTimestamp(String value){
		writeData(DEVICE_DOWN_TIMESTAMP, value);
	}
	
	/**
	 * 读设备下载时间戳
	 * @return
	 */
	public String readDeviceDownTimestamp(){
		return readData(DEVICE_DOWN_TIMESTAMP);
	}
	
	/**
	 * 
	 * @return
	 */
	public String readIsAutoLogin(){
		return readData(IS_AUTO_LOGIN);
	}
	
	/**
	 * 
	 * @param value
	 */
	public void writeIsAutoLogin(String value){
		writeData(IS_AUTO_LOGIN, value);
	}
	
	
	public int readSpiltDistance(){
		int idx = 0;
		String idxStr = readData(SPILT_DISTANCE);
		if(idxStr!=null && !"".equals(idxStr)){
			idx = Integer.valueOf(idxStr).intValue();
		}
		return idx;
	}
	
	public void writeSplitDistance(String value){
		writeData(SPILT_DISTANCE, value);
	}
	
	/**
	 * 最小值为0
	 * @return
	 */
	public int readSpiltTime(){
		int idx = 0;
		String idxStr = readData(SPILT_TIME);
		if(idxStr!=null && !"".equals(idxStr)){
			idx = Integer.valueOf(idxStr).intValue();
		}
		return idx;
	}
	
	public void writeSpliteTime(String value){
		writeData(SPILT_TIME, value);
	}
	
	/**
	 * 写注册设�?
	 * @param value
	 */
	public void writeRegisterDevice(String value){
		writeData(REGISTER_DEVICE, value);
	}
	
	/**
	 * 读注册设�?
	 * @return
	 */
	public String readRegisterDevice(){
		return readData(REGISTER_DEVICE);
	}
	
	public int readPatrolSpiltDistance(){
		int idx = 0;
		String idxStr = readData(PATROL_SPILT_DISTANCE);
		if(idxStr!=null && !"".equals(idxStr)){
			idx = Integer.valueOf(idxStr).intValue();
		}
		return idx;
	}
	
	public void writePatrolSplitDistance(String value){
		writeData(PATROL_SPILT_DISTANCE, value);
	}
	
	public void writeLoginId(String value){
		writeData(LOGIN_ID, value);
	}
	
	public String readLoginId(){
		return readData(LOGIN_ID);
	}
	
	public void writeSuperOrgId(String value){
		writeData(SUPER_ORG_ID, value);
	}
	
	public String readSuperOrgId(){
		return readData(SUPER_ORG_ID);
	}
	//写最近登录用户ID的权�?
	public void writeRecentLoginIdQX(String value){
		writeData(RECENT_LOGIN_ID_QX, value);
	}

	 //读最近登录用户ID的权�?
	public String readRecentLoginIdQX(){
		return readData(RECENT_LOGIN_ID_QX);
	}

	public  void saveHostIp(String serverIp) {
		// TODO Auto-generated method stub
		writeData("SERVER_HOST_IP", serverIp);
	}
	
	public  String readHostIp() {
		// TODO Auto-generated method stub
		return readData("SERVER_HOST_IP");
	}
	
	public  void saveHostPort(String serverPort) {
		// TODO Auto-generated method stub
		writeData("SERVER_HOST_PORT", serverPort);
	}
	public  String readHostPort() {
		// TODO Auto-generated method stub
		return readData("SERVER_HOST_PORT");
	}

	/**
	 * 巡视任务与消缺任务
	 * @param value
	 */
	public void writeTaskType(String value) {
		writeData("TASK_TYPE", value);
	}
	
	public String readTaskType() {
		return readData("TASK_TYPE");
	}
}
