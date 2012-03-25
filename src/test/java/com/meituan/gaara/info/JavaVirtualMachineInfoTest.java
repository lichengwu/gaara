/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.info;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.meituan.gaara.test.BaseTest;

/**
 *
 * @author lichengwu
 * @created 2012-3-23
 *
 * @version 1.0
 */
public class JavaVirtualMachineInfoTest extends BaseTest{

	/**
	 * JVM args:-XX:+UseConcMarkSweepGC
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 *
	 */
	@Test
	public void test() {
		 JavaVirtualMachineInfo jvm = JavaVirtualMachineInfo.getInstance();
		List<ThreadDetails> threadInfoList = jvm.getThreadInfoList();
		for(ThreadDetails detail : threadInfoList){
			System.out.println(detail);
		}
		System.out.println(jvm.getSystemPorpterties());
		System.out.println(jvm.getPID());
		
	}
	
	@Test
	public void testAvaliableProcessers() throws InterruptedException{
		int iCount=0;
		while(iCount<10){
			System.out.println(Runtime.getRuntime().availableProcessors());
			iCount++;
			TimeUnit.SECONDS.sleep(10);
		}
				
	}
	
	@SuppressWarnings("restriction")
    @Test
	public void testCpuUsage() throws InterruptedException{
		int  iCount=0;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					System.out.println(UUID.randomUUID().toString());
					try {
	                    TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
                    }
				}
			}
		}).start();
		com.sun.management.OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		while(iCount<10){
			System.out.println(operatingSystemMXBean.getProcessCpuTime());
			TimeUnit.SECONDS.sleep(1);
			iCount++;
		}
	}

}
