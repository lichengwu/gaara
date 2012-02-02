/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.jvm;

import java.io.Serializable;

import com.meituan.gaara.info.TransientInfo;


/**
 * 虚拟机信息
 * 
 * @author lichengwu
 * @created 2012-1-8
 * 
 * @version 1.0
 */
final public class VirtualMachineInfo implements TransientInfo,Serializable {

    private static final long serialVersionUID = 4145011994188568350L;

    public boolean refresh() {
	    return false;
    }
	
}
