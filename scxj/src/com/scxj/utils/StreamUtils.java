package com.scxj.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StreamUtils {
	/**
	 * 文件输入�?to byte[]
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static byte[] file2ByteArray(File file) throws IOException {
		FileInputStream fileInput = new FileInputStream(file);
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = fileInput.read(buffer)) !=-1 ){
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		fileInput.close();
		return outSteam.toByteArray();
	}
}
