package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.maples.serinus.model.SerinusStrategy;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SerinusStrategyMapper extends Mapper<SerinusStrategy> {
    int selectCountByProduct(@Param("product") String product);

    List<SerinusStrategy> selectAllEnabledByProduct(@Param("product") String product);
}