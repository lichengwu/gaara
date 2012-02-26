/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.jvm.gc;

import org.junit.Test;

import com.meituan.gaara.info.GarbageCollector;
import com.meituan.gaara.info.GarbageCollectorInfo;


/**
 * test for com.meituan.gaara.jvm.gc.GarbageCollectorHolder
 *
 * @author lichengwu
 * @created 2012-1-8
 *
 * @version 1.0
 */
public class GarbageCollectorHolderTest {

	
	/**
	 * JVM args : -XX:+UseConcMarkSweepGC
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 *
	 */
	@Test
	public void testEmpty(){
		GarbageCollectorInfo gcHolder = GarbageCollectorInfo.getInstance();
		for(GarbageCollector gc : gcHolder.describe()){
			System.out.println(gc.toString());
		}
	}
	
	/**
	 * JVM args : -XX:+UseConcMarkSweepGC
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 *
	 */
	@Test
	public void testWithGC(){
		new Object();
		System.gc();
		GarbageCollectorInfo gcHolder = GarbageCollectorInfo.getInstance();
		for(GarbageCollector gc : gcHolder.describe()){
			System.out.println(gc.toString());
		}
	}
}
