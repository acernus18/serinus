package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusPermission;
import org.maples.serinus.model.SerinusRole;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.repository.SerinusPermissionMapper;
import org.maples.serinus.repository.SerinusRoleMapper;
import org.maples.serinus.repository.SerinusUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PermissionService {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private SerinusPermissionMapper permissionMapper;

    public void addSerinusUser(SerinusUser serinusUser) {
        serinusUser.setStatus(0);
        userMapper.insert(serinusUser);
    }

    public void addSerinusRole(String roleName) {
        SerinusRole role = new SerinusRole();
        role.setName(roleName);
        role.setStatus(0);

        roleMapper.insert(role);
    }

    public void addSerinusPermission(String principal, String roleName, int level) {

        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            SerinusUser user = userMapper.selectOneByPrincipal(principal);
            SerinusRole role = roleMapper.selectByRoleName(roleName);

            if (role != null && user != null) {
                SerinusPermission permission = permissionMapper.selectOneByUserIdAndRoleId(user.getId(), role.getId());
                if (permission == null) {

                    permission = new SerinusPermission();
                    permission.setUserId(user.getId());
                    permission.setRoleId(role.getId());
                    permission.setPermissionLevel(level);
                    permission.setStatus(0);
                    permissionMapper.insert(permission);
                }
            }

            transactionManager.commit(transaction);
        } catch (Throwable e) {
            log.info("Rollback because {}", e.getLocalizedMessage());
            transactionManager.rollback(transaction);
        }

    }

    public void deleteSerinusRole(String roleName) {
        SerinusRole role = roleMapper.selectByRoleName(roleName);

        if (role != null) {
            roleMapper.deleteByPrimaryKey(role.getId());
        }
    }

    public void deleteSerinusPermission(String principal, String roleName) {
        SerinusUser user = userMapper.selectOneByPrincipal(principal);
        SerinusRole role = roleMapper.selectByRoleName(roleName);

        if (role == null || user == null) {
            return;
        }

        SerinusPermission permission = permissionMapper.selectOneByUserIdAndRoleId(user.getId(), role.getId());
        if (permission != null) {
            permissionMapper.deleteByPrimaryKey(permission.getId());
        }
    }

    public List<SerinusRole> getSerinusRoles() {
        return roleMapper.selectAll();
    }

    public List<SerinusUser> getSerinusUsers() {
        return userMapper.selectAll();
    }

    public Map<String, List<SerinusUser>> getRoleUserMapping() {
        Map<String, List<SerinusUser>> roleUserMap = new HashMap<>();

        List<SerinusRole> serinusRoles = roleMapper.selectAll();
        for (SerinusRole serinusRole : serinusRoles) {
            List<SerinusUser> users = new ArrayList<>();

            List<SerinusPermission> permissions = permissionMapper.selectByRoleId(serinusRole.getId());
            for (SerinusPermission permission : permissions) {
                users.add(userMapper.selectByPrimaryKey(permission.getUserId()));
            }

            roleUserMap.put(serinusRole.getName(), users);
        }

        return roleUserMap;
    }
}
