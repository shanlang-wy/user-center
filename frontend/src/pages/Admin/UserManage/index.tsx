import React, { useRef, useState } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {
  ModalForm,
  ProFormSelect,
  ProFormText,
  ProFormUploadButton,
} from '@ant-design/pro-form';
import { Button, Image, message, Popconfirm, Select, Space, Tag } from 'antd';
import type { UploadFile } from 'antd/es/upload/interface';
import {
  createUser,
  deleteUser,
  searchUsers,
  updateUser,
  updateUserRole,
  updateUserStatus,
} from '@/services/user-center/api';
import './index.less';

type UserFormValues = API.UserCreateParams & {
  avatarUrl?: string | UploadFile[];
};

const normalizeAvatarUrl = (avatarUrl?: string) => {
  if (!avatarUrl) {
    return undefined;
  }
  if (/^https?:\/\//i.test(avatarUrl) || avatarUrl.startsWith('data:')) {
    return avatarUrl;
  }
  if (avatarUrl.startsWith('/api/')) {
    return avatarUrl;
  }
  if (avatarUrl.startsWith('/upload/')) {
    return `/api${avatarUrl}`;
  }
  if (avatarUrl.startsWith('upload/')) {
    return `/api/${avatarUrl}`;
  }
  return avatarUrl;
};

const genderValueEnum = {
  0: { text: '保密' },
  1: { text: '男' },
  2: { text: '女' },
};

const statusValueEnum = {
  0: { text: '正常', status: 'Success' },
  1: { text: '禁用', status: 'Error' },
};

const roleValueEnum = {
  0: { text: '普通用户', status: 'Default' },
  1: { text: '管理员', status: 'Success' },
};

const genderOptions = [
  { label: '保密', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 },
];

const statusOptions = [
  { label: '正常', value: 0 },
  { label: '禁用', value: 1 },
];

const roleOptions = [
  { label: '普通用户', value: 0 },
  { label: '管理员', value: 1 },
];

const buildRangeParams = (value: string[] | undefined, startKey: string, endKey: string) => {
  if (!value || value.length < 2) {
    return {};
  }
  return {
    [startKey]: `${value[0]} 00:00:00`,
    [endKey]: `${value[1]} 23:59:59`,
  };
};

const buildAvatarFileList = (avatarUrl?: string): UploadFile[] => {
  if (!avatarUrl) {
    return [];
  }
  return [
    {
      uid: '-1',
      name: 'avatar',
      status: 'done',
      url: normalizeAvatarUrl(avatarUrl),
    },
  ];
};

const getAvatarUrl = (avatarValue?: string | UploadFile[]) => {
  if (!avatarValue) {
    return undefined;
  }
  if (typeof avatarValue === 'string') {
    return avatarValue;
  }
  const file = avatarValue[0];
  if (!file) {
    return undefined;
  }
  const response = file.response as API.BaseResponse<string> | undefined;
  return response?.data || file.url;
};

const getAvatarUploadError = (avatarValue?: string | UploadFile[]) => {
  if (!Array.isArray(avatarValue)) {
    return undefined;
  }
  const file = avatarValue[0];
  if (!file) {
    return undefined;
  }
  if (file.status === 'uploading') {
    return '头像仍在上传，请稍后再提交';
  }
  if (file.status === 'error') {
    return '头像上传失败，请删除后重新上传';
  }
  const response = file.response as API.BaseResponse<string> | undefined;
  if (response && response.code !== 0) {
    return response.description || response.message || '头像上传失败，请删除后重新上传';
  }
  if (file.status === 'done' && !response?.data && !file.url) {
    return '头像上传未返回有效地址，请重新上传';
  }
  return undefined;
};

const toUserPayload = (values: UserFormValues): API.UserCreateParams => {
  const payload = {
    ...values,
    avatarUrl: getAvatarUrl(values.avatarUrl),
  };
  if (!payload.avatarUrl) {
    delete payload.avatarUrl;
  }
  if (!payload.userPassword) {
    delete payload.userPassword;
  }
  return payload;
};

const UserFormFields: React.FC<{ update?: boolean; targetUserId?: number }> = ({ update, targetUserId }) => (
  <>
    <ProFormText
      name="userAccount"
      label="用户账户"
      rules={[
        { required: true, message: '请输入用户账户' },
        { min: 3, message: '账号不少于 3 位' },
      ]}
      placeholder="请输入用户账户"
    />
    <ProFormText.Password
      name="userPassword"
      label="密码"
      rules={[
        { required: !update, message: '请输入密码' },
        { min: 8, message: '密码不少于 8 位' },
      ]}
      placeholder={update ? '留空则不修改密码' : '请输入密码'}
    />
    <ProFormText name="username" label="用户名" placeholder="请输入用户名" />
    <ProFormUploadButton
      name="avatarUrl"
      label="头像"
      max={1}
      action="/api/upload/avatar"
      fieldProps={{
        name: 'file',
        data: targetUserId ? { targetUserId } : undefined,
        listType: 'picture-card',
        withCredentials: true,
        accept: 'image/*',
        onChange: ({ file }) => {
          if (file.status === 'done') {
            const response = file.response as API.BaseResponse<string> | undefined;
            if (response?.code !== 0) {
              file.status = 'error';
              message.error(response?.description || response?.message || '头像上传失败');
              return;
            }
            message.success('头像上传成功');
          }
          if (file.status === 'error') {
            message.error('头像上传失败');
          }
        },
      }}
    />
    <ProFormSelect name="gender" label="性别" options={genderOptions} placeholder="请选择性别" />
    <ProFormText name="phone" label="电话" placeholder="请输入电话" />
    <ProFormText
      name="email"
      label="邮箱"
      placeholder="请输入邮箱"
      rules={[{ type: 'email', message: '邮箱格式不正确' }]}
    />
    <ProFormText name="planetCode" label="星球编号" placeholder="请输入星球编号" />
    <ProFormSelect
      name="userStatus"
      label="状态"
      options={statusOptions}
      initialValue={0}
      placeholder="请选择状态"
    />
    {!update && (
      <ProFormSelect
        name="userRole"
        label="角色"
        options={roleOptions}
        initialValue={0}
        placeholder="请选择角色"
      />
    )}
  </>
);

const UserManage: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [updateModalVisible, setUpdateModalVisible] = useState(false);
  const [currentRow, setCurrentRow] = useState<API.CurrentUser>();

  const handleCreate = async (values: UserFormValues) => {
    const avatarError = getAvatarUploadError(values.avatarUrl);
    if (avatarError) {
      message.error(avatarError);
      return false;
    }
    const hide = message.loading('正在创建用户');
    const result = await createUser(toUserPayload(values));
    hide();
    if (!result) {
      return false;
    }
    message.success('创建成功');
    actionRef.current?.reload();
    return true;
  };

  const handleUpdate = async (values: UserFormValues) => {
    if (!currentRow?.id) {
      return false;
    }
    const avatarError = getAvatarUploadError(values.avatarUrl);
    if (avatarError) {
      message.error(avatarError);
      return false;
    }
    const hide = message.loading('正在更新用户');
    const result = await updateUser({
      ...toUserPayload(values),
      id: currentRow.id,
    });
    hide();
    if (!result) {
      return false;
    }
    message.success('更新成功');
    setUpdateModalVisible(false);
    setCurrentRow(undefined);
    actionRef.current?.reload();
    return true;
  };

  const handleDelete = async (record: API.CurrentUser) => {
    if (!record.id) {
      return;
    }
    const hide = message.loading('正在删除用户');
    const result = await deleteUser(record.id);
    hide();
    if (!result) {
      return;
    }
    message.success('删除成功');
    actionRef.current?.reload();
  };

  const handleRoleChange = async (record: API.CurrentUser, userRole: number) => {
    if (!record.id) {
      return;
    }
    const hide = message.loading('正在更新权限');
    const result = await updateUserRole({
      id: record.id,
      userRole,
    });
    hide();
    if (!result) {
      return;
    }
    message.success('权限已更新');
    actionRef.current?.reload();
  };

  const handleStatusChange = async (record: API.CurrentUser, userStatus: number) => {
    if (!record.id) {
      return;
    }
    const hide = message.loading(userStatus === 1 ? '正在封禁用户' : '正在解除封禁');
    const result = await updateUserStatus({
      id: record.id,
      userStatus,
    });
    hide();
    if (!result) {
      return;
    }
    message.success(userStatus === 1 ? '用户已封禁' : '用户已恢复正常');
    actionRef.current?.reload();
  };

  const columns: ProColumns<API.CurrentUser>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 72,
      valueType: 'digit',
    },
    {
      title: '关键词',
      dataIndex: 'keyword',
      hideInTable: true,
      fieldProps: {
        placeholder: '昵称 / 账号 / 电话 / 邮箱 / 星球编号',
      },
    },
    {
      title: '用户名',
      dataIndex: 'username',
      copyable: true,
    },
    {
      title: '用户账户',
      dataIndex: 'userAccount',
      copyable: true,
    },
    {
      title: '头像',
      dataIndex: 'avatarUrl',
      hideInSearch: true,
      width: 90,
      render: (_, record) =>
        record.avatarUrl ? (
          <Image
            className="user-manage-avatar"
            src={normalizeAvatarUrl(record.avatarUrl)}
            width={64}
            height={64}
            style={{ objectFit: 'cover' }}
          />
        ) : (
          '-'
        ),
    },
    {
      title: '性别',
      dataIndex: 'gender',
      valueType: 'select',
      valueEnum: genderValueEnum,
      width: 80,
    },
    {
      title: '电话',
      dataIndex: 'phone',
      copyable: true,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      copyable: true,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'userStatus',
      valueType: 'select',
      valueEnum: statusValueEnum,
      width: 90,
      render: (_, record) =>
        record.userStatus === 1 ? <Tag className="user-manage-tag" color="red">封禁</Tag> : <Tag className="user-manage-tag" color="green">正常</Tag>,
    },
    {
      title: '星球编号',
      dataIndex: 'planetCode',
      copyable: true,
    },
    {
      title: '角色',
      dataIndex: 'userRole',
      valueType: 'select',
      valueEnum: roleValueEnum,
      width: 130,
      render: (_, record) => (
        <Select
          className="user-role-select"
          size="small"
          value={record.userRole ?? 0}
          style={{ width: 112 }}
          options={roleOptions}
          onChange={(value) => handleRoleChange(record, value)}
        />
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInSearch: true,
      width: 170,
    },
    {
      title: '创建时间',
      dataIndex: 'createTimeRange',
      valueType: 'dateRange',
      hideInTable: true,
      search: {
        transform: (value) => buildRangeParams(value, 'createTimeStart', 'createTimeEnd'),
      },
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTimeRange',
      valueType: 'dateRange',
      hideInTable: true,
      search: {
        transform: (value) => buildRangeParams(value, 'updateTimeStart', 'updateTimeEnd'),
      },
    },
    {
      title: '操作',
      valueType: 'option',
      width: 200,
      render: (_, record) => (
        <Space>
          <Button
            type="primary"
            size="small"
            onClick={() => {
              setCurrentRow(record);
              setUpdateModalVisible(true);
            }}
          >
            修改
          </Button>
          <Popconfirm
            title={record.userStatus === 1 ? '确认解除该用户封禁？' : '确认封禁该用户？'}
            okText="确认"
            cancelText="取消"
            onConfirm={() => handleStatusChange(record, record.userStatus === 1 ? 0 : 1)}
          >
            <Button size="small">{record.userStatus === 1 ? '解封' : '封禁'}</Button>
          </Popconfirm>
          <Popconfirm
            title="确认删除该用户？"
            okText="删除"
            cancelText="取消"
            onConfirm={() => handleDelete(record)}
          >
            <Button danger size="small">
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <ProTable<API.CurrentUser>
        className="user-manage-table"
        columns={columns}
        actionRef={actionRef}
        cardBordered
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const result = await searchUsers({
            ...rest,
            current,
            pageSize,
          });
          return {
            data: result?.records || [],
            total: result?.total || 0,
            success: true,
          };
        }}
        columnsState={{
          persistenceKey: 'user-manage-table',
          persistenceType: 'localStorage',
        }}
        rowKey="id"
        search={{
          labelWidth: 'auto',
        }}
        pagination={{
          defaultPageSize: 5,
          showSizeChanger: true,
        }}
        dateFormatter="string"
        headerTitle="用户管理"
        toolBarRender={() => [
          <ModalForm<UserFormValues>
            key="create"
            title="新建用户"
            width={520}
            trigger={
              <Button type="primary" icon={<PlusOutlined />}>
                新建用户
              </Button>
            }
            modalProps={{
              destroyOnClose: true,
            }}
            onFinish={handleCreate}
          >
            <UserFormFields />
          </ModalForm>,
        ]}
      />
      <ModalForm<UserFormValues>
        key={currentRow?.id || 'update'}
        title="编辑用户"
        width={520}
        visible={updateModalVisible}
        modalProps={{
          destroyOnClose: true,
          onCancel: () => {
            setCurrentRow(undefined);
          },
        }}
        initialValues={{
          ...currentRow,
          avatarUrl: buildAvatarFileList(currentRow?.avatarUrl),
        }}
        onVisibleChange={(visible) => {
          setUpdateModalVisible(visible);
          if (!visible) {
            setCurrentRow(undefined);
          }
        }}
        onFinish={handleUpdate}
      >
            <UserFormFields update targetUserId={currentRow?.id} />
      </ModalForm>
    </>
  );
};

export default UserManage;
