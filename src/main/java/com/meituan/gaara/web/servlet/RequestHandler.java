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

import com.meituan.gaara.util.Closer;
import com.meituan.gaara.util.FileUtil;
import com.meituan.gaara.util.IOUtil;
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
	public static void handle(HttpServletRequest request, HttpServletResponse response) {
		RequestType type = RequestType.getRequestType(request.getParameter(RequestParam.TYPE
		        .getName()));

		switch (type) {
		case RESOURCE:
			InputStream in = null;
			try {
				in = FileUtil.class.getResourceAsStream(FileUtil.getResourcePath(""));
				IOUtil.pump(in, response.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				Closer.close(in);
			}
			break;
		case GRAPH:

			break;

		default:
			break;
		}
	}
}
