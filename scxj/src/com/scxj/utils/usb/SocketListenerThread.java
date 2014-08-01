package com.scxj.utils.usb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import android.util.Log;

public class SocketListenerThread extends Thread{

	public static final String TAG = "usb";
	final int SERVER_PORT = 10086;
	private boolean isListening = false;
	private ServerSocket serverSocket = null;
	private SocketClientThread socketClientThread = null;
	
	public void startListen() throws IOException {
		isListening = true;
		serverSocket = new ServerSocket(SERVER_PORT);
		this.start();
	}
	
	public void stopListen() throws IOException {
		isListening = false;
		if (socketClientThread != null) {
			socketClientThread.stopAccept();
			socketClientThread = null;
		}
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
	}
	
	public boolean isListening() {
		return isListening;
	}

	public Object callWS(Map<String, Object> map) {
		Object obj = null;
		if (socketClientThread != null ) {
			if (socketClientThread.isConnectd()) {
				obj = socketClientThread.callWS(map);
			}
		}
		return obj;
	}
	
	public Object callHttp(String url,String fileName, String localFilePath){
		if(url!=null){
			int count  = 0;
			while(socketClientThread == null && count<3){
				Log.d("USB","socketClientThread is null ,try "+count );
				try {
					Thread.sleep(5000);
					count++;
				} catch (InterruptedException e) {}
			}
			if (socketClientThread.isConnectd()) {
				return socketClientThread.callHttp(url,fileName,localFilePath);
			}
		}
		return null;
	}
	
	@Override
	public void run() {
		Log.d(TAG, Thread.currentThread().getName() + "---->"
				+ " doListen() START");
		try {
			Log.d(TAG, Thread.currentThread().getName() + "---->"
					+ " doListen() new serverSocket");

			while (isListening) {
				Log.d(TAG, Thread.currentThread().getName() + "---->"
						+ " doListen() listen");

				Socket client = serverSocket.accept();
				
				if (socketClientThread != null) {
					socketClientThread.stopAccept();
					socketClientThread = null;
				}
				socketClientThread = new SocketClientThread(client);
				socketClientThread.startAccept();
			}
		} catch (IOException e1) {
			isListening = false;
			Log.v(TAG, Thread.currentThread().getName()
					+ "---->" + "new serverSocket error");
		} finally {
			try {
				this.stopListen();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}