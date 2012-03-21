/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.web.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.util.Closer;
import com.meituan.gaara.util.FileUtil;
import com.meituan.gaara.util.IOUtil;
import com.meituan.gaara.util.ServletUtil;
import com.meituan.gaara.web.RequestParam;
import com.meituan.gaara.web.RequestType;

/**
 * 请求处理器
 * 
 * @author lichengwu
 * @created 2012-3-20
 * 
 * @version 1.0
 */
public class RequestHandler {

	private static final Log log = LogFactory.getLog(RequestHandler.class);

	public static void handle(HttpServletRequest request, HttpServletResponse response) {
		RequestType type = RequestType.getRequestType(request.getParameter(RequestParam.TYPE
		        .getName()));
		switch (type) {
		case RESOURCE:
			handleResource(request, response);
			break;
		case GRAPH:

			break;

		default:
			break;
		}
	}

	/**
	 * 处理资源文件
	 * 
	 * @author lichengwu
	 * @created 2012-3-20
	 * 
	 * @param request
	 * @param response
	 */
	private static void handleResource(HttpServletRequest request, HttpServletResponse response) {
		InputStream in = null;
		try {
			String fileName = request.getParameter(RequestParam.FILE_NAME.getName());
			if (fileName == null || "".equals(fileName.trim())) {
				ServletUtil.writeString(response, ServletUtil.exception2HTML(new GaaraException(
				        "resource name not assigned")));
				return;
			}
			fileName = "resources/" + fileName;
			in = FileUtil.class.getResourceAsStream(FileUtil.getResourcePath(fileName));
			if(in==null){
				ServletUtil.writeString(response, ServletUtil.exception2HTML(new GaaraException("resource ["+fileName+"] not found")));
				return;
			}
			IOUtil.pump(in, response.getOutputStream());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			try {
				ServletUtil.writeString(response, ServletUtil.exception2HTML(e));
			} catch (IOException ex) {
				// ingore
			}
		} finally {
			Closer.close(in);
		}
	}
}
