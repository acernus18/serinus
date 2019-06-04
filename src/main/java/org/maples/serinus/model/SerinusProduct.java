package org.maples.serinus.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_serinus_product")
public class SerinusProduct {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name = "belong_to")
    private String belongTo;

    @Column(name = "product_name")
    private String productName;

    private Byte status;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Date createTime;
}