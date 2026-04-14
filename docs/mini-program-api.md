# 家政小程序 - API 接口设计文档 (小程序端)

## 1. 接口设计规范

### 1.1 基础信息
- **协议**: HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: JWT Token (Bearer Auth)

### 1.2 请求头 (Headers)
```
Content-Type: application/json
Authorization: Bearer <token>  (需要认证的接口)
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

### 1.4 分页响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 100,
      "totalPages": 10
    }
  },
  "timestamp": 1711468800000
}
```

### 1.5 错误码定义

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录/Token 失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 10001 | 微信登录失败 |
| 10002 | Token 无效 |
| 10003 | Token 已过期 |
| 20001 | 需求不存在 |
| 20002 | 需求已被接单 |
| 20003 | 不能接自己的单 |
| 30001 | 订单不存在 |
| 30002 | 订单状态异常 |

## 2. 认证接口

### 2.1 微信登录

**接口**: `POST /api/mini/auth/login`

**描述**: 微信小程序授权登录

**请求参数**:
```json
{
  "code": "wx_code_string",
  "encryptedData": "encrypted_data_string",
  "iv": "iv_string",
  "userInfo": {
    "nickName": "张三",
    "avatarUrl": "https://xxx.com/avatar.png",
    "gender": 1
  }
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 604800,
    "user": {
      "id": 1001,
      "openid": "oXXXX_openid",
      "nickname": "张三",
      "avatarUrl": "https://xxx.com/avatar.png",
      "gender": 1,
      "phone": "",
      "certificationStatus": 0,
      "score": 5.00,
      "totalOrders": 0,
      "followerCount": 0,
      "followingCount": 0
    }
  }
}
```

**备注**: 
- code 通过 `wx.login()` 获取
- 首次登录自动创建用户
- token 有效期 7 天

### 2.2 刷新 Token

**接口**: `POST /api/mini/auth/refresh`

**描述**: 使用 refresh token 刷新 access token

**请求参数**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 604800
  }
}
```

### 2.3 绑定手机号

**接口**: `POST /api/mini/user/bind-phone`

**描述**: 绑定用户手机号

**请求参数**:
```json
{
  "code": "wx_code_string",
  "encryptedData": "encrypted_data_string",
  "iv": "iv_string"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "phone": "138****1234"
  }
}
```

## 3. 需求接口

### 3.1 获取需求列表

**接口**: `GET /api/mini/demands`

**描述**: 获取需求列表 (支持筛选、分页)

**请求参数**:
```
Query Parameters:
- page: 1 (页码，默认 1)
- pageSize: 10 (每页数量，默认 10)
- categoryId: 1 (分类 ID，可选)
- city: 北京市 (城市，可选)
- district: 朝阳区 (区县，可选)
- status: 1 (状态，默认 1-招募中)
- sortBy: latest (排序 latest/hot/price_asc/price_desc)
- keyword: 关键词 (可选)
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
        "description": "120㎡三居室，需要打扫卫生...",
        "category": {
          "id": 1,
          "name": "保洁",
          "icon": "🧹"
        },
        "expectedPrice": 80.00,
        "priceUnit": "小时",
        "serviceTimeDesc": "明天上午",
        "location": {
          "city": "北京市",
          "district": "朝阳区",
          "address": "建国路 88 号",
          "distance": 0.8
        },
        "publisher": {
          "id": 2001,
          "nickname": "李**",
          "avatarUrl": "https://xxx.com/avatar.png",
          "score": 4.9
        },
        "images": ["https://xxx.com/img1.jpg"],
        "viewCount": 156,
        "footprintCount": 23,
        "status": 1,
        "statusText": "招募中",
        "createdAt": "2026-03-25 10:30:00",
        "createdAtText": "5 分钟前"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 100,
      "totalPages": 10
    }
  }
}
```

### 3.2 获取需求详情

**接口**: `GET /api/mini/demands/:id`

**描述**: 获取需求详细信息

**路径参数**:
```
id: 1001 (需求 ID)
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1001,
    "title": "家庭日常保洁，约 2-3 小时",
    "description": "120㎡三居室，需要打扫卫生间、厨房油烟机、地板吸尘拖地。家中有宠物，请无过敏人士前来，自带工具优先。",
    "category": {
      "id": 1,
      "name": "保洁",
      "icon": "🧹"
    },
    "serviceType": 1,
    "serviceTypeText": "小时工",
    "expectedPrice": 80.00,
    "priceUnit": "小时",
    "minDuration": 2.0,
    "maxDuration": 3.0,
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
    "images": [
      "https://xxx.com/img1.jpg",
      "https://xxx.com/img2.jpg"
    ],
    "publisher": {
      "id": 2001,
      "nickname": "李美华",
      "avatarUrl": "https://xxx.com/avatar.png",
      "gender": 2,
      "score": 4.9,
      "totalOrders": 12,
      "followerCount": 34,
      "isFollowed": false
    },
    "viewCount": 156,
    "footprintCount": 23,
    "status": 1,
    "statusText": "招募中",
    "takenBy": null,
    "takenAt": null,
    "reviews": [
      {
        "id": 5001,
        "reviewer": {
          "nickname": "王师傅",
          "avatarUrl": "https://xxx.com/avatar2.png"
        },
        "content": "雇主很好说话，房间很整洁",
        "rating": 5,
        "createdAt": "2026-03-24 15:30:00"
      }
    ],
    "createdAt": "2026-03-25 10:30:00",
    "updatedAt": "2026-03-25 10:30:00"
  }
}
```

### 3.3 发布需求

**接口**: `POST /api/mini/demands`

**描述**: 发布新的家政需求

**请求参数**:
```json
{
  "categoryId": 1,
  "title": "家庭日常保洁，约 2-3 小时",
  "description": "120㎡三居室，需要打扫卫生间、厨房油烟机、地板吸尘拖地。",
  "serviceType": 1,
  "expectedPrice": 80.00,
  "priceUnit": "小时",
  "minDuration": 2.0,
  "maxDuration": 3.0,
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "address": "建国路 88 号",
  "latitude": 39.9042,
  "longitude": 116.4074,
  "serviceTime": "2026-03-27 09:00:00",
  "serviceTimeDesc": "明天 09:00-12:00",
  "contactName": "李女士",
  "contactPhone": "138****1234",
  "imageUrls": ["https://xxx.com/img1.jpg", "https://xxx.com/img2.jpg"]
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 1001,
    "title": "家庭日常保洁，约 2-3 小时",
    "status": 1,
    "statusText": "招募中",
    "createdAt": "2026-03-25 10:30:00"
  }
}
```

### 3.4 更新需求

**接口**: `PUT /api/mini/demands/:id`

**描述**: 更新需求信息 (仅发布者)

**路径参数**:
```
id: 1001
```

**请求参数**: (同发布需求，所有字段可选)
```json
{
  "title": " updated title",
  "description": "updated description"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1001,
    "updatedAt": "2026-03-25 11:30:00"
  }
}
```

### 3.5 删除需求

**接口**: `DELETE /api/mini/demands/:id`

**描述**: 删除需求 (软删除，仅发布者)

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

### 3.6 添加足迹

**接口**: `POST /api/mini/demands/:id/footprint`

**描述**: 记录用户浏览需求 (自动调用)

**路径参数**:
```
id: 1001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 4. 订单接口

### 4.1 接单

**接口**: `POST /api/mini/orders/:demandId/take`

**描述**: 接取需求

**路径参数**:
```
demandId: 1001
```

**请求参数**:
```json
{
  "remark": "我可以接单，请及时联系"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "接单成功",
  "data": {
    "orderId": 3001,
    "orderNo": "ORD202603250001",
    "demandId": 1001,
    "status": 1,
    "statusText": "待服务"
  }
}
```

### 4.2 获取我发布的订单

**接口**: `GET /api/mini/orders/published`

**描述**: 查看我发布的需求订单

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 10
- status: 1 (可选，筛选状态)
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
        "demand": {
          "id": 1001,
          "title": "家庭日常保洁",
          "category": {
            "name": "保洁",
            "icon": "🧹"
          }
        },
        "taker": {
          "id": 3001,
          "nickname": "王师傅",
          "avatarUrl": "https://xxx.com/avatar.png",
          "score": 4.8
        },
        "actualPrice": 80.00,
        "priceUnit": "小时",
        "serviceTime": "2026-03-27 09:00:00",
        "status": 2,
        "statusText": "进行中",
        "createdAt": "2026-03-25 10:30:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 5,
      "totalPages": 1
    }
  }
}
```

### 4.3 获取我接的订单

**接口**: `GET /api/mini/orders/taken`

**描述**: 查看我接的订单

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 10
- status: 1 (可选)
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
        "demand": {
          "id": 1001,
          "title": "家庭日常保洁",
          "category": {
            "name": "保洁",
            "icon": "🧹"
          }
        },
        "publisher": {
          "id": 2001,
          "nickname": "李女士",
          "avatarUrl": "https://xxx.com/avatar.png"
        },
        "actualPrice": 80.00,
        "priceUnit": "小时",
        "serviceTime": "2026-03-27 09:00:00",
        "location": {
          "district": "朝阳区",
          "address": "建国路 88 号"
        },
        "status": 2,
        "statusText": "进行中",
        "createdAt": "2026-03-25 10:30:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 8,
      "totalPages": 1
    }
  }
}
```

### 4.4 获取订单详情

**接口**: `GET /api/mini/orders/:id`

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
      "avatarUrl": "https://xxx.com/avatar.png",
      "phone": "138****1234"
    },
    "taker": {
      "id": 3001,
      "nickname": "王师傅",
      "avatarUrl": "https://xxx.com/avatar2.png",
      "phone": "139****5678",
      "score": 4.8
    },
    "title": "家庭日常保洁",
    "description": "120㎡三居室...",
    "category": {
      "name": "保洁",
      "icon": "🧹"
    },
    "serviceTime": "2026-03-27 09:00:00",
    "location": {
      "address": "朝阳区建国路 88 号",
      "latitude": 39.9042,
      "longitude": 116.4074
    },
    "actualPrice": 80.00,
    "priceUnit": "小时",
    "actualDuration": null,
    "totalAmount": null,
    "status": 2,
    "statusText": "进行中",
    "start_time": "2026-03-27 09:00:00",
    "end_time": null,
    "remark": "我可以接单，请及时联系",
    "createdAt": "2026-03-25 10:30:00"
  }
}
```

### 4.5 更新订单状态

**接口**: `PUT /api/mini/orders/:id/status`

**描述**: 更新订单状态 (开始服务/确认完成等)

**路径参数**:
```
id: 3001
```

**请求参数**:
```json
{
  "status": 3,
  "remark": "服务已完成，请确认"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 3001,
    "status": 3,
    "statusText": "待确认",
    "updatedAt": "2026-03-27 12:00:00"
  }
}
```

### 4.6 取消订单

**接口**: `POST /api/mini/orders/:id/cancel`

**描述**: 取消订单

**路径参数**:
```
id: 3001
```

**请求参数**:
```json
{
  "reason": "临时有事，无法按时上门",
  "cancelBy": "taker"
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

## 5. 评价接口

### 5.1 发布评价

**接口**: `POST /api/review`

**描述**: 对订单进行评价

**请求参数**:
```json
{
  "orderId": 3001,
  "demandId": 1001,
  "rating": 5,
  "content": "非常满意，服务很专业",
  "images": ["https://xxx.com/review1.jpg"],
  "isAnonymous": false
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "评价成功",
  "data": {
    "id": 5001,
    "rating": 5,
    "content": "非常满意，服务很专业",
    "createdAt": "2026-03-27 14:00:00"
  }
}
```

### 5.2 获取用户评价列表

**接口**: `GET /api/mini/reviews/user/:userId`

**描述**: 查看某个用户的所有评价

**路径参数**:
```
userId: 2001
```

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 10
- type: all (all-全部 good-好评 normal-中评 bad-差评)
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
          "nickname": "王师傅",
          "avatarUrl": "https://xxx.com/avatar.png"
        },
        "rating": 5,
        "content": "非常满意，服务很专业",
        "images": ["https://xxx.com/review1.jpg"],
        "replyContent": "感谢您的认可！",
        "replyAt": "2026-03-27 15:00:00",
        "createdAt": "2026-03-27 14:00:00"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 12,
      "totalPages": 2
    }
  }
}
```

## 6. 用户接口

### 6.1 获取用户信息

**接口**: `GET /api/mini/user/profile`

**描述**: 获取当前登录用户信息

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2001,
    "openid": "oXXXX_openid",
    "nickname": "张小华",
    "avatarUrl": "https://xxx.com/avatar.png",
    "gender": 1,
    "phone": "138****1234",
    "realName": "张*",
    "certificationStatus": 2,
    "bio": "专业保洁服务，5 年经验",
    "score": 4.9,
    "totalOrders": 8,
    "publishedCount": 12,
    "takenCount": 8,
    "followerCount": 34,
    "followingCount": 56
  }
}
```

### 6.2 更新用户信息

**接口**: `PUT /api/mini/user/profile`

**描述**: 更新用户个人信息

**请求参数**:
```json
{
  "nickname": "张小华",
  "bio": "专业保洁服务，5 年经验",
  "gender": 1
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 2001,
    "nickname": "张小华",
    "bio": "专业保洁服务，5 年经验",
    "avatarUrl": "https://xxx.com/avatar.png"
  }
}
```

### 6.3 获取其他用户信息

**接口**: `GET /api/mini/user/:id`

**描述**: 查看其他用户公开信息

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
    "nickname": "王师傅",
    "avatarUrl": "https://xxx.com/avatar.png",
    "gender": 1,
    "bio": "专业家政服务 10 年",
    "score": 4.8,
    "totalOrders": 45,
    "followerCount": 128,
    "followingCount": 23,
    "isFollowed": false,
    "categories": ["保洁", "家电清洗"],
    "recentReviews": [
      {
        "rating": 5,
        "content": "服务很专业",
        "createdAt": "2026-03-25"
      }
    ]
  }
}
```

### 6.4 关注用户

**接口**: `POST /api/mini/user/:id/follow`

**描述**: 关注某个用户

**路径参数**:
```
id: 3001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "关注成功",
  "data": {
    "isFollowed": true,
    "followerCount": 129
  }
}
```

### 6.5 取消关注

**接口**: `DELETE /api/mini/user/:id/follow`

**描述**: 取消关注

**路径参数**:
```
id: 3001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "取消成功",
  "data": {
    "isFollowed": false,
    "followerCount": 128
  }
}
```

### 6.6 我的关注列表

**接口**: `GET /api/mini/user/following`

**描述**: 查看我关注的用户列表

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 10
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
        "nickname": "王师傅",
        "avatarUrl": "https://xxx.com/avatar.png",
        "bio": "专业家政服务 10 年",
        "score": 4.8,
        "totalOrders": 45,
        "isFollowed": true
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 56,
      "totalPages": 6
    }
  }
}
```

### 6.7 我的粉丝列表

**接口**: `GET /api/mini/user/follower`

**描述**: 查看我的粉丝列表

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 10
```

**响应数据**: (结构同关注列表)

## 7. 足迹接口

### 7.1 我的足迹列表

**接口**: `GET /api/mini/footprints`

**描述**: 查看我的浏览足迹

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 10
- dateGroup: today (today-今天 yesterday-昨天 earlier-更早)
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
          "name": "保洁",
          "icon": "🧹"
        },
        "expectedPrice": 80.00,
        "priceUnit": "小时",
        "location": {
          "district": "朝阳区"
        },
        "publisher": {
          "nickname": "李**"
        },
        "status": 1,
        "statusText": "招募中",
        "browsedAt": "2026-03-26 11:03:00",
        "browsedAtText": "11 分钟前"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 56,
      "totalPages": 6
    }
  }
}
```

### 7.2 清除足迹

**接口**: `DELETE /api/mini/footprints`

**描述**: 批量清除足迹

**请求参数**:
```json
{
  "ids": [1001, 1002, 1003]
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "清除成功",
  "data": null
}
```

## 8. 消息通知接口

### 8.1 消息列表

**接口**: `GET /api/mini/messages`

**描述**: 获取消息通知列表

**请求参数**:
```
Query Parameters:
- page: 1
- pageSize: 10
- type: all (all-全部 system-系统 order-订单 review-评价)
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 6001,
        "type": 2,
        "typeText": "接单通知",
        "title": "您的需求有人接单了",
        "content": "王师傅已接单您的需求：家庭日常保洁",
        "relatedType": "order",
        "relatedId": 3001,
        "isRead": false,
        "createdAt": "2026-03-26 10:30:00"
      }
    ],
    "unreadCount": 3,
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 25,
      "totalPages": 3
    }
  }
}
```

### 8.2 标记已读

**接口**: `PUT /api/mini/message/:id/read`

**描述**: 标记消息为已读

**路径参数**:
```
id: 6001
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 8.3 一键已读

**接口**: `PUT /api/mini/messages/read-all`

**描述**: 将所有消息标记为已读

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 9. 文件上传接口

### 9.1 上传图片

**接口**: `POST /api/mini/upload/image`

**描述**: 上传图片到 OSS

**请求参数**: (FormData)
```
file: image_file
type: demand/review/avatar
```

**响应数据**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://xxx.com/uploads/2026/03/26/abc123.jpg",
    "thumbnailUrl": "https://xxx.com/uploads/2026/03/26/abc123_thumb.jpg",
    "size": 1024000,
    "width": 1920,
    "height": 1080
  }
}
```

## 10. 分类接口

### 10.1 获取分类列表

**接口**: `GET /api/mini/categories`

**描述**: 获取所有服务分类

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
      "sortOrder": 100,
      "isHot": true,
      "demandCount": 256
    },
    {
      "id": 2,
      "name": "烹饪",
      "icon": "🍳",
      "sortOrder": 90,
      "isHot": true,
      "demandCount": 128
    }
  ]
}
```

## 11. 首页推荐接口

### 11.1 首页数据

**接口**: `GET /api/mini/home`

**描述**: 获取首页推荐数据

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "nearbyDemandCount": 3,
    "categories": [],
    "banner": {
      "title": "附近有 3 条新需求",
      "subtitle": "快来抢单，收入翻倍！",
      "imageUrl": "https://xxx.com/banner.jpg"
    },
    "recommendedDemands": [
      {
        "id": 1001,
        "title": "家庭日常保洁",
        "expectedPrice": 80.00,
        "distance": 0.8,
        "createdAtText": "5 分钟前"
      }
    ]
  }
}
```

---

**版本**: v1.0  
**更新日期**: 2026-03-26  
**作者**: AI 后端工程师
