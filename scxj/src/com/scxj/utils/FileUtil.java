package com.scxj.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.scxj.MyApplication;

/**
 * 
 * @author jiuhua.song
 *
 */

public class FileUtil {

	private static FileUtil _fileUtil = new FileUtil();

	public static FileUtil getInstance() {
		return _fileUtil;
	}

	/**
	 * �Ƿ����
	 * 
	 * @param filepath
	 * @return
	 */
	public static boolean isExist(String filepath)  {
		File file = new File(filepath);
		return file.isFile();
	}

	/**
	 * 删除文件夹 
	 * @param filepath
	 * @return
	 */
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		Log.d("aa", "deleteFile--------------");
		if (file.isDirectory()) {// ����Ŀ¼
			File files[] = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i].getAbsolutePath());
			}
		}

		if (!file.isDirectory()) {// ������ļ���ɾ��
			file.delete();
		} else {// Ŀ¼
			if (file.listFiles().length == 0) {// Ŀ¼��û���ļ�����Ŀ¼��ɾ��
				file.delete();
			}
		}
	}

	/**
	 * ɾ����ļ���Ŀ¼
	 * 
	 * @param filepath
	 * @return
	 */
	public static void deleteUserCacheFile(String filePath) {
		File file = new File(filePath);
		Log.d("aa", "deleteFile--------------");
		if (file.isDirectory()) {// ����Ŀ¼
			File files[] = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if(files[i].getName().equals("User"))
					continue;
				deleteFile(files[i].getAbsolutePath());
			}
		}

		if (!file.isDirectory()) {// ������ļ���ɾ��
			file.delete();
		} else {// Ŀ¼
			if (file.listFiles().length == 0) {// Ŀ¼��û���ļ�����Ŀ¼��ɾ��
				file.delete();
			}
		}
	}


	/**
	 * ��ȡ�ļ�����
	 * 
	 * @param filepath
	 * @return
	 */
	public static String readFileByString(String filePath) {

		if (!isExist(filePath)) {
			return null;
		}

		File file = new File(filePath);
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer xml = new StringBuffer();
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "UTF-8");
			char[] b = new char[4096];
			for (int n; (n = isr.read(b)) != -1;) {
				xml.append(new String(b, 0, n));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return xml.toString();
	}

	/**
	 * ��ȡ�ļ�����
	 * 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileByByte(String filePath) {

		if (!isExist(filePath)) {
			return null;
		}

		File file = new File(filePath);
		int length = (int) file.length();
		byte content[] = new byte[length];
		try{
			FileInputStream fis = new FileInputStream(file);
			fis.read(content, 0, length);
			fis.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * ����д���ļ�
	 * 
	 * @param mContext
	 * @param filepath
	 * @param data
	 * @throws FileNotFoundException 
	 */
	public static void writeFileByString(String filePath, String data) {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		File file = null;

		try {
			file = new File(filePath);
			if (!file.isFile()) {
				String path = filePath.substring(0, filePath.lastIndexOf("/"));
				Log.i("info", "-------------------------"+path);
				File dirFile = new File(path);
				dirFile.mkdirs();
				file.createNewFile();

			}

			fOut = new FileOutputStream(file);
			osw = new OutputStreamWriter(fOut, "UTF-8");
			osw.write(data);
			osw.flush();
		} catch(FileNotFoundException e){

		}catch (IOException e) {

		}finally {
			try {
				if(osw != null)
					osw.close();
				if(fOut != null)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ����д���ļ�
	 * 
	 * @param mContext
	 * @param filepath
	 * @param data
	 * @throws FileNotFoundException 
	 */
	public static void writeFileByByte(String filePath, byte content[]) {
		Log.i("info", "--------writeFileByByte=="+filePath);
		FileOutputStream fOut = null;
		File file = null;
		try {
			file = new File(filePath);
			if (!file.isFile()) {
				String path = filePath.substring(0, filePath.lastIndexOf("/"));
				File dirFile = new File(path);
				dirFile.mkdirs();
				file.createNewFile();
			}else{
				Log.i("info", "-�ļ��Ѿ�����-------file foud");
			}

			fOut = new FileOutputStream(file);
			fOut.write(content, 0, content.length);
			fOut.flush();			
		} catch(FileNotFoundException e){

		} catch (IOException e) {

		}finally {
			try {
				if(fOut != null)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* ��ȡ�ļ�����
	 * 
	 * @param filepath
	 * @return
	 */
	public static String readFileByString(Context context,String filePath) {
		String xml=null;
		Log.i("info", "------readFileByString=="+filePath);
		xml=readFileFromSdcard(filePath);
		//		if(Constant.ISSDCARD){
		//			
		//		}else {
		//			xml=this.readFileFromLocal(context, filePath);			
		//		}
		return xml;
	}


	/**
	 * �Ӱ�װ���ж�ȡ���
	 * @param context
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String readFileFromLocal(Context context,String filePath) {

		AssetManager assets = context.getAssets();         		
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer xml = new StringBuffer();
		try {
			InputStream is=assets.open(filePath);
			isr = new InputStreamReader(is, "UTF-8");
			char[] b = new char[4096];
			for (int n; (n = isr.read(b)) != -1;) {
				xml.append(new String(b, 0, n));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return xml.toString();
	}

	/**
	 * ��sdcard�ж�ȡ���
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
	public static String readFileFromSdcard(String filePath) {
		//		 filePath=Constant.ROOTPATH+filePath;
		//		 Log.i("lsw","readFileFromSdcard----"+filePath);

		if (!isExist(filePath)) {
			return null;
		}
		FileInputStream fis = null;
		String xmString = null;
		try{
			File file = new File(filePath);
			int length = (int) file.length();
			byte content[] = new byte[length];	
			fis = new FileInputStream(file);
			fis.read(content, 0, length);
			xmString = new String(content,"UTF-8");
		}catch(FileNotFoundException e){
			//			throw new FileNotFoundException();
		}catch(IOException e){
			//			throw new FileIOException("�ļ��쳣");
		}finally{
			try {
				fis.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return xmString;
	}


	public static String readFileFrom(String filePath){
		if (!isExist(filePath)) {
			return null;
		}
		FileInputStream fis = null;
		String xmString = null;
		try{
			File file = new File(filePath);
			int length = (int) file.length();
			byte content[] = new byte[length];				
			fis = new FileInputStream(file);
			fis.read(content, 0, length);
			xmString = new String(content,"UTF-8");
		}catch(FileNotFoundException e){
			//			throw new FileNotFoundException();
		}catch(IOException e){
			//			throw new FileIOException("�ļ��쳣");
		}finally{
			try {
				fis.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return xmString;
	}

	public static boolean downFile(String url, String fileName) {
		Log.i("downFile", "downFile->url=" + url + "|fileName="+ fileName);
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				String dirPath = fileName.substring(0,fileName.lastIndexOf("/")+1);
				File f = new File(dirPath);
				if (!f.exists()) {
					f.mkdirs();
				}
				File file= new File(fileName);
				if(file.exists()){
					file.delete();
				}
				file.createNewFile();
				fileOutputStream = new FileOutputStream(file);

				byte[] buf = new byte[1024];
				int ch = -1;
				while ((ch = is.read(buf)) != -1
						&& MyApplication.getInstance().threadFlag) {
					fileOutputStream.write(buf, 0, ch);
				}
				
				fileOutputStream.flush();
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}

				if (!MyApplication.getInstance().threadFlag) {
					if (file.exists()) {
						file.delete();
					}
				} 
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MyApplication.getInstance().threadFlag;
	}

	public static void copyFile(String fileName, String srcPath, String desPath,boolean flag) {
		copyFile(fileName,srcPath,fileName,desPath,flag);
	}

	public static void copyFile(String srcFileName, String srcPath, String desFileName,String desPath,boolean flag) {
		if(flag){
			if(isExist(desPath + "/" + desFileName)){
				deleteFile(desPath + "/" + desFileName);
				
			}else{
				File f = new File(desPath);
				if(!f.exists()){
					f.mkdirs();
				}
			}
			if(isExist(srcPath + "/" + srcFileName)){
				writeFileByByte(desPath+"/"+desFileName, readFileByByte(srcPath+"/"+srcFileName));
			}
		}else{
			if(!isExist(desPath + "/" + desFileName)){
				if(isExist(srcPath + "/" + srcFileName)){
					writeFileByByte(desPath+"/"+desFileName, readFileByByte(srcPath+"/"+srcFileName));
				}
			}
		}
		
	}
}
