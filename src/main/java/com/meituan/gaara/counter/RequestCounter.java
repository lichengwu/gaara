/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.counter;

import java.io.Serializable;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link HttpServletRequest}请求计数器
 * 
 * @author lichengwu
 * @created 2012-1-20
 * 
 * @version 1.0
 */
public class RequestCounter implements Counter, Cloneable, Serializable {

	private static final long serialVersionUID = 5372773688992982639L;

	/**
	 * 构造方法
	 * 
	 * @param name
	 *            url/uri
	 * @param showName
	 *            可读名字,用于生成唯一id = showName + "_" + {@link UUID#randomUUID()}
	 */
	public RequestCounter(String name, String showName) {
		assert name != null;
		assert showName != null;
		this.name = name;
		this.id = showName + "_" + UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 请求名字(url/uri)
	 */
	private String name;

	/**
	 * 请求id(唯一)
	 */
	private String id;

	/**
	 * 命中次数
	 */
	private long hits;

	/**
	 * 持续时间
	 */
	private long durationTime;

	/**
	 * 这个请求的最长时间
	 */
	private long maxTime;

	/**
	 * 这个请求的最短时间
	 */
	private long minTime;

	/**
	 * 总计CPU耗时
	 */
	private long cupTimeCount;

	/**
	 * 总计响应大小
	 */
	private long responseSizeCount;

	/**
	 * 总计系统错误请求次数
	 */
	private long systemErrorCount;

	/**
	 * @see com.meituan.gaara.counter.Counter#add(com.meituan.gaara.counter.Counter)
	 */
	@Override
	public void add(Counter counter) {
		RequestCounter requestCounter = (RequestCounter) counter;
		if (requestCounter.getHits() > 0) {
			this.hits += requestCounter.getHits();
		}
		this.maxTime = Math.max(this.maxTime, requestCounter.getMaxTime());
		this.minTime = Math.min(this.minTime, requestCounter.getMinTime());
		this.cupTimeCount += requestCounter.getCupTimeCount();
		this.durationTime += requestCounter.getDurationTime();
		this.responseSizeCount += requestCounter.getResponseSizeCount();
		this.systemErrorCount += requestCounter.getSystemErrorCount();

	}

	/**
	 * 获得请求平均相应时间
	 * 
	 * @author lichengwu
	 * @created 2012-1-20
	 * 
	 * @return 返回以毫秒为单位的平均相应时间，或者-1 如果没有任何请求。
	 */
	public long getAverageDurationTime() {
		if (this.hits > 0) {
			return durationTime / hits;
		}
		return -1;
	}

	/**
	 * 获得http响应数据品均大小
	 * 
	 * @author lichengwu
	 * @created 2012-1-20
	 * 
	 * @return 以字节为单位的品均响应数据大小，或者-1如果没有任何响应。
	 */
	public long getAverageResponseSize() {
		if (this.hits > 0) {
			return responseSizeCount / hits;
		}
		return -1;
	}

	/**
	 * 获得这个请求的平均cpu消耗
	 * 
	 * @author lichengwu
	 * @created 2012-1-20
	 * 
	 * @return 返回以毫秒为单位的平均cpu耗时，或者-1如果没有cpu消耗
	 */
	public long getAverageCupTime() {
		if (this.hits > 0) {
			return cupTimeCount / hits;
		}
		return -1;
	}
	
	/**
	 * 返回这个请求系统错误的百分比
	 * 
	 * @author lichengwu
	 * @created 2012-1-20
	 *
	 * @return 返回这个请求系统错误的百分比(0~100)
	 */
	public double getAverageSystemErrorPercent(){
		if(this.hits>0){
			return Math.min(100d*systemErrorCount/hits, 100d);
		}
		return 0d;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Object clone = super.clone();
		return clone;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the hits
	 */
	public long getHits() {
		return hits;
	}

	/**
	 * @return the durationTime
	 */
	public long getDurationTime() {
		return durationTime;
	}

	/**
	 * @return the maxTime
	 */
	public long getMaxTime() {
		return maxTime;
	}

	/**
	 * @return the minTime
	 */
	public long getMinTime() {
		return minTime;
	}

	/**
	 * @return the cupTimeCount
	 */
	public long getCupTimeCount() {
		return cupTimeCount;
	}

	/**
	 * @return the responseSizeCount
	 */
	public long getResponseSizeCount() {
		return responseSizeCount;
	}

	/**
	 * @return the systemErrorCount
	 */
	public long getSystemErrorCount() {
		return systemErrorCount;
	}

}
