import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Divider, message, Space } from 'antd';
import React from 'react';
import { LoginForm, ProFormCheckbox, ProFormText } from '@ant-design/pro-form';
import { history, Link, useModel } from 'umi';
import { SYSTEM_LOGO } from '@/constants';
import { login } from '@/services/user-center/api';
import styles from './index.less';

const Login: React.FC = () => {
  const { initialState, setInitialState } = useModel('@@initialState');

  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();

    if (userInfo) {
      await setInitialState((s) => ({ ...s, currentUser: userInfo }));
    }
  };

  const handleSubmit = async (values: API.LoginParams) => {
    try {
      const user = await login({ ...values, type: 'account' });

      if (user) {
        message.success('登录成功！');
        await fetchUserInfo();

        if (!history) return;
        const { query } = history.location;
        const { redirect } = query as {
          redirect: string;
        };
        history.push(redirect || '/');
      }
    } catch (error) {
      message.error('登录失败，请重试！');
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.brand}>
        <img alt="logo" src={SYSTEM_LOGO} />
        <div>
          <h1>用户中心</h1>
          <span>User Center Management Platform</span>
        </div>
      </div>
      <div className={styles.content}>
        <div className={styles.loginPanel}>
          <LoginForm
            title="登录系统"
            initialValues={{
              autoLogin: true,
            }}
            submitter={{
              searchConfig: {
                submitText: '登 录',
              },
              submitButtonProps: {
                size: 'large',
                block: true,
              },
            }}
            onFinish={async (values) => {
              await handleSubmit(values as API.LoginParams);
            }}
          >
            <ProFormText
              name="userAccount"
              label="账号"
              fieldProps={{
                size: 'large',
                prefix: <UserOutlined className={styles.prefixIcon} />,
              }}
              placeholder="请输入账号"
              rules={[
                {
                  required: true,
                  message: '账号是必填项！',
                },
              ]}
            />
            <ProFormText.Password
              name="userPassword"
              label="密码"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined className={styles.prefixIcon} />,
              }}
              placeholder="请输入密码"
              rules={[
                {
                  required: true,
                  message: '密码是必填项！',
                },
                {
                  min: 8,
                  type: 'string',
                  message: '长度不能小于 8',
                },
              ]}
            />
            <div className={styles.loginLinks}>
              <Space split={<Divider type="vertical" />}>
                <ProFormCheckbox noStyle name="autoLogin">
                  自动登录
                </ProFormCheckbox>
                <Link to="/user/register">新用户注册</Link>
              </Space>
            </div>
          </LoginForm>
        </div>
      </div>
      <div className={styles.copyright}>copyright © 用户中心</div>
    </div>
  );
};

export default Login;
