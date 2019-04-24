create database if not exists `db_serinus_system` default character set utf8mb4 collate utf8mb4_unicode_ci;

create table if not exists `tb_serinus_user`
(
    `id`         int          not null primary key auto_increment,
    `principal`  varchar(255) not null,
    `credential` varchar(255) not null,
    `status`     int          not null default 0,
    `name`       varchar(255) not null,
    `email`      varchar(255) not null,

    unique index u_idx_principal (`principal`)
) engine = Innodb
  default charset `utf8mb4`;

create table if not exists `tb_serinus_role`
(
    `id`     int          not null primary key auto_increment,
    `name`   varchar(255) not null,
    `status` int          not null default 0,

    unique index u_idx_name (`name`)
) engine = Innodb
  default charset `utf8mb4`;

create table if not exists `tb_serinus_permission`
(
    `id`               int not null primary key auto_increment,
    `user_id`          int not null,
    `role_id`          int not null,
    `permission_level` int not null default 0,
    `status`           int not null default 0,

    foreign key (`user_id`) references `tb_serinus_user` (`id`) on delete cascade on update cascade,
    foreign key (`role_id`) references `tb_serinus_role` (`id`) on delete cascade on update cascade,

    unique index u_idx_user_role_id (`user_id`, `role_id`)
) engine = Innodb
  default charset `utf8mb4`;

create table if not exists `tb_serinus_strategy`
(
    `id` int primary key
) engine = Innodb
  default charset `utf8mb4`;