CREATE TABLE `iam_user` (
  `id` bigint(20) NOT NULL,
  `username` varchar(32) NOT NULL,
  `password` varchar(128) NOT NULL,
  `nickname` varchar(40) DEFAULT '',
  `email` varchar(60) DEFAULT '',
  `phone` varchar(20) DEFAULT '',
  `status` varchar(10) DEFAULT '',
  `created_by` varchar(30) DEFAULT '',
  `created_at` datetime DEFAULT NULL,
  `updated_by` varchar(30) DEFAULT '',
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
);

CREATE TABLE `iam_group` (
  `id` bigint(20) NOT NULL,
  `name` varchar(20) NOT NULL,
  `roles` varchar(200) DEFAULT NULL,
  `created_by` varchar(30) DEFAULT '',
  `created_at` datetime DEFAULT NULL,
  `updated_by` varchar(30) DEFAULT '',
  `updated_at` datetime DEFAULT NULL,
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
);

CREATE TABLE `iam_user_group` (
  `id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `created_by` varchar(30) DEFAULT '',
  `created_at` datetime DEFAULT NULL,
  `updated_by` varchar(30) DEFAULT '',
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_user_group_id` (`user_id`, `group_id`),
  KEY `idx_group_id` (`group_id`)
);