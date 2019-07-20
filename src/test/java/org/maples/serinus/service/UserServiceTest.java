package org.maples.serinus.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.maples.serinus.model.SerinusUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void addSerinusUser() throws Exception {

        SerinusUser user = new SerinusUser();

        user.setPrincipal("maples");
        user.setCredential("123456");
        user.setNickname("maples");
        user.setEmail("maples0108@foxmail.com");

        userService.addSerinusUser(user);
    }
}