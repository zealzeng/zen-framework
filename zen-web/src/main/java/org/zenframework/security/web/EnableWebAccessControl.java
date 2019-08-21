/*
 * Copyright (c) 2015, All rights reserved.
 */
package org.zenframework.security.web;

import java.lang.annotation.*;

/**
 * Identify controller will use which access control
 * @author Zeal
 */
@Target( {ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableWebAccessControl {
}
