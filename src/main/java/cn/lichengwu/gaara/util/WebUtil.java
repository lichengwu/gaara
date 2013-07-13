/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

/**
 * web工具类
 * 
 * @author lichengwu
 * @created 2012-2-16
 * 
 * @version 1.0
 */
final public class WebUtil {

	private WebUtil() {
	}

	/**
	 * 获得当前应该的名字
	 * <p>
	 * <b>格式:</b>contextPath+@+主机名称，<br />
	 * 如果contextPath不能获得，contextPath="unknown"
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 *
	 * @return 获得当前应该的名字
	 */
	public static String getFullCurrentApplication(){
		String contextPath = getContextPath(ParameterUtil.getServletContext());
		if(contextPath==null || "".equals(contextPath)){
			contextPath="unknown";
		}
		return contextPath+"@"+ParameterUtil.getParameter(Parameter.HOST_NAME);
	}
	
	/**
	 * 获取ContextPath
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 * 
	 * @param context
	 * @return 获取ContextPath
	 */
	public static String getContextPath(ServletContext context) {
		if (context == null) {
			return null;
		}
		// 2.5以后版本直接取值
		if (context.getMajorVersion() == 2 && context.getMinorVersion() >= 5
		        || context.getMajorVersion() > 2) {
			return context.getContextPath();
		}
		URL webXmlUrl;
		try {
			webXmlUrl = context.getResource("/WEB-INF/web.xml");
		} catch (final MalformedURLException e) {
			throw new IllegalStateException(e);
		}
		String contextPath = webXmlUrl.toExternalForm();
		contextPath = contextPath.substring(0, contextPath.indexOf("/WEB-INF/web.xml"));
		final int indexOfWar = contextPath.indexOf(".war");
		if (indexOfWar > 0) {
			contextPath = contextPath.substring(0, indexOfWar);
		}
		// tomcat 可能返回 jndi:/localhost
		if (contextPath.startsWith("jndi:/localhost")) {
			contextPath = contextPath.substring("jndi:/localhost".length());
		}
		final int lastIndexOfSlash = contextPath.lastIndexOf('/');
		if (lastIndexOfSlash != -1) {
			contextPath = contextPath.substring(lastIndexOfSlash);
		}
		return contextPath;
	}
}
