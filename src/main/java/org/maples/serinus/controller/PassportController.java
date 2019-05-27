package org.maples.serinus.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.maples.serinus.utility.ResultBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/passport")
public class PassportController {

    @PostMapping("/login")
    public ResultBean<String> login(String username, String password, boolean rememberMe) {
        log.info("Login user = {}, password = {}, rememberMe = {}", username, password, rememberMe);

        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        Subject currentUser = SecurityUtils.getSubject();

        try {
            currentUser.login(token);
            return new ResultBean<>(200, "", "Success");
        } catch (Exception e) {
            log.error("Login fail username = [{}] because = {}", username, e.getLocalizedMessage());
            token.clear();
            return new ResultBean<>(200, e.getLocalizedMessage(), "Fail");
        }
    }

    @GetMapping("/logout")
    public ResultBean<String> logout() {
        SecurityUtils.getSubject().logout();
        return new ResultBean<>(200, "", "Success");
    }
}
