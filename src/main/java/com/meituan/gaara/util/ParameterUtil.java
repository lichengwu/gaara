package com.meituan.gaara.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Garra系统参数
 * 
 * @author lichengwu
 * @created 2012-1-9
 * 
 * @version 1.0
 */
final public class ParameterUtil implements Serializable {

	private static final Log log = LogFactory.getLog(ParameterUtil.class);

	private static final long serialVersionUID = 1291142391439595995L;

	private static Properties properties = new Properties();

	private static ServletContext servletContext = null;

	/**
	 * 私有构造方法
	 */
	private ParameterUtil() {
		super();
	}

	/**
	 * 初始化方法
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @param context
	 */
	public static void initialize(ServletContext context) {
		servletContext = context;
		try {
			readProperties();
			initLocalInfo();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 获得参数值
	 * <p>
	 * <b>读取参数顺序:</b><br />
	 * 1.{@link System#getProperty(String)}<br />
	 * 2.com/meituan/gaara/conf下所有properties文件<br />
	 * 3.如果 {@link #getServletContext()}不为空，读取
	 * {@link ServletContext#getInitParameter(String)}<br />
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @param parameter
	 * @return
	 */
	public static String getParameter(Parameter parameter) {
		assert parameter != null;
		String name = parameter.getName();
		String value = System.getProperty(name);
		if (value != null) {
			return value;
		}
		value = properties.getProperty(name);
		if (value != null) {
			return value;
		}
		if (servletContext != null) {
			value = servletContext.getInitParameter(name);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * 获得参数值
	 * <p>
	 * <b>读取参数顺序:</b><br />
	 * 1.{@link System#getProperty(String)}<br />
	 * 2.com/meituan/gaara/conf下所有properties文件<br />
	 * 3.如果 {@link #getServletContext()}不为空，读取
	 * {@link ServletContext#getInitParameter(String)}<br />
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @param name
	 *            参数名字
	 * @return
	 */
	public static String getParameter(String name) {
		assert name != null;
		String value = System.getProperty(name);
		if (value != null) {
			return value;
		}
		value = properties.getProperty(name);
		if (value != null) {
			return value;
		}
		if (servletContext != null) {
			value = servletContext.getInitParameter(name);
			if (value != null) {
				return value;
			}
		}
		return null;
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
		return ParameterUtil.class.getResource("/com/meituan/gaara/resources/" + fileName)
		        .getFile();
	}

	/**
	 * 获得配置文件的路径
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @return 配置文件的路径
	 */
	private static String getConfigPath() {
		return ParameterUtil.class.getResource("/com/meituan/gaara/conf/").getFile();
	}

	/**
	 * 读取配置文件
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void readProperties() throws FileNotFoundException, IOException {
		File file = new File(getConfigPath());
		File[] configFiles = file.listFiles(new FilenameFilter() {
			// 所有配置文件
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".properties");
			}
		});
		// 读取配置文件
		if (configFiles != null && configFiles.length > 0) {
			for (File configFile : configFiles) {
				properties.load(new FileReader(configFile));
			}
		}
	}

	/**
	 * 初始化本地信息
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 */
	private static void initLocalInfo() {
		// 主机名字
		String hostName = null;
		// 主机地址
		String hostAddress = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.warn(e.getMessage());
		}
		if (hostName != null) {
			properties.put(Parameter.HOST_NAME.getName(), hostName);
		}
		if (hostAddress != null) {
			properties.put(Parameter.HOST_ADDRESS.getName(), hostAddress);
		}
	}

	/**
	 * @return the servletContext
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @param servletContext
	 */
	public static void setServletContext(ServletContext servletContext) {
		ParameterUtil.servletContext = servletContext;
	}

	public static void main(String[] args) throws IOException {
		ParameterUtil.initialize(null);
		System.out.println(ParameterUtil.getParameter("java.version"));
		;
	}

}
