# 环境变量配置指南

## 概述

本项目使用多环境配置管理，支持开发、测试、预发布和生产环境的不同配置。

## 环境文件说明

| 文件 | 环境 | 说明 |
|------|------|------|
| `.env` | 所有环境 | 基础配置，会被其他环境文件覆盖 |
| `.env.development` | 开发环境 | 开发环境特定配置 |
| `.env.testing` | 测试环境 | 测试环境配置 |
| `.env.staging` | 预发布环境 | 预发布环境配置 |
| `.env.production` | 生产环境 | 生产环境配置 |

## 构建脚本

```bash
# 开发环境
npm run dev                 # 启动开发服务器
npm run dev:staging         # 使用 staging 配置启动开发服务器

# 构建
npm run build:dev           # 开发环境构建
npm run build:test          # 测试环境构建
npm run build:staging       # 预发布环境构建
npm run build:prod          # 生产环境构建

# 预览
npm run preview             # 预览构建结果
npm run preview:staging     # 预览 staging 构建结果
npm run preview:prod        # 预览生产构建结果
```

## 使用方式

### 1. 直接使用环境变量

```typescript
// 直接访问（不推荐）
const apiUrl = import.meta.env.VITE_APP_API_BASE_URL
```

### 2. 使用类型安全的配置（推荐）

```typescript
import { envConfig, envChecks } from '@/config/env'

// 获取配置
const apiUrl = envConfig.APP_API_BASE_URL
const appTitle = envConfig.APP_TITLE

// 环境检查
if (envChecks.isDevelopment()) {
  console.log('开发环境')
}

if (envChecks.isProduction()) {
  // 生产环境特定逻辑
}
```

### 3. 在组件中使用

```vue
<template>
  <div>
    <h1>{{ appTitle }}</h1>
    <p>当前环境: {{ currentEnv }}</p>
  </div>
</template>

<script setup lang="ts">
import { envConfig } from '@/config/env'

const appTitle = envConfig.APP_TITLE
const currentEnv = envConfig.APP_ENV
</script>
```

## 环境变量命名规范

- 所有客户端可访问的环境变量必须以 `VITE_` 开头
- 使用大写字母和下划线命名
- 按功能分组命名：
  - `VITE_APP_*` - 应用基础信息
  - `VITE_API_*` - API 相关配置
  - `VITE_BUILD_*` - 构建相关配置
  - `VITE_DEV_*` - 开发环境配置

## 最佳实践

1. **敏感信息**: 不要在环境变量中存储敏感信息，客户端环境变量会暴露在构建产物中
2. **默认值**: 为所有环境变量提供合理的默认值
3. **类型安全**: 使用 `src/config/env.ts` 进行类型转换和验证
4. **文档**: 及时更新环境变量文档和类型定义

## 部署配置

### 开发环境
- API 地址: `http://localhost:8500`
- 启用所有调试功能

### 测试环境
- API 地址: `http://localhost:8500`
- 保留调试信息，关闭 PWA

### 预发布环境
- API 地址: `https://staging-api.your-domain.com`
- 接近生产环境配置，保留部分调试信息

### 生产环境
- API 地址: `https://your-production-domain.com`
- 启用所有优化，移除调试信息
