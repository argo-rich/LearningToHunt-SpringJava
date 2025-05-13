package com.learningtohunt.web.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = { "/hunting-guide", "/article/**", "/account/**", "/privacy" }) // replicate in ProjectSecurityConfig.filterChain()
    public String forward() {
        return "forward:/index.html";
    }
}
