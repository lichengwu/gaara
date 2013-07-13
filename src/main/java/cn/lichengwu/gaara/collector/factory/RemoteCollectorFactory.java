/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.collector.factory;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.lichengwu.gaara.collector.RemoteCollector;
import cn.lichengwu.gaara.util.Parameter;
import cn.lichengwu.gaara.util.ParameterUtil;

/**
 * 远程收集器工厂
 * 
 * @author lichengwu
 * @created 2012-3-10
 * 
 * @version 1.0
 */
public final class RemoteCollectorFactory {

	private static final Log log = LogFactory.getLog(RemoteCollectorFactory.class);

	private Map<String, RemoteCollector> remoteCollectorMap = new ConcurrentHashMap<String, RemoteCollector>();

	private static RemoteCollectorFactory INSTANCE = new RemoteCollectorFactory();

	private RemoteCollectorFactory() {
		// cilent模式不收集远程信息
		if (!"client".equals(ParameterUtil.getParameter(Parameter.GAARA_RUN_MODE))) {
			initRemoteCollector();
		}
	}

	/**
	 * 获取远程收集器工厂实例
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @return 远程收集器工厂实例
	 */
	public static RemoteCollectorFactory getInstance() {
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
			log.warn("this is no remote collector...");
			return;
		}
		log.info("start init remote collector...");
		Set<String> collectors = new HashSet<String>(Arrays.asList(registeredName.split(";")));
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
		log.info("finish init remote collector...");
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
	public RemoteCollector addRemoteCollector(String application, String url) {
		if (remoteCollectorMap.containsKey(application)) {
			log.info("remote collector [" + application + ":" + url + "] already exists");
			return remoteCollectorMap.get(application);
		}
		RemoteCollector remoteCollector = null;
		try {
			remoteCollector = new RemoteCollector(application, url);
			remoteCollectorMap.put(remoteCollector.getApplication(), remoteCollector);
			// 存入配置文件
			String exists = ParameterUtil.getParameter(Parameter.REGISTERED_REMOTE_COLLECTOR);
			if (exists == null) {
				exists = application + "," + url;
			} else {
				exists = exists + ";" + application + "," + url;
			}

			ParameterUtil.storeCustomerProperties(Parameter.REGISTERED_REMOTE_COLLECTOR.getName(),
			        exists);
			log.info("add remote controller [" + application + ":" + url.toString()
			        + "] successfull");
		} catch (MalformedURLException e) {
			log.error("can not create remote collector[" + application + ":" + url + "]", e);
		}
		return remoteCollector;
	}

	/**
	 * 删除远程收集器
	 * 
	 * @author lichengwu
	 * @created 2012-3-11
	 * 
	 * @param application
	 *            远程应用的名字
	 */
	public void removeCollector(String application) {
		assert application != null;
		// 删除收集器
		RemoteCollector collector = remoteCollectorMap.remove(application);
		if (collector == null) {
			log.warn("remote collector for application:" + application + " not exists");
			return;
		}
		// 删除配置
		String exists = ParameterUtil.getParameter(Parameter.REGISTERED_REMOTE_COLLECTOR);
		if (exists != null) {
			Set<String> collectorInfo = new HashSet<String>(Arrays.asList(exists.split(";")));
			Iterator<String> itr = collectorInfo.iterator();
			while (itr.hasNext()) {
				String info = itr.next();
				if (info.split(",")[0].equals(application)) {
					itr.remove();
					break;
				}
			}
			StringBuilder config = new StringBuilder();
			for (String info : collectorInfo) {
				config.append(info).append(";");
			}
			if (config.length() > 1 && config.charAt(config.length() - 1) == ';') {
				config.deleteCharAt(config.length() - 1);
			}
			// 存储新的配置
			ParameterUtil.storeCustomerProperties(Parameter.REGISTERED_REMOTE_COLLECTOR.getName(),
			        config.toString());
			// 消耗相关信息
			collector.destory();
			log.info("remove remote controller [" + application + ":"
			        + collector.getURL().toString() + "] successfull");
			collector = null;
		}
	}

	/**
	 * 根据应用名字获得{@link RemoteCollector}
	 * 
	 * @author lichengwu
	 * @created 2012-3-22
	 * 
	 * @param application
	 *            远程应用的名字
	 * @return {@link RemoteCollector} 或者null
	 */
	public RemoteCollector getRemoteCollector(String application) {
		return remoteCollectorMap.get(application);
	}
}
