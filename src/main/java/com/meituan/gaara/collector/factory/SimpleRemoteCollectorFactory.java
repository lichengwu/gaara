/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector.factory;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.collector.RemoteCollector;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;

/**
 * 远程收集器工厂
 * 
 * @author lichengwu
 * @created 2012-3-10
 * 
 * @version 1.0
 */
public final class SimpleRemoteCollectorFactory {

	private static final Log log = LogFactory.getLog(SimpleRemoteCollectorFactory.class);

	private Map<String, RemoteCollector> remoteCollectorMap = new ConcurrentHashMap<String, RemoteCollector>();

	private static SimpleRemoteCollectorFactory INSTANCE = new SimpleRemoteCollectorFactory();

	private SimpleRemoteCollectorFactory() {
		initRemoteCollector();
	}

	/**
	 * 获取远程收集器工厂实例
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @return 远程收集器工厂实例
	 */
	public static SimpleRemoteCollectorFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 */
	private void initRemoteCollector() {
		String registeredName = ParameterUtil.getParameter(Parameter.REGISTERED_REMOTE_COLLECTOR);
		if (registeredName == null || "".equals(registeredName)) {
			log.info("this no remote collector...");
			return;
		}
		List<String> collectors = Arrays.asList(registeredName.split(";"));
		for (String collector : collectors) {
			String[] remoteInfo = collector.split(",");
			try {
				RemoteCollector remoteCollector = new RemoteCollector(remoteInfo[0], remoteInfo[1]);
				remoteCollectorMap.put(remoteCollector.getApplication(), remoteCollector);
			} catch (MalformedURLException e) {
				log.error("can not init remote collector[" + remoteInfo[0] + ":" + remoteInfo[1]
				        + "]", e);
			}
		}
	}

	/**
	 * 获得已注册的远程收集器
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @return 已注册的远程收集器
	 */
	public Map<String, RemoteCollector> getRegisteredRemoteCollectorMap() {
		return Collections.unmodifiableMap(remoteCollectorMap);
	}

	/**
	 * 添加远程收集器
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @param application
	 *            远程应用的名字
	 * @param url
	 *            远程应用的url
	 * @return RemoteCollector
	 */
	public RemoteCollector addCollector(String application, String url) {
		if (remoteCollectorMap.containsKey(application)) {
			log.info("remote collector[" + application + ":" + url + "] already exists");
			return remoteCollectorMap.get(application);
		}
		RemoteCollector remoteCollector = null;
		try {
			remoteCollector = new RemoteCollector(application, url);
		} catch (MalformedURLException e) {
			log.error("can not create remote collector[" + application + ":" + url + "]", e);
		}
		return remoteCollector;
	}
}
