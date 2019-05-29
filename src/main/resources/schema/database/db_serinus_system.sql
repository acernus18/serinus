create database if not exists `db_serinus_system` default character set utf8mb4 collate utf8mb4_unicode_ci;

create table if not exists `tb_serinus_user`
(
    `id`              int          not null primary key auto_increment,
    `principal`       varchar(255) not null,
    `credential`      varchar(255) not null comment '登录密码',
    `nickname`        varchar(30)  not null default '' comment '昵称',
    `mobile`          varchar(30) comment '手机号',
    `email`           varchar(255) not null comment '邮箱地址',
    `birthday`        date                  default null comment '生日',
    `gender`          tinyint(1)            default null comment '性别',
    `avatar`          varchar(255)          default null comment '头像地址',
    `user_type`       varchar(30)           default '' comment '超级管理员、管理员、普通用户',
    `reg_ip`          varchar(30)           default null comment '注册ip',
    `last_login_ip`   varchar(30)           default null comment '最近登录ip',
    `last_login_time` datetime              default null comment '最近登录时间',
    `login_count`     int(10)               default '0' comment '登录次数',
    `remark`          varchar(100)          default null comment '用户备注',
    `status`          int                   default 0 comment '用户状态',
    `create_time`     datetime              default current_timestamp comment '注册时间',
    `update_time`     datetime              default current_timestamp comment '更新时间',

    unique index u_idx_principal (`principal`)
) engine = innodb
  default charset `utf8mb4`;

create table if not exists `tb_serinus_role`
(
    `id`          int          not null primary key auto_increment,
    `name`        varchar(255) not null comment '角色名',
    `description` varchar(100) not null,
    `available`   tinyint(1)   not null,
    `status`      int          not null,
    `create_time` datetime default current_timestamp comment '添加时间',
    `update_time` datetime default current_timestamp comment '更新时间',

    unique index u_idx_name (`name`)
) engine = innodb
  default charset `utf8mb4`;

create table if not exists `tb_serinus_resources`
(
    `id`          int not null primary key auto_increment,
    `name`        varchar(255) default null,
    `type`        varchar(50)  default null,
    `url`         varchar(200) default null,
    `permission`  varchar(100) default null,
    `parent_id`   bigint(20)   default '0',
    `sort`        int(10)      default null,
    `external`    tinyint(1)   default null comment '是否外部链接',
    `available`   tinyint(1)   default '0',
    `icon`        varchar(100) default null comment '菜单图标',
    `create_time` datetime     default current_timestamp comment '添加时间',
    `update_time` datetime     default current_timestamp comment '更新时间',

    index `idx_sys_resource_parent_id` (`parent_id`) using btree
) engine = innodb
  default charset = utf8mb4;

create table if not exists `tb_serinus_role_resources`
(
    `id`           int primary key not null auto_increment,
    `role_id`      int             not null,
    `resources_id` int             not null,
    `status`       int             not null default 0,
    `create_time`  datetime                 default current_timestamp comment '添加时间',
    `update_time`  datetime                 default current_timestamp comment '更新时间',

    foreign key (`role_id`) references `tb_serinus_user` (`id`) on delete cascade on update cascade,
    foreign key (`resources_id`) references `tb_serinus_resources` (`id`) on delete cascade on update cascade

) engine = innodb
  default charset = utf8mb4;

create table if not exists `tb_serinus_user_role`
(
    `id`          int not null primary key auto_increment,
    `user_id`     int not null,
    `role_id`     int not null,
    `status`      int not null default 0,
    `create_time` datetime     default current_timestamp comment '添加时间',
    `update_time` datetime     default current_timestamp comment '更新时间',

    foreign key (`user_id`) references `tb_serinus_user` (`id`) on delete cascade on update cascade,
    foreign key (`role_id`) references `tb_serinus_role` (`id`) on delete cascade on update cascade

) engine = innodb
  default charset = utf8mb4;


# create table if not exists `tb_serinus_permission`
# (
#     `id`               int not null primary key auto_increment,
#     `user_id`          int not null,
#     `role_id`          int not null,
#     `permission_level` int not null default 0,
#     `status`           int not null default 0,
#
#     foreign key (`user_id`) references `tb_serinus_user` (`id`) on delete cascade on update cascade,
#     foreign key (`role_id`) references `tb_serinus_role` (`id`) on delete cascade on update cascade,
#
#     unique index u_idx_user_role_id (`user_id`, `role_id`)
# ) engine = Innodb
#   default charset `utf8mb4`;


-- Business
create table if not exists `tb_serinus_strategy`
(
    `uuid`             varchar(24)  not null primary key,
    `product`          varchar(255) not null,
    `title`            varchar(255) not null,
    `type`             int(2)       not null,
    `preset_type`      int(2)       not null,
    `max_count`        int          not null,
    `start_at`         datetime     not null,
    `end_at`           datetime     not null,
    `filter`           varchar(255) not null,
    `always_return`    int(1)       not null,
    `content`          text         not null,
    `order_in_product` int          not null
) engine = Innodb
  default charset `utf8mb4`;

create table if not exists `tb_strategy_history`
(
    `uuid`          varchar(24)  not null primary key,
    `product`       varchar(255) not null,
    `strategy_uuid` varchar(24)  not null,
    `operator_id`   int          not null,
    `inspector_id`  int          not null,
    `time`          datetime     not null,
    `value_before`  text         not null,
    `value_after`   text         not null,
    `flag`          int          not null
) engine = Innodb
  default charset `utf8mb4`;

create table if not exists `tb_system_process`
(
    `id`         int          not null primary key auto_increment,
    `sponsor_id` int          not null,
    `type`       int          not null,
    `content`    varchar(255) not null,
    `status`     int          not null
) engine = Innodb
  default charset `utf8mb4`;

create table if not exists `tb_strategy_process`
(
    `id`            int          not null primary key auto_increment,
    `product`       varchar(255) not null,
    `strategy_uuid` varchar(24)  not null,
    `type`          int          not null,
    `history_id`    int          not null,
    `status`        int          not null
) engine = Innodb
  default charset `utf8mb4`;

-- FastDFS

create table if not exists `tb_serinus_file_meta`
(
    `id`               int          not null primary key auto_increment,
    `filename`         varchar(255) not null,
    `extension`        varchar(255) not null,
    `md5`              varchar(255) not null,
    `uploader`         varchar(255) not null,
    `group_name`       varchar(255) not null,
    `remote_filename`  varchar(255) not null,
    `source_ip_addr`   varchar(255) not null,
    `file_size`        bigint       not null,
    `create_timestamp` datetime     not null,
    `crc32`            bigint       not null
) engine = Innodb
  default charset `utfmb4`;
