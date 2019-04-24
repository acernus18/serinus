package org.maples.serinus.repository;

import org.junit.Assert;
import org.junit.Test;
import org.maples.serinus.SerinusApplicationTests;
import org.maples.serinus.model.SerinusPermission;
import org.maples.serinus.model.SerinusRole;
import org.maples.serinus.model.SerinusUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class SerinusPermissionTest extends SerinusApplicationTests {

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private SerinusPermissionMapper permissionMapper;

    @Test
    @Transactional
    public void testDeleteCascade() {
        SerinusUser serinusUser = new SerinusUser();
        serinusUser.setPrincipal("v_test_account");
        serinusUser.setCredential("v_test_password");
        serinusUser.setStatus(0);
        serinusUser.setName("test");
        serinusUser.setEmail("maples0108@foxmail.com");
        userMapper.insert(serinusUser);

        SerinusRole serinusRole = new SerinusRole();
        serinusRole.setName("system_admin");
        serinusRole.setStatus(0);
        roleMapper.insert(serinusRole);

        SerinusPermission permission = new SerinusPermission();
        permission.setUserId(serinusUser.getId());
        permission.setRoleId(serinusRole.getId());
        permission.setPermissionLevel(0);
        permission.setStatus(0);
        permissionMapper.insert(permission);

        // Read
        SerinusUser testUser = userMapper.selectOneByPrincipal("v_test_account");
        Assert.assertEquals(testUser.getCredential(), "v_test_password");

        // Delete
        Assert.assertNotNull(permissionMapper.selectByPrimaryKey(permission.getId()));
        userMapper.deleteByPrimaryKey(serinusUser.getId());
        Assert.assertNull(permissionMapper.selectByPrimaryKey(permission.getId()));
        roleMapper.deleteByPrimaryKey(serinusRole.getId());
    }

    @Test
    @Transactional
    public void testUniqueInsert() {
        // TODO: duplicate insert
    }
}