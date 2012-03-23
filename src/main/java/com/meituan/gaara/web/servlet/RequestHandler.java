/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.collector.DefaultInfoCollector;
import com.meituan.gaara.collector.RemoteCollector;
import com.meituan.gaara.collector.factory.LocalCollectorFactory;
import com.meituan.gaara.collector.factory.RemoteCollectorFactory;
import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.store.JRobin;
import com.meituan.gaara.util.Closer;
import com.meituan.gaara.util.FileUtil;
import com.meituan.gaara.util.I18N;
import com.meituan.gaara.util.IOUtil;
import com.meituan.gaara.util.Period;
import com.meituan.gaara.util.ServletUtil;
import com.meituan.gaara.util.StringUtil;
import com.meituan.gaara.util.TimeRange;
import com.meituan.gaara.util.WebUtil;
import com.meituan.gaara.web.RequestParam;
import com.meituan.gaara.web.RequestType;
import com.meituan.gaara.web.html.HtmlRender;
import com.meituan.gaara.web.html.TemplateFile;

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
			try {
				handleGarph(request, response);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
			break;

		case PAGE:
			handlePage(request, response);
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
			String fileName = request.getParameter(RequestParam.KEY.getName());
			if (fileName == null || "".equals(fileName.trim())) {
				GaaraException ex = new GaaraException("resource name not assigned");
				log.error(ex.getMessage(), ex);
				ServletUtil.writeString(response, ServletUtil.exception2HTML(ex));
			} else {
				fileName = "resources/" + fileName;
				in = FileUtil.class.getResourceAsStream(FileUtil.getResourcePath(fileName));
				if (in == null) {
					GaaraException ex = new GaaraException("resource [" + fileName + "] not found");
					log.error(ex.getMessage(), ex);
					ServletUtil.writeString(response, ServletUtil.exception2HTML(ex));
					return;
				}
				IOUtil.pump(in, response.getOutputStream());
			}

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

	/**
	 * 处理页面请求 TODO
	 * 
	 * @author lichengwu
	 * @created 2012-3-21
	 * 
	 * @param request
	 * @param response
	 */
	private static void handlePage(HttpServletRequest request, HttpServletResponse response) {
		try {
			String template = request.getParameter(RequestParam.KEY.getName());
			if (template == null || "".equals(template.trim())) {
				GaaraException ex = new GaaraException("template name not assigned");
				log.error(ex.getMessage(), ex);
				ServletUtil.writeString(response, ServletUtil.exception2HTML(ex));
			} else {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("app", "gt");
				data.put("application", "gt@Oliver-ThinkPad");
				String html = HtmlRender.getInstance().render(TemplateFile.APP_INDEX, data);
				ServletUtil.writeString(response, html);
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 * 处理图像请求
	 * 
	 * @author lichengwu
	 * @created 2012-3-22
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException
	 */
	private static void handleGarph(HttpServletRequest request, HttpServletResponse response)
	        throws IOException {

		// 图像名称
		String graphName = request.getParameter(RequestParam.GRAPH_NAME.getName());

		String collecorName = request.getParameter(RequestParam.KEY.getName());

		if (StringUtil.isBlank(graphName)) {
			log.error("empty graph name....");
			return;
		}

		DefaultInfoCollector collector = null;
		// 应用名字，如果没有，默认本地应用
		String application = request.getParameter(RequestParam.APPLICATION.getName());
		if (application == null || "".equals(application.trim())) {
			application = WebUtil.getFullCurrentApplication();
			collector = LocalCollectorFactory.getInstance().getCollector(collecorName);
		} else {
			RemoteCollector remoteCollector = RemoteCollectorFactory.getInstance()
			        .getRemoteCollector(application);
			if (remoteCollector == null) {
				log.error("remote collector for application [" + application + "] missing...");
				return;
			}
			collector = remoteCollector.getLocalCollector(collecorName);
		}

		if (collector == null) {
			log.error("can note obtain collector for application [" + application + "]");
			return;
		}
		// read grath from JRobin
		JRobin jRobin = collector.getJRobin(graphName);
		if (jRobin != null) {
			byte[] img = new byte[0];
			// deal with time range
			TimeRange tr = null;
			Period period = Period.valueOfIgnoreCase(request.getParameter(RequestParam.PERIHOD
			        .getName()));
			if (period == null) {
				try {
					Date startDate = I18N.createDateFormat().parse(
					        request.getParameter(RequestParam.START_DATE.getName()));
					Date endDate = I18N.createDateFormat().parse(
					        request.getParameter(RequestParam.END_DATE.getName()));
					tr = TimeRange.createCustomRange(startDate, endDate);
				} catch (Exception e) {
					// log.warn(e.getMessage(), e);
				}
			}
			if (tr == null) {
				tr = Period.DAY.getRange();
			}

			// deal with graph withd&height
			int width = 200;
			int height = 50;
			try {
				width = Integer.valueOf(request.getParameter(RequestParam.WIDTH.getName()));
				height = Integer.valueOf(request.getParameter(RequestParam.HEIGHT.getName()));
			} catch (NumberFormatException ex) {
				// ignore
			}
			try {
				img = jRobin.graph(tr, width, height);
			} catch (GaaraException e) {
				e.printStackTrace();
			}
			response.setContentType("image/png");
			response.setContentLength(img.length);
			final String fileName = graphName + ".png";
			response.addHeader("Content-Disposition", "inline;filename=" + fileName);
			response.getOutputStream().write(img);
			response.flushBuffer();
		}
	}
}
