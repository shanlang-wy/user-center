package com.han.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Current user password update request.
 */
@Data
public class UserPasswordUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String oldPassword;

    private String newPassword;

    private String checkPassword;
}

