# 布局组件使用说明

## 概述

本项目的布局系统由三个主要组件组成:
- **AppHeader.vue** - 顶部导航栏
- **AppFooter.vue** - 底部页脚
- **MainLayout.vue** - 主布局框架(包含 AppHeader 和 AppFooter)

## AppHeader 组件

### Props

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| showSearch | boolean | false | 是否显示搜索框 |
| showNotification | boolean | false | 是否显示通知按钮 |
| showSidebarToggle | boolean | false | 是否显示侧边栏切换按钮(移动端) |
| title | string | '协同编辑系统' | 自定义标题 |

### Events

| 事件名 | 参数 | 说明 |
|--------|------|------|
| search | keyword: string | 搜索事件 |
| notification-click | - | 通知按钮点击事件 |
| sidebar-toggle | - | 侧边栏切换事件 |

### 功能特性

- ✅ 响应式设计,支持移动端和桌面端
- ✅ 集成用户认证状态(登录/注册/用户信息)
- ✅ 用户下拉菜单(个人资料、工作空间、分享、转换、设置、退出)
- ✅ 可配置的搜索框和通知按钮
- ✅ 使用 Tailwind CSS 样式

## MainLayout 组件

### Props

继承 AppHeader 的所有 props:
- showSearch
- showNotification
- showSidebarToggle
- title

### Events

继承 AppHeader 的所有 events:
- search
- notification-click
- sidebar-toggle

### 使用示例

#### 基础用法(不显示搜索和通知)

```vue
<template>
  <MainLayout>
    <div class="container">
      <!-- 你的页面内容 -->
      <h1>欢迎</h1>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import MainLayout from '@/components/layout/MainLayout.vue'
</script>
```

#### 完整功能用法(显示搜索、通知、侧边栏切换)

```vue
<template>
  <MainLayout
    :show-search="true"
    :show-notification="true"
    :show-sidebar-toggle="true"
    @sidebar-toggle="sidebarOpen = !sidebarOpen"
    @search="handleSearch"
    @notification-click="handleNotificationClick"
  >
    <div class="container">
      <!-- 你的页面内容 -->
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import MainLayout from '@/components/layout/MainLayout.vue'

const sidebarOpen = ref(false)

function handleSearch(keyword: string) {
  console.log('搜索:', keyword)
}

function handleNotificationClick() {
  console.log('通知点击')
}
</script>
```

#### 自定义标题

```vue
<template>
  <MainLayout title="我的工作空间">
    <!-- 页面内容 -->
  </MainLayout>
</template>
```

## 页面应用指南

### 需要使用 MainLayout 的页面

- ✅ Home.vue - 首页(显示搜索、通知、侧边栏切换)
- ✅ Profile.vue - 个人资料
- ✅ Workspace - 工作空间相关页面
- ✅ Share - 分享相关页面
- ✅ Convert - 文档转换页面
- ✅ Settings - 设置页面
- ✅ Editor - 编辑器页面
- ✅ File - 文件管理页面
- ✅ Search - 搜索页面
- ✅ Trash - 回收站页面

### 不需要使用 MainLayout 的页面

- ❌ SignIn.vue - 登录页
- ❌ SignUp.vue - 注册页
- ❌ ForgotPassword.vue - 忘记密码页
- ❌ EmailLogin.vue - 邮箱登录页
- ❌ 404.vue - 404 错误页(可选)

## 注意事项

1. **固定定位**: AppHeader 使用 `fixed` 定位,高度为 `h-16` (64px),因此主内容区需要添加 `pt-16` 的上边距
2. **响应式**: 布局组件已经内置响应式设计,无需额外处理
3. **认证状态**: AppHeader 会自动根据用户登录状态显示不同的UI
4. **事件处理**: 如果页面需要响应搜索或通知事件,记得监听相应的事件

## 样式说明

布局组件使用 Tailwind CSS,主要样式类:
- 固定定位: `fixed top-0 left-0 right-0`
- 高度: `h-16` (64px)
- 背景: `bg-white`
- 边框: `border-b border-gray-200`
- 阴影: `shadow-sm`
- 层级: `z-50`

