package org.maples.serinus.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_serinus_role")
public class SerinusRole {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}