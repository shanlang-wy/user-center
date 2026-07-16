package com.han.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.han.usercenter.model.domain.User;
import com.han.usercenter.model.domain.request.UserCreateRequest;
import com.han.usercenter.model.domain.request.UserPasswordUpdateRequest;
import com.han.usercenter.model.domain.request.UserRegisterRequest;
import com.han.usercenter.model.domain.request.UserRoleUpdateRequest;
import com.han.usercenter.model.domain.request.UserSearchRequest;
import com.han.usercenter.model.domain.request.UserStatusUpdateRequest;
import com.han.usercenter.model.domain.request.UserUpdateRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册信息
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 管理员创建用户
     *
     * @param userCreateRequest
     * @param request
     * @return 新用户 id
     */
    long userCreate(UserCreateRequest userCreateRequest, HttpServletRequest request);

    /**
     * 管理员更新用户信息
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    boolean userUpdate(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    /**
     * 更新用户角色
     *
     * @param userRoleUpdateRequest
     * @param request
     * @return
     */
    boolean updateUserRole(UserRoleUpdateRequest userRoleUpdateRequest, HttpServletRequest request);

    /**
     * 更新用户状态
     *
     * @param userStatusUpdateRequest
     * @param request
     * @return
     */
    boolean updateUserStatus(UserStatusUpdateRequest userStatusUpdateRequest, HttpServletRequest request);

    /**
     * 修改当前用户密码
     *
     * @param userPasswordUpdateRequest
     * @param request
     * @return
     */
    boolean updateMyPassword(UserPasswordUpdateRequest userPasswordUpdateRequest, HttpServletRequest request);

    /**
     * 根据条件分页搜索用户
     *
     * @param userSearchRequest
     * @param request
     * @return
     */
    Page<User> searchUsers(UserSearchRequest userSearchRequest, HttpServletRequest request);
}

