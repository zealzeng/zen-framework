package org.zenframework.security.tag;

import org.zenframework.util.StringUtils;
import org.zenframework.security.AuthInfo;
import org.zenframework.security.annotation.Logical;
import org.zenframework.security.util.AuthUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Set;

/**
 * Created by Zeal on 2019/5/6 0006.
 */
public class AuthorityTag extends TagSupport {

	private static final String AND_DELIMETER = "&&";

	private static final String OR_DELIMETER = "||";

	private String role = "";

	private String permission = "";

    public int doStartTag() throws JspException {
        // execute our condition() method once per invocation
        boolean result = condition();
        // handle conditional behavior
        if (result) {
            return EVAL_BODY_INCLUDE;
        }
        else {
            return SKIP_BODY;
        }
    }

    protected boolean condition() throws JspTagException {
        boolean roleBlank = StringUtils.isBlank(this.role);
        boolean permissionBlank = StringUtils.isBlank(this.permission);
        if (roleBlank && permissionBlank) {
			return false;
		}
		HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        AuthInfo authInfo = AuthUtils.getAuthInfo(request);
        if (authInfo == null) {
            return false;
        }
		if (!roleBlank) {
            if (!checkUserRole(authInfo)) {
                return false;
            }
        }
        //Role and permission relationship is AND
        if (!permissionBlank) {
            if (!checkUserPermission(authInfo)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkUserRole(AuthInfo authInfo) {
        Set<String> roles = authInfo.getRoles();
        if (roles == null || roles.size() <= 0) {
            return false;
        }
        int andIndex = this.role.indexOf(AND_DELIMETER);
        int orIndex = this.role.indexOf(OR_DELIMETER);
        //Invalid format
        if (andIndex >= 0 && orIndex >= 0) {
            return false;
        }
        String[] requireRoles = null;
        Logical logical = Logical.AND;
        if (andIndex >= 0) {
            requireRoles = StringUtils.split(this.role, AND_DELIMETER, true);
        }
        else if (orIndex >= 0) {
            requireRoles = StringUtils.split(this.role, OR_DELIMETER, true);
            logical = Logical.OR;
        }
        else {
            requireRoles = new String[] { this.role };
        }
        return AuthUtils.logicalContain(roles, requireRoles, logical);
    }

    private boolean checkUserPermission(AuthInfo authInfo) {
        Set<String> permissions = authInfo.getPermissions();
        if (permissions == null || permissions.size() <= 0) {
            return false;
        }
        int andIndex = this.permission.indexOf(AND_DELIMETER);
        int orIndex = this.permission.indexOf(OR_DELIMETER);
        //Invalid format
        if (andIndex >= 0 && orIndex >= 0) {
            return false;
        }
        String[] requireRoles = null;
        Logical logical = Logical.AND;
        if (andIndex >= 0) {
            requireRoles = StringUtils.split(this.permission, AND_DELIMETER, true);
        }
        else if (orIndex >= 0) {
            requireRoles = StringUtils.split(this.permission, OR_DELIMETER, true);
            logical = Logical.OR;
        }
        else {
            requireRoles = new String[] { this.permission };
        }
        return AuthUtils.logicalContain(permissions, requireRoles, logical);
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
