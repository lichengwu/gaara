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
 * 
 * @author lichengwu
 * @created 2012-1-9
 * @modified 2012-2-2
 * 
 * @version 1.1
 */
final public class ParameterUtil implements Serializable {

	private static final Log log = LogFactory.getLog(ParameterUtil.class);

	private static final long serialVersionUID = 1291142391439595995L;

	private static Properties properties = new Properties();

	private static final String DEFAULT_DIR = "gaara";

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
		log.info("start init param...");
		servletContext = context;
		try {
			readProperties();
			initLocalInfo();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		log.info("finish init param...");
	}

	/**
	 * 获得参数值
	 * <p>
	 * <b>读取参数顺序:</b><br />
	 * 1.com/meituan/gaara/conf下所有properties文件。<br />
	 * 2.读取{@link ServletContext#getInitParameter(String)}。<br />
	 * 3.{@link System#getProperty(String)}。
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @param parameter
	 *            参数
	 * @return 参数值(字符串)
	 */
	public static String getParameter(Parameter parameter) {
		assert parameter != null;
		String name = parameter.getName();
		// 1.com/meituan/gaara/conf下所有properties文件读取
		String value = properties.getProperty(name);
		if (value != null) {
			return value;
		}
		// 2.如果 servletContext不为空，读取初始化参数
		if (servletContext != null) {
			value = servletContext.getInitParameter(name);
			if (value != null) {
				return value;
			}
		}
		// 3.从系统读取
		value = System.getProperty(name);
		if (value != null) {
			return value;
		}
		return null;
	}

	/**
	 * 获得数字形式参数值
	 * <p>
	 * 参考:{@link ParameterUtil#getParameter(Parameter)}。·
	 * 
	 * @author lichengwu
	 * @created 2012-1-22
	 * 
	 * @param parameter
	 *            参数
	 * @return 数字
	 */
	public static int getParameterAsInt(Parameter parameter) {
		return Integer.valueOf(getParameter(parameter));
	}

	/**
	 * 获得参数值
	 * <p>
	 * <b>读取参数顺序:</b><br />
	 * 1.com/meituan/gaara/conf下所有properties文件。<br />
	 * 2.读取{@link ServletContext#getInitParameter(String)}。<br />
	 * 3.{@link System#getProperty(String)}。
	 * 
	 * @author lichengwu
	 * @created 2012-1-13
	 * 
	 * @param name
	 *            参数
	 * @return 参数值(字符串)
	 */
	public static String getParameter(String name) {
		assert name != null;
		// 1.com/meituan/gaara/conf下所有properties文件读取
		String value = properties.getProperty(name);
		if (value != null) {
			return value;
		}
		// 2.如果 servletContext不为空，读取初始化参数
		if (servletContext != null) {
			value = servletContext.getInitParameter(name);
			if (value != null) {
				return value;
			}
		}
		// 3.从系统读取
		value = System.getProperty(name);
		if (value != null) {
			return value;
		}
		return null;
	}

	/**
	 * 获得数字形式参数值
	 * <p>
	 * 参考:{@link ParameterUtil#getParameter(Parameter)}。·
	 * 
	 * @author lichengwu
	 * @created 2012-1-22
	 * 
	 * @param name
	 *            参数名字
	 * @return 数字
	 */
	public static int getParameterAsInt(String name) {
		return Integer.valueOf(getParameter(name));
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
			public boolean accept(File dir, String name) {
				return name.endsWith(".properties");
			}
		});
		// 读取配置文件
		if (configFiles != null && configFiles.length > 0) {
			for (File configFile : configFiles) {
				log.info("read properties from file:" + configFile.getCanonicalPath());
				properties.load(new FileReader(configFile));
			}
		}
	}

	/**
	 * 设置参数
	 * 
	 * @author lichengwu
	 * @created 2012-2-2
	 * 
	 * @param name
	 *            参数名
	 * @param value
	 *            参数值
	 */
	public static void setParameter(String name, Object value) {
		assert name != null;
		assert value != null;
		properties.put(name, value);
	}

	/**
	 * 设置参数
	 * 
	 * @author lichengwu
	 * @created 2012-2-2
	 * 
	 * @param parameter
	 *            参数
	 * @param value
	 *            参数值
	 */
	public static void setParameter(Parameter parameter, Object value) {
		assert parameter != null;
		assert value != null;
		properties.put(parameter.getName(), value);
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
			log.info("get host name:" + hostName);
		}
		if (hostAddress != null) {
			properties.put(Parameter.HOST_ADDRESS.getName(), hostAddress);
			log.info("get host address:" + hostAddress);
		}
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
		if (servletContext != null) {
			return new File(absolutePath + File.separator + application);
		}
		// 非web app 获得 servletContext==null
		return new File(absolutePath);
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
