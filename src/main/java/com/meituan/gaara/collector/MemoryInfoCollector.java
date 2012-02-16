/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.jvm.memory.MemoryInfo;
import com.meituan.gaara.store.JRobin;

/**
 * 内存信息收集器
 * 
 * @author lichengwu
 * @created 2012-2-16
 * 
 * @version 1.0
 */
public class MemoryInfoCollector extends DefaultStorageCollector {
	
	
	private static final Log log = LogFactory.getLog(MemoryInfoCollector.class);

	/**
	 * rdd数据库存储实例
	 */
	private Map<String, JRobin> jRobinMap = new HashMap<String, JRobin>(0);

	/**
	 * 应用名字
	 */
	private String application;

	/**
	 * 创建内存收集器实例
	 * 
	 * @param application
	 * @throws GaaraException
	 */
	public MemoryInfoCollector(String application) throws GaaraException {
		this.application = application;
		if (!client) {
			initJRobin();
		}
	}

	/**
	 * 初始化JRobin
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @throws GaaraException
	 */
	private void initJRobin() throws GaaraException {
		// 堆内存
		jRobinMap.put("memory.used.heap", JRobin.createInstance(application, "memory.used"));
		// 永久带内存
		jRobinMap.put("memory.used.PermGen",
		        JRobin.createInstance(application, "memory.used.PermGen"));
		// 老年代内存
		jRobinMap.put("memory.used.heap.OldGen",
		        JRobin.createInstance(application, "memory.used.heap.OldGen"));
		// 新生代内存
		jRobinMap.put("memory.used.Eden", JRobin.createInstance(application, "memory.used.Eden"));
		// 非堆内存
		jRobinMap.put("memory.used.NonHeap",
		        JRobin.createInstance(application, "memory.used.NonHeap"));
		// 交换空间 TODO 是否支持物理内存和交换空间？
		// jRobinMap.put("momory.used.SwapSpace",
		// JRobin.createInstance(application, "momory.used.SwapSpace"));
		// Survivor内存
		jRobinMap.put("memory.used.Survivor",
		        JRobin.createInstance(application, "memory.used.Survivor"));
	}

	/**
	 * @see com.meituan.gaara.collector.Collector#collect()
	 */
	@Override
	public MemoryInfo collect() {
		try {
			MemoryInfo memoryInfo = MemoryInfo.getInstance();
			memoryInfo.refresh();
			if (client) {
				log.debug("client模式下不支持存储");
			} else {
				jRobinMap.get("memory.used.heap").addValue(memoryInfo.getUsedHeapSize());
				jRobinMap.get("memory.used.PermGen").addValue(memoryInfo.getUsedPermGenSize());
				jRobinMap.get("memory.used.heap.OldGen").addValue(memoryInfo.getUsedOldGenSize());
				jRobinMap.get("memory.used.Eden").addValue(memoryInfo.getUsedEdenSize());
				jRobinMap.get("memory.used.NonHeap").addValue(memoryInfo.getUsedNoHeapMaxSize());
				jRobinMap.get("memory.used.Survivor").addValue(memoryInfo.getUsedSurvivorSize());
			}
			return memoryInfo;
		} catch (GaaraException e) {
			return null;
		}
	}

	/**
     * @see com.meituan.gaara.collector.Collector#destory()
     */
    @Override
    public void destory() {
    	for(Entry<String, JRobin> entry : jRobinMap.entrySet()){
    		JRobin jRobin = entry.getValue();
    		try {
	            jRobin.stop();
	            jRobin.delete();
	            jRobin =null;
            } catch (GaaraException e) {
	            log.error("error occur while destory "+this.getClass().getSimpleName()+":"+e.getMessage());
            }finally{
            	jRobin=null;
            }
    	}
    }

	/**
     * @see com.meituan.gaara.collector.Collector#getName()
     */
    @Override
    public String getName() {
	    return this.getClass().getSimpleName();
    }
}
