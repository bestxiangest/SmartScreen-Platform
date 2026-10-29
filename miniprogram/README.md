# 智能班牌实验室场景微信小程序

## 项目简介

这是一个基于 uni-app 框架开发的智能班牌实验室场景微信小程序，主要用于实验室设备管理、项目协作、安全监控等功能。

## 功能模块

### 1. 首页 (index)
- 实验室状态概览
- 快捷功能入口
- 通知公告
- 今日课程安排

### 2. 设备管理 (device)
- **设备列表** (`device.vue`) - 设备搜索、统计、列表展示
- **设备预约** (`booking.vue`) - 设备预约表单、时间选择
- **设备报修** (`repair.vue`) - 故障报修、图片上传

### 3. 项目协作 (project)
- **项目主页** (`project.vue`) - 项目列表、筛选、统计
- **创建项目** (`create.vue`) - 项目信息填写、团队设置
- **项目详情** (`detail.vue`) - 项目详情、任务管理、团队协作

### 4. 安全监控 (safety)
- 安全状态概览
- 监控区域管理
- 安全事件记录
- 紧急求助功能

### 5. AI智能助手 (ai)
- 智能对话
- 实验室状态查询
- 学习资源推荐
- 语音交互

### 6. 校园服务 (campus)
- 实验室导航
- 电子通行证
- 校园通知
- 便民服务

### 7. 个人中心 (profile)
- 用户信息管理
- 个人统计
- 设置选项
- 历史记录

## 技术栈

- **框架**: uni-app
- **前端**: Vue.js 2.x
- **构建工具**: Vue CLI
- **样式**: SCSS
- **平台**: 微信小程序

## 开发环境

### 环境要求
- Node.js >= 12.0.0
- npm >= 6.0.0
- 微信开发者工具

### 安装依赖
```bash
npm install
```

### 开发运行
```bash
# 微信小程序
npm run dev:mp-weixin

# H5
npm run dev:h5

# App
npm run dev:app-plus
```

### 构建发布
```bash
# 微信小程序
npm run build:mp-weixin

# H5
npm run build:h5

# App
npm run build:app-plus
```

## 项目结构

```
智能班牌——实验室场景/
├── pages/                 # 页面文件
│   ├── index/            # 首页
│   ├── device/           # 设备管理
│   ├── project/          # 项目协作
│   ├── safety/           # 安全监控
│   ├── ai/               # AI助手
│   ├── campus/           # 校园服务
│   └── profile/          # 个人中心
├── static/               # 静态资源
│   ├── logo.png         # 应用图标
│   └── tabbar/          # 底部导航图标
├── App.vue              # 应用入口组件
├── main.js              # 应用入口文件
├── manifest.json        # 应用配置文件
├── pages.json           # 页面路由配置
├── package.json         # 项目依赖配置
└── vite.config.js       # 构建配置
```

## 使用说明

1. 使用微信开发者工具导入项目
2. 在 `manifest.json` 中配置微信小程序 appid
3. 运行 `npm run dev:mp-weixin` 编译项目
4. 在微信开发者工具中预览和调试

## 注意事项

- 请确保已安装微信开发者工具
- 开发前请先配置好微信小程序的 appid
- 部分功能需要后端 API 支持，请根据实际情况调整接口地址
- 图片资源建议使用 CDN 或本地静态资源

## 开发团队

Smart Lab Team

## 许可证

MIT License