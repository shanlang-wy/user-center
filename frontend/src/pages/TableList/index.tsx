import React, { useRef, useState } from 'react';
import { Button, Drawer, message, Modal, Space, Tag } from 'antd';
import { PageContainer, FooterToolbar } from '@ant-design/pro-layout';
import type { ActionType, ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import type { ProDescriptionsItemProps } from '@ant-design/pro-descriptions';
import ProDescriptions from '@ant-design/pro-descriptions';
import {
  getAuditList,
  deleteAuditLog,
  batchDeleteAuditLog,
  clearAuditLog,
  type AuditRecord,
} from '@/services/user-center/audit';

const resultValueEnum = {
  success: { text: '成功', status: 'Success' },
  warning: { text: '需关注', status: 'Warning' },
  error: { text: '失败', status: 'Error' },
};

const moduleValueEnum = {
  登录认证: { text: '登录认证' },
  用户资料: { text: '用户资料' },
  个人设置: { text: '个人设置' },
  头像上传: { text: '头像上传' },
  权限管理: { text: '权限管理' },
  访问审计: { text: '访问审计' },
};

const getResultTag = (result: AuditRecord['result']) => {
  if (result === 'error') {
    return <Tag color="red">失败</Tag>;
  }
  if (result === 'warning') {
    return <Tag color="gold">需关注</Tag>;
  }
  return <Tag color="green">成功</Tag>;
};

const TableList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [showDetail, setShowDetail] = useState(false);
  const [currentRow, setCurrentRow] = useState<AuditRecord>();
  const [selectedRowsState, setSelectedRows] = useState<AuditRecord[]>([]);

  const handleDelete = async (id: number) => {
    const success = await deleteAuditLog(id);
    if (success) {
      message.success('删除成功');
      actionRef.current?.reload();
    } else {
      message.error('删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowsState.length === 0) {
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedRowsState.length} 条审计记录吗？`,
      onOk: async () => {
        const ids = selectedRowsState.map((item) => item.id);
        const success = await batchDeleteAuditLog(ids);
        if (success) {
          message.success('删除成功');
          setSelectedRows([]);
          actionRef.current?.reload();
        } else {
          message.error('删除失败');
        }
      },
    });
  };

  const handleClearAll = async () => {
    Modal.confirm({
      title: '确认清空',
      content: '确定要清空所有审计记录吗？此操作不可恢复。',
      onOk: async () => {
        const success = await clearAuditLog();
        if (success) {
          message.success('已清空全部审计记录');
          setSelectedRows([]);
          actionRef.current?.reload();
        } else {
          message.error('清空失败');
        }
      },
    });
  };

  const columns: ProColumns<AuditRecord>[] = [
    {
      title: '记录 ID',
      dataIndex: 'id',
      valueType: 'digit',
      width: 96,
    },
    {
      title: '关键词',
      dataIndex: 'keyword',
      hideInTable: true,
      fieldProps: {
        placeholder: '账号 / 用户名 / 动作 / 详情',
      },
    },
    {
      title: '用户账号',
      dataIndex: 'userAccount',
      copyable: true,
      render: (dom, entity) => (
        <a
          onClick={() => {
            setCurrentRow(entity);
            setShowDetail(true);
          }}
        >
          {dom}
        </a>
      ),
    },
    {
      title: '用户名',
      dataIndex: 'username',
    },
    {
      title: '模块',
      dataIndex: 'module',
      valueType: 'select',
      valueEnum: moduleValueEnum,
      width: 110,
    },
    {
      title: '动作',
      dataIndex: 'action',
      ellipsis: true,
    },
    {
      title: '结果',
      dataIndex: 'result',
      valueType: 'select',
      valueEnum: resultValueEnum,
      width: 100,
      render: (_, record) => getResultTag(record.result),
    },
    {
      title: 'IP 地址',
      dataIndex: 'ip',
      copyable: true,
    },
    {
      title: '客户端',
      dataIndex: 'client',
      hideInSearch: true,
    },
    {
      title: '操作身份',
      dataIndex: 'operator',
      valueType: 'select',
      valueEnum: {
        本人: { text: '本人' },
        管理员: { text: '管理员' },
        系统: { text: '系统' },
      },
      width: 100,
    },
    {
      title: '发生时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInSearch: true,
      width: 170,
    },
    {
      title: '发生时间',
      dataIndex: 'createTimeRange',
      valueType: 'dateRange',
      hideInTable: true,
      search: {
        transform: (value: any) => {
          if (Array.isArray(value) && value.length === 2) {
            return {
              createTimeStart: value[0],
              createTimeEnd: value[1],
            };
          }
          return {};
        },
      },
    },
    {
      title: '操作',
      valueType: 'option',
      width: 120,
      render: (_, record) => [
        <a
          key="detail"
          onClick={() => {
            setCurrentRow(record);
            setShowDetail(true);
          }}
        >
          详情
        </a>,
        <a
          key="delete"
          onClick={() => {
            Modal.confirm({
              title: '确认删除',
              content: `确定要删除审计记录 #${record.id} 吗？`,
              onOk: () => handleDelete(record.id),
            });
          }}
        >
          删除
        </a>,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<AuditRecord, Record<string, any>>
        headerTitle="访问审计"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Button
            key="refresh"
            onClick={() => {
              actionRef.current?.reload();
            }}
          >
            刷新
          </Button>,
          <Button
            key="clearAll"
            danger
            onClick={handleClearAll}
          >
            清空全部
          </Button>,
        ]}
        request={async (params) => {
          const query = params as Record<string, any>;
          const res = await getAuditList({
            current: query.current,
            pageSize: query.pageSize,
            id: query.id,
            keyword: query.keyword,
            userAccount: query.userAccount,
            username: query.username,
            module: query.module,
            action: query.action,
            result: query.result,
            ip: query.ip,
            operator: query.operator,
            createTimeStart: query.createTimeStart,
            createTimeEnd: query.createTimeEnd,
          });
          return {
            data: res.records || [],
            total: res.total || 0,
            success: true,
          };
        }}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => {
            setSelectedRows(selectedRows);
          },
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <Space>
              已选择 <a>{selectedRowsState.length}</a> 项
              <span>失败记录 {selectedRowsState.filter((item) => item.result === 'error').length} 条</span>
            </Space>
          }
        >
          <Button
            danger
            onClick={handleBatchDelete}
          >
            批量删除
          </Button>
          <Button
            type="primary"
            onClick={() => {
              message.success('已生成审计摘要');
            }}
          >
            生成摘要
          </Button>
        </FooterToolbar>
      )}

      <Drawer
        width={620}
        visible={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable
      >
        {currentRow?.id && (
          <ProDescriptions<AuditRecord>
            column={1}
            title={`审计详情 #${currentRow.id}`}
            request={async () => ({
              data: currentRow,
            })}
            params={{
              id: currentRow.id,
            }}
            columns={columns as ProDescriptionsItemProps<AuditRecord>[]}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default TableList;
