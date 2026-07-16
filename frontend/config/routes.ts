export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user', routes: [
          {name: '登录', path: '/user/login', component: './user/Login'},
          {name: '注册', path: '/user/register', component: './user/Register'}
        ]
      },
      {component: './404'},
    ],
  },
  {path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome'},
  {
    path: '/account',
    name: '个人页',
    icon: 'user',
    hideInMenu: true,
    routes: [
      {path: '/account/center', name: '个人中心', component: './Account/Center'},
      {path: '/account/settings', name: '个人设置', component: './Account/Settings'},
      {component: './404'},
    ],
  },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    component: './Admin',
    routes: [
      {path: '/admin/user-manage', name: '用户管理', icon: 'smile', component: './Admin/UserManage'},
      {component: './404'},
    ],
  },
  {name: '访问审计', icon: 'table', path: '/list', component: './TableList'},
  {path: '/', redirect: '/welcome'},
  {component: './404'},
];
