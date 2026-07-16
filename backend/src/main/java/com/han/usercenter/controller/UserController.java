package com.han.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.usercenter.common.BaseResponse;
import com.han.usercenter.common.ErrorCode;
import com.han.usercenter.common.ResultUtils;
import com.han.usercenter.exception.BusinessException;
import com.han.usercenter.model.domain.User;
import com.han.usercenter.model.domain.request.*;
import com.han.usercenter.service.AuditLogService;
import com.han.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AuditLogService auditLogService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest,
                                           HttpServletRequest request) {
        String userAccount = userRegisterRequest == null ? null : userRegisterRequest.getUserAccount();
        try {
            // 校验
            if (userRegisterRequest == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            String userPassword = userRegisterRequest.getUserPassword();
            String checkPassword = userRegisterRequest.getCheckPassword();
            String planetCode = userRegisterRequest.getPlanetCode();
            String username = userRegisterRequest.getUsername();
            if (StringUtils.isAnyBlank(userAccount, username, userPassword, checkPassword, planetCode)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
            }
            long result = userService.userRegister(userRegisterRequest);
            auditLogService.record(result, userAccount, null, request, "登录认证", "用户注册", "success",
                    "本人", "用户注册成功，用户 ID：" + result);
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            auditLogService.record(null, userAccount, null, request, "登录认证", "用户注册", "error",
                    "本人", "注册失败：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(null, userAccount, null, request, "登录认证", "用户注册", "error",
                    "系统", "注册异常：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest == null ? null : userLoginRequest.getUserAccount();
        if (userLoginRequest == null) {
            auditLogService.record(null, null, null, request, "登录认证", "登录失败", "error", "系统",
                    "登录失败：请求参数为空");
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            auditLogService.record(null, userAccount, null, request, "登录认证", "登录失败", "error", "系统",
                    "登录失败：账号或密码为空");
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        try {
            User user = userService.userLogin(userAccount, userPassword, request);
            auditLogService.record(user, request, "登录认证", "账号密码登录", "success", "本人", "登录成功");
            return ResultUtils.success(user);
        } catch (BusinessException e) {
            auditLogService.record(null, userAccount, null, request, "登录认证", "登录失败", "error", "系统",
                    "登录失败：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(null, userAccount, null, request, "登录认证", "登录失败", "error", "系统",
                    "登录异常：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        User currentUser = getLoginUserOrNull(request);
        try {
            if (request == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (currentUser == null) {
                currentUser = userService.getCurrentUser(request);
            }
            int result = userService.userLogout(request);
            auditLogService.record(currentUser, request, "登录认证", "退出登录", "success", "本人", "用户退出登录");
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            auditLogService.record(currentUser, request, "登录认证", "退出登录", "error", "本人",
                    "退出登录失败：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = userService.getCurrentUser(request);
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 管理员创建用户
     *
     * @param userCreateRequest
     * @param request
     * @return
     */
    @PostMapping("/create")
    public BaseResponse<Long> userCreate(@RequestBody UserCreateRequest userCreateRequest, HttpServletRequest request) {
        try {
            if (userCreateRequest == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            long result = userService.userCreate(userCreateRequest, request);
            auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "创建用户", "success",
                    "管理员", "创建用户：" + userCreateRequest.getUserAccount());
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "创建用户", "error",
                    "管理员", "创建用户失败：" + getTargetAccount(userCreateRequest) + "，原因：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "创建用户", "error",
                    "系统", "创建用户异常：" + getTargetAccount(userCreateRequest) + "，原因：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 管理员更新用户信息
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> userUpdate(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        try {
            if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            boolean result = userService.userUpdate(userUpdateRequest, request);
            if (result) {
                auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "编辑用户信息",
                        "success", "管理员", "更新用户 ID：" + userUpdateRequest.getId());
            }
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "编辑用户信息", "error",
                    "管理员", "更新用户失败：ID=" + getTargetId(userUpdateRequest) + "，原因：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "编辑用户信息", "error",
                    "系统", "更新用户异常：ID=" + getTargetId(userUpdateRequest) + "，原因：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 更新用户角色（权限）
     *
     * @param userRoleUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/role")
    public BaseResponse<Boolean> updateUserRole(@RequestBody UserRoleUpdateRequest userRoleUpdateRequest, HttpServletRequest request) {
        try {
            if (userRoleUpdateRequest == null || userRoleUpdateRequest.getId() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            boolean result = userService.updateUserRole(userRoleUpdateRequest, request);
            if (result) {
                auditLogService.record(getLoginUserOrNull(request), request, "权限管理", "更改用户角色",
                        "success", "管理员", "更新用户 ID：" + userRoleUpdateRequest.getId()
                                + " 的角色为：" + userRoleUpdateRequest.getUserRole());
            }
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "权限管理", "更改用户角色", "error",
                    "管理员", "更改角色失败：ID=" + getTargetId(userRoleUpdateRequest) + "，原因：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "权限管理", "更改用户角色", "error",
                    "系统", "更改角色异常：ID=" + getTargetId(userRoleUpdateRequest) + "，原因：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 更新用户状态（封禁 / 解封）
     *
     * @param userStatusUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/status")
    public BaseResponse<Boolean> updateUserStatus(@RequestBody UserStatusUpdateRequest userStatusUpdateRequest,
                                                  HttpServletRequest request) {
        try {
            if (userStatusUpdateRequest == null || userStatusUpdateRequest.getId() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            boolean result = userService.updateUserStatus(userStatusUpdateRequest, request);
            if (result) {
                auditLogService.record(getLoginUserOrNull(request), request, "权限管理", "更改用户状态",
                        "success", "管理员", "更新用户 ID：" + userStatusUpdateRequest.getId()
                                + " 的状态为：" + userStatusUpdateRequest.getUserStatus());
            }
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "权限管理", "更改用户状态", "error",
                    "管理员", "更改状态失败：ID=" + getTargetId(userStatusUpdateRequest) + "，原因：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "权限管理", "更改用户状态", "error",
                    "系统", "更改状态异常：ID=" + getTargetId(userStatusUpdateRequest) + "，原因：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 根据条件分页搜索用户
     *
     * @param userSearchRequest
     * @param request
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<Page<User>> searchUsers(UserSearchRequest userSearchRequest, HttpServletRequest request) {
        Page<User> userPage = userService.searchUsers(userSearchRequest, request);
        // 对用户列表进行脱敏
        List<User> safetyUserList = userPage.getRecords().stream()
                .map(user -> userService.getSafetyUser(user))
                .collect(Collectors.toList());
        userPage.setRecords(safetyUserList);
        return ResultUtils.success(userPage);
    }

    /**
     * 管理员删除用户
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        try {
            if (!userService.isAdmin(request)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            if (id == null || id <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            User deletedUser = userService.getById(id);
            boolean result = userService.removeById(id);
            if (result) {
                auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "删除用户",
                        "success", "管理员", "删除用户：" + (deletedUser == null ? id : deletedUser.getUserAccount()));
            }
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "删除用户", "error",
                    "管理员", "删除用户失败：ID=" + id + "，原因：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(getLoginUserOrNull(request), request, "用户资料", "删除用户", "error",
                    "系统", "删除用户异常：ID=" + id + "，原因：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 更新当前用户资料
     */
    @PostMapping("/profile")
    public BaseResponse<User> updateMyProfile(@RequestBody UserProfileUpdateRequest userProfileUpdateRequest,
                                              HttpServletRequest request) {
        User currentUser = getLoginUserOrNull(request);
        try {
            if (userProfileUpdateRequest == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (currentUser == null) {
                currentUser = userService.getCurrentUser(request);
            }
            User updateUser = new User();
            updateUser.setId(currentUser.getId());
            updateUser.setUsername(userProfileUpdateRequest.getUsername());
            updateUser.setAvatarUrl(userProfileUpdateRequest.getAvatarUrl());
            updateUser.setGender(userProfileUpdateRequest.getGender());
            updateUser.setPhone(userProfileUpdateRequest.getPhone());
            updateUser.setEmail(userProfileUpdateRequest.getEmail());
            boolean result = userService.updateById(updateUser);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新个人资料失败");
            }
            User newUser = userService.getSafetyUser(userService.getById(currentUser.getId()));
            request.getSession().setAttribute(com.han.usercenter.contant.UserConstant.USER_LOGIN_STATE, newUser);
            auditLogService.record(newUser, request, "个人设置", "更新个人资料", "success", "本人", "用户更新个人资料");
            return ResultUtils.success(newUser);
        } catch (BusinessException e) {
            auditLogService.record(currentUser, request, "个人设置", "更新个人资料", "error", "本人",
                    "更新个人资料失败：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(currentUser, request, "个人设置", "更新个人资料", "error", "系统",
                    "更新个人资料异常：" + getErrorMessage(e));
            throw e;
        }
    }

    /**
     * 更新当前用户密码
     */
    @PostMapping("/password")
    public BaseResponse<Boolean> updateMyPassword(@RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest,
                                                  HttpServletRequest request) {
        User currentUser = getLoginUserOrNull(request);
        try {
            if (userPasswordUpdateRequest == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            boolean result = userService.updateMyPassword(userPasswordUpdateRequest, request);
            if (result) {
                auditLogService.record(getLoginUserOrNull(request), request, "个人设置", "修改密码",
                        "success", "本人", "用户修改登录密码");
            }
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            auditLogService.record(currentUser, request, "个人设置", "修改密码", "error", "本人",
                    "修改密码失败：" + getErrorMessage(e));
            throw e;
        } catch (RuntimeException e) {
            auditLogService.record(currentUser, request, "个人设置", "修改密码", "error", "系统",
                    "修改密码异常：" + getErrorMessage(e));
            throw e;
        }
    }

    private User getLoginUserOrNull(HttpServletRequest request) {
        try {
            if (request == null) {
                return null;
            }
            return userService.getCurrentUser(request);
        } catch (Exception e) {
            return null;
        }
    }

    private String getErrorMessage(Exception e) {
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            if (StringUtils.isNotBlank(businessException.getDescription())) {
                return businessException.getDescription();
            }
        }
        return StringUtils.defaultIfBlank(e.getMessage(), "未知错误");
    }

    private String getTargetAccount(UserCreateRequest request) {
        return request == null ? "未知账号" : StringUtils.defaultIfBlank(request.getUserAccount(), "未知账号");
    }

    private String getTargetId(UserUpdateRequest request) {
        return request == null || request.getId() == null ? "未知" : String.valueOf(request.getId());
    }

    private String getTargetId(UserRoleUpdateRequest request) {
        return request == null || request.getId() == null ? "未知" : String.valueOf(request.getId());
    }

    private String getTargetId(UserStatusUpdateRequest request) {
        return request == null || request.getId() == null ? "未知" : String.valueOf(request.getId());
    }
}

