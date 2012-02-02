/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import org.junit.Test;


/**
 *
 * @author lichengwu
 * @created 2012-2-2
 *
 * @version 1.0
 */
public class ParameterUtilTest {
	@Test
	public void test(){
		ParameterUtil.initialize(null);
		System.out.println("PID:"+ParameterUtil.getParameter(Parameter.PID));
		System.out.println("java_version:"+ParameterUtil.getParameter(Parameter.JAVA_VERSION));
	}
}
