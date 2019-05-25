CREATE TABLE IF NOT EXISTS `sys_resources`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(100) DEFAULT NULL,
    `type`        varchar(50)  DEFAULT NULL,
    `url`         varchar(200) DEFAULT NULL,
    `permission`  varchar(100) DEFAULT NULL,
    `parent_id`   bigint(20)   DEFAULT '0',
    `sort`        int(10)      DEFAULT NULL,
    `external`    tinyint(1)   DEFAULT NULL COMMENT '是否外部链接',
    `available`   tinyint(1)   DEFAULT '0',
    `icon`        varchar(100) DEFAULT NULL COMMENT '菜单图标',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_sys_resource_parent_id` (`parent_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_role`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(100) DEFAULT NULL COMMENT '角色名',
    `description` varchar(100) DEFAULT NULL,
    `available`   tinyint(1)   DEFAULT '0',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_role_resources`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `role_id`      bigint(20) NOT NULL,
    `resources_id` bigint(20) NOT NULL,
    `create_time`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT,
    `username`        varchar(100)                 DEFAULT NULL,
    `password`        varchar(100)                 DEFAULT NULL COMMENT '登录密码',
    `nickname`        varchar(30)                  DEFAULT '' COMMENT '昵称',
    `mobile`          varchar(30)                  DEFAULT NULL COMMENT '手机号',
    `email`           varchar(100)                 DEFAULT NULL COMMENT '邮箱地址',
    `qq`              varchar(20)                  DEFAULT NULL COMMENT 'QQ',
    `birthday`        date                         DEFAULT NULL COMMENT '生日',
    `gender`          tinyint(2)                   DEFAULT NULL COMMENT '性别',
    `avatar`          varchar(255)                 DEFAULT NULL COMMENT '头像地址',
    `user_type`       enum ('ROOT','ADMIN','USER') DEFAULT 'ADMIN' COMMENT '超级管理员、管理员、普通用户',
    `reg_ip`          varchar(30)                  DEFAULT NULL COMMENT '注册IP',
    `last_login_ip`   varchar(30)                  DEFAULT NULL COMMENT '最近登录IP',
    `last_login_time` datetime                     DEFAULT NULL COMMENT '最近登录时间',
    `login_count`     int(10)                      DEFAULT '0' COMMENT '登录次数',
    `remark`          varchar(100)                 DEFAULT NULL COMMENT '用户备注',
    `status`          int(1)                       DEFAULT NULL COMMENT '用户状态',
    `create_time`     datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time`     datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_user_role`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`     bigint(20) NOT NULL,
    `role_id`     bigint(20) NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

