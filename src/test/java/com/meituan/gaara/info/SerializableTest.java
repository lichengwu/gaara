/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.info;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

/**
 *
 * @author lichengwu
 * @created 2012-3-24
 *
 * @version 1.0
 */
public class SerializableTest {

	@Test
	public void testWrite() throws IOException{
		SerializableObject obj = new SerializableObject();
		obj.setLongValue(1);
		obj.setLongValue2(2);
		obj.setStrValue("oliver");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("E:\\a.txt"));
		oos.writeObject(obj);
	}
	
	@Test
	public void testRead() throws IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("E:\\a.txt"));
		SerializableObject obj = (SerializableObject) ois.readObject();
		System.out.println(obj);
	}
}
