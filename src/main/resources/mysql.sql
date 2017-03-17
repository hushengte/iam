DROP TABLE IF EXISTS `iam_user_group`;
DROP TABLE IF EXISTS `iam_user`;
DROP TABLE IF EXISTS `iam_group`;
DROP TABLE IF EXISTS `iam_role`;

CREATE TABLE `iam_role` (
  `id` VARCHAR(32) NOT NULL,
  `name` VARCHAR(20) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `iam_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `roles` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `iam_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `roles` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  `phone` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `iam_user_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_id` int(11) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsypry2vx0fciu5nobfl0af1wa` (`group_id`),
  KEY `FKm89o42dxdqg07ervh128jvewp` (`user_id`),
  CONSTRAINT `FKm89o42dxdqg07ervh128jvewp` FOREIGN KEY (`user_id`) REFERENCES `iam_user` (`id`),
  CONSTRAINT `FKsypry2vx0fciu5nobfl0af1wa` FOREIGN KEY (`group_id`) REFERENCES `iam_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

insert  into `iam_role`(`id`,`name`) values ('ADMIN','管理员');
insert  into `iam_group`(`id`,`name`,`roles`,`create_time`) values (1,'管理员','ADMIN', NOW());
insert  into `iam_user`(`id`,`username`,`create_time`,`email`,`name`,`password`,`roles`,`enabled`,`phone`) values 
(1,'admin',NOW(),NULL,'Administrator','e10adc3949ba59abbe56e057f20f883e',NULL,1,NULL);
insert  into `iam_user_group`(`id`,`group_id`,`user_id`) values (1,1,1);