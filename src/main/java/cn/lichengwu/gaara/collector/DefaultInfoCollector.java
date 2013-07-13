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

import cn.lichengwu.gaara.exception.GaaraException;
import cn.lichengwu.gaara.info.TransientInfo;
import cn.lichengwu.gaara.store.JRobin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.lichengwu.gaara.util.Parameter;
import cn.lichengwu.gaara.util.ParameterUtil;

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

	protected TransientInfo info;

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
	}

	/**
	 * 初始化collector
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 */
	public void init() {
		this.info = getNewInfo();
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
	 * 初始化collector
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @param info
	 *            collector收集的信息
	 */
	public void init(TransientInfo info) {
		this.info = info;
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
	 * @see Collector#destory()
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
	 * @see Collector#collect()
	 */
	@Override
	public Serializable collect() {
		info = getNewInfo();
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
	
	/**
	 * 获得JRobin对象
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 *
	 * @param name
	 * @return
	 */
	public JRobin getJRobin(String name){
		return jRobinMap.get(name);
	}
}
