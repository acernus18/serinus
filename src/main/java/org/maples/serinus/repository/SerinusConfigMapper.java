package org.maples.serinus.repository;

import org.apache.ibatis.annotations.Param;
import org.maples.serinus.model.SerinusConfig;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SerinusConfigMapper extends Mapper<SerinusConfig> {

    SerinusConfig selectOneByCKey(@Param("cKey") String cKey);

    SerinusConfig selectOneNormalByCKey(@Param("cKey") String cKey);

    SerinusConfig selectOnePolicyByCKey(@Param("cKey") String cKey);

    List<String> selectByKeywordWithOrder(@Param("product") String product,
                                          @Param("word") String word,
                                          @Param("status") Integer status,
                                          @Param("orderKey") String orderKey,
                                          @Param("order") Boolean order);

    Integer updateValueWithHistoryByID(@Param("id") Long id);
}