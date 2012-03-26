/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.sync;

import java.io.Serializable;

/**
 * data for synchronization between server and client
 * 
 * @author lichengwu
 * @created 2012-3-26
 * 
 * @version 1.0
 */
public class SyncData implements Serializable {

	private static final long serialVersionUID = -5937938531657688020L;
	
	/**
	 * application name
	 */
	private String application;

	/**
	 * jvm bootup time
	 */
	private long bootupTime;

	/**
	 * sync time for time lag
	 */
	private long syncTime;

	/**
	 * host address
	 */
	private String hostAddress;

	/**
	 * gaara version
	 */
	private String gaaraVersion;

	/**
	 * @return the application name
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * @param application
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * @return jvm bootup time
	 */
	public long getBootupTime() {
		return bootupTime;
	}

	/**
	 * @param bootupTime
	 */
	public void setBootupTime(long bootupTime) {
		this.bootupTime = bootupTime;
	}

	/**
	 * @return the sync time for time lag
	 */
	public long getSyncTime() {
		return syncTime;
	}

	/**
	 * @param syncTime
	 */
	public void setSyncTime(long syncTime) {
		this.syncTime = syncTime;
	}

	/**
	 * @return the host address
	 */
	public String getHostAddress() {
		return hostAddress;
	}

	/**
	 * @param hostAddress
	 */
	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	/**
	 * @return the gaara version
	 */
	public String getGaaraVersion() {
		return gaaraVersion;
	}

	/**
	 * @param gaaraVersion
	 */
	public void setGaaraVersion(String gaaraVersion) {
		this.gaaraVersion = gaaraVersion;
	}

}
