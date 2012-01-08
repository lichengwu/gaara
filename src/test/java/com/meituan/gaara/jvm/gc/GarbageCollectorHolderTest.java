/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.jvm.gc;

import org.junit.Test;


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
		GarbageCollectorHolder gcHolder = GarbageCollectorHolder.getInstance();
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
		GarbageCollectorHolder gcHolder = GarbageCollectorHolder.getInstance();
		for(GarbageCollector gc : gcHolder.describe()){
			System.out.println(gc.toString());
		}
	}
}