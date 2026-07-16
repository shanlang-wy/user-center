package com.han.usercenter.model.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Audit log record.
 */
@Data
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String userAccount;

    private String username;

    private String module;

    private String action;

    private String result;

    private String ip;

    private String client;

    private String operator;

    private String detail;

    private Date createTime;
}

