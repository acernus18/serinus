package org.maples.serinus.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maples.serinus.model.SerinusRole;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SerinusPermissionTest {

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Test
    @Transactional
    public void testDeleteCascade() {
        SerinusUser serinusUser = new SerinusUser();
        serinusUser.setPrincipal("v_test_account");
        serinusUser.setCredential("v_test_password");
        serinusUser.setStatus(0);
        serinusUser.setNickname("test");
        serinusUser.setEmail("maples0108@foxmail.com");
        userMapper.insert(serinusUser);

        SerinusRole serinusRole = new SerinusRole();
        serinusRole.setName("system_admin");
        serinusRole.setDescription("hello");
        serinusRole.setAvailable(true);
        serinusRole.setStatus(0);
        roleMapper.insert(serinusRole);

        UserRole permission = new UserRole();
        permission.setUserId(serinusUser.getId());
        permission.setRoleId(serinusRole.getId());
        permission.setStatus(0);
        userRoleMapper.insert(permission);

        // Read
        SerinusUser testUser = userMapper.selectOneByPrincipal("v_test_account");
        Assert.assertEquals(testUser.getCredential(), "v_test_password");

        // Delete
        Assert.assertNotNull(userRoleMapper.selectByPrimaryKey(permission.getId()));
        userMapper.deleteByPrimaryKey(serinusUser.getId());
        Assert.assertNull(userRoleMapper.selectByPrimaryKey(permission.getId()));
        roleMapper.deleteByPrimaryKey(serinusRole.getId());
    }
}