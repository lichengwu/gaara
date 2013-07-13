/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.util;

/**
 * 参数枚举
 * 
 * @author lichengwu
 * @created 2012-1-13
 * 
 * @version 1.0
 */
public enum Parameter {

	/**
	 * 当前Gaara版本号
	 */
	GAARA_VERSION("gaara.version"),
	/**
	 * 主机名称
	 */
	HOST_NAME("host_name"),
	/**
	 * 主机地址
	 */
	HOST_ADDRESS("host_address"),

	/**
	 * 当前应用名字
	 */
	WEB_APPLICATION_NAME("web_application_name"),

	/**
	 * 上下文路径
	 */
	CONTEXT_PATH("context_path"),
	/**
	 * 系统名称
	 */
	OS_NAME("os.name"),

	/**
	 * gaara文件存储路径
	 */
	STORAGE_DIRECTORY("storage_directory"),

	/**
	 * 收集频率
	 */
	COLLECT_RATE("gaara.collect_rate"),

	/**
	 * java版本号
	 */
	JAVA_VERSION("java.version"),

	/**
	 * PID
	 */
	PID("java_pid"),

	/**
	 * gaara运行模式
	 */
	GAARA_RUN_MODE("gaara.run.mode"),

	/**
	 * 已注册的本地收集器
	 */
	REGISTERED_LOCAL_COLLECTORS("gaara.registered.local.collectors"),

	/**
	 * 已注册的远程收集器
	 */
	REGISTERED_REMOTE_COLLECTOR("gaara.registered.remote.collectors"),

	/**
	 * 系统启动时间
	 */
	APPLICATION_START_TIME("gaara.start.time");

	private String name;

	private Parameter(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
