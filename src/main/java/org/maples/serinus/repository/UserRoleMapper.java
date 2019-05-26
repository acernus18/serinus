package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.maples.serinus.model.UserRole;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserRoleMapper extends Mapper<UserRole> {
    List<UserRole> selectByUserId(@Param("userId") Integer userId);

    List<UserRole> selectByRoleId(@Param("roleId") Integer roleId);

    UserRole selectOneByUserIdAndRoleId(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}