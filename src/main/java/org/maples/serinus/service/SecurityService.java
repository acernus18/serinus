package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.maples.serinus.component.SecurityRealm;
import org.maples.serinus.model.SerinusUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SecurityService {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    @Autowired
    private SecurityRealm securityRealm;

    @Autowired
    private UserService userService;

    private DefaultFilterChainManager getFilterChainManager() throws Exception {
        DefaultFilterChainManager defaultFilterChainManager = null;

        AbstractShiroFilter filter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();

        if (filter == null) {
            throw new NullPointerException("Cannot get filter");
        }

        FilterChainResolver resolver = filter.getFilterChainResolver();
        if (resolver instanceof PathMatchingFilterChainResolver) {
            FilterChainManager manager = ((PathMatchingFilterChainResolver) resolver).getFilterChainManager();
            if (manager instanceof DefaultFilterChainManager) {
                defaultFilterChainManager = (DefaultFilterChainManager) manager;
            }
        }
        return defaultFilterChainManager;
    }

    public synchronized void updatePermission() {
        Map<String, String> chainDefinitions = permissionService.loadFilterChainDefinitions();

        try {
            DefaultFilterChainManager filterChainManager = getFilterChainManager();

            filterChainManager.getFilterChains().clear();

            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            shiroFilterFactoryBean.setFilterChainDefinitionMap(chainDefinitions);
            shiroFilterFactoryBean.getFilterChainDefinitionMap().forEach((k, v) -> {
                log.info("Updating url {}, permission = {}", k, v);
                filterChainManager.createChain(k, v.trim().replace(" ", ""));
            });
        } catch (Exception e) {
            throw new RuntimeException("get ShiroFilter from shiroFilterFactoryBean error");
        }
    }

    public void reloadAuthorizingByRoleId(int roleId) {
        Subject subject = SecurityUtils.getSubject();
        String realmName = securityRealm.getName();

        List<SerinusUser> userList = userService.listUsersByRoleID(roleId);

        for (SerinusUser user : userList) {

            SimplePrincipalCollection principals = new SimplePrincipalCollection(user, realmName);
            subject.runAs(principals);
            securityRealm.getAuthorizationCache().remove(subject.getPrincipals());
            subject.releaseRunAs();

            log.info("Serinus User [{}] permission update", user.getPrincipal());
        }
    }
}
