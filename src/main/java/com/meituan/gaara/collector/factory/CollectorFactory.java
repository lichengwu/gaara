/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.collector.Collector;
import com.meituan.gaara.collector.MemoryInfoCollector;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;
import com.meituan.gaara.util.WebUtil;

/**
 * collector工厂
 * 
 * @author lichengwu
 * @created 2012-2-16
 * 
 * @version 1.0
 */
public class CollectorFactory {

	private static final Log log = LogFactory.getLog(CollectorFactory.class);

	public Map<String, Collector> registeredCollectors = new ConcurrentHashMap<String, Collector>();

	private final static CollectorFactory INSTANCE = new CollectorFactory();

	private CollectorFactory() {
		registerCollector();
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
		String registeredName = ParameterUtil.getParameter(Parameter.REGISTERED_COLLECTORS);
		if (registeredName == null || "".equals(registeredName)) {
			registeredName = ParameterUtil.getParameter("gaara.default.collector");
			ParameterUtil.storeCustomerProperties(Parameter.REGISTERED_COLLECTORS.getName(),
			        registeredName);
		}
		List<String> collectors = Arrays.asList(registeredName.split(","));
		for (String name : collectors) {
			Collector collector = newCollectorByName(name);
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
	public Collector addCollector(String collector) {
		Collector c=null;
		if (registeredCollectors.containsKey(collector)) {
			log.warn("collector[" + collector + "] already exist,ignore...");
			c=registeredCollectors.get(collector);
		} else {
			c = newCollectorByName(collector);
			if (c != null) {
				registeredCollectors.put(c.getName(), c);
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
	private Collector newCollectorByName(String name) {
		assert name != null;
		Collector collector = null;
		if (name.equals(MemoryInfoCollector.class.getSimpleName())) {
			collector = new MemoryInfoCollector(WebUtil.getCurrentApplication());
		}
		if (collector == null) {
			log.warn("can not create collector[" + name + "]");
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
	public Map<String, Collector> getRegisteredCollectorMap() {
		return registeredCollectors;
		//return Collections.unmodifiableMap(registeredCollectors);
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
	public Collector getCollector(String name) {
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
	public static CollectorFactory getInstance() {
		return INSTANCE;
	}
	
	/**
	 * 删除collector
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 *
	 * @param collectName 收集器的名字
	 * @return 当且仅当collector存在并且成功删除返回true，否则返回false
	 */
	public boolean removeCollector(String collectName) {
		assert collectName != null;
		synchronized (registeredCollectors) {
	        Collector collector = registeredCollectors.remove(collectName);
	        collector.destory();
	        return collector == null;
        }
	}
}
