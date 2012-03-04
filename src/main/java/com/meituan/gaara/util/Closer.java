/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;

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

	/**
	 * 关闭URLConnection并保持长连接
	 * 参考：http://docs.oracle.com/javase/1.5.0/docs/guide/net/http-keepalive.html
	 * 
	 * @author lichengwu
	 * @created 2012-3-4
	 * 
	 * @param conn
	 * @return 如果关闭发生错误，返回错误消息
	 */
	public static String closeUrlConnection(URLConnection conn) {
		String errMsg = null;
		try {
			InputStream is = conn.getInputStream();
			is.close();
		} catch (IOException e) {
			try {
				if (conn instanceof HttpURLConnection) {
					HttpURLConnection httpConnection = (HttpURLConnection) conn;
					BufferedReader reader = new BufferedReader(new InputStreamReader(
					        httpConnection.getErrorStream()));
					StringBuilder err = new StringBuilder();
					while ((errMsg = reader.readLine()) != null) {
						err.append(errMsg).append(System.getProperty("line.separator"));
					}
					errMsg = err.toString();
					reader.close();
				}
			} catch (IOException ex) {
				// ignore
			}
		}
		return errMsg;
	}
}
