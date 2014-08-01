package com.scxj.utils.usb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import android.util.Log;

public class Utils {

	public static final int headlen = 9;

	public static void sendToSocket(OutputStream out, byte[] b) throws Exception {
		String length = Integer.toString(b.length);
		if (length.length() > Utils.headlen) {
			throw new Exception("超出报文最大长度");
		}
		length = Utils.padStr(length, Utils.headlen, "L", "0");
		out.write(length.getBytes());
    	out.flush();
		Log.d("USB", "发送报文head：" + length);
		out.write(b);
    	out.flush();
    	Log.d("USB", "发送报文body：" + new String(b, "UTF-8"));
    }

	public static void sendToSocket(OutputStream out, Map<String, Object> map, String splitterRecord, String splitterColum) throws Exception {
		int len = 0;
		for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> e = (Map.Entry<String, Object>) it.next();
			String value = e.getValue().toString();
			String key = e.getKey();
			len += (key.getBytes("UTF-8").length + splitterColum.length() + value.getBytes("UTF-8").length);
			if (it.hasNext()) {
				len += splitterRecord.length();
			}
		}
		String length = Integer.toString(len);
		if (length.length() > Utils.headlen) {
			throw new Exception("超出报文最大长度");
		}
		length = Utils.padStr(length, Utils.headlen, "L", "0");
		out.write(length.getBytes());
    	out.flush();
		Log.d("USB", "发送报文head：" + length);
		for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> e = (Map.Entry<String, Object>) it.next();
			String value = e.getValue().toString();
			String key = e.getKey();

			out.write(key.getBytes("UTF-8"));
			out.write(splitterColum.getBytes("UTF-8"));
			out.write(value.getBytes("UTF-8"));
			if (it.hasNext()) {
				out.write(splitterRecord.getBytes("UTF-8"));
			}
		}
    	out.flush();
	}
	
	public static String readFromSocket(InputStream in) throws Exception {  
		int MAX_BUFFER_BYTES = 1024;
        String recv = "";
        byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
        ByteArrayOutputStream headbuffer = new ByteArrayOutputStream(Utils.headlen);
    	int numReadedBytes = 0;
    	// 包头8位表示包体长度
    	int b = 0;
    	while ((b = in.read()) != -1) {
    		headbuffer.write(b);
    		if (headbuffer.size() == Utils.headlen) {
    			break;
    		}
    	}
    	if (headbuffer.size() < Utils.headlen) {
        	Log.d("USB", "长度接收错误：" + headbuffer.size());
        	return recv;
    	}
    	
    	int size = Integer.parseInt(headbuffer.toString());
    	Log.d("USB", "收到报文head：" + size);
    	
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(size);
    	while (buffer.size() < size) {
    		int toread = 0;
    		int remainSize = size - buffer.size();
    		if (remainSize - 1024 >= 0) {
    			toread = 1024;
    		}
    		else {
    			toread = remainSize;
    		}
    		if ((numReadedBytes = in.read(tempbuffer, 0, toread)) != -1) {
				buffer.write(tempbuffer, 0, numReadedBytes);
			}
    	}
        tempbuffer = null;
        recv = buffer.toString("utf-8");
        Log.d("USB", "收到报文body：" + recv);
        return recv;
    } 
	public static boolean readFromSocketAPK(InputStream in,String fileName, String localFielPath) throws Exception { 
		Log.d("USB","readFromSocketfile ...");
		ByteArrayOutputStream headbuffer = new ByteArrayOutputStream(Utils.headlen);
		// 包头8位表示包体长度
		int b = -1;
		while ((b = in.read()) != -1) {
			headbuffer.write(b);
			if (headbuffer.size() == Utils.headlen) {
				break;
			}
		}
		if (headbuffer.size() < Utils.headlen) {
			Log.d("USB", "长度接收错误：" + headbuffer.size());
			return false;
		}
		
		int size = Integer.parseInt(headbuffer.toString());
		Log.d("USB", "收到报文head：" + size);
		
		File file = new File(localFielPath);
		if(!file.exists()){
			file.mkdirs();
		}
		String filePath = localFielPath + "/" + fileName;
		File tmpFile = new File(filePath);
		if(tmpFile.exists()){
			tmpFile.delete();
			tmpFile.createNewFile();
		}
		
		
		FileOutputStream fileOut = new FileOutputStream(tmpFile);
		b = -1;
		int totalReaded = 0;
		byte[] tempbuffer = new byte[1024*8];
		b=in.read(tempbuffer, 0, 1024*8);
		try {
			while(b!=-1){
				fileOut.write(tempbuffer, 0, b);
				totalReaded += b;
				if(totalReaded>=size){
					break;
				}
				b = in.read(tempbuffer, 0, 1024*8);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fileOut.flush();
		fileOut.close();
		Log.d("USB", "文件 下载完成");
		return true;
	} 
	
	public static Map<String, Object> convertStringToMap(String msg, String splitterRecord, String splitterColum) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (msg != null) {
			String[] records = msg.split(splitterRecord);
			for (String r : records) {
				String[] kvs = r.split(splitterColum);
				if (kvs.length == 2) {
					map.put(kvs[0], kvs[1]);
				}
				else if(kvs.length == 1) {
					map.put(kvs[0], "");
				}
			}
		}
		return map;
	}
	/**
	 * 
	 * @param map
	 * @param splitterRecord
	 * @param splitterColum
	 * @return
	 *  如：ydtf_fzr@雷昊|
	 */
	public static byte[] convertMapToByte(Map<String, Object> map, String splitterRecord, String splitterColum) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> e = (Map.Entry<String, Object>) it.next();
			String value = e.getValue().toString();
			sb.append(e.getKey()).append(splitterColum).append(value).append(splitterRecord);
		}
		if (sb.lastIndexOf(splitterRecord) == sb.length() - 1) {
			sb.deleteCharAt(sb.lastIndexOf(splitterRecord));
		}
		try {
			return sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	/**
	 * 填充字符串
	 * @param str 源字符串
	 * @param len 总长度
	 * @param loc 补的位置（左补“L”、右补“R”）
	 * @param padding 填充符号（单个字符）
	 * 默认补充在右边
	 */
	public static String padStr(String str, int len, String loc, String padding)
	{
		byte[] b = str.getBytes();
		if (len > b.length)
		{
			String padsrt = "";
			for (int i=0; i < (len - b.length); i++ )
			{
				padsrt += padding;
			}
			
			if (loc.equals("L"))
			{
				str = padsrt + str;
			}
			else
			{
				str += padsrt;
			}
		}
		return str;
	}
}
