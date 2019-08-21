package org.zenframework.shiro.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by Zeal on 2019/1/13 0013.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ShiroConfiguration.class)
public @interface EnableShiro {
}
