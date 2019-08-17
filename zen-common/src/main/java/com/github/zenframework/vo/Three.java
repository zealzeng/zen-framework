/*
 * Copyright (c) 2016, All rights reserved.
 */
package com.github.zenframework.vo;

import java.io.Serializable;

/**
 * @author Zeal 2016年10月19日
 */
public class Three<A,B,C> implements Serializable {

	private static final long serialVersionUID = 7223091958198298510L;
	
	private A first = null;
	
	private B second = null;
	
	private C third = null;
	
	public Three() {
	}
	
	public Three(A a, B b, C c) {
		this.first = a;
		this.second = b;
		this.third = c;
	}
	

	/**
	 * @return the first
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(A first) {
		this.first = first;
	}

	/**
	 * @return the second
	 */
	public B getSecond() {
		return second;
	}

	/**
	 * @param second the second to set
	 */
	public void setSecond(B second) {
		this.second = second;
	}

	/**
	 * @return the third
	 */
	public C getThird() {
		return third;
	}

	/**
	 * @param third the third to set
	 */
	public void setThird(C third) {
		this.third = third;
	}

}
