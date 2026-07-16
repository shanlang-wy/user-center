import React from 'react';
import { history, useModel } from 'umi';
import { PageContainer } from '@ant-design/pro-layout';
import { Button, Card, Col, Row, Space, Statistic, Tag, Typography } from 'antd';
import {
  AuditOutlined,
  SafetyCertificateOutlined,
  TeamOutlined,
  UserSwitchOutlined,
} from '@ant-design/icons';
import styles from './Welcome.less';

const { Paragraph, Text, Title } = Typography;

const metricItems = [
  { title: '用户资料字段', value: 9, suffix: '项' },
  { title: '管理操作入口', value: 4, suffix: '类' },
  { title: '头像上传限制', value: 2, suffix: 'MB' },
  { title: '权限角色', value: 2, suffix: '级' },
];

const capabilityItems = [
  {
    icon: <TeamOutlined />,
    title: '用户档案',
    desc: '集中维护账号、昵称、联系方式、头像和星球编号。',
  },
  {
    icon: <UserSwitchOutlined />,
    title: '权限管理',
    desc: '管理员可以快速切换普通用户与管理员角色。',
  },
  {
    icon: <SafetyCertificateOutlined />,
    title: '安全登录',
    desc: '统一登录态、接口鉴权和跨域会话配置。',
  },
  {
    icon: <AuditOutlined />,
    title: '访问审计',
    desc: '围绕登录、资料修改和权限变更查看操作记录。',
  },
];

const Welcome: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;

  return (
    <PageContainer>
      <div className={styles.hero}>
        <div>
          <Space size={10} wrap>
            <Tag color="blue">用户中心</Tag>
            <Tag color="green">管理后台</Tag>
          </Space>
          <Title level={2}>欢迎回来，{currentUser?.username || currentUser?.userAccount || '管理员'}</Title>
          <Paragraph>
            这里是用户中心的工作台，聚合用户管理、权限控制、资料维护和访问审计。
          </Paragraph>
          <Space wrap>
            <Button type="primary" onClick={() => history.push('/admin/user-manage')}>
              进入用户管理
            </Button>
            <Button onClick={() => history.push('/list')}>查看访问审计</Button>
          </Space>
        </div>
      </div>

      <Row gutter={[16, 16]} className={styles.metrics}>
        {metricItems.map((item) => (
          <Col xs={24} sm={12} lg={6} key={item.title}>
            <Card bordered={false}>
              <Statistic title={item.title} value={item.value} suffix={item.suffix} />
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[16, 16]}>
        {capabilityItems.map((item) => (
          <Col xs={24} md={12} xl={6} key={item.title}>
            <Card className={styles.capability} bordered={false}>
              <div className={styles.capabilityIcon}>{item.icon}</div>
              <Text strong>{item.title}</Text>
              <Paragraph>{item.desc}</Paragraph>
            </Card>
          </Col>
        ))}
      </Row>
    </PageContainer>
  );
};

export default Welcome;
