/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.gaara.exception;

/**
 * GaaraException
 *
 * @author lichengwu
 * @created 2012-1-31
 *
 * @version 1.0
 */
public class GaaraException extends Exception {

    private static final long serialVersionUID = 7257966965457103680L;

	/**
     * 
     */
    public GaaraException() {
	    super();
    }

	/**
     * @param message
     * @param cause
     */
    public GaaraException(String message, Throwable cause) {
	    super(message, cause);
    }

	/**
     * @param message
     */
    public GaaraException(String message) {
	    super(message);
    }

	/**
     * @param cause
     */
    public GaaraException(Throwable cause) {
	    super(cause);
    }

}
