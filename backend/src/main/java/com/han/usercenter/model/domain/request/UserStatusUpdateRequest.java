package com.han.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员更新用户状态请求体
 */
@Data
public class UserStatusUpdateRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120797L;

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 状态 0 - 正常 1 - 封禁
     */
    private Integer userStatus;
}

