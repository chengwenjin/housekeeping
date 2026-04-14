# 家政小程序 - API 接口设计文档 (管理后台端)

## 1. 接口设计规范

### 1.1 基础信息
- **协议**: HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: JWT Token (Bearer Auth)
- **权限控制**: RBAC 基于角色的访问控制

### 1.2 请求头 (Headers)
```
Content-Type: application/json
Authorization: Bearer <admin_token>
X-Requested-With: XMLHttpRequest
```

### 1.3 统一响应格式

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1711468800000
}
```

**错误响应**:
```json
{
  "code": 40001,
  "message": "参数错误",
  "data": null,
  "timestamp": 1711468800000
}
```

### 1.4 管理后台错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录/Token 失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 40001 | 管理员账号不存在 |
| 40002 | 密码错误 |
| 40003 | 账号已被禁用 |
| 40004 | 旧密码错误 |
| 40005 | 角色已被使用 |

## 2. 管理员认证接口

### 2.1 管理员登录

**接口**: `POST /api/admin/auth/login`

**描述**: 管理员账号密码登录

**请求参数**:
```json
{
  "username": "admin",
  "password": "encrypted_password"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 28800,
    "admin": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "avatar": "https://xxx.com/admin-avatar.png",
      "role": {
        "id": 1,
        "name": "超级管理员",
        "code": "super_admin"
      },
      "permissions": ["*"]
    }
  }
}
```

**备注**: 
- token 有效期 8 小时
- 密码需前端加密传输 (如 bcrypt + RSA)

### 2.2 获取当前管理员信息

**接口**: `GET /api/admin/auth/info`

**描述**: 获取当前登录管理员信息

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员",
    "avatar": "https://xxx.com/admin-avatar.png",
    "role": {
      "id": 1,
      "name": "超级管理员",
      "code": "super_admin",
      "permissions": ["*"]
    },
    "lastLoginAt": "2026-03-26 10:00:00",
    "lastLoginIp": "192.168.1.100"
  }
}
```

### 2.3 修改密码

**接口**: `PUT /api/admin/auth/password`

**描述**: 修改管理员密码

**请求参数**:
```json
{
  "oldPassword": "old_encrypted_password",
  "newPassword": "new_encrypted_password"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

### 2.4 退出登录

**接口**: `POST /api/admin/auth/logout`

**描述**: 退出登录，使 token 失效

**响应数据**:
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

## 3. 用户管理接口

### 3.1 获取用户列表

**接口**: `GET /api/admin/users`

**描述**: 获取用户列表 (支持筛选、分页)

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 20
- keyword: 关键词 (昵称/手机号)
- certificationStatus: 2 (0-未认证 1-认证中 2-已认证 3-认证失败)
- status: 1 (0-禁用 1-正常)
- sortBy: created_at (created_at/score/total_orders)
- sortOrder: desc (asc/desc)
- startDate: 2026-01-01 (注册时间起始)
- endDate: 2026-03-26 (注册时间结束)
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 2001,
        "openid": "oXXXX_openid",
        "nickname": "张小华",
        "avatarUrl": "https://xxx.com/avatar.png",
        "gender": 1,
        "phone": "138****1234",
        "realName": "张*",
        "certificationStatus": 2,
        "score": 4.9,
        "totalOrders": 8,
        "publishedCount": 12,
        "takenCount": 8,
        "followerCount": 34,
        "followingCount": 56,
        "status": 1,
        "statusText": "正常",
        "lastLoginAt": "2026-03-26 09:30:00",
        "createdAt": "2026-01-15 10:00:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 1580,
      "totalPages": 79
    },
    "stats": {
      "totalUsers": 1580,
      "todayNewUsers": 23,
      "activeUsers": 856
    }
  }
}
```

### 3.2 获取用户详情

**接口**: `GET /api/admin/users/:id`

**描述**: 获取用户详细信息

**路径参数**:
```
id: 2001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2001,
    "openid": "oXXXX_openid",
    "unionid": "uXXXX_unionid",
    "nickname": "张小华",
    "avatarUrl": "https://xxx.com/avatar.png",
    "gender": 1,
    "phone": "138****1234",
    "realName": "张**",
    "idCard": "110***********1234",
    "certificationStatus": 2,
    "certificationReason": "",
    "bio": "专业保洁服务，5 年经验",
    "score": 4.9,
    "totalOrders": 8,
    "publishedCount": 12,
    "takenCount": 8,
    "followerCount": 34,
    "followingCount": 56,
    "status": 1,
    "lastLoginAt": "2026-03-26 09:30:00",
    "lastLoginIp": "192.168.1.100",
    "createdAt": "2026-01-15 10:00:00",
    "recentDemands": [
      {
        "id": 1001,
        "title": "家庭日常保洁",
        "status": 2,
        "createdAt": "2026-03-25"
      }
    ],
    "recentOrders": [
      {
        "id": 3001,
        "orderNo": "ORD202603250001",
        "role": "publisher",
        "status": 2,
        "amount": 160.00,
        "createdAt": "2026-03-25"
      }
    ],
    "reviews": [
      {
        "id": 5001,
        "rating": 5,
        "content": "非常满意",
        "createdAt": "2026-03-20"
      }
    ]
  }
}
```

### 3.3 禁用/启用用户

**接口**: `PUT /api/admin/users/:id/status`

**描述**: 禁用或启用用户账号

**路径参数**:
```
id: 2001
```

**请求参数**:
```json
{
  "status": 0,
  "reason": "违规操作，恶意刷单"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 2001,
    "status": 0,
    "statusText": "已禁用"
  }
}
```

### 3.4 删除用户

**接口**: `DELETE /api/admin/users/:id`

**描述**: 删除用户 (软删除，谨慎操作)

**路径参数**:
```
id: 2001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 3.5 导出用户数据

**接口**: `POST /api/admin/users/export`

**描述**: 导出用户数据为 Excel

**请求参数**:
```json
{
  "ids": [2001, 2002, 2003],
  "fields": ["nickname", "phone", "realName", "totalOrders", "score"]
}
```

**响应**: 
- Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
- 文件下载

## 4. 需求管理接口

### 4.1 获取需求列表

**接口**: `GET /api/admin/demands`

**描述**: 获取需求列表 (支持筛选、分页)

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 20
- keyword: 关键词 (标题/描述)
- categoryId: 1 (分类 ID)
- city: 北京市
- status: 1 (1-招募中 2-已接单 3-进行中 4-已完成 5-已取消 6-已过期)
- startDate: 2026-01-01
- endDate: 2026-03-26
- sortBy: created_at
- sortOrder: desc
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1001,
        "title": "家庭日常保洁，约 2-3 小时",
        "category": {
          "id": 1,
          "name": "保洁"
        },
        "publisher": {
          "id": 2001,
          "nickname": "李女士"
        },
        "expectedPrice": 80.00,
        "priceUnit": "小时",
        "location": {
          "city": "北京市",
          "district": "朝阳区"
        },
        "viewCount": 156,
        "footprintCount": 23,
        "status": 2,
        "statusText": "已接单",
        "taker": {
          "id": 3001,
          "nickname": "王师傅"
        },
        "createdAt": "2026-03-25 10:30:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 3580,
      "totalPages": 179
    },
    "stats": {
      "totalDemands": 3580,
      "recruitingCount": 856,
      "takenCount": 1245,
      "completedCount": 1389,
      "cancelledCount": 90
    }
  }
}
```

### 4.2 获取需求详情

**接口**: `GET /api/admin/demands/:id`

**描述**: 获取需求详细信息

**路径参数**:
```
id: 1001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1001,
    "title": "家庭日常保洁，约 2-3 小时",
    "description": "120㎡三居室，需要打扫卫生间、厨房油烟机、地板吸尘拖地。",
    "category": {
      "id": 1,
      "name": "保洁",
      "icon": "🧹"
    },
    "publisher": {
      "id": 2001,
      "nickname": "李女士",
      "phone": "138****1234",
      "avatarUrl": "https://xxx.com/avatar.png"
    },
    "serviceType": 1,
    "expectedPrice": 80.00,
    "priceUnit": "小时",
    "location": {
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "address": "建国路 88 号",
      "latitude": 39.9042,
      "longitude": 116.4074
    },
    "serviceTime": "2026-03-27 09:00:00",
    "serviceTimeDesc": "明天 09:00-12:00",
    "contactName": "李女士",
    "contactPhone": "138****1234",
    "images": ["https://xxx.com/img1.jpg"],
    "viewCount": 156,
    "footprintCount": 23,
    "status": 2,
    "statusText": "已接单",
    "taker": {
      "id": 3001,
      "nickname": "王师傅",
      "phone": "139****5678"
    },
    "takenAt": "2026-03-25 11:00:00",
    "order": {
      "id": 3001,
      "orderNo": "ORD202603250001",
      "status": 2
    },
    "createdAt": "2026-03-25 10:30:00",
    "updatedAt": "2026-03-25 11:00:00"
  }
}
```

### 4.3 下架需求

**接口**: `PUT /api/admin/demands/:id/offline`

**描述**: 强制下架违规需求

**路径参数**:
```
id: 1001
```

**请求参数**:
```json
{
  "reason": "内容违规，包含虚假信息",
  "notifyUser": true
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "下架成功",
  "data": {
    "id": 1001,
    "status": 5,
    "statusText": "已取消"
  }
}
```

### 4.4 删除需求

**接口**: `DELETE /api/admin/demands/:id`

**描述**: 删除需求 (硬删除)

**路径参数**:
```
id: 1001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

## 5. 订单管理接口

### 5.1 获取订单列表

**接口**: `GET /api/admin/orders`

**描述**: 获取订单列表 (支持筛选、分页)

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 20
- orderNo: ORD202603250001 (订单号)
- keyword: 关键词 (标题/用户名)
- categoryId: 1
- status: 1 (1-待服务 2-服务中 3-待确认 4-已完成 5-已取消 6-已评价)
- paymentStatus: 1 (0-未支付 1-已支付)
- startDate: 2026-01-01
- endDate: 2026-03-26
- sortBy: created_at
- sortOrder: desc
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 3001,
        "orderNo": "ORD202603250001",
        "title": "家庭日常保洁",
        "category": {
          "name": "保洁"
        },
        "publisher": {
          "id": 2001,
          "nickname": "李女士"
        },
        "taker": {
          "id": 3001,
          "nickname": "王师傅"
        },
        "actualPrice": 80.00,
        "priceUnit": "小时",
        "totalAmount": 160.00,
        "paymentStatus": 1,
        "paymentStatusText": "已支付",
        "status": 4,
        "statusText": "已完成",
        "serviceTime": "2026-03-27 09:00:00",
        "completedAt": "2026-03-27 12:00:00",
        "createdAt": "2026-03-25 10:30:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 2156,
      "totalPages": 108
    },
    "stats": {
      "totalOrders": 2156,
      "pendingCount": 125,
      "processingCount": 356,
      "completedCount": 1589,
      "cancelledCount": 86,
      "totalAmount": 458960.00,
      "todayAmount": 12580.00
    }
  }
}
```

### 5.2 获取订单详情

**接口**: `GET /api/admin/orders/:id`

**描述**: 获取订单详细信息

**路径参数**:
```
id: 3001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 3001,
    "orderNo": "ORD202603250001",
    "demandId": 1001,
    "publisher": {
      "id": 2001,
      "nickname": "李女士",
      "phone": "138****1234",
      "avatarUrl": "https://xxx.com/avatar.png"
    },
    "taker": {
      "id": 3001,
      "nickname": "王师傅",
      "phone": "139****5678",
      "avatarUrl": "https://xxx.com/avatar2.png",
      "score": 4.8
    },
    "title": "家庭日常保洁",
    "description": "120㎡三居室...",
    "category": {
      "id": 1,
      "name": "保洁"
    },
    "serviceTime": "2026-03-27 09:00:00",
    "location": {
      "address": "朝阳区建国路 88 号",
      "latitude": 39.9042,
      "longitude": 116.4074
    },
    "actualPrice": 80.00,
    "priceUnit": "小时",
    "actualDuration": 2.5,
    "totalAmount": 200.00,
    "paymentStatus": 1,
    "paymentStatusText": "已支付",
    "paymentMethod": 1,
    "paymentMethodText": "微信支付",
    "paidAt": "2026-03-27 12:30:00",
    "status": 6,
    "statusText": "已评价",
    "start_time": "2026-03-27 09:00:00",
    "end_time": "2026-03-27 11:30:00",
    "completedAt": "2026-03-27 12:00:00",
    "review": {
      "id": 5001,
      "rating": 5,
      "content": "非常满意，服务很专业",
      "images": ["https://xxx.com/review1.jpg"],
      "createdAt": "2026-03-27 14:00:00"
    },
    "remark": "我可以接单，请及时联系",
    "operationLogs": [
      {
        "action": "order_created",
        "text": "订单创建",
        "operator": "system",
        "timestamp": "2026-03-25 10:30:00"
      },
      {
        "action": "service_started",
        "text": "开始服务",
        "operator": "taker",
        "timestamp": "2026-03-27 09:00:00"
      },
      {
        "action": "service_completed",
        "text": "服务完成",
        "operator": "taker",
        "timestamp": "2026-03-27 11:30:00"
      }
    ],
    "createdAt": "2026-03-25 10:30:00",
    "updatedAt": "2026-03-27 14:00:00"
  }
}
```

### 5.5 订单退款

**接口**: `POST /api/admin/orders/:id/refund`

**描述**: 后台手动退款

**路径参数**:
```
id: 3001
```

**请求参数**:
```json
{
  "amount": 200.00,
  "reason": "服务质量问题，协商退款",
  "notifyUser": true
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "退款成功",
  "data": {
    "id": 3001,
    "paymentStatus": 2,
    "paymentStatusText": "已退款"
  }
}
```

### 5.6 取消订单

**接口**: `POST /api/admin/orders/:id/cancel`

**描述**: 后台强制取消订单

**路径参数**:
```
id: 3001
```

**请求参数**:
```json
{
  "reason": "用户投诉，核实后取消",
  "notifyBoth": true
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "取消成功",
  "data": null
}
```

## 6. 评价管理接口

### 6.1 获取评价列表

**接口**: `GET /api/admin/reviews`

**描述**: 获取评价列表 (支持筛选、分页)

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 20
- keyword: 关键词 (内容/用户名)
- rating: 5 (评分 1-5)
- type: all (all-全部 good-好评 normal-中评 bad-差评)
- startDate: 2026-01-01
- endDate: 2026-03-26
- sortBy: created_at
- sortOrder: desc
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 5001,
        "orderNo": "ORD202603250001",
        "reviewer": {
          "id": 2001,
          "nickname": "李女士"
        },
        "reviewee": {
          "id": 3001,
          "nickname": "王师傅"
        },
        "rating": 5,
        "content": "非常满意，服务很专业",
        "images": ["https://xxx.com/review1.jpg"],
        "isAnonymous": false,
        "replyContent": "感谢您的认可！",
        "replyAt": "2026-03-27 15:00:00",
        "helpfulCount": 12,
        "createdAt": "2026-03-27 14:00:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 1589,
      "totalPages": 80
    },
    "stats": {
      "totalReviews": 1589,
      "goodRate": 0.92,
      "avgRating": 4.7
    }
  }
}
```

### 6.2 删除评价

**接口**: `DELETE /api/admin/reviews/:id`

**描述**: 删除违规评价

**路径参数**:
```
id: 5001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 6.3 回复评价

**接口**: `POST /api/admin/reviews/:id/reply`

**描述**: 官方回复评价

**路径参数**:
```
id: 5001
```

**请求参数**:
```json
{
  "content": "感谢您的反馈，我们会继续提供优质服务！"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "回复成功",
  "data": {
    "id": 5001,
    "replyContent": "感谢您的反馈，我们会继续提供优质服务！",
    "replyAt": "2026-03-26 10:30:00"
  }
}
```

## 7. 分类管理接口

### 7.1 获取分类列表

**接口**: `GET /api/admin/categories`

**描述**: 获取所有分类 (树形结构)

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "保洁",
      "icon": "🧹",
      "parentId": 0,
      "level": 1,
      "sortOrder": 100,
      "isHot": true,
      "demandCount": 256,
      "status": 1,
      "children": []
    }
  ]
}
```

### 7.2 创建分类

**接口**: `POST /api/admin/categories`

**描述**: 新增服务分类

**请求参数**:
```json
{
  "name": "家电清洗",
  "icon": "🧼",
  "parentId": 0,
  "level": 1,
  "sortOrder": 30,
  "isHot": false,
  "description": "空调、冰箱、洗衣机等家电清洗"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 9,
    "name": "家电清洗",
    "createdAt": "2026-03-26 10:30:00"
  }
}
```

### 7.3 更新分类

**接口**: `PUT /api/admin/categories/:id`

**描述**: 更新分类信息

**路径参数**:
```
id: 1
```

**请求参数**:
```json
{
  "name": "保洁服务",
  "sortOrder": 110,
  "isHot": true
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 7.4 删除分类

**接口**: `DELETE /api/admin/categories/:id`

**描述**: 删除分类 (无需求时才可删除)

**路径参数**:
```
id: 9
```

**响应数据**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 7.5 启用/禁用分类

**接口**: `PUT /api/admin/categories/:id/status`

**描述**: 启用或禁用分类

**路径参数**:
```
id: 1
```

**请求参数**:
```json
{
  "status": 0,
  "reason": "分类调整，暂时禁用"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

## 8. 数据统计接口

### 8.1 首页数据概览

**接口**: `GET /api/admin/dashboard/overview`

**描述**: 获取首页统计数据

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "users": {
      "total": 15823,
      "todayNew": 156,
      "weekNew": 892,
      "monthNew": 3456,
      "active": 8567
    },
    "demands": {
      "total": 45678,
      "todayNew": 234,
      "recruiting": 1256,
      "completed": 42156
    },
    "orders": {
      "total": 38956,
      "todayNew": 189,
      "pending": 456,
      "processing": 1234,
      "completed": 36789,
      "totalAmount": 2567890.00,
      "todayAmount": 45670.00
    },
    "reviews": {
      "total": 35678,
      "goodRate": 0.92,
      "avgRating": 4.7
    },
    "conversion": {
      "demandToOrderRate": 0.85,
      "avgTakeTime": 2.5
    }
  }
}
```

### 8.2 趋势图表数据

**接口**: `GET /api/admin/dashboard/trend`

**描述**: 获取趋势图表数据

**请求参数**:
```
Query Parameters:
- metric: users (users/demands/orders/amount)
- dimension: day (day/week/month)
- startDate: 2026-01-01
- endDate: 2026-03-26
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "metric": "orders",
    "dimension": "day",
    "series": [
      {
        "date": "2026-03-20",
        "value": 156,
        "amount": 12580.00
      },
      {
        "date": "2026-03-21",
        "value": 189,
        "amount": 15670.00
      }
    ]
  }
}
```

### 8.3 分类统计

**接口**: `GET /api/admin/dashboard/category-stats`

**描述**: 获取分类统计数据

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "categoryId": 1,
      "categoryName": "保洁",
      "demandCount": 1256,
      "orderCount": 1089,
      "totalAmount": 456780.00,
      "avgPrice": 85.00,
      "completionRate": 0.87
    },
    {
      "categoryId": 2,
      "categoryName": "烹饪",
      "demandCount": 567,
      "orderCount": 489,
      "totalAmount": 234560.00,
      "avgPrice": 100.00,
      "completionRate": 0.86
    }
  ]
}
```

### 8.4 地区统计

**接口**: `GET /api/admin/dashboard/region-stats`

**描述**: 获取地区分布统计

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "city": "北京市",
      "userCount": 5678,
      "demandCount": 12345,
      "orderCount": 10567,
      "totalAmount": 856790.00
    },
    {
      "city": "上海市",
      "userCount": 4567,
      "demandCount": 10234,
      "orderCount": 8765,
      "totalAmount": 723450.00
    }
  ]
}
```

### 8.5 用户排行

**接口**: `GET /api/admin/dashboard/user-rank`

**描述**: 获取用户排行榜

**请求参数**:
```
Query Parameters:
- type: publisher (publisher-发布者 taker-服务者)
- metric: orders (orders-订单数 amount-金额 score-评分)
- limit: 10
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "type": "taker",
    "metric": "orders",
    "list": [
      {
        "userId": 3001,
        "nickname": "王师傅",
        "avatarUrl": "https://xxx.com/avatar.png",
        "value": 156,
        "totalAmount": 25680.00,
        "score": 4.9
      }
    ]
  }
}
```

## 9. 系统配置接口

### 9.1 获取配置列表

**接口**: `GET /api/admin/configs`

**描述**: 获取系统配置列表

**请求参数**:
```
Query Parameters:
- group: wechat (wechat/oss/system)
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "configKey": "wechat_appid",
      "configValue": "wx1234567890abcdef",
      "configType": 1,
      "group": "wechat",
      "description": "微信小程序 AppID"
    }
  ]
}
```

### 9.2 更新配置

**接口**: `PUT /api/admin/configs/:id`

**描述**: 更新系统配置

**路径参数**:
```
id: 1
```

**请求参数**:
```json
{
  "configValue": "wx_new_appid"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

## 10. 管理员管理接口

### 10.1 获取管理员列表

**接口**: `GET /api/admin/admins`

**描述**: 获取管理员列表

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 20
- keyword: 关键词
- roleId: 1
- status: 1
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "avatar": "https://xxx.com/admin-avatar.png",
        "role": {
          "id": 1,
          "name": "超级管理员"
        },
        "status": 1,
        "lastLoginAt": "2026-03-26 10:00:00",
        "createdAt": "2026-01-01 00:00:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 5,
      "totalPages": 1
    }
  }
}
```

### 10.2 创建管理员

**接口**: `POST /api/admin/admins`

**描述**: 创建新管理员

**请求参数**:
```json
{
  "username": "newadmin",
  "password": "encrypted_password",
  "realName": "新管理员",
  "roleId": 2
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 2,
    "username": "newadmin"
  }
}
```

### 10.3 更新管理员

**接口**: `PUT /api/admin/admins/:id`

**描述**: 更新管理员信息

**路径参数**:
```
id: 2
```

**请求参数**:
```json
{
  "realName": "更新后的名字",
  "roleId": 3
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 10.4 禁用/启用管理员

**接口**: `PUT /api/admin/admins/:id/status`

**描述**: 禁用或启用管理员

**路径参数**:
```
id: 2
```

**请求参数**:
```json
{
  "status": 0
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 10.5 删除管理员

**接口**: `DELETE /api/admin/admins/:id`

**描述**: 删除管理员账号

**路径参数**:
```
id: 2
```

**响应数据**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

## 11. 角色管理接口

### 11.1 获取角色列表

**接口**: `GET /api/admin/roles`

**描述**: 获取所有角色

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "超级管理员",
      "code": "super_admin",
      "description": "拥有所有权限",
      "permissions": ["*"],
      "status": 1,
      "adminCount": 2
    }
  ]
}
```

### 11.2 创建角色

**接口**: `POST /api/admin/roles`

**描述**: 创建新角色

**请求参数**:
```json
{
  "name": "运营管理员",
  "code": "operation_admin",
  "description": "负责内容和用户管理",
  "permissions": ["user:*", "demand:*", "review:*"]
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 3,
    "name": "运营管理员"
  }
}
```

### 11.3 更新角色

**接口**: `PUT /api/admin/roles/:id`

**描述**: 更新角色信息和权限

**路径参数**:
```
id: 3
```

**请求参数**:
```json
{
  "name": "高级运营管理员",
  "permissions": ["user:*", "demand:*", "review:*", "order:view"]
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 11.4 删除角色

**接口**: `DELETE /api/admin/roles/:id`

**描述**: 删除角色 (未被使用时可删除)

**路径参数**:
```
id: 3
```

**响应数据**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

## 12. 日志管理接口

### 12.1 获取操作日志

**接口**: `GET /api/admin/logs`

**描述**: 获取后台操作日志

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 20
- adminId: 1
- action: user_delete
- module: user
- startDate: 2026-01-01
- endDate: 2026-03-26
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 10001,
        "admin": {
          "id": 1,
          "username": "admin",
          "realName": "系统管理员"
        },
        "action": "user_delete",
        "module": "user",
        "method": "DELETE",
        "url": "/api/admin/users/2001",
        "ip": "192.168.1.100",
        "requestData": {},
        "responseCode": 200,
        "duration": 156,
        "createdAt": "2026-03-26 10:30:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 5678,
      "totalPages": 284
    }
  }
}
```

---

**版本**: v1.0  
**更新日期**: 2026-03-26  
**作者**: AI 后端工程师
