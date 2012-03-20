/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.web;

/**
 * Http请求类型
 * 
 * @author lichengwu
 * @created 2012-3-20
 * 
 * @version 1.0
 */
public enum RequestType {

	/**
	 * com.meituan.gaara.resources 下的资源
	 */
	RESOURCE("resource"),
	/**
	 * JRobin图像
	 */
	GRAPH("graph");

	private String name;

	RequestType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static RequestType getRequestType(String name){
		assert name!=null;
		for(RequestType type : RequestType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return null;
	}

}
