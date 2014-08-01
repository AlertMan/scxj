package com.scxj.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具�?
 * @author Administrator
 *
 */
public class DateUtil {
	/**
	 * 字符串转时间
	 * @param strDate
	 * @return
	 */
	 public static Date strToDate(String strDate) {
	       SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       ParsePosition pos = new ParsePosition(0);
	       Date strtodate = formatter.parse(strDate, pos);
	       return strtodate;
	     }
     
	 public static Date strToDate(String strDate,String format) {
	       SimpleDateFormat formatter = new SimpleDateFormat(format);
	       ParsePosition pos = new ParsePosition(0);
	       Date strtodate = formatter.parse(strDate, pos);
	       return strtodate;
	     }
	 
	 
	 /**
	  * 日期转字符串
	  * @param date
	  * @return
	  */
	 public static String dateToString(Date date){
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 return formatter.format(date);
	 }
	 
	 public static String dateToString(Date date,String format){
		 SimpleDateFormat formatter = new SimpleDateFormat(format);
		 return formatter.format(date);
	 }
	 
	 /**
	  * 得到指定过去几年的现在的时间
	  * @return
	  */
	 public static String getNowOfBeforeYears(int beforeYears,String format){
		 Date   d =  new Date(); 
		 Calendar   calendar=Calendar.getInstance(); 
		 calendar.setTime(d); 
		 calendar.add(Calendar.DATE,-beforeYears*365); 
		 Date   d2=calendar.getTime();
		 return dateToString(d2,format);
	 }
}
