/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.exception.GaaraException;

/**
 * 收集器控制器
 * 
 * @author lichengwu
 * @created 2012-2-16
 * 
 * @version 1.0
 */
public class LocalCollectorController {

	private static final Log log = LogFactory.getLog(LocalCollectorController.class);

	private Map<String, Collector> localCollectorMap = new HashMap<String, Collector>();

	/**
	 * 是否正在收集信息
	 */
	private boolean collecting = false;

	private static LocalCollectorController controller = new LocalCollectorController();

	private LocalCollectorController() {
		registerCollector();
	}

	public static LocalCollectorController getInstance() {
		return controller;
	}

	/**
	 * 注册收集器 //TODO
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 */
	private void registerCollector() {
		try {
			MemoryInfoCollector collector = new MemoryInfoCollector("gaara");
			localCollectorMap.put(collector.getName(), collector);
		} catch (GaaraException e) {
			// TODO
		}
	}

	/**
	 * 将收集器添加到LocalCollectorController
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @param collector
	 * @return 当添加成功时，返回true；否则返回false并销毁collector
	 */
	public synchronized boolean addCollector(Collector collector) {
		try {
			while (true) {
				if (!collecting) {
					if (localCollectorMap.containsKey(collector.getName())) {
						throw new GaaraException("collector already exist");
					}
					return true;
				}
				TimeUnit.MILLISECONDS.sleep(500);
			}
		} catch (Throwable e) {
			collector.destory();
			log.error("添加收集器[" + collector.getClass().getSimpleName() + "]失败," + e.getMessage());
			return false;
		}
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
	public synchronized boolean removeCollector(String collectName) {
		assert collectName != null;
		Collector collector = localCollectorMap.remove(collectName);
		return collector == null;
	}

	/**
	 * 收集
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @return
	 */
	public Serializable doCollect() {
		collecting = true;
		StringBuilder data = new StringBuilder();
		for (Entry<String, Collector> entry : localCollectorMap.entrySet()) {
			Collector collector = entry.getValue();
			data.append((Serializable) collector.collect());
		}
		collecting = false;
		return data;
	}

}
