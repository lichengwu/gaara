/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.web.html;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meituan.gaara.util.FileUtil;
import com.meituan.gaara.util.ServletUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * html工具
 * 
 * @author lichengwu
 * @created 2012-3-19
 * 
 * @version 1.0
 */
final public class HtmlRender {

	private static final Log log = LogFactory.getLog(HtmlRender.class);

	private Configuration conf = new Configuration();

	/**
	 * 单例
	 */
	private static HtmlRender INSTANCE = new HtmlRender();

	/**
	 * 获得{@link HtmlRender}实例
	 * 
	 * @author lichengwu
	 * @created 2012-3-20
	 * 
	 * @return {@link HtmlRender}实例
	 */
	public static HtmlRender getInstance() {
		return INSTANCE;
	}

	/**
	 * 私有化构造方法
	 */
	private HtmlRender() {
		loadTemplate();
	}

	/**
	 * 加载模板
	 * 
	 * @author lichengwu
	 * @created 2012-3-20
	 * 
	 */
	private void loadTemplate() {
		StringTemplateLoader loader = new StringTemplateLoader();
		for (TemplateFile file : TemplateFile.values()) {
			loader.putTemplate(file.getName(),
			        FileUtil.getResourceAsString("resources/views/" + file.getName()));
		}
		conf.setTemplateLoader(loader);
		conf.setObjectWrapper(new DefaultObjectWrapper());
	}

	/**
	 * 获得freemarker模板
	 * 
	 * @author lichengwu
	 * @created 2012-3-20
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Template getTemplate(TemplateFile file) throws IOException {
		Template template = conf.getTemplate(file.getName());
		return template;
	}

	/**
	 * 渲染html文件
	 * 
	 * @author lichengwu
	 * @created 2012-3-20
	 * 
	 * @param file
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String render(TemplateFile file, Map<String, Object> data) {
		try {
			Template template = conf.getTemplate(file.getName());
			StringWriter writer = new StringWriter();
			template.process(data, writer);
			writer.flush();
			// StringWriter的关闭方法是空实现
			// writer.close();
			return writer.getBuffer().toString();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return ServletUtil.exception2HTML(e);
		} catch (TemplateException e) {
			log.error(e.getMessage(), e);
			return ServletUtil.exception2HTML(e);
		}
	}

}
