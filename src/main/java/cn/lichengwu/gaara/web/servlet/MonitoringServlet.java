package cn.lichengwu.gaara.web.servlet;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lichengwu.gaara.collector.CollectorServer;
import cn.lichengwu.gaara.util.ParameterUtil;
import cn.lichengwu.gaara.util.ServletUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.lichengwu.gaara.util.I18N;

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
		// super.init(config);
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
		
		RequestHandler.handle(request, response);
		
//		RemoteCollectorFactory.getInstance().addRemoteCollector("gt", "http://localhost:8088/gt/");
		
//		request.setAttribute("test", "ssss");
//		request.getRequestDispatcher("index.ftl").forward(request, response);
//		InputStream in = null;
//		try {
//			in = FileUtil.class.getResourceAsStream(FileUtil.getResourcePath("resources/lang/language_zh.properties"));
//			System.out.println(FileUtil.getResourcePath("resources/lang/language_zh.properties"));
//			IOUtil.pump(in, response.getOutputStream());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			Closer.close(in);
//		}
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
