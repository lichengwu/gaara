/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.collector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.lichengwu.gaara.collector.factory.LocalCollectorFactory;

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

	/**
	 * 是否正在收集信息
	 */
	private boolean collecting = false;

	private static LocalCollectorController controller = new LocalCollectorController();

	private LocalCollectorController() {
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
			int iCount = 0;
			while (true) {
				// 尝试20，不成功则返回false
				if (iCount > 20) {
					log.warn("add collector field:[time out]");
					return false;
				}
				// 如果正在收集，休息500ms
				if (!collecting) {
					LocalCollectorFactory.getInstance().addCollector(collector);
					return true;
				}
				TimeUnit.MILLISECONDS.sleep(500);
				iCount++;
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 将收集器从LocalCollectorController删除
	 * 
	 * @author lichengwu
	 * @created 2012-3-3
	 * 
	 * @param collector
	 *            收集器名字
	 * @return 删除成功返回true，否则返回false
	 */
	public synchronized boolean romoveCollector(String collector) {
		try {
			int iCount = 0;
			while (true) {
				// 尝试20，不成功则返回false
				if (iCount > 20) {
					log.warn("add collector field:[time out]");
					return false;
				}
				// 如果正在收集，休息500ms
				if (!collecting) {
					LocalCollectorFactory.getInstance().removeCollector(collector);
					return true;
				}
				TimeUnit.MILLISECONDS.sleep(500);
				iCount++;
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
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
	public synchronized Serializable doCollect() {
		collecting = true;
		long start = System.currentTimeMillis();
		log.info("start to collect info...");
		Map<String, DefaultInfoCollector> registeredCollectorMap = LocalCollectorFactory.getInstance()
		        .getRegisteredLocalCollectorMap();
		HashMap<String, Serializable> collectedInfo = new HashMap<String, Serializable>();
		for (Entry<String, DefaultInfoCollector> entry : registeredCollectorMap.entrySet()) {
			Collector collector = entry.getValue();
			collectedInfo.put(collector.getClass().getSimpleName(), collector.collect());
		}
		collecting = false;
		log.info("finish collect info, time uesd: " + (System.currentTimeMillis() - start) + "ms");
		return collectedInfo;
	}

}
