/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.collector.Collector;
import com.meituan.gaara.collector.DefaultInfoCollector;
import com.meituan.gaara.info.TransientInfo;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;
import com.meituan.gaara.util.WebUtil;

/**
 * 本地收集器工厂
 * 
 * @author lichengwu
 * @created 2012-2-16
 * 
 * @version 1.1
 */
public class LocalCollectorFactory {

	private static final Log log = LogFactory.getLog(LocalCollectorFactory.class);

	public Map<String, DefaultInfoCollector> registeredCollectors = new ConcurrentHashMap<String, DefaultInfoCollector>();

	private final static LocalCollectorFactory INSTANCE = new LocalCollectorFactory();

	/**
	 * 收集器所在的包
	 */
	private static final String COLLECTOR_PACKAGE = "com.meituan.gaara.collector.";

	private LocalCollectorFactory() {
		// server模式不收集本地资源
		if (!"server".equals(ParameterUtil.getParameter(Parameter.GAARA_RUN_MODE))) {
			registerCollector();
		}
	}

	/**
	 * 注册收集器
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 */
	private void registerCollector() {
		// 如果用户没有自定义collector，那么使用默认collector
		String registeredName = ParameterUtil.getParameter(Parameter.REGISTERED_LOCAL_COLLECTORS);
		if (registeredName == null || "".equals(registeredName)) {
			registeredName = ParameterUtil.getParameter("gaara.default.collector");
			ParameterUtil.storeCustomerProperties(Parameter.REGISTERED_LOCAL_COLLECTORS.getName(),
			        registeredName);
		}
		List<String> collectors = Arrays.asList(registeredName.split(","));
		for (String name : collectors) {
			DefaultInfoCollector collector = newCollectorByName(name);
			if (collector != null) {
				registeredCollectors.put(collector.getName(), collector);
			}
		}
	}

	/**
	 * 向系统添加一个collector，如果collector已存在，直接返回
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @param collector名字
	 * @return collector实例或者null(添加失败)
	 */
	public DefaultInfoCollector addCollector(String collector) {
		DefaultInfoCollector c = null;
		if (registeredCollectors.containsKey(collector)) {
			log.warn("local collector[" + collector + "] already exist, ignored...");
			c = registeredCollectors.get(collector);
		} else {
			c = newCollectorByName(collector);
			if (c != null) {
				registeredCollectors.put(c.getName(), c);
				// 储存新添加的collector
				String exists = ParameterUtil.getParameter(Parameter.REGISTERED_LOCAL_COLLECTORS);
				List<String> list = Arrays.asList(exists.split(","));
				if (!list.contains(c.getName())) {
					ParameterUtil.storeCustomerProperties(
					        Parameter.REGISTERED_LOCAL_COLLECTORS.getName(),
					        exists + "," + c.getName());
				}
				log.info("register new local collector [" + collector + "]");
			} else {
				log.warn("can not register local collector [" + collector + "]");
			}
		}
		return c;
	}

	/**
	 * 根据名字创建collector
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @param name
	 * @return
	 */
	private DefaultInfoCollector newCollectorByName(String name) {
		assert name != null;
		DefaultInfoCollector collector = null;
		try {
			Class<?> clazz = Class.forName(COLLECTOR_PACKAGE + name);
			Constructor<?> constructor = clazz.getConstructor(String.class);
			collector = (DefaultInfoCollector) constructor.newInstance(WebUtil
			        .getContextPath(ParameterUtil.getServletContext()));
			// 初始化
			collector.init();
		} catch (InstantiationException e) {
			log.error("make sure " + COLLECTOR_PACKAGE + name + " is an instance", e);
		} catch (IllegalAccessException e) {
			log.error("can not access " + COLLECTOR_PACKAGE + name, e);
		} catch (ClassNotFoundException e) {
			log.error("class not found:" + COLLECTOR_PACKAGE + name, e);
		} catch (NoSuchMethodException e) {
			log.error("can not found Constructor(String):" + COLLECTOR_PACKAGE + name, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (collector == null) {
			log.error("can not create local collector by name:" + name);
		} else {
			log.info("create new local collector [" + name + "]");
		}
		return collector;
	}

	/**
	 * 为远程信息提供收集器
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @param name
	 *            收集器名称
	 * @param application
	 *            应用名字
	 * @param info
	 *            收集器手收集的信息
	 * @return DefaultInfoCollector
	 */
	public static DefaultInfoCollector newCollectorForRemote(String name, String application,
	        TransientInfo info) {
		assert name != null;
		assert info != null;
		assert application != null;
		DefaultInfoCollector collector = null;
		try {
			Class<?> clazz = Class.forName(COLLECTOR_PACKAGE + name);
			Constructor<?> constructor = clazz.getConstructor(String.class);
			collector = (DefaultInfoCollector) constructor.newInstance(application);
			// 初始化
			collector.init(info);
		} catch (InstantiationException e) {
			log.error("make sure " + COLLECTOR_PACKAGE + name + " is an instance", e);
		} catch (IllegalAccessException e) {
			log.error("can not access " + COLLECTOR_PACKAGE + name, e);
		} catch (ClassNotFoundException e) {
			log.error("class not found:" + COLLECTOR_PACKAGE + name, e);
		} catch (NoSuchMethodException e) {
			log.error("can not found Constructor(String):" + COLLECTOR_PACKAGE + name, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (collector == null) {
			log.error("can not create collector [" + name + "] for application : " + application);
		} else {
			log.info("create new collector[" + name + "] for application [" + application + "]");
		}
		return collector;
	}

	/**
	 * 获得已注册的收集器
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @return 以类名为key，以collector为value的map
	 */
	public Map<String, DefaultInfoCollector> getRegisteredLocalCollectorMap() {
		return Collections.unmodifiableMap(registeredCollectors);
	}

	/**
	 * 根据名字获得collector
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @param name
	 *            collector名字
	 * @return collector实例或者null
	 */
	public DefaultInfoCollector getCollector(String name) {
		return registeredCollectors.get(name);
	}

	/**
	 * 获得collector工厂
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @return
	 */
	public static LocalCollectorFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * 删除collector
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @param collectName
	 *            收集器的名字
	 * @return 当且仅当collector存在并且成功删除返回true，否则返回false
	 */
	public boolean removeCollector(String collectName) {
		assert collectName != null;
		synchronized (registeredCollectors) {
			Collector collector = registeredCollectors.remove(collectName);
			if (collector != null) {
				collector.destory();
				// 删除配置
				String exists = ParameterUtil.getParameter(Parameter.REGISTERED_LOCAL_COLLECTORS);
				List<String> list = new ArrayList<String>(Arrays.asList(exists.split(",")));
				list.remove(collector.getName());
				StringBuilder newCollecot = new StringBuilder();
				for (String collecor : list) {
					newCollecot.append(collecor).append(",");
				}
				if (newCollecot.length() > 1 && newCollecot.charAt(newCollecot.length() - 1) == ',') {
					newCollecot.deleteCharAt(newCollecot.length() - 1);
				}
				ParameterUtil.storeCustomerProperties(
				        Parameter.REGISTERED_LOCAL_COLLECTORS.getName(), newCollecot.toString());
				collector = null;
				log.info("remove collector[" + collectName + "] from system.");
				return true;
			}
			log.warn("collector[" + collectName + "] does not exists, can not remove!");
			return false;
		}
	}
}
