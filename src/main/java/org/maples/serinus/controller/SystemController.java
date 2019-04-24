package org.maples.serinus.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.service.AuthorizingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private AuthorizingService authorizingService;

    @GetMapping("/dashboard")
    public String dashboardFTL(Model model) {
        model.addAttribute("allUsers", authorizingService.getSerinusUsers());
        model.addAttribute("rolesMapping", authorizingService.getRoleUserMapping());
        return "system/dashboard";
    }

    @PostMapping("/user/add")
    public String addSerinusUser(String principal, String credential, String name, String email) {
        SerinusUser serinusUser = new SerinusUser();
        serinusUser.setPrincipal(principal);
        serinusUser.setCredential(credential);
        serinusUser.setName(name);
        serinusUser.setEmail(email);

        log.info("Receive add user: \n {}", JSON.toJSONString(serinusUser, true));
        authorizingService.addSerinusUser(serinusUser);
        log.info("Success");

        return "redirect:/system/dashboard";
    }

    @PostMapping("/role/add")
    public String addSerinusRole(String roleName) {
        log.info("Receive add role: \n {}", roleName);
        authorizingService.addSerinusRole(roleName);
        log.info("Success");

        return "redirect:/system/dashboard";
    }

    @GetMapping("/role/delete/{roleName}")
    public String deleteSerinusRole(@PathVariable("roleName") String roleName) {
        authorizingService.deleteSerinusRole(roleName);
        return "redirect:/system/dashboard";
    }

    @PostMapping("/permission/{roleName}/add")
    public String addSerinusPermission(@PathVariable String roleName, String principal, int level) {
        log.info("Receive add permission {} to {}, level {}", roleName, principal, level);
        authorizingService.addSerinusPermission(principal, roleName, level);
        log.info("Success");

        return "redirect:/system/dashboard";
    }

    @GetMapping("/permission/{roleName}/delete/{principal}")
    public String deleteSerinusPermission(@PathVariable("principal") String principal,
                                          @PathVariable("roleName") String roleName) {
        authorizingService.deleteSerinusPermission(principal, roleName);
        return "redirect:/system/dashboard";
    }
}
