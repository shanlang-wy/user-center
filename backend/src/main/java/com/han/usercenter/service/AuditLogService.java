package com.han.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.usercenter.model.domain.AuditLog;
import com.han.usercenter.model.domain.User;
import com.han.usercenter.model.domain.request.AuditLogQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Audit log service.
 */
@Service
@Slf4j
public class AuditLogService {

    private static final int MAX_PAGE_SIZE = 50;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<AuditLog> auditLogRowMapper = (rs, rowNum) -> {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(rs.getLong("id"));
        auditLog.setUserId(rs.getLong("user_id"));
        auditLog.setUserAccount(rs.getString("user_account"));
        auditLog.setUsername(rs.getString("username"));
        auditLog.setModule(rs.getString("module"));
        auditLog.setAction(rs.getString("action"));
        auditLog.setResult(rs.getString("result"));
        auditLog.setIp(rs.getString("ip"));
        auditLog.setClient(rs.getString("client"));
        auditLog.setOperator(rs.getString("operator"));
        auditLog.setDetail(rs.getString("detail"));
        auditLog.setCreateTime(rs.getTimestamp("create_time"));
        return auditLog;
    };

    @PostConstruct
    public void initTable() {
        jdbcTemplate.execute("create table if not exists audit_log ("
                + "id bigint auto_increment primary key,"
                + "user_id bigint null,"
                + "user_account varchar(256) null,"
                + "username varchar(256) null,"
                + "module varchar(128) not null,"
                + "action varchar(128) not null,"
                + "result varchar(32) not null,"
                + "ip varchar(128) null,"
                + "client varchar(256) null,"
                + "operator varchar(128) null,"
                + "detail varchar(1024) null,"
                + "create_time datetime default CURRENT_TIMESTAMP null,"
                + "index idx_user_account (user_account),"
                + "index idx_module (module),"
                + "index idx_result (result),"
                + "index idx_create_time (create_time)"
                + ") comment 'audit log'");
        ensureColumn("user_id", "bigint null");
        ensureColumn("user_account", "varchar(256) null");
        ensureColumn("username", "varchar(256) null");
        ensureColumn("module", "varchar(128) null");
        ensureColumn("action", "varchar(128) null");
        ensureColumn("result", "varchar(32) null");
        ensureColumn("ip", "varchar(128) null");
        ensureColumn("client", "varchar(256) null");
        ensureColumn("operator", "varchar(128) null");
        ensureColumn("detail", "varchar(1024) null");
        ensureColumn("create_time", "datetime default CURRENT_TIMESTAMP null");
        jdbcTemplate.execute("alter table audit_log convert to character set utf8mb4 collate utf8mb4_unicode_ci");
    }

    public void record(User user, HttpServletRequest request, String module, String action, String result,
                       String operator, String detail) {
        Long userId = user == null ? null : user.getId();
        String userAccount = user == null ? null : user.getUserAccount();
        String username = user == null ? null : user.getUsername();
        record(userId, userAccount, username, request, module, action, result, operator, detail);
    }

    public void record(Long userId, String userAccount, String username, HttpServletRequest request, String module,
                       String action, String result, String operator, String detail) {
        try {
            jdbcTemplate.update("insert into audit_log "
                            + "(user_id, user_account, username, module, action, result, ip, client, operator, detail) "
                            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    userId, safeText(userAccount, 256), safeText(username, 256), safeText(module, 128),
                    safeText(action, 128), safeText(result, 32), safeText(getClientIp(request), 128),
                    safeText(getClient(request), 256), safeText(operator, 128), safeText(detail, 1024));
        } catch (Exception e) {
            log.error("write audit log failed, userAccount={}, module={}, action={}, result={}",
                    userAccount, module, action, result, e);
        }
    }

    private void ensureColumn(String column, String definition) {
        Long count = jdbcTemplate.queryForObject("select count(*) from information_schema.columns "
                        + "where table_schema = database() and table_name = 'audit_log' and column_name = ?",
                new Object[]{column}, Long.class);
        if (count == null || count == 0) {
            jdbcTemplate.execute("alter table audit_log add column " + column + " " + definition);
        }
    }

    private String safeText(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    /**
     * 根据 ID 删除单条审计记录
     *
     * @param id 记录 ID
     * @return 影响的行数
     */
    public int deleteById(Long id) {
        if (id == null || id <= 0) {
            return 0;
        }
        return jdbcTemplate.update("delete from audit_log where id = ?", id);
    }

    /**
     * 清空所有审计记录
     *
     * @return 影响的行数
     */
    public int clear() {
        return jdbcTemplate.update("delete from audit_log");
    }

    public Page<AuditLog> search(AuditLogQueryRequest queryRequest) {
        if (queryRequest == null) {
            queryRequest = new AuditLogQueryRequest();
        }
        StringBuilder where = new StringBuilder(" where 1 = 1");
        List<Object> params = new ArrayList<>();
        if (queryRequest.getId() != null && queryRequest.getId() > 0) {
            where.append(" and id = ?");
            params.add(queryRequest.getId());
        }
        if (StringUtils.isNotBlank(queryRequest.getKeyword())) {
            where.append(" and (user_account like ? or username like ? or module like ? or action like ? or detail like ?)");
            String keyword = "%" + queryRequest.getKeyword() + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }
        appendLike(where, params, "user_account", queryRequest.getUserAccount());
        appendLike(where, params, "username", queryRequest.getUsername());
        appendLike(where, params, "action", queryRequest.getAction());
        appendLike(where, params, "ip", queryRequest.getIp());
        appendEquals(where, params, "module", queryRequest.getModule());
        appendEquals(where, params, "result", queryRequest.getResult());
        appendEquals(where, params, "operator", queryRequest.getOperator());
        if (StringUtils.isNotBlank(queryRequest.getCreateTimeStart())) {
            where.append(" and create_time >= ?");
            params.add(queryRequest.getCreateTimeStart());
        }
        if (StringUtils.isNotBlank(queryRequest.getCreateTimeEnd())) {
            where.append(" and create_time <= ?");
            params.add(queryRequest.getCreateTimeEnd());
        }

        int current = queryRequest.getCurrent() != null && queryRequest.getCurrent() > 0 ? queryRequest.getCurrent() : 1;
        int pageSize = queryRequest.getPageSize() != null && queryRequest.getPageSize() > 0 ? queryRequest.getPageSize() : 10;
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        long total = jdbcTemplate.queryForObject("select count(*) from audit_log" + where, params.toArray(), Long.class);
        List<Object> listParams = new ArrayList<>(params);
        listParams.add(pageSize);
        listParams.add((current - 1) * pageSize);
        List<AuditLog> records = jdbcTemplate.query("select * from audit_log" + where
                + " order by id desc limit ? offset ?", listParams.toArray(), auditLogRowMapper);
        Page<AuditLog> page = new Page<>(current, pageSize);
        page.setTotal(total);
        page.setRecords(records);
        return page;
    }

    private void appendLike(StringBuilder where, List<Object> params, String column, String value) {
        if (StringUtils.isNotBlank(value)) {
            where.append(" and ").append(column).append(" like ?");
            params.add("%" + value + "%");
        }
    }

    private void appendEquals(StringBuilder where, List<Object> params, String column, String value) {
        if (StringUtils.isNotBlank(value)) {
            where.append(" and ").append(column).append(" = ?");
            params.add(value);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(realIp)) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

    private String getClient(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isBlank(userAgent)) {
            return "";
        }
        String browser = userAgent.contains("Edg") ? "Edge"
                : userAgent.contains("Chrome") ? "Chrome"
                : userAgent.contains("Firefox") ? "Firefox"
                : userAgent.contains("Safari") ? "Safari" : "Unknown";
        String os = userAgent.contains("Windows") ? "Windows"
                : userAgent.contains("Mac OS") ? "macOS"
                : userAgent.contains("Linux") ? "Linux" : "Unknown";
        return browser + " / " + os;
    }
}

