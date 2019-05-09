package org.maples.serinus.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_strategy_history")
public class StrategyHistory {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String uuid;

    private String product;

    @Column(name = "strategy_uuid")
    private String strategyUuid;

    @Column(name = "operator_id")
    private Integer operatorId;

    @Column(name = "inspector_id")
    private Integer inspectorId;

    private Date time;

    private Integer flag;

    @Column(name = "value_before")
    private String valueBefore;

    @Column(name = "value_after")
    private String valueAfter;
}