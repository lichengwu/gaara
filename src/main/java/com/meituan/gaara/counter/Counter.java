/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.counter;

/**
 * 计数器接口
 *
 * @author lichengwu
 * @created 2012-1-20
 *
 * @version 1.0
 */
public interface Counter {
	
	/**
	 * 计数器累加
	 * 
	 * @author lichengwu
	 * @created 2012-1-20
	 *
	 * @param counter
	 */
	void add(Counter counter);
}
