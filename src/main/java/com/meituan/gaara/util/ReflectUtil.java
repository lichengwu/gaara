/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * 反射工具类
 * 
 * @author lichengwu
 * @created 2012-1-31
 * 
 * @version 1.0
 */
final public class ReflectUtil {

	/**
	 * 设置类字段可访问
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 *
	 * @param field 类的字段
	 */
	public static void setFieldAccessible(final Field field) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				field.setAccessible(true);
				return null;
			}
		});
	}
}
