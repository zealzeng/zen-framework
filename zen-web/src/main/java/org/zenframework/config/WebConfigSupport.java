/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.config;

//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
//import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;

//import javax.xml.transform.Source;
//import java.nio.charset.Charset;
import java.util.List;

/**
 * Provide some fault settings for spring mvc
 * @author Zeal 2016年4月27日
 */
public class WebConfigSupport implements WebMvcConfigurer {

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
	}

	/**
	 * Content negotiation
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.ignoreAcceptHeader(true);
		configurer.defaultContentType(MediaType.TEXT_HTML);
		//<property name="favorParameter" value="false"/>
		//<!-- 用于开启 /userinfo/123?format=json 的支持 -->.parameterName("format")
		configurer.favorParameter(false);
		//这里是是否启用扩展名支持，默认就是true,例如  /user/{userid}.json
		configurer.favorPathExtension(true).useJaf(false);
		//index.json, index.xml
		configurer.mediaType("xml", MediaType.APPLICATION_XML);
		configurer.mediaType("json", MediaType.APPLICATION_JSON);
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

	}

	/**
	 * Message converters
	 */
	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
//		//Default converts copied from WebMvcConfigurationSupport.java
//		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
//		stringConverter.setWriteAcceptCharset(false);
//		messageConverters.add(new ByteArrayHttpMessageConverter());
//		messageConverters.add(stringConverter);
//		messageConverters.add(new ResourceHttpMessageConverter());
//		messageConverters.add(new SourceHttpMessageConverter());
//		//messageConverters.add(new AllEncompassingFormHttpMessageConverter());
//		messageConverters.add(new FormHttpMessageConverter());
//		//Only support fast json right now
//		//FastJsonHttpMessageConverter json = new FastJsonHttpMessageConverter();
//		FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
//		fastJsonConverter.getFastJsonConfig().setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
//		messageConverters.add(fastJsonConverter);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	@Override
	public Validator getValidator() {
		return null;
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		return null;
	}

	/**
	 * Use jsp default servlet to handle static resources
	 */
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

	@Override
	public void addFormatters(FormatterRegistry registry) {
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	}

	/**
	 * By default we use messages.properties, override it when it's necessary
	 * @return
	 */
	@Bean(name="messageSource")
	public ResourceBundleMessageSource resourceBundleMessageSource() {
		ResourceBundleMessageSource r = new ResourceBundleMessageSource();
		r.setDefaultEncoding("UTF-8");
		r.setBasename("messages");
		return r;
	}
	
	
	/**
	 * {@inheritDoc}
	 * <p>This implementation is empty.
	 */
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/jsp/", ".jsp");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}


}
