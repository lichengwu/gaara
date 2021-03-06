/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cn.lichengwu.gaara.collector.factory.LocalCollectorFactory;
import cn.lichengwu.gaara.exception.GaaraException;
import cn.lichengwu.gaara.info.TransientInfo;
import cn.lichengwu.gaara.store.HttpDataRetriever;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 远程收集器
 * 
 * @author lichengwu
 * @created 2012-3-10
 * 
 * @version 1.0
 */
public class RemoteCollector {

	private static final Log log = LogFactory.getLog(RemoteCollector.class);

	private Map<String, DefaultInfoCollector> collectorHandler = new ConcurrentHashMap<String, DefaultInfoCollector>();

	/**
	 * 远程应用的名字
	 */
	private String application;

	private URL url;

	/**
	 * @param application
	 *            应用程序名字
	 * @param url
	 *            远程应用url
	 * @throws MalformedURLException
	 */
	public RemoteCollector(String application, String url) throws MalformedURLException {
		this(application, new URL(url));
	}

	/**
	 * @param application
	 *            应用程序名字
	 * @param url
	 *            远程应用url
	 */
	public RemoteCollector(String application, URL url) {
		assert application != null;
		assert url != null;
		this.application = application;
		this.url = url;
		log.info("init remote collector [" + application + ":" + url.toString() + "]");
	}

	/**
	 * 收集远程应用的监控信息
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @throws cn.lichengwu.gaara.exception.GaaraException
	 */
	public void collectRemoteApplication() throws GaaraException {
		Map<String, TransientInfo> call = null;
		try {
			call = new HttpDataRetriever(url).callPost();
		} catch (IOException e) {
			log.error("can not collect remote application[" + application + ":" + url.toString()
			        + "]", e);
		}
		long begin = System.currentTimeMillis();
		log.info("start collect remote application:" + application);
		// 收集所有信息
		for (Entry<String, TransientInfo> entry : call.entrySet()) {
			String collectorName = entry.getKey();
			TransientInfo info = entry.getValue();
			DefaultInfoCollector collector = collectorHandler.get(collectorName);
			if (collector == null) {
				collector = LocalCollectorFactory.newCollectorForRemote(collectorName, application, info);
				collectorHandler.put(collectorName, collector);
			}
			collector.saveInfo(info);
		}
		log.info("finish collect remote application:" + application + ", time used: "
		        + (System.currentTimeMillis() - begin) + "ms");
	}

	/**
	 * 获取远程应用的名字
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @return 远程应用的名字
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * 获取远程应用的url
	 * 
	 * @author lichengwu
	 * @created 2012-3-10
	 * 
	 * @return 远程应用的url
	 */
	public URL getURL() {
		return url;
	}
	
	/**
	 * 获得远程应用对应在本地的收集器
	 * 
	 * @author lichengwu
	 * @created 2012-3-22
	 * 
	 * @param collectorName
	 *
	 * @return 远程应用对应在本地的收集器 或者 null
	 */
	public DefaultInfoCollector getLocalCollector(String collectorName){
		return collectorHandler.get(collectorName);
	}
	
	/**
	 * 销毁远程收集器
	 * <p>
	 * 递归消耗远程收集器的rrd信息，及注册信息。
	 * </p>
	 * @author lichengwu
	 * @created 2012-3-18
	 *
	 */
	public synchronized void destory(){
		for(Entry<String, DefaultInfoCollector> entry : collectorHandler.entrySet()){
			entry.getValue().destory();
		}
	}

}
