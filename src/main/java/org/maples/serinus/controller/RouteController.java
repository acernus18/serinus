package org.maples.serinus.controller;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.ZKMessage;
import org.maples.serinus.service.ZKClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
public class RouteController {

    @Autowired
    private ZKClientService zkClientService;

    @GetMapping("/config/index")
    public String configPage() {
        return "config/index";
    }

    @GetMapping("/config/0b5b00d51b08d3918fb22033b797a87a.png")
    public String image() {
        return "forward:/static/image/0b5b00d51b08d3918fb22033b797a87a.png";
    }

    @GetMapping("/message")
    public void message(String value) {

        ZKMessage zkMessage = new ZKMessage();
        zkMessage.setValue(value);
        zkClientService.publish("/message", zkMessage);
    }
}
