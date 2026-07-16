package com.han.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.usercenter.common.BaseResponse;
import com.han.usercenter.common.ErrorCode;
import com.han.usercenter.common.ResultUtils;
import com.han.usercenter.exception.BusinessException;
import com.han.usercenter.model.domain.AuditLog;
import com.han.usercenter.model.domain.request.AuditLogQueryRequest;
import com.han.usercenter.service.AuditLogService;
import com.han.usercenter.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Audit log API.
 */
@RestController
@RequestMapping("/audit")
public class AuditLogController {

    @Resource
    private AuditLogService auditLogService;

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public BaseResponse<Page<AuditLog>> listAuditLogs(AuditLogQueryRequest auditLogQueryRequest,
                                                      HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return ResultUtils.success(auditLogService.search(auditLogQueryRequest));
    }

    /**
     * 删除单条审计记录
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAuditLog(@RequestParam("id") Long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "记录 ID 非法");
        }
        int result = auditLogService.deleteById(id);
        if (result > 0) {
            auditLogService.record(userService.getCurrentUser(request), request, "访问审计", "删除审计记录",
                    "success", "管理员", "删除审计记录 ID：" + id);
        }
        return ResultUtils.success(result > 0);
    }

    /**
     * 批量删除审计记录
     */
    @PostMapping("/delete/batch")
    public BaseResponse<Boolean> batchDeleteAuditLog(@RequestBody List<Long> ids, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择要删除的记录");
        }
        int total = 0;
        for (Long id : ids) {
            total += auditLogService.deleteById(id);
        }
        if (total > 0) {
            auditLogService.record(userService.getCurrentUser(request), request, "访问审计", "批量删除审计记录",
                    "success", "管理员", "批量删除审计记录数量：" + total);
        }
        return ResultUtils.success(total > 0);
    }

    /**
     * 清空所有审计记录
     */
    @PostMapping("/clear")
    public BaseResponse<Boolean> clearAuditLogs(HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        int result = auditLogService.clear();
        auditLogService.record(userService.getCurrentUser(request), request, "访问审计", "清空审计记录",
                "warning", "管理员", "清空审计记录数量：" + result);
        return ResultUtils.success(result >= 0);
    }
}

