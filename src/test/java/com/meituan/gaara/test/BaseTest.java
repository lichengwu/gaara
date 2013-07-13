/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.test;

import java.util.Locale;

import org.junit.Before;

import cn.lichengwu.gaara.util.I18N;
import cn.lichengwu.gaara.util.ParameterUtil;

/**
 *
 * 测试基础类
 * @author lichengwu
 * @created 2012-2-14
 *
 * @version 1.0
 */
public class BaseTest {

	/**
	 * 初始化
	 * 
	 * @author lichengwu
	 * @created 2012-2-14
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ParameterUtil.initialize(null);
		I18N.bindLocale(Locale.CHINESE);
	}
}
