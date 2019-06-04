package org.maples.serinus.controller.restful;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.service.UserService;
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
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResultBean<Object> addSerinusUser(@RequestBody SerinusUser serinusUser) {

        log.info("adding user: \n {}", JSON.toJSONString(serinusUser,true));
        serinusUser.setId(null);

        try {
            userService.addSerinusUser(serinusUser);
        } catch (Throwable e) {
            log.error("add user fail, because = [{}]", e.getLocalizedMessage());
            return new ResultBean<>(-1, "Failure", e.getLocalizedMessage());
        }

        return new ResultBean<>(0, "Success", "");
    }

    @PostMapping("/active")
    public ResultBean<String> activeUser(String principal) {

        if (StringUtils.isNotBlank(principal)) {
            userService.active(principal);
            return new ResultBean<>(0, "Success", "");
        }

        return new ResultBean<>(-1, "Failure", "");
    }

    @GetMapping("/list")
    public ResultBean<List<SerinusUser>> listSerinusUsers() {
        return new ResultBean<>(0, "success", userService.getSerinusUsers());
    }
}
