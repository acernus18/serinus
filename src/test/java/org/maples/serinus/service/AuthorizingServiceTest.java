package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.maples.serinus.SerinusApplicationTests;
import org.maples.serinus.config.DataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.Assert.*;

@Slf4j
public class AuthorizingServiceTest extends SerinusApplicationTests {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private SecurityManager securityManager;

    @Test
    public void test() {
        Subject subject = SecurityUtils.getSubject();
        log.info("login = {}", subject.isAuthenticated());
        subject = securityManager.login(subject, new UsernamePasswordToken("maples", "216106"));
        log.info("login = {}", subject.isAuthenticated());
    }

}