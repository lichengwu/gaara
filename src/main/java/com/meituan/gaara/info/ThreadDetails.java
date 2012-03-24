/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.info;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.meituan.gaara.util.PID;

/**
 * Thread information in details
 * 
 * @author lichengwu
 * @created 2012-3-24
 * 
 * @version 1.0
 */
public class ThreadDetails implements Serializable {

	private static final long serialVersionUID = 1261389040317700041L;

	private final String name;
	private final long id;
	private final int priority;
	private final boolean daemon;
	private final Thread.State state;
	private final long cpuTimeMillis;
	private final long userTimeMillis;
	private final boolean deadlocked;
	private final String globalThreadId;
	private final List<StackTraceElement> stackTrace;

	ThreadDetails(Thread thread, List<StackTraceElement> stackTrace, long cpuTimeMillis,
	        long userTimeMillis, boolean deadlocked, String hostAddress) {
		assert thread != null;
		assert stackTrace == null || stackTrace instanceof Serializable;

		this.name = thread.getName();
		this.id = thread.getId();
		this.priority = thread.getPriority();
		this.daemon = thread.isDaemon();
		this.state = thread.getState();
		this.stackTrace = stackTrace;
		this.cpuTimeMillis = cpuTimeMillis;
		this.userTimeMillis = userTimeMillis;
		this.deadlocked = deadlocked;
		this.globalThreadId = buildGlobalThreadId(thread, hostAddress);
	}

	/**
	 * get the thread name
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return thread name
	 */
	public String getName() {
		return name;
	}

	/**
	 * get thread id
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return thread id
	 */
	public long getId() {
		return id;
	}

	/**
	 * get the thread priority
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return thread priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * test is daemon thread
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @returns
	 */
	public boolean isDaemon() {
		return daemon;
	}

	/**
	 * get the state of the thread
	 * <p>
	 * reference:{@link Thread.State}
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return thread state
	 */
	public Thread.State getState() {
		return state;
	}

	/**
	 * get the stack trace of the thread if has any
	 * <p>
	 * <b>Notices: </b>the return list is unmodifiable.
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 * 
	 * @return thread stack trace
	 */
	public List<StackTraceElement> getStackTrace() {
		if (stackTrace != null) {
			return Collections.unmodifiableList(stackTrace);
		}
		return stackTrace;
	}

	/**
	 * get method executed by the thread
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 *
	 * @return method executed by the thread or empty string.
	 */
	public String getExecutedMethod() {
		final List<StackTraceElement> trace = stackTrace;
		if (trace != null && !trace.isEmpty()) {
			return trace.get(0).toString();
		}
		return "";
	}

	/**
	 * get thread cpu time in milliseconds
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 *
	 * @return thread cpu time in milliseconds
	 */
	public long getCpuTimeMillis() {
		return cpuTimeMillis;
	}

	/**
	 * get thread user time in milliseconds
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 *
	 * @return thread user time in milliseconds
	 */
	public long getUserTimeMillis() {
		return userTimeMillis;
	}

	/**
	 * test whether the thread is dead locked
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 *
	 * @return whether the thread is dead locked
	 */
	public boolean isDeadlocked() {
		return deadlocked;
	}

	/**
	 * get global thread id.
	 * <p>
	 * global thread id = pid_hostaddress_threadid
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 *
	 * @return
	 */
	public String getGlobalThreadId() {
		return globalThreadId;
	}

	/**
	 * build global thread id
	 * 
	 * @author lichengwu
	 * @created 2012-3-24
	 *
	 * @param thread 
	 * @param hostAddress
	 * @return global thread id
	 */
	private static String buildGlobalThreadId(Thread thread, String hostAddress) {
		return PID.getPID() + '_' + hostAddress + '_' + thread.getId();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[id=" + getId() + ", name=" + getName() + ", daemon="
		        + isDaemon() + ", priority=" + getPriority() + ", state=" + getState() + ']';
	}
}
