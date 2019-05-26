package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusRole;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.model.UserRole;
import org.maples.serinus.repository.SerinusRoleMapper;
import org.maples.serinus.repository.SerinusUserMapper;
import org.maples.serinus.repository.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PermissionService {

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;


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
    public void addUserRole(String principal, String roleName) {
        SerinusUser user = userMapper.selectOneByPrincipal(principal);
        SerinusRole role = roleMapper.selectByRoleName(roleName);

        if (role != null && user != null) {
            UserRole userRole = userRoleMapper.selectOneByUserIdAndRoleId(user.getId(), role.getId());
            if (userRole == null) {

                userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(role.getId());
                userRole.setStatus(0);
                userRoleMapper.insert(userRole);
            }
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
    public void deleteUserRole(String principal, String roleName) {
        SerinusUser user = userMapper.selectOneByPrincipal(principal);
        SerinusRole role = roleMapper.selectByRoleName(roleName);

        if (role == null || user == null) {
            return;
        }

        UserRole userRole = userRoleMapper.selectOneByUserIdAndRoleId(user.getId(), role.getId());
        if (userRole != null) {
            userRoleMapper.deleteByPrimaryKey(userRole.getId());
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

            List<UserRole> userRoles = userRoleMapper.selectByRoleId(serinusRole.getId());
            for (UserRole permission : userRoles) {
                users.add(userMapper.selectByPrimaryKey(permission.getUserId()));
            }

            roleUserMap.put(serinusRole.getName(), users);
        }

        return roleUserMap;
    }
}
