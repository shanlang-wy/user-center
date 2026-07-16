import { Request, Response } from 'express';

const getNotices = (req: Request, res: Response) => {
  res.json({
    data: [
      {
        id: '000000001',
        avatar: '',
        title: '有 3 个新用户完成注册',
        datetime: '2026-07-16',
        type: 'notification',
      },
      {
        id: '000000002',
        avatar: '',
        title: '管理员更新了一名用户的角色',
        datetime: '2026-07-16',
        type: 'notification',
      },
      {
        id: '000000003',
        avatar: '',
        title: '系统记录了一条新的登录审计',
        datetime: '2026-07-16',
        read: true,
        type: 'notification',
      },
      {
        id: '000000004',
        avatar: '',
        title: '有用户资料发生变更',
        datetime: '2026-07-16',
        type: 'notification',
      },
      {
        id: '000000005',
        avatar: '',
        title: '头像上传服务运行正常',
        datetime: '2026-07-16',
        type: 'notification',
      },
      {
        id: '000000006',
        avatar: '',
        title: '系统管理员处理了一条用户变更',
        description: '用户中心操作提醒',
        datetime: '2026-07-16',
        type: 'message',
        clickClose: true,
      },
      {
        id: '000000007',
        avatar: '',
        title: '用户中心提醒',
        description: '请及时查看最新审计日志',
        datetime: '2026-07-16',
        type: 'message',
        clickClose: true,
      },
      {
        id: '000000008',
        avatar: '',
        title: '资料维护提醒',
        description: '建议完善账号头像和联系方式',
        datetime: '2026-07-16',
        type: 'message',
        clickClose: true,
      },
      {
        id: '000000009',
        title: '用户资料核对',
        description: '检查用户资料字段是否完整',
        extra: '未开始',
        status: 'todo',
        type: 'event',
      },
      {
        id: '000000010',
        title: '封禁记录复核',
        description: '复核近期封禁和解封操作',
        extra: '马上到期',
        status: 'urgent',
        type: 'event',
      },
      {
        id: '000000011',
        title: '权限配置检查',
        description: '检查管理员角色分配是否合理',
        extra: '已耗时 8 天',
        status: 'doing',
        type: 'event',
      },
      {
        id: '000000012',
        title: '审计日志归档',
        description: '整理并归档历史操作记录',
        extra: '进行中',
        status: 'processing',
        type: 'event',
      },
    ],
  });
};

export default {
  'GET /api/notices': getNotices,
};
