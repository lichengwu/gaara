/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.io.Closeable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 关闭资源工具
 * 
 * @author lichengwu
 * @created 2012-1-18
 * 
 * @version 1.0
 */
public class Closer {

	private static final Log log = LogFactory.getLog(Closer.class);

	/**
	 * 关闭
	 * 
	 * @author lichengwu
	 * @created 2012-1-18
	 *
	 * @param closeable
	 */
	public static void close(Closeable... closeable) {

		for (Closeable close : closeable) {
			if (close != null) {
				try {
					close.close();
				} catch (Throwable e) {
					log.error("error occur while closing " + close.getClass().getSimpleName()
					        + " : " + e.getMessage());
				} finally {
					close = null;
				}
			}
		}

	}
}
