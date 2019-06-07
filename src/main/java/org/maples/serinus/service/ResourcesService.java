package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.SerinusResources;
import org.maples.serinus.repository.SerinusResourcesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ResourcesService {

    @Autowired
    private SerinusResourcesMapper resourcesMapper;

    public Map<String, String> loadFilterChainDefinitions() {

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        filterChainDefinitionMap.put("/passport/logout", "logout");
        filterChainDefinitionMap.put("/passport/login", "anon");

        List<SerinusResources> resourcesList = resourcesMapper.selectAll();
        for (SerinusResources resources : resourcesList) {
            if (!StringUtils.isEmpty(resources.getUrl()) && !StringUtils.isEmpty(resources.getPermission())) {
                String permission = "perms[" + resources.getPermission() + "]";
                filterChainDefinitionMap.put(resources.getUrl(), permission);
            }
        }
        filterChainDefinitionMap.put("/**", "user");
        return filterChainDefinitionMap;
    }
}
