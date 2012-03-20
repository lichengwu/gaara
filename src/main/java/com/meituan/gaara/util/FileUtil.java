/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 文件工具类
 * 
 * @author lichengwu
 * @created 2012-1-22
 * 
 * @version 1.0
 */
final public class FileUtil {
	
	private static final Log log = LogFactory.getLog(FileUtil.class);

	/**
	 * 临时文件目录
	 */
	public static File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));

	private static String BASE_RESOURCES_PATH = "/com/meituan/gaara/";

	private static final String DEFAULT_DIR = "gaara";

	/**
	 * 判断路径是否是绝对路径
	 * 
	 * @author lichengwu
	 * @created 2012-1-22
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static boolean isAbsolute(String path) {
		if (path == null || path.length() < 1) {
			return false;
		}
		return new File(path).isAbsolute();
	}

	/**
	 * 确保文件路径存在，如果不存在就创建路径
	 * 
	 * @author lichengwu
	 * @created 2012-1-22
	 * 
	 * @param path
	 *            文件路径
	 * @return 如果path是一个路径，并且能够创建创建，返回true；否则返回false
	 */
	public static boolean ensureFilePath(File path) {
		return path.mkdirs() || path.exists();
	}

	/**
	 * 确保文件路径存在，如果不存在就创建路径
	 * 
	 * @author lichengwu
	 * @created 2012-1-22
	 * 
	 * @param path
	 *            文件路径
	 * @return 如果path是一个路径，并且能够创建创建，返回true；否则返回false
	 */
	public static boolean ensureFilePath(String path) {
		return ensureFilePath(new File(path));
	}

	/**
	 * 删除
	 * 
	 * @author lichengwu
	 * @created 2012-1-31
	 * 
	 * @param path
	 *            文件或目录的对路径
	 * @return 文件或目录删除成功返回true，否则返回false
	 */
	public static boolean delete(String path) {
		return new File(path).delete();
	}

	/**
	 * 获得gaara文件存储路径
	 * 
	 * @author lichengwu
	 * @created 2012-1-22
	 * 
	 * @param application
	 *            应用名字
	 * @return 获得gaara文件存储路径
	 */
	public static File getStorageDirectory(String application) {
		String dir = ParameterUtil.getParameter(Parameter.STORAGE_DIRECTORY);
		if (dir == null) {
			dir = DEFAULT_DIR;
		}
		String absolutePath;
		// 绝对路径处理
		if (FileUtil.isAbsolute(dir)) {
			absolutePath = dir;
		}
		// 相对路径处理
		else {
			absolutePath = FileUtil.TEMP_DIR.getPath() + File.separator + dir;
		}
		// web app
		if (ParameterUtil.getServletContext() != null) {
			return new File(absolutePath + File.separator + application + "@"
			        + ParameterUtil.getParameter(Parameter.HOST_NAME));
		}
		// 非web app 获得 servletContext==null
		return new File(absolutePath);
	}

	/**
	 * 获得资源的路径
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @param fileName
	 *            文件名
	 * @return 资源文件位置
	 */
	public static String getResourcePath(String fileName) {
		assert fileName != null;
		assert fileName.length() > 1;
		if (fileName.startsWith("/")) {
			fileName = fileName.substring(1);
		}
		return BASE_RESOURCES_PATH + fileName;
	}
	
	/**
	 * 已字符串的形式获得资源
	 * 
	 * @author lichengwu
	 * @created 2012-3-20
	 *
	 * @param resource 资源的path
	 * @return 资源的内容
	 */
	public static String getResourceAsString(String resource){
		assert resource!=null;
		String result=null;
		InputStream in=null;
		try {
	        in = FileUtil.class.getResourceAsStream(getResourcePath(resource));
	        result=IOUtil.readString(in);
        } catch (IOException e) {
        	log.error(e.getMessage(), e);
        	return null;
        }finally{
        	Closer.close(in);
        }
		return result;
	}

	/**
	 * 获得配置文件的路径
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @return 配置文件的路径
	 */
	public static String getConfigPath() {
		return getResourcePath("conf/");
	}

	public static void main(String[] args) {
		File file = new File(
		        "/D:/servers/mtssh/webapps/gt/WEB-INF/lib/gaara-1.0.1-SNAPSHOT.jar!/com/meituan/gaara/conf/");
		System.out.println(file.isDirectory());
	}
}
