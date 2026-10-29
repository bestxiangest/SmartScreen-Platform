# SmartScreen 后端部署说明（阿里云 101.132.166.117）

> 记录时间：2026-10-28 21:00（CST）
> 宿主环境：Ubuntu 22.04.5 LTS，2 vCPU / 1.6 GiB 内存，内网 172.24.8.252
> 管理后台：`http://sys.sharpcaterpillar.top:5000/admin/`（admin / Admin@2026）

## 1. 部署对象

| 项目 | 值 |
| --- | --- |
| 代码目录 | `/www/wwwroot/SmartScreen` |
| 入口 | `run.py` → `app.create_app()` |
| Python 环境 | `/www/wwwroot/SmartScreen/venv`（Python 3.10.12，按 requirements.txt 安装） |
| WSGI 服务 | gunicorn 26.2.0，2 个 sync worker |
| 监听 | `0.0.0.0:5000` |
| 对外地址 | `http://sys.sharpcaterpillar.top:5000/api/v1` |
| 数据库 | MariaDB 10.6.22，库 `campusscreen`，数据目录 `/www/server/data` |

## 2. systemd 服务

单元文件：`/etc/systemd/system/smartscreen.service`

```ini
[Unit]
Description=SmartScreen Campus Screen Backend (gunicorn)
After=network-online.target mysqld.service
Wants=network-online.target mysqld.service

[Service]
Type=simple
User=smartscreen
Group=smartscreen
WorkingDirectory=/www/wwwroot/SmartScreen
Environment=PYTHONUNBUFFERED=1
Environment=FLASK_CONFIG=default
Environment=FLASK_DEBUG=false
ExecStart=/www/wwwroot/SmartScreen/venv/bin/gunicorn \\
    --workers 2 --bind 0.0.0.0:5000 --timeout 60 --graceful-timeout 30 \\
    --keep-alive 5 --no-control-socket \\
    --access-logfile /var/log/smartscreen/access.log \\
    --error-logfile /var/log/smartscreen/error.log run:app
Restart=always
RestartSec=5
KillSignal=SIGQUIT
TimeoutStopSec=35
MemoryHigh=384M
MemoryMax=512M
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=full
ProtectHome=true
ReadWritePaths=/www/wwwroot/SmartScreen/static/uploads /var/log/smartscreen

[Install]
WantedBy=multi-user.target
```

运行账号 `smartscreen`（系统用户，无登录 shell），仅对 `static/uploads` 与 `/var/log/smartscreen` 有写权限。

常用操作：

```bash
systemctl status smartscreen
systemctl restart smartscreen
journalctl -u smartscreen -n 100 --no-pager
tail -f /var/log/smartscreen/error.log
```

## 3. 网络与域名

| 项 | 值 | 说明 |
| --- | --- | --- |
| UFW | `5000/tcp ALLOW`（注释 SmartScreen CampusScreen API） | 与阿里云安全组同时放行后才公网可达 |
| 阿里云安全组 | 已放行 5000/tcp | 由运维侧操作 |
| DNS | `sys.sharpcaterpillar.top` A → `101.132.166.117`，Cloudflare **DNS only（灰云）** | Cloudflare 代理端口仅支持 80/443/8080/8443 等，5000 必须直连回源 |
| 记录 ID | `9bc43e9144a19e9a7e6746cf68cff60a` | 需要回滚时删除该记录 |
| 安卓端地址 | `http://sys.sharpcaterpillar.top:5000/api/v1` | 与 `AnnouncementApiService.BASE_URL` 一致，无需改代码 |

当前为明文 HTTP。若后续改为 HTTPS，需要：为 `sys.sharpcaterpillar.top` 申请证书 → Nginx 增加 443 站点反代 `127.0.0.1:5000` → DNS 切橙云 → 同步修改客户端 BASE_URL（端口 443 或省略端口）。

## 4. 数据库与演示数据

- `mysqld` 已设为开机自启（`systemctl enable mysqld`），否则后端启动时 `db.create_all()` 会因连接失败退出。
- 表结构由 `db.create_all()` 建出，共 22 张表。
- 演示数据脚本：`/www/wwwroot/SmartScreen/seed_demo_data.py`

```bash
cd /www/wwwroot/SmartScreen
venv/bin/python seed_demo_data.py            # 仅在业务表全空时写入
venv/bin/python seed_demo_data.py --force    # 清空业务表后重灌
```

数据口径：

| 表 | 行数 | 说明 |
| --- | ---: | --- |
| roles / users / user_roles / user_profiles | 4 / 11 / 11 / 10 | 4 类角色：admin、teacher、maintainer、student |
| labs | 3 | A301、B208、C105 三个场地 |
| environmental_logs | 96 | A301 近 24 小时逐小时温湿度/光照/CO2 |
| announcements | 8 | 覆盖 6 种公告类型，含 3 条重要通知 |
| projects / project_members | 3 / 7 | 获奖、专利、软著各 1 项 |
| device_categories / devices | 6 / 11 | 仪器型号、位置、状态、购置日期 |
| device_usage_logs | 4 | 含 1 条未归还（对应"使用中"设备） |
| attendance_logs | 35 | 5 名学生近 10 个工作日的签到签退 |
| schedules | 12 | 未来 10 天工作日课表 |
| safety_guidelines | 4 | 用电、设备、应急、数据四类 |
| ai_training_plans | 2 | 12 周 / 10 周培养方案 JSON |
| material_categories / materials | 5 / 8 | 含 2 项低库存、1 项零库存 |
| material_requests / material_transactions | 5 / 12 | 5 种状态，出入库与库存逐笔自洽 |
| maintenance_orders | 3 | 已完成 / 维修中 / 急件各 1 |
| duty_schedules | 24 | 2 天已完成 + 6 天排班 |

演示账号：

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `Admin@2026` | 系统管理员 |
| `zhanghaiyang` / `liwenbo` | `Campus@2026` | 指导教师 |
| `sunjianguo` | `Campus@2026` | 设备管理员 |
| `2022210101`（李明轩）等 7 个学号 | `Campus@2026` | 学生 |

## 5. 代码修正记录

| 文件 | 问题 | 处理 | 备份 |
| --- | --- | --- | --- |
| `app/api/duty.py` | `func.case(..., else_=0)` 用法错误，统计接口 500 | 改为 `db.case((条件, 1), else_=0)` | `duty.py.bak-20261028` |
| `app/api/maintenance.py` | 只读接口按旧表结构取字段（`created_at`/`title`/`description`/`location`/`actual_completion_date`） | 列表、统计、关键字检索改用 `reported_at`/`fault_description`/`order_number`/`actual_completion` | `maintenance.py.bak-20261028` |

仍待处理：`maintenance.py` 的写入类接口（创建工单、派单、完工）仍按旧字段（`title`/`description`/`resolution`/`attachments`）实现，与当前模型不匹配，调用会失败，需要按 `fault_type`/`fault_description`/`solution_description` 等字段重写。

## 6. 回滚

```bash
cd /www/wwwroot/SmartScreen/app/api
cp -p duty.py.bak-20261028 duty.py
cp -p maintenance.py.bak-20261028 maintenance.py
systemctl restart smartscreen
```

如需完全下线：`systemctl disable --now smartscreen`，并删除 Cloudflare 中 `sys` 记录与 UFW 的 5000 规则。

## 7. 风险提示

1. `/www/wwwroot/SmartScreen/,env`（文件名缺少前导点）中存有明文 Gemini API Key，建议改名为 `.env`、`chmod 600` 并轮换该 Key。
2. `config.py` 内硬编码数据库口令与 JWT/SECRET 默认值，建议改为环境变量注入。
3. 5000 端口对公网明文开放，且接口仅登录类路径有认证，生产化前建议统一加 HTTPS 与访问控制。
4. 交付过程中使用过的 Cloudflare API Token 已在会话中明文出现，建议在 Cloudflare 控制台轮换。

## 8. 后台管理平台（2026-10-28 追加）

| 项 | 值 |
| --- | --- |
| 源码目录 | `admin-web/`（Vue 3 + Vite 6 + Element Plus + ECharts） |
| 访问地址 | `http://sys.sharpcaterpillar.top:5000/admin/`（也可用 `101.132.166.117:5000/admin/`） |
| 构建产物 | `/www/wwwroot/SmartScreen/static/admin/`（`npm run build` 生成 `dist/` 后同步） |
| 后端路由 | `app/main.py` 新增 `/admin`（302 到 `/admin/`）、`/admin/`、`/admin/<path:filename>` 三条，备份 `main.py.bak-20261028` |
| 前端路由模式 | hash 路由 + `base: './'`，可放在任意子目录，无需服务端 rewrite |
| 鉴权 | 复用 `/api/v1/auth/login` 的 JWT，令牌存 localStorage，401 自动回登录页 |

### 页面清单

| 菜单 | 页面 | 数据来源 | 能力 |
| --- | --- | --- | --- |
| 概览 | 数据概览 | 公告/项目/设备/物料/工单/账号统计、环境曲线、今日值班、最新公告 | 只读 |
| 内容管理 | 通知公告 | `/announcements` | 增删改查 |
| 内容管理 | 项目成果 | `/projects` | 增删改查 |
| 内容管理 | 课程表 | `/schedules` | 增删改查 |
| 内容管理 | 安全须知 | `/safety-guidelines` | 增删改查 |
| 内容管理 | 基地信息 | `/labs` | 增删改查 |
| 设备与资产 | 设备管理 | `/devices` | 增删改查（分类/状态筛选） |
| 设备与资产 | 设备分类 | `/device-categories` | 增删改查 |
| 设备与资产 | 物料仓库 | `/materials` | 增删改查（低库存/缺货筛选） |
| 运行管理 | 值班管理 | `/duty-schedules` | 增删改查 |
| 运行管理 | 维修工单 | `/maintenance-orders` | 只读（写入接口尚未适配当前模型） |
| 运行管理 | 考勤记录 | `/attendance` | 只读 |
| 数据 | 环境监测 | `/environmental/data` | 图表与统计、多传感器切换 |
| 系统 | 用户管理 | `/users` | 只读 |

### 本地开发与发布

```bash
cd admin-web
npm install
npm run dev        # http://localhost:5174
npm run build      # 产物 dist/
rsync -az dist/ aliyunserver:/www/wwwroot/SmartScreen/static/admin/
systemctl restart smartscreen   # 仅当改动了 Python 路由时才需要
```

## 9. 管理后台改造（2026-10-28 追加）

### 9.1 决策

项目本身自带的 `/dashboard`（`templates/index.html`）已经是可用的管理后台，因此**下线**上一轮新建的 Vue 版面板：

- 服务器：`app/main.py` 还原为备份 `main.py.bak-20261028`，`static/admin/` 已删除，`/admin` 现在返回 404；
- 本地：`admin-web/` 源码保留但不再部署，可按需删除。

### 9.2 首屏变慢的真实原因（实测）

| 项 | 改造前 | 改造后 |
| --- | ---: | ---: |
| `/dashboard` HTML | 117 KB | 16 KB（gzip） |
| Tailwind | 479 KB 浏览器端运行时 + 运行时编译 CSS | 6 KB 预编译 CSS |
| `lucide.js` | 559 KB | 101 KB（gzip） |
| `dashboard-api.js` | — | 5 KB |
| **首屏合计** | **约 1.13 MB** | **约 125 KB（-89%）** |

后端接口本身并不慢：`/announcements` 之类平均 4–11 ms（服务器本地）、公网约 110 ms，瓶颈在静态资源体积与浏览器端 Tailwind 运行时。

### 9.3 已实施的优化

1. **预编译 Tailwind**：用 Tailwind CLI 扫描 `templates/*.html` 生成 `static/tailwind.build.css`（30 KB / gzip 6 KB），替换 `tailwindcss.js` 运行时；`index.html`、`login.html` 已改引用。
2. **gzip 压缩**：`app/__init__.py` 引入 `Flask-Compress`，对 HTML / CSS / JS / JSON 生效。
3. **静态资源缓存**：`after_request` 为 `/static/*` 加 `Cache-Control: public, max-age=604800`，页面保持 `no-cache`；ETag 304 正常。
4. **gunicorn 线程模型**：`--worker-class gthread --threads 4 --keep-alive 5`，避免单进程串行处理并发静态请求。

> 注意：`static/dashboard-api.js` 走 7 天缓存，改动后必须同时修改模板里的 `?v=` 版本号，否则浏览器不会重新拉取。

### 9.4 补齐的数据页面

原有页面里只有消息通知、项目管理、用户管理、课程表接了接口，其余是静态假数据或“开发中”占位，现已全部接上真实数据（新增 `static/dashboard-api.js`，约 400 行，纯只读调用）：

| 页面 | 改造前 | 改造后 |
| --- | --- | --- |
| 总览 | 写死 32 / 4 / 12 / 1，动态全是假文案，图表空白 | 设备 11 / 项目 3 / 今日考勤 5 / 待处理工单 2，真实公告动态，24 小时温度曲线（canvas） |
| 设备中心 | 4 张写死的设备卡 | 11 台真实设备，分类/状态/关键字筛选 |
| 考勤记录 | “开发中”占位 | 真实签到签退表（30 条） |
| 安全管理 | “开发中”占位 | 4 条安全须知全文 |
| AI 培养方案 | “开发中”占位 | 2 份培养方案（目标/周期/导师/周任务） |
| 环境监控 | 写死 25.5°C / 60.2% / 450ppm | 实时 22.6°C / 56.2% / 620ppm + 24 小时曲线 |

同时修掉两处细节：实验室 ID 改为从 `/labs` 动态获取（重灌数据后主键不是 1）；考勤菜单图标由 `clipboard-user`（当前 lucide 版本不存在）换成 `file-text`。

### 9.5 涉及文件与回滚

| 文件 | 说明 | 备份 |
| --- | --- | --- |
| `app/__init__.py` | 增加 gzip 与缓存头 | `__init__.py.bak-20261028` |
| `templates/index.html` | 挂载点改造 + 引入数据脚本 | `index.html.bak-20261028` |
| `templates/login.html` | 改为预编译 CSS | `login.html.bak-20261028` |
| `static/tailwind.build.css` | 新增（30 KB） | — |
| `static/dashboard-api.js` | 新增（数据接入层） | — |
| `/etc/systemd/system/smartscreen.service` | 改 gthread 线程模型 | 原单元见第 2 节 |

回滚：把三个 `.bak-20261028` 覆盖回去、删除新增的两个静态文件、`systemctl restart smartscreen` 即可（gzip 依赖 `Flask-Compress`，回滚 `__init__.py` 后不再使用，可保留安装）。

### 9.6 仍待处理

- `系统设置` 页面仍是占位；
- 维修工单的写入接口（创建/派单/完工）仍按旧模型实现，前端暂未接入；
- 设备/物料/课程等页面只有“查看 + 原有四类增删改”，其余资源的增删改尚未在页面上实现。
