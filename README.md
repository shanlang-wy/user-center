# 用户中心

用户中心全栈项目，包含 Spring Boot 后端和 React 前端。

## 项目结构

```text
user-center
├── backend   # Spring Boot 后端服务
└── frontend  # React 前端应用
```

## 功能概览

- 用户注册、登录、退出登录
- 注册时支持用户名、头像、性别、手机号、邮箱等信息
- 个人中心和个人设置
- 用户管理列表、搜索、创建、编辑、删除
- 管理员角色管理
- 用户封禁和解封
- 头像上传
- 访问审计日志

## 后端运行

进入后端目录：

```bash
cd backend
```

修改数据库配置：

```text
backend/src/main/resources/application.yml
```

初始化数据库：

```sql
source backend/sql/create_table.sql;
```

启动服务：

```bash
mvnw.cmd spring-boot:run
```

默认后端地址：

```text
http://localhost:8080/api
```

## 前端运行

进入前端目录：

```bash
cd frontend
```

安装依赖：

```bash
npm install
```

启动前端：

```bash
npm run start:dev
```

默认前端地址：

```text
http://localhost:8000
```

## 构建

后端：

```bash
cd backend
mvnw.cmd -DskipTests package
```

前端：

```bash
cd frontend
npm run build
```

## 说明

后端包名为：

```text
com.han.usercenter
```

前端代理配置位于：

```text
frontend/config/proxy.ts
```
