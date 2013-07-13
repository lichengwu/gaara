/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import java.util.concurrent.TimeUnit;

import cn.lichengwu.gaara.collector.LocalCollectorController;
import org.junit.Test;

import cn.lichengwu.gaara.info.MemoryInfo;
import com.meituan.gaara.test.BaseTest;


/**
 *
 * @author lichengwu
 * @created 2012-2-16
 *
 * @version 1.0
 */
public class LocalCollectorControllerTest extends BaseTest {
	
	@Test
	public void test() throws InterruptedException{
		LocalCollectorController controller = LocalCollectorController.getInstance();
		MemoryInfo data = (MemoryInfo) controller.doCollect();
		System.out.println(data);
		System.out.println(System.getProperty("java.io.tmpdir"));
		TimeUnit.SECONDS.sleep(1000);
	}

}
