/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meituan.gaara.collector.MemoryInfoCollector;
import com.meituan.gaara.collector.factory.SimpleLocalCollectorFactory;
import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.store.JRobin;
import com.meituan.gaara.util.Period;

/**
 * 画图测试
 * 
 * @author lichengwu
 * @created 2012-3-17
 * 
 * @version 1.0
 */
public class GraphServeltTest extends HttpServlet {

	/**
     * 
     */
	private static final long serialVersionUID = -667010887101198674L;

	/**
	 * Constructor of the object.
	 */
	public GraphServeltTest() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		String graphName = request.getParameter("name");
		MemoryInfoCollector collector = (MemoryInfoCollector) SimpleLocalCollectorFactory
		        .getInstance().getCollector(MemoryInfoCollector.class.getSimpleName());
		JRobin jRobin = collector.getJRobin(graphName);
		if (jRobin != null) {
			byte[] img = new byte[0];
			try {
				img = jRobin.graph(Period.DAY.getRange(), 400, 200);
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

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

		doGet(request, response);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

}
