/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.collector;

import cn.lichengwu.gaara.info.TransientInfo;
import cn.lichengwu.gaara.store.JRobin;
import cn.lichengwu.gaara.exception.GaaraException;
import cn.lichengwu.gaara.info.MemoryInfo;

/**
 * 内存信息收集器
 * 
 * @author lichengwu
 * @created 2012-2-16
 * 
 * @version 1.0
 */
public class MemoryInfoCollector extends DefaultInfoCollector {

	/**
	 * 创建内存收集器实例
	 * 
	 * @param application
	 * @throws GaaraException
	 */
	public MemoryInfoCollector(String application) {
		super(application);
	}

	/**
	 * 初始化JRobin
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @throws GaaraException
	 */
	protected void initJRobin() throws GaaraException {
		// 堆内存
		jRobinMap.put("memory.used.heap", JRobin.createInstance(application, "memory.used", null));
		// 永久带内存
		jRobinMap.put("memory.used.PermGen",
		        JRobin.createInstance(application, "memory.used.PermGen", null));
		// 老年代内存
		jRobinMap.put("memory.used.heap.OldGen",
		        JRobin.createInstance(application, "memory.used.heap.OldGen", null));
		// 新生代内存
		jRobinMap.put("memory.used.Eden",
		        JRobin.createInstance(application, "memory.used.Eden", null));
		// 非堆内存
		jRobinMap.put("memory.used.NonHeap",
		        JRobin.createInstance(application, "memory.used.NonHeap", null));
		// 交换空间 TODO 是否支持物理内存和交换空间？
		// jRobinMap.put("momory.used.SwapSpace",
		// JRobin.createInstance(application, "momory.used.SwapSpace"));
		// Survivor内存
		jRobinMap.put("memory.used.Survivor",
		        JRobin.createInstance(application, "memory.used.Survivor", null));
	}

	/**
	 * @see Collector#getName()
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * @see DefaultInfoCollector#saveInfo(cn.lichengwu.gaara.info.TransientInfo)
	 */
	@Override
	protected void saveInfo(TransientInfo info) throws GaaraException {
		MemoryInfo newInfo = (MemoryInfo) info;
		jRobinMap.get("memory.used.heap").addValue(newInfo.getUsedHeapSize());
		jRobinMap.get("memory.used.PermGen").addValue(newInfo.getUsedPermGenSize());
		jRobinMap.get("memory.used.heap.OldGen").addValue(newInfo.getUsedOldGenSize());
		jRobinMap.get("memory.used.Eden").addValue(newInfo.getUsedEdenSize());
		jRobinMap.get("memory.used.NonHeap").addValue(newInfo.getUsedNoHeapMaxSize());
		jRobinMap.get("memory.used.Survivor").addValue(newInfo.getUsedSurvivorSize());
	}

	/**
	 * @see DefaultInfoCollector#getNewInfo()
	 */
	@Override
	protected TransientInfo getNewInfo() {
		MemoryInfo info = MemoryInfo.getInstance();
		info.refresh();
		return info;
	}
}
