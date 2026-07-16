// @ts-ignore
/* eslint-disable */
import request from '@/plugins/globalRequest';

/** 获取当前的用户 GET /api/user/current */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.CurrentUser>('/api/user/current', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 退出登录接口 POST /api/user/logout */
export async function outLogin(options?: { [key: string]: any }) {
  return request<number>('/api/user/logout', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 登录接口 POST /api/user/login */
export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  return request<API.CurrentUser>('/api/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 注册接口 POST /api/user/register */
export async function register(body: API.RegisterParams, options?: { [key: string]: any }) {
  return request<API.RegisterResult>('/api/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 搜索用户 GET /api/user/search */
export async function searchUsers(params?: API.UserSearchParams, options?: { [key: string]: any }) {
  return request<API.UserPage>('/api/user/search', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 管理员创建用户 POST /api/user/create */
export async function createUser(body: API.UserCreateParams, options?: { [key: string]: any }) {
  return request<number>('/api/user/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 管理员修改用户信息 POST /api/user/update */
export async function updateUser(body: API.UserUpdateParams, options?: { [key: string]: any }) {
  return request<boolean>('/api/user/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 管理员删除用户 POST /api/user/delete */
export async function deleteUser(id: number, options?: { [key: string]: any }) {
  return request<boolean>('/api/user/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: id,
    ...(options || {}),
  });
}

/** 更新用户角色 POST /api/user/role */
export async function updateUserRole(body: API.UserRoleUpdateParams, options?: { [key: string]: any }) {
  return request<boolean>('/api/user/role', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新用户状态 POST /api/user/status */
export async function updateUserStatus(body: API.UserStatusUpdateParams, options?: { [key: string]: any }) {
  return request<boolean>('/api/user/status', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新个人资料 POST /api/user/profile */
export async function updateMyProfile(body: API.UserProfileUpdateParams, options?: { [key: string]: any }) {
  return request<API.CurrentUser>('/api/user/profile', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新个人密码 POST /api/user/password */
export async function updateMyPassword(body: API.UserPasswordUpdateParams, options?: { [key: string]: any }) {
  return request<boolean>('/api/user/password', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /api/notices */
export async function getNotices(options?: { [key: string]: any }) {
  return request<API.NoticeIconList>('/api/notices', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取规则列表 GET /api/rule */
export async function rule(
  params: {
    // query
    /** 当前的页码 */
    current?: number;
    /** 页面的容量 */
    pageSize?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.RuleList>('/api/rule', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 新建规则 PUT /api/rule */
export async function updateRule(options?: { [key: string]: any }) {
  return request<API.RuleListItem>('/api/rule', {
    method: 'PUT',
    ...(options || {}),
  });
}

/** 新建规则 POST /api/rule */
export async function addRule(options?: { [key: string]: any }) {
  return request<API.RuleListItem>('/api/rule', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 删除规则 DELETE /api/rule */
export async function removeRule(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/api/rule', {
    method: 'DELETE',
    ...(options || {}),
  });
}
