/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import cn.lichengwu.gaara.util.ParameterUtil;
import org.junit.Test;

import com.meituan.gaara.test.BaseTest;


/**
 *
 * @author lichengwu
 * @created 2012-2-2
 *
 * @version 1.0
 */
public class ParameterUtilTest extends BaseTest{
	@Test
	public void test(){
		ParameterUtil.storeCustomerProperties("key.sss", "value");
		System.out.println(ParameterUtil.getParameter("key.sss"));
	}
}
