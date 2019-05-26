package org.maples.serinus.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Data
@Table(name = "tb_serinus_role_resources")
public class RoleResources {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "resources_id")
    private Integer resourcesId;

    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}