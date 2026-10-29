# 智能班牌实验室场景微信小程序项目概述

## 1. 项目简介

本项目是一个基于 `uni-app` 和 `Vue.js` (Vue 2) 开发的微信小程序，旨在为实验室场景提供一个智能化的班牌解决方案。它集成了设备管理、消息通知、项目协作、安全监控、AI 助手等多种功能，方便实验室成员进行日常管理和协作。

## 2. 技术栈

*   **前端框架**: [Vue.js 2](https://v2.vuejs.org/)
*   **跨端框架**: [uni-app](https://uniapp.dcloud.io/)
*   **状态管理**: [Vuex](https://v3.vuex.vuejs.org/)

## 3. 项目结构

```
.
├── App.vue               # 主组件，所有页面的父组件
├── index.html            # H5 端的模板文件
├── main.js               # Vue 初始化入口文件
├── manifest.json         # uni-app 配置文件
├── package.json          # npm 包管理文件
├── pages.json            # 页面和路由配置文件
├── README.md             # 项目说明文档
├── static/               # 静态资源目录
│   ├── devices/          # 设备图片
│   └── tabbar/           # TabBar 图标
├── pages/                # 业务页面目录
│   ├── ai/               # AI 助手相关页面
│   ├── campus/           # 校园服务相关页面
│   ├── device/           # 设备管理相关页面
│   ├── duty/             # 值班调度相关页面
│   ├── index/            # 首页
│   ├── maintenance/      # 维保调度相关页面
│   ├── message/          # 消息中心相关页面
│   ├── profile/          # 个人中心相关页面
│   ├── project/          # 项目协作相关页面
│   ├── safety/           # 安全监控相关页面
│   └── warehouse/        # 电子元件库相关页面
└── unpackage/            # uni-app 打包目录
```

## 4. 主要功能模块

根据 [`pages.json`](pages.json) 的配置，项目包含以下主要功能模块：

*   **首页** ([`pages/index/index.vue`](pages/index/index.vue)): 应用的主入口，展示核心功能和信息。
*   **设备管理**:
    *   设备列表 ([`pages/device/device.vue`](pages/device/device.vue))
    *   设备预约 ([`pages/device/booking.vue`](pages/device/booking.vue))
    *   设备报修 ([`pages/device/repair.vue`](pages/device/repair.vue))
*   **消息中心**:
    *   消息列表 ([`pages/message/message.vue`](pages/message/message.vue))
    *   新建消息 ([`pages/message/compose.vue`](pages/message/compose.vue))
    *   消息详情 ([`pages/message/detail.vue`](pages/message/detail.vue))
*   **项目协作**:
    *   项目列表 ([`pages/project/project.vue`](pages/project/project.vue))
    *   创建项目 ([`pages/project/create.vue`](pages/project/create.vue))
    *   项目详情 ([`pages/project/detail.vue`](pages/project/detail.vue))
*   **安全监控** ([`pages/safety/safety.vue`](pages/safety/safety.vue)): 实时监控实验室安全状态。
*   **AI 助手**:
    *   AI 项目评估 ([`pages/ai/evaluation.vue`](pages/ai/evaluation.vue))
    *   AI 人才培养 ([`pages/ai/talent.vue`](pages/ai/talent.vue))
*   **校园服务** ([`pages/campus/campus.vue`](pages/campus/campus.vue)): 提供与校园相关的便捷服务。
*   **个人中心**:
    *   个人主页 ([`pages/profile/profile.vue`](pages/profile/profile.vue))
    *   个人简介 ([`pages/profile/personal-info.vue`](pages/profile/personal-info.vue))
    *   成员列表 ([`pages/profile/member-list.vue`](pages/profile/member-list.vue))
*   **维保调度中心** ([`pages/maintenance/maintenance.vue`](pages/maintenance/maintenance.vue)): 管理设备的维护和保养。
*   **电子元件库** ([`pages/warehouse/warehouse.vue`](pages/warehouse/warehouse.vue)): 管理和申领电子元件。
*   **值班调度中心** ([`pages/duty/duty.vue`](pages/duty/duty.vue)): 安排和查看值班信息。