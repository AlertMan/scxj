/**
 * 
 */
package com.scxj.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * @author think
 *
 */
public class DBUtil {

	/**
	 * cursor对象转换成Object
	 * @param cursor
	 * @param clazz
	 * @return
	 */
	public static Object cursorToObject(Cursor cursor,
			Class clazz) {
		if(cursor == null || clazz == null){
			Log.e("cursorToObject","cursorToObject 参数为null");
			return null;
		}
		
		int numCols = cursor.getColumnCount();
		String[] colNames = new String[numCols];
		for (int i = 0; i < numCols; i++) {
			colNames[i] = cursor.getColumnName(i);
		}
	
		Method[] ms = clazz.getMethods();
		Object object = null;
		try {
			object = clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < colNames.length; i++) {
			String colName = colNames[i];
			String methodName = "set" + colName;
			
			for (Method m : ms) {
				if (methodName.equalsIgnoreCase(m.getName())) {
					try {
						String value = cursor.getString(cursor.getColumnIndex(colName));
						if(StringUtils.isNull(value)){
							value = "";
						}
						m.invoke(object, value);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					Log.d("cursorToObject", colName + "||" + methodName+"||"+cursor.getString(cursor.getColumnIndex(colName)) );
					break;
				}
			}
		}
		return object;
	}
	
	/**
	 * 通过对象组装�?��cv，方便于更新与插入数�?这个方法现在只能只简单属性进行操�?
	 * @return
	 */
	public static ContentValues objectToCV(Object o){
		if(o == null ){
			Log.e("objectToCV","objectToCV 参数为null");
			return null;
		}
		Method[] ms = o.getClass().getMethods();
		Field[] fileds = o.getClass().getDeclaredFields();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < fileds.length; i++) {
			Field filed = fileds[i];
			String methodName = "get" + filed.getName();
			
			for (Method m : ms) {
				if (methodName.equalsIgnoreCase(m.getName())) {
					try {
						Object value = m.invoke(o);
						if(value != null){
							if(value instanceof String && /*!"".equals(value.toString()) && */!"null".equalsIgnoreCase(value.toString())){
								cv.put(filed.getName(), value.toString());
							}
							
//						Log.d("objectToCV","key="+filed.getName()+"|value="+value.toString());
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		return cv;
	}
	
	/**
	 * 争取通用,后面仿HB
	 * @param o
	 * @param clazz
	 * @return
	 */
	public static ContentValues objectToCV(Object o,Class clazz){
		if(o == null || clazz == null){
			Log.e("objectToCV","objectToCV 参数为null");
			return null;
		}
			
		Method[] ms = o.getClass().getMethods();
		Field[] fileds = o.getClass().getDeclaredFields();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < fileds.length; i++) {
			Field filed = fileds[i];
			String methodName = "get" + filed.getName();
			
			for (Method m : ms) {
				if (methodName.equalsIgnoreCase(m.getName())) {
					try {
						Object value = m.invoke(o);
						if(value != null && /*!"".equals(value.toString()) && */!"null".equalsIgnoreCase(value.toString())){
							cv.put(filed.getName(), value.toString());
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		return cv;
	}	
}
