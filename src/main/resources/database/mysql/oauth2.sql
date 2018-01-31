CREATE TABLE IF NOT EXISTS `oauth_client_details` (
  `client_id` varchar(128) NOT NULL,
  `resource_ids` varchar(128) DEFAULT NULL,
  `client_secret` varchar(128) DEFAULT NULL,
  `scope` varchar(128) DEFAULT NULL,
  `authorized_grant_types` varchar(128) DEFAULT NULL,
  `web_server_redirect_uri` varchar(128) DEFAULT NULL,
  `authorities` varchar(128) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(255) DEFAULT NULL,
  `autoapprove` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `oauth_access_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` longblob,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  KEY `IK_oat_authentication_id` (`authentication_id`),
  KEY `IK_oat_user_name` (`user_name`),
  KEY `IK_oat_client_id` (`client_id`),
  KEY `IK_oat_refresh_token` (`refresh_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `oauth_refresh_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication` longblob,
  PRIMARY KEY (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `oauth_code` (
  `code` varchar(255) DEFAULT NULL,
  `authentication` longblob,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `oauth_client_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  KEY `IK_oct_authentication_id` (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `oauth_approvals` (
  `userId` varchar(255) DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `expiresAt` timestamp DEFAULT '0000-00-00 00:00:00',
  `lastModifiedAt` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`, `clientId`),
  KEY `IK_oa_scope` (`scope`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;