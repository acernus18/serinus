package org.maples.serinus.controller.restful;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/role/add")
    public void addSerinusRole(String roleName) {
        log.info("Receive add role: \n {}", roleName);
        permissionService.addSerinusRole(roleName);
        log.info("Success");
    }
}
