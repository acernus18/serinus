insert into tb_serinus_role
values (null, 'lispon_admin', 'test', 0, 0, current_timestamp(), current_timestamp());

insert into tb_serinus_resources
values (null, 'name', 'type', '/strategy/operator/lispon/', 'permission', 0, 0, 0, 0, 'http://hello', current_timestamp(),
        current_timestamp());

insert into tb_serinus_role_resources values (null, 10, 8, 0, current_timestamp(), current_timestamp());