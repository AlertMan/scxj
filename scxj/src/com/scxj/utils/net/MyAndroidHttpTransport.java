package com.scxj.utils.net;

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

public class MyAndroidHttpTransport extends HttpTransportSE// HttpTransportSE
{
	private int timeout = 5000; // 默认超时时间�?0s

	public MyAndroidHttpTransport(String url) {
		super(url);
	}

	public MyAndroidHttpTransport(String url, int timeout) {
		super(url);
		this.timeout = timeout;
	}

	@Override
	protected ServiceConnection getServiceConnection() throws IOException {
		MyServiceConnectionSE serviceConnection = new MyServiceConnectionSE(url);
		serviceConnection.setConnectionTimeOut(timeout);
		return serviceConnection;
	}

	protected ServiceConnection getServiceConnection(String url)
			throws IOException {
		MyServiceConnectionSE serviceConnection = new MyServiceConnectionSE(url);
		serviceConnection.setConnectionTimeOut(timeout);
		return serviceConnection;
	}
}