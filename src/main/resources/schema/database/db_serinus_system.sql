create database if not exists `db_serinus_system` default character set utf8mb4 collate utf8mb4_unicode_ci;

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

create table `tb_serinus_config`
(
    `id`            int unsigned        not null auto_increment comment 'config id',
    `c_key`         varchar(64)         not null default '' comment '键值',
    `c_md5`         varchar(64)         not null default '' comment '键值hash',
    `value`         mediumtext          not null comment '內容',
    `value_history` mediumtext          not null comment '內容history',
    `json_value`    text                not null comment 'json format',
    `update_time`   int(11) unsigned    not null default '0' comment '最后更新时间',
    `create_time`   int(11) unsigned    not null default '0' comment '创建时间',
    `op_uid`        int(11) unsigned    not null default '0' comment '最后修改者id',
    `extra`         varchar(1024)       not null default '' comment '通用扩展信息',
    `type`          tinyint(2) unsigned not null default '0' comment '内容格式类型, 0:ini, 1:json, 2:policy, 3:bd',
    `note`          varchar(128)        not null comment '注释',
    `status`        tinyint(2) unsigned not null default '0' comment '状态值',

    primary key (`id`),
    unique key `u_idx_c_key` (`c_key`)
) engine = InnoDB
  default charset = utf8mb4;

create table `tb_serinus_product`
(
    `id`           int unsigned        not null auto_increment,
    `belong_to`    varchar(255)        not null default 'all',
    `product_name` varchar(255)        not null,
    `status`       tinyint(2) unsigned not null default '0',
    `create_time`  datetime                     default current_timestamp comment '注册时间',
    `update_time`  datetime                     default current_timestamp comment '更新时间',

    primary key (`id`),
    unique key `u_idx_name` (`product_name`)
) engine = InnoDB
  default charset = utf8mb4;
