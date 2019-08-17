/*
 * Copyright (c) 2017, All rights reserved.
 */
package com.github.zenframework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Zeal 2017年5月26日
 */
public class MethodUtils extends org.apache.commons.lang3.reflect.MethodUtils {

    /**
     * Get first declared method by method name
     *
     * @param clazz
     * @param methodName
     * @return
     * @deprecated Use getMatchingMethod instead
     */
    public static Method getDeclaredMethodByName(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods == null || methods.length <= 0) {
            return null;
        }
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    /**
     * @param cls
     * @param methodName
     * @param args
     * @param parameterTypes
     * @param forceAccess
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokeStaticMethod(boolean forceAccess, final Class<?> cls, final String methodName,
        Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        if (!forceAccess) {
            return invokeStaticMethod(cls, methodName, args, parameterTypes);
        }
        else {
            Method method = getMatchingMethod(cls, methodName, parameterTypes);
            if (method == null) {
                throw  new NoSuchMethodException(methodName);
            }
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(null, args);
        }

    }


}
