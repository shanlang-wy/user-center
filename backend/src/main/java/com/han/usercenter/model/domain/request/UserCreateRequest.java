package com.han.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员创建用户请求体
 *
 */
@Data
public class UserCreateRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120794L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;
}

