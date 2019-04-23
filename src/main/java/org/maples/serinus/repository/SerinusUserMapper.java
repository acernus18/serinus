package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.maples.serinus.model.SerinusUser;
import tk.mybatis.mapper.common.Mapper;

public interface SerinusUserMapper extends Mapper<SerinusUser> {
    SerinusUser selectOneByPrincipal(@Param("principal") String principal);
}