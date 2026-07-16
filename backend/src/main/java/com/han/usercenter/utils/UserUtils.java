package com.han.usercenter.utils;

import com.han.usercenter.common.ErrorCode;
import com.han.usercenter.exception.BusinessException;
import com.han.usercenter.model.domain.User;

import javax.servlet.http.HttpServletRequest;

import static com.han.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户工具类
 *
 */
public class UserUtils {

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    public static User getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    public static boolean isAdmin(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && Integer.valueOf(1).equals(user.getUserRole());
    }
}

