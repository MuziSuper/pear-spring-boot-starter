CREATE DATABASE IF NOT EXISTS pear DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pear;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `autoload` bit(1) DEFAULT NULL,
                          `description` varchar(200) DEFAULT NULL,
                          `format` varchar(20) DEFAULT NULL,
                          `key` varchar(128) DEFAULT NULL,
                          `pub` bit(1) DEFAULT NULL,
                          `value` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1965701366474133506 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `extra` varchar(255) DEFAULT NULL,
                         `gmt_created` datetime(6) DEFAULT NULL,
                         `gmt_modified` datetime(6) DEFAULT NULL,
                         `name` varchar(200) DEFAULT NULL,
                         `permission` varbinary(255) DEFAULT NULL,
                         `type` varchar(24) DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for group_extra
-- ----------------------------
DROP TABLE IF EXISTS `group_extra`;
CREATE TABLE `group_extra` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `group_id` bigint DEFAULT NULL,
                               `group_type` varchar(128) DEFAULT NULL,
                               `key` varchar(128) DEFAULT NULL,
                               `value` varchar(255) DEFAULT NULL,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for group_member
-- ----------------------------
DROP TABLE IF EXISTS `group_member`;
CREATE TABLE `group_member` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `description` varchar(255) DEFAULT NULL,
                                `gmt_created` datetime(6) DEFAULT NULL,
                                `gmt_modified` datetime(6) DEFAULT NULL,
                                `group_id` bigint DEFAULT NULL,
                                `role` varchar(100) DEFAULT NULL,
                                `user_id` bigint DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
                           `id` bigint NOT NULL,
                           `enabled` bit(1) DEFAULT NULL,
                           `gmt_created` datetime(6) DEFAULT NULL,
                           `gmt_modified` datetime(6) DEFAULT NULL,
                           `group_id` bigint DEFAULT NULL,
                           `model` varbinary(255) DEFAULT NULL,
                           `name` varchar(200) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `activated` bit(1) DEFAULT NULL,
                        `avatar` varchar(255) DEFAULT NULL,
                        `display_name` varchar(128) DEFAULT NULL,
                        `email` varchar(128) DEFAULT NULL,
                        `enabled` bit(1) DEFAULT NULL,
                        `first_name` varchar(128) DEFAULT NULL,
                        `gmt_created` datetime(6) DEFAULT NULL,
                        `gmt_modified` datetime(6) DEFAULT NULL,
                        `last_login` datetime(6) DEFAULT NULL,
                        `last_login_ip` varchar(128) DEFAULT NULL,
                        `last_name` varchar(128) DEFAULT NULL,
                        `locale` varchar(20) DEFAULT NULL,
                        `password` varchar(128) DEFAULT NULL,
                        `phone` varchar(64) DEFAULT NULL,
                        `profile` varchar(255) DEFAULT NULL,
                        `role` tinyint DEFAULT NULL,
                        `source` varchar(64) DEFAULT NULL,
                        `timezone` varchar(200) DEFAULT NULL,
                        `token` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
