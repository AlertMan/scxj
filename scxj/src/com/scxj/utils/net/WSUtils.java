package com.scxj.utils.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

/**
 * webservice 访问
 */

public class WSUtils {
	public static int timeout = 30000; // webservice 超时时间

	public static String wsdl = "";/*?wsdl*/
	public static String nameSpace = "";
	public static String soapAction = "";
	//	private static String nameSpace ="http://schemas.xmlsoap.org/soap/encoding";

	public static void init(String serverIp, String serverPort) {
		wsdl = "http://"+serverIp+":"+serverPort+"/sdxsService/services/sdxsServiceSOAP?wsdl";
		soapAction = "http://"+serverIp+":"+serverPort+"/sdxsService/services/sdxsServiceSOAP";
			nameSpace = "http://www.cetc7.com/sdxsService/";
	}

	/**
	 * 下载用户信息库
	 * @param userName
	 * @return
	 */
	public static String downLoadPatrolUserDB(String userName) {
		String methodName = "getUserInfo";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("userName", userName);
		SoapObject results = (SoapObject)WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}
	

	/**
	 * 生产端用户登�?
	 * @param loginId
	 * @param password
	 * @param wsdl
	 * @return
	 */
	public static String checkUser(String jh,String bz,String userName,String password){
		String methodName = "checkUser";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("USERNAME", userName);
		params.put("PASSWORD", password);
		params.put("JH", jh);
		params.put("BZ", bz);
		SoapObject results = WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "0";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();//1是正确的0是失败
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}


	/**
	 * 下载任务数据库
	 * @param userName
	 * @return
	 */
	public static String downLoadPatrolTaskDB(String userName){
		String methodName = "downloadTaskInfo";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("userName", userName);
		SoapObject results = (SoapObject)WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}
	
	
	/**
	 * 下载消缺任务
	 * @param userName
	 * @return
	 */
	public static String downLoadPatrolDefectDB(String userName) {
		String methodName = "downloadTaskDefectInfo";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("userName", userName);
		SoapObject results = (SoapObject)WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}
	
	/**
	 * 下载台账信息库
	 * @param userName
	 * @return
	 */
	public static String downLoadPatrolAssetDB(String userName){
		String methodName = "downloadAssetInfo";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("userName", userName);
		SoapObject results = (SoapObject)WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;



	}
	
	
	/**
	 * 下载巡视卡信息库
	 * 接口需要判断是否有权限下载绑卡数据库
	 * @param userName
	 * @return
	 */
	public static String downloadBindingInfo(String userName){
		String methodName = "downloadBindingInfo";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("userName", userName);
		SoapObject results = (SoapObject)WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}
	


	public static SoapObject callWS(String nameSpace, String methodName,
			Map<String, Object> params, String wsdl, int currTimeOut) {
		final String SOAP_ACTION = nameSpace + methodName;
		org.ksoap2.serialization.SoapObject request = new SoapObject(nameSpace,	methodName);
		org.ksoap2.serialization.SoapObject soapResult = null;
		if (params != null && !params.isEmpty()) {
			for (@SuppressWarnings("rawtypes")
			Iterator it = params.entrySet().iterator(); it.hasNext();) {
				// 遍历MAP
				@SuppressWarnings("unchecked")
				Map.Entry<String, Object> e = (Map.Entry<String, Object>) it.next();
				request.addProperty(e.getKey().toString(), e.getValue());
			}
		}
		/**
		 * 设置Soap版本 类型：VER1.0,VER1.1,VER1.2
		 */
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);// SOAP 1.1
		// envelope.dotNet = true;// 是否是dotNet WebService
		envelope.bodyOut = request;
		// HttpTransportSE ht = new HttpTransportSE(wsdl);
		MyAndroidHttpTransport androidHT = new MyAndroidHttpTransport(wsdl,	currTimeOut);
		try {
			// androidHT.call(SOAP_ACTION, envelope);
			androidHT.call(null, envelope);
		} catch ( Exception e) {
			e.printStackTrace();
		}
	
		if (envelope != null) {
			try {
				soapResult = (SoapObject) envelope.bodyIn;
			} catch (Exception e) {
				e.printStackTrace();
				soapResult = null;
			}
		}
		return soapResult;
	}

	/**
	 * 上传
	 * 
	 * @return
	 */
	public static SoapObject upload(String nameSpace, String methodName,
			List<PropertyInfo> params, String wsdl, int currTimeOut) {
		org.ksoap2.serialization.SoapObject request = new SoapObject(nameSpace,methodName);
		if (params != null && !params.isEmpty()) {
			for (PropertyInfo propertyInfo : params) {
				request.addProperty(propertyInfo);
			}
		}
		/**
		 * 设置Soap版本 类型：VER1.0,VER1.1,VER1.2
		 */
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		// envelope.dotNet=true;
		envelope.encodingStyle = SoapEnvelope.ENC;
		envelope.xsd = "http://www.w3.org/2001/XMLSchema";
		envelope.enc = "http://schemas.xmlsoap.org/soap/encoding/";
		envelope.xsi = " http://www.w3.org/2001/XMLSchema-instance";
		envelope.setOutputSoapObject(request);
		
		MyAndroidHttpTransport androidHttpTransport = new MyAndroidHttpTransport(wsdl, currTimeOut);
		try {
			androidHttpTransport.call(null, envelope);
			return  (SoapObject) envelope.bodyIn;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// error here
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 版本升级检查
	 * @param versionName　目前的版本号
	 * @return
	 */
	public static String checkVersion(String versionName,String appName) {
		String methodName = "updateSoftInfo";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("versionname", versionName);
		params.put("appname", appName);
		SoapObject results = WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();//1是正确的0是失败
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
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
		String methodName = "updateUserInfo";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("userName", userName);
		params.put("password", pw);
		params.put("sex", sex);
		params.put("phone", tel);
		params.put("updatetime", System.currentTimeMillis()+"");
		SoapObject results = WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();//1是正确的0是失败
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}

	/**
	 * 巡视结果信息（每次只能回传�?��任务�?
	 * @param taskId
	 * @param db
	 * @return userId,data,taskId,
	 */
	public static String uploadTaskInfo(String userName,String taskId,byte[] db) {
		String methodName = "uploadTaskInfo";
		List<PropertyInfo> params = new ArrayList<PropertyInfo>();
		PropertyInfo p = new PropertyInfo();
		p.setName("userName");
		p.setValue(userName);
		params.add(p);
		
		PropertyInfo p1 = new PropertyInfo();
		p1.setName("taskfile");
		p1.setValue(Base64.encode(db));
		params.add(p1);
		
		PropertyInfo p2 = new PropertyInfo();
		p2.setName("taskid");
		p2.setValue(taskId);
		params.add(p2);
		
		SoapObject results = WSUtils.upload(nameSpace, methodName, params, wsdl,timeout);
		String returnStatus = "";
		try {
			returnStatus =   results.getProperty("out").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}

	
	/**
	 * 上传消缺任务
	 * @param userName
	 * @param taskDefectId
	 * @param data
	 * @return
	 */
	public static String uploadTaskDefectInfo(String userName,String taskDefectId,byte[] data) {
		String methodName = "uploadTaskDefectInfo";
		List<PropertyInfo> params = new ArrayList<PropertyInfo>();
		PropertyInfo p = new PropertyInfo();
		p.setName("userName");
		p.setValue(userName);
		params.add(p);
		
		PropertyInfo p1 = new PropertyInfo();
		p1.setName("taskdefectfile");
		p1.setValue(Base64.encode(data));
		params.add(p1);
		
		PropertyInfo p2 = new PropertyInfo();
		p2.setName("taskdefectid");
		p2.setValue(taskDefectId);
		params.add(p2);
		
		SoapObject results = WSUtils.upload(nameSpace, methodName, params, wsdl,timeout);
		String returnStatus = "";
		try {
			returnStatus =   results.getProperty("out").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}


	/**
	 * 上传附件
	 * @param taskType
	 * @param taskId
	 * @param attachId
	 * @param db
	 * @return
	 */
	public static String uploadAttachInfo(String taskType, String taskId,
			String attachId, String assetId,byte[] db) {
		String methodName = "uploadAttachInfo";
		List<PropertyInfo> params = new ArrayList<PropertyInfo>();
		PropertyInfo p = new PropertyInfo();
		p.setName("taskType");
		p.setValue(taskType);
		params.add(p);
		
		PropertyInfo p2 = new PropertyInfo();
		p2.setName("taskID");
		p2.setValue(taskId);
		params.add(p2);
		
		PropertyInfo p3 = new PropertyInfo();
		p3.setName("attachID");
		p3.setValue(attachId);
		params.add(p3);
	
		PropertyInfo p4 = new PropertyInfo();
		p4.setName("devID");
		p4.setValue(assetId);
		params.add(p4);
	
		
		PropertyInfo p1 = new PropertyInfo();
		p1.setName("fileData");
		p1.setValue(Base64.encode(db));
		params.add(p1);
		
		SoapObject results = WSUtils.upload(nameSpace, methodName, params, wsdl,timeout);
		String returnStatus = "";
		try {
			returnStatus =   results.getProperty("out").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}

	public static String updateTaskStatus(String taskId, String taskType,
			String confirm) {
		String methodName = "updateTaskStatus";
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("taskType", taskType);
		params.put("taskID", taskId);
		params.put("taskConfirm", confirm);
		SoapObject results = WSUtils.callWS(nameSpace, methodName, params, wsdl, timeout);
		String returnStatus = "";
		try {
			if(results != null){
				returnStatus =   results.getProperty("out").toString();//1是正确的0是失败
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}

	public static String uploadBindingInfo(String userName, String encode) {
		String methodName = "uploadBindingInfo";
		List<PropertyInfo> params = new ArrayList<PropertyInfo>();
		PropertyInfo p = new PropertyInfo();
		p.setName("userName");
		p.setValue(userName);
		params.add(p);
		
		PropertyInfo p1 = new PropertyInfo();
		p1.setName("pointFile");
		p1.setValue(encode);
		params.add(p1);
		
		
		SoapObject results = WSUtils.upload(nameSpace, methodName, params, wsdl,timeout);
		String returnStatus = "";
		try {
			returnStatus =   results.getProperty("out").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStatus;
	}

	


}
