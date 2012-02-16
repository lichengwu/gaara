/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.collector;

import com.meituan.gaara.util.Parameter;
import com.meituan.gaara.util.ParameterUtil;

/**
 * 默认收集器
 *
 * @author lichengwu
 * @created 2012-2-16
 *
 * @version 1.0
 */
public abstract class DefaultStorageCollector implements Collector{

	protected boolean client = "client".equals(ParameterUtil.getParameter(Parameter.GAARA_RUN_MODE));
	
}
