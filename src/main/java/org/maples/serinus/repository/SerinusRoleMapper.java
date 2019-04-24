package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.maples.serinus.model.SerinusRole;
import tk.mybatis.mapper.common.Mapper;

public interface SerinusRoleMapper extends Mapper<SerinusRole> {
    SerinusRole selectByRoleName(@Param("roleName") String roleName);
}