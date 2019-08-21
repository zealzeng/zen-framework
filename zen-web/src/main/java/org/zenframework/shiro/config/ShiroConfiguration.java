package org.zenframework.shiro.config;

import org.zenframework.shiro.filter.WebAuthenticationFilter;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by Zeal on 2019/1/11 0011.
 */
@Import({
        ShiroBeanConfiguration.class,
        ShiroAnnotationProcessorConfiguration.class,
        ShiroWebConfiguration.class,
        ShiroWebFilterConfiguration.class
})
@Configuration
public class ShiroConfiguration {

    @Value("#{ @environment['shiro.filter.webAuthc.loginUrl'] ?: '/login' }")
    protected String webAuthcFilterLoginUrl;

    @Value("#{ @environment['shiro.filter.webAuthc.successUrl'] ?: '/' }")
    protected String webAuthcFilterSuccessUrl;

    @Bean(name = WebAuthenticationFilter.FILTER_NAME)
    public WebAuthenticationFilter webAuthenticationFilter() {
        WebAuthenticationFilter filter = new WebAuthenticationFilter();
        if (this.webAuthcFilterLoginUrl != null) {
            filter.setLoginUrl(this.webAuthcFilterLoginUrl);
        }
        if (this.webAuthcFilterSuccessUrl != null) {
            filter.setSuccessUrl(webAuthcFilterSuccessUrl);
        }
        return filter;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/favicon.ico", DefaultFilter.anon.name());
        chainDefinition.addPathDefinition("/vendor/**", DefaultFilter.anon.name());
        chainDefinition.addPathDefinition("/static/**", DefaultFilter.anon.name());
        chainDefinition.addPathDefinition("/js/**", DefaultFilter.anon.name());
        chainDefinition.addPathDefinition("/css/**", DefaultFilter.anon.name());
        chainDefinition.addPathDefinition("/images/**", DefaultFilter.anon.name());
        chainDefinition.addPathDefinition("/icons/**", DefaultFilter.anon.name());
        chainDefinition.addPathDefinition("/", DefaultFilter.anon.name());
        //All the others, refer to DefaultFilter enum
        chainDefinition.addPathDefinition("/**", WebAuthenticationFilter.FILTER_NAME);
        return chainDefinition;
    }

}
