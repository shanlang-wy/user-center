# 数据库初始化

-- 创建库
create database if not exists user_center;

-- 切换库
use user_center;

# 用户表
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常 1 - 封禁',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512)                       null comment '星球编号',
    unique key uk_user_account (userAccount),
    unique key uk_planet_code (planetCode)
)
    comment '用户';

-- 查看当前表结构
SHOW FULL COLUMNS FROM user_center.user;

-- 修改表和列字符集
ALTER TABLE user_center.user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

