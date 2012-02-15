/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.store;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;

import org.jrobin.core.ConsolFuns;
import org.jrobin.core.DsTypes;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDbPool;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.RrdNioBackend;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

import com.meituan.gaara.exception.GaaraException;
import com.meituan.gaara.util.FileUtil;
import com.meituan.gaara.util.I18N;
import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;
import com.meituan.gaara.util.ReflectUtil;
import com.meituan.gaara.util.TimeRange;

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
	private String label;

	/**
	 * 私有构造器
	 * 
	 * @param application
	 *            应用名字
	 * @param name
	 *            JRobin标识
	 * @param rrdFile
	 *            rrd文件
	 * @param step
	 *            步长
	 * @param label
	 *            非必须，用于显示
	 * @throws RrdException
	 * @throws IOException
	 */
	private JRobin(String application, String name, File rrdFile, int step, String label)
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
		this.label = label;
		// 初始化
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
	 * @param label
	 *            可选名字，用于显示
	 * @return
	 * @throws RrdException
	 * @throws IOException
	 */
	public static JRobin createInstance(String application, String name, String label)
	        throws RrdException, IOException {
		File rrdStorageDir = FileUtil.getStorageDirectory(application);
		File rrdFile = new File(rrdStorageDir, name + ".rrd");
		int step = ParameterUtil.getParameterAsInt(Parameter.COLLECT_RATE);
		return new JRobin(application, name, rrdFile, step, label);
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
			throw new IOException("can not create rrd directory:" + rrdDir.getCanonicalPath());
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

	/**
	 * 重置RRD文件
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @throws RrdException
	 * @throws IOException
	 */
	public void resetFile() throws GaaraException {
		FileUtil.delete(rrdFileName);
		try {
			init();
		} catch (Throwable e) {
			throw new GaaraException(e);
		}
	}

	/**
	 * 绘图
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param range
	 *            时间范围
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @return 图片的二进制表示
	 * @throws IOException
	 * @throws RrdException
	 */
	public byte[] graph(TimeRange range, int width, int height) throws IOException, RrdException {
		try {
			// create common part of graph definition
			RrdGraphDef graphDef = new RrdGraphDef();
			// 中文 TODO 目前没找到更好的解决中文乱码问题
			if (Locale.CHINESE.getLanguage().equals(
			        I18N.getResourceBundle().getLocale().getLanguage())) {
				graphDef.setSmallFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
				graphDef.setLargeFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
			}

			setGraphSource(graphDef, height);
			setGraphPeriodAndSize(range, width, height, graphDef);

			graphDef.setImageFormat("png");
			graphDef.setFilename("-");
			graphDef.setPoolUsed(true);
			return new RrdGraph(graphDef).getRrdGraphInfo().getBytes();
		} catch (final RrdException e) {
			throw e;
		}
	}

	/**
	 * 设置图像时间范围和大小
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param range
	 *            时间范围
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param graphDef
	 */
	private void setGraphPeriodAndSize(TimeRange range, int width, int height, RrdGraphDef graphDef) {
		long endTime;
		long startTime;
		if (range.getPeriod() == null) {
			// 结束时间不能超过当前时间
			endTime = Math.min(range.getEndDate().getTime() / 1000, Util.getTime());
			startTime = range.getStartDate().getTime() / 1000;
		} else {
			endTime = Util.getTime();
			startTime = endTime - range.getPeriod().getDurationSeconds();
		}
		String label = getLabel();

		// 设置图像标签头
		StringBuilder title = new StringBuilder();
		if (label.length() > 31 && width <= 200) {
			title.append(label);
		} else {
			title.append(label).append(" - ").append(range.getLabel());
		}
		// 设置图像标签尾
		if (width > 400) {
			if (range.getPeriod() == null) {
				title.append(" - ").append(I18N.getFormattedString("at", application));
			} else {
				title.append(" - ").append(I18N.getCurrentDate()).append(' ')
				        .append(I18N.getFormattedString("at", application));
			}
		} else {
			if (range.getPeriod() == null) {
				graphDef.setLargeFont(graphDef.getLargeFont().deriveFont(
				        graphDef.getLargeFont().getSize2D() - 2f));
			}
		}
		graphDef.setStartTime(startTime);
		graphDef.setEndTime(endTime);
		graphDef.setTitle(title.toString());
		graphDef.setFirstDayOfWeek(Calendar.getInstance(I18N.getCurrentLocale())
		        .getFirstDayOfWeek());

		// User defined locale in graphics:
		// 参见:https://sourceforge.net/tracker/?func=detail&aid=3403733&group_id=82668&atid=566807
		// graphDef.setLocale(I18N.getCurrentLocale());

		// 设置图片宽高
		graphDef.setWidth(width);
		graphDef.setHeight(height);
		// 小图处理
		if (width <= 100) {
			graphDef.setNoLegend(true);
			graphDef.setUnitsLength(0);
			graphDef.setShowSignature(false);
			graphDef.setTitle(null);
		}
	}

	/**
	 * 获得JRobin名字
	 * 
	 * @author lichengwu
	 * @created 2012-2-14
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获得显示标签
	 * 
	 * @author lichengwu
	 * @created 2012-2-2
	 * 
	 * @return
	 */
	public String getLabel() {
		return I18N.getString(label);
	}

	/**
	 * 设置数据源
	 * 
	 * @author lichengwu
	 * @created 2012-2-2
	 * 
	 * @param graphDef
	 *            {@link RrdGraphDef}
	 * @param height
	 *            高度
	 */
	private void setGraphSource(RrdGraphDef graphDef, int height) {
		final String average = "average";
		final String max = "max";
		final String dataSourceName = getDataSourceName();
		graphDef.datasource(average, rrdFileName, dataSourceName, "AVERAGE");
		graphDef.datasource(max, rrdFileName, dataSourceName, "MAX");
		graphDef.setMinValue(0);
		final String averageLabel = I18N.getString("average_value");
		final String maxLabel = I18N.getString("max_value");
		graphDef.area(average, getPaint(height), averageLabel);
		graphDef.line(max, Color.BLUE, maxLabel);
		graphDef.gprint(average, "AVERAGE", averageLabel + ": %9.0f %S\\r");
		graphDef.gprint(max, "MAX", maxLabel + ": %9.0f %S\\r");
	}

	/**
	 * 根据图像高度获得显色模式定义
	 * 
	 * @author lichengwu
	 * @created 2012-2-2
	 * 
	 * @param height
	 *            图像高度
	 * @return 颜色模式
	 */
	private Paint getPaint(int height) {
		if (height == TINY_HEIGHT) {
			return SMALL_GRADIENT;
		}
		return new GradientPaint(0, 0, BRIGHT_RED, 0, height, Color.GREEN, false);
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

	/**
	 * 停止存储数据
	 * 
	 * @author lichengwu
	 * @throws GaaraException
	 * @created 2012-1-31
	 * 
	 * @throws GaaraException
	 */
	public void stop() throws GaaraException {
		try {
			getJRobinFileSyncTimer().cancel();
		} catch (Throwable e) {
			throw new GaaraException(e);
		}
	}

	/**
	 * 获得最后一次存储到数据库的值
	 * 
	 * @author lichengwu
	 * @created 2012-2-14
	 *
	 * @return 最后一次存储到数据库的值
	 * @throws GaaraException
	 */
	public double getLastValue() throws GaaraException {
		// 从池中获得rrd数据库的引用
		try {
			RrdDb rrdDb = rrdPool.requestRrdDb(rrdFileName);
			try {
				return rrdDb.getLastDatasourceValue(getDataSourceName());
			} finally {
				// 释放rrd数据库的引用
				rrdPool.release(rrdDb);
			}
		} catch (Exception ex) {
			throw new GaaraException(ex);
		}

	}
	
	/**
	 * 写入数据
	 * 
	 * @author lichengwu
	 * @created 2012-2-14
	 *
	 * @param value 数据
	 * @throws GaaraException
	 */
	public void addValue(double value) throws GaaraException {
		try {
			// 从池中获得rrd数据库引用
			final RrdDb rrdDb = rrdPool.requestRrdDb(rrdFileName);
			synchronized (rrdDb) {
				try {
					// create sample with the current timestamp
					final Sample sample = rrdDb.createSample();
					//如果最后更新时间大于当前时间，将会抛出异常：Bad sample timestamp
					if (sample.getTime() > rrdDb.getLastUpdateTime()) {
						// set value for load datasource
						sample.setValue(getDataSourceName(), value);
						// update database
						sample.update();
					}
				} finally {
					// 释放rrd数据库的引用
					rrdPool.release(rrdDb);
				}
			}
		} catch (Throwable e){ 
			if (e.getMessage() != null && e.getMessage().startsWith("Invalid file header")) {
				//如果rrd文件损坏，重置文件
				resetFile();
				addValue(value);
			}
			throw new GaaraException(e);
		}
		
	}
	
	
	/**
	 * 删除数据库
	 * 
	 * @author lichengwu
	 * @created 2012-2-14
	 *
	 * @return
	 */
	public boolean delete(){
		return FileUtil.delete(rrdFileName);
	}

	/**
	 * 获得JRobin的文件同步定时器
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @return
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private Timer getJRobinFileSyncTimer() throws IOException, SecurityException,
	        NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		final Field field = RrdNioBackend.class.getDeclaredField("fileSyncTimer");
		ReflectUtil.setFieldAccessible(field);
		return (Timer) field.get(null);
	}

}
