/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Zeal 2016年4月26日
 */
public class JSONUtils {

	protected static ObjectMapper objectMapper = new ObjectMapper();

	static {
		// 如果存在未知属性，则忽略不报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许key没有双引号
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许key有单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许整数以0开头
        objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        // 允许字符串中存在回车换行控制符
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	}

	public static String toJSONString(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to call toJSONString, " + e.toString(), e);
		}
	}

	public static <T>T parseObject(String text, Class<T> clazz) {
		try {
			return objectMapper.readValue(text, clazz);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parseObject, " + e.toString(), e);
		}
	}

	public static JSONObject parseObject(String text) {
		try {
			Map<String,Object> map = objectMapper.readValue(text, Map.class);
			return new JSONObject(map);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parseObject, " + e.toString(), e);
		}

	}

	public static<T>List<T>  parseArray(String text, Class<T> clazz) {
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
		try {
			return objectMapper.readValue(text, javaType);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parseArray, " + e.toString(), e);
		}
	}

	public static JSONArray parseArray(String text) {
		try {
			List<Object> list = objectMapper.readValue(text, List.class);
			return new JSONArray(list);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parseArray, " + e.toString(), e);
		}
	}

	public static void main(String[] args) throws Exception {
		String value = "{a:1, b:[{b1:1},{b2:2},{b3:3}],c:5}";
		Map<String,Object> map = parseObject(value, Map.class);
		System.out.println(((List)map.get("b")).get(0).getClass());

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("a1", 1);
		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		jsonObject.put("a2", list);
		System.out.println(JSONUtils.toJSONString(jsonObject));
		JSONArray jsonArray = new JSONArray();
		jsonArray.add("z1");
		jsonArray.add(list);
		System.out.println(JSONUtils.toJSONString(jsonArray));
	}

}
