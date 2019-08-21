package org.zenframework.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * We can move most of the web.xml configurations to here, but it's required to support servlet3.0 or above
 * Created by Zeal on 2019/1/12 0012.
 */
public abstract class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {


	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		customizeRegistration(servletContext);
	}

	/**
	 * Other registration
	 * @param servletContext
	 */
	protected void customizeRegistration(ServletContext servletContext) {
	}


    /**
     * Dispatch servlet mapping URI
     * @return
     */
	@Override
    protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers() {
		return new ApplicationContextInitializer[] { getAppConfigInitializer() };
	}

    /**
     * We need a root application configuration initializer
     * @return
     */
	protected abstract AppConfigInitializer getAppConfigInitializer();

    /**
     * By default we only support CharacterEncodingFilter
     * @return
     */
    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] { getCharacterEncodingFilter() };
    }

    protected Filter getCharacterEncodingFilter() {
		CharacterEncodingFilter charFilter = new CharacterEncodingFilter();
		charFilter.setEncoding("UTF-8");
		charFilter.setForceEncoding(true);
		return charFilter;
	}

	private EnumSet<DispatcherType> getDispatcherTypes() {
		return (isAsyncSupported() ?
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) :
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE));
	}

	protected void registerFilterForUrlPatterns(ServletContext context, String filterName, Class<? extends Filter> filterClass, String[] urlMappings) {
    	FilterRegistration.Dynamic filter = context.addFilter(filterName, filterClass);
    	filter.addMappingForUrlPatterns(getDispatcherTypes(), false, urlMappings);
	}

	protected void registerDelegatingFilterProxy(ServletContext context, String filterName, boolean targetFilterLifecycle,  String[] urlMappings) {
    	FilterRegistration.Dynamic filter = context.addFilter(filterName, DelegatingFilterProxy.class);
    	if (targetFilterLifecycle) {
			filter.setInitParameter("targetFilterLifecycle", "true");
		}
		filter.addMappingForUrlPatterns(getDispatcherTypes(), false, urlMappings);
	}
}
