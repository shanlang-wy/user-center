package com.han.usercenter.controller;

import com.han.usercenter.common.BaseResponse;
import com.han.usercenter.common.ErrorCode;
import com.han.usercenter.common.ResultUtils;
import com.han.usercenter.exception.BusinessException;
import com.han.usercenter.model.domain.User;
import com.han.usercenter.service.AuditLogService;
import com.han.usercenter.service.UserService;
import com.han.usercenter.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传接口
 *
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Resource
    private UserService userService;

    @Resource
    private AuditLogService auditLogService;

    /**
     * 头像保存路径
     */
    @Value("${upload.avatar-path:upload/avatar/}")
    private String avatarSavePath;

    /**
     * 头像访问路径前缀
     */
    @Value("${upload.avatar-url-prefix:/upload/avatar/}")
    private String avatarUrlPrefix;

    /**
     * 上传头像
     *
     * @param file
     * @param request
     * @return 头像访问 URL
     */
    @PostMapping("/avatar")
    public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "targetUserId", required = false) Long targetUserId,
                                             HttpServletRequest request) {
        User currentUser = null;
        User targetUser = null;
        boolean adminUpload = false;
        try {
            currentUser = getLoginUserOrNull(request);
            if (targetUserId != null && (currentUser == null || !targetUserId.equals(currentUser.getId()))) {
                if (targetUserId <= 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标用户不存在");
                }
                if (!userService.isAdmin(request)) {
                    throw new BusinessException(ErrorCode.NO_AUTH, "仅管理员可为其他用户上传头像");
                }
                targetUser = userService.getById(targetUserId);
                if (targetUser == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标用户不存在");
                }
                adminUpload = true;
            }
            String avatarUrl = FileUtils.saveFile(file, avatarSavePath, avatarUrlPrefix);
            String fullAvatarUrl = withContextPath(request, avatarUrl);
            auditLogService.record(currentUser, request, "头像上传", "上传头像", "success",
                    adminUpload ? "管理员" : currentUser == null ? "游客" : "本人",
                    adminUpload
                            ? "管理员为用户 " + getTargetUserName(targetUser, targetUserId) + " 上传头像文件：" + fullAvatarUrl
                            : currentUser == null ? "注册前上传头像成功：" + fullAvatarUrl : "上传本人头像成功：" + fullAvatarUrl);
            return ResultUtils.success(fullAvatarUrl);
        } catch (RuntimeException e) {
            auditLogService.record(currentUser, request, "头像上传", "上传头像", "error",
                    adminUpload ? "管理员" : currentUser == null ? "游客" : "本人",
                    adminUpload
                            ? "管理员为用户 " + getTargetUserName(targetUser, targetUserId) + " 上传头像失败：" + getErrorMessage(e)
                            : "上传头像失败：" + getErrorMessage(e));
            throw e;
        }
    }

    private String withContextPath(HttpServletRequest request, String url) {
        String contextPath = request.getContextPath();
        if (!StringUtils.hasText(contextPath) || !StringUtils.hasText(url) || !url.startsWith("/")) {
            return url;
        }
        if (url.equals(contextPath) || url.startsWith(contextPath + "/")) {
            return url;
        }
        return contextPath + url;
    }

    private User getLoginUserOrNull(HttpServletRequest request) {
        try {
            return userService.getCurrentUser(request);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String getErrorMessage(RuntimeException e) {
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            if (org.apache.commons.lang3.StringUtils.isNotBlank(businessException.getDescription())) {
                return businessException.getDescription();
            }
        }
        return org.apache.commons.lang3.StringUtils.defaultIfBlank(e.getMessage(), "未知错误");
    }

    private String getTargetUserName(User targetUser, Long targetUserId) {
        if (targetUser == null) {
            return "ID=" + targetUserId;
        }
        String account = org.apache.commons.lang3.StringUtils.defaultIfBlank(targetUser.getUserAccount(), "未知账号");
        String username = org.apache.commons.lang3.StringUtils.defaultIfBlank(targetUser.getUsername(), "未命名");
        return account + "（" + username + "，ID=" + targetUser.getId() + "）";
    }
}

