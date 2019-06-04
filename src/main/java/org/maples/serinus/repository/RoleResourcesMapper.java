package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.maples.serinus.model.RoleResources;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleResourcesMapper extends Mapper<RoleResources> {
    List<RoleResources> selectByRoleID(@Param("roleID") int roleID);

    RoleResources selectByRoleIDAndResourceID(@Param("roleID") int roleID, @Param("resourcesID") int resourcesID);
}