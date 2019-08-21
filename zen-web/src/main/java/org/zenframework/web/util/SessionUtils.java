/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Http session utility, suggest always using HttpServletRequest/Response 
 * in spring ctrl but not WebRequest/Response
 * @author Zeal
 * @since 2016年4月28日
 */
public class SessionUtils {
	
	/**
	 * Set value to session
	 * @param request
	 * @param key
	 * @param value
	 */
	public static void setAttribute(HttpServletRequest request, String key, Object value) {
		request.getSession(true).setAttribute(key, value);
	}
	
    /**
     * Get attribute and not depend on concrete VO
     * @param request
     * @param key
     * @param cls
     * @return
	 * @deprecated
     */
    @SuppressWarnings("unchecked")
	public static <T>T getAttribute(HttpServletRequest request, String key, Class<T> cls) {
    	HttpSession session = request.getSession(false);
    	if (session == null) {
    		return null;
    	}
    	return (T) session.getAttribute(key);
    }

    public static <T>T getAttribute(HttpServletRequest request, String key) {
    	HttpSession session = request.getSession(false);
    	if (session == null) {
    		return null;
    	}
    	return (T) session.getAttribute(key);
    }
	
	/**
	 * Remove session attribute by key
	 * @param request
	 * @param key
	 */
	public static void removeAttribute(HttpServletRequest request, String key) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(key);
		}
	}
	
	/**
	 * Invalidate session
	 * @param request
	 */
	public static void invalidate(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

}
