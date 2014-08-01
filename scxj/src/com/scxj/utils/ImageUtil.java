package com.scxj.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;


public class ImageUtil {
	
	/**
	 * ͼƬ�����?
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	
	/**
	 * ��drawableת��BITMAP
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * IDת����BITMAP
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap drawableToBitmap(Context context,int resId){
		Drawable drawable = context.getResources().getDrawable(resId);
		return drawableToBitmap(drawable);
	}
	
	
	/**
	 * ͼƬ���ر���</br> 1��ͼƬ���ڲ��ٽ��д��?2��ͼƬ�����ڽ��д��?
	 * 
	 * @param bitmap
	 * @throws IOException
	 */

	public static String saveImage(String imageName, Bitmap bmp) throws IOException {
		String path = Const.FILEPATH + "/images/" + imageName;
		File f = new File(path);
		if (!f.isFile())// Ҫ��ŵ�ͼƬ������?
		{
			String dir = path.substring(0, path.lastIndexOf("/"));
			File dirFile = new File(dir);
			if (!dirFile.exists()) {// Ŀ¼������ʱ���˽�
				boolean isMakeDir = dirFile.mkdirs();
				if (!isMakeDir) {// �˽�Ŀ¼ʧ��
					return null;
				}
			}

			f.createNewFile();

			FileOutputStream fOut = null;
			try {
				if(bmp == null){
					return "";
				}
				fOut = new FileOutputStream(f);
				bmp.compress(Bitmap.CompressFormat.JPEG, 50, fOut);//把大小限制一�?
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (fOut != null) {
					fOut.flush();
					fOut.close();
				}
			}
		}
		return path;

	}

	public static String saveImageDirect(String imageName,byte[] data) throws IOException {
		String path = Const.FILEPATH + "/images/" + imageName;
		File f = new File(path);
		if (!f.isFile())// Ҫ��ŵ�ͼƬ������?
		{
			String dir = path.substring(0, path.lastIndexOf("/"));
			File dirFile = new File(dir);
			if (!dirFile.exists()) {// Ŀ¼������ʱ���˽�
				boolean isMakeDir = dirFile.mkdirs();
				if (!isMakeDir) {// �˽�Ŀ¼ʧ��
					return null;
				}
			}

		}
		if(f.exists()){
			f.delete();
		}
		f.createNewFile();

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			fOut.write(data,0,data.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
				if (fOut != null) {
					fOut.flush();
					fOut.close();
				}
		}
		return path;

	}

	/**
	 * ��ȡ��Ӧ��Ƶ�ͼ�?
	 * 
	 * @param path
	 *            �����·��?
	 * @return
	 */
	public static Bitmap getImage(String imageName) throws Exception {
		Bitmap bit = null;
		try {
			String path = Const.FILEPATH + "/images/" + imageName;
			File imageFile = new File(path);
			if (imageFile.exists()) {// ��ǰͼƬ����
				bit = BitmapFactory.decodeFile(path);
			}
		} catch (Exception e) {
		//	e.printStackTrace();
		//	throw new Exception();
		} 
		return bit;
	}
	
	/**
	 * �Ӱ�װ���ж�ȡͼƬ
	 * 
	 * @param context
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getImageFromLocal(Context context, String imageName)throws Exception {
		String imagePath="content/images/"+imageName;
		Bitmap bit = null;
		try {
			AssetManager assets = context.getAssets();
			InputStream is = assets.open(imagePath);
			bit = BitmapFactory.decodeStream(is);

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return bit;
	}

	// ��ȡSD��������ͼƬ
	public List<Object> getImgFromSDcard() {
		List<Object> fileList = new ArrayList<Object>(); 
		String PATH = "/sdcard/";
		File file = new File("");
		File[] files = file.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				String filename = files[i].getName();
				// ��ȡbmp,jpg,png��ʽ��ͼƬ
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".bmp")) {
					String filePath = files[i].getAbsolutePath();
					fileList.add(filePath);
				}
			} else if (files[i].isDirectory()) {
				PATH = files[i].getAbsolutePath();
				getImgFromSDcard();
			}
		}
		return fileList;
	}
	
	/**          
	 * * @param 将字节数组转换为ImageView可调用的Bitmap对象       
	 * * @param bytes        
	 * * @param opts         
	 * * @return Bitmap 
	 * */ 
	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts); 
			else 
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length); 
		return null; 
	}
}
