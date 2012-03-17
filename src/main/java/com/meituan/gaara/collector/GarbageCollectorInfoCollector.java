/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.util.List;

import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.info.GarbageCollector;
import com.meituan.gaara.info.GarbageCollectorInfo;
import com.meituan.gaara.info.TransientInfo;
import com.meituan.gaara.store.JRobin;
import com.meituan.gaara.util.I18N;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;

/**
 * 垃圾收集器信息收集器
 * 
 * @author lichengwu
 * @created 2012-3-3
 * 
 * @version 1.0
 */
public class GarbageCollectorInfoCollector extends DefaultInfoCollector {

	/**
	 * @param application
	 */
	public GarbageCollectorInfoCollector(String application) {
		super(application);
	}

	/**
	 * @see com.meituan.gaara.collector.Collector#getName()
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * @see com.meituan.gaara.collector.DefaultInfoCollector#initJRobin()
	 */
	@Override
	protected void initJRobin() throws GaaraException {
		GarbageCollectorInfo gcInfo = (GarbageCollectorInfo) info;
		// 按照垃圾收集器的个数存储
		List<GarbageCollector> describe = gcInfo.describe();
		for (GarbageCollector gc : describe) {
			if (gc.isValid()) {
				// 吞吐量数据
				jRobinMap.put(
				        gc.getName() + " gc_throughputs",
				        JRobin.createInstance(application,
				                gc.getName() + " " + I18N.getString("gc_throughputs")));
				// 平均gc暂停时间数据
				jRobinMap.put(
				        gc.getName() + " gc_average_pause_time",
				        JRobin.createInstance(application,
				                gc.getName() + " " + I18N.getString("gc_average_pause_time")));
			}
		}
	}

	/**
	 * @see com.meituan.gaara.collector.DefaultInfoCollector#saveInfo(com.meituan.gaara.info.TransientInfo)
	 */
	@Override
	protected void saveInfo(TransientInfo info) throws GaaraException {
		GarbageCollectorInfo newInfo = (GarbageCollectorInfo) info;
		List<GarbageCollector> describe = newInfo.describe();
		// 应用已运行的时间
		long applicationRuntime = System.currentTimeMillis()
		        - Long.valueOf(ParameterUtil.getParameter(Parameter.APPLICATION_START_TIME));
		for (GarbageCollector gc : describe) {
			// 吞吐量数据
			jRobinMap.get(gc.getName() + " gc_throughputs").addValue(
			        (applicationRuntime - gc.getCollectionTime()) * 100 / applicationRuntime);
			// 平均gc暂停时间数据
			jRobinMap.get(gc.getName() + " gc_average_pause_time").addValue(
			        (gc.getCollectionTime() / Math.max(1, gc.getCollectionCount())));
		}

	}

	/**
	 * @see com.meituan.gaara.collector.DefaultInfoCollector#getNewInfo()
	 */
	@Override
	protected TransientInfo getNewInfo() {
		GarbageCollectorInfo info = GarbageCollectorInfo.getInstance();
		info.refresh();
		return info;
	}

}
