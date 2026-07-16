import React from 'react';
import {Card, Col, message, Row} from 'antd';
import {
  ProForm,
  ProFormRadio,
  ProFormText,
  ProFormUploadButton,
} from '@ant-design/pro-form';
import type {UploadFile} from 'antd/es/upload/interface';
import {useModel} from 'umi';
import {updateMyPassword, updateMyProfile} from '@/services/user-center/api';
import './index.less';

type ProfileFormValues = API.UserProfileUpdateParams & {
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
  return undefined;
};

const AccountSettings: React.FC = () => {
  const {initialState, setInitialState} = useModel('@@initialState');
  const currentUser = initialState?.currentUser;

  const handleProfileFinish = async (values: ProfileFormValues) => {
    const avatarError = getAvatarUploadError(values.avatarUrl);
    if (avatarError) {
      message.error(avatarError);
      return false;
    }
    const newUser = await updateMyProfile({
      ...values,
      avatarUrl: getAvatarUrl(values.avatarUrl),
    });
    if (!newUser) {
      return false;
    }
    await setInitialState((state) => ({
      ...state,
      currentUser: newUser,
    }));
    message.success('个人资料已更新');
    return true;
  };

  const handlePasswordFinish = async (values: API.UserPasswordUpdateParams) => {
    if (values.newPassword !== values.checkPassword) {
      message.error('两次输入的新密码不一致');
      return false;
    }
    const result = await updateMyPassword(values);
    if (!result) {
      return false;
    }
    message.success('密码已更新');
    return true;
  };

  return (
    <Row className="account-settings" gutter={24}>
      <Col xs={24} lg={14}>
        <Card title="个人设置" bordered={false}>
          <ProForm<ProfileFormValues>
            submitter={{
              searchConfig: {
                submitText: '保存资料',
              },
            }}
            initialValues={{
              ...currentUser,
              avatarUrl: buildAvatarFileList(currentUser?.avatarUrl),
            }}
            onFinish={handleProfileFinish}
          >
            <ProFormText
              name="username"
              label="用户名"
              rules={[{required: true, message: '请输入用户名'}]}
              placeholder="请输入用户名"
            />
            <ProFormUploadButton
              name="avatarUrl"
              label="头像"
              max={1}
              action="/api/upload/avatar"
              fieldProps={{
                name: 'file',
                listType: 'picture-card',
                accept: 'image/*',
                withCredentials: true,
              }}
            />
            <ProFormRadio.Group
              name="gender"
              label="性别"
              options={[
                {label: '男', value: 1},
                {label: '女', value: 2},
                {label: '保密', value: 0},
              ]}
            />
            <ProFormText name="phone" label="手机号" placeholder="请输入手机号"/>
            <ProFormText
              name="email"
              label="邮箱"
              placeholder="请输入邮箱"
              rules={[{type: 'email', message: '邮箱格式不正确'}]}
            />
          </ProForm>
        </Card>
      </Col>
      <Col xs={24} lg={10}>
        <Card title="修改密码" bordered={false}>
          <ProForm<API.UserPasswordUpdateParams>
            submitter={{
              searchConfig: {
                submitText: '更新密码',
              },
            }}
            onFinish={handlePasswordFinish}
          >
            <ProFormText.Password
              name="oldPassword"
              label="原密码"
              rules={[{required: true, message: '请输入原密码'}]}
            />
            <ProFormText.Password
              name="newPassword"
              label="新密码"
              rules={[
                {required: true, message: '请输入新密码'},
                {min: 8, message: '密码不少于 8 位'},
              ]}
            />
            <ProFormText.Password
              name="checkPassword"
              label="确认新密码"
              rules={[
                {required: true, message: '请确认新密码'},
                {min: 8, message: '密码不少于 8 位'},
              ]}
            />
          </ProForm>
        </Card>
      </Col>
    </Row>
  );
};

export default AccountSettings;
