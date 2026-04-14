# 管理后台前端开发计划

## 项目概述

家政小程序管理后台前端项目，基于 Vue 3 + Element Plus 构建，为运营管理人员提供完整的后台管理系统。

## 技术架构

### 核心技术栈
- **框架**: Vue 3.4 (Composition API)
- **UI 组件**: Element Plus 2.5
- **状态管理**: Pinia 2.1
- **路由**: Vue Router 4.2
- **HTTP**: Axios 1.6
- **构建工具**: Vite 5.0
- **可视化**: ECharts 5.4

### 项目特色
- 响应式布局，支持主流屏幕分辨率
- RESTful API 集成
- JWT Token 认证
- 统一错误处理
- 组件化开发

## 已完成功能 ✅

### 1. 项目基础架构 (100%)
- ✅ 项目初始化
- ✅ 依赖安装配置
- ✅ 路由配置
- ✅ API 请求封装
- ✅ 代理配置（开发环境）

### 2. 核心页面 (80%)
- ✅ **登录页面** (`/login`)
  - 用户名密码登录
  - 表单验证
  - Token 存储
  - 自动跳转
  
- ✅ **主布局** (`MainLayout.vue`)
  - 侧边栏导航
  - 顶部工具栏
  - 用户信息展示
  - 退出登录
  
- ✅ **首页 Dashboard** (`/dashboard`)
  - 数据统计卡片（用户、需求、订单、金额）
  - 快捷操作入口
  - 系统信息展示
  - 实时数据加载
  
- ✅ **用户管理列表** (`/users`)
  - 用户列表展示
  - 搜索筛选（关键词、认证状态、状态）
  - 分页功能
  - 禁用/启用用户
  - 查看详情（待实现对话框）

### 3. 占位页面 (20%)
以下页面已创建基础框架，待完善具体功能：

- 🚧 需求管理 (`/demands`)
- 🚧 订单管理 (`/orders`)
- 🚧 评价管理 (`/reviews`)
- 🚧 分类管理 (`/categories`)
- 🚧 数据统计 (`/statistics`)
- 🚧 系统配置 (`/system`)
- 🚧 操作日志 (`/logs`)

## 待开发功能 📋

### 第一阶段：完善核心业务模块（优先级：高）

#### 1. 用户管理详情功能
**文件**: `src/views/user/UserDetailDialog.vue`
- [ ] 用户基本信息展示
- [ ] 用户订单记录
- [ ] 用户需求记录
- [ ] 用户评价记录
- [ ] 足迹记录
- [ ] 关注/粉丝列表

#### 2. 需求管理模块
**文件**: `src/views/demand/DemandList.vue`
- [ ] 需求列表（完整表格）
- [ ] 搜索筛选（关键词、分类、城市、状态、日期）
- [ ] 需求详情对话框
- [ ] 下架需求功能
- [ ] 删除需求功能
- [ ] 导出 Excel

#### 3. 订单管理模块
**文件**: `src/views/order/OrderList.vue`
- [ ] 订单列表（完整表格）
- [ ] 搜索筛选（订单号、关键词、状态、支付状态）
- [ ] 订单详情对话框
- [ ] 订单操作日志
- [ ] 取消订单功能
- [ ] 退款功能
- [ ] 导出 Excel

#### 4. 评价管理模块
**文件**: `src/views/review/ReviewList.vue`
- [ ] 评价列表（完整表格）
- [ ] 搜索筛选（关键词、评分、类型）
- [ ] 评价详情对话框
- [ ] 官方回复评价
- [ ] 删除评价功能
- [ ] 点赞数统计

#### 5. 分类管理模块
**文件**: `src/views/category/CategoryList.vue`
- [ ] 分类树形列表
- [ ] 新增分类对话框
- [ ] 编辑分类对话框
- [ ] 排序调整
- [ ] 启用/禁用分类
- [ ] 删除分类

### 第二阶段：统计与配置（优先级：中）

#### 6. 数据统计模块
**文件**: `src/views/statistics/StatisticsView.vue`
- [ ] 趋势图表（ECharts）
  - 用户增长曲线
  - 订单数量趋势
  - 交易金额趋势
  - 需求完成趋势
- [ ] 分类统计饼图
- [ ] 地区分布地图
- [ ] 用户排行榜
- [ ] 数据导出功能

#### 7. 系统配置模块
**文件**: `src/views/system/SystemConfig.vue`
- [ ] 配置列表（表格）
- [ ] 按分组筛选
- [ ] 编辑配置对话框
- [ ] 配置项管理
- [ ] 缓存刷新功能

#### 8. 操作日志模块
**文件**: `src/views/log/OperationLogs.vue`
- [ ] 日志列表（表格）
- [ ] 搜索筛选（管理员、模块、动作、日期）
- [ ] 日志详情对话框
- [ ] 请求参数查看
- [ ] 响应结果查看
- [ ] 导出日志

### 第三阶段：增强功能（优先级：低）

#### 9. 公共组件开发
**目录**: `src/components/`
- [ ] `Pagination.vue` - 分页组件封装
- [ ] `SearchForm.vue` - 搜索表单封装
- [ ] `DataTable.vue` - 数据表格封装
- [ ] `ImagePreview.vue` - 图片预览组件
- [ ] `RichTextEditor.vue` - 富文本编辑器

#### 10. 权限控制优化
**目录**: `src/store/`
- [ ] 创建权限 Store (`permission.js`)
- [ ] 按钮级权限控制
- [ ] 菜单动态加载
- [ ] 角色管理界面

#### 11. 个人中心
**文件**: `src/views/profile/Profile.vue`
- [ ] 个人信息修改
- [ ] 头像上传
- [ ] 修改密码
- [ ] 登录日志

## 项目结构规范

```
管理后台前端/
├── public/                  # 静态资源
│   └── vite.svg
├── src/
│   ├── assets/             # 项目资源
│   │   └── styles/
│   │       └── variables.scss
│   ├── components/         # 公共组件
│   │   ├── Pagination.vue
│   │   └── SearchForm.vue
│   ├── layouts/            # 布局组件
│   │   └── MainLayout.vue
│   ├── router/             # 路由配置
│   │   └── index.js
│   ├── store/              # Pinia 状态管理
│   │   ├── index.js
│   │   └── permission.js
│   ├── utils/              # 工具函数
│   │   ├── request.js
│   │   └── format.js
│   ├── views/              # 页面组件
│   │   ├── Login.vue
│   │   ├── Dashboard.vue
│   │   ├── user/
│   │   │   ├── UserList.vue
│   │   │   └── UserDetailDialog.vue
│   │   ├── demand/
│   │   ├── order/
│   │   ├── review/
│   │   ├── category/
│   │   ├── statistics/
│   │   ├── system/
│   │   └── log/
│   ├── App.vue
│   └── main.js
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## API 接口对接

### 基础配置
- **Base URL**: `/api`
- **超时时间**: 10000ms
- **认证方式**: Bearer Token

### 已对接接口
- ✅ POST `/api/admin/auth/login` - 管理员登录
- ✅ GET `/api/admin/statistics/dashboard` - 首页统计
- 🚧 GET `/api/admin/users` - 用户列表（部分对接）

### 待对接接口
详见 `admin-api.md` 文档

## 开发规范

### 代码风格
- 使用 Composition API (`<script setup>`)
- 统一使用 JavaScript（非 TypeScript）
- 组件名使用 PascalCase
- 文件名使用 PascalCase.vue

### 注释规范
- 组件文件需要说明功能
- 复杂逻辑需要添加注释
- API 调用需要标注接口文档位置

### Git 提交规范
```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
chore: 构建/工具链相关
```

## 测试计划

### 单元测试（计划中）
- 工具函数测试
- 组件渲染测试
- API 请求测试

### 集成测试（计划中）
- 登录流程测试
- 列表页面测试
- 表单提交测试

### E2E 测试（可选）
- 完整业务流程测试
- 跨浏览器测试

## 部署方案

### 开发环境
- 端口：3001
- API 代理：http://localhost:8080

### 生产环境
- 执行 `npm run build`
- 输出目录：`dist/`
- 需要配置 Nginx 反向代理

### Docker 部署（可选）
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install --production
COPY . .
RUN npm run build
FROM nginx:alpine
COPY --from=0 /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

## 性能优化

### 已实现
- ✅ Vite 快速构建
- ✅ 按需加载 Element Plus
- ✅ 路由懒加载

### 计划中
- ⏳ 图片懒加载
- ⏳ 虚拟滚动（大数据列表）
- ⏳ 请求缓存
- ⏳ 防抖节流优化

## 安全考虑

### 已实现
- ✅ Token 认证
- ✅ XSS 防护（Vue 自动转义）
- ✅ CSRF Token（后端处理）

### 待加强
- ⏳ 敏感信息脱敏
- ⏳ 操作二次确认
- ⏳ 权限细粒度控制

## 里程碑

### M1 - 基础框架完成（当前进度）
- 时间：2026-03-27
- 完成度：30%
- 标志：项目可运行，核心页面框架搭建完成

### M2 - 核心功能完成
- 时间：预计 2026-04-03
- 目标：所有列表页面、详情对话框、基础 CRUD
- 完成度：70%

### M3 - 高级功能完成
- 时间：预计 2026-04-10
- 目标：统计图表、系统配置、日志管理
- 完成度：90%

### M4 - 测试与优化
- 时间：预计 2026-04-17
- 目标：Bug 修复、性能优化、文档完善
- 完成度：100%

## 风险管理

### 技术风险
- **Element Plus 版本兼容性**: 已验证 2.5 版本可用
- **浏览器兼容性**: 仅支持现代浏览器（Chrome/Firefox/Edge）

### 进度风险
- **后端接口延迟**: 保持沟通，优先开发已定义接口的功能
- **需求变更**: 预留 20% 缓冲时间

## 后续规划

### 短期（1-2 个月）
- 移动端适配（可选）
- 暗黑模式
- 多语言支持

### 中期（3-6 个月）
- 工单系统
- 消息通知
- 数据大屏

### 长期（6-12 个月）
- AI 智能客服
- 自动化运营工具
- 数据分析平台

---

**最后更新**: 2026-03-27  
**维护者**: 开发团队
