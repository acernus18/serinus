package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.maples.serinus.model.SerinusResources;
import tk.mybatis.mapper.common.Mapper;

public interface SerinusResourcesMapper extends Mapper<SerinusResources> {
    SerinusResources selectByUrl(@Param("url") String url);
}