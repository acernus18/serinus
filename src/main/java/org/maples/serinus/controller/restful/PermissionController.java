package org.maples.serinus.controller.restful;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.service.PermissionService;
import org.maples.serinus.utility.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/user/add")
    public ResultBean<Object> addSerinusUser(@RequestBody SerinusUser serinusUser) {

        log.info("adding user: \n {}", JSON.toJSONString(serinusUser,true));
        serinusUser.setId(null);

        try {
            permissionService.addSerinusUser(serinusUser);
        } catch (Throwable e) {
            log.error("add user fail, because = [{}]", e.getLocalizedMessage());
            return new ResultBean<>(-1, "Failure", e.getLocalizedMessage());
        }

        return new ResultBean<>(0, "Success", "");
    }

    @GetMapping("/user/list")
    public ResultBean<List<SerinusUser>> listSerinusUsers() {
        return new ResultBean<>(200, "success", permissionService.getSerinusUsers());
    }

    @PostMapping("/role/add")
    public String addSerinusRole(String roleName) {
        log.info("Receive add role: \n {}", roleName);
        permissionService.addSerinusRole(roleName);
        log.info("Success");

        return "redirect:/system/dashboard";
    }
}
