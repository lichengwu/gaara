/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.store;

import java.io.IOException;
import java.net.URL;

import cn.lichengwu.gaara.store.HttpDataRetriever;
import org.junit.Test;


/**
 *
 * @author lichengwu
 * @created 2012-3-4
 *
 * @version 1.0
 */
public class HttpDataRetrieverTest {

	@Test
	public void test1() throws IOException{
		URL url = new URL("http://www.meituan.com");
		String call = new HttpDataRetriever(url).callPost();
		System.out.println(call);
	}
}
