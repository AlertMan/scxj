package com.scxj.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * 文本工具
 * @author zhudameng
 *
 */
public class TextUtil {
	/**
	 * 根据正则表达式和指定颜色高亮文本中的关键字
	 * @param content
	 * @return
	 */
	public static SpannableString highlightKeywords(String content,String pattern,int Color) {
		SpannableString s = new SpannableString(content);
		Pattern p = Pattern.compile(pattern);
		 Matcher m = p.matcher(s);
		while (m.find()) {
		    int start = m.start();
		    int end = m.end();
		    s.setSpan(new ForegroundColorSpan(Color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return s;
	}
}
