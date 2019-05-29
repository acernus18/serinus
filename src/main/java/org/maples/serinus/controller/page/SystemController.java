package org.maples.serinus.controller.page;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.service.PermissionService;
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
    private PermissionService permissionService;

    @GetMapping("/dashboard")
    public String dashboardFTL(Model model) {
        model.addAttribute("allUsers", permissionService.getSerinusUsers());
        model.addAttribute("rolesMapping", permissionService.getRoleUserMapping());
        return "system/dashboard";
    }



    @GetMapping("/role/delete/{roleName}")
    public String deleteSerinusRole(@PathVariable("roleName") String roleName) {
        permissionService.deleteSerinusRole(roleName);
        return "redirect:/system/dashboard";
    }

    @PostMapping("/permission/{roleName}/add")
    public String addUserRole(@PathVariable String roleName, String principal) {
        log.info("Receive add permission {} to {}", roleName, principal);
        permissionService.addUserRole(principal, roleName);
        log.info("Success");

        return "redirect:/system/dashboard";
    }

    @GetMapping("/permission/{roleName}/delete/{principal}")
    public String deleteUserRole(@PathVariable("principal") String principal,
                                 @PathVariable("roleName") String roleName) {
        permissionService.deleteUserRole(principal, roleName);
        return "redirect:/system/dashboard";
    }
}
