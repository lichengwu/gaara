/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.store;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;

import org.jrobin.core.ConsolFuns;
import org.jrobin.core.DsTypes;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDbPool;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.Util;
import org.jrobin.core.jrrd.DataSourceType;

import com.meituan.gaara.util.FileUtil;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;

/**
 * 采用JRobin存储数据在RRD(Round Robin Database)。
 * 
 * @author lichengwu
 * @created 2012-1-22
 * 
 * @version 1.0
 */
public final class JRobin {
	/**
	 * 小图片高度
	 */
	public static int TINY_HEIGHT = 50;

	private static final Color BRIGHT_RED = Color.RED.brighter().brighter();

	private static final Paint SMALL_GRADIENT = new GradientPaint(0, 0, BRIGHT_RED, 0, TINY_HEIGHT,
	        Color.GREEN, false);
	private static final int HOUR = 60 * 60;
	private static final int DAY = 24 * HOUR;

	private RrdDbPool rrdPool;
	private String application;
	private String name;
	private String rrdFileName;
	private int step;
	private String requestName;

	/**
	 * 私有构造器
	 * 
	 * @param application
	 *            应用名字
	 * @param name
	 *            rrd文件名
	 * @param rrdFile
	 *            rrd文件
	 * @param step
	 *            步长
	 * @param requestName
	 *            非必须，用于显示
	 * @throws RrdException
	 * @throws IOException
	 */
	private JRobin(String application, String name, File rrdFile, int step, String requestName)
	        throws RrdException, IOException {
		assert application != null;
		assert name != null;
		assert rrdFile != null;
		assert step > 0;
		rrdPool = RrdDbPool.getInstance();
		this.application = application;
		this.name = name;
		this.rrdFileName = rrdFile.getPath();
		this.step = step;
		this.requestName = requestName;

		init();
	}

	/**
	 * 创建JRobin实例
	 * 
	 * @author lichengwu
	 * @created 2012-1-22
	 * 
	 * @param application
	 *            应用名字
	 * @param name
	 *            rrd文件名
	 * @param requestName
	 *            可选名字，用于显示
	 * @return
	 * @throws RrdException
	 * @throws IOException
	 */
	public static JRobin createInstance(String application, String name, String requestName)
	        throws RrdException, IOException {
		File rrdStorageDir = ParameterUtil.getStorageDirectory(application);
		File rrdFile = new File(rrdStorageDir, name + ".rrd");
		int step = ParameterUtil.getParameterAsInt(Parameter.COLLECT_RATE);
		return new JRobin(application, name, rrdFile, step, requestName);
	}

	/**
	 * 初始化JRobin
	 * 
	 * @author lichengwu
	 * @throws IOException
	 * @throws RrdException
	 * @created 2012-1-22
	 * 
	 */
	private void init() throws IOException, RrdException {
		File rrdFile = new File(rrdFileName);
		File rrdDir = rrdFile.getParentFile();
		if (!FileUtil.ensureFilePath(rrdDir)) {
			throw new IOException("can not create directory:" + rrdDir.getCanonicalPath());
		}
		if (rrdFile.exists() || rrdFile.length() == 0) {
			RrdDef rrdDef = new RrdDef(rrdFileName, step);
			rrdDef.setStartTime(Util.getTime() - step);
			rrdDef.addDatasource(getDataSourceName(), DsTypes.DT_GAUGE, step * 2, 0, Double.NaN);
			// 添加文档
			// 1天
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 1, DAY / step);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, 1, DAY / step);
			// 1周
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, HOUR / step, 7 * 24);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, HOUR / step, 7 * 24);
			// 1个月(6个小时合并一次)
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 6 * HOUR / step, 31 * 4);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, 6 * HOUR / step, 31 * 4);
			// 一年（两天合并一次）
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 2 * 24 * DAY / step, 15 * 12);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, 2 * 24 * DAY / step, 15 * 12);
			
			RrdDb rrdDb = rrdPool.requestRrdDb(rrdDef);
			rrdPool.release(rrdDb);
		}
	}
	
	public byte[] paint(){
		return null;
	}

	/**
	 * 获得数据源名字
	 * <p>
	 * 最多只能有20个字符
	 * 
	 * @author lichengwu
	 * @created 2012-1-22
	 * 
	 * @return
	 */
	private String getDataSourceName() {
		if (name.length() <= 20) {
			return name;
		}
		return name.substring(0, 20);
	}

}
