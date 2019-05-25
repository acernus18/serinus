package org.maples.serinus.service;

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
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class RealmService extends AuthorizingRealm {
    private static final boolean ENABLE = false;
    private static final String SYSTEM_ADMIN = "system_admin";
    private static final String USER_SUFFIX = "_user";

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private SerinusPermissionMapper permissionMapper;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String principal = (String) token.getPrincipal();
        SerinusUser serinusUser = userMapper.selectOneByPrincipal(principal);

        if (serinusUser == null) {
            throw new UnknownAccountException("Invalid principal");
        }

        return new SimpleAuthenticationInfo(principal, serinusUser.getCredential(), this.getName());
    }

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
}
