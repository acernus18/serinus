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
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/strategy/index")
    public String strategyIndex() {
        return "strategy/index";
    }

    @GetMapping("/conf-service/0b5b00d51b08d3918fb22033b797a87a.png")
    public String configImage() {
        return "forward:/static/image/0b5b00d51b08d3918fb22033b797a87a.png";
    }

    @GetMapping("/conf-service/index")
    public String configIndex() {
        return "config/index";
    }
}
