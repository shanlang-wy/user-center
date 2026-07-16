# 用户中心

一个前后端分离的用户中心全栈项目，面向后台管理系统中常见的账号体系场景，提供用户注册登录、资料维护、头像上传、管理员用户管理、角色权限、封禁解封和访问审计等能力。项目同时覆盖前端页面、后端接口、数据库脚本和基础部署配置，适合作为用户管理模块的学习案例或二次开发基础。

## 项目亮点

- 业务流程完整：覆盖注册、登录、退出登录、登录态获取、个人中心、个人设置等用户基础闭环。
- 用户资料更完善：注册和资料维护支持用户名、头像、性别、手机号、邮箱等信息，便于扩展真实业务字段。
- 登录反馈更清晰：区分用户不存在、密码错误、账号封禁等异常场景，前端可以给出更准确的交互提示。
- 数据返回更安全：后端统一进行用户信息脱敏，避免向前端暴露密码、删除标记等不必要字段。
- 管理能力更完整：管理员支持用户搜索、创建、编辑、删除、角色调整、封禁和解封，覆盖常见后台操作。
- 操作可追踪：内置关键操作审计日志，便于排查登录、资料修改、权限调整等管理行为。
- 前后端职责清晰：React 前端负责页面交互，Spring Boot 后端提供统一接口，方便独立开发、调试和部署。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 后端 | Java 8, Spring Boot 2.6.4, MyBatis-Plus, MySQL, Maven, Lombok |
| 前端 | React 17, TypeScript, Umi 3, Ant Design 4, Ant Design Pro Components |
| 工程化 | Maven Wrapper, npm scripts, Dockerfile, Git |

## 项目结构

```text
user-center
├── backend   # Spring Boot 后端服务
├── frontend  # React 前端应用
└── README.md
```

## 快速开始

克隆项目：

```bash
git clone https://github.com/shanlang-wy/user-center.git
cd user-center
```

初始化数据库：

```sql
source backend/sql/create_table.sql;
```

默认数据库名为 `user_center`。本地数据库连接可通过环境变量配置：

```bash
MYSQL_URL=jdbc:mysql://localhost:3306/user_center?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
MYSQL_USERNAME=your_mysql_user
MYSQL_PASSWORD=your_mysql_password
```

启动后端：

```bash
cd backend
mvnw.cmd spring-boot:run
```

启动前端：

```bash
cd frontend
npm install
npm run start:dev
```

默认访问地址：

```text
后端：http://localhost:8080/api
前端：http://localhost:8000
```

## 常用命令

```bash
# 后端构建
cd backend
mvnw.cmd -DskipTests package

# 前端类型检查
cd frontend
npm run tsc

# 前端构建
cd frontend
npm run build
```

## 核心接口

- 用户认证：`/api/user/register`、`/api/user/login`、`/api/user/logout`、`/api/user/current`
- 个人资料：`/api/user/profile`、`/api/user/password`
- 用户管理：`/api/user/search`、`/api/user/create`、`/api/user/update`、`/api/user/delete`
- 权限和状态：`/api/user/role`、`/api/user/status`
- 文件和审计：`/api/upload/avatar`、`/api/audit/search`、`/api/audit/delete`、`/api/audit/clear`

## 配置说明

后端配置文件：

```text
backend/src/main/resources/application.yml
backend/src/main/resources/application-prod.yml
```

前端代理配置：

```text
frontend/config/proxy.ts
```

生产环境请通过环境变量注入数据库连接信息，不要提交真实密码、Token、私钥或用户上传文件。

## 后续计划

- 接入 JWT 或 OAuth2。
- 增加 Docker Compose 一键启动。
- 完善 RBAC 权限模型。
- 补充自动化测试和部署文档。
