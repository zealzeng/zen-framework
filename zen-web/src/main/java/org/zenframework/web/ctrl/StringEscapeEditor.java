/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.web.ctrl;

import java.beans.PropertyEditorSupport;

import org.zenframework.util.StringEscapeUtils;

/**
 * @author Zeal 2016年5月31日
 */
public class StringEscapeEditor extends PropertyEditorSupport {
	
	@Override
	public void setAsText(String text) {
		if (text == null) {
			this.setValue(null);
		}
		else {
		    text = StringEscapeUtils.escapeHtml4(text);
		    this.setValue(text);
		}
	}

	@Override
	public String getAsText() { 
		Object value = getValue(); 
		return value != null ? value.toString() : "";
	}

}
