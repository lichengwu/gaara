/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

/**
 * servlet工具类
 * 
 * @author lichengwu
 * @created 2012-3-17
 * 
 * @version 1.0
 */
public final class ServletUtil {

	/**
	 * HttpServletResponse强制不用缓存
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @param response
	 *            HttpServletResponse
	 */
	public static void noCache(HttpServletResponse response) {
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
	}
	
	/**
	 * 向响应流中写入png图片
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param graph
	 *            图像数据
	 * @param graphName
	 *            图像名称
	 * @throws IOException
	 */
	public static void writePngGraph(HttpServletResponse response, byte[] graph, String graphName)
	        throws IOException {
		response.setContentType("image/png");
		response.setContentLength(graph.length);
		response.addHeader("Content-Disposition", "inline;filename=" + graphName + ".png");
		response.getOutputStream().write(graph);
		response.flushBuffer();
	}

	/**
	 * 向响应流中写入{@link Serializable}数据
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 *
	 * @param response HttpServletResponse
	 * @param data 数据
	 * @throws IOException
	 */
	public static void writeSerializable(HttpServletResponse response, Serializable data)
	        throws IOException {
		response.setContentType("application/octet-stream");
		OutputStream out = response.getOutputStream();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(data);
		oout.flush();
		byte[] byteArry = bout.toByteArray();
		Closer.close(bout, oout);
		out.write(byteArry);
		out.flush();
	}
	
	/**
	 * 向流中写入字符串
	 * 
	 * @author lichengwu
	 * @created 2012-3-20
	 *
	 * @param response HttpServletResponse
	 * @param str 
	 * @throws IOException
	 */
	public static void writeString(HttpServletResponse response,String str) throws IOException{
		response.setContentType("text/html");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
		writer.append(str);
		writer.flush();
	}
	
	/**
	 * 将异常转化成html
	 * 
	 * @author lichengwu
	 * @created 2012-3-20
	 * 
	 * @param throwable
	 *            异常
	 * @return html
	 */
	public static String exception2HTML(Throwable throwable) {
		StringBuilder html = new StringBuilder();
		int index = 0;
		for (StackTraceElement ste : throwable.getStackTrace()) {
			if (index == 0) {
				html.append(ste.toString()+" : "+throwable.getLocalizedMessage()).append("<br />");
			} else {
				html.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(ste.toString()).append("<br />");
			}
			index++;
		}
		return html.toString();
	}
}
