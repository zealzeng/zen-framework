/**
 * 
 */
package org.zenframework.web.util;

//import org.jsoup.Jsoup;
//import org.jsoup.safety.Whitelist;
//import org.zenframework.util.StringUtils;
//import org.owasp.validator.html.AntiSamy;
//import org.owasp.validator.html.CleanResults;
//import org.owasp.validator.html.Policy;

//import java.io.InputStream;

/**
 * 默认使用jsoup处理
 * @author Zeal
 */
public class HtmlUtils {

	private static final HtmlHelper htmlHelper = new JsoupHtmlHelper();
	
//	private static AntiSamy antiSamy = null;
//
//	static {
//		try (InputStream in = HtmlUtils.class.getResourceAsStream("/antisamy-whlylc.xml") ) {
//		    Policy antisamyPolicy = Policy.getInstance(in);
//		    antiSamy = new AntiSamy(antisamyPolicy);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
	
//	/**
//	 * Get safe html
//	 * @param html
//	 * @return
//	 */
//	public static String getSafeHtml(String html) {
//		if (StringUtils.isEmpty(html)) {
//			return html;
//		}
//		try {
//		    CleanResults result = antiSamy.scan(html);
//		    return result.getCleanHTML();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			return html;
//		}
//	}

	public static String cleanHtml(String html) {
		return htmlHelper.cleanHtml(html);
	}

	/**
     * @param html
     * @return
	 * @deprecated
     */
    public static String getText(String html) {
    	return htmlHelper.getText(html);
    	//代码暂时别删除
    	/**
        if (StringUtils.isEmpty(html)) {
            return html;
        }
        return html.replaceAll("</?[^>]+>", "");
		 */
    }

	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
    	//System.out.println(getSafeHtml("<div onload=\"test();\">aaa<script>alert('hello');</script></div>"));
    	//System.out.println(getSafeHtml("<div><pre>import java.lang.String;</pre>/div>"));

	}

}
