/*
 * Copyright (c) 2016, All rights reserved.
 */
package com.github.zealzeng.zenframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copying won't through exception while property class type is not matched
 * @author Zeal 2016年5月12日
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
	
	/**
	 * Clone list
	 * @param sources
	 * @return
	 * @throws Exception
	 */
	public static <T extends Serializable> List<T> deepClone(List<T> sources) throws Exception {
	    if (sources == null) {
	    	return sources;
	    }
	    List<T> targets = new ArrayList<>(sources.size());
	    for (T source : sources) {
	    	targets.add(deepClone(source));
	    }
	    return targets;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deepClone(T source) throws Exception {
		
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		    ObjectOutputStream out = new ObjectOutputStream(bos);) {
		    
			out.writeObject(source);
		    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
		        return (T) in.readObject();
		    }
		}
		
	}

}
