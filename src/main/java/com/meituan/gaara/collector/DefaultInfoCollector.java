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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.info.TransientInfo;
import com.meituan.gaara.store.JRobin;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;

/**
 * 默认收集器
 * 
 * @author lichengwu
 * @created 2012-2-16
 * 
 * @version 1.0
 */
public abstract class DefaultInfoCollector implements Collector {

	private static final Log log = LogFactory.getLog(DefaultInfoCollector.class);

	/**
	 * rdd数据库存储实例
	 */
	protected Map<String, JRobin> jRobinMap = new HashMap<String, JRobin>(0);

	/**
	 * 获得gaara运行的模式
	 */
	protected boolean client = "client"
	        .equals(ParameterUtil.getParameter(Parameter.GAARA_RUN_MODE));

	/**
	 * 应用程序名字
	 */
	protected String application;

	public DefaultInfoCollector(String application) {
		this.application = application;
		try {
			// client模式不存储数据
			if (!client) {
				initJRobin();
			}
		} catch (GaaraException e) {
			log.error("error occur while creating collector[" + getName() + "]:" + e.getMessage(),
			        e);
		}
	}

	/**
	 * 初始化JRobin
	 * 
	 * @author lichengwu
	 * @created 2012-3-3
	 * 
	 * @throws GaaraException
	 */
	protected abstract void initJRobin() throws GaaraException;

	/**
	 * @see com.meituan.gaara.collector.Collector#destory()
	 */
	@Override
	public void destory() {
		for (Entry<String, JRobin> entry : jRobinMap.entrySet()) {
			JRobin jRobin = entry.getValue();
			try {
				jRobin.stop();
				jRobin.delete();
				jRobin = null;
			} catch (GaaraException e) {
				log.error("error occur while destory collector [" + getName() + "]", e);
			} finally {
				jRobin = null;
			}
		}
	}

	/**
	 * @see com.meituan.gaara.collector.Collector#collect()
	 */
	@Override
	public Serializable collect() {
		TransientInfo info = getNewInfo();
		if (client) {
			log.info("client mode does not store monitoring information");
		} else {
			try {
				saveInfo(info);
			} catch (GaaraException e) {
				log.error("can not collect info from collector[" + getName() + "]", e);
			}
		}
		return info;
	}

	/**
	 * 将监控信息写入rrd文件
	 * 
	 * @author lichengwu
	 * @created 2012-3-3
	 * 
	 * @return
	 * @throws GaaraException
	 */
	protected abstract void saveInfo(TransientInfo info) throws GaaraException;

	/**
	 * 获得最新的监控信息
	 * 
	 * @author lichengwu
	 * @created 2012-3-3
	 * 
	 * @return
	 */
	protected abstract TransientInfo getNewInfo();
}
