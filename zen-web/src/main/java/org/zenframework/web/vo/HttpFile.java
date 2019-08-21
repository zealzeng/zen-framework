/*
 * Copyright (c) 2013,  All rights reserved.
 */
package org.zenframework.web.vo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * @author zeal 2013-2-4
 */
public class HttpFile {
	
	private String fileName = "";
	private byte[] bytes = null;
	private String contentType = "";
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}
	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		if (contentType != null) {
		    this.contentType = contentType;
		}
		
	}
	
	public InputStream toInputStream() {
		return new ByteArrayInputStream(this.bytes);
	}
	

}
