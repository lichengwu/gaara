/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.sync;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.store.HttpDataRetriever;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;
import com.meituan.gaara.util.ServletUtil;
import com.meituan.gaara.util.UrlUtil;
import com.meituan.gaara.util.WebUtil;
import com.meituan.gaara.web.RequestParam;
import com.meituan.gaara.web.RequestType;

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
	 * get the time lag between server and client
	 * <p>
	 * server time = remote time + time lag
	 * </p>
	 * <p>
	 * if any exception occur while call tyrTimeLag(), the method will return 0.
	 * ie, not time lag
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-3-27
	 * 
	 * @param baseUrl
	 *            remote url
	 * @return time lag
	 */
	public long tyrTimeLag(String baseUrl) {
		int tryTimes = 3;
		long timeLag = 0;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(RequestParam.TYPE.getName(), RequestType.SYNC.getName());
			param.put(RequestParam.KEY.getName(), "timeLag");
			param.put(RequestParam.TIME.getName(), System.currentTimeMillis());

			URL remoteUrl = UrlUtil.buildURL(baseUrl, param);
			for (int i = 0; i < tryTimes; i++) {
				long temp = new HttpDataRetriever(remoteUrl).callPost();
				timeLag += temp;
				TimeUnit.MILLISECONDS.sleep(500);
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return 0;
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			return 0;
		}
		return timeLag / tryTimes;
	}

	/**
	 * work out time lag between server and client
	 * <p>
	 * server time = remote time + time lag
	 * </p>
	 * <p>
	 * if any exception occur while call handleTimeLag(), the method will send 0
	 * to server. ie, not time lag.
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-3-27
	 * 
	 * @param request
	 * @param response
	 */
	public void handleTimeLag(HttpServletRequest request, HttpServletResponse response) {
		try {
			long timeLag = 0;
			long clientTime = System.currentTimeMillis();
			String timeStr = request.getParameter(RequestParam.TIME.getName());
			long serverTime = Long.valueOf(timeStr);
			timeLag = serverTime - clientTime;
			ServletUtil.writeSerializable(response, timeLag);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			try {
				ServletUtil.writeSerializable(response, 0);
			} catch (IOException ex) {
				// ignore
			}
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
