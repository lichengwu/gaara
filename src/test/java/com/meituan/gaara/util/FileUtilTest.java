/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import cn.lichengwu.gaara.util.FileUtil;
import org.junit.Test;

/**
 *
 * @author lichengwu
 * @created 2012-2-2
 *
 * @version 1.0
 */
public class FileUtilTest {

	/**
	 * Test method for {@link cn.lichengwu.gaara.util.FileUtil#getStorageDirectory(java.lang.String)}.
	 */
	@Test
	public void testGetStorageDirectory() {
		System.out.println(FileUtil.getStorageDirectory("gaara"));
	}

	/**
	 * Test method for {@link cn.lichengwu.gaara.util.FileUtil#getResourcePath(java.lang.String)}.
	 */
	@Test
	public void testGetResourcePath() {
		System.out.println(FileUtil.getResourcePath(""));
	}

	/**
	 * Test method for {@link cn.lichengwu.gaara.util.FileUtil#getConfigPath()}.
	 */
	@Test
	public void testGetConfigPath() {
		System.out.println(FileUtil.getConfigPath());
	}

}
