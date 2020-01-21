insert into `iam_role`(`id`,`name`) values ('ADMIN','管理员');
insert into `iam_group`(`id`,`name`,`roles`,`create_time`) values (1,'管理员','ADMIN', NOW());
insert into `iam_user`(`id`,`username`,`create_time`,`email`,`name`,`password`,`roles`,`enabled`,`phone`) values 
  (1,'admin',NOW(),NULL,'Administrator','e10adc3949ba59abbe56e057f20f883e',NULL,1,NULL);
insert into `iam_user_group`(`id`,`group_id`,`user_id`) values (1,1,1);