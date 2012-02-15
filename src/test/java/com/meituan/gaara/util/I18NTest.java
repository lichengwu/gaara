/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;


import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author lichengwu
 * @created 2012-2-14
 *
 * @version 1.0
 */
public class I18NTest {

	/**
	 * 
	 * @author lichengwu
	 * @created 2012-2-14
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ParameterUtil.initialize(null);
	}
	
	@Test
	public void test(){
//		//System.out.println(I18N.getCurrentDate());
		System.out.println(I18N.getString("at"));
	}

}
