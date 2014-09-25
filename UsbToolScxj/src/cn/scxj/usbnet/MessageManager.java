package cn.scxj.usbnet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import cn.scxj.substationPatrol.util.net.webservice.Base64;
import cn.scxj.substationPatrol.util.net.webservice.WSUtils;
import cn.scxj.utils.Utils;

public class MessageManager extends Thread{
	
	private static MessageManager messageManager;
	
	private static Socket socket = null;
	private boolean isRunning = true;
	private static BufferedOutputStream out = null;
	private static BufferedInputStream in  = null;
	private static String errorMsg;
	
	private MessageManager() {
	}
	
	public static MessageManager getMessageManager() {
		if (messageManager == null) {
			messageManager = new MessageManager();
		}
		return messageManager;
	}
	public static MessageManager getNewMessageManager() {
		if (messageManager != null) {
			messageManager = null;
		}
		messageManager = new MessageManager();
		return messageManager;
	}
	
	public static void startManager() {
		getNewMessageManager().isRunning = true;
		getMessageManager().start();
	}
	
	public static void stopManager() {
		getMessageManager().isRunning = false;
		MainScreen.getMainScreen().setConnectState(false, false);
    	MainScreen.getMainScreen().setStatus(Status.STOP, null);
		// 只需关闭流，即可等待线程结束
		getMessageManager().shutdownStream();
	}
	
	@Override
	public void run() {
		Logger.info("启动连接");
		MainScreen.getMainScreen().setConnectState(false, false);
		
		if (!this.startadb()) {
			MainScreen.getMainScreen().setConnectState(false);
			return;
		}
		
		try {
			boolean needConnect = true;
            while(isRunning){
            	if (needConnect) {
	                socket = new Socket("127.0.0.1", MainScreen.port);
	            	Logger.info("正在连接设备..."); 
	            	
	            	in = new BufferedInputStream(socket.getInputStream());  
	            	out = new BufferedOutputStream(socket.getOutputStream());
	            	Logger.info("连接设备成功");
	            	MainScreen.getMainScreen().setConnectState(true);
            	}
            	if (!socket.isConnected() || socket.isInputShutdown()) {
            		Logger.warn("设备已断开");
            		break;
            	}
            	Logger.info("等待命令...");
            	MainScreen.getMainScreen().setStatus(Status.USING, null);
                String recCommand = Utils.readFromSocket(in); 
                // 如果是点击按钮主动关闭可以检测到，不用再进行次连接重试了；但是直接拔USB线无法检测到，需要重新连接一次进行测试
                if((recCommand == null || recCommand.length()==0) && !socket.isInputShutdown()){
                	MainScreen.getMainScreen().setStatus(Status.WAIT, null);
                	needConnect = true;
            		Logger.warn("无法连接移动端应用");
            		closeSocket();
            		Logger.warn("5秒后将重新进行连接");
                	Thread.sleep(5000);
                	continue;
                }else if("ready".equals(recCommand)){
                	needConnect = false;
                	Logger.debug("收到：ready");
                	Utils.sendToSocket(out, "yes".getBytes());
                	Logger.debug("发送：yes");
                }else if(recCommand.startsWith("http://")){
                	needConnect = false;
                	String re = getContent(recCommand);
                } else {
                	needConnect = false;
                	Map<String, Object> map = Utils.convertStringToMap(recCommand, "\\|\\$", "\\|#");
                	String methodName = (String)map.get("methodName");
                	Logger.info("测试打印收到命令：" + map.toString());
                	if("downfile".equals(methodName)){
                		getFile((String)map.get("url"), (String)map.get("fileName"));
                	}else if("updateSoftInfo".equals(methodName)) {
                		updateSoftInfo((String)map.get("nameSpace"), (String)map.get("wsdl"),(String)map.get("versionName"),(String)map.get("appName"));
                	}else if ("checkUser".equals(methodName)) {
                		checkUser((String)map.get("nameSpace"), (String)map.get("wsdl"), (String)map.get("USERNAME"), 
                				(String)map.get("PASSWORD"), (String)map.get("JH"), (String)map.get("BZ"));
            		}else if("getUserInfo".equals(methodName)){
            			getUserInfo((String)map.get("nameSpace"),(String)map.get("wsdl"),(String)map.get("userName"));
            		}else if ("downloadTaskInfo".equals(methodName)) {
                		
                		downloadTaskInfo((String)map.get("nameSpace"), (String)map.get("wsdl"),(String)map.get("userName"));
                	}else if ("downloadTaskDefectInfo".equals(methodName)) {
                		downloadTaskDefectInfo((String)map.get("nameSpace"), (String)map.get("wsdl"),(String)map.get("userName"));
            		}else if ("downloadAssetInfo".equals(methodName)) {
            			downloadAssetInfo((String)map.get("nameSpace"), (String)map.get("wsdl"),(String)map.get("userName"));
            		}else if ("uploadTaskInfo".equals(methodName)) {//上传任务结果库
            			uploadTaskInfo((String)map.get("nameSpace"),(String)map.get("wsdl"),(String)map.get("userName"),(String)map.get("taskid"),(String)map.get("fileData"));
            		}else if("uploadTaskDefectInfo".equals(methodName)){//上传消缺任务结果库
            			uploadTaskDefectInfo((String)map.get("nameSpace"),(String)map.get("wsdl"),(String)map.get("userName"),(String)map.get("taskdefectid"),(String)map.get("fileData"));
            		}else if("uploadAttachInfo".equals(methodName)){//上传附件
            			uploadAttachInfo((String)map.get("nameSpace"),(String)map.get("wsdl"),(String)map.get("taskType"),(String)map.get("taskId"),(String)map.get("attachId"),(String)map.get("assetId"),(String)map.get("fileData"));
            		}else if("updateUserInfo".equals(methodName)){//修改用户信息
            			updateUserInfo((String)map.get("nameSpace"),(String)map.get("wsdl"),(String)map.get("userName"),(String)map.get("password"),(String)map.get("sex"),(String)map.get("phone"));
            		}else if("updateTaskStatus".equals(methodName)){
            			updateTaskStatus((String)map.get("nameSpace"),(String)map.get("wsdl"),(String)map.get("taskId"),(String)map.get("taskType"),(String)map.get("taskConfirm"));
            		}else {
                		Utils.sendToSocket(out, "".getBytes());
                	}
                }
            }
        } catch (java.net.ConnectException ce) {
        	Logger.warn("请先使用USB数据线连接设备并确认已开启设备的USB调试模式");
        	MainScreen.getMainScreen().setStatus(Status.STOP, null);
        } catch (java.net.SocketException se) {
        	Logger.warn("系统异常：" + se.getMessage());
			Logger.warn("请关闭豌豆荚等手机工具重试连接");
			MainScreen.getMainScreen().setStatus(Status.ERROR, "连接已断开\r\n请关闭豌豆荚等手机工具重试连接");
        }
		catch (Exception e) {  
			Logger.warn("系统异常：" + e.getMessage());
			MainScreen.getMainScreen().setStatus(Status.ERROR, e.getMessage());
        } finally {  
            this.closeSocket();
            MainScreen.getMainScreen().setConnectState(false);
    		stopadb();
        }  
	}


	/**
	 * 任务闭环
	 * @param nameSpace
	 * @param wsdl
	 * @param taskId
	 * @param taskType
	 * @param taskConfirm
	 */
	private void updateTaskStatus(String nameSpace, String wsdl,
			String taskId, String taskType, String taskConfirm) throws Exception{
		try {
			WSUtils.nameSpace = nameSpace;
			WSUtils.wsdl = wsdl;
			String taskdata = WSUtils.updateTaskStatus(taskId, taskType, taskConfirm);
			Utils.sendToSocket(out, taskdata.getBytes("UTF-8"));
		} catch (Exception e) {
			Logger.warn("连接后台服务器异常：" + e.getMessage());
			Utils.sendToSocket(out, "".getBytes("UTF-8"));
		}
}

	/**
	 * 更新用户信息
	 * @param nameSpace
	 * @param wsdl
	 * @param userName
	 * @param password
	 * @param sex
	 * @param phone
	 * @throws Exception
	 */
	private void updateUserInfo(String nameSpace, String wsdl, String userName,
			String password, String sex, String phone) throws Exception{
		try {
			WSUtils.nameSpace = nameSpace;
			WSUtils.wsdl = wsdl;
			String taskdata = WSUtils.updateUserInfo(userName,password,sex,phone);
			Utils.sendToSocket(out, taskdata.getBytes("UTF-8"));
		} catch (Exception e) {
			Logger.warn("连接后台服务器异常：" + e.getMessage());
			Utils.sendToSocket(out, "".getBytes("UTF-8"));
		}
}

	/**
	 * 获取台账数据
	 * @param nameSpace
	 * @param wsdl
	 * @param userName
	 */
	private void downloadAssetInfo(String nameSpace, String wsdl, String userName) throws Exception {
		try {
			WSUtils.nameSpace = nameSpace;
			WSUtils.wsdl = wsdl;
			String taskdata = WSUtils.downLoadPatrolAssetDB(userName);
			Utils.sendToSocket(out, taskdata.getBytes("UTF-8"));
		} catch (Exception e) {
			Logger.warn("连接后台服务器异常：" + e.getMessage());
			Utils.sendToSocket(out, "".getBytes("UTF-8"));
		}
}

	/**
	 * 获取用户信息库
	 * @param nameSpace
	 * @param wsdl
	 * @param userName
	 */
	private void getUserInfo(String nameSpace, String wsdl, String userName) throws Exception {
			String returnStatus = "";
			try {
				WSUtils.nameSpace = nameSpace;
				WSUtils.wsdl = wsdl;
				returnStatus = WSUtils.downLoadPatrolUserDB(userName);
				Utils.sendToSocket(out, returnStatus.getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
				Utils.sendToSocket(out, "".getBytes("UTF-8"));
			}
	}

	private void updateSoftInfo(String nameSpace, String wsdl, String versionName,
			String appName) throws Exception {
		if (wsdl != null && nameSpace != null) {
			String returnStatus = "";
			try {
				WSUtils.nameSpace = nameSpace;
				WSUtils.wsdl = wsdl;
				returnStatus = WSUtils.checkVersion(versionName,appName);
				Utils.sendToSocket(out, returnStatus.getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
				Utils.sendToSocket(out, "".getBytes("UTF-8"));
			}
		}
	
	}
	
	/**
	 * 上传附件
	 * @param nameSpace
	 * @param wsdl
	 * @param taskType
	 * @param taskId
	 * @param attachId
	 * @param data
	 * @throws Exception
	 */
	private void uploadAttachInfo(String nameSpace, String wsdl,String taskType, String taskId, String attachId, String assetId,String data)throws Exception {
		try {
			WSUtils.nameSpace = nameSpace;
			WSUtils.wsdl = wsdl;
			String result = WSUtils.uploadAttachInfo(taskType, taskId, attachId,assetId,data);
			Logger.debug("后台返回：" + result);
			if (result == null) {
				result = "";
			}
			Utils.sendToSocket(out, result.getBytes("UTF-8"));
		} catch (Exception e) {
			Logger.warn("连接后台服务器异常：" + e.getMessage());
			Utils.sendToSocket(out, "".getBytes("UTF-8"));
		}
}
 
	/**
	 * 生产端用户登录
	 * @param userName
	 * @param password
	 * @param wsdl
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	public static void checkUser(String nameSpace, String wsdl, String userName, String password, String jh, String bz) throws UnsupportedEncodingException, Exception{
			String returnStatus = "";
			try {
				WSUtils.nameSpace = nameSpace;
				WSUtils.wsdl = wsdl;
				returnStatus = WSUtils.checkUser(jh,bz,userName, password);
				Utils.sendToSocket(out, returnStatus.getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
				Utils.sendToSocket(out, "".getBytes("UTF-8"));
			}
	}
	
	/**
	 * 任务下载数据
	 * @param userName 用户名
	 * @param wsdl
	 * @param nameSpace 返回参数标签名
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	public static void downloadTaskInfo(String nameSpace, String wsdl,String userName) throws UnsupportedEncodingException, Exception{
			try {
				WSUtils.nameSpace = nameSpace;
				WSUtils.wsdl = wsdl;
				String taskdata = WSUtils.downLoadPatrolTaskDB(userName);
				Utils.sendToSocket(out, taskdata.getBytes("UTF-8"));
			} catch (Exception e) {
				Logger.warn("连接后台服务器异常：" + e.getMessage());
				Utils.sendToSocket(out, "".getBytes("UTF-8"));
			}
	}
	
	/**
	 * 下载消缺任务库
	 * @param nameSpace
	 * @param wsdl
	 * @param userName
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static void downloadTaskDefectInfo(String nameSpace, String wsdl,String userName) throws UnsupportedEncodingException, Exception{
			try {
				WSUtils.nameSpace = nameSpace;
				WSUtils.wsdl = wsdl;
				String taskdata = WSUtils.downLoadPatrolDefectDB(userName);
				if (taskdata == null) {
					taskdata = "";
				}
			//	Logger.warn("getViewTaskList：" + taskdata.toString());
				Utils.sendToSocket(out, taskdata.getBytes("UTF-8"));
			} catch (Exception e) {
				Logger.warn("连接后台服务器异常：" + e.getMessage());
				Utils.sendToSocket(out, "".getBytes("UTF-8"));
			}
	}
	
	
	/**
	 * 回传任务信息
	 * @param userName
	 * @param fileData
	 * @param wsdl
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	public static void uploadTaskInfo(String nameSpace,String wsdl,String userName,String taskId,String fileData) throws UnsupportedEncodingException, Exception{
		if (nameSpace != null && wsdl != null && userName != null ) {
			try {
				WSUtils.nameSpace = nameSpace;
				WSUtils.wsdl = wsdl;
				String result = WSUtils.uploadTaskInfo( userName,taskId,fileData);
				Logger.debug("后台返回：" + result);
				if (result == null) {
					result = "";
				}
				Utils.sendToSocket(out, result.getBytes("UTF-8"));
			} catch (Exception e) {
				Logger.warn("连接后台服务器异常：" + e.getMessage());
				Utils.sendToSocket(out, "".getBytes("UTF-8"));
			}
		}	
	}
	
	/**
	 * 回传消缺任务信息
	 * @param userName
	 * @param fileData
	 * @param wsdl
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 */
	public static void uploadTaskDefectInfo(String nameSpace,String wsdl,String userName,String taskdefectid,String fileData) throws UnsupportedEncodingException, Exception{
			try {
				WSUtils.nameSpace = nameSpace;
				WSUtils.wsdl = wsdl;
				String result = WSUtils.uploadTaskDefectInfo( userName,taskdefectid,fileData);
				Logger.debug("后台返回：" + result);
				if (result == null) {
					result = "";
				}
				Utils.sendToSocket(out, result.getBytes("UTF-8"));
			} catch (Exception e) {
				Logger.warn("连接后台服务器异常：" + e.getMessage());
				Utils.sendToSocket(out, "".getBytes("UTF-8"));
			}
	}
	
	public static void downloadFileAndSave(String urls) throws UnsupportedEncodingException, Exception{
		downloadFileAndSave(urls,true);
	}
	
	public static void downloadFileAndSave(String urls,boolean isCheck) throws UnsupportedEncodingException, Exception{
		if (urls != null) {
			try {
				Utils.sendToSocket(out, null);
			} catch (Exception e) {
				Logger.warn("连接后台服务器异常：" + e.getMessage());
				Utils.sendToSocket(out, "".getBytes("UTF-8"));
			}
		}	
	}
	
	/**
	 * 获取url路径指定的内容
	 * @param urlpath url路径
	 * @throws Exception
	 */
	public static String getContent(String urlpath) throws Exception{
		StringBuffer sb = new StringBuffer();
		InputStream inputStream = null;
		FileOutputStream fileOut = null;
		try {
			URL url = new URL(urlpath);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(20*1000);
			byte[] buffer = new byte[1024];
			int len = 0;
			if(conn.getResponseCode() == 200){
				inputStream =  conn.getInputStream();
				File file = new File("./SubstationPatrol.apk");
				if(!urlpath.endsWith("version.xml")){
					if(file.exists()){
						file.delete();
					}
					
					file.createNewFile();
					fileOut = new FileOutputStream(file, false);
					System.out.println("begin download apk \n path is "+file.getAbsolutePath());
				}
				while((len = inputStream.read(buffer))!=-1){
					if(urlpath.endsWith("version.xml")){
						sb.append(new String(buffer, 0, len));
					}else{
						fileOut.write(buffer, 0, len);
					}
				}
				if(urlpath.endsWith("version.xml")){
					System.out.println("send version  To  MB:\n"+sb.toString());
					Utils.sendToSocket(out, sb.toString().getBytes("UTF-8"));
				}else{
					if(fileOut!=null){
						fileOut.flush();
						fileOut.close();
					}
					System.out.println("end download apk");
					Utils.sendAPKToSocket(out,file);
				}
			}
		} catch (Exception e) {
			Logger.warn("连接后台服务器异常：" + e.getMessage());
			Utils.sendToSocket(out, "".getBytes("UTF-8"));
		}finally{
			inputStream.close();
		}
		return sb.toString();
	}
	/**
	 * 获取url路径指定的内容
	 * @param urlpath url路径
	 * @throws Exception
	 */
	public static String getFile(String urlpath,String fileName) throws Exception{
		InputStream inputStream = null;
		FileOutputStream fileOut = null;
		try {
			String preUrl = urlpath.substring(0,urlpath.lastIndexOf("/")+1);
			String sufUrl = urlpath.substring(urlpath.lastIndexOf("/")+1,urlpath.length());
			String enCodeSufUrl = URLEncoder.encode(sufUrl, "UTF-8"); 
			String destUrl = preUrl+enCodeSufUrl;
			
			URL url = new URL(destUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(20*1000);
			byte[] buffer = new byte[8192];
			int len = 0;
			if(conn.getResponseCode() == 200){
				inputStream =  conn.getInputStream();
				File fileDir = new File("C:\\temp\\");
				if(!fileDir.exists()){
					fileDir.mkdirs();
				}
				File file = new File(fileDir,fileName);
				if(file.exists()){
					file.delete();
				}
				file.createNewFile();
				fileOut = new FileOutputStream(file, false);
				 
					System.out.println("begin download file, \n path is "+file.getAbsolutePath()  + "url="+urlpath);
				while((len = inputStream.read(buffer))!=-1){
						fileOut.write(buffer, 0, len);
				}
			 
					if(fileOut!=null){
						fileOut.flush();
						fileOut.close();
					}
					System.out.println("end download file");
					Utils.sendAPKToSocket(out,file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.warn("连接后台服务器异常：" + e.getMessage());
			Utils.sendToSocket(out, "".getBytes("UTF-8"));
		}finally{
			if(inputStream != null){
				inputStream.close();
			}
		}
		return "OK".toString();
	}
	private boolean startadb() {
		try {
			stopOtherTool();
			stopadb();
//			Runtime.getRuntime().exec("./lib/platform-tools/adb forward tcp:" + MainScreen.port + " tcp:10086");
	        final Process p = Runtime.getRuntime().exec("./lib/platform-tools/adb forward tcp:" + MainScreen.port + " tcp:10086");
	        // 必须用线程把input流和error流都读取出来
	        new Thread(new Runnable() {  
	            public void run() {  
	                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));  
	                String line = null;
	                try {
						while((line = br.readLine()) != null) {
							Logger.debug(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
	                
	            }  
	        }).start(); // 启动单独的线程来清空process.getInputStream()的缓冲区
	        new Thread(new Runnable() {  
	            public void run() {  
	                BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));  
	                String line = null;
	                try {
						while((line = br.readLine()) != null) {
							Logger.error(line);
							errorMsg = line;
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
	            }  
	        }).start(); // 启动单独的线程来清空process.getInputStream()的缓冲区
	        
			p.waitFor();
			if(p.exitValue() != 0) {
				if (errorMsg == null) {
					errorMsg = "系统异常，请重试";
				}
				String eMsg = errorMsg;
				if (errorMsg.contains("device not found")) {
					eMsg = "设备未找到\r\n请先使用USB数据线连接设备并确认已开启设备的USB调试模式";
				}
				else if (errorMsg.contains("device offline")) {
					eMsg = "设备已断开\r\n请重新连接USB数据线";
				}
				else if (errorMsg.contains("cannot bind socket")) {
					eMsg = "端口已占用\r\n请更换端口重新尝试";
				}
				else if (errorMsg.contains("unknown host service")) {
					eMsg = "请关闭豌豆荚等手机工具后再重试连接";
				}
				else if (errorMsg.contains("more than one device and emulator")) {
					eMsg = "请只连接单个设备";
				}
				throw new Exception(eMsg);
			}
	        sleep(5000);
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        MainScreen.getMainScreen().setStatus(Status.ERROR, e.getMessage());
	        Logger.error("启动adb失败");
	        Logger.error(e.getMessage());
	        return false;
	    }
	}
	
	public static void stopadb() {
		try {
			Runtime.getRuntime().exec("./lib/platform-tools/adb kill-server");
			sleep(500);
			Runtime.getRuntime().exec("taskkill /F /im adb.exe");
			sleep(500);
			Runtime.getRuntime().exec("taskkill /F /im adb.exe");
			sleep(500);
	    } catch (Exception e) {
	        e.printStackTrace();
	        Logger.error("关闭adb失败");
	        Logger.error(e.getMessage());
	    }
	}
	
	private static void stopOtherTool() {
		try {
			// 关闭豌豆荚
			Runtime.getRuntime().exec("./lib/platform-tools/adb kill-server");
			Runtime.getRuntime().exec("taskkill /F /im wandoujia*");
			Runtime.getRuntime().exec("taskkill /F /im wandoujia*");
			Runtime.getRuntime().exec("taskkill /F /im wandoujia*");
			Runtime.getRuntime().exec("taskkill /F /im wandoujia_helper*");
			
			// 关闭91
			Runtime.getRuntime().exec("taskkill /F /im Android*");
			Runtime.getRuntime().exec("taskkill /F /im Android*");
			Runtime.getRuntime().exec("taskkill /F /im Android*");
			
			// 关闭腾讯手机管家
			Runtime.getRuntime().exec("taskkill /F /im QQPhoneManager*");
			Runtime.getRuntime().exec("taskkill /F /im QQPhoneManager*");
			Runtime.getRuntime().exec("taskkill /F /im QQPhoneManager*");
			
			// 关闭360手机管家
			Runtime.getRuntime().exec("taskkill /F /im 360Mobile*");
			
			//关闭金山手机助手 
			Runtime.getRuntime().exec("taskkill /F /im sjk_daemon*");
			
			//关闭其它的手机助手进程
			Runtime.getRuntime().exec("taskkill /F /im tadb*");
			sleep(1000);
	    } catch (Exception e) {
	        e.printStackTrace();
	        Logger.error("关闭其他工具失败");
	        Logger.error(e.getMessage());
	    }
	}
	
	private void closeSocket() {
		try {    
			if (socket != null) {
	            if(out!=null){
	            	out.close();
	            	out = null;
	            }
	            if(in!=null){
	            	in.close();
	            	in = null;
	            }
	            if (!socket.isClosed()) {  
	                socket.close();
	                socket = null;
	            }
                Logger.info("设备连接已断开");
			}
        } catch (IOException e) {  
        	Logger.warn("关闭连接异常：" + e.getMessage());
        }
	}
	
	private void shutdownStream() {
		try {    
			if (socket != null && !socket.isClosed()) {
				if (!socket.isInputShutdown()) {
					socket.shutdownInput();
				}
				if (!socket.isOutputShutdown()) {
					socket.shutdownOutput();
				}
			}
        } catch (IOException e) {  
        	Logger.warn("关闭连接异常：" + e.getMessage());
        }
	}
}
