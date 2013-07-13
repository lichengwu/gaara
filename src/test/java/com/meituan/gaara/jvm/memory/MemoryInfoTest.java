/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.jvm.memory;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import cn.lichengwu.gaara.info.MemoryInfo;

/**
 * test for com.meituan.gaara.jvm.memory.MemoryInformation
 * 
 * @author lichengwu
 * @created 2012-1-8
 * 
 * @version 1.0
 */
public class MemoryInfoTest {

	/**
	 * JVM args : -XX:+UseConcMarkSweepGC -Xmx60m -Xms60m -Xmn15m
	 * -XX:PermSize=20m -XX:MaxPermSize=20m -XX:MaxPermSize=20m
	 * -XX:SurvivorRatio=8
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 */
	@Test
	public void test() {
		MemoryInfo memoryInfo = MemoryInfo.getInstance();
		Assert.assertNotNull(memoryInfo);
		Assert.assertEquals(memoryInfo.getMaxPermGenSize(), 20 * 1024 * 1024);
		// PermSize * SurvivorRatio
		Assert.assertEquals(memoryInfo.getMaxEdenSize(), 15 * 1024 * 1024 * 8 / 10);
		// PermSize/10
		Assert.assertEquals(memoryInfo.getMaxSurvivorSize(), 15 * 1024 * 1024 / 10);
		// HeapSize - EdenSize - 2 * SurvivorSize
		Assert.assertEquals(memoryInfo.getMaxOldGenSize(), 45 * 1024 * 1024);

		Assert.assertTrue(memoryInfo.getMaxHeapSize() < 60 * 1024 * 1024);

		System.out.println(memoryInfo.toString());
	}
	
	@Test
	public void testRefresh(){
		List<int []> list = new ArrayList<int[]>(0);
		list.add(new int[1024*1024]);
		MemoryInfo memoryInfo = MemoryInfo.getInstance();
		System.out.println(memoryInfo);
		list.add(new int[1024*1024]);
		memoryInfo.refresh();
		System.out.println(memoryInfo);
	}

}
