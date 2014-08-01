package com.scxj.utils.usb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class SocketClientThread extends Thread {
	public static final String TAG = "usb";
	public static final int timeout = 600000;
	private Socket client;
	private List<Map<String, Object>> message = null;
	private List<Object> results = null;
	private boolean isAccepted = true;
	private Object lock = new Object();

	public SocketClientThread(Socket client) {
		message = new ArrayList<Map<String, Object>>();
		results = new ArrayList<Object>();
		this.client = client;
	}

	public Object callWS(Map<String, Object> map) {
		synchronized (lock) {
			Object ret = null;
			message.add(message.size(), map);
			try {
				long be = System.currentTimeMillis();
				System.out.println(be);
				lock.wait();
				long en = System.currentTimeMillis();
				System.out.println(en);
				System.out.println(en - be);
				if (results.size() > 0) {
					ret = results.get(0);
					results.remove(0);
				}
				return ret;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return ret;
			}
		}
	}

	public Object callHttp(String url, String fileName, String localFilePath) {
		synchronized (lock) {
			Object ret = null;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", "0");
			map.put("url", url);
			map.put("http", "1");
			map.put("methodName", "downfile");
			map.put("fileName", fileName);
			map.put("localFilePath", localFilePath);
			message.add(message.size(), map);
			try {
				long be = System.currentTimeMillis();
				// 20�볬ʱʱ��
				Log.d("USB", "begin wait 20s");
				lock.wait();
				long en = System.currentTimeMillis();
				System.out.println(en);
				System.out.println(en - be);
				Log.d("USB", "end wait 20s");
				if (results.size() > 0) {
					ret = results.get(0);
					results.remove(0);
				}
				return ret;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return ret;
			}
		}
	}

	public void startAccept() throws IOException {
		if (client != null && client.isConnected()) {
			isAccepted = true;
			this.start();
		}
	}

	public void stopAccept() throws IOException {
		isAccepted = false;
		if (client != null && !client.isClosed()) {
			client.close();
		}
	}

	public boolean isConnectd() {
		return client != null && client.isConnected() && isAccepted;
	}

	@Override
	public void run() {

		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			if (client != null) {
				String currCMD = "";
				while (isAccepted) {
					try {
						if (!isConnectd()) {
							break;
						}

						out = new BufferedOutputStream(client.getOutputStream());
						in = new BufferedInputStream(client.getInputStream());

						Log.d(TAG, "wait send request to PC.....");
						if (message.size() == 0
								|| message.get(0).get("status").equals("1")) {
							Thread.sleep(1000);
							continue;
						}

						String ready = "";
						Log.d("USB", "Are you ready?");
						Utils.sendToSocket(out, "ready".getBytes());
						int retry = 0;
						do {
							ready = Utils.readFromSocket(in);
							Log.d("USB", ready);
							retry++;
						} while (!"yes".equals(ready) && retry <= 3);
						if (retry > 3) {
							continue;
						}

						/*
						 * if(message.get(0).get("http")!=null &&
						 * "1".equals(message.get(0).get("http"))){ // http
						 * ,version update
						 * Utils.sendToSocket(out,((String)message
						 * .get(0).get("url")).getBytes() ); }else{
						 * Utils.sendToSocket(out, message.get(0), "|$", "|#");
						 * }
						 */

						Utils.sendToSocket(out, message.get(0), "|$", "|#");
						message.get(0).put("status", "1");
						if (message.get(0).get("http") != null
								&& "1".equals(message.get(0).get("http"))
								&& message.get(0).get("fileName") != null
								&& message.get(0).get("localFilePath") != null) {
							Utils.readFromSocketAPK(in, (String) message.get(0)
									.get("fileName"), (String) message.get(0)
									.get("localFilePath"));
						} else {
							currCMD = Utils.readFromSocket(in);
							Log.d("USB", "readFromSocket currCMD:" + currCMD);
						}
						synchronized (lock) {
							results.add(currCMD);
							message.remove(0);
							lock.notify();
						}
					} catch (Exception e) {
						Log.e("USB", Thread.currentThread().getName() + "---->"
								+ "read write error" + e.getMessage());
						isAccepted = false;
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, Thread.currentThread().getName() + "---->"
					+ "read write error" + e.getMessage());
			e.printStackTrace();
			isAccepted = false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				this.stopAccept();
			} catch (IOException e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"
						+ "read write error" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
