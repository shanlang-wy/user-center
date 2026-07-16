package com.han.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.usercenter.common.ErrorCode;
import com.han.usercenter.exception.BusinessException;
import com.han.usercenter.mapper.UserMapper;
import com.han.usercenter.model.domain.User;
import com.han.usercenter.model.domain.request.UserCreateRequest;
import com.han.usercenter.model.domain.request.UserPasswordUpdateRequest;
import com.han.usercenter.model.domain.request.UserRegisterRequest;
import com.han.usercenter.model.domain.request.UserRoleUpdateRequest;
import com.han.usercenter.model.domain.request.UserSearchRequest;
import com.han.usercenter.model.domain.request.UserStatusUpdateRequest;
import com.han.usercenter.model.domain.request.UserUpdateRequest;
import com.han.usercenter.service.UserService;
import com.han.usercenter.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.han.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "han-user-center";

    /**
     * 用户账号最小长度
     */
    private static final int MIN_ACCOUNT_LENGTH = 3;

    /**
     * 用户密码最小长度
     */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * 默认普通用户
     */
    private static final int DEFAULT_USER_ROLE = 0;

    /**
     * 默认正常状态
     */
    private static final int DEFAULT_USER_STATUS = 0;

    /**
     * 最大分页大小
     */
    private static final int MAX_PAGE_SIZE = 50;

    /**
     * 账户特殊字符正则
     */
    private static final String VALID_PATTERN = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUsername(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        userRegisterRequest.setPlanetCode(planetCode);
        return userRegister(userRegisterRequest);
    }

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = StringUtils.trim(userRegisterRequest.getUserAccount());
        String username = StringUtils.trim(userRegisterRequest.getUsername());
        String userPassword = StringUtils.trim(userRegisterRequest.getUserPassword());
        String checkPassword = StringUtils.trim(userRegisterRequest.getCheckPassword());
        String planetCode = StringUtils.trim(userRegisterRequest.getPlanetCode());
        if (StringUtils.isAnyBlank(userAccount, username, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        validateAccount(userAccount);
        if (userPassword.length() < MIN_PASSWORD_LENGTH || checkPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        validateGender(userRegisterRequest.getGender());
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        ensureUserAccountNotExists(userAccount);
        ensurePlanetCodeNotExists(planetCode, null);
        String encryptPassword = encryptPassword(userPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUsername(username);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        user.setAvatarUrl(userRegisterRequest.getAvatarUrl());
        user.setGender(userRegisterRequest.getGender());
        user.setPhone(userRegisterRequest.getPhone());
        user.setEmail(userRegisterRequest.getEmail());
        user.setUserRole(DEFAULT_USER_ROLE);
        user.setUserStatus(DEFAULT_USER_STATUS);
        try {
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或编号已存在");
        }
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 去除首尾空格，避免前端或用户误输入空格导致登录失败
        if (userAccount != null) {
            userAccount = userAccount.trim();
        }
        if (userPassword != null) {
            userPassword = userPassword.trim();
        }
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        validateAccount(userAccount);
        if (userPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 2. 加密
        String encryptPassword = encryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount does not exist");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        if (!encryptPassword.equals(user.getUserPassword())) {
            log.info("user login failed, password error");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 校验用户状态
        if (user.getUserStatus() != null && user.getUserStatus() != DEFAULT_USER_STATUS) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被禁用");
        }
        if (user.getIsDelete() != null && user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被删除");
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 统一密码加密，使用 UTF-8 编码，避免不同平台默认编码不一致导致密码不匹配
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        return UserUtils.getCurrentUser(request);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        return UserUtils.isAdmin(request);
    }

    /**
     * 管理员创建用户
     *
     * @param userCreateRequest
     * @param request
     * @return 新用户 id
     */
    @Override
    public long userCreate(UserCreateRequest userCreateRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        if (userCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userCreateRequest.getUserAccount();
        String userPassword = userCreateRequest.getUserPassword();
        String planetCode = userCreateRequest.getPlanetCode();
        // 校验必填参数
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }
        validateAccount(userAccount);
        if (userPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        validateGender(userCreateRequest.getGender());
        ensureUserAccountNotExists(userAccount);
        // 星球编号重复校验
        if (StringUtils.isNotBlank(planetCode)) {
            ensurePlanetCodeNotExists(planetCode, null);
        }
        Integer userRole = userCreateRequest.getUserRole();
        if (userRole == null) {
            userRole = DEFAULT_USER_ROLE;
        }
        validateUserRole(userRole);
        Integer userStatus = userCreateRequest.getUserStatus();
        if (userStatus == null) {
            userStatus = DEFAULT_USER_STATUS;
        }
        validateUserStatus(userStatus);
        // 加密密码
        String encryptPassword = encryptPassword(userPassword);
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername(userCreateRequest.getUsername());
        user.setAvatarUrl(userCreateRequest.getAvatarUrl());
        user.setGender(userCreateRequest.getGender());
        user.setPhone(userCreateRequest.getPhone());
        user.setEmail(userCreateRequest.getEmail());
        user.setPlanetCode(planetCode);
        user.setUserRole(userRole);
        user.setUserStatus(userStatus);
        try {
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建用户失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或编号已存在");
        }
        return user.getId();
    }

    /**
     * 管理员更新用户信息
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @Override
    public boolean userUpdate(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        if (userUpdateRequest == null || userUpdateRequest.getId() == null || userUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户 id 非法");
        }
        Long userId = userUpdateRequest.getId();
        User oldUser = this.getById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 如果修改了账号，校验账号唯一性
        String userAccount = userUpdateRequest.getUserAccount();
        if (StringUtils.isNotBlank(userAccount) && !userAccount.equals(oldUser.getUserAccount())) {
            validateAccount(userAccount);
            ensureUserAccountNotExists(userAccount);
        }
        String planetCode = userUpdateRequest.getPlanetCode();
        if (StringUtils.isNotBlank(planetCode) && !planetCode.equals(oldUser.getPlanetCode())) {
            ensurePlanetCodeNotExists(planetCode, userId);
        }
        validateGender(userUpdateRequest.getGender());
        if (userUpdateRequest.getUserStatus() != null) {
            validateUserStatus(userUpdateRequest.getUserStatus());
        }
        // 构建更新对象
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setUserAccount(userAccount);
        updateUser.setUsername(userUpdateRequest.getUsername());
        updateUser.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        updateUser.setGender(userUpdateRequest.getGender());
        updateUser.setPhone(userUpdateRequest.getPhone());
        updateUser.setEmail(userUpdateRequest.getEmail());
        updateUser.setPlanetCode(planetCode);
        updateUser.setUserStatus(userUpdateRequest.getUserStatus());
        // 如果密码不为空，则加密更新
        String userPassword = userUpdateRequest.getUserPassword();
        if (StringUtils.isNotBlank(userPassword)) {
            if (userPassword.length() < MIN_PASSWORD_LENGTH) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
            }
            String encryptPassword = encryptPassword(userPassword);
            updateUser.setUserPassword(encryptPassword);
        }
        return this.updateById(updateUser);
    }

    /**
     * 更新用户角色
     *
     * @param userRoleUpdateRequest
     * @param request
     * @return
     */
    @Override
    public boolean updateUserRole(UserRoleUpdateRequest userRoleUpdateRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        if (userRoleUpdateRequest == null || userRoleUpdateRequest.getId() == null || userRoleUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户 id 非法");
        }
        Integer userRole = userRoleUpdateRequest.getUserRole();
        validateUserRole(userRole);
        Long userId = userRoleUpdateRequest.getId();
        User oldUser = this.getById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setUserRole(userRole);
        return this.updateById(updateUser);
    }

    @Override
    public boolean updateUserStatus(UserStatusUpdateRequest userStatusUpdateRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        if (userStatusUpdateRequest == null || userStatusUpdateRequest.getId() == null || userStatusUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户 id 非法");
        }
        Integer userStatus = userStatusUpdateRequest.getUserStatus();
        validateUserStatus(userStatus);
        Long userId = userStatusUpdateRequest.getId();
        User oldUser = this.getById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setUserStatus(userStatus);
        return this.updateById(updateUser);
    }

    @Override
    public boolean updateMyPassword(UserPasswordUpdateRequest userPasswordUpdateRequest, HttpServletRequest request) {
        if (userPasswordUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String oldPassword = userPasswordUpdateRequest.getOldPassword();
        String newPassword = userPasswordUpdateRequest.getNewPassword();
        String checkPassword = userPasswordUpdateRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(oldPassword, newPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码参数为空");
        }
        if (newPassword.length() < MIN_PASSWORD_LENGTH || checkPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码过短");
        }
        if (!newPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }
        User currentUser = getCurrentUser(request);
        User dbUser = this.getById(currentUser.getId());
        if (dbUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        String oldEncryptPassword = encryptPassword(oldPassword);
        if (!oldEncryptPassword.equals(dbUser.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码错误");
        }
        String newEncryptPassword = encryptPassword(newPassword);
        User updateUser = new User();
        updateUser.setId(currentUser.getId());
        updateUser.setUserPassword(newEncryptPassword);
        return this.updateById(updateUser);
    }

    /**
     * 根据条件分页搜索用户
     *
     * @param userSearchRequest
     * @param request
     * @return
     */
    @Override
    public Page<User> searchUsers(UserSearchRequest userSearchRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        if (userSearchRequest == null) {
            userSearchRequest = new UserSearchRequest();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 精确查询条件
        if (userSearchRequest.getId() != null && userSearchRequest.getId() > 0) {
            queryWrapper.eq("id", userSearchRequest.getId());
        }
        if (userSearchRequest.getGender() != null) {
            queryWrapper.eq("gender", userSearchRequest.getGender());
        }
        if (userSearchRequest.getUserStatus() != null) {
            queryWrapper.eq("userStatus", userSearchRequest.getUserStatus());
        }
        if (userSearchRequest.getUserRole() != null) {
            queryWrapper.eq("userRole", userSearchRequest.getUserRole());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getKeyword())) {
            String keyword = userSearchRequest.getKeyword();
            queryWrapper.and(wrapper -> wrapper
                    .like("username", keyword)
                    .or()
                    .like("userAccount", keyword)
                    .or()
                    .like("phone", keyword)
                    .or()
                    .like("email", keyword)
                    .or()
                    .like("planetCode", keyword));
        }
        // 模糊查询条件
        if (StringUtils.isNotBlank(userSearchRequest.getUsername())) {
            queryWrapper.like("username", userSearchRequest.getUsername());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getUserAccount())) {
            queryWrapper.like("userAccount", userSearchRequest.getUserAccount());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getPhone())) {
            queryWrapper.like("phone", userSearchRequest.getPhone());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getEmail())) {
            queryWrapper.like("email", userSearchRequest.getEmail());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getPlanetCode())) {
            queryWrapper.like("planetCode", userSearchRequest.getPlanetCode());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getCreateTimeStart())) {
            queryWrapper.ge("createTime", userSearchRequest.getCreateTimeStart());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getCreateTimeEnd())) {
            queryWrapper.le("createTime", userSearchRequest.getCreateTimeEnd());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getUpdateTimeStart())) {
            queryWrapper.ge("updateTime", userSearchRequest.getUpdateTimeStart());
        }
        if (StringUtils.isNotBlank(userSearchRequest.getUpdateTimeEnd())) {
            queryWrapper.le("updateTime", userSearchRequest.getUpdateTimeEnd());
        }
        queryWrapper.orderByAsc("id");
        // 分页参数
        int pageNum = userSearchRequest.getPageNum() != null && userSearchRequest.getPageNum() > 0 ? userSearchRequest.getPageNum() : 0;
        if (pageNum <= 0) {
            pageNum = userSearchRequest.getCurrent() != null && userSearchRequest.getCurrent() > 0 ? userSearchRequest.getCurrent() : 1;
        }
        int pageSize = userSearchRequest.getPageSize() != null && userSearchRequest.getPageSize() > 0 ? userSearchRequest.getPageSize() : 10;
        // 限制最大分页大小
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        Page<User> page = new Page<>(pageNum, pageSize);
        return this.page(page, queryWrapper);
    }

    private void validateUserRole(Integer userRole) {
        if (userRole == null || (userRole != 0 && userRole != 1)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户角色非法");
        }
    }

    private void validateUserStatus(Integer userStatus) {
        if (userStatus == null || (userStatus != 0 && userStatus != 1)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户状态非法");
        }
    }

    private void validateGender(Integer gender) {
        if (gender != null && gender != 0 && gender != 1 && gender != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别非法");
        }
    }

    private void validateAccount(String userAccount) {
        if (StringUtils.isBlank(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号为空");
        }
        if (userAccount.length() < MIN_ACCOUNT_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
    }

    private void ensureUserAccountNotExists(String userAccount) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
    }

    private void ensurePlanetCodeNotExists(String planetCode, Long excludeUserId) {
        if (StringUtils.isBlank(planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号为空");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        if (excludeUserId != null) {
            queryWrapper.ne("id", excludeUserId);
        }
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号已存在");
        }
    }
}

