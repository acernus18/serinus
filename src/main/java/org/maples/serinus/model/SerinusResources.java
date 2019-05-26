package org.maples.serinus.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "tb_serinus_resources")
public class SerinusResources {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String name;

    private String type;

    private String url;

    private String permission;

    @Column(name = "parent_id")
    private Long parentId;

    private Integer sort;

    private Boolean external;

    private Boolean available;

    private String icon;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}