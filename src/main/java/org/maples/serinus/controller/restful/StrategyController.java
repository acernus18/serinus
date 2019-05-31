package org.maples.serinus.controller.restful;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.service.StrategyService;
import org.maples.serinus.utility.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @PostMapping("/save")
    public ResultBean<Boolean> saveStrategy(@RequestBody SerinusStrategy strategy) {
        try {
            strategyService.saveStrategy(strategy);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage());
            return new ResultBean<>(-1, e.getLocalizedMessage(), false);
        }
        return new ResultBean<>(0, "Success", true);
    }

    @GetMapping("/{product}")
    public Object listStrategyInProduct(@PathVariable("product") String product) {
        return strategyService.getSerinusStrategyMap();
    }
}
