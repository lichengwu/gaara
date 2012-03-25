/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.info.JavaVirtualMachineInfo;
import com.meituan.gaara.info.TransientInfo;
import com.meituan.gaara.store.JRobin;

/**
 * a colltector for JVM inforamation
 * 
 * @author lichengwu
 * @created 2012-3-25
 * 
 * @version 1.0
 */
public class JavaVirtualMachineInfoCollector extends DefaultInfoCollector {

	/**
	 * @param application
	 */
	public JavaVirtualMachineInfoCollector(String application) {
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
		JavaVirtualMachineInfo jvmInfo = (JavaVirtualMachineInfo) info;
		if (jvmInfo.getIsAverageSystemLoadSupport()) {
			// average system load
			jRobinMap.put("jvm.averageSystemLoad",
			        JRobin.createInstance(application, "jvm.averageSystemLoad", null));
		}
		if (jvmInfo.getIsOracleMBean()) {
			// process cpu time
			jRobinMap.put("jvm.processCpuTime",
			        JRobin.createInstance(application, "jvm.processCpuTime", null));
		}
		if (jvmInfo.getIsOracleMBeanOnUnix()) {
			// open file descriptor count
			jRobinMap.put("jvm.openFileDescriptorCount",
			        JRobin.createInstance(application, "jvm.openFileDescriptorCount", null));
		}
		// thread count
		jRobinMap.put("jvm.threadCount",
		        JRobin.createInstance(application, "jvm.threadCount", null));

	}

	/**
	 * @see com.meituan.gaara.collector.DefaultInfoCollector#saveInfo(com.meituan.gaara.info.TransientInfo)
	 */
	@Override
	protected void saveInfo(TransientInfo info) throws GaaraException {
		JavaVirtualMachineInfo jvmInfo = (JavaVirtualMachineInfo) info;
		if (jvmInfo.getIsAverageSystemLoadSupport()) {
			jRobinMap.get("jvm.averageSystemLoad").addValue(jvmInfo.getAverageSystemLoad());
		}
		if (jvmInfo.getIsOracleMBean()) {
			jRobinMap.get("jvm.processCpuTime").addValue(jvmInfo.getProcessCpuTime());
		}
		if (jvmInfo.getIsOracleMBeanOnUnix()) {
			jRobinMap.get("jvm.openFileDescriptorCount").addValue(
			        jvmInfo.getOpenFileDescriptorCount());
		}
		jRobinMap.get("jvm.threadCount").addValue(jvmInfo.getThreadCount());

	}

	/**
	 * @see com.meituan.gaara.collector.DefaultInfoCollector#getNewInfo()
	 */
	@Override
	protected TransientInfo getNewInfo() {
		JavaVirtualMachineInfo info = JavaVirtualMachineInfo.getInstance();
		info.refresh();
		return info;
	}

}
