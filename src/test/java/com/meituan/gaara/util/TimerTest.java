/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 *
 * @author lichengwu
 * @created 2012-2-15
 *
 * @version 1.0
 */
public class TimerTest {

	@Test
	public void test() throws InterruptedException{
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println(System.currentTimeMillis());
			}
		}, 0,1000);
		TimeUnit.SECONDS.sleep(10);
	}
}
