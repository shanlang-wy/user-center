# 用户中心后端

基于 Spring Boot 的用户中心后端服务，提供用户注册、登录、资料维护、头像上传、用户管理、角色管理、封禁管理和访问审计等能力。

## 功能特性

- 用户注册：支持账号、用户名、密码、编号、头像、性别、手机号、邮箱。
- 用户登录：区分用户不存在、密码错误、账号封禁等异常信息。
- 用户资料：支持查看当前用户、更新个人资料、修改密码。
- 用户管理：管理员可分页搜索、创建、编辑、删除用户。
- 权限管理：管理员可修改用户角色。
- 封禁管理：管理员可封禁或解封用户，封禁用户无法登录。
- 头像上传：支持注册前上传头像，也支持管理员为指定用户上传头像。
- 访问审计：记录注册、登录、退出、资料修改、权限变更、头像上传等操作。
- 统一响应：接口统一返回 `BaseResponse<T>`，便于前端处理。

## 技术栈

- Java 8
- Spring Boot 2.6.4
- MyBatis-Plus
- MySQL
- Maven
- Lombok

## 项目结构

```text
src/main/java/com/han/usercenter
├── common        # 通用响应和错误码
├── config        # Web 和 MyBatis-Plus 配置
├── contant       # 常量
├── controller    # 接口控制器
├── exception     # 业务异常和全局异常处理
├── mapper        # MyBatis Mapper
├── model         # 实体和请求模型
├── service       # 业务服务
└── utils         # 工具类
```

## 环境准备

1. 安装 JDK 8 或以上版本。
2. 安装 MySQL 5.7 或以上版本。
3. 创建数据库并执行初始化脚本：

```sql
source sql/create_table.sql;
```

4. 修改 `src/main/resources/application.yml` 中的数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/han?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

生产环境可通过环境变量配置：

```text
MYSQL_URL
MYSQL_USERNAME
MYSQL_PASSWORD
```

## 本地运行

```bash
./mvnw spring-boot:run
```

Windows：

```bash
mvnw.cmd spring-boot:run
```

服务默认地址：

```text
http://localhost:8080/api
```

## 构建

```bash
./mvnw -DskipTests package
```

Windows：

```bash
mvnw.cmd -DskipTests package
```

如果本地已有打包产物被进程占用，可先停止相关 Java 进程后重新打包。

## 主要接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/user/register` | 用户注册 |
| POST | `/api/user/login` | 用户登录 |
| POST | `/api/user/logout` | 用户退出 |
| GET | `/api/user/current` | 获取当前用户 |
| POST | `/api/user/profile` | 更新个人资料 |
| POST | `/api/user/password` | 修改个人密码 |
| GET | `/api/user/search` | 管理员分页查询用户 |
| POST | `/api/user/create` | 管理员创建用户 |
| POST | `/api/user/update` | 管理员更新用户 |
| POST | `/api/user/delete` | 管理员删除用户 |
| POST | `/api/user/role` | 管理员修改角色 |
| POST | `/api/user/status` | 管理员封禁或解封用户 |
| POST | `/api/upload/avatar` | 上传头像 |
| GET | `/api/audit/search` | 查询审计日志 |
| POST | `/api/audit/delete` | 删除审计日志 |
| POST | `/api/audit/clear` | 清空审计日志 |

## 状态约定

用户状态：

- `0`：正常
- `1`：封禁

用户角色：

- `0`：普通用户
- `1`：管理员

性别：

- `0`：保密
- `1`：男
- `2`：女

## 上传目录

头像默认保存到：

```text
upload/avatar/
```

访问路径默认前缀：

```text
/upload/avatar/
```

可在 `application.yml` 中修改：

```yaml
upload:
  avatar-path: upload/avatar/
  avatar-url-prefix: /upload/avatar/
```
