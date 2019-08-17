/*
 * Copyright (c) 2012,  All rights reserved.
 */
package com.github.zenframework.util;

import java.io.ByteArrayInputStream;

/**
 * IO utility
 * @author Zed 2012-5-24
 */
public class IOUtils extends org.apache.commons.io.IOUtils {
	
	/**
	 * Byte array to input stream
	 * @param bytes
	 * @return
	 */
	public static ByteArrayInputStream toByteArrayInputStream(byte[] bytes) {
		return new ByteArrayInputStream(bytes);   
	}

}
