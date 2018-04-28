/*
Navicat MariaDB Data Transfer

Source Server         : 10.200.110.97
Source Server Version : 50556
Source Host           : 10.200.110.97:3306
Source Database       : ops_uc

Target Server Type    : MariaDB
Target Server Version : 50556
File Encoding         : 65001

Date: 2018-04-28 10:40:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for uc_authorize_info
-- ----------------------------
DROP TABLE IF EXISTS `uc_authorize_info`;
CREATE TABLE `uc_authorize_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token_type` enum('MAC','BEARER') NOT NULL DEFAULT 'MAC',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `access_token` varchar(128) NOT NULL COMMENT '签名Key',
  `refresh_token` varchar(128) DEFAULT NULL COMMENT '刷新key',
  `expires_time` bigint(20) DEFAULT NULL COMMENT 'token过期时间',
  `sign_key` varchar(128) DEFAULT NULL COMMENT '签名的key',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for uc_permission
-- ----------------------------
DROP TABLE IF EXISTS `uc_permission`;
CREATE TABLE `uc_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限表',
  `code` varchar(50) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_index` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for uc_role
-- ----------------------------
DROP TABLE IF EXISTS `uc_role`;
CREATE TABLE `uc_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色表',
  `name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `code` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_index` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COMMENT='角色信息';

-- ----------------------------
-- Table structure for uc_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `uc_role_permission`;
CREATE TABLE `uc_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色权限表',
  `role_id` bigint(20) DEFAULT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roleId_permissionId_index` (`role_id`,`permission_id`),
  KEY `roleId_index` (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=212 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for uc_user
-- ----------------------------
DROP TABLE IF EXISTS `uc_user`;
CREATE TABLE `uc_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `realm` varchar(50) NOT NULL DEFAULT 'ops.yealink.com',
  `source` enum('INNER','THIRD') NOT NULL DEFAULT 'INNER' COMMENT '账户类型',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱地址',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `status` varchar(1) DEFAULT '' COMMENT '用户状态(0启用,1禁用)',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_index` (`username`) USING BTREE,
  UNIQUE KEY `email_index` (`email`) USING BTREE,
  KEY `username_status_index` (`username`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2939 DEFAULT CHARSET=utf8 COMMENT='用户信息';

-- ----------------------------
-- Table structure for uc_user_group
-- ----------------------------
DROP TABLE IF EXISTS `uc_user_group`;
CREATE TABLE `uc_user_group` (
  `id` bigint(255) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `realm` varchar(50) DEFAULT NULL COMMENT '域值',
  `code` varchar(100) DEFAULT NULL COMMENT '用户组编码',
  `name` varchar(100) DEFAULT NULL COMMENT '用户组名称',
  `guid` varchar(255) DEFAULT NULL,
  `source` enum('INNER','THIRD') NOT NULL DEFAULT 'INNER',
  PRIMARY KEY (`id`),
  UNIQUE KEY `guid_index` (`guid`) USING BTREE,
  UNIQUE KEY `code_index` (`realm`,`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2591 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for uc_user_group_permission
-- ----------------------------
DROP TABLE IF EXISTS `uc_user_group_permission`;
CREATE TABLE `uc_user_group_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户角色表',
  `group_id` bigint(20) DEFAULT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId_roleId_index` (`group_id`,`permission_id`) USING BTREE,
  KEY `userId_index` (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=370 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for uc_user_role
-- ----------------------------
DROP TABLE IF EXISTS `uc_user_role`;
CREATE TABLE `uc_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户角色表',
  `user_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId_roleId_index` (`user_id`,`role_id`),
  KEY `userId_index` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=159 DEFAULT CHARSET=utf8;
