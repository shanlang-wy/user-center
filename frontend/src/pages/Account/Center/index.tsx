import React from 'react';
import {Avatar, Descriptions, Space, Tag} from 'antd';
import {UserOutlined} from '@ant-design/icons';
import {useModel} from 'umi';
import './index.less';

const genderText = {
  0: '保密',
  1: '男',
  2: '女',
};

const AccountCenter: React.FC = () => {
  const {initialState} = useModel('@@initialState');
  const currentUser = initialState?.currentUser;

  return (
    <div className="account-center">
      <div className="account-center-header">
        <Avatar size={96} src={currentUser?.avatarUrl} icon={<UserOutlined/>}/>
        <div>
          <h2>{currentUser?.username || '未设置用户名'}</h2>
          <Space size={8}>
            <Tag color={currentUser?.userStatus === 1 ? 'red' : 'green'}>
              {currentUser?.userStatus === 1 ? '封禁' : '正常'}
            </Tag>
            <Tag color={currentUser?.userRole === 1 ? 'green' : 'blue'}>
              {currentUser?.userRole === 1 ? '管理员' : '普通用户'}
            </Tag>
          </Space>
        </div>
      </div>

      <Descriptions className="account-center-detail" title="个人中心" column={2} bordered>
        <Descriptions.Item label="用户 ID">{currentUser?.id || '-'}</Descriptions.Item>
        <Descriptions.Item label="账号">{currentUser?.userAccount || '-'}</Descriptions.Item>
        <Descriptions.Item label="用户名">{currentUser?.username || '-'}</Descriptions.Item>
        <Descriptions.Item label="性别">{genderText[currentUser?.gender as 0 | 1 | 2] || '-'}</Descriptions.Item>
        <Descriptions.Item label="手机号">{currentUser?.phone || '-'}</Descriptions.Item>
        <Descriptions.Item label="邮箱">{currentUser?.email || '-'}</Descriptions.Item>
        <Descriptions.Item label="编号">{currentUser?.planetCode || '-'}</Descriptions.Item>
        <Descriptions.Item label="创建时间">{currentUser?.createTime || '-'}</Descriptions.Item>
      </Descriptions>
    </div>
  );
};

export default AccountCenter;
