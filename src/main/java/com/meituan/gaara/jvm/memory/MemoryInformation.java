/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.jvm.memory;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

/**
 * 虚拟机内存信息
 * 
 * @author lichengwu
 * @created 2012-1-8
 * 
 * @version 1.0
 */
public class MemoryInformation implements Serializable {

	private static final long serialVersionUID = 5145204746778194464L;

	private MemoryPoolMXBean edenPoolMXBean;

	private MemoryPoolMXBean permGenPoolMXBean;

	private MemoryPoolMXBean oldGenPoolMXBean;

	private MemoryPoolMXBean survivorPoolMXBean;

	private MemoryMXBean memoryMXBean;

	/**
	 * heap堆中已使用内存大小
	 */
	private long usedHeapSize = -1;

	/**
	 * heap大小(即-Xmx参数的值)
	 */
	private long maxHeapSize = -1;

	/**
	 * 永久代已使用的内存大小
	 */
	private long usedPermGenSize = -1;

	/**
	 * 永久代大小
	 */
	private long maxPermGenSize = -1;

	/**
	 * 老年代已使用大小
	 */
	private long usedOldGenSize = -1;

	/**
	 * 老年代大小
	 */
	private long maxOldGenSize = -1;

	/**
	 * 伊甸园(新生代)已使用大小
	 */
	private long usedEdenSize = -1;

	/**
	 * 伊甸园(新生代)最大值
	 */
	private long maxEdenSize = -1;

	/**
	 * Survivor已使用大小
	 */
	private long usedSurvivorSize = -1;

	/**
	 * Survivor大小
	 */
	private long maxSurvivorSize = -1;

	/**
	 * 已使用非堆大小
	 */
	private long usedNoHeapMaxSize = -1;

	/**
	 * 非堆最大值
	 */
	private long maxNoHeapSize = -1;


	/**
	 * 构造方法
	 */
	private MemoryInformation() {
		memoryMXBean = ManagementFactory.getMemoryMXBean();
		maxHeapSize = memoryMXBean.getHeapMemoryUsage().getMax();
		usedHeapSize = memoryMXBean.getHeapMemoryUsage().getUsed();
		maxNoHeapSize = memoryMXBean.getNonHeapMemoryUsage().getMax();
		usedNoHeapMaxSize = memoryMXBean.getNonHeapMemoryUsage().getUsed();
		initPoolMXBean();

	}

	public static MemoryInformation getInstance() {
		return new MemoryInformation();
	}

	/**
	 * 获得堆已使用大小
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 堆已使用大小
	 */
	public long getUsedHeapSize() {
		return usedHeapSize;
	}

	/**
	 * 获得堆的最大值
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 堆的最大值
	 */
	public long getMaxHeapSize() {
		return maxHeapSize;
	}

	/**
	 * 获得永久代已使用大小
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 持久带已使用大小 或者 -1(不能检测到永久代的使用情况)
	 */
	public long getUsedPermGenSize() {
		return usedPermGenSize;
	}

	/**
	 * 获得永久代的最大值
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 永久代已使用大小 或者 -1(不能检测到永久带的使用情况)
	 */
	public long getMaxPermGenSize() {
		return maxPermGenSize;
	}

	/**
	 * 获得老年代已使用大小
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 老年代已使用大小 或者 -1(不能检测到老年代的使用情况)
	 */
	public long getUsedOldGenSize() {
		return usedOldGenSize;
	}

	/**
	 * 获得老年代最大值
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 老年代最大值 或者 -1(不能检测到老年代的使用情况)
	 */
	public long getMaxOldGenSize() {
		return maxOldGenSize;
	}

	/**
	 * 获得新生代已使用大小
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 新生代已使用大小 或者 -1(不能检测到新生代的使用情况)
	 */
	public long getUsedEdenSize() {
		return usedEdenSize;
	}

	/**
	 * 获得新生代最大值
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return 新生代最大值 或者 -1(不能检测到新生代的使用情况)
	 */
	public long getMaxEdenSize() {
		return maxEdenSize;
	}

	/**
	 * 获得Survivor已使用大小
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return Survivor已使用大小 或者 -1(不能检测到Survivor的使用情况)
	 */
	public long getUsedSurvivorSize() {
		return usedSurvivorSize;
	}

	/**
	 * 获得Survivor区域的最大值
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 * @return Survivor区域的最大值 或者 -1(不能检测到Survivor的使用情况)
	 */
	public long getMaxSurvivorSize() {
		return maxSurvivorSize;
	}

	/**
	 * 获得非堆已使用大小
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 *
	 * @return 非堆已使用大小
	 */
	public long getUsedNoHeapMaxSize() {
    	return usedNoHeapMaxSize;
    }

	/**
	 * 获得非堆最大值
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 *
	 * @return 非堆最大值
	 */
	public long getMaxNoHeapSize() {
    	return maxNoHeapSize;
    }
	
	/**
	 * 初始化PoolMXBean
	 * 
	 * @author lichengwu
	 * @created 2012-1-8
	 * 
	 */
	private void initPoolMXBean() {
		List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
		for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
			if (poolMXBean.getName().contains("Eden")) {
				edenPoolMXBean = poolMXBean;
				maxEdenSize = edenPoolMXBean.getUsage().getMax();
				usedEdenSize = edenPoolMXBean.getUsage().getUsed();
			} else if (poolMXBean.getName().contains("Survivor")) {
				survivorPoolMXBean = poolMXBean;
				maxSurvivorSize = survivorPoolMXBean.getUsage().getMax();
				usedSurvivorSize = survivorPoolMXBean.getUsage().getUsed();
			} else if (poolMXBean.getName().contains("Old Gen")) {
				oldGenPoolMXBean = poolMXBean;
				maxOldGenSize = oldGenPoolMXBean.getUsage().getMax();
				usedOldGenSize = oldGenPoolMXBean.getUsage().getUsed();
			} else if (poolMXBean.getName().contains("Perm Gen")) {
				permGenPoolMXBean = poolMXBean;
				maxPermGenSize = permGenPoolMXBean.getUsage().getMax();
				usedPermGenSize = permGenPoolMXBean.getUsage().getUsed();
			}
		}
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("MemoryInformation [maxHeapSize=");
	    builder.append(maxHeapSize);
	    builder.append(", usedHeapSize=");
	    builder.append(usedHeapSize);
	    builder.append(", maxPermGenSize=");
	    builder.append(maxPermGenSize);
	    builder.append(", usedPermGenSize=");
	    builder.append(usedPermGenSize);
	    builder.append(", maxEdenSize=");
	    builder.append(maxEdenSize);
	    builder.append(", usedEdenSize=");
	    builder.append(usedEdenSize);
	    builder.append(", maxSurvivorSize=");
	    builder.append(maxSurvivorSize);
	    builder.append(", usedSurvivorSize=");
	    builder.append(usedSurvivorSize);
	    builder.append(", maxOldGenSize=");
	    builder.append(maxOldGenSize);
	    builder.append(", usedOldGenSize=");
	    builder.append(usedOldGenSize);
	    builder.append(", maxNoHeapSize=");
	    builder.append(maxNoHeapSize);
	    builder.append(", usedNoHeapMaxSize=");
	    builder.append(usedNoHeapMaxSize);
	    builder.append("]");
	    return builder.toString();
    }
}
