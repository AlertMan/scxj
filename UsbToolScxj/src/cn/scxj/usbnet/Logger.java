package cn.scxj.usbnet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	public static final String splitter = "  |  ";
	
	public static void debug(String log) {
		log("调试", log);
	}
	
	public static void info(String log) {
		log("信息", log);
	}
	
	public static void warn(String log) {
		log("警告", log);
	}
	
	public static void error(String log) {
		log("错误", log);
	}
	
	private static void log (String level, String log) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		StringBuffer sb = new StringBuffer();
		sb.append(date).append(splitter).append(level).append(splitter).append(log);
		MainScreen.getMainScreen().addLog(sb.toString());
	}
}
