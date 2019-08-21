package org.zenframework.security.tag;

import org.zenframework.security.AuthInfo;
import org.zenframework.security.util.AuthUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Created by Zeal on 2019/5/6 0006.
 */
public class AuthenticatedTag extends TagSupport {

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        AuthInfo authInfo = AuthUtils.getAuthInfo(request);
        if (authInfo == null) {
            return SKIP_BODY;
        }
        else {
            return EVAL_BODY_INCLUDE;
        }
    }

}
