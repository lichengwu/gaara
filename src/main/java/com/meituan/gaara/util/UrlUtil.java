/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

/**
 * URL工具类
 * 
 * @author lichengwu
 * @created 2012-3-27
 * 
 * @version 1.0
 */
final public class UrlUtil {

	/**
	 * build an url
	 * 
	 * @author lichengwu
	 * @created 2012-3-27
	 * 
	 * @param url
	 *            base url
	 * @param params
	 *            query parameters
	 * @return URL
	 * @throws MalformedURLException
	 */
	public static URL buildURL(String url, Map<String, Object> params) throws MalformedURLException {
		assert url != null;
		boolean hasQuery = url.indexOf('?') != -1;
		StringBuilder query = new StringBuilder(url);
		if (params != null && !params.isEmpty()) {
			if (hasQuery) {
				for (Entry<String, Object> entry : params.entrySet()) {
					query.append("&").append(entry.getKey()).append("=").append(entry.getValue());
				}
			} else {
				query.append('?');
				for (Entry<String, Object> entry : params.entrySet()) {
					query.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
				}
				query.deleteCharAt(query.length() - 1);
			}

		}
		return new URL(query.toString());
	}
}
