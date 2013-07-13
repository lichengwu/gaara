/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.web;

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
	 * 资源名字
	 */
	KEY("key"),

	/**
	 * 宽度
	 */
	WIDTH("width"),

	/**
	 * 高度
	 */
	HEIGHT("height"),

	/**
	 * 应用名字
	 */
	APPLICATION("app"),

	/**
	 * 周期
	 */
	PERIHOD("period"),

	/**
	 * 开始日期
	 */
	START_DATE("start"),
	
	/**
	 * 结束日期
	 */
	END_DATE("end"),
	
	/**
	 * 图像名称
	 */
	GRAPH_NAME("gname");

	private String name;

	RequestParam(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
