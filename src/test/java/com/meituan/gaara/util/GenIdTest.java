/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.Test;

/**
 *
 * @author lichengwu
 * @created 2012-1-20
 *
 * @version 1.0
 */
public class GenIdTest {
	
	static MessageDigest messageDigest = null;
	static{
		try {
	        messageDigest=MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
        }
	}
	
	public String genId(String name) throws NoSuchAlgorithmException{
		messageDigest.update(name.getBytes());
		final byte[] digest = messageDigest.digest();

		final StringBuilder sb = new StringBuilder();
		int j;
		for (final byte element : digest) {
			j = element < 0 ? 256 + element : element;
			if (j < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(j));
		}
		return sb.toString();
	}
	
	@Test
	public void testUUID(){
		for(int i=0;i<1000000;i++){
			UUID.randomUUID().toString();
		}
	}
	
	@Test
	public void testGenID() throws NoSuchAlgorithmException{
		for(int i=0;i<1000000;i++){
			genId("oliver"+i);
		}
	}

}
