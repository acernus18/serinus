package org.maples.serinus.controller;

import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @PostMapping("/save")
    public void saveStrategy(@RequestBody SerinusStrategy strategy) {
        strategyService.saveStrategy(strategy);
    }

    @GetMapping("/{product}")
    @ResponseBody
    public Object listStrategyInProduct(@PathVariable("product") String product) {
        return strategyService.getSerinusStrategyMap();
    }
}
