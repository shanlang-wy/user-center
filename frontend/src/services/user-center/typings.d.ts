// @ts-ignore
/* eslint-disable */

declare namespace API {
  type CurrentUser = {
    id: number;
    username?: string;
    userAccount?: string;
    avatarUrl?: string;
    gender?: number;
    phone?: string;
    email?: string;
    userStatus?: number;
    userRole?: number;
    planetCode?: string;
    createTime?: string;
    updateTime?: string;
    unreadCount?: number;
  };

  type UserPage = {
    records?: CurrentUser[];
    total?: number;
    size?: number;
    current?: number;
    pages?: number;
  };

  type UserSearchParams = PageParams & {
    pageNum?: number;
    id?: number;
    keyword?: string;
    username?: string;
    userAccount?: string;
    gender?: number;
    phone?: string;
    email?: string;
    userStatus?: number;
    userRole?: number;
    planetCode?: string;
    createTimeStart?: string;
    createTimeEnd?: string;
    updateTimeStart?: string;
    updateTimeEnd?: string;
  };

  type UserCreateParams = {
    userAccount?: string;
    userPassword?: string;
    username?: string;
    avatarUrl?: string;
    gender?: number;
    phone?: string;
    email?: string;
    planetCode?: string;
    userRole?: number;
    userStatus?: number;
  };

  type UserUpdateParams = UserCreateParams & {
    id: number;
  };

  type UserRoleUpdateParams = {
    id: number;
    userRole: number;
  };

  type UserStatusUpdateParams = {
    id: number;
    userStatus: number;
  };

  type UserProfileUpdateParams = {
    username?: string;
    avatarUrl?: string;
    gender?: number;
    phone?: string;
    email?: string;
  };

  type UserPasswordUpdateParams = {
    oldPassword?: string;
    newPassword?: string;
    checkPassword?: string;
  };

  type LoginResult = {
    status?: string;
    type?: string;
    currentAuthority?: string;
  };

  type RegisterResult = number;

  type PageParams = {
    current?: number;
    pageSize?: number;
  };

  type RuleListItem = {
    key?: number;
    disabled?: boolean;
    href?: string;
    avatar?: string;
    name?: string;
    owner?: string;
    desc?: string;
    callNo?: number;
    status?: number;
    updatedAt?: string;
    createdAt?: string;
    progress?: number;
  };

  /**
   * 通用返回类
   */
  type BaseResponse<T> = {
    code: number,
    data: T,
    message: string,
    description: string,
  }

  type RuleList = {
    data?: RuleListItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type FakeCaptcha = {
    code?: number;
    status?: string;
  };

  type LoginParams = {
    userAccount?: string;
    userPassword?: string;
    autoLogin?: boolean;
    type?: string;
  };

  type RegisterParams = {
    userAccount?: string;
    username?: string;
    userPassword?: string;
    checkPassword?: string;
    planetCode?: string;
    avatarUrl?: string;
    gender?: number;
    phone?: string;
    email?: string;
    type?: string;
  };

  type ErrorResponse = {
    /** 业务约定的错误码 */
    errorCode: string;
    /** 业务上的错误信息 */
    errorMessage?: string;
    /** 业务上的请求是否成功 */
    success?: boolean;
  };

  type NoticeIconList = {
    data?: NoticeIconItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type NoticeIconItemType = 'notification' | 'message' | 'event';

  type NoticeIconItem = {
    id?: string;
    extra?: string;
    key?: string;
    read?: boolean;
    avatar?: string;
    title?: string;
    status?: string;
    datetime?: string;
    description?: string;
    type?: NoticeIconItemType;
  };
}
