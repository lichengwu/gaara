/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lichengwu.gaara.util.Closer;
import cn.lichengwu.gaara.util.IOUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.lichengwu.gaara.util.I18N;

/**
 * 通过http收集数据
 * 
 * @author lichengwu
 * @created 2012-3-4
 * 
 * @version 1.0
 */
final public class HttpDataRetriever {

	private static final Log log = LogFactory.getLog(HttpDataRetriever.class);
	/**
	 * 连接超时20s
	 */
	private static final int CONNECTION_TIMEOUT = 20000;

	/**
	 * 数据读取超时50s
	 */
	private static final int READ_TIMEOUT = 50000;

	private URL url;

	private Map<String, String> headers;

	/**
	 * @param url
	 *            请求url
	 */
	public HttpDataRetriever(URL url) {
		assert url != null;
		this.url = url;
	}

	/**
	 * @param url
	 *            请求url
	 * @param headers
	 *            请求header
	 */
	public HttpDataRetriever(URL url, Map<String, String> headers) {
		assert url != null;
		assert headers != null;
		this.url = url;
		this.headers = headers;
	}

	/**
	 * 以POST方式获取数据
	 * 
	 * @author lichengwu
	 * @created 2012-3-4
	 * 
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public <T> T callPost() throws IOException {
		T result = null;
		HttpURLConnection conn = (HttpURLConnection) openConnection();
		conn.setRequestMethod("POST");
		conn.connect();
		result = (T) read(conn);
		if (result instanceof Throwable) {
			Throwable error = (Throwable) result;
			throw new IOException(error);
		}
		return result;
	}

	/**
	 * 以GET方式获取数据
	 * 
	 * @author lichengwu
	 * @created 2012-3-17
	 * 
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public <T> T callGet() throws IOException {
		T result = null;
		HttpURLConnection conn = (HttpURLConnection) openConnection();
		conn.setRequestMethod("POST");
		conn.connect();
		result = (T) read(conn);
		if (result instanceof Throwable) {
			Throwable error = (Throwable) result;
			throw new IOException(error);
		}
		return result;
	}

	/**
	 * 拷贝数据到响应流中
	 * 
	 * @author lichengwu
	 * @created 2012-3-4
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException
	 */
	public void copyTo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		assert request != null;
		assert response != null;
		final URLConnection conn = openConnection();
		conn.setRequestProperty("Accept-Language", request.getHeader("Accept-Language"));
		conn.connect();
		try {
			InputStream in = conn.getInputStream();
			if ("gzip".equals(conn.getContentEncoding())) {
				in = new GZIPInputStream(in);
			}
			response.setContentType(conn.getContentType());
			// 拷贝
			IOUtil.pump(in, response.getOutputStream());
		} finally {
			Closer.closeUrlConnection(conn);
		}
	}

	/**
	 * 从URLConnection读取数据
	 * 
	 * @author lichengwu
	 * @created 2012-3-4
	 * 
	 * @param conn
	 *            URLConnection
	 * @return 序列化的数据
	 * @throws IOException
	 */
	private Serializable read(URLConnection conn) throws IOException {
		Serializable result = null;
		try {
			InputStream in = conn.getInputStream();
			if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
				in = new GZIPInputStream(in);
			}
			String contentType = conn.getContentType();
			// text
			if (contentType != null
			        && (contentType.startsWith("text") || contentType
			                .startsWith("application/json"))) {
				result = IOUtil.readString(in);
			}
			// Serializable
			else {
				ObjectInputStream ois = new ObjectInputStream(in);
				result = (Serializable) ois.readObject();
			}

		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		} finally {
			String errMsg = Closer.closeUrlConnection(conn);
			if (errMsg != null) {
				log.warn("error occur while closing URLConnection:" + errMsg);
			}
		}
		return result;
	}

	/**
	 * 根据url和请求头打开URLConnection
	 * 
	 * @author lichengwu
	 * @created 2012-3-4
	 * 
	 * @return URLConnection
	 * @throws IOException
	 */
	private URLConnection openConnection() throws IOException {
		URLConnection conn = url.openConnection();
		// 关闭缓存
		conn.setUseCaches(false);
		// 设置超时
		conn.setReadTimeout(READ_TIMEOUT);
		conn.setConnectTimeout(CONNECTION_TIMEOUT);
		// 压缩流
		conn.setRequestProperty("Accept-Encoding", "gzip");
		// 设置语言
		conn.setRequestProperty("Accept-Language", I18N.getCurrentLocale().getLanguage());
		// 设置请求头信息
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return conn;
	}
}
