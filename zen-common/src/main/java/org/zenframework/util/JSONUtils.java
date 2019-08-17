/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * @author Zeal 2016年4月26日
 */
public class JSONUtils extends JSON {
	
    static {
    	defaultSettings();
    }
    
    /**
     * Default json settings
     */
    public static void defaultSettings() {
    	JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        //JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteDateUseDateFormat.getMask();
    }
	
	/**
	 * Covert bean to json string by class property filter 
	 * @param bean
	 * @param filterClass the bean class or bean's property class
	 * @return
	 */
	public static String toJSONString(Object bean, Class<?> filterClass, String...includedProperties) {
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter(filterClass, includedProperties);
		return JSON.toJSONString(bean, filter, new SerializerFeature[0]);
	}
	
	/**
	 * Covert bean to json string by mulitple classes include/exclude property filter
	 * @param bean
	 * @param classIncludeProps
	 * @param classExcludeProps
	 * @return
	 */
	public static String toJSONString(Object bean, Map<Class<?>, String[]> classIncludeProps, Map<Class<?>, String[]> classExcludeProps) {
		ComplexPropertyPreFilter filter = new ComplexPropertyPreFilter();
		if (classIncludeProps != null && classIncludeProps.size() > 0) {
			filter.setIncludes(classIncludeProps);
		}
		if (classExcludeProps != null && classExcludeProps.size() > 0) {
			filter.setExcludes(classExcludeProps);
		}
		return JSON.toJSONString(bean, filter, new SerializerFeature[0]);
	}
	
    public static void main(String[] args) throws Exception {
    	String str = "{'replace':function(){alert(/xss/);}}";
    	JSONObject map = new JSONObject();
    	map.put("info", str);
    	System.out.println(JSONUtils.toJSONString(map));
    }

}
