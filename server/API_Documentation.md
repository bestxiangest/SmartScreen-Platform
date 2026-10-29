# 智慧实验室电子班牌系统 - API 文档

## 📋 基础信息

- **项目名称**: 智慧实验室电子班牌系统
- **API版本**: v1.0
- **基础URL**: `http://sys.sharpcaterpillar.top:5000/api/v1`
- **认证方式**: JWT Bearer Token
- **Content-Type**: `application/json`
- **字符编码**: UTF-8

## 🔐 认证说明

大部分API接口需要JWT认证，请在请求头中添加：
```
Authorization: Bearer YOUR_JWT_TOKEN
```

## 📚 目录

1. [基础接口](#基础接口)
2. [认证模块](#认证模块)
3. [用户管理](#用户管理)
4. [角色管理](#角色管理)
5. [通知公告](#通知公告)
6. [项目成果](#项目成果)
7. [课程安排](#课程安排)
8. [设备管理](#设备管理)
9. [考勤管理](#考勤管理)
10. [实验室信息](#实验室信息)
11. [安全须知](#安全须知)
12. [AI培养方案](#ai培养方案)
13. [环境监测](#环境监测)
14. [文件上传](#文件上传)

---

## 基础接口

### 1. 健康检查

**接口地址**: `GET /health`

**描述**: 检查系统健康状态

**请求参数**: 无

**响应示例**:
```json
{
  "status": "healthy",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 2. 系统信息

**接口地址**: `GET /`

**描述**: 获取系统基本信息

**请求参数**: 无

**响应示例**:
```json
{
  "name": "智慧实验室电子班牌系统",
  "version": "1.0.0",
  "description": "智慧实验室电子班牌管理系统"
}
```

### 3. API测试

**接口地址**: `GET /api/v1/test`

**描述**: API连通性测试

**请求参数**: 无

**响应示例**:
```json
{
  "success": true,
  "message": "API测试成功",
  "data": {
    "timestamp": "2024-01-01T12:00:00Z",
    "server": "Flask Development Server"
  }
}
```

---

## 认证模块

### 1. 用户登录

**接口地址**: `POST /api/v1/auth/login`

**描述**: 用户登录获取JWT令牌

**请求参数**:
```json
{
  "username": "admin",
  "password": "password123"
}
```

**参数说明**:
- `username` (string, 必填): 用户名
- `password` (string, 必填): 密码

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "full_name": "管理员",
      "major": "计算机科学与技术",
      "class": "2022级1班",
      "email": "admin@example.com",
      "phone_number": "13800138000",
      "avatar_url": "http://xxx.com/images/admin.jpg",
      "created_at": "2024-01-01T12:00:00Z"
    }
  }
}
```

### 2. 获取用户信息

**接口地址**: `GET /api/v1/auth/profile`

**描述**: 获取当前登录用户信息

**认证**: 需要JWT令牌

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取用户信息成功",
  "data": {
    "id": 1,
    "username": "admin",
    "full_name": "管理员",
    "major": "计算机科学与技术",
    "class": "2022级1班",
    "email": "admin@example.com",
    "phone_number": "13800138000",
    "avatar_url": "http://xxx.com/images/admin.jpg",
    "created_at": "2024-01-01T12:00:00Z",
    "roles": [
      {
        "id": 1,
        "role_name": "管理员",
        "permissions": ["all"]
      }
    ]
  }
}
```

---

## 用户管理

### 1. 获取用户列表

**接口地址**: `GET /api/v1/users`

**描述**: 获取用户列表（分页）

**认证**: 需要JWT令牌

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10，最大100
- `role` (string, 可选): 按角色筛选
- `search` (string, 可选): 搜索用户名或姓名

### 1.1 通过真名查询用户ID

**接口地址**: `GET /api/v1/users/search-by-name`

**描述**: 通过用户真名精确查询用户ID

**认证**: 需要JWT令牌

**查询参数**:
- `full_name` (string, 必填): 用户真名

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "查询用户成功",
  "data": [
    {
      "id": 1,
      "full_name": "张三",
      "username": "zhangsan"
    }
  ]
}
```

**错误响应示例**:
```json
{
  "code": 404,
  "success": false,
  "message": "未找到匹配的用户"
}
```

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取用户列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "username": "admin",
        "full_name": "管理员",
        "major": "计算机科学与技术",
        "class": "2022级1班",
        "email": "admin@example.com",
        "phone_number": "13800138000",
        "avatar_url": "http://xxx.com/images/admin.jpg",
        "created_at": "2024-01-01T12:00:00Z",
        "roles": [
          {
            "id": 1,
            "role_name": "管理员",
            "permissions": ["all"]
          }
        ]
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 创建用户

**接口地址**: `POST /api/v1/users`

**描述**: 创建新用户

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "username": "newuser",
  "password": "password123",
  "full_name": "新用户",
  "major": "物联网工程",
  "class": "2023级1班",
  "email": "newuser@example.com",
  "phone_number": "13800138001",
  "avatar_url": "http://xxx.com/images/sss.jpg",
  "role_ids": [2]
}
```

**参数说明**:
- `username` (string, 必填): 用户名，唯一
- `password` (string, 必填): 密码
- `full_name` (string, 必填): 真实姓名
- `major` (string, 可选): 专业
- `class` (string, 可选): 班级
- `email` (string, 可选): 邮箱地址
- `phone_number` (string, 可选): 手机号码
- `avatar_url` (string, 可选): 头像图片URL
- `role_ids` (array, 可选): 角色ID列表

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "用户创建成功",
  "data": {
    "id": 2,
    "username": "newuser",
    "full_name": "新用户",
    "major": "物联网工程",
    "class": "2023级1班",
    "email": "newuser@example.com",
    "phone_number": "13800138001",
    "avatar_url": "http://xxx.com/images/sss.jpg",
    "created_at": "2024-01-01T12:00:00Z",
    "roles": [
      {
        "id": 2,
        "role_name": "普通用户",
        "permissions": ["read"]
      }
    ]
  }
}
```

### 3. 获取用户详情

**接口地址**: `GET /api/v1/users/{user_id}`

**描述**: 获取指定用户详情

**认证**: 需要JWT令牌

**路径参数**:
- `user_id` (int, 必填): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取用户详情成功",
  "data": {
    "id": 1,
    "username": "admin",
    "full_name": "管理员",
    "major": "计算机科学与技术",
    "class": "2022级1班",
    "email": "admin@example.com",
    "phone_number": "13800138000",
    "avatar_url": "http://xxx.com/images/admin.jpg",
    "created_at": "2024-01-01T12:00:00Z",
    "roles": [
      {
        "id": 1,
        "role_name": "管理员",
        "permissions": ["all"]
      }
    ]
  }
}
```

### 4. 更新用户信息

**接口地址**: `PUT /api/v1/users/{user_id}`

**描述**: 更新用户信息

**认证**: 需要JWT令牌

**路径参数**:
- `user_id` (int, 必填): 用户ID

**请求参数**:
```json
{
  "username": "updateduser",
  "full_name": "更新的用户",
  "major": "软件工程",
  "class": "2023级2班",
  "email": "updated@example.com",
  "phone_number": "13800138002",
  "avatar_url": "http://xxx.com/images/updated.jpg",
  "password": "newpassword123",
  "role_ids": [1, 2]
}
```

**参数说明**:
- `username` (string, 可选): 用户名
- `full_name` (string, 可选): 真实姓名
- `major` (string, 可选): 专业
- `class` (string, 可选): 班级
- `email` (string, 可选): 邮箱地址
- `phone_number` (string, 可选): 手机号码
- `avatar_url` (string, 可选): 头像图片URL
- `password` (string, 可选): 新密码
- `role_ids` (array, 可选): 角色ID列表

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "更新用户信息成功",
  "data": {
    "id": 1,
    "username": "updateduser",
    "full_name": "更新的用户",
    "major": "软件工程",
    "class": "2023级2班",
    "email": "updated@example.com",
    "phone_number": "13800138002",
    "avatar_url": "http://xxx.com/images/updated.jpg",
    "created_at": "2024-01-01T12:00:00Z",
    "roles": [
      {
        "id": 1,
        "role_name": "管理员",
        "permissions": ["all"]
      },
      {
        "id": 2,
        "role_name": "普通用户",
        "permissions": ["read"]
      }
    ]
  }
}
```

### 5. 删除用户

**接口地址**: `DELETE /api/v1/users/{user_id}`

**描述**: 删除指定用户

**认证**: 需要JWT令牌

**路径参数**:
- `user_id` (int, 必填): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除用户成功"
}
```

---

## 角色管理

### 1. 获取角色列表

**接口地址**: `GET /api/v1/roles`

**描述**: 获取所有角色列表

**认证**: 需要JWT令牌

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取角色列表成功",
  "data": [
    {
      "id": 1,
      "role_name": "管理员",
      "permissions": ["all"]
    },
    {
      "id": 2,
      "role_name": "普通用户",
      "permissions": ["read"]
    }
  ]
}
```

### 2. 创建角色

**接口地址**: `POST /api/v1/roles`

**描述**: 创建新角色

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "role_name": "编辑者",
  "permissions": ["read", "write"]
}
```

**参数说明**:
- `role_name` (string, 必填): 角色名称，唯一
- `permissions` (array, 可选): 权限列表

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建角色成功",
  "data": {
    "id": 3,
    "role_name": "编辑者",
    "permissions": ["read", "write"]
  }
}
```

### 3. 获取角色详情

**接口地址**: `GET /api/v1/roles/{role_id}`

**描述**: 获取指定角色详情

**认证**: 需要JWT令牌

**路径参数**:
- `role_id` (int, 必填): 角色ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取角色详情成功",
  "data": {
    "id": 1,
    "role_name": "管理员",
    "permissions": ["all"]
  }
}
```

### 4. 更新角色

**接口地址**: `PUT /api/v1/roles/{role_id}`

**描述**: 更新指定角色

**认证**: 需要JWT令牌

**路径参数**:
- `role_id` (int, 必填): 角色ID

**请求参数**:
```json
{
  "role_name": "高级编辑者",
  "permissions": ["read", "write", "delete"]
}
```

**参数说明**: 同创建角色，所有字段均为可选

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "更新角色成功",
  "data": {
    "id": 3,
    "role_name": "高级编辑者",
    "permissions": ["read", "write", "delete"]
  }
}
```

### 5. 删除角色

**接口地址**: `DELETE /api/v1/roles/{role_id}`

**描述**: 删除指定角色

**认证**: 需要JWT令牌

**路径参数**:
- `role_id` (int, 必填): 角色ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除角色成功"
}
```

---

## 通知公告

### 1. 获取公告列表

**接口地址**: `GET /api/v1/announcements`

**描述**: 获取通知公告列表（分页）

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10
- `type` (string, 可选): 公告类型筛选
- `is_important` (boolean, 可选): 是否重要通知

**公告类型**:
- `通知`
- `新闻`
- `动态`
- `安全提示`
- `天气提示`
- `名言金句`

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取通知公告列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "title": "重要通知",
        "content": "这是一条重要通知的内容",
        "author_name": "管理员",
        "type": "通知",
        "is_important": true,
        "created_at": "2024-01-01T12:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 创建公告

**接口地址**: `POST /api/v1/announcements`

**描述**: 创建新的通知公告

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "title": "新公告标题",
  "content": "公告内容详情",
  "author_name": "发布者",
  "type": "通知",
  "is_important": false
}
```

**参数说明**:
- `title` (string, 必填): 公告标题
- `content` (string, 必填): 公告内容
- `author_name` (string, 可选): 发布者名称
- `type` (string, 必填): 公告类型
- `is_important` (boolean, 可选): 是否重要，默认false

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建通知公告成功",
  "data": {
    "id": 2,
    "title": "新公告标题",
    "content": "公告内容详情",
    "author_name": "发布者",
    "type": "通知",
    "is_important": false,
    "created_at": "2024-01-01T12:00:00Z"
  }
}
```

### 3. 获取公告详情

**接口地址**: `GET /api/v1/announcements/{announcement_id}`

**描述**: 获取指定公告详情

**路径参数**:
- `announcement_id` (int, 必填): 公告ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取通知公告详情成功",
  "data": {
    "id": 1,
    "title": "重要通知",
    "content": "这是一条重要通知的内容",
    "author_name": "管理员",
    "type": "通知",
    "is_important": true,
    "created_at": "2024-01-01T12:00:00Z"
  }
}
```

### 4. 更新公告

**接口地址**: `PUT /api/v1/announcements/{announcement_id}`

**描述**: 更新指定公告

**认证**: 需要JWT令牌

**路径参数**:
- `announcement_id` (int, 必填): 公告ID

**请求参数**:
```json
{
  "title": "更新的公告标题",
  "content": "更新的公告内容",
  "author_name": "更新者",
  "type": "新闻",
  "is_important": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "更新通知公告成功",
  "data": {
    "id": 1,
    "title": "更新的公告标题",
    "content": "更新的公告内容",
    "author_name": "更新者",
    "type": "新闻",
    "is_important": true,
    "created_at": "2024-01-01T12:00:00Z"
  }
}
```

### 5. 删除公告

**接口地址**: `DELETE /api/v1/announcements/{announcement_id}`

**描述**: 删除指定公告

**认证**: 需要JWT令牌

**路径参数**:
- `announcement_id` (int, 必填): 公告ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除通知公告成功"
}
```

---

## 项目成果

### 1. 获取项目列表

**接口地址**: `GET /api/v1/projects`

**描述**: 获取项目成果列表（分页）

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10
- `achievement_type` (string, 可选): 成果类型筛选

**成果类型**:
- `获奖`
- `专利`
- `软著`

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取项目成果列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "project_name": "智能识别系统",
        "description": "基于深度学习的智能识别系统",
        "achievement_type": "专利",
        "achievement_details": "已获得国家发明专利授权",
        "start_date": "2024-01-01",
        "end_date": "2024-06-30",
        "image_url": "https://example.com/project1.jpg",
        "members": [
          {
            "id": 1,
            "full_name": "张三",
            "role_in_project": "项目负责人"
          },
          {
            "id": 2,
            "full_name": "李四",
            "role_in_project": "开发工程师"
          }
        ]
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 创建项目

**接口地址**: `POST /api/v1/projects`

**描述**: 创建新的项目成果

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "project_name": "新项目名称",
  "description": "项目描述",
  "achievement_type": "获奖",
  "achievement_details": "获得省级一等奖",
  "start_date": "2024-01-01",
  "end_date": "2024-12-31",
  "image_url": "https://example.com/project.jpg",
  "members": [
    {
      "user_id": 1,
      "role_in_project": "项目负责人"
    },
    {
      "user_id": 2,
      "role_in_project": "开发工程师"
    }
  ]
}
```

**参数说明**:
- `project_name` (string, 必填): 项目名称
- `description` (string, 可选): 项目描述
- `achievement_type` (string, 必填): 成果类型
- `achievement_details` (string, 可选): 成果详情
- `start_date` (string, 可选): 开始日期，格式YYYY-MM-DD
- `end_date` (string, 可选): 结束日期，格式YYYY-MM-DD
- `image_url` (string, 可选): 项目图片URL
- `members` (array, 可选): 项目成员列表
  - `user_id` (int, 必填): 用户ID
  - `role_in_project` (string, 可选): 在项目中的角色

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建项目成果成功",
  "data": {
    "id": 2,
    "project_name": "新项目名称",
    "description": "项目描述",
    "achievement_type": "获奖",
    "achievement_details": "获得省级一等奖",
    "start_date": "2024-01-01",
    "end_date": "2024-12-31",
    "image_url": "https://example.com/project.jpg",
    "members": [
      {
        "id": 1,
        "full_name": "张三",
        "role_in_project": "项目负责人"
      },
      {
        "id": 2,
        "full_name": "李四",
        "role_in_project": "开发工程师"
      }
    ]
  }
}
```

### 3. 获取项目详情

**接口地址**: `GET /api/v1/projects/{project_id}`

**描述**: 获取指定项目详情

**路径参数**:
- `project_id` (int, 必填): 项目ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取项目成果详情成功",
  "data": {
    "id": 1,
    "project_name": "智能识别系统",
    "description": "基于深度学习的智能识别系统",
    "achievement_type": "专利",
    "achievement_details": "已获得国家发明专利授权",
    "start_date": "2024-01-01",
    "end_date": "2024-06-30",
    "image_url": "https://example.com/project1.jpg",
    "members": [
      {
        "id": 1,
        "full_name": "张三",
        "role_in_project": "项目负责人"
      },
      {
        "id": 2,
        "full_name": "李四",
        "role_in_project": "开发工程师"
      }
    ]
  }
}
```

### 4. 更新项目

**接口地址**: `PUT /api/v1/projects/{project_id}`

**描述**: 更新指定项目

**认证**: 需要JWT令牌

**路径参数**:
- `project_id` (int, 必填): 项目ID

**请求参数**: 同创建项目，所有字段均为可选

**响应示例**: 同获取项目详情

### 5. 删除项目

**接口地址**: `DELETE /api/v1/projects/{project_id}`

**描述**: 删除指定项目

**认证**: 需要JWT令牌

**路径参数**:
- `project_id` (int, 必填): 项目ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除项目成果成功"
}
```

---

## 课程安排

### 1. 获取课程列表

**接口地址**: `GET /api/v1/schedules`

**描述**: 获取课程安排列表（分页）

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10
- `class_date` (string, 可选): 上课日期筛选，格式YYYY-MM-DD

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取课程表列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "course_name": "数据结构",
        "teacher_name": "王老师",
        "class_date": "2024-01-15",
        "start_time": "08:00",
        "end_time": "09:40",
        "location": "A101教室"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 创建课程安排

**接口地址**: `POST /api/v1/schedules`

**描述**: 创建新的课程安排

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "course_name": "算法设计",
  "teacher_name": "李老师",
  "class_date": "2024-01-16",
  "start_time": "10:00",
  "end_time": "11:40",
  "location": "B202教室"
}
```

**参数说明**:
- `course_name` (string, 必填): 课程名称
- `teacher_name` (string, 可选): 教师姓名
- `class_date` (string, 必填): 上课日期，格式YYYY-MM-DD
- `start_time` (string, 必填): 开始时间，格式HH:MM
- `end_time` (string, 必填): 结束时间，格式HH:MM
- `location` (string, 可选): 上课地点

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建课程安排成功",
  "data": {
    "id": 2,
    "course_name": "算法设计",
    "teacher_name": "李老师",
    "class_date": "2024-01-16",
    "start_time": "10:00",
    "end_time": "11:40",
    "location": "B202教室"
  }
}
```

### 3. 获取课程详情

**接口地址**: `GET /api/v1/schedules/{schedule_id}`

**描述**: 获取指定课程安排详情

**路径参数**:
- `schedule_id` (int, 必填): 课程安排ID

**响应示例**: 同创建课程安排的响应

### 4. 更新课程安排

**接口地址**: `PUT /api/v1/schedules/{schedule_id}`

**描述**: 更新指定课程安排

**认证**: 需要JWT令牌

**路径参数**:
- `schedule_id` (int, 必填): 课程安排ID

**请求参数**: 同创建课程安排，所有字段均为可选

**响应示例**: 同获取课程详情

### 5. 删除课程安排

**接口地址**: `DELETE /api/v1/schedules/{schedule_id}`

**描述**: 删除指定课程安排

**认证**: 需要JWT令牌

**路径参数**:
- `schedule_id` (int, 必填): 课程安排ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除课程安排成功"
}
```

---

## 设备管理

### 设备分类

#### 1. 获取设备分类列表

**接口地址**: `GET /api/v1/device-categories`

**描述**: 获取所有设备分类

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取设备分类列表成功",
  "data": [
    {
      "id": 1,
      "category_name": "计算机设备"
    },
    {
      "id": 2,
      "category_name": "实验仪器"
    }
  ]
}
```

#### 2. 创建设备分类

**接口地址**: `POST /api/v1/device-categories`

**描述**: 创建新的设备分类

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "category_name": "网络设备"
}
```

**参数说明**:
- `category_name` (string, 必填): 分类名称，唯一

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建设备分类成功",
  "data": {
    "id": 3,
    "category_name": "网络设备"
  }
}
```

#### 3. 获取设备分类详情

**接口地址**: `GET /api/v1/device-categories/{category_id}`

**描述**: 获取指定设备分类详情

**路径参数**:
- `category_id` (int, 必填): 设备分类ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取设备分类详情成功",
  "data": {
    "id": 1,
    "category_name": "计算机设备"
  }
}
```

#### 4. 更新设备分类

**接口地址**: `PUT /api/v1/device-categories/{category_id}`

**描述**: 更新指定设备分类

**认证**: 需要JWT令牌

**路径参数**:
- `category_id` (int, 必填): 设备分类ID

**请求参数**:
```json
{
  "category_name": "更新的分类名称"
}
```

**参数说明**:
- `category_name` (string, 必填): 分类名称，唯一

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "更新设备分类成功",
  "data": {
    "id": 1,
    "category_name": "更新的分类名称"
  }
}
```

#### 5. 删除设备分类

**接口地址**: `DELETE /api/v1/device-categories/{category_id}`

**描述**: 删除指定设备分类

**认证**: 需要JWT令牌

**路径参数**:
- `category_id` (int, 必填): 设备分类ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除设备分类成功"
}
```

### 设备管理

#### 1. 获取设备列表

**接口地址**: `GET /api/v1/devices`

**描述**: 获取设备列表（分页）

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10
- `category_id` (int, 可选): 设备分类ID筛选
- `status` (string, 可选): 设备状态筛选

**设备状态**:
- `可用`
- `使用中`
- `维修中`
- `报废`

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取设备列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "device_name": "高性能服务器",
        "category_id": 1,
        "category_name": "计算机设备",
        "model": "Dell PowerEdge R740",
        "status": "可用",
        "location": "机房A-01",
        "image_url": "https://example.com/device1.jpg",
        "purchase_date": "2024-01-01"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

#### 2. 创建设备

**接口地址**: `POST /api/v1/devices`

**描述**: 创建新设备

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "device_name": "激光打印机",
  "category_id": 1,
  "model": "HP LaserJet Pro",
  "status": "可用",
  "location": "办公室A-101",
  "image_url": "https://example.com/printer.jpg",
  "purchase_date": "2024-01-15"
}
```

**参数说明**:
- `device_name` (string, 必填): 设备名称
- `category_id` (int, 必填): 设备分类ID
- `model` (string, 可选): 设备型号
- `status` (string, 可选): 设备状态，默认"可用"
- `location` (string, 可选): 存放位置
- `image_url` (string, 可选): 设备图片URL
- `purchase_date` (string, 可选): 购置日期，格式YYYY-MM-DD

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建设备成功",
  "data": {
    "id": 2,
    "device_name": "激光打印机",
    "category_id": 1,
    "category_name": "计算机设备",
    "model": "HP LaserJet Pro",
    "status": "可用",
    "location": "办公室A-101",
    "image_url": "https://example.com/printer.jpg",
    "purchase_date": "2024-01-15"
  }
}
```

#### 3. 获取设备详情

**接口地址**: `GET /api/v1/devices/{device_id}`

**描述**: 获取指定设备详情

**路径参数**:
- `device_id` (int, 必填): 设备ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取设备详情成功",
  "data": {
    "id": 1,
    "device_name": "高性能服务器",
    "category_id": 1,
    "category_name": "计算机设备",
    "model": "Dell PowerEdge R740",
    "status": "可用",
    "location": "机房A-01",
    "image_url": "https://example.com/device1.jpg",
    "purchase_date": "2024-01-01"
  }
}
```

#### 4. 更新设备

**接口地址**: `PUT /api/v1/devices/{device_id}`

**描述**: 更新指定设备信息

**认证**: 需要JWT令牌

**路径参数**:
- `device_id` (int, 必填): 设备ID

**请求参数**:
```json
{
  "device_name": "更新的设备名称",
  "category_id": 2,
  "model": "新型号",
  "status": "维修中",
  "location": "新位置",
  "image_url": "https://example.com/new-device.jpg",
  "purchase_date": "2024-02-01"
}
```

**参数说明**: 同创建设备，所有字段均为可选

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "更新设备信息成功",
  "data": {
    "id": 1,
    "device_name": "更新的设备名称",
    "category_id": 2,
    "category_name": "实验仪器",
    "model": "新型号",
    "status": "维修中",
    "location": "新位置",
    "image_url": "https://example.com/new-device.jpg",
    "purchase_date": "2024-02-01"
  }
}
```

#### 5. 删除设备

**接口地址**: `DELETE /api/v1/devices/{device_id}`

**描述**: 删除指定设备

**认证**: 需要JWT令牌

**路径参数**:
- `device_id` (int, 必填): 设备ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除设备成功"
}
```

---

## 考勤管理

### 1. 获取考勤记录列表

**接口地址**: `GET /api/v1/attendance`

**描述**: 获取考勤记录列表（分页）

**认证**: 需要JWT令牌

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10
- `user_id` (int, 可选): 用户ID筛选
- `full_name` (string, 可选): 用户真名模糊筛选（支持部分匹配）
- `date` (string, 可选): 日期筛选，格式YYYY-MM-DD
- `method` (string, 可选): 考勤方式筛选

**考勤方式**:
- `人脸识别`
- `扫码`
- `手动`

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取考勤记录列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "user_id": 1,
        "user_name": "张三",
        "check_in_time": "2024-01-15T08:30:00Z",
        "check_out_time": "2024-01-15T17:30:00Z",
        "method": "人脸识别",
        "emotion_status": "正常"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 签到

**接口地址**: `POST /api/v1/attendance/check-in`

**描述**: 用户签到

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "method": "人脸识别",
  "emotion_status": "正常"
}
```

**参数说明**:
- `method` (string, 可选): 考勤方式，默认"手动"
- `emotion_status` (string, 可选): 情绪状态

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "签到成功",
  "data": {
    "id": 2,
    "user_id": 1,
    "user_name": "张三",
    "check_in_time": "2024-01-16T08:30:00Z",
    "check_out_time": null,
    "method": "人脸识别",
    "emotion_status": "正常"
  }
}
```

### 3. 签出

**接口地址**: `PUT /api/v1/attendance/{log_id}/check-out`

**描述**: 用户签出

**认证**: 需要JWT令牌

**路径参数**:
- `log_id` (int, 必填): 考勤记录ID

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "签出成功",
  "data": {
    "id": 2,
    "user_id": 1,
    "user_name": "张三",
    "check_in_time": "2024-01-16T08:30:00Z",
    "check_out_time": "2024-01-16T17:30:00Z",
    "method": "人脸识别",
    "emotion_status": "正常"
  }
}
```

### 4. 获取今日考勤状态

**接口地址**: `GET /api/v1/attendance/today`

**描述**: 获取当前用户今日考勤状态

**认证**: 需要JWT令牌

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取今日考勤状态成功",
  "data": {
    "id": 2,
    "user_id": 1,
    "user_name": "张三",
    "check_in_time": "2024-01-16T08:30:00Z",
    "check_out_time": null,
    "method": "人脸识别",
    "emotion_status": "正常"
  }
}
```

### 5. 获取考勤统计

**接口地址**: `GET /api/v1/attendance/statistics`

**描述**: 获取考勤统计数据

**认证**: 需要JWT令牌

**查询参数**:
- `user_id` (int, 可选): 用户ID，默认当前用户
- `full_name` (string, 可选): 用户真名，优先级高于user_id
- `start_date` (string, 可选): 开始日期，格式YYYY-MM-DD
- `end_date` (string, 可选): 结束日期，格式YYYY-MM-DD

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取考勤统计成功",
  "data": {
    "total_days": 20,
    "attendance_days": 18,
    "absence_days": 2,
    "attendance_rate": 0.9,
    "average_check_in_time": "08:25:00",
    "average_check_out_time": "17:35:00"
  }
}
```

### 6. 管理员控制签到

**接口地址**: `POST /api/v1/attendance/admin/check-in`

**描述**: 管理员为指定用户进行签到操作

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "full_name": "张三",
  "method": "手动",
  "emotion_status": "正常"
}
```

**参数说明**:

- `full_name` (string, 必填): 要签到的用户真名
- `method` (string, 可选): 考勤方式，默认"手动"
- `emotion_status` (string, 可选): 情绪状态

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "管理员为 张三 签到成功",
  "data": {
    "id": 3,
    "user_id": 1,
    "full_name": "张三",
    "check_in_time": "2024-01-16T08:30:00Z",
    "check_out_time": null,
    "method": "手动",
    "emotion_status": "正常"
  }
}
```

### 7. 管理员控制签退

**接口地址**: `POST /api/v1/attendance/admin/check-out`

**描述**: 管理员为指定用户进行签退操作

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "full_name": "张三"
}
```

**参数说明**:
- `full_name` (string, 必填): 要签退的用户真名

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "管理员为 张三 签退成功",
  "data": {
    "id": 3,
    "user_id": 1,
    "full_name": "张三",
    "check_in_time": "2024-01-16T08:30:00Z",
    "check_out_time": "2024-01-16T17:30:00Z",
    "method": "手动",
    "emotion_status": "正常"
  }
}
```

---

## 实验室信息

### 1. 获取实验室列表

**接口地址**: `GET /api/v1/labs`

**描述**: 获取所有实验室信息

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取实验室信息列表成功",
  "data": [
    {
      "id": 1,
      "lab_name": "人工智能实验室",
      "description": "专注于AI技术研究与应用",
      "culture_info": {
        "vision": "成为AI领域的领先实验室",
        "mission": "推动AI技术创新与应用",
        "values": ["创新", "协作", "卓越"]
      },
      "logo_url": "https://example.com/lab-logo.jpg"
    }
  ]
}
```

### 2. 创建实验室

**接口地址**: `POST /api/v1/labs`

**描述**: 创建新的实验室信息

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "lab_name": "机器学习实验室",
  "description": "专注于机器学习算法研究",
  "culture_info": {
    "vision": "推动机器学习技术发展",
    "mission": "培养优秀的ML人才",
    "values": ["学习", "创新", "分享"]
  },
  "logo_url": "https://example.com/ml-lab-logo.jpg"
}
```

**参数说明**:
- `lab_name` (string, 必填): 实验室名称，唯一
- `description` (string, 可选): 实验室描述
- `culture_info` (object, 可选): 文化理念信息
- `logo_url` (string, 可选): 实验室Logo URL

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建实验室信息成功",
  "data": {
    "id": 2,
    "lab_name": "机器学习实验室",
    "description": "专注于机器学习算法研究",
    "culture_info": {
      "vision": "推动机器学习技术发展",
      "mission": "培养优秀的ML人才",
      "values": ["学习", "创新", "分享"]
    },
    "logo_url": "https://example.com/ml-lab-logo.jpg"
  }
}
```

### 3. 获取实验室详情

**接口地址**: `GET /api/v1/labs/{lab_id}`

**描述**: 获取指定实验室详情

**路径参数**:
- `lab_id` (int, 必填): 实验室ID

**响应示例**: 同创建实验室的响应

### 4. 更新实验室信息

**接口地址**: `PUT /api/v1/labs/{lab_id}`

**描述**: 更新指定实验室信息

**认证**: 需要JWT令牌

**路径参数**:
- `lab_id` (int, 必填): 实验室ID

**请求参数**: 同创建实验室，所有字段均为可选

**响应示例**: 同获取实验室详情

### 5. 删除实验室

**接口地址**: `DELETE /api/v1/labs/{lab_id}`

**描述**: 删除指定实验室

**认证**: 需要JWT令牌

**路径参数**:
- `lab_id` (int, 必填): 实验室ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除实验室信息成功"
}
```

### 6. 获取默认实验室信息

**接口地址**: `GET /api/v1/labs/default`

**描述**: 获取默认实验室信息

**请求参数**: 无

**响应示例**: 同获取实验室详情

---

## 安全须知

### 1. 获取安全须知列表

**接口地址**: `GET /api/v1/safety`

**描述**: 获取安全须知列表（分页）

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10
- `category` (string, 可选): 安全类别筛选
- `is_important` (boolean, 可选): 是否重要

**安全类别**:
- `实验安全`
- `设备安全`
- `消防安全`
- `用电安全`
- `化学安全`

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取安全须知列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "title": "实验室用电安全规范",
        "content": "1. 使用电器设备前检查电源线是否完好\n2. 不得私拉乱接电线\n3. 离开实验室时关闭所有电源",
        "category": "用电安全",
        "is_important": true,
        "created_at": "2024-01-01T12:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 创建安全须知

**接口地址**: `POST /api/v1/safety`

**描述**: 创建新的安全须知

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "title": "化学试剂安全使用规范",
  "content": "1. 使用化学试剂前阅读安全数据表\n2. 佩戴适当的防护用品\n3. 在通风良好的环境中操作",
  "category": "化学安全",
  "is_important": true
}
```

**参数说明**:
- `title` (string, 必填): 安全须知标题
- `content` (string, 必填): 安全须知内容
- `category` (string, 必填): 安全类别
- `is_important` (boolean, 可选): 是否重要，默认false

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建安全须知成功",
  "data": {
    "id": 2,
    "title": "化学试剂安全使用规范",
    "content": "1. 使用化学试剂前阅读安全数据表\n2. 佩戴适当的防护用品\n3. 在通风良好的环境中操作",
    "category": "化学安全",
    "is_important": true,
    "created_at": "2024-01-01T12:00:00Z"
  }
}
```

### 3. 获取安全须知详情

**接口地址**: `GET /api/v1/safety/{safety_id}`

**描述**: 获取指定安全须知详情

**路径参数**:
- `safety_id` (int, 必填): 安全须知ID

**响应示例**: 同创建安全须知的响应

### 4. 更新安全须知

**接口地址**: `PUT /api/v1/safety/{safety_id}`

**描述**: 更新指定安全须知

**认证**: 需要JWT令牌

**路径参数**:
- `safety_id` (int, 必填): 安全须知ID

**请求参数**: 同创建安全须知，所有字段均为可选

**响应示例**: 同获取安全须知详情

### 5. 删除安全须知

**接口地址**: `DELETE /api/v1/safety/{safety_id}`

**描述**: 删除指定安全须知

**认证**: 需要JWT令牌

**路径参数**:
- `safety_id` (int, 必填): 安全须知ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除安全须知成功"
}
```

### 6. 获取安全类别列表

**接口地址**: `GET /api/v1/safety/categories`

**描述**: 获取所有安全类别

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取安全类别列表成功",
  "data": [
    "实验安全",
    "设备安全",
    "消防安全",
    "用电安全",
    "化学安全"
  ]
}
```

---

## AI培养方案

### 1. 获取培养方案列表

**接口地址**: `GET /api/v1/ai-training`

**描述**: 获取AI个性化培养方案列表（分页）

**认证**: 需要JWT令牌

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10
- `user_id` (int, 可选): 用户ID筛选
- `status` (string, 可选): 状态筛选

**方案状态**:
- `进行中`
- `已完成`
- `暂停`

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取AI培养方案列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "user_id": 1,
        "user_name": "张三",
        "plan_name": "Python编程进阶计划",
        "description": "针对Python编程能力提升的个性化学习方案",
        "goals": ["掌握高级Python特性", "学会使用常用框架", "完成实际项目"],
        "current_progress": 65,
        "status": "进行中",
        "start_date": "2024-01-01",
        "end_date": "2024-06-30",
        "created_at": "2024-01-01T12:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 创建培养方案

**接口地址**: `POST /api/v1/ai-training`

**描述**: 创建新的AI培养方案

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "user_id": 2,
  "plan_name": "机器学习入门计划",
  "description": "从零开始学习机器学习的系统性方案",
  "goals": ["理解机器学习基本概念", "掌握常用算法", "完成实战项目"],
  "start_date": "2024-02-01",
  "end_date": "2024-08-31"
}
```

**参数说明**:
- `user_id` (int, 必填): 用户ID
- `plan_name` (string, 必填): 方案名称
- `description` (string, 可选): 方案描述
- `goals` (array, 可选): 学习目标列表
- `start_date` (string, 可选): 开始日期，格式YYYY-MM-DD
- `end_date` (string, 可选): 结束日期，格式YYYY-MM-DD

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建AI培养方案成功",
  "data": {
    "id": 2,
    "user_id": 2,
    "user_name": "李四",
    "plan_name": "机器学习入门计划",
    "description": "从零开始学习机器学习的系统性方案",
    "goals": ["理解机器学习基本概念", "掌握常用算法", "完成实战项目"],
    "current_progress": 0,
    "status": "进行中",
    "start_date": "2024-02-01",
    "end_date": "2024-08-31",
    "created_at": "2024-01-01T12:00:00Z"
  }
}
```

### 3. 获取培养方案详情

**接口地址**: `GET /api/v1/ai-training/{plan_id}`

**描述**: 获取指定培养方案详情

**认证**: 需要JWT令牌

**路径参数**:
- `plan_id` (int, 必填): 培养方案ID

**响应示例**: 同创建培养方案的响应

### 4. 更新培养方案

**接口地址**: `PUT /api/v1/ai-training/{plan_id}`

**描述**: 更新指定培养方案

**认证**: 需要JWT令牌

**路径参数**:
- `plan_id` (int, 必填): 培养方案ID

**请求参数**: 同创建培养方案，所有字段均为可选，另外可包含：
- `current_progress` (int, 可选): 当前进度（0-100）
- `status` (string, 可选): 方案状态

**响应示例**: 同获取培养方案详情

### 5. 删除培养方案

**接口地址**: `DELETE /api/v1/ai-training/{plan_id}`

**描述**: 删除指定培养方案

**认证**: 需要JWT令牌

**路径参数**:
- `plan_id` (int, 必填): 培养方案ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除AI培养方案成功"
}
```

### 6. 获取当前用户培养方案

**接口地址**: `GET /api/v1/ai-training/my-plans`

**描述**: 获取当前登录用户的所有培养方案

**认证**: 需要JWT令牌

**请求参数**: 无

**响应示例**: 同获取培养方案列表，但只包含当前用户的方案

---

## 环境监测

### 1. 获取监测日志列表

**接口地址**: `GET /api/v1/environmental`

**描述**: 获取环境监测日志列表（分页）

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10
- `start_date` (string, 可选): 开始日期，格式YYYY-MM-DD
- `end_date` (string, 可选): 结束日期，格式YYYY-MM-DD

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取环境监测日志列表成功",
  "data": {
    "items": [
      {
        "id": 1,
        "temperature": 23.5,
        "humidity": 45.2,
        "air_quality": 85,
        "noise_level": 42.3,
        "light_intensity": 350,
        "recorded_at": "2024-01-15T14:30:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 创建监测日志

**接口地址**: `POST /api/v1/environmental`

**描述**: 创建新的环境监测日志

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "temperature": 24.0,
  "humidity": 48.5,
  "air_quality": 88,
  "noise_level": 40.1,
  "light_intensity": 380
}
```

**参数说明**:
- `temperature` (float, 可选): 温度（摄氏度）
- `humidity` (float, 可选): 湿度（百分比）
- `air_quality` (int, 可选): 空气质量指数（0-100）
- `noise_level` (float, 可选): 噪音水平（分贝）
- `light_intensity` (int, 可选): 光照强度（勒克斯）

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "创建环境监测日志成功",
  "data": {
    "id": 2,
    "temperature": 24.0,
    "humidity": 48.5,
    "air_quality": 88,
    "noise_level": 40.1,
    "light_intensity": 380,
    "recorded_at": "2024-01-15T15:00:00Z"
  }
}
```

### 3. 获取监测日志详情

**接口地址**: `GET /api/v1/environmental/{log_id}`

**描述**: 获取指定监测日志详情

**路径参数**:
- `log_id` (int, 必填): 监测日志ID

**响应示例**: 同创建监测日志的响应

### 4. 更新监测日志

**接口地址**: `PUT /api/v1/environmental/{log_id}`

**描述**: 更新指定监测日志

**认证**: 需要JWT令牌

**路径参数**:
- `log_id` (int, 必填): 监测日志ID

**请求参数**: 同创建监测日志，所有字段均为可选

**响应示例**: 同获取监测日志详情

### 5. 删除监测日志

**接口地址**: `DELETE /api/v1/environmental/{log_id}`

**描述**: 删除指定监测日志

**认证**: 需要JWT令牌

**路径参数**:
- `log_id` (int, 必填): 监测日志ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "删除环境监测日志成功"
}
```

### 6. 获取最新监测数据

**接口地址**: `GET /api/v1/environmental/latest`

**描述**: 获取最新的环境监测数据

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取最新环境监测数据成功",
  "data": {
    "id": 2,
    "temperature": 24.0,
    "humidity": 48.5,
    "air_quality": 88,
    "noise_level": 40.1,
    "light_intensity": 380,
    "recorded_at": "2024-01-15T15:00:00Z"
  }
}
```

### 7. 获取监测统计数据

**接口地址**: `GET /api/v1/environmental/statistics`

**描述**: 获取环境监测统计数据

**查询参数**:
- `start_date` (string, 可选): 开始日期，格式YYYY-MM-DD
- `end_date` (string, 可选): 结束日期，格式YYYY-MM-DD
- `period` (string, 可选): 统计周期，可选值：day、week、month

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取环境监测统计数据成功",
  "data": {
    "period": "day",
    "statistics": {
      "temperature": {
        "avg": 23.8,
        "min": 22.1,
        "max": 25.3
      },
      "humidity": {
        "avg": 46.7,
        "min": 42.0,
        "max": 52.1
      },
      "air_quality": {
        "avg": 86.5,
        "min": 82,
        "max": 92
      },
      "noise_level": {
        "avg": 41.2,
        "min": 38.5,
        "max": 45.8
      },
      "light_intensity": {
        "avg": 365,
        "min": 320,
        "max": 420
      }
    }
  }
}
```

---

## 文件上传

### 1. 单个图片上传

**接口地址**: `POST /api/v1/upload/image`

**描述**: 上传单个图片文件

**认证**: 需要JWT令牌

**请求方式**: multipart/form-data

**请求参数**:
- `file` (file, 必填): 图片文件

**支持格式**: jpg, jpeg, png, gif, bmp, webp

**文件大小限制**: 最大5MB

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "图片上传成功",
  "data": {
    "filename": "20240115_143022_abc123.jpg",
    "original_filename": "photo.jpg",
    "file_size": 1024000,
    "file_type": "image/jpeg",
    "url": "http://sys.sharpcaterpillar.top:5000/uploads/20240115_143022_abc123.jpg",
    "upload_time": "2024-01-15T14:30:22Z"
  }
}
```

### 2. 批量图片上传

**接口地址**: `POST /api/v1/upload/multiple`

**描述**: 批量上传多个图片文件

**认证**: 需要JWT令牌

**请求方式**: multipart/form-data

**请求参数**:
- `files` (file[], 必填): 多个图片文件

**文件数量限制**: 最多10个文件

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "批量上传成功",
  "data": {
    "uploaded_files": [
      {
        "filename": "20240115_143022_abc123.jpg",
        "original_filename": "photo1.jpg",
        "file_size": 1024000,
        "file_type": "image/jpeg",
        "url": "http://sys.sharpcaterpillar.top:5000/uploads/20240115_143022_abc123.jpg",
        "upload_time": "2024-01-15T14:30:22Z"
      },
      {
        "filename": "20240115_143023_def456.png",
        "original_filename": "photo2.png",
        "file_size": 2048000,
        "file_type": "image/png",
        "url": "http://sys.sharpcaterpillar.top:5000/uploads/20240115_143023_def456.png",
        "upload_time": "2024-01-15T14:30:23Z"
      }
    ],
    "total_uploaded": 2,
    "failed_files": []
  }
}
```

### 3. 获取上传配置

**接口地址**: `GET /api/v1/upload/info`

**描述**: 获取文件上传配置信息

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取上传配置成功",
  "data": {
    "max_file_size": 5242880,
    "max_file_size_mb": 5,
    "allowed_extensions": ["jpg", "jpeg", "png", "gif", "bmp", "webp"],
    "max_files_per_upload": 10,
    "upload_path": "/uploads/",
    "base_url": "http://sys.sharpcaterpillar.top:5000"
  }
}
```

---

## 📋 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问，权限不足 |
| 404 | 资源不存在 |
| 409 | 资源冲突，如用户名已存在 |
| 422 | 请求参数验证失败 |
| 500 | 服务器内部错误 |

## 📋 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": {}
}
```

### 分页响应
```json
{
  "code": 200,
  "success": true,
  "message": "获取列表成功",
  "data": {
    "items": [],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 100,
      "total_pages": 10,
      "has_next": true,
      "has_prev": false
    }
  }
}
```

### 错误响应
```json
{
  "code": 400,
  "success": false,
  "message": "请求参数错误",
  "error": "详细错误信息"
}
```

## 📋 使用示例

### 1. 登录获取Token
```bash
curl -X POST http://sys.sharpcaterpillar.top:5000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

### 2. 使用Token访问API
```bash
curl -X GET http://sys.sharpcaterpillar.top:5000/api/v1/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 3. 创建资源
```bash
curl -X POST http://sys.sharpcaterpillar.top:5000/api/v1/announcements \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "新公告",
    "content": "公告内容",
    "type": "通知",
    "is_important": true
  }'
```

### 4. 上传文件
```bash
curl -X POST http://sys.sharpcaterpillar.top:5000/api/v1/upload/image \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

---

## 用户个人资料

### 1. 获取用户个人资料列表

**接口地址**: `GET /api/v1/user-profiles`

**描述**: 获取用户个人资料列表（分页）

**认证**: 需要JWT令牌

**查询参数**:
- `page` (int, 可选): 页码，默认1
- `limit` (int, 可选): 每页数量，默认10，最大100
- `search` (string, 可选): 搜索用户名或姓名
- `position` (string, 可选): 按职务筛选

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取用户个人资料列表成功",
  "data": {
    "items": [
      {
        "user_id": 1,
        "gender": "男",
        "birth_date": "2000-01-15",
        "position": "实验室负责人",
        "dormitory": "1栋101室",
        "tech_stack": ["Python", "Flask", "MySQL", "Vue.js"],
        "user": {
          "id": 1,
          "username": "admin",
          "full_name": "管理员",
          "avatar_url": "http://example.com/avatar1.jpg"
        }
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 1,
      "total_pages": 1,
      "has_next": false,
      "has_prev": false
    }
  }
}
```

### 2. 获取指定用户个人资料

**接口地址**: `GET /api/v1/user-profiles/{user_id}`

**描述**: 获取指定用户的个人资料详情

**认证**: 需要JWT令牌

**路径参数**:
- `user_id` (int, 必填): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取用户个人资料成功",
  "data": {
    "user_id": 1,
    "gender": "男",
    "birth_date": "2000-01-15",
    "position": "实验室负责人",
    "dormitory": "1栋101室",
    "tech_stack": ["Python", "Flask", "MySQL", "Vue.js"],
    "user": {
      "id": 1,
      "username": "admin",
      "full_name": "管理员",
      "major": "计算机科学与技术",
      "class": "2022级1班",
      "email": "admin@example.com",
      "phone_number": "13800138000",
      "avatar_url": "http://example.com/avatar1.jpg",
      "created_at": "2024-01-01T12:00:00Z"
    }
  }
}
```

### 3. 创建用户个人资料

**接口地址**: `POST /api/v1/user-profiles`

**描述**: 创建用户个人资料

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "user_id": 1,
  "gender": "男",
  "birth_date": "2000-01-15",
  "position": "实验室负责人",
  "dormitory": "1栋101室",
  "tech_stack": ["Python", "Flask", "MySQL", "Vue.js"]
}
```

**参数说明**:
- `user_id` (int, 必填): 用户ID
- `gender` (string, 可选): 性别，可选值：男、女、保密，默认保密
- `birth_date` (string, 可选): 出生日期，格式：YYYY-MM-DD
- `position` (string, 可选): 职务
- `dormitory` (string, 可选): 宿舍信息
- `tech_stack` (array, 可选): 技术栈数组

**响应示例**:
```json
{
  "code": 201,
  "success": true,
  "message": "用户个人资料创建成功",
  "data": {
    "user_id": 1,
    "gender": "男",
    "birth_date": "2000-01-15",
    "position": "实验室负责人",
    "dormitory": "1栋101室",
    "tech_stack": ["Python", "Flask", "MySQL", "Vue.js"],
    "user": {
      "id": 1,
      "username": "admin",
      "full_name": "管理员",
      "avatar_url": "http://example.com/avatar1.jpg"
    }
  }
}
```

### 4. 更新用户个人资料

**接口地址**: `PUT /api/v1/user-profiles/{user_id}`

**描述**: 更新指定用户的个人资料

**认证**: 需要JWT令牌

**路径参数**:
- `user_id` (int, 必填): 用户ID

**请求参数**:
```json
{
  "gender": "女",
  "birth_date": "2001-03-20",
  "position": "项目组长",
  "dormitory": "2栋205室",
  "tech_stack": ["Java", "Spring Boot", "React", "PostgreSQL"]
}
```

**参数说明**: 所有字段均为可选，只更新提供的字段

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "用户个人资料更新成功",
  "data": {
    "user_id": 1,
    "gender": "女",
    "birth_date": "2001-03-20",
    "position": "项目组长",
    "dormitory": "2栋205室",
    "tech_stack": ["Java", "Spring Boot", "React", "PostgreSQL"],
    "user": {
      "id": 1,
      "username": "admin",
      "full_name": "管理员",
      "avatar_url": "http://example.com/avatar1.jpg"
    }
  }
}
```

### 5. 删除用户个人资料

**接口地址**: `DELETE /api/v1/user-profiles/{user_id}`

**描述**: 删除指定用户的个人资料

**认证**: 需要JWT令牌

**路径参数**:
- `user_id` (int, 必填): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "用户个人资料删除成功"
}
```

### 6. 获取当前用户个人资料

**接口地址**: `GET /api/v1/my-profile`

**描述**: 获取当前登录用户的个人资料

**认证**: 需要JWT令牌

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "获取个人资料成功",
  "data": {
    "user_id": 1,
    "gender": "男",
    "birth_date": "2000-01-15",
    "position": "实验室负责人",
    "dormitory": "1栋101室",
    "tech_stack": ["Python", "Flask", "MySQL", "Vue.js"],
    "user": {
      "id": 1,
      "username": "admin",
      "full_name": "管理员",
      "major": "计算机科学与技术",
      "class": "2022级1班",
      "email": "admin@example.com",
      "phone_number": "13800138000",
      "avatar_url": "http://example.com/avatar1.jpg",
      "created_at": "2024-01-01T12:00:00Z"
    }
  }
}
```

### 7. 更新当前用户个人资料

**接口地址**: `PUT /api/v1/my-profile`

**描述**: 更新当前登录用户的个人资料（如果不存在则自动创建）

**认证**: 需要JWT令牌

**请求参数**:
```json
{
  "gender": "男",
  "birth_date": "2000-01-15",
  "position": "实验室负责人",
  "dormitory": "1栋101室",
  "tech_stack": ["Python", "Flask", "MySQL", "Vue.js"]
}
```

**参数说明**: 所有字段均为可选

**响应示例**:
```json
{
  "code": 200,
  "success": true,
  "message": "个人资料更新成功",
  "data": {
    "user_id": 1,
    "gender": "男",
    "birth_date": "2000-01-15",
    "position": "实验室负责人",
    "dormitory": "1栋101室",
    "tech_stack": ["Python", "Flask", "MySQL", "Vue.js"],
    "user": {
      "id": 1,
      "username": "admin",
      "full_name": "管理员",
      "major": "计算机科学与技术",
      "class": "2022级1班",
      "email": "admin@example.com",
      "phone_number": "13800138000",
      "avatar_url": "http://example.com/avatar1.jpg",
      "created_at": "2024-01-01T12:00:00Z"
    }
  }
}
```

---

**文档版本**: v1.1  
**最后更新**: 2024-01-15  
**维护者**: 智慧实验室开发团队