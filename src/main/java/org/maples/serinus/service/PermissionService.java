package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class PermissionService extends AuthorizingRealm {

    private static final boolean ENABLE = false;
    private static final String SYSTEM_ADMIN = "system_admin";
    private static final String USER_SUFFIX = "_user";

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private SerinusPermissionMapper permissionMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String principal = (String) principals.getPrimaryPrincipal();
        SerinusUser serinusUser = userMapper.selectOneByPrincipal(principal);
        List<SerinusPermission> permissions = permissionMapper.selectByUserId(serinusUser.getId());

        Set<String> roles = new HashSet<>();
        for (SerinusPermission permission : permissions) {
            SerinusRole role = roleMapper.selectByPrimaryKey(permission.getRoleId());

            if (permission.getPermissionLevel() == 0) {
                roles.add(role.getName());
            } else if (permission.getPermissionLevel() == 1) {
                roles.add(role.getName() + USER_SUFFIX);
            }
        }
        return new SimpleAuthorizationInfo(roles);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        String principal = (String) token.getPrincipal();
        SerinusUser serinusUser = userMapper.selectOneByPrincipal(principal);

        if (serinusUser == null) {
            throw new UnknownAccountException("Invalid principal");
        }

        return new SimpleAuthenticationInfo(principal, serinusUser.getCredential(), this.getName());
    }

    @Transactional
    public void addSerinusUser(SerinusUser serinusUser) {
        serinusUser.setStatus(0);
        userMapper.insert(serinusUser);
    }

    @Transactional
    public void addSerinusRole(String roleName) {
        SerinusRole role = new SerinusRole();
        role.setName(roleName);
        role.setStatus(0);

        roleMapper.insert(role);
    }

    @Transactional
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

    @Transactional
    public void deleteSerinusRole(String roleName) {
        SerinusRole role = roleMapper.selectByRoleName(roleName);

        if (role != null) {
            roleMapper.deleteByPrimaryKey(role.getId());
        }
    }

    @Transactional
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
