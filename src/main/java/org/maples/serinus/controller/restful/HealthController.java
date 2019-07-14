package org.maples.serinus.controller.restful;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/system/health")
public class HealthController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/filter")
    public Map<String, String> getFilterChainDefinitionMap() {
        return securityService.getFilterChainDefinitionMap();
    }
}
