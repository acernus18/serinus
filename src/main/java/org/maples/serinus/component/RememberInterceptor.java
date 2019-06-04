package org.maples.serinus.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.repository.SerinusUserMapper;
import org.maples.serinus.utility.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class RememberInterceptor implements HandlerInterceptor {

    @Autowired
    private SerinusUserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        log.info("RememberInterceptor is taking effect");

        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return true;
        }

        if(subject.isRemembered()) {
            try {
                String principal = (String) subject.getPrincipal();

                SerinusUser user = userMapper.selectOneByPrincipal(principal);
                UsernamePasswordToken token = new UsernamePasswordToken();

                token.setUsername(principal);
                token.setPassword(PasswordUtil.decrypt(user.getCredential(), principal).toCharArray());
                token.setRememberMe(true);

                subject.login(token);

                log.info("[{}] - Has login automatic", principal);
            } catch (Exception e) {
                log.error("automatic login action fail [{}], redirect to login page", e.getLocalizedMessage());
                resp.sendRedirect(req.getContextPath() + "/passport/login");
                return false;
            }
            return true;
        }

        log.info("Interceptor return true");
        return true;
    }
}
