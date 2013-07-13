/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.collector;

import java.util.List;

import cn.lichengwu.gaara.store.JRobin;
import cn.lichengwu.gaara.exception.GaaraException;
import cn.lichengwu.gaara.info.GarbageCollector;
import cn.lichengwu.gaara.info.GarbageCollectorInfo;
import cn.lichengwu.gaara.info.TransientInfo;
import cn.lichengwu.gaara.util.Parameter;
import cn.lichengwu.gaara.util.ParameterUtil;

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
	 * @see Collector#getName()
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * @see DefaultInfoCollector#initJRobin()
	 */
	@Override
	protected void initJRobin() throws GaaraException {
		GarbageCollectorInfo gcInfo = (GarbageCollectorInfo) info;
		// 按照垃圾收集器的个数存储
		List<GarbageCollector> describe = gcInfo.describe();
		for (GarbageCollector gc : describe) {
			if (gc.isValid()) {
				// 吞吐量数据
				String label_throughpus = gc.getName() + " gc_throughputs";
				jRobinMap.put(label_throughpus,
				        JRobin.createInstance(application, "gc_throughputs", gc.getName()));
				// 平均gc暂停时间数据
				String label_pause_time = gc.getName() + " gc_average_pause_time";
				jRobinMap.put(label_pause_time,
				        JRobin.createInstance(application, "gc_average_pause_time",gc.getName()));
			}
		}
	}

	/**
	 * @see DefaultInfoCollector#saveInfo(cn.lichengwu.gaara.info.TransientInfo)
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
			String label_throughpus = gc.getName() + " gc_throughputs";
			jRobinMap.get(label_throughpus).addValue(
			        (applicationRuntime - gc.getCollectionTime()) * 100 / applicationRuntime);
			// 平均gc暂停时间数据
			String label_pause_time = gc.getName() + " gc_average_pause_time";
			jRobinMap.get(label_pause_time).addValue(
			        (gc.getCollectionTime() / Math.max(1, gc.getCollectionCount())));
		}

	}

	/**
	 * @see DefaultInfoCollector#getNewInfo()
	 */
	@Override
	protected TransientInfo getNewInfo() {
		GarbageCollectorInfo info = GarbageCollectorInfo.getInstance();
		info.refresh();
		return info;
	}

}
