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
import java.util.Collections;
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

import com.meituan.gaara.info.TransientInfo;

/**
 * JVM 垃圾回收器工具类，用于获得整个JVM的垃圾回收信息
 * 
 * @see TransientInfo
 * @author lichengwu
 * @created 2012-1-8
 * 
 * @version 1.0
 */
final public class GarbageCollectorInfo implements TransientInfo, Serializable {

	private static final long serialVersionUID = -2774335299862104087L;
	
	private long lastUpdate = 0;

	private List<GarbageCollector> gcList = new ArrayList<GarbageCollector>(0);

	private static final Log log = LogFactory.getLog(GarbageCollectorInfo.class);

	private List<GarbageCollectorMXBean> garbageCollectorMXBeans = null;
	private MBeanServer platformMBeanServer = null;

	/**
	 * 私有构造方法
	 */
	private GarbageCollectorInfo() {
		garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
		platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
		obtainGcInfo();
	}

	/**
	 * 获得GarbageCollectorHolder实例
	 * <p>
	 * <b>注意：</b>{@link GarbageCollectorInfo}
	 * 对象包含的垃圾收集器信息为调用 {@link #getInstance()}返回的瞬时静态信息。
	 * 随着JVM内存不断变化，如果需要获得最新的JVM信息，请重新调用 {@link #getInstance()}获得新的实例。
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return {@link GarbageCollectorInfo} 实例。
	 */
	public static GarbageCollectorInfo getInstance() {
		return new GarbageCollectorInfo();
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
	 *         <p>
	 *         <b>注意：</b>返回的列表为只读。
	 */
	public List<GarbageCollector> describe() {
		return Collections.unmodifiableList(gcList);
	}

	/**
	 * 刷新垃圾回收去信息
	 * 
	 * @return 如果能获得到垃圾收集器信息(即垃圾收集器可用)，然后true；否则返回false。
	 * @see com.meituan.gaara.info.TransientInfo#refresh()
	 */
	public boolean refresh() {
		lastUpdate = System.currentTimeMillis();
		obtainGcInfo();
		return gcList.size() > 0;
	}

	/**
	 * 获得垃圾回收器信息
	 * 
	 * @author lichengwu
	 * @created 2012-1-9
	 * 
	 */
	private void obtainGcInfo() {
		if (!gcList.isEmpty()) {
			gcList.clear();
		}
		for (GarbageCollectorMXBean bean : garbageCollectorMXBeans) {
			GarbageCollector gc = new GarbageCollector();
			try {
				synchronized (gcList) {
					gc.setName(bean.getName());
					gc.setCollectionCount(bean.getCollectionCount());
					gc.setCollectionTime(bean.getCollectionTime());
					gc.setMemoryPoolNames(bean.getMemoryPoolNames());
					gc.setValid(bean.isValid());
					ObjectName on = new ObjectName("java.lang:type=GarbageCollector,name="
					        + bean.getName());
					String lastGcInfo = platformMBeanServer.getAttribute(on,
					        GarbageCollector.Attribute.LASTGCINFO.getCode()).toString();
					gc.setLastGcInfo(lastGcInfo);
					gcList.add(gc);
				}
			} catch (MalformedObjectNameException e) {
				log.error(e.getMessage(), e);
				continue;
			} catch (NullPointerException e) {
				log.error(e.getMessage(), e);
				continue;
			} catch (AttributeNotFoundException e) {
				log.error(e.getMessage(), e);
				continue;
			} catch (InstanceNotFoundException e) {
				log.error(e.getMessage(), e);
				continue;
			} catch (MBeanException e) {
				log.error(e.getMessage(), e);
				continue;
			} catch (ReflectionException e) {
				log.error(e.getMessage(), e);
				continue;
			}
		}
	}

	/**
     * @see com.meituan.gaara.info.TransientInfo#lastUpdate()
     */
    @Override
    public long lastUpdate() {
	    return lastUpdate;
    }

}
