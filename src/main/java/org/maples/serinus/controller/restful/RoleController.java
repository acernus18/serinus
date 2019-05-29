package org.maples.serinus.controller.restful;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusRole;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.service.PermissionService;
import org.maples.serinus.utility.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/role")
public class RoleController {
    
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add")
    public String addSerinusRole(String roleName) {
        log.info("Receive add role: \n {}", roleName);
        permissionService.addSerinusRole(roleName);
        log.info("Success");

        return "redirect:/system/dashboard";
    }

    @PostMapping("/modify")
    public ResultBean<String> modifySerinusRole(@RequestBody SerinusRole serinusRole) {

        return new ResultBean<>(200, "success", "");
    }

    @PostMapping("/delete")
    public ResultBean<String> deleteSerinusRole(long serinusRoleID) {

        return new ResultBean<>(200, "success", "");
    }

    @PostMapping("/list")
    public ResultBean<String> listSerinusRole() {

        return new ResultBean<>(200, "success", "");
    }
}
