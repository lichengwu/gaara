/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.info;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import cn.lichengwu.gaara.util.PID;
import cn.lichengwu.gaara.util.Parameter;
import cn.lichengwu.gaara.util.ParameterUtil;

/**
 * JVM信息
 * 
 * @author lichengwu
 * @created 2012-3-23
 * 
 * @version 1.0
 */
public class JavaVirtualMachineInfo implements TransientInfo {

	private static final long serialVersionUID = -9217176108573004487L;

	private static final long PERIOD_IN_MILLISECONDS = ParameterUtil
	        .getParameterAsInt(Parameter.COLLECT_RATE) * 1000L;

	// stack traces of threads that are retrieved from java 1.6.0 update 1
	// to avoid memory leak bug
	// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6434648
	private static final boolean STACK_TRACES_ENABLED = "1.6.0_01".compareTo(ParameterUtil
	        .getParameter(Parameter.JAVA_VERSION)) <= 0;

	private static final boolean SYNCHRONIZER_ENABLED = "1.6".compareTo(ParameterUtil
	        .getParameter(Parameter.JAVA_VERSION)) < 0;

	private List<String> jvmArguments = null;

	private long lastUpdate = -1;

	private double averageSystemLoad = -1d;

	private long processCpuTime = -1;

	private long openFileDescriptorCount = -1;

	private long maxFileDescriptorCount = -1;

	private List<ThreadDetails> threadInfoList = null;

	private Map<String, String> systemPorpterties = null;

	private String pid = PID.getPID();

	private long threadCount;

	private long totalStartedThreadCount;

	private long peakThreadCount;

	private Boolean isOracleMBean = false;

	private Boolean isOracleMBeanOnUnix = false;

	private Boolean isAverageSystemLoadSupport = false;

	private double cpuUsage = 0;

	/**
	 * 
	 */
	private static JavaVirtualMachineInfo INSTANCE = new JavaVirtualMachineInfo();

	/**
	 * 构造方法
	 */
	private JavaVirtualMachineInfo() {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		String className = operatingSystemMXBean.getClass().getName();
		// com.sun.management.UnixOperatingSystem extends
		// com.sun.management.OperatingSystem
		isOracleMBean = "com.sun.management.OperatingSystem".equals(className)
		        || "com.sun.management.UnixOperatingSystem".equals(className);
		isOracleMBeanOnUnix = "com.sun.management.UnixOperatingSystem".equals(className);
		isAverageSystemLoadSupport = operatingSystemMXBean.getSystemLoadAverage() >= 0;
		init();
	}

	/**
	 * init method
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 */
	private void init() {
		loadJvmArguments();
		loadSystemProperties();
		loadAverageSystemLoad();
		loadOpenFileDescriptorCount();
		loadMaxFileDescriptorCount();
		loadAllThreadInfo();
		loadCpuUsage();
	}

	/**
	 * 获得{@link JavaVirtualMachineInfo}实例
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 * @return
	 */
	public static JavaVirtualMachineInfo getInstance() {
		return INSTANCE;
	}

	/**
	 * @see TransientInfo#refresh()
	 */
	@Override
	public boolean refresh() {
		loadAverageSystemLoad();
		loadOpenFileDescriptorCount();
		loadMaxFileDescriptorCount();
		loadAllThreadInfo();
		lastUpdate = System.currentTimeMillis();
		loadCpuUsage();
		return true;
	}

	/**
	 * @see TransientInfo#lastUpdate()
	 */
	@Override
	public long lastUpdate() {
		return lastUpdate;
	}

	/**
	 * 获得jvm启动参数
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 * @return
	 */
	private void loadJvmArguments() {
		jvmArguments = Collections.unmodifiableList(ManagementFactory.getRuntimeMXBean()
		        .getInputArguments());
	}

	/**
	 * load system properties
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 */
	private void loadSystemProperties() {
		Properties properties = System.getProperties();
		systemPorpterties = new HashMap<String, String>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			systemPorpterties.put(entry.getKey().toString(), entry.getValue().toString());
		}
	}

	/**
	 * load cpu usage
	 * 
	 * @author lichengwu
	 * @created 2012-3-25
	 * 
	 */
	private void loadCpuUsage() {
		OperatingSystemMXBean operatingSystem = ManagementFactory.getOperatingSystemMXBean();
		if (isOracleMBean) {
			@SuppressWarnings("restriction")
			final com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) operatingSystem;
			@SuppressWarnings("restriction")
			long newProcessCpuTime = osBean.getProcessCpuTime() / 1000000;
			this.cpuUsage = Math.min((100*(newProcessCpuTime - this.processCpuTime)
			        / PERIOD_IN_MILLISECONDS / Runtime.getRuntime().availableProcessors()), 100);
			this.processCpuTime = newProcessCpuTime;
		}
	}

	// /**
	// * load ProcessCpuTime
	// *
	// * @author lichengwu
	// * @created 2012-3-23
	// *
	// * @return
	// */
	// @SuppressWarnings("restriction")
	// private void loadProcessCpuTimeMillis() {
	// OperatingSystemMXBean operatingSystem =
	// ManagementFactory.getOperatingSystemMXBean();
	// if (isOracleMBean) {
	// final com.sun.management.OperatingSystemMXBean osBean =
	// (com.sun.management.OperatingSystemMXBean) operatingSystem;
	// // 将纳秒转换成毫秒
	// processCpuTime = osBean.getProcessCpuTime() / 1000000;
	// }
	// }

	/**
	 * load the maximum number of file descriptors.
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 */
	@SuppressWarnings("restriction")
	private void loadMaxFileDescriptorCount() {
		final OperatingSystemMXBean operatingSystem = ManagementFactory.getOperatingSystemMXBean();
		if (isOracleMBeanOnUnix) {
			final com.sun.management.UnixOperatingSystemMXBean unixOsBean = (com.sun.management.UnixOperatingSystemMXBean) operatingSystem;
			try {
				maxFileDescriptorCount = unixOsBean.getMaxFileDescriptorCount();
			} catch (final Error e) {
				// using jsvc on ubuntu or debian
			}
		}
	}

	/**
	 * load the number of open file descriptors
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 */
	@SuppressWarnings("restriction")
	private void loadOpenFileDescriptorCount() {
		final OperatingSystemMXBean operatingSystem = ManagementFactory.getOperatingSystemMXBean();
		if (isOracleMBeanOnUnix) {
			com.sun.management.UnixOperatingSystemMXBean unixOsBean = (com.sun.management.UnixOperatingSystemMXBean) operatingSystem;
			try {
				openFileDescriptorCount = unixOsBean.getOpenFileDescriptorCount();
			} catch (final Error e) {
				// using jsvc on ubuntu or debian
			}
		}
	}

	/**
	 * load AverageSystemLoad if available
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 */
	private void loadAverageSystemLoad() {
		OperatingSystemMXBean mf = ManagementFactory.getOperatingSystemMXBean();
		averageSystemLoad = mf.getSystemLoadAverage();
	}

	/**
	 * load alll thread detals
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 */
	private void loadAllThreadInfo() {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		// set peakThreadCount
		peakThreadCount = threadBean.getPeakThreadCount();
		// set threadCount
		threadCount = threadBean.getThreadCount();
		// set totalStartedThreadCount
		totalStartedThreadCount = threadBean.getTotalStartedThreadCount();

		List<Thread> threads;
		Map<Thread, StackTraceElement[]> stackTraces;

		if (STACK_TRACES_ENABLED) {
			stackTraces = Thread.getAllStackTraces();
			threads = new ArrayList<Thread>(stackTraces.keySet());
		} else {
			threads = getThreadsFromThreadGroups();
			Thread currentThread = Thread.currentThread();
			stackTraces = Collections.singletonMap(currentThread, currentThread.getStackTrace());
		}

		boolean cpuTimeEnabled = threadBean.isThreadCpuTimeSupported()
		        && threadBean.isThreadCpuTimeEnabled();

		long[] deadlockedThreads = getDeadlockedThreads(threadBean);

		threadInfoList = new ArrayList<ThreadDetails>(threads.size());

		String hostAddress = ParameterUtil.getParameter(Parameter.HOST_ADDRESS);
		for (Thread thread : threads) {
			StackTraceElement[] stackTraceElements = stackTraces.get(thread);
			List<StackTraceElement> stackTraceElementList = stackTraceElements == null ? null
			        : new ArrayList<StackTraceElement>(Arrays.asList(stackTraceElements));
			long cpuTimeMillis;
			long userTimeMillis;
			if (cpuTimeEnabled) {
				cpuTimeMillis = threadBean.getThreadCpuTime(thread.getId()) / 1000000;
				userTimeMillis = threadBean.getThreadUserTime(thread.getId()) / 1000000;
			} else {
				cpuTimeMillis = -1;
				userTimeMillis = -1;
			}
			boolean deadlocked = deadlockedThreads != null
			        && Arrays.binarySearch(deadlockedThreads, thread.getId()) >= 0;
			threadInfoList.add(new ThreadDetails(thread, stackTraceElementList, cpuTimeMillis,
			        userTimeMillis, deadlocked, hostAddress));
		}
	}

	/**
	 * Finds cycles of threads that are in deadlock waiting to acquire object
	 * monitors.
	 * <p>
	 * <b>NOTICES: </b>This method is designed for troubleshooting use, but not
	 * for synchronization control. It might be an expensive operation.
	 * </p>
	 * 
	 * @see {@link ThreadMXBean#findDeadlockedThreads()}
	 * @see {@link ThreadMXBean#findMonitorDeadlockedThreads()}
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @param threadBean
	 * @return an array of IDs of the threads that are monitor deadlocked, if
	 *         any; null otherwise.
	 */
	private long[] getDeadlockedThreads(ThreadMXBean threadBean) {
		final long[] deadlockedThreads;
		if (SYNCHRONIZER_ENABLED && threadBean.isSynchronizerUsageSupported()) {
			deadlockedThreads = threadBean.findDeadlockedThreads();
		} else {
			deadlockedThreads = threadBean.findMonitorDeadlockedThreads();
		}
		if (deadlockedThreads != null) {
			Arrays.sort(deadlockedThreads);
		}
		return deadlockedThreads;
	}

	/**
	 * get all active thread from thread groups
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return all active thread
	 */
	private List<Thread> getThreadsFromThreadGroups() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		while (group.getParent() != null) {
			group = group.getParent();
		}
		final Thread[] threadsArray = new Thread[group.activeCount()];
		group.enumerate(threadsArray, true);
		return Arrays.asList(threadsArray);
	}

	/**
	 * <p>
	 * Returns the system load average for the last minute. The system load
	 * average is the sum of the number of runnable entities queued to the
	 * available processors and the number of runnable entities running on the
	 * available processors averaged over a period of time. The way in which the
	 * load average is calculated is operating system specific but is typically
	 * a damped time-dependent average.
	 * </p>
	 * <p>
	 * If the load average is not available, a negative value is returned.
	 * </p>
	 * <p>
	 * This method is designed to provide a hint about the system load and may
	 * be queried frequently. The load average may be unavailable on some
	 * platform where it is expensive to implement this method.
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 * @return AverageSystemLoad
	 * 
	 */
	public double getAverageSystemLoad() {
		return averageSystemLoad;
	}

	/**
	 * Returns the CPU time used by the process on which the Java virtual
	 * machine is running in milliseconds. The returned value is of milliseconds
	 * precision but not necessarily milliseconds accuracy. This method returns
	 * <tt>-1</tt> if the the platform does not support this operation.
	 * 
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 * @return the CPU time used by the process in nanoseconds, or <tt>-1</tt>
	 *         if this operation is not supported.
	 */
	public long getProcessCpuTime() {
		return processCpuTime;
	}

	/**
	 * get the number of open file descriptors (file handle count on Windows)
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 * @return the number of open file descriptors (file handle count on
	 *         Windows)
	 */
	public long getOpenFileDescriptorCount() {
		return openFileDescriptorCount;
	}

	/**
	 * get JVM args for start the JVM
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 * @return JVM args for start the JVM
	 */
	public List<String> getJvmArguments() {
		return jvmArguments;
	}

	/**
	 * get the maximum number of file descriptors
	 * 
	 * @author lichengwu
	 * @created 2012-3-23
	 * 
	 * @return the maximum number of file descriptors
	 */
	public long getMaxFileDescriptorCount() {
		return maxFileDescriptorCount;
	}

	/**
	 * get all thread details
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return all thread details
	 */
	public List<ThreadDetails> getThreadInfoList() {
		return threadInfoList;
	}

	/**
	 * @return the systemPorpterties
	 */
	public Map<String, String> getSystemPorpterties() {
		return systemPorpterties;
	}

	/**
	 * get the pid of JVM
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return the pid of JVM
	 */
	public String getPID() {
		return pid;
	}

	/**
	 * get the total number of threads created and also started since the Java
	 * virtual machine started.
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return the total number of threads created and also started since the
	 *         Java virtual machine started
	 */
	public long getTotalStartedThreadCount() {
		return totalStartedThreadCount;
	}

	/**
	 * get the current number of live threads including both daemon and
	 * non-daemon threads.
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return the current number of live threads
	 */
	public long getThreadCount() {
		return threadCount;
	}

	/**
	 * test if average system load support
	 * 
	 * @author lichengwu
	 * @created 2012-3-25
	 * 
	 * @return
	 */
	public Boolean getIsAverageSystemLoadSupport() {
		return isAverageSystemLoadSupport;
	}

	/**
	 * get the peak live thread count since the Java virtual machine started or
	 * peak was reset
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return the peak live thread count
	 */
	public long getPeakThreadCount() {
		return peakThreadCount;
	}

	/**
	 * @return the isOracleMBean
	 */
	public Boolean getIsOracleMBean() {
		return isOracleMBean;
	}

	/**
	 * @return the isOracleMBeanOnUnix
	 */
	public Boolean getIsOracleMBeanOnUnix() {
		return isOracleMBeanOnUnix;
	}

	/**
	 * get CPU usage
	 * 
	 * @author lichengwu
	 * @created 2012-3-25
	 * 
	 * @param CPU
	 *            usage
	 */
	public double getCpuUsage() {
		return cpuUsage;
	}

	// 下面两个方法用于“冷藏”和“解冻”
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(jvmArguments);
		out.writeLong(lastUpdate);
		out.writeDouble(averageSystemLoad);
		out.writeLong(processCpuTime);
		out.writeLong(openFileDescriptorCount);
		out.writeLong(maxFileDescriptorCount);
		out.writeObject(threadInfoList);
		out.writeObject(systemPorpterties);
		out.writeUTF(pid);
		out.writeLong(threadCount);
		out.writeLong(totalStartedThreadCount);
		out.writeLong(peakThreadCount);
		out.writeBoolean(isOracleMBean);
		out.writeBoolean(isOracleMBeanOnUnix);
		out.writeBoolean(isAverageSystemLoadSupport);
		out.writeDouble(cpuUsage);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		jvmArguments = (List<String>) in.readObject();
		lastUpdate = in.readLong();
		averageSystemLoad = in.readDouble();
		processCpuTime = in.readLong();
		openFileDescriptorCount = in.readLong();
		maxFileDescriptorCount = in.readLong();
		threadInfoList = (List<ThreadDetails>) in.readObject();
		systemPorpterties = (Map<String, String>) in.readObject();
		pid = in.readUTF();
		threadCount = in.readLong();
		totalStartedThreadCount = in.readLong();
		peakThreadCount = in.readLong();
		isOracleMBean = in.readBoolean();
		isOracleMBeanOnUnix = in.readBoolean();
		isAverageSystemLoadSupport = in.readBoolean();
		cpuUsage = in.readDouble();
	}
}
