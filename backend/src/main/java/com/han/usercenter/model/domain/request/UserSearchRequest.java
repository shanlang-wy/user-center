package com.han.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户搜索请求体
 *
 */
@Data
public class UserSearchRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120797L;

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 关键词，匹配昵称、账号、电话、邮箱、星球编号
     */
    private String keyword;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户账号
     */
    private String userAccount;

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
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 创建时间开始
     */
    private String createTimeStart;

    /**
     * 创建时间结束
     */
    private String createTimeEnd;

    /**
     * 更新时间开始
     */
    private String updateTimeStart;

    /**
     * 更新时间结束
     */
    private String updateTimeEnd;

    /**
     * ProTable 默认当前页参数
     */
    private Integer current;

    /**
     * 当前页码，默认为 1
     */
    private Integer pageNum = 1;

    /**
     * 每页条数，默认为 10
     */
    private Integer pageSize = 10;
}

