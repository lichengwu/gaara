/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Process ID in Java
 * 
 * @author lichengwu
 * @created 2012-1-18
 * 
 * @version 1.0
 */
public final class PID {

	private static final Log log = LogFactory.getLog(PID.class);

	/**
	 * 私有构造方法
	 */
	private PID() {
		super();
	}

	/**
	 * 获得java进程id
	 * 
	 * @author lichengwu
	 * @created 2012-1-18
	 * 
	 * @return java进程id
	 */
	public static final String getPID() {
		String pid = ParameterUtil.getParameter(Parameter.PID);
		if (pid == null) {
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			String processName = runtimeMXBean.getName();
			if (processName.indexOf('@') != -1) {
				pid = processName.substring(0, processName.indexOf('@'));
			} else {
				pid = getPIDFromOS();
			}
			// 缓存
			ParameterUtil.setParameter(Parameter.PID, pid);
		}
		return pid;
	}

	/**
	 * 从操作系统获得pid
	 * <p>
	 * 对于windows，请参考:http://www.scheibli.com/projects/getpids/index.html
	 * 
	 * @author lichengwu
	 * @created 2012-1-18
	 * 
	 * @return
	 */
	private static String getPIDFromOS() {

		String pid = null;

		String[] cmd = null;

		File tempFile = null;

		String osName = ParameterUtil.getParameter(Parameter.OS_NAME);
		// 处理windows
		if (osName.toLowerCase().contains("windows")) {
			InputStream in = null;
			FileOutputStream fos = null;

			try {
				// 创建临时getpids.exe文件
				tempFile = File.createTempFile("getpids", ".exe");
				in = PID.class.getResourceAsStream(FileUtil.getResourcePath("getpids.exe"));
				//File getpids = new File();
				//fis = new FileInputStream(getpids);
				fos = new FileOutputStream(tempFile);
				byte[] buf = new byte[1024];
				while (in.read(buf) != -1) {
					fos.write(buf);
				}
				// 获得临时getpids.exe文件路径作为命令
				cmd = new String[] { tempFile.getAbsolutePath() };
			} catch (FileNotFoundException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			} finally {
				if (tempFile != null) {
					tempFile.deleteOnExit();
				}
				Closer.close(in, fos);
			}
		}
		// 处理非windows
		else {
			cmd = new String[] { "/bin/sh", "-c", "echo $$ $PPID" };
		}
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			byte[] buf = new byte[1024];
			Process exec = Runtime.getRuntime().exec(cmd);
			is = exec.getInputStream();
			baos = new ByteArrayOutputStream();
			while (is.read(buf) != -1) {
				baos.write(buf);
			}
			String ppids = baos.toString();
			// 对于windows参考：http://www.scheibli.com/projects/getpids/index.html
			pid = ppids.split(" ")[1];
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (tempFile != null) {
				tempFile.deleteOnExit();
			}
			Closer.close(is, baos);
		}
		return pid;
	}
}
