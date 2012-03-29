/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 *
 * @author lichengwu
 * @created 2012-3-27
 *
 * @version 1.0
 */
public class UrlUtilTest {

	@Test
	public void test() throws MalformedURLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "oliver");
		params.put("time", System.currentTimeMillis());
		System.out.println(UrlUtil.buildURL("http://localhost:8080/gaara", params));
		System.out.println(UrlUtil.buildURL("http://localhost:8080/gaara?aa=a", params));
		System.out.println(UrlUtil.buildURL("http://localhost:8080/gaara?aa=a", null));
	}

}
