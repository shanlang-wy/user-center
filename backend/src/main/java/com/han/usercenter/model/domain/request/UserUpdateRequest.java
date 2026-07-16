package com.han.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员更新用户信息请求体
 *
 */
@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120795L;

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码（为空表示不修改）
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
     * 状态 0 - 正常
     */
    private Integer userStatus;
}

