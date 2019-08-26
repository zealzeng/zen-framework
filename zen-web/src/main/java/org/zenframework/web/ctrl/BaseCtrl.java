/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.web.ctrl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import org.zenframework.util.NumberUtils;
import org.zenframework.util.StringUtils;
import org.zenframework.common.Result;
import org.zenframework.web.util.WebUtils;
import org.zenframework.web.error.WebError;
import org.zenframework.web.common.Pagination;

/**
 * Extract some common functions of message resource,pagination,json
 * @author Zeal
 * @since 2016年4月27日
 */
public abstract class BaseCtrl {
	
	@Autowired(required = false)
	protected MessageSource messageSource;
	
	/**
	 * Get message by key from resource
	 * @param key
	 * @return
	 */
	protected String getMessage(String key) {
		try {
		    return this.messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
		}
		catch (Exception e) {
			e.printStackTrace();
			return key;
		}
	}
	
	/**
	 * The error message's key is started with 'err'
	 * @param errorCode
	 * @return
	 */
	protected String getErrorMessage(int errorCode) {
		String key = WebError.KEY_PREFIX + errorCode;
		try {
		    return this.messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
		}
		catch (Exception e) {
		    e.printStackTrace();
			return getDefaultErrorMessage(errorCode);
		}
	}
	
	/**
	 * Create default error message if errors do not exist in message source
	 * @param errorCode
	 * @return
	 */
	private String getDefaultErrorMessage(int errorCode) {
		//Avoid web application missing these public error code defined in ErrorCode
		if (errorCode == WebError.NO_ERROR) {
			return "Operation is successful";
		}
		else if (errorCode == WebError.UNKNOWN_ERROR) {
			return "Unknown error";
		}
//		else if (errorCode == WebError.INVALID_SESSION) {
//			return "User session is expired or invalid";
//		}
//		else if (errorCode == WebError.INVALID_SESSION_TOKEN) {
//			return "Request token is invalid";
//		}
		else {
			return "error code is " + errorCode;
		}
	}
	
	/**
	 * The new version of pagination object
	 * @param request
	 * @return
	 */
	protected Pagination<?> getPagination(HttpServletRequest request) {
		return getPagination(request, true);
	}

	
	/**
	 * The new version of pagination object
	 * @param request
	 * @return
	 */
	protected Pagination<?> getPagination(HttpServletRequest request, boolean generateDefault) {
		if (!this.isPaginationRequest(request) && !generateDefault) {
			return null;
		}
		Pagination<?> page = new Pagination<>();
		String pageNoStr = request.getParameter(Pagination.PAGE_NO);
		int pageNo = 1;
		if(StringUtils.isNotBlank(pageNoStr)){
			pageNo = NumberUtils.toInt(pageNoStr, 1);
			if (pageNo <= 0) {
				pageNo = 1;
			}
		}
		String pageSizeStr = request.getParameter(Pagination.PAGE_SIZE);
		int pageSize = Pagination.DEFALUT_PAGE_SIZE;
		if(StringUtils.isNotBlank(pageSizeStr)){
			pageSize = NumberUtils.toInt(pageSizeStr, Pagination.DEFALUT_PAGE_SIZE);
			if (pageSize > Pagination.MAX_PAGE_SIZE) {
				pageSize = Pagination.MAX_PAGE_SIZE;
			}
			if (pageSize <= 0) {
				pageSize = Pagination.DEFALUT_PAGE_SIZE;
			}
		}
		//Notice, never use it directly in your sql since it's not filtered
		String sortKey = request.getParameter(Pagination.SORT_KEY);
		String sortAscStr = request.getParameter(Pagination.SORT_ASC);
		boolean sortAsc = true;
		if(StringUtils.isNotBlank(sortAscStr)){
			sortAsc = Boolean.valueOf(sortAscStr);
		}
		page.setPageNum(pageNo);
		page.setNumPerPage(pageSize);
		page.setSortKey(sortKey);
		page.setSortAsc(sortAsc);		
		page.setRequestParamMap(WebUtils.getRequestParameterSingleMap(request, true));
		page.setUri(WebUtils.getRequestURI(request));
		return page;
	}
	
	/**
	 * Check whether it's pagination request
	 * @param request
	 * @return
	 */
	private boolean isPaginationRequest(HttpServletRequest request) {
		String pageNoStr = request.getParameter(Pagination.PAGE_NO);
		if (StringUtils.isBlank(pageNoStr)) {
			return false;
		}
		int pageNo = NumberUtils.toInt(pageNoStr, 0);
		return pageNo > 0;
	}
	
	/**
	 * Get result 
	 * @param errorCode
	 * @return
	 */
	protected <T>Result<T> getResult(int errorCode) {
		return getResult(errorCode, this.getErrorMessage(errorCode));
	}
	
	/**
	 * Get result 
	 * @param errorCode
	 * @return
	 */
	protected <T>Result<T> getResult(int errorCode, String resultMessage) {
		Result<T> result = new Result<>(errorCode);
		result.setResultMessage(resultMessage);
		return result;
	}
	
	/**
	 * Get result 
	 * @param errorCode
	 * @return
	 */
	protected <T>Result<T> getResult(int errorCode, String resultMessage, T resultEntity) {
		Result<T> result = new Result<>(errorCode);
		result.setResultMessage(resultMessage);
		result.setResultEntity(resultEntity);
		return result;
	}
	
	/**
	 * Get result 
	 * @param errorCode
	 * @return
	 */
	protected <T>Result<T> getResult(int errorCode, T resultEntity) {
		Result<T> result = new Result<>(errorCode);
		result.setResultMessage(this.getErrorMessage(errorCode));
		result.setResultEntity(resultEntity);
		return result;
	}
	
	
	/**
	 * Extract standard result from bean validator binding result
	 * @param result
	 * @return
	 */
	protected <T>Result<T> getBindingResult(BindingResult result) {
		List<ObjectError> errors = result.getAllErrors();
		//Assume operation is successful if no error
		if (errors == null || errors.size() <= 0) {
			return this.getResult(WebError.NO_ERROR);
		}
		ObjectError error = errors.get(0);
		String message = error.getDefaultMessage();
		if (StringUtils.isEmpty(message)) {
			return this.getResult(WebError.UNKNOWN_ERROR);
		}
		if (message.startsWith(WebError.KEY_PREFIX)) {
			String errorCodeStr = message.substring(WebError.KEY_PREFIX.length());
			int errorCode = 0;
			try {
			    errorCode = Integer.parseInt(errorCodeStr);
			    return this.getResult(errorCode, this.getMessage(message));
			}
			catch (Exception e) {
				return this.getResult(WebError.UNKNOWN_ERROR, this.getMessage(message));
			}
		}
		else {
			return this.getResult(WebError.UNKNOWN_ERROR, this.getMessage(message));
		}
	}
	
	/**
	 * Extract standard result object into request attribute
	 * @param request
	 * @param bindingResult
	 */
	protected <T> void setBindingResultAttribute(HttpServletRequest request, BindingResult bindingResult) {
		Result<T> result = this.getBindingResult(bindingResult);
		request.setAttribute(Result.RESULT_KEY, result);
	}
	
	/**
	 * Default binder
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		if (enableXssFilter()) {
	        binder.registerCustomEditor(String.class, new StringEscapeEditor());
		}
	}
	
	/**
	 * Enable xss filter or not
	 * @return
	 */
	protected boolean enableXssFilter() {
		return true;
	}
	
	/**
	 * Set pagination into request attribute
	 * @param request
	 * @param page
	 */
	protected void setPagination(HttpServletRequest request, Pagination<?> page) {
		request.setAttribute(Pagination.ATTR_KEY, page);
	}


}
