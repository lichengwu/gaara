/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.services;

import java.util.HashMap;
import java.util.Map;

import com.meituan.gaara.util.ParameterUtil;

/**
 * core services for gaara
 * 
 * @author lichengwu
 * @created 2012-3-27
 * 
 * @version 1.0
 */
public class CoreServices {

	public Map<String, Object> showApplication(String app) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("app", app);
		data.put("application", ParameterUtil.getApplicationProperty(app, "host"));
		return data;
	}

	/**
	 * 首页数据
	 * 
	 * @author lichengwu
	 * @created 2012-4-3
	 * 
	 * @return
	 */
	public Map<String, Object> index() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("apps", ParameterUtil.getApplicationSettings());
		return data;
	}
}
