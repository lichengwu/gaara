/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.info;

/**
 * 瞬时信息适配器
 *
 * @author lichengwu
 * @created 2012-2-16
 *
 * @version 1.0
 */
public class TransientInfoAdapter implements TransientInfo{

    private static final long serialVersionUID = 236172952310263876L;

	private long lastUpdate=0;
	
	private TransientInfo info;
	
	public TransientInfoAdapter(TransientInfo info){
		this.info=info;
	}
	
	public boolean refresh(){
		lastUpdate= System.currentTimeMillis();
		return info.refresh();
	}
	
	public long lastUpdate(){
		return lastUpdate;
	}
}
