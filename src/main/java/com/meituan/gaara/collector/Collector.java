/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.io.Serializable;

/**
 * 信息收集器接口
 * 
 * @author lichengwu
 * @created 2012-2-15
 * 
 * @version 1.0
 */
public interface Collector {

	/**
	 * 收集数据
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @return
	 */
	Serializable collect();

	/**
	 * 获得收集器的名字 <br >
	 * 推荐用Collector类名作为唯一名字
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 销毁收集器
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 */
	void destory();

}
