package org.maples.serinus.controller.page;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("message", "database");
        return "health";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/session")
    @ResponseBody
    @RequiresRoles("system")
    public Object session() {
        return SecurityUtils.getSubject().getSession();
    }
    
}
