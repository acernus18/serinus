package org.maples.serinus.model;

import lombok.Data;

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

    private Integer status;

    private String name;

    private String email;
}