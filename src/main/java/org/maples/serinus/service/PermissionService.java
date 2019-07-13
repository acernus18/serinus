package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.RoleResources;
import org.maples.serinus.model.SerinusResources;
import org.maples.serinus.model.SerinusRole;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.model.UserRole;
import org.maples.serinus.repository.RoleResourcesMapper;
import org.maples.serinus.repository.SerinusResourcesMapper;
import org.maples.serinus.repository.SerinusRoleMapper;
import org.maples.serinus.repository.SerinusUserMapper;
import org.maples.serinus.repository.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PermissionService {

    private static final boolean ENABLE_ACCESS_CONTROL = false;

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private SerinusResourcesMapper resourcesMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleResourcesMapper roleResourcesMapper;

    @Transactional
    public void addSerinusRole(String roleName) {
        SerinusRole role = new SerinusRole();
        role.setName(roleName);
        role.setStatus(0);
        role.setDescription("");
        role.setAvailable(true);
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        roleMapper.insert(role);
    }

    @Transactional
    public void addSerinusResources(String url, String permission) {
        SerinusResources resources = new SerinusResources();
        resources.setName(url);
        resources.setUrl(url);
        resources.setPermission(permission);
        resources.setAvailable(true);
        resources.setCreateTime(new Date());
        resources.setUpdateTime(new Date());
        resourcesMapper.insert(resources);
    }

    @Transactional
    public void addRoleResources(String roleName, String url) {
        SerinusRole serinusRole = roleMapper.selectByRoleName(roleName);
        SerinusResources resources = resourcesMapper.selectByUrl(url);

        if (serinusRole != null && resources != null) {
            Integer serinusRoleId = serinusRole.getId();
            Integer resourcesId = resources.getId();
            RoleResources roleResources = roleResourcesMapper.selectByRoleIDAndResourceID(serinusRoleId, resourcesId);

            if (roleResources == null) {
                roleResources = new RoleResources();
                roleResources.setRoleId(serinusRoleId);
                roleResources.setResourcesId(resourcesId);
                roleResources.setStatus(0);
                roleResources.setCreateTime(new Date());
                roleResources.setUpdateTime(new Date());

                roleResourcesMapper.insert(roleResources);
            }
        }
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
                userRole.setCreateTime(new Date());
                userRole.setUpdateTime(new Date());
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
    public void deleteSerinusResources(String url) {
        SerinusResources resources = resourcesMapper.selectByUrl(url);

        if (resources != null) {
            resourcesMapper.deleteByPrimaryKey(resources.getId());
        }
    }

    @Transactional
    public void deleteRoleResources(String roleName, String url) {
        SerinusRole serinusRole = roleMapper.selectByRoleName(roleName);
        SerinusResources resources = resourcesMapper.selectByUrl(url);

        if (serinusRole != null && resources != null) {
            Integer serinusRoleId = serinusRole.getId();
            Integer resourcesId = resources.getId();
            RoleResources roleResources = roleResourcesMapper.selectByRoleIDAndResourceID(serinusRoleId, resourcesId);

            if (roleResources != null) {
                roleResourcesMapper.deleteByPrimaryKey(roleResources.getId());
            }
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

    public Map<String, String> getUrlRoleMapping() {
        Map<String, String> result = new HashMap<>();

        List<RoleResources> roleResources = roleResourcesMapper.selectAll();

        for (RoleResources roleResource : roleResources) {
            int resourcesId = roleResource.getResourcesId();
            int roleId = roleResource.getRoleId();

            SerinusResources resources = resourcesMapper.selectByPrimaryKey(resourcesId);
            SerinusRole role = roleMapper.selectByPrimaryKey(roleId);

            result.put(resources.getUrl(), role.getName());
        }

        return result;
    }

    public Map<String, String> loadFilterChainDefinitions() {

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        if (ENABLE_ACCESS_CONTROL) {
            filterChainDefinitionMap.put("/webjars/**", "anon");
            filterChainDefinitionMap.put("/static/**", "anon");
            filterChainDefinitionMap.put("/passport/**", "anon");
            filterChainDefinitionMap.put("/index", "anon");
            filterChainDefinitionMap.put("/register", "anon");
            filterChainDefinitionMap.put("/**", "authc");

            List<SerinusResources> resourcesList = resourcesMapper.selectAll();
            for (SerinusResources resources : resourcesList) {
                if (!StringUtils.isEmpty(resources.getUrl()) && !StringUtils.isEmpty(resources.getPermission())) {
                    String permission = "perms[" + resources.getPermission() + "]";
                    filterChainDefinitionMap.put(resources.getUrl(), permission);
                }
            }
            filterChainDefinitionMap.put("/**", "user");
        } else {
            filterChainDefinitionMap.put("/**", "anon");
        }

        return filterChainDefinitionMap;
    }
}
