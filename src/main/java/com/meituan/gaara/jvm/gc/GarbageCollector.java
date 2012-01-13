/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.jvm.gc;

import java.io.Serializable;

/**
 * 垃圾回收信息
 * 
 * @author lichengwu
 * @created 2012-1-8
 * 
 * @version 1.0
 */
final public class GarbageCollector implements Serializable {

	private static final long serialVersionUID = 8048217287804398922L;

	/**
	 * 垃圾回收器的名字
	 */
	private String name;

	/**
	 * 发生垃圾回收的次数
	 */
	private long collectionCount;

	/**
	 * 垃圾回收器累计消耗的时间(单位ms)
	 */
	private long collectionTime;

	/**
	 * 最后一次垃圾回收详细信息
	 */
	private String lastGcInfo;

	/**
	 * {@link java.lang.management.GarbageCollectorMXBean}所管理的所有
	 * {@link java.lang.management.MemoryPoolMXBean}名字。 即所有的
	 * {@link java.lang.management.MemoryPoolMXBean#getName()}。
	 */
	private String[] memoryPoolNames;

	/**
	 * 垃圾回收器是否否有效
	 */
	private boolean valid;

	/**
	 * 获得垃圾回收器的名字
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置垃圾回收器的名字
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @param name
	 */
	public void setName(String name) {
		assert name != null;
		this.name = name;
	}

	/**
	 * 获得发生垃圾回收的次数
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return
	 */
	public long getCollectionCount() {
		return collectionCount;
	}

	/**
	 * 设置发生垃圾回收的次数
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @param collectionCount
	 */
	public void setCollectionCount(long collectionCount) {
		assert collectionCount >= 0;
		this.collectionCount = collectionCount;
	}

	/**
	 * 获得垃圾回收器累计消耗的时间(单位ms)
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return
	 */
	public long getCollectionTime() {
		return collectionTime;
	}

	/**
	 * 设置垃圾回收器累计消耗的时间(单位ms)
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @param collectionTime
	 */
	public void setCollectionTime(long collectionTime) {
		assert collectionTime >= 0;
		this.collectionTime = collectionTime;
	}

	/**
	 * 获得最后一次垃圾回收详细信息
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 最后一次垃圾回收详细信息，如果信息不存在，返回null。
	 */
	public String getLastGcInfo() {
		return lastGcInfo;
	}

	/**
	 * 设置垃圾回收器是否否有效
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @param lastGcInfo
	 */
	public void setLastGcInfo(String lastGcInfo) {
		this.lastGcInfo = lastGcInfo;
	}

	/**
	 * 获得 {@link java.lang.management.GarbageCollectorMXBean}所管理的所有
	 * {@link java.lang.management.MemoryPoolMXBean}名字。 即所有的
	 * {@link java.lang.management.MemoryPoolMXBean#getName()}。
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return
	 */
	public String[] getMemoryPoolNames() {
		return memoryPoolNames;
	}

	/**
	 * 设置 {@link java.lang.management.GarbageCollectorMXBean}所管理的所有
	 * {@link java.lang.management.MemoryPoolMXBean}名字。 即所有的
	 * {@link java.lang.management.MemoryPoolMXBean#getName()}。
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return
	 */
	public void setMemoryPoolNames(String[] memoryPoolNames) {
		assert memoryPoolNames != null;
		assert this.memoryPoolNames.length > 0;
		this.memoryPoolNames = memoryPoolNames;
	}

	/**
	 * 获得垃圾回收器是否否有效
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * 设置
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @param valid
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("GarbageCollector [name=");
	    builder.append(name);
	    builder.append(", collectionCount=");
	    builder.append(collectionCount);
	    builder.append(", collectionTime=");
	    builder.append(collectionTime);
	    builder.append(", valid=");
	    builder.append(valid);
	    builder.append("]");
	    return builder.toString();
    }
    
    /**
     * 垃圾回收器属性
     * 
     * @author lichengwu
     * @created 2012-1-8
     * 
     * @version 1.0
     */
    public static enum Attribute {

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

    	private Attribute(String code) {
    		this.code = code;
    	}

    	/**
    	 * @return the code
    	 */
    	public String getCode() {
    		return code;
    	}
    }

}
