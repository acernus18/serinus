package org.maples.serinus.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class RouteController {
    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("message", "database");
        return "index";
    }

    @GetMapping("/register")
    public String userAdd() {
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
}
