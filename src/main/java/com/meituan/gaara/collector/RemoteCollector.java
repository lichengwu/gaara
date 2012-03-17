/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class RemoteCollector extends DefaultInfoCollector {

	private static final Log log = LogFactory.getLog(RemoteCollector.class);

	/**
	 * 被装饰的collector
	 */
	private DefaultInfoCollector collector;

	private URL url;

	/**
	 * @param application
	 *            应用程序名字
	 * @param url
	 *            远程应用url
	 * @throws MalformedURLException
	 */
	public RemoteCollector(DefaultInfoCollector collector, String url) throws MalformedURLException {
		this(collector, new URL(url));
	}

	/**
	 * @param application
	 *            应用程序名字
	 * @param url
	 *            远程应用url
	 */
	public RemoteCollector(DefaultInfoCollector collector, URL url) {
		super(collector.getApplication());
		this.collector = collector;
		this.url = url;
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

	/**
	 * @see com.meituan.gaara.collector.Collector#getName()
	 */
	@Override
	public String getName() {
		return collector.getName();
	}

	/**
	 * @see com.meituan.gaara.collector.DefaultInfoCollector#initJRobin()
	 */
	@Override
	protected void initJRobin() throws GaaraException {
		collector.initJRobin();
	}

	/**
	 * @see com.meituan.gaara.collector.DefaultInfoCollector#saveInfo(com.meituan.gaara.info.TransientInfo)
	 */
	@Override
	protected void saveInfo(TransientInfo info) throws GaaraException {
		collector.saveInfo(info);
	}

	/**
	 * 从远程获得信息
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 */
	@Override
	protected TransientInfo getNewInfo() {
		TransientInfo info = null;
		try {
			info = new HttpDataRetriever(url).call();
		} catch (IOException e) {
			log.error("can not collect remote application[" + application + ":" + url.toString()
			        + "]", e);
		}
		return info;
	}

}
