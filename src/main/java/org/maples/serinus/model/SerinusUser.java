package org.maples.serinus.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_serinus_user")
public class SerinusUser {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String principal;

    private String credential;

    private String nickname;

    private String mobile;

    private String email;

    private Date birthday;

    private Boolean gender;

    private String avatar;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "reg_ip")
    private String regIp;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "last_login_time")
    private Date lastLoginTime;

    @Column(name = "login_count")
    private Integer loginCount;

    private String remark;

    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}