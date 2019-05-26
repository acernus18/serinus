package org.maples.serinus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    
}
