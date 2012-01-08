/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.jvm.gc;

import java.io.Serializable;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.jvm.GarbageCollectorAttribute;

/**
 * JVM 垃圾回收器工具类，用于获得整个JVM的垃圾回收信息
 * 
 * @author lichengwu
 * @created 2012-1-8
 * 
 * @version 1.0
 */
final public class GarbageCollectorHolder implements Serializable {

	private static final long serialVersionUID = -2774335299862104087L;

	private List<GarbageCollector> gcList = new ArrayList<GarbageCollector>(0);

	private static final Log log = LogFactory.getLog(GarbageCollectorHolder.class);

	/**
	 * 私有构造方法
	 */
	private GarbageCollectorHolder() {
		List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory
		        .getGarbageCollectorMXBeans();
		MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
		for (GarbageCollectorMXBean bean : garbageCollectorMXBeans) {
			GarbageCollector gc = new GarbageCollector();
			try {
				gc.setName(bean.getName());
				gc.setCollectionCount(bean.getCollectionCount());
				gc.setCollectionTime(bean.getCollectionTime());
				gc.setMemoryPoolNames(bean.getMemoryPoolNames());
				gc.setValid(bean.isValid());
				ObjectName on = new ObjectName("java.lang:type=GarbageCollector,name="
				        + bean.getName());
				String lastGcInfo = platformMBeanServer.getAttribute(on,
				        GarbageCollectorAttribute.LASTGCINFO.getCode()).toString();
				gc.setLastGcInfo(lastGcInfo);
				gcList.add(gc);
			} catch (MalformedObjectNameException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (NullPointerException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (AttributeNotFoundException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (InstanceNotFoundException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (MBeanException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (ReflectionException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获得GarbageCollectorHolder实例
	 * <p>
	 * <b>注意：</b>{@link com.meituan.gaara.jvm.gc.GarbageCollectorHolder}
	 * 对象包含的垃圾收集器信息为调用 {@link #getInstance()}返回的瞬时静态信息。
	 * 随着JVM内存不断变化，如果需要获得最新的JVM信息，请重新调用 {@link #getInstance()}获得新的实例。
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return {@link com.meituan.gaara.jvm.gc.GarbageCollectorHolder} 实例。
	 */
	public static GarbageCollectorHolder getInstance() {
		return new GarbageCollectorHolder();
	}

	/**
	 * 根据名字获得垃圾回收器
	 * <p>
	 * <b>注意:</b>名字区分大小写
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @param name
	 * @return name所代表的垃圾回收器，如果不存在，返回null。
	 */
	public GarbageCollector getGarbageCollectorByName(String name) {
		assert name != null;
		for (GarbageCollector gc : gcList) {
			if (gc.getName().equals(name)) {
				return gc;
			}
		}
		return null;
	}

	/**
	 * 描述整个JVM垃圾收集器的详细信息
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 整个JVM所有垃圾收集器的详细信息
	 */
	public List<GarbageCollector> describe() {
		return gcList;
	}

}
