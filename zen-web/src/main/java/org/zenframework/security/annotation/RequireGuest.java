package org.zenframework.security.annotation;

import java.lang.annotation.*;

/**
 * Guest can access the URL
 * Created by Zeal on 2019/1/10 0010.
 */
@Target( {ElementType.METHOD, ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireGuest {

}
