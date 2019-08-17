/*
 * Copyright (c) 2016, All rights reserved.
 */
package com.github.zealzeng.zenframework.util;

/**
 * @author Zeal 2016年5月19日
 */
public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {
	
	/**
	 * Same values and ignore the order
	 * @param srcArray
	 * @param targetArray
	 * @return
	 */
	public static boolean isValueEqual(int[] srcArray, int[] targetArray) {
		if (srcArray == null || targetArray == null || srcArray.length != targetArray.length) {
			return false;
		}
		for (int src : srcArray) {
			boolean found = false;
		    for (int target : targetArray) {
		    	if (src == target) {
		    		found = true;
		    		break;
		    	}
		    }
		    if (!found) {
		    	return false;
		    }
		}
		return true;
	}

}
