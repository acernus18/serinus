package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.maples.serinus.model.SerinusStrategy;
import tk.mybatis.mapper.common.Mapper;

public interface SerinusStrategyMapper extends Mapper<SerinusStrategy> {

    @Select(value = {"select count(0)",
            "from `tb_serinus_strategy`",
            "where product = ${product}"})
    int selectCountByProduct(@Param("product") String product);
}