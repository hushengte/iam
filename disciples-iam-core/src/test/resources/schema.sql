CREATE TABLE IF NOT EXISTS `iam_role` (
  `id` VARCHAR(32) NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `iam_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `roles` varchar(255) DEFAULT NULL,
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

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
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `iam_user_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);
