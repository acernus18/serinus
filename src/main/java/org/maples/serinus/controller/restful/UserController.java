package org.maples.serinus.controller.restful;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add")
    public String addSerinusUser(String principal, String credential, String name, String email) {

        SerinusUser serinusUser = new SerinusUser();
        serinusUser.setPrincipal(principal);
        serinusUser.setCredential(credential);
        serinusUser.setNickname(name);
        serinusUser.setEmail(email);

        log.info("Receive add user: \n {}", JSON.toJSONString(serinusUser, true));
        permissionService.addSerinusUser(serinusUser);
        log.info("Success");

        return "redirect:/system/dashboard";
    }

    @PostMapping("/modify")
    public ResultBean<String> modifySerinusUser(@RequestBody SerinusUser serinusUser) {

        return new ResultBean<>(200, "success", "");
    }

    @PostMapping("/delete")
    public ResultBean<String> deleteSerinusUser(long serinusUserID) {

        return new ResultBean<>(200, "success", "");
    }

    @PostMapping("/list")
    public ResultBean<String> listSerinusUsers() {

        return new ResultBean<>(200, "success", "");
    }
}
