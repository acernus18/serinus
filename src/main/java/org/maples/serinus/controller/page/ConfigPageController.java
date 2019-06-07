package org.maples.serinus.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/conf-service")
public class ConfigPageController {
    @GetMapping("/0b5b00d51b08d3918fb22033b797a87a.png")
    public String image() {
        return "forward:/static/image/0b5b00d51b08d3918fb22033b797a87a.png";
    }

    @GetMapping("/index")
    public String index() {
        return "config/index";
    }
}
