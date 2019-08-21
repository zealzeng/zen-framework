/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.web.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import org.zenframework.web.util.WebUtils;
import org.zenframework.web.vo.ErrorCode;

/**
 * Extract some common methods 
 * @author Zeal since 2016年5月6日
 * @deprecated
 */
public abstract class HandlerInterceptorSupport extends HandlerInterceptorAdapter {
	
	/**
	 * Find annocation in method header first, and then find in class header
	 * @param handlerMethod
	 * @param clazz
	 * @return
	 */
	protected <A extends Annotation>A getMethodPageAnnotation(HandlerMethod handlerMethod, Class<A> clazz) {
		if (handlerMethod == null) {
			return null;
		}
		//TODO Reserve the method/class as cache? which is faster?
		//We had better to load all the @UserPage when system is started
		A up = handlerMethod.getMethodAnnotation(clazz);
		if (up != null) {
			return up;
		}
		Class<?> ctrlClass = handlerMethod.getMethod().getDeclaringClass();
		return AnnotationUtils.findAnnotation(ctrlClass, clazz);
	}
	
	/**
	 * Handle error
	 * @param request
	 * @param response
	 * @param errorCode
	 * @return
	 * @throws IOException 
	 */
	protected boolean error(HttpServletRequest request, HttpServletResponse response, HandlerMethod method, int errorCode) throws Exception {
		if (WebUtils.isAjaxRequest(request)) {
			response.setStatus(errorCode);
		}
		else {
			//request.setAttribute(ErrorCode.ERROR_CODE, errorCode);
			errorPage(request, response, method, errorCode);
		}
		return false;
	}
	
	/**
	 * Handle error page(Not ajax request)
	 * @param request
	 * @param response
	 * @param errorCode
	 * @see ErrorCode
	 */
	protected void errorPage(HttpServletRequest request, HttpServletResponse response, HandlerMethod method, int errorCode) throws Exception {
		request.setAttribute(ErrorCode.ERROR_CODE, errorCode);
		throw new IllegalStateException("Error code is " + errorCode);
	}

}
