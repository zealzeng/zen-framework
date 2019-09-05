package org.zenframework.web_demo.web.ctrl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zenframework.web.ctrl.BaseCtrl;

import javax.servlet.http.HttpServletRequest;

/**
 * Index, login, logout page
 * @author Zeal
 */
@Controller
public class IndexCtrl extends BaseCtrl {

    @RequestMapping("")
    public String index(HttpServletRequest request) {
        return "index";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        return "redirect:/home";
    }

}
