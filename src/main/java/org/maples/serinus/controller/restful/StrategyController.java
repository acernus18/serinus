package org.maples.serinus.controller.restful;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusStrategy;
import org.maples.serinus.service.DispatchService;
import org.maples.serinus.service.StrategyService;
import org.maples.serinus.utility.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private DispatchService dispatchService;


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

    @GetMapping("/dispatch/{product}/{deviceID}")
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

        Map<String, List<SerinusStrategy>> serinusStrategyList = strategyService.getSerinusStrategyList();
        List<SerinusStrategy> strategies = serinusStrategyList.get(product);
        return dispatchService.dispatch(strategies, parameters);
    }
}
