/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;


import java.text.ParseException;

import org.junit.Test;

import com.meituan.gaara.test.BaseTest;

/**
 *
 * @author lichengwu
 * @created 2012-2-14
 *
 * @version 1.0
 */
public class I18NTest extends BaseTest {

	@Test
	public void test(){
//		//System.out.println(I18N.getCurrentDate());
		System.out.println(I18N.getString("at"));
	}
	
	@Test
	public void testDateFormat() throws ParseException{
//		System.out.println(I18N.getCurrentDate());
		System.out.println(I18N.createDateFormat().parse("12-3-22"));
	}

}
