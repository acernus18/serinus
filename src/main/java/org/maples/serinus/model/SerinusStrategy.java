package org.maples.serinus.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_serinus_strategy")
public class SerinusStrategy {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String uuid;

    private String product;

    private String title;

    private Integer type;

    @Column(name = "preset_type")
    private Integer presetType;

    @Column(name = "max_count")
    private Integer maxCount;

    @Column(name = "start_at")
    private Date startAt;

    @Column(name = "end_at")
    private Date endAt;

    private String filter;

    @Column(name = "always_return")
    private Boolean alwaysReturn;

    @Column(name = "order_in_product")
    private Integer orderInProduct;

    private Boolean enabled;

    private String content;
}