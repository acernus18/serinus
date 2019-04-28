package org.maples.serinus.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.maples.serinus.model.SerinusPermission;
import org.maples.serinus.model.SerinusRole;
import org.maples.serinus.model.SerinusUser;
import org.maples.serinus.repository.SerinusPermissionMapper;
import org.maples.serinus.repository.SerinusRoleMapper;
import org.maples.serinus.repository.SerinusUserMapper;
import org.maples.serinus.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
public class SecurityConfig {

    private static final boolean ENABLE = false;
    private static final String SYSTEM_ADMIN = "system_admin";
    private static final String USER_SUFFIX = "_user";

    @Autowired
    private SerinusUserMapper userMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private SerinusPermissionMapper permissionMapper;

    @Autowired
    private SessionService sessionService;

    @Bean
    public Realm authorizingRealm() {
        return new AuthorizingRealm() {
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
        };
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(sessionService);
        return sessionManager;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(authorizingRealm());
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/check", "anon");

        if (ENABLE) {
            filterChainDefinitionMap.put("/**", "authc");
        } else {
            filterChainDefinitionMap.put("/**", "anon");
        }

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
}
