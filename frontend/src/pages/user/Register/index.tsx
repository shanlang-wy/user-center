import {LockOutlined, UploadOutlined, UserOutlined,} from '@ant-design/icons';
import {message, Tabs} from 'antd';
import React, {useState} from 'react';
import {history} from 'umi';
import {SYSTEM_LOGO} from '@/constants';
import Footer from '@/components/Footer';
import {register} from '@/services/user-center/api';
import styles from './index.less';
import {LoginForm, ProFormRadio, ProFormText, ProFormUploadButton} from '@ant-design/pro-form';
import type {UploadFile} from 'antd/es/upload/interface';

type RegisterFormValues = API.RegisterParams & {
  avatarUrl?: string | UploadFile[];
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

const Register: React.FC = () => {
  const [type, setType] = useState<string>('account');

  // 表单提交
  const handleSubmit = async (values: RegisterFormValues) => {
    const {userPassword, checkPassword} = values;
    // 校验
    if (userPassword !== checkPassword) {
      message.error('两次输入的密码不一致');
      return;
    }
    const avatarError = getAvatarUploadError(values.avatarUrl);
    if (avatarError) {
      message.error(avatarError);
      return;
    }

    try {
      // 注册
      const id = await register({
        ...values,
        avatarUrl: getAvatarUrl(values.avatarUrl),
      });
      if (id) {
        const defaultLoginSuccessMessage = '注册成功！';
        message.success(defaultLoginSuccessMessage);

        /** 此方法会跳转到 redirect 参数所在的位置 */
        if (!history) return;
        const {query} = history.location;
        history.push({
          pathname: '/user/login',
          query,
        });
        return;
      }
    } catch (error: any) {
      const defaultLoginFailureMessage = '注册失败，请重试！';
      message.error(defaultLoginFailureMessage);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <LoginForm
          submitter={{
            searchConfig: {
              submitText: '注册'
            }
          }}
          logo={<img alt="logo" src={SYSTEM_LOGO}/>}
          title="用户注册"
          subTitle="创建你的用户中心账号"
          initialValues={{
            gender: 0,
          }}
          onFinish={async (values) => {
            await handleSubmit(values as RegisterFormValues);
          }}
        >
          <Tabs activeKey={type} onChange={setType}>
            <Tabs.TabPane key="account" tab={'账号密码注册'}/>
          </Tabs>
          {type === 'account' && (
            <>
              <ProFormText
                name="username"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon}/>,
                }}
                placeholder="请输入用户名"
                rules={[
                  {
                    required: true,
                    message: '用户名是必填项！',
                  },
                ]}
              />
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon}/>,
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
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
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
              <ProFormText.Password
                name="checkPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder="请再次输入密码"
                rules={[
                  {
                    required: true,
                    message: '确认密码是必填项！',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: '长度不能小于 8',
                  },
                ]}
              />
              <ProFormText
                name="planetCode"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon}/>,
                }}
                placeholder="请输入星球编号"
                rules={[
                  {
                    required: true,
                    message: '星球编号是必填项！',
                  },
                ]}
              />
              <ProFormUploadButton
                name="avatarUrl"
                label="头像"
                max={1}
                action="/api/upload/avatar"
                icon={<UploadOutlined />}
                fieldProps={{
                  name: 'file',
                  listType: 'picture-card',
                  accept: 'image/*',
                  withCredentials: true,
                  onChange: ({file}) => {
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
              <ProFormRadio.Group
                name="gender"
                label="性别"
                options={[
                  {label: '男', value: 1},
                  {label: '女', value: 2},
                  {label: '保密', value: 0},
                ]}
                rules={[
                  {
                    required: true,
                    message: '请选择性别！',
                  },
                ]}
              />
              <ProFormText
                name="phone"
                fieldProps={{
                  size: 'large',
                }}
                placeholder="请输入手机号"
              />
              <ProFormText
                name="email"
                fieldProps={{
                  size: 'large',
                }}
                placeholder="请输入邮箱"
                rules={[
                  {
                    type: 'email',
                    message: '邮箱格式不正确',
                  },
                ]}
              />
            </>
          )}
        </LoginForm>
      </div>
      <Footer/>
    </div>
  );
};

export default Register;
