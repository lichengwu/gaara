/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.web.html;

/**
 * ftl模板路径
 *
 * @author lichengwu
 * @created 2012-3-20
 *
 * @version 1.0
 */
public enum TemplateFile {
	
	INDEX("index.ftl"),
	APP_INDEX("app_index.ftl");
	
	
	/**
	 * 文件名
	 */
	private String name;
	
	TemplateFile(String name){
		this.name =name;
	}
	
	public String getName(){
		return name;
	}
}
