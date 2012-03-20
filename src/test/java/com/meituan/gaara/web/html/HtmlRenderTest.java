/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.web.html;

import java.util.HashMap;

import org.junit.Test;

import com.meituan.gaara.test.BaseTest;

/**
 *
 * @author lichengwu
 * @created 2012-3-20
 *
 * @version 1.0
 */
public class HtmlRenderTest extends BaseTest {

	@Test
	public void test(){
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("test", "中文");
		HtmlRender instance = HtmlRender.getInstance();
		String render = instance.render(TemplateFile.INDEX, data);
		System.out.println(render);
	}

}
