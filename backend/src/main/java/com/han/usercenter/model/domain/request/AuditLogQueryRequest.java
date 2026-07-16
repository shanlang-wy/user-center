package com.han.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Audit log query request.
 */
@Data
public class AuditLogQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String keyword;

    private String userAccount;

    private String username;

    private String module;

    private String action;

    private String result;

    private String ip;

    private String operator;

    private String createTimeStart;

    private String createTimeEnd;

    private Integer current = 1;

    private Integer pageSize = 10;
}

