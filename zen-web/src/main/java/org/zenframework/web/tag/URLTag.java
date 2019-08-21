//package org.zenframework.web.tag;
//
//import org.zenframework.config.Environment;
//import org.zenframework.core.util.StringUtils;
//import org.zenframework.core.util.URLUtils;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.servlet.support.RequestContextUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.jsp.tagext.TagSupport;
//
///**
// * URL utility tag
// * @author Zeal
// * @deprecated
// */
//public class URLTag extends TagSupport {
//
//	private static final long serialVersionUID = -2641420884358770843L;
//
//	private String DEFAULT_APP_VERSION = String.valueOf(System.currentTimeMillis());
//
//	protected String value;
//
//	protected boolean withSessionId = false;
//
//	//Get the version from environment configuration
//	protected boolean withVersion = false;
//
//	public String getValue() {
//		return value;
//	}
//
//	public void setValue(String value) {
//		this.value = value;
//	}
//
//	/**
//     * Output URL
//     */
//    public int doEndTag() {
//        try {
//        	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
//	        String contextPath = request.getContextPath();
//	        StringBuilder url = new StringBuilder();
//	        if (contextPath.length() == 1 && contextPath.charAt(0) == '/') {
//	        	contextPath = "";
//	        }
//	        if (this.value.charAt(0) == '/') {
//	        	url.append(contextPath).append(this.value);
//	        }
//	        else {
//	        	url.append(contextPath).append('/').append(this.value);
//	        }
//
//	        //Append app version
//	        if (this.withVersion) {
//	        	Environment env = this.getEnvironment();
//	        	if (env != null) {
//	        		this.appendParameter(url, "v", this.getAppVersion(env));
//				}
//			}
//
//	        //Append jsessionid
//	        if (this.withSessionId) {
//	        	url.append(";jsessionid=").append(request.getSession(true).getId());
//	        }
//	        pageContext.getOut().print(url.toString());
//        }
//        catch (Exception e) {
//        	e.printStackTrace();
//        }
//        return EVAL_PAGE;
//    }
//
//	/**
//	 * Get app version
//	 * @param env
//	 * @return
//	 */
//	private String getAppVersion(Environment env) {
//    	String appVersion = env.getAppVersion();
//    	if (StringUtils.isEmpty(appVersion)) {
//    		return DEFAULT_APP_VERSION;
//		}
//		else {
//    		return appVersion;
//		}
//	}
//
//	/**
//	 * Append url parameter
//	 * @param url
//	 * @param paramName
//	 * @param paramValue
//	 */
//    private void appendParameter(StringBuilder url, String paramName, String paramValue) {
//    	int index = url.indexOf("?");
//    	if (index == -1) {
//    		url.append('?').append(paramName).append('=').append(URLUtils.urlEncode(paramValue));
//		}
//		else {
//    		url.append('&').append(paramName).append('=').append(URLUtils.urlEncode(paramValue));
//		}
//	}
//
//	/**
//	 * Get environment from application context
//	 * @return
//	 */
//	private Environment getEnvironment() {
//		WebApplicationContext appCtx = RequestContextUtils.findWebApplicationContext((HttpServletRequest) this.pageContext.getRequest());
//		if (appCtx != null) {
//			return appCtx.getBean(Environment.class);
//		}
//		else {
//			return null;
//		}
//	}
//
//	/**
//	 * @return the withSessionId
//	 */
//	public boolean isWithSessionId() {
//		return withSessionId;
//	}
//
//	/**
//	 * @param withSessionId the withSessionId to set
//	 */
//	public void setWithSessionId(boolean withSessionId) {
//		this.withSessionId = withSessionId;
//	}
//
//	/**
//	 * @return
//	 */
//	public boolean isWithVersion() {
//		return withVersion;
//	}
//
//	/**
//	 * @param withVersion
//	 */
//	public void setWithVersion(boolean withVersion) {
//		this.withVersion = withVersion;
//	}
//}
//
