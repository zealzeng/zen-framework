package org.zenframework.security.annotation;

import java.lang.annotation.*;

/**
 * Identify the ctrl method is login/authentication request or not
 * Created by Zeal on 2019/1/10 0010.
 */
@Target( {ElementType.METHOD} )
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthenticationRequest {

//    /**
//     * Let handler method handle auth result or not, if it's true, AccessControl onAuthenticateSuccess or onAuthenticateFailure won't be called
//     * @return
//     */
//    boolean handleResult() default false;

//    /**
//     * When authenticating failure, forward,redirect or include to specified url.
//     * Format: forward:xxx, or redirect:xxx or include:xxx, others are redirection
//     * @return
//     */
//    String failureUrl() default "";


}
