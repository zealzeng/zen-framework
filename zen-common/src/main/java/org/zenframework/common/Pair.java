/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.common;

import java.io.Serializable;

/**
 * @author Zeal 2016年8月13日
 */
public class Pair<K,V> implements Serializable {

	private static final long serialVersionUID = 8229362343776612199L;
	private K key = null;
	private V value = null;
	
	public Pair() {
	}
	
	public Pair(K k, V v) {
		this.key = k;
		this.value = v;
	}

	/**
	 * @return the key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}
}
