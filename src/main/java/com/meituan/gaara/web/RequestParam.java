/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.web;

/**
 * request请求参数
 * 
 * @author lichengwu
 * @created 2012-3-20
 * 
 * @version 1.0
 */
public enum RequestParam {

	/**
	 * 请求类型
	 */
	TYPE("type"),

	/**
	 * 文件名
	 */
	FILE_NAME("fn");

	private String name;

	RequestParam(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
