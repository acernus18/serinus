package org.maples.serinus.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Data
@Table(name = "tb_serinus_role")
public class SerinusRole {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String name;

    private Integer status;
}