package com.han.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色更新请求体
 *
 */
@Data
public class UserRoleUpdateRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120796L;

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;
}

