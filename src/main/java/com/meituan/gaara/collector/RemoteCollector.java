/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.collector.factory.SimpleLocalCollectorFactory;
import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.info.TransientInfo;
import com.meituan.gaara.store.HttpDataRetriever;

/**
 * 远程收集器
 * 
 * @author lichengwu
 * @created 2012-3-10
 * 
 * @version 1.0
 */
public class RemoteCollector {

	private static final Log log = LogFactory.getLog(RemoteCollector.class);

	private Map<String, DefaultInfoCollector> collectorHandler = new ConcurrentHashMap<String, DefaultInfoCollector>();

	/**
	 * 远程应用的名字
	 */
	private String application;

	private URL url;

	/**
	 * @param application
	 *            应用程序名字
	 * @param url
	 *            远程应用url
	 * @throws MalformedURLException
	 */
	public RemoteCollector(String application, String url) throws MalformedURLException {
		this(application, new URL(url));
	}

	/**
	 * @param application
	 *            应用程序名字
	 * @param url
	 *            远程应用url
	 */
	public RemoteCollector(String application, URL url) {
		assert application != null;
		assert url != null;
		this.application = application;
		this.url = url;
		log.info("init remote collector [" + application + ":" + url.toString() + "]");
	}

	/**
	 * 收集远程应用的监控信息
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @throws GaaraException
	 */
	public void collectRemoteApplication() throws GaaraException {
		Map<String, TransientInfo> call = null;
		try {
			call = new HttpDataRetriever(url).callPost();
		} catch (IOException e) {
			log.error("can not collect remote application[" + application + ":" + url.toString()
			        + "]", e);
		}
		long begin = System.currentTimeMillis();
		log.info("start collect remote application:" + application);
		// 收集所有信息
		for (Entry<String, TransientInfo> entry : call.entrySet()) {
			String collectorName = entry.getKey();
			TransientInfo info = entry.getValue();
			DefaultInfoCollector collector = collectorHandler.get(collectorName);
			if (collector == null) {
				collector = SimpleLocalCollectorFactory.newCollectorForRemote(collectorName,
				        application, info);
				collectorHandler.put(collectorName, collector);
			}
			collector.saveInfo(info);
		}
		log.info("finish collect remote application:" + application + ", timeused:"
		        + (System.currentTimeMillis() - begin) + "ms");
	}

	/**
	 * 获取远程应用的名字
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @return 远程应用的名字
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * 获取远程应用的url
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @return 远程应用的url
	 */
	public URL getURL() {
		return url;
	}

}
