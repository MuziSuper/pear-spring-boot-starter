CREATE DATABASE IF NOT EXISTS pear DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pear;


-- Config 表
CREATE TABLE IF NOT EXISTS `config` (
    `id` BIGINT NOT NULL,
    `key` VARCHAR(128),
    `description` VARCHAR(200),
    `autoload` TINYINT(1),
    `pub` TINYINT(1),
    `format` VARCHAR(20),
    `value` TEXT,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Group 表（group 是 MySQL 保留字，所以加反引号）
CREATE TABLE IF NOT EXISTS `group` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(200),
    `type` VARCHAR(24),
    `extra` TEXT,
    `permission` BLOB,
    `gmt_created` DATETIME,
    `gmt_modified` DATETIME,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- GroupExtra 表
CREATE TABLE IF NOT EXISTS `group_extra` (
    `id` BIGINT NOT NULL,
    `group_type` VARCHAR(128),
    `group_id` BIGINT,
    `key` VARCHAR(128),
    `value` TEXT,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- GroupMember 表
CREATE TABLE IF NOT EXISTS `group_member` (
    `id` BIGINT NOT NULL,
    `user_id` BIGINT,
    `group_id` BIGINT,
    `role` VARCHAR(100),
    `description` TEXT,
    `gmt_created` DATETIME,
    `gmt_modified` DATETIME,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Product 表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(200),
    `group_id` BIGINT,
    `enabled` TINYINT(1),
    `model` BLOB,
    `gmt_created` DATETIME,
    `gmt_modified` DATETIME,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User 表（user 是 MySQL 保留字）
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL,
    `avatar` VARCHAR(255),
    `email` VARCHAR(128),
    `password` VARCHAR(128),
    `phone` VARCHAR(64),
    `first_name` VARCHAR(128),
    `last_name` VARCHAR(128),
    `display_name` VARCHAR(128),
    `is_superUser` TINYINT(1),
    `is_staff` TINYINT(1),
    `enabled` TINYINT(1),
    `activated` TINYINT(1),
    `last_login` DATETIME,
    `last_login_ip` VARCHAR(128),
    `source` VARCHAR(64),
    `locale` VARCHAR(20),
    `timezone` VARCHAR(200),
    `profile` TEXT,
    `token` TEXT,
    `gmt_created` DATETIME,
    `gmt_modified` DATETIME,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
