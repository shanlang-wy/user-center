# 用户中心前端

基于 React、Umi 和 Ant Design Pro Components 的用户中心前端应用，配套用户中心后端服务使用。

## 功能特性

- 登录、注册和退出登录
- 注册时填写用户名、账号、密码、编号、头像、性别、手机号和邮箱
- 个人中心资料展示
- 个人设置：修改头像、用户名、性别、手机号、邮箱和密码
- 用户管理：用户列表、搜索、创建、编辑、删除
- 管理员操作：角色调整、封禁和解封用户
- 访问审计：查询、删除和清空审计日志

## 技术栈

- React 17
- Umi 3
- Ant Design 4
- Ant Design Pro Components
- TypeScript

## 本地运行

安装依赖：

```bash
npm install
```

启动开发服务：

```bash
npm run start:dev
```

默认访问地址：

```text
http://localhost:8000
```

后端默认代理地址：

```text
http://localhost:8080
```

代理配置位于 `config/proxy.ts`。

## 构建

```bash
npm run build
```

构建产物输出到 `dist/`。

## 常用脚本

```bash
npm run tsc
npm run lint
npm run lint:fix
npm run build
```

## 目录结构

```text
src
├── components      # 公共组件
├── pages           # 页面
├── plugins         # 请求封装
├── services        # 接口请求
├── constants       # 常量
├── app.tsx         # 运行时配置
└── global.less     # 全局样式
```
