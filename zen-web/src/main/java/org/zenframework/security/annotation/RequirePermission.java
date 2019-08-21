package org.zenframework.security.annotation;

import java.lang.annotation.*;

/**
 * User with specified permissions can access the URL
 * Created by Zeal on 2019/1/10 0010.
 */
@Target( {ElementType.METHOD, ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * Permission values
     * @return
     */
    String[] value();

    /**
     * Logical relationship for these roles
     * @return
     */
    Logical logical() default Logical.AND;
}
