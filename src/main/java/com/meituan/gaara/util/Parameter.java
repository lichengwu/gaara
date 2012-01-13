/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

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
	CONTEXT_PATH("context_path");

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
