/*
 * Copyright (c) 2012,  All rights reserved.
 */
package com.github.zealzeng.zenframework.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Collection utility
 * @author Zed 2012-5-11
 */
public class CollectionUtils {
	
	/**
	 * Return {@code true} if the supplied Collection is {@code null} or empty.
	 * Otherwise, return {@code false}.
	 * @param collection the Collection to check
	 * @return whether the given Collection is empty
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}
	
	/**
	 * Covert string array to integer list
	 * @param strArray
	 * @return
	 */
	public static List<Integer> toIntList(String[] strArray) {
		if (strArray == null || strArray.length <= 0) {
			return new ArrayList<Integer>(0);
		}
		List<Integer> intArray = new ArrayList<Integer>(strArray.length);
		for (int i = 0; i < strArray.length; ++i) {
			intArray.add(Integer.parseInt(strArray[i])); 
		}
		return intArray;
	}
	
	
	/**
	 * Covert collection into map whose key is the field value of collection element
	 * @param collection
	 * @param keyFieldInBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T1,T2>Map<T1, T2> beanCollectionToMap(Collection<T2> collection, 
		String keyFieldInBean, Class<T1> keyFieldCls) {
		
		Iterator<T2> iter = collection.iterator();
		Map<T1,T2> map = new HashMap<T1, T2>(collection.size());
		try {
			while (iter.hasNext()) {
				T2 obj = iter.next();
				if (obj == null) {
					continue;
				}
				map.put((T1)PropertyUtils.getProperty(obj, keyFieldInBean), obj);
			}
		} 
		catch (IllegalAccessException e) {
			throw new java.lang.IllegalStateException(e.toString());
		} 
		catch (InvocationTargetException e) {
			throw new java.lang.IllegalStateException(e.toString());
		} 
		catch (NoSuchMethodException e) {
			throw new java.lang.IllegalStateException(e.toString());
		}
		return map;
	}
	
	/**
	 * Covert collection into map, collection element can contain same field value
	 * @param collection
	 * @param keyFieldInBean The value of the bean must be unique.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T1,T2>Map<T1, List<T2>> beanCollectionToListMap(
		Collection<T2> collection, String keyFieldInBean, Class<T1> keyFieldCls) {
		
		Iterator<T2> iter = collection.iterator();
		Map<T1,List<T2>> map = new HashMap<T1, List<T2>>(collection.size());
		try {
			while (iter.hasNext()) {
				T2 obj = iter.next();
				if (obj == null) {
					continue;
				}
				//map.put((T1)PropertyUtils.getProperty(obj, keyFieldInBean), obj);
				T1 key = (T1)PropertyUtils.getProperty(obj, keyFieldInBean);
				List<T2> values = map.get(key);
				if (values == null) {
					values = new ArrayList<T2>();
					map.put(key, values);
				}
				values.add(obj);
			}
		} 
		catch (IllegalAccessException e) {
			throw new java.lang.IllegalStateException(e.toString());
		} 
		catch (InvocationTargetException e) {
			throw new java.lang.IllegalStateException(e.toString());
		} 
		catch (NoSuchMethodException e) {
			throw new java.lang.IllegalStateException(e.toString());
		}
		return map;
	}
	
	/**
	 * Covert collection into map, collection element can contain same field value, 
	 * the key in the map is integer
	 * @param collection
	 * @param keyFieldInBean The value of the bean must be unique.
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static <T2>Map<Integer, List<T2>> beanCollectionToIntListMap(
		Collection<T2> collection, String keyFieldInBean) { 
		return beanCollectionToListMap(collection, keyFieldInBean, Integer.class);
	}
	
	/**
	 * Covert collection into map, key in the map is integer and key is unique
	 * @param collection
	 * @param keyFieldInBean The value of the bean must be unique.
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static <T2>Map<Integer, T2> beanCollectionToIntMap(
		Collection<T2> collection, String keyFieldInBean) { 
		return beanCollectionToMap(collection, keyFieldInBean, Integer.class);
	}
	
	/**
	 * Covert collection into map, collection element can contain same value, 
	 * the key in the map is string type
	 * @param collection
	 * @param keyFieldInBean The value of the bean must be unique.
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static <T2>Map<String, List<T2>> beanCollectionToStringListMap(
		Collection<T2> collection, String keyFieldInBean) { 
		return beanCollectionToListMap(collection, keyFieldInBean, String.class);
	}
	
	/**
	 * Covert collection into map, the key in the map is string type
	 * @param collection
	 * @param keyFieldInBean The value of the bean must be unique.
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static <T2>Map<String, T2> beanCollectionToStringMap(
		Collection<T2> collection, String keyFieldInBean) { 
		return beanCollectionToMap(collection, keyFieldInBean, String.class);
	}	
	
	/**
	 * Add object array into collection
	 * @param collection
	 * @param array
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addAll(Collection collection, Object[] array) {
		if (collection == null || array == null || array.length <= 0) {
			return;
		}
		for (Object object: array) {
			collection.add(object);
		}
	}

}
