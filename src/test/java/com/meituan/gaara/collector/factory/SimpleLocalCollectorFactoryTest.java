/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector.factory;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.meituan.gaara.collector.Collector;
import com.meituan.gaara.collector.GarbageCollectorInfoCollector;
import com.meituan.gaara.test.BaseTest;


/**
 *
 * @author lichengwu
 * @created 2012-3-11
 *
 * @version 1.0
 */
public class SimpleLocalCollectorFactoryTest extends BaseTest{
	LocalCollectorFactory instance;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
		instance = LocalCollectorFactory.getInstance();
	}
	
	
	@Test
	public void test(){
		Map<String, Collector> map = instance.getRegisteredLocalCollectorMap();
		for(Entry<String, Collector> entey : map.entrySet()){
			Serializable info = entey.getValue().collect();
			System.out.println(info.toString());
		}
	}
	
	@Test
	public void testAddCollector(){
		Collector collector = instance.addCollector(GarbageCollectorInfoCollector.class.getSimpleName());
		Serializable info = collector.collect();
		System.out.println(info.toString());
	}
	
	@Test
	public void testRemoveCollector(){
		boolean removeCollector = instance.removeCollector(GarbageCollectorInfoCollector.class.getSimpleName());
		System.out.println(removeCollector);
	}
}
