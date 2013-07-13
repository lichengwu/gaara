/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.collector;

import cn.lichengwu.gaara.exception.GaaraException;
import cn.lichengwu.gaara.info.JavaVirtualMachineInfo;
import cn.lichengwu.gaara.info.TransientInfo;
import cn.lichengwu.gaara.store.JRobin;

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
		JavaVirtualMachineInfo jvmInfo = (JavaVirtualMachineInfo) info;
		if (jvmInfo.getIsAverageSystemLoadSupport()) {
			// average system load
			jRobinMap.put("jvm.averageSystemLoad",
			        JRobin.createInstance(application, "jvm.averageSystemLoad", null));
		}
		if (jvmInfo.getIsOracleMBean()) {
			// process cpu time
			jRobinMap.put("jvm.cpu",
			        JRobin.createInstance(application, "jvm.cpu", null));
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
	 * @see DefaultInfoCollector#saveInfo(cn.lichengwu.gaara.info.TransientInfo)
	 */
	@Override
	protected void saveInfo(TransientInfo info) throws GaaraException {
		JavaVirtualMachineInfo jvmInfo = (JavaVirtualMachineInfo) info;
		if (jvmInfo.getIsAverageSystemLoadSupport()) {
			jRobinMap.get("jvm.averageSystemLoad").addValue(jvmInfo.getAverageSystemLoad());
		}
		if (jvmInfo.getIsOracleMBean()) {
			jRobinMap.get("jvm.cpu").addValue(jvmInfo.getCpuUsage());
		}
		if (jvmInfo.getIsOracleMBeanOnUnix()) {
			jRobinMap.get("jvm.openFileDescriptorCount").addValue(
			        jvmInfo.getOpenFileDescriptorCount());
		}
		jRobinMap.get("jvm.threadCount").addValue(jvmInfo.getThreadCount());

	}

	/**
	 * @see DefaultInfoCollector#getNewInfo()
	 */
	@Override
	protected TransientInfo getNewInfo() {
		JavaVirtualMachineInfo info = JavaVirtualMachineInfo.getInstance();
		info.refresh();
		return info;
	}

}
