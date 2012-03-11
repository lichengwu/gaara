/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.util.Timer;
import java.util.TimerTask;

import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;

/**
 * 本地收集器服务
 * 
 * @author lichengwu
 * @created 2012-2-15
 * 
 * @version 1.0
 */
public class LocalCollectorServer {
	
	/**
	 * 定时器
	 */
	private Timer timer;
	
	
	private LocalCollectorServer(){
	}

	/**
	 * 收集信息
	 * 
	 * @author lichengwu
	 * @created 2012-2-15
	 *
	 */
	public void doCollect() {
		// 收集时间间隔
		long interval = ParameterUtil.getParameterAsInt(Parameter.COLLECT_RATE) * 1000L;
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
			}
		}, 500, interval);
	}

	public synchronized void stop() {
		timer.cancel();
	}
}
