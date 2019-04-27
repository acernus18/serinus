package org.maples.serinus.controller;

import org.maples.serinus.model.SerinusStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/strategy")
public class StrategyController {

    @PostMapping("/save")
    public void saveStrategy(@RequestBody SerinusStrategy strategy) {

    }

}
