package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.maples.serinus.model.SerinusPermission;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SerinusPermissionMapper extends Mapper<SerinusPermission> {
    List<SerinusPermission> selectByUserId(@Param("userId") Integer userId);
}