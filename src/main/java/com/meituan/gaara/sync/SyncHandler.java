/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.sync;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.store.HttpDataRetriever;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;
import com.meituan.gaara.util.ServletUtil;
import com.meituan.gaara.util.WebUtil;

/**
 * sync handler for sending and receiving sync data
 * 
 * @author lichengwu
 * @created 2012-3-26
 * 
 * @version 1.0
 */
public class SyncHandler {

	private static final Log log = LogFactory.getLog(SyncHandler.class);

	/**
	 * send loacal sync data to server
	 * 
	 * @author lichengwu
	 * @created 2012-3-26
	 * 
	 * @param response
	 *            HttpServletResponse
	 */
	public void send(HttpServletResponse response) {
		try {
			SyncData data = new SyncData();
			data.setApplication(WebUtil.getContextPath(ParameterUtil.getServletContext()));
			data.setBootupTime(ParameterUtil.getParameterAsLong(Parameter.APPLICATION_START_TIME
			        .getName()));
			data.setGaaraVersion(ParameterUtil.getParameter(Parameter.GAARA_VERSION));
			data.setHostAddress(ParameterUtil.getParameter(Parameter.HOST_ADDRESS));
			data.setSyncTime(System.currentTimeMillis());
			ServletUtil.writeSerializable(response, data);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * try to get sync data from remote application
	 * 
	 * @author lichengwu
	 * @created 2012-3-26
	 * 
	 * @param url
	 *            remote application url
	 * @return remote application sync data if exists.
	 */
	public SyncData trySync(URL url) {
		SyncData data = null;
		try {
			data = new HttpDataRetriever(url).callPost();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return data;
	}
}
