package org.maples.serinus.controller;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.service.StrategyService;
import org.maples.serinus.utility.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @GetMapping("/index")
    public String strategyIndex() {
        return "strategy/index";
    }

    @GetMapping("/{product}")
    @ResponseBody
    public Object listStrategyInProduct(@PathVariable("product") String product) {
        return new ResultBean<>(0, "Success", strategyService.getSerinusStrategyList().get(product));
    }

    @PostMapping("/{product}/save")
    @ResponseBody
    public ResultBean<Boolean> saveStrategy(@PathVariable("product") String product,
                                            @RequestBody SerinusStrategy strategy) {
        try {
            strategy.setProduct(product);
            strategyService.saveStrategy(strategy);
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage());
            return new ResultBean<>(-1, e.getLocalizedMessage(), false);
        }
        return new ResultBean<>(0, "Success", true);
    }

    // delete
    // update
    // search
    // active
    // inactive
    // export
    // import dispatched list
    // import black and white list
    // add dispatched list
    // adjust


    @GetMapping("/dispatch/{product}/{deviceID}")
    @ResponseBody
    public Object dispatch(@PathVariable("product") String product,
                           @PathVariable("deviceID") String deviceID,
                           HttpServletRequest request) {

        Map<String, String> parameters = new HashMap<>();

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = request.getParameter(key);
            parameters.put(key, value);
        }

        parameters.put("deviceID", deviceID);

        List<SerinusStrategy> strategies = strategyService.getSerinusStrategiesByProduct(product);
        return strategyService.dispatch(strategies, parameters);
    }
}
