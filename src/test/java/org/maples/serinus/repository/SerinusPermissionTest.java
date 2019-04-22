package org.maples.serinus.repository;

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
    public void testCreate() {
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
    }

    // @Test
    // public void testRead() {
    //
    // }
    //
    // @Test
    // public void testUpdate() {
    //     List<SerinusUser> serinusUsers = userMapper.selectAll();
    //     Assert.assertTrue(serinusUsers.isEmpty());
    // }
    //
    // @Test
    // public void testDelete() {
    //     List<SerinusUser> serinusUsers = userMapper.selectAll();
    //     Assert.assertTrue(serinusUsers.isEmpty());
    // }
}