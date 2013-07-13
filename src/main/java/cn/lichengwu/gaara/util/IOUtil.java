/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * IO工具类
 * 
 * @author lichengwu
 * @created 2012-3-4
 * 
 * @version 1.0
 */
public final class IOUtil {

	/**
	 * 将输入流数据写到输出流中
	 * 
	 * @author lichengwu
	 * @created 2012-3-4
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @throws IOException
	 */
	public static void pump(InputStream in, OutputStream out) throws IOException {
		final byte[] bytes = new byte[4096];
		int length = in.read(bytes);
		while (length != -1) {
			out.write(bytes, 0, length);
			length = in.read(bytes);
		}
		out.flush();
	}

	/**
	 * 从流中读字符串
	 * 
	 * @author lichengwu
	 * @created 2012-3-4
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String readString(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();
		String temp = reader.readLine();
		String separator = System.getProperty("line.separator");
		while (temp != null) {
			result.append(temp).append(separator);
			temp = reader.readLine();
		}
		if (result.length() > separator.length()) {
			result.delete(result.length() - separator.length(), result.length());
		}
		return result.toString();
	}
}
