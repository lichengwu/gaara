/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.collector.factory.RemoteCollectorFactory;
import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;

/**
 * 收集器服务
 * 
 * @author lichengwu
 * @created 2012-2-15
 * 
 * @version 1.0
 */
public class CollectorServer {

	private static final Log log = LogFactory.getLog(CollectorServer.class);

	/**
	 * gaara运行模式
	 */
	private static final String RUN_MODE = ParameterUtil.getParameter(Parameter.GAARA_RUN_MODE);

	private static CollectorServer INSTANCE = new CollectorServer();

	private Serializable recentInfo = null;

	/**
	 * 标记收集状态
	 */
	private boolean collecting = false;

	/**
	 * 定时器
	 */
	private Timer localTimer;

	private CollectorServer() {
		localTimer = new Timer("collector_server", true);
	}

	/**
	 * 获得{@link CollectorServer}实例。
	 * <p>
	 * <b>注意：</b>{@link CollectorServer}是一个单例，在全局只有一个。
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @return
	 */
	public static CollectorServer getInstance() {
		return INSTANCE;
	}

	/**
	 * 收集信息
	 * <p>
	 * 这个方法是同步的，只会执行一次，方法内存开启一个任务执行收集。
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-2-15
	 * 
	 */
	public synchronized void doCollect() {
		if (collecting) {
			log.warn("collect local info already started!");
			return;
		}
		// 收集时间间隔
		long interval = ParameterUtil.getParameterAsInt(Parameter.COLLECT_RATE) * 1000L;
		localTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if ("client".equals(RUN_MODE)) {
					// 搜集本地
					collectLocalApplication();
				} else if ("server".equals(RUN_MODE)) {
					// 收集远程
					collectRemoteApllications();
				} else {
					// 混合模式
					collectLocalApplication();
					collectRemoteApllications();
				}
			}
		}, 500, interval);
		collecting = true;
	}

	/**
	 * 收集本地信息
	 * 
	 * @author lichengwu
	 * @created 2012-3-18
	 * 
	 */
	private void collectLocalApplication() {
		recentInfo = LocalCollectorController.getInstance().doCollect();
	}

	/**
	 * 收集远程服务器信息
	 * 
	 * @author lichengwu
	 * @created 2012-3-18
	 * 
	 */
	private void collectRemoteApllications() {
		Map<String, RemoteCollector> remoteCollectorMap = RemoteCollectorFactory
		        .getInstance().getRegisteredRemoteCollectorMap();
		if (!remoteCollectorMap.isEmpty()) {
			for (Entry<String, RemoteCollector> entry : remoteCollectorMap.entrySet()) {
				RemoteCollector remoteCollector = entry.getValue();
				try {
					remoteCollector.collectRemoteApplication();
				} catch (GaaraException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 获得本地最新监控信息
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @return 本地最新监控信息
	 */
	public Serializable getLastestInfo() {
		return recentInfo;
	}

	/**
	 * 取消监控(同步)
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 */
	public synchronized void stop() {
		localTimer.cancel();
		collecting = false;
	}
}
