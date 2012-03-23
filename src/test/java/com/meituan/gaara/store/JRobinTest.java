/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jrobin.core.RrdException;
import org.junit.Test;

import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.test.BaseTest;
import com.meituan.gaara.util.TimeRange;


/**
 *
 * @author lichengwu
 * @created 2012-2-14
 *
 * @version 1.0
 */
public class JRobinTest extends BaseTest{

	@Test
	public void test() throws RrdException, IOException, GaaraException, InterruptedException{
		JRobin robin = JRobin.createInstance("gaara", "memory.used",null);
		long now = System.currentTimeMillis();
		List<Object> list = new ArrayList<Object>();
		for(int i=0;i<20;i++){
			list.add(new double[(int)Math.random()*1000000]);
			robin.addValue((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024);
			TimeUnit.SECONDS.sleep(1);
		}
		File file = new File("memory.png");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(robin.graph(TimeRange.createCustomRange(new Date(now), new Date(now+1000*20)), 800, 800));
		fos.close();
	}
}
