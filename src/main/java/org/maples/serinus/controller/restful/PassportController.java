package org.maples.serinus.controller.restful;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.maples.serinus.utility.ResultBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
            return new ResultBean<>(0, "", "Success");
        } catch (Exception e) {
            log.error("Login fail username = [{}] because = {}", username, e.getLocalizedMessage());
            token.clear();
            return new ResultBean<>(-1, e.getLocalizedMessage(), "Fail");
        }
    }

    @GetMapping("/logout")
    public ResultBean<String> logout() {
        SecurityUtils.getSubject().logout();
        return new ResultBean<>(0, "", "Success");
    }

    @GetMapping("/check")
    public ResultBean<Map<String, Object>> check() {
        Map<String, Object> result = new HashMap<>();

        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();

        result.put("authenticated", subject.isAuthenticated());
        result.put("session", session.getId());
        result.put("rememberMe", subject.isRemembered());

        session.getAttributeKeys().forEach(k -> result.put((String) k, session.getAttribute(k)));

        return new ResultBean<>(0, "Success", result);
    }
}
