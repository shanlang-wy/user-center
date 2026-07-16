# 用户中心

一个前后端分离的用户中心全栈项目，覆盖注册登录、个人资料、头像上传、用户管理、角色权限、封禁解封和访问审计等常见后台系统能力。项目采用 Spring Boot + MyBatis-Plus 提供后端接口，React + Umi + Ant Design Pro Components 构建前端管理界面。

## 项目亮点

- 完整用户流程：注册、登录、退出登录、当前用户获取、个人中心和个人设置。
- 注册信息更完整：支持用户名、头像、性别、手机号、邮箱等资料。
- 登录异常更清晰：区分用户不存在、密码错误、账号封禁等场景。
- 用户信息脱敏：接口返回前隐藏密码、删除标记等无需暴露的字段。
- 管理员能力：支持用户搜索、创建、编辑、删除、角色调整、封禁和解封。
- 头像上传：支持注册前上传头像，也支持登录后维护个人头像。
- 访问审计：记录注册、登录、退出、资料修改、权限变更等关键操作。
- 前后端分离：接口响应结构统一，前端通过代理访问后端服务，便于本地开发和部署。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 后端 | Java 8, Spring Boot 2.6.4, MyBatis-Plus, MySQL, Maven, Lombok |
| 前端 | React 17, TypeScript, Umi 3, Ant Design 4, Ant Design Pro Components |
| 工程化 | Maven Wrapper, npm scripts, Dockerfile, Git |

## 项目结构

```text
user-center
├── backend                 # Spring Boot 后端服务
│   ├── sql                 # 数据库初始化脚本
│   └── src/main/java       # 后端业务代码
├── frontend                # React 前端应用
│   ├── config              # Umi 配置和代理配置
│   └── src                 # 前端页面、组件、接口请求
├── README.md
└── .gitignore
```

## 功能模块

| 模块 | 说明 |
| --- | --- |
| 用户认证 | 注册、登录、退出登录、登录态获取 |
| 个人中心 | 展示账号、昵称、头像、性别、联系方式、角色、状态等信息 |
| 个人设置 | 修改头像、用户名、性别、手机号、邮箱和密码 |
| 用户管理 | 管理员分页查询、创建、编辑、删除用户 |
| 权限管理 | 管理员调整用户角色 |
| 封禁管理 | 管理员封禁/解封用户，封禁用户无法登录 |
| 文件上传 | 本地头像上传、静态资源访问 |
| 审计日志 | 查询、删除、清空关键操作日志 |

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/shanlang-wy/user-center.git
cd user-center
```

### 2. 初始化数据库

安装 MySQL 5.7 或以上版本后，执行初始化脚本：

```sql
source backend/sql/create_table.sql;
```

默认数据库名为：

```text
user_center
```

本地开发建议创建单独的数据库账号，并通过环境变量传入连接信息：

```bash
MYSQL_URL=jdbc:mysql://localhost:3306/user_center?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
MYSQL_USERNAME=your_mysql_user
MYSQL_PASSWORD=your_mysql_password
```

### 3. 启动后端

```bash
cd backend
mvnw.cmd spring-boot:run
```

macOS / Linux：

```bash
cd backend
./mvnw spring-boot:run
```

后端默认地址：

```text
http://localhost:8080/api
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run start:dev
```

前端默认地址：

```text
http://localhost:8000
```

开发环境接口代理位于：

```text
frontend/config/proxy.ts
```

## 常用命令

后端构建：

```bash
cd backend
mvnw.cmd -DskipTests package
```

前端类型检查：

```bash
cd frontend
npm run tsc
```

前端构建：

```bash
cd frontend
npm run build
```

## 后端接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/user/register` | 用户注册 |
| POST | `/api/user/login` | 用户登录 |
| POST | `/api/user/logout` | 用户退出 |
| GET | `/api/user/current` | 获取当前用户 |
| POST | `/api/user/profile` | 更新个人资料 |
| POST | `/api/user/password` | 修改个人密码 |
| GET | `/api/user/search` | 管理员查询用户 |
| POST | `/api/user/create` | 管理员创建用户 |
| POST | `/api/user/update` | 管理员更新用户 |
| POST | `/api/user/delete` | 管理员删除用户 |
| POST | `/api/user/role` | 管理员修改角色 |
| POST | `/api/user/status` | 管理员封禁或解封用户 |
| POST | `/api/upload/avatar` | 上传头像 |
| GET | `/api/audit/search` | 查询审计日志 |
| POST | `/api/audit/delete` | 删除审计日志 |
| POST | `/api/audit/clear` | 清空审计日志 |

## 配置说明

后端配置文件：

```text
backend/src/main/resources/application.yml
backend/src/main/resources/application-prod.yml
```

生产环境请使用环境变量配置数据库连接，避免把真实账号、密码提交到仓库：

```text
MYSQL_URL
MYSQL_USERNAME
MYSQL_PASSWORD
```

头像默认保存目录：

```text
backend/upload/avatar/
```

该目录已加入 `.gitignore`，不会提交用户上传文件。

## 状态约定

用户状态：

| 值 | 含义 |
| --- | --- |
| `0` | 正常 |
| `1` | 封禁 |

用户角色：

| 值 | 含义 |
| --- | --- |
| `0` | 普通用户 |
| `1` | 管理员 |

性别：

| 值 | 含义 |
| --- | --- |
| `0` | 保密 |
| `1` | 男 |
| `2` | 女 |

## 安全提示

- 不要在公开仓库中提交真实数据库密码、Token、私钥或用户上传文件。
- `application-prod.yml` 不提供默认数据库账号密码，部署时必须通过环境变量注入。
- 首个管理员账号可通过注册后手动在数据库中将 `userRole` 更新为 `1`。
- 生产环境建议将头像上传迁移到对象存储，并增加文件访问权限和大小限制。

## 后续优化方向

- 接入 JWT 或 OAuth2，增强登录态管理。
- 增加 Docker Compose，一键启动 MySQL、后端和前端。
- 增加更细粒度的 RBAC 权限模型。
- 完善单元测试和接口自动化测试。
- 增加线上部署文档和演示截图。
