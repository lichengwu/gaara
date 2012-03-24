/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.info;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 
 * @author lichengwu
 * @created 2012-3-24
 * 
 * @version 1.0
 */
public class SerializableObject implements Serializable {

	private static final long serialVersionUID = 4885138836887753040L;

	private long longValue;
	
	private long longValue2;

	private String strValue;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(longValue);
		out.writeLong(longValue2);
		out.writeUTF(strValue);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		longValue = in.readLong();
		longValue2 = in.readLong();
		strValue = in.readUTF();
	}

	/**
	 * @return the longValue
	 */
	public long getLongValue() {
		return longValue;
	}

	/**
	 * @param longValue
	 */
	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	/**
	 * @return the strValue
	 */
	public String getStrValue() {
		return strValue;
	}

	/**
	 * @param strValue
	 */
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}


	/**
     * @return the longValue2
     */
    public long getLongValue2() {
    	return longValue2;
    }

	/**
     * @param longValue2
     */
    public void setLongValue2(long longValue2) {
    	this.longValue2 = longValue2;
    }

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("SerializableObject [longValue=");
	    builder.append(longValue);
	    builder.append(", longValue2=");
	    builder.append(longValue2);
	    builder.append(", strValue=");
	    builder.append(strValue);
	    builder.append("]");
	    return builder.toString();
    }
}
