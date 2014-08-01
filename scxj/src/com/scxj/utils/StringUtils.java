/**
 * 
 */
package com.scxj.utils;

import java.math.BigDecimal;
import java.util.UUID;

import android.os.SystemClock;
import android.util.Log;

/**
 * @author think
 * 
 */
public final class StringUtils {
	/**
	 * 解决LogCat的缓存导致的大字符串不能全部输出问题，默认输出长度为10000个字�?
	 * 
	 * @param source
	 */
	public static void printToLogCat(String source) {
		printToLogCat(source, source.length());
	}

	/**
	 * 解决LogCat的缓存导致的大字符串不能全部输出问题
	 * 
	 * @param source
	 * @param showLen
	 *            输出长度
	 */
	public static void printToLogCat(String source, int showLen) {
		if (source != null && source.length() > 0) {
			final int strLen = source.length();
			final int unitCnt = showLen;
			int cnt = strLen / unitCnt;
			for (int i = 0; i < cnt; i++) {
				System.out.println(source.substring(unitCnt * i > 0 ? unitCnt
						* i - 1 : 0, unitCnt + unitCnt * i));
			}

			int tailLen = strLen % unitCnt;
			System.out.println(source.substring(unitCnt * cnt > 0 ? unitCnt
					* cnt - 1 : 0, tailLen + unitCnt * cnt));
		}

	}

	/**
	 * 含空字符�?null字段串�?返回为true;
	 * 
	 * @param source
	 * @return
	 */
	public static boolean isNull(String source) {
		if(source == null || source.trim().length() == 0 || "null".equalsIgnoreCase(source)){
			return true;
		}
		return false;
	}

	/**
	 * @return 随机数ID
	 */
	public static String generatorRandomId() {
		String uuid = UUID.randomUUID().toString();
		String m = System.currentTimeMillis() + "";
		int len = m.length();
		char mLast = m.charAt(len - 1);
		uuid = uuid.replace('-', mLast);
		String preffix = uuid.substring(0, 4);
	//	long id = Long.parseLong(uuid.substring(len - 9, len), 16);
	//	return "" + Math.abs((int) id);
		return preffix+m;
	}

	public static Double string2Double(String str) {
		if (StringUtils.isNull(str)) {
			str = "0.000";
		}
		Double value = 0.00;

		try {
			value = Double.valueOf(str);
			//value = round(str,3,BigDecimal.ROUND_DOWN);
		} catch (Exception e) {
			e.printStackTrace();
			value = 0.00;
		}
		return value;
	}
	
	private static Double round(String value, int scale, int roundingMode) { 

		BigDecimal bd = new BigDecimal(value); 

		bd = bd.setScale(scale, roundingMode); 

		double d = bd.doubleValue(); 

		bd = null; 

		return d; 

		}

	/**
	 * 格式化数字,
	 * @param position
	 * @return
	 */
	public static String getTwoLenNumic(int position) {
		if(position < 0){
			return "-00";
		}
		if(position > 99){
			return "99+";
		}
		return 	String.format("%2d", position);
	} 

}
