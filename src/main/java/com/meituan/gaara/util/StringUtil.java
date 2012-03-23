/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

/**
 * String工具类
 * 
 * @author lichengwu
 * @created 2012-3-22
 * 
 * @version 1.0
 */
final public class StringUtil {

	/**
	 * 字符串是否有长度
	 * 
	 * @author lichengwu
	 * @created 2012-3-22
	 * 
	 * @param str
	 * @return 如果字符串不为空，且不是空串返回true，否则返回false
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * 字符串是否有内容
	 * 
	 * @author lichengwu
	 * @created 2012-3-22
	 *
	 * @param str
	 * @return 如果字符串有内容，并且不全是空格，返回true，否则返回false
	 */
	public static boolean isBlank(String str) {
		return (str == null || str.trim().length() == 0);
	}
}
