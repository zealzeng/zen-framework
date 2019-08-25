package org.zenframework.web.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * @author Zeal
 */
public class JsoupHtmlHelper implements HtmlHelper {

    private Whitelist whitelist = null;

    public JsoupHtmlHelper() {
        whitelist = Whitelist.relaxed();
        //FIXME 没antisamy的模板那么丰富,后面想办法迁移下
        whitelist.addTags("embed", "object", "param", "div");
        whitelist.addAttributes(":all", "style", "class", "id", "name");
        whitelist.addAttributes("object", "width", "height", "classid", "codebase");
        whitelist.addAttributes("param", "name", "value");
        whitelist.addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen", "allowScriptAccess", "flashvars", "name", "type", "pluginspage");
    }

    @Override
    public String cleanHtml(String html) {
        return Jsoup.clean(html, whitelist);
    }

    @Override
    public String getText(String html) {
        return Jsoup.parse(html).text();
    }
}
