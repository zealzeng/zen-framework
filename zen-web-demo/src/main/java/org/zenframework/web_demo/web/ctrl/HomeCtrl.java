package org.zenframework.web_demo.web.ctrl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author
 */
@Controller
public class HomeCtrl {

    @RequestMapping("/home")
    public String home(HttpServletRequest request) {
        return "home";
    }

}
