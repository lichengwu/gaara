package com.meituan.gaara.web.servlet;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.collector.CollectorServer;
import com.meituan.gaara.collector.factory.SimpleRemoteCollectorFactory;
import com.meituan.gaara.util.I18N;
import com.meituan.gaara.util.ParameterUtil;
import com.meituan.gaara.util.ServletUtil;

public class MonitoringServlet extends HttpServlet {

	private static final long serialVersionUID = 7334334202508972434L;

	private static final Log log = LogFactory.getLog(MonitoringServlet.class);

	/**
	 * 初始化Servlet
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @param config
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
		// 初始化参数
		ParameterUtil.initialize(config.getServletContext());
		// 开始监控
		CollectorServer.getInstance().doCollect();
	}

	/**
	 * get请求
	 * <p>
	 * 查看监控信息。
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		I18N.bindLocale(request.getLocale());
		SimpleRemoteCollectorFactory.getInstance().addCollector("gaara_test", "http://localhost:8088/gt/gaara");
		I18N.unbindLocale();
	}

	/**
	 * post请求
	 * <p>
	 * 远程请求，用post。
	 * </p>
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @param request
	 * @param response
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		I18N.bindLocale(request.getLocale());
		Serializable lastestInfo = CollectorServer.getInstance().getLastestInfo();
		ServletUtil.writeSerializable(response, lastestInfo);
		I18N.unbindLocale();
	}

	/**
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		super.destroy();
		log.info(this.getClass().getSimpleName() + " destroy");
	}

}
