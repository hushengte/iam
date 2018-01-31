CREATE TABLE IF NOT EXISTS `iam_role` (
  `id` VARCHAR(32) NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `iam_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `roles` varchar(255) DEFAULT NULL,
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `IK_ig_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `iam_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(50) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `roles` varchar(255) DEFAULT NULL,
  `enabled` smallint(1) DEFAULT '1',
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_iu_username` (`username`),
  KEY `IK_iu_email` (`email`),
  KEY `IK_iu_name` (`name`),
  KEY `IK_iu_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `iam_user_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_iug_group_id` (`group_id`),
  KEY `FK_iug_user_id` (`user_id`),
  CONSTRAINT `FK_iug_group_id` FOREIGN KEY (`group_id`) REFERENCES `iam_group` (`id`),
  CONSTRAINT `FK_iug_user_id` FOREIGN KEY (`user_id`) REFERENCES `iam_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
