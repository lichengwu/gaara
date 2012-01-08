/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.jvm;

/**
 * 垃圾回收器属性
 * 
 * @author lichengwu
 * @created 2012-1-8
 * 
 * @version 1.0
 */
public enum GarbageCollectorAttribute {

	/**
	 * 垃圾回收器的名字
	 */
	NAME("Name"),
	/**
	 * 发生垃圾回收的次数
	 */
	COLLECTIONCOUNT("CollectionCount"),
	/**
	 * 垃圾回收器累计消耗的时间(单位ms)
	 */
	COLLECTIONTIME("CollectionTime"),
	/**
	 * {@link java.lang.management.GarbageCollectorMXBean}所管理的所有
	 * {@link java.lang.management.MemoryPoolMXBean}名字。 即所有的
	 * {@link java.lang.management.MemoryPoolMXBean#getName()}。
	 */
	MEMORYPOOLNAMES("MemoryPoolNames"),
	/**
	 * 最后一次垃圾回收详细信息
	 */
	LASTGCINFO("LastGcInfo"),
	/**
	 * 垃圾回收器是否否有效
	 */
	VALID("Valid");

	private String code;

	private GarbageCollectorAttribute(String code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}
