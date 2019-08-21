/*
 * Copyright (c) 2017, All rights reserved.
 */
package org.zenframework.cache;

import org.zenframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Support copy on read/write
 * @author Zeal
 * @since 2017年6月30日
 */
public class ConcurrentMapCache extends org.springframework.cache.concurrent.ConcurrentMapCache {
	
	private boolean copyOnRead = false;
	
	private boolean copyOnWrite = false;
	
	public ConcurrentMapCache(String name) {
		this(name, true, false, false);
	}
	
	public ConcurrentMapCache(String name, boolean allowNullValues, boolean copyOnRead, boolean copyOnWrite) {
		super(name, new ConcurrentHashMap<>(256), allowNullValues);
		this.copyOnRead = copyOnRead;
		this.copyOnWrite = copyOnWrite;
	}
	
	@Override
	public void put(Object key, Object value) {
		super.put(key, copyValueOnWrite(value));
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return super.putIfAbsent(key, copyValueOnWrite(value));
	}
	
	private Object copyValueOnWrite(Object value) {
		if (copyOnWrite && value != null) {
			if (value instanceof Serializable) {
			    value = SerializationUtils.clone((Serializable) value);
			}
			else {
				throw new IllegalArgumentException("value is not serializable");
			}
		}
		return value;
	}
	
	@Override
	public ValueWrapper get(Object key) {
		Object value = lookup(key);
		return toValueWrapper(copyValueOnRead(value));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object value = fromStoreValue(lookup(key));
		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) copyValueOnRead(value);
	}

	private Object copyValueOnRead(Object value) {
		if (copyOnRead && value != null) {
			if (value instanceof Serializable) {
			    value = SerializationUtils.clone((Serializable) value);
			}
			else {
				throw new IllegalArgumentException("value is not serializable");
			}
		}
		return value;
	}

	
	

}
