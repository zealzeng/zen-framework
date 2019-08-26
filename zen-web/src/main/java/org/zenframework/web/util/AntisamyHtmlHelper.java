package org.zenframework.web.util;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.zenframework.util.StringUtils;

import java.io.InputStream;

/**
 * maven外部需要手工导入antisamy的依赖,这个库依赖有点多
 * @author Zeal
 */
public class AntisamyHtmlHelper implements HtmlHelper {

    private AntiSamy antiSamy = null;

    public AntisamyHtmlHelper() throws Exception {
        try (InputStream in = HtmlUtils.class.getResourceAsStream("/antisamy-zenframework.xml")) {
            Policy antisamyPolicy = Policy.getInstance(in);
            antiSamy = new AntiSamy(antisamyPolicy);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String cleanHtml(String html) {
		if (StringUtils.isEmpty(html)) {
			return html;
		}
		try {
		    CleanResults result = antiSamy.scan(html);
		    return result.getCleanHTML();
		}
		catch (Exception e) {
			e.printStackTrace();
			return html;
		}
    }

    /**
     * 不严谨的,暂时没找到提取文本的接口
     * @param html
     * @return
     */
    @Override
    public String getText(String html) {
        if (StringUtils.isEmpty(html)) {
            return html;
        }
        else {
            return html.replaceAll("</?[^>]+>", "");
        }
    }
}
