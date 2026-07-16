package com.han.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Current user profile update request.
 */
@Data
public class UserProfileUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String avatarUrl;

    private Integer gender;

    private String phone;

    private String email;
}

