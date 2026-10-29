# 智慧实验室电子班牌系统（SmartScreen）

> 面向高校实验室/创新基地的电子班牌平台：服务端（Flask）+ 电子班牌终端（Android，RK3568）+ 内容管理后台（Web）+ 微信小程序端。

## 项目简介

系统用于实验室门口/大厅的电子班牌展示与日常管理，覆盖五类场景：

- **内容展示**：通知公告、项目成果、课程表、安全须知、基地介绍、环境数据、值班信息
- **考勤签到**：人脸 / 扫码 / 手动三种方式，支持二维码签到页
- **设备管理**：仪器台账、借用归还、维修工单
- **物料仓库**：分类、库存、申领单、出入库流水
- **AI 能力**：个性化培养方案生成、项目/方案评估（对接 DashScope 通义千问与 gpt-image 系列）

## 三端架构

```
┌─────────────────────┐        ┌──────────────────────┐
│  Android 班牌终端    │        │  微信小程序端         │
│  (RK3568 / API 30)  │        │  (uni-app + Vue3)    │
└──────────┬──────────┘        └───────────┬──────────┘
           │  HTTP / JSON (JWT)            │
           └───────────────┬───────────────┘
                           ▼
              ┌──────────────────────────┐
              │   Flask 服务端 (/api/v1)  │
              │   + 后台管理 /dashboard   │
              └────────────┬─────────────┘
                           ▼
                    MySQL (MariaDB 10.6)
```

## 目录结构

```
.
├── server/          # Flask 服务端（API + 管理后台模板 + 演示数据脚本）
│   ├── app/         # 应用代码（api / models / helpers / main）
│   ├── templates/   # 管理后台页面（index.html / login.html / 二维码页）
│   ├── static/      # 前端静态资源（tailwind.build.css / lucide.js / dashboard-api.js）
│   ├── migrations/  # 数据库迁移脚本
│   ├── mysql.sql    # 建库建表脚本
│   ├── seed_demo_data.py  # 演示数据（22 张表、可重复执行）
│   └── requirements.txt
├── android/         # 电子班牌 Android 客户端（Java + Gradle）
│   ├── app/src/main/java/com/example/campusscreen/
│   └── files/       # 设计稿、接口文档、参考代码
├── miniprogram/     # 微信小程序端（uni-app）
└── docs/            # 部署说明、交接清单
```

## 技术栈

| 端 | 技术 |
| --- | --- |
| 服务端 | Python 3.10 / Flask 2.3 / Flask-SQLAlchemy / Flask-JWT-Extended / MariaDB 10.6 / gunicorn |
| 管理后台 | 服务端模板（Tailwind 预编译 CSS + 原生 JS），入口 `/dashboard` |
| Android | Java / Gradle 8.11 / AGP 8.10，minSdk 30、targetSdk 34，OkHttp、Glide |
| 小程序 | uni-app + Vue 3 + Vite |

## 快速开始

### 1. 服务端

```bash
cd server
python3 -m venv venv && source venv/bin/activate
pip install -r requirements.txt
cp .env.example .env        # 填写数据库口令、SECRET_KEY、JWT_SECRET_KEY
mysql -uroot -p < mysql.sql # 建库建表（或让应用自动 db.create_all）
python seed_demo_data.py    # 可选：灌入演示数据
python run.py               # 开发模式，默认 http://0.0.0.0:5000
```

生产部署、systemd 单元、域名与 HTTPS 见 `docs/部署说明.md`。

### 2. Android 班牌端

```bash
cd android
cp local.properties.example local.properties   # 填 sdk.dir 与 DASHSCOPE_API_KEY
./gradlew :app:assembleDebug                   # 产物 app/build/outputs/apk/debug/
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

安装后首次进入需在管理后台创建账号；公告列表接口地址在
`app/src/main/java/com/example/campusscreen/AnnouncementApiService.java` 的 `BASE_URL`。

### 3. 微信小程序端

```bash
cd miniprogram
npm install
npm run dev:mp-weixin     # 产物 dist/dev/mp-weixin，用微信开发者工具导入
```

用 HBuilderX 也可直接导入本目录。`manifest.json` 中的 `appid` 需替换为自己的小程序 AppID。

## 默认账号

| 角色 | 账号 | 说明 |
| --- | --- | --- |
| 管理员 | `admin` | 首次部署请立即修改密码 |
| 教师 / 设备管理员 / 学生 | 由后台创建或由 `seed_demo_data.py` 生成 | 演示数据中的学生密码统一为占位值，需自行重设 |

## 安全说明

- 仓库内**不含任何真实密钥**：DashScope Key、阿里云 AccessKey、Gemini Key 均已替换为占位符。
- Android 的 DashScope Key 通过 `local.properties` 注入 `BuildConfig.DASHSCOPE_API_KEY`，不进入版本库。
- 小程序端的第三方密钥位于 `pages/ai/*.vue` 与 `files/*.py`，替换 `YOUR_*` 占位符后使用。
- 服务端数据库口令、`SECRET_KEY`、`JWT_SECRET_KEY` 全部走环境变量，见 `server/.env.example`。

## 已知限制

- 维修工单的**写入类接口**（创建 / 派单 / 完工）仍按旧模型实现，与管理后台字段不一致，前端仅做只读展示。
- 管理后台的「系统设置」页仍是占位；设备 / 物料 / 课程的增删改尚未在页面实现（公告、项目、用户、课程表已支持）。
- 演示数据脚本会清空业务表，生产环境请勿直接执行 `--force`。

## 许可证

MIT License，详见 `LICENSE`。
