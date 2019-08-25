/*
 * Copyright (c) 2015, All rights reserved.
 */
package org.zenframework.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UrlPathHelper;

import org.zenframework.util.IOUtils;
import org.zenframework.util.JSONUtils;
import org.zenframework.util.NumberUtils;
import org.zenframework.util.StringEscapeUtils;
import org.zenframework.util.StringUtils;
import org.zenframework.common.Result;
import org.zenframework.web.vo.HttpFile;

/**
 * Web utility 
 * @author Zeal
 * @since 2015年12月24日
 */
public class WebUtils extends org.springframework.web.util.WebUtils {
	
	/** URL helper */
	private static UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	
	/**
	 *  Check whether request is from weixin/wechat internal browser or not
	 * @param request
	 * @return
	 */
	public static boolean isWechatBrowser(HttpServletRequest request) {
		String agent = request.getHeader("USER-AGENT");
		if (agent == null) {
			return false;
		}
		agent = agent.toLowerCase();
		return (-1 != agent.indexOf("micromessenger"));
	}
	
	/**
	 * 判断是否是IE请求
	 * @param request
	 * @return
	 */
	public static boolean isIEBrowser(HttpServletRequest request) {
		String agent = request.getHeader("USER-AGENT"); 
		if (agent == null) {
			return false;
		}
		agent = agent.toUpperCase();
		return (-1 != agent.indexOf("MSIE")); 
	}
	
	
	/**
	 * Check whether it's ajax request, actually it only supports jquery
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")); 
	}
	
	/**
	 * Get request URL
	 * @param request
	 * @param includeQueryString
	 * @return
	 */
	public static String getRequestURL(HttpServletRequest request, boolean includeQueryString) {
	    StringBuffer sb = request.getRequestURL();
        if (includeQueryString) {
            String queryString = request.getQueryString();
            if (StringUtils.isNotEmpty(queryString)) {
                sb.append('?').append(queryString);
            }
        }
        return sb.toString();
    }
	
	/**
	 * Get request path, concat the request path with server ip,port,jsessionid..
	 * @param request
	 * @param requestPath
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request, String requestPath) {
		return getRequestPath(request, requestPath, false, false);
	}
	
	/**
	 * Get request path, concat the request path with server ip,port,jsessionid..
	 * @param request
	 * @param requestPath
	 * @param completedURL
	 * @param withSessionId
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request,
		String requestPath, boolean completedURL, boolean withSessionId) {
		
		String contextPath = request.getContextPath();
		String serverPrefix = "";
		if (completedURL) {
			serverPrefix = request.getScheme() + "://" + request.getServerName();
			if ("http".equalsIgnoreCase(request.getScheme()) && request.getServerPort() == 80) {
			} 
			else if ("https".equalsIgnoreCase(request.getScheme())
					&& request.getServerPort() == 843) {
			} 
			else {
				serverPrefix += (":" + request.getServerPort());
			}
		}
		String url = null;
		if (requestPath == null || requestPath.length() <= 0) {
			url = serverPrefix + contextPath;
		} 
		else if (requestPath.charAt(0) == '/') {
			url = serverPrefix + contextPath + requestPath;
		} 
		else {
			url = serverPrefix + contextPath + '/' + requestPath;
		}

		if (withSessionId) {
			url += (";jsessionid=" + request.getSession(true).getId());
		}
		return url;
	}
	
	/**
	 * 获取远程用户IP
	 * @param request
	 * @return
	 */
	public static String getRemoteClientIp(HttpServletRequest request) {
		String [] ipHeaders = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
		String ip = null;
		for (int i = 0; i < ipHeaders.length;) {
			ip = request.getHeader(ipHeaders[i]);
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				++i;
				continue;
			}
			else {
				break;
			}
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			return request.getRemoteAddr();
		}
		else {
			int index = ip.indexOf(',');
			if (index > 0) {
				String[] values = StringUtils.split(ip, ',');
				return values[0].trim();
			}
			else {
			    return ip;
			}
		}
	}
	
	/**
	 * Get request parameter and escape to defense xss
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getRequestParameter(HttpServletRequest request, String name) {
		return getRequestParameter(request, name, true, false);
	}
	
	/**
	 * Get request parameter and escape to defense xss
	 * @param request
	 * @param name
	 * @param escapeHtml
	 * @return
	 */
	public static String getRequestParameter(HttpServletRequest request, String name, boolean escapeHtml) {
		return getRequestParameter(request, name, escapeHtml, false);
	}
	
	/**
	 * Get request parameters and escape html
	 * @param request
	 * @param name
	 * @param escapeHtml
	 * @return
	 * @deprecated
	 */
	public static String[] getRequestParameters(HttpServletRequest request, String name) {
		return getRequestParameterValues(request, name, true, false);
	}
	
	/**
	 * Get request parameters and escape html
	 * @param request
	 * @param name
	 * @param escapeHtml
	 * @return
	 * @deprecated
	 */
	public static String[] getRequestParameters(HttpServletRequest request, String name, boolean escapeHtml) {
		return getRequestParameterValues(request, name, escapeHtml, false);
	}
	
	/**
	 * Get request parameter map and escape html
	 * @param request
	 * @param name
	 * @param escapeHtml
	 * @return
	 */
	public static Map<String,String[]> getRequestParameterMap(HttpServletRequest request, String name) {
		return getRequestParameterMap(request, name, true);
	}
	
	/**
	 * Get request parameter map and escape html
	 * @param request
	 * @param name
	 * @param escapeHtml
	 * @return
	 */
	public static Map<String,String[]> getRequestParameterMap(HttpServletRequest request, String name, boolean escapeHtml) {
		Map<String,String[]> map = request.getParameterMap();
		if (map == null || map.size() <= 0 || !escapeHtml) {
			return map;
		}
		Iterator<Map.Entry<String, String[]>> entryIter = map.entrySet().iterator();
		while (entryIter.hasNext()) {
			Map.Entry<String, String[]> entry = entryIter.next();
			String[] values = entry.getValue();
			if (values == null || values.length <= 0) {
				continue;
			}
			for (int i = 0; i < values.length; ++i) {
				if (StringUtils.isNotEmpty(values[i])) {
					values[i] = StringEscapeUtils.escapeHtml4(values[i]);
				}
			}
		}
		return map;
	}
	
	/**
	 * Get request map that each parameter has one value
	 * @param request
	 * @param escapeHtml
	 * @return
	 */
	public static Map<String,String> getRequestParameterSingleMap(HttpServletRequest request, boolean escapeHtml) {
		Map<String,String[]> map = request.getParameterMap();
		if (map == null || map.size() <= 0 || !escapeHtml) {
			return new HashMap<>(0);
		}
		Map<String, String> resultMap = new LinkedHashMap<>(map.size());
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String paramName = en.nextElement();
			String value = request.getParameter(paramName);
			if (value != null && escapeHtml) {
				value = StringEscapeUtils.escapeHtml4(value);
			}
			resultMap.put(paramName, value);
		}
		return resultMap;
	}
	
	/**
	 * Get requst uri and trim ;
	 * @param request
	 * @return
	 */
	public static String getRequestURI(HttpServletRequest request) {
		return urlPathHelper.getRequestUri(request);
	}
	
	/**
	 * Set request attribute
	 * @param request
	 * @param attrMap
	 */
	public static void setRequestAttribute(HttpServletRequest request, Map<String, Object> attrMap) {
		if (attrMap == null || attrMap.size() <= 0) {
			return;
		}
		Iterator<Map.Entry<String, Object>> iter = attrMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Object> entry = iter.next();
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}
	
	public static void jsonResult(HttpServletResponse response, int resultCode, String resultMessage,
		Object resultEntity) throws IOException {
		String json = jsonResult(resultCode, resultMessage, resultEntity);
		response.getWriter().write(json);
		response.getWriter().flush();
	}
	
	public static void jsonResult(HttpServletResponse response, Result<?> result) throws IOException {
		String json = JSONUtils.toJSONString(result);
		response.getWriter().write(json);
		response.getWriter().flush();
	}

	public static String jsonResult(int resultCode, String resultMessage, Object resultEntity) {
		Result<Object> result = new Result<>();
		result.setResultCode(resultCode);
		// FIXME Inject MessageSource
		result.setResultMessage(resultMessage);
		result.setResultEntity(resultEntity);
		String json = JSONUtils.toJSONString(result);
		return json;
	}
	
	public static String jsonResult(Result<?> result) {
		String json = JSONUtils.toJSONString(result);
		return json;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getRequestHeaders(HttpServletRequest request) {
		Enumeration<String> iter = request.getHeaderNames();
		Map<String, String> values = new LinkedHashMap<String, String>();
		while (iter.hasMoreElements()) {
			String name = iter.nextElement();
			values.put(name, request.getHeader(name)); 
		}
		return values;
	}	
	
	
	/**
	 * 一般的参数应该是string->string[]
	 * 上传的时候是string, MutipartFIle
	 * @param request
	 * @return
	 */
	public static Map<String,Object> getRequestParameters(HttpServletRequest request)throws IOException {
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.putAll(request.getParameterMap());
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest mreq =(MultipartHttpServletRequest) request;
			//map.putAll(mreq.getFileMap());
			Iterator<String> iter = mreq.getFileNames();
			while (iter.hasNext()) {
				String name = iter.next();
				MultipartFile file = mreq.getFile(name);
				HttpFile hfile = new HttpFile();
				hfile.setBytes(file.getBytes());
				hfile.setContentType(file.getContentType());
				hfile.setFileName(name);
				map.put(name, hfile);
			}
		}
		return map;
	}
	
	public static boolean isPostFormRequest(HttpServletRequest request) {
		//return "application/x-www-form-urlencoded".equalsIgnoreCase(request.getContentType());
		String contentType = request.getContentType();
		return contentType != null && contentType.startsWith("application/x-www-form-urlencoded");
	}
	
	public static String getRequestBody(HttpServletRequest request) throws IOException {
		
	     if (isPostFormRequest(request)) {
	    	 return "";
	     }
		
		 InputStream in = request.getInputStream();
		 int size = request.getContentLength();
		 if (size > 0) {
			 byte[] bytes = new byte[size];
			 //in.read(bytes);
			 IOUtils.readFully(in, bytes);
			 return new String(bytes, "UTF-8");
		 }
		 
		 size = in.available();
		 if (size > 0) {
			 byte[] bytes = new byte[size];
			 //in.read(bytes);
			 IOUtils.readFully(in, bytes);
			 return new String(bytes, "UTF-8");
		 }
		 byte[] bytes = IOUtils.toByteArray(in);
		 if (bytes != null && bytes.length > 0) {
			 return new String(bytes, "UTF-8"); 
		 }
		 else {
			 return "";
		 }
		 
	}
	
	

	
	/**
	 * 转义
	 * @param request
	 * @param paramName
	 * @param htmlEscapse 基本转义就无需htmlSafe
	 * @return
	 */
	public static String getRequestParameter(HttpServletRequest request, String paramName, boolean htmlEscapse, boolean htmlSafe) {
		String value = request.getParameter(paramName);
		if (StringUtils.isNotEmpty(value)) {
			if (htmlEscapse) {
				value = StringEscapeUtils.escapeHtml4(value);
			}
			if (htmlSafe) {
				value = HtmlUtils.getSafeHtml(value);
			}
		}
		return value;
	}
	
	/**
	 * 默认转义,一般转义了非富文本就不需要无需html safe
	 * @param request
	 * @param paramName
	 * @return
	 */
	public static String[] getRequestParameterValues(HttpServletRequest request, String paramName) {
		return getRequestParameterValues(request, paramName, true, false);
	}
	
	public static String[] getRequestParameterValues(HttpServletRequest request, String paramName, boolean htmlEscapse, boolean htmlSafe) {
		String[] values = request.getParameterValues(paramName);
		if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; ++i) {
				if (StringUtils.isEmpty(values[i])) {
					continue;
				}
				if (htmlEscapse) {
				    values[i] = StringEscapeUtils.escapeHtml4(values[i]);
				}
				if (htmlSafe) {
					values[i] = HtmlUtils.getSafeHtml(values[i]);
				}
			}
		}
		return values;
	}
	
	public static int getRequestIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
		return NumberUtils.toInt(request.getParameter(paramName), defaultValue);
	}
	
	public static double getRequestDoubleParameter(HttpServletRequest request, String paramName, double defaultValue) {
		return NumberUtils.toDouble(request.getParameter(paramName), defaultValue);
	}
	
	/**
	 * NOTICE: it does not contain default int value, if param is not valid integer, it will through exception
	 * @param request
	 * @param paramName
	 * @return
	 */
	public static int[] getRequestIntParameterValues(HttpServletRequest request, String paramName) {
		String[] values = request.getParameterValues(paramName);
		if (values != null) {
			return NumberUtils.toIntArray(values);
		}
		else {
			return null;
		}
	}
	
	/**
	 * @param request
	 * @param paramName
	 * @return
	 */
	public static int[] getRequestIntParameterValues(HttpServletRequest request, String paramName, int defaultValue) {
		String[] values = request.getParameterValues(paramName);
		if (values != null) {
			return NumberUtils.toIntArray(values, defaultValue);
		}
		else {
			return null;
		}
	}
	
	public static double[] getRequestDoubleParameterValues(HttpServletRequest request, String paramName) {
		String[] values = request.getParameterValues(paramName);
		if (values != null) {
			return NumberUtils.toDoubleArray(values);
		}
		else {
			return null;
		}
	}
	
	public static double[] getRequestDoubleParameterValues(HttpServletRequest request, String paramName, double defaultValue) {
		String[] values = request.getParameterValues(paramName);
		if (values != null) {
			return NumberUtils.toDoubleArray(values, defaultValue);
		}
		else {
			return null;
		}
	}
	
	
	
	
	
	

}
