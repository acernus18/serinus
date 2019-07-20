package org.maples.serinus.controller.restful;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.service.PermissionService;
import org.maples.serinus.utility.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/role/add")
    public void addSerinusRole(String roleName) {
        log.info("Receive add role: \n {}", roleName);
        permissionService.addSerinusRole(roleName);
        log.info("Success");
    }

    @GetMapping("/urlRoleMapping")
    public ResultBean<Map<String, List<String>>> getFilterChainMap() {
        return new ResultBean<>(0, "success", permissionService.getUrlRoleMapping());
    }
}
