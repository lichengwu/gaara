/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector.factory;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import cn.lichengwu.gaara.collector.factory.LocalCollectorFactory;
import org.junit.Before;
import org.junit.Test;

import cn.lichengwu.gaara.collector.Collector;
import cn.lichengwu.gaara.collector.DefaultInfoCollector;
import cn.lichengwu.gaara.collector.GarbageCollectorInfoCollector;
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
		Map<String, DefaultInfoCollector> map = instance.getRegisteredLocalCollectorMap();
		for(Entry<String, DefaultInfoCollector> entey : map.entrySet()){
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
