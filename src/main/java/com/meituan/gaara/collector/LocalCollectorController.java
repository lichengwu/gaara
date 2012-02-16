/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.collector.factory.CollectorFactory;

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

	private Map<String, Collector> localCollectorMap = new ConcurrentHashMap<String, Collector>();

	/**
	 * 是否正在收集信息
	 */
	private boolean collecting = false;

	private static LocalCollectorController controller = new LocalCollectorController();

	private LocalCollectorController() {
		localCollectorMap=CollectorFactory.getInstance().getRegisteredCollectorMap();
	}

	/**
	 * 获得LocalCollectorController实例
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 *
	 * @return
	 */
	public static LocalCollectorController getInstance() {
		return controller;
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
	public synchronized boolean addCollector(String collector) {
		try {
			while (true) {
				//如果正在收集，休息500ms
				if (!collecting) {
					CollectorFactory.getInstance().addCollector(collector);
					return true;
				}
				TimeUnit.MILLISECONDS.sleep(500);
			}
		} catch (Throwable e) {
			return false;
		}
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
