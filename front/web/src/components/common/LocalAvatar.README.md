# LocalAvatar 组件

一个完全离线可用的头像组件，用于替代在线头像服务（如 ui-avatars.com）。

## 特性

- ✅ **完全离线** - 无需网络连接
- ✅ **自动降级** - 图片加载失败时自动显示首字母头像
- ✅ **智能识别** - 支持中英文名称
  - 中文名：显示前2个字（如：张三 → 张三）
  - 英文名：显示首字母（如：John Doe → JD）
- ✅ **一致配色** - 根据用户名生成一致的背景色
- ✅ **高度可定制** - 支持自定义大小、圆角、颜色等

## 使用方法

### 基础用法

```vue
<template>
  <LocalAvatar name="张三" />
</template>

<script setup>
import LocalAvatar from '@/components/common/LocalAvatar.vue'
</script>
```

### 带头像图片

```vue
<template>
  <LocalAvatar 
    :src="userAvatar" 
    name="张三" 
  />
</template>
```

### 自定义大小

```vue
<template>
  <!-- 小头像 -->
  <LocalAvatar name="张三" :size="24" />
  
  <!-- 中等头像 -->
  <LocalAvatar name="张三" :size="32" />
  
  <!-- 大头像 -->
  <LocalAvatar name="张三" :size="64" />
</template>
```

### 方形头像

```vue
<template>
  <LocalAvatar 
    name="张三" 
    :rounded="false" 
    :border-radius="8" 
  />
</template>
```

### 自定义颜色

```vue
<template>
  <LocalAvatar 
    name="张三" 
    custom-color="#1890ff" 
  />
</template>
```

## Props

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `src` | `string` | `''` | 头像图片地址 |
| `name` | `string` | `'User'` | 用户名称（必填） |
| `size` | `number` | `32` | 头像大小（像素） |
| `rounded` | `boolean` | `true` | 是否圆形 |
| `borderRadius` | `number` | `4` | 圆角大小（当 rounded 为 false 时生效） |
| `customColor` | `string` | `''` | 自定义背景色 |

## 配色方案

组件内置了 15 种柔和的配色方案：

- 红色 `#FF6B6B`
- 青色 `#4ECDC4`
- 蓝色 `#45B7D1`
- 绿色 `#96CEB4`
- 黄色 `#FFEAA7`
- 紫色 `#DDA0DD`
- 薄荷绿 `#98D8C8`
- 金黄色 `#F7DC6F`
- 淡紫色 `#BB8FCE`
- 天蓝色 `#85C1E9`
- 橙色 `#F8B88B`
- 浅橙色 `#FAD7A0`
- 浅蓝色 `#AED6F1`
- 浅绿色 `#A9DFBF`
- 粉红色 `#F5B7B1`

颜色会根据用户名自动生成，同一个用户名总是显示相同的颜色。

## 实际应用示例

### 在列表中使用

```vue
<template>
  <div v-for="user in users" :key="user.id" class="user-item">
    <LocalAvatar 
      :src="user.avatar" 
      :name="user.name" 
      :size="40" 
    />
    <span>{{ user.name }}</span>
  </div>
</template>
```

### 在导航栏中使用

```vue
<template>
  <div class="user-menu">
    <LocalAvatar 
      :src="currentUser.avatar" 
      :name="currentUser.name" 
      :size="32" 
      class="cursor-pointer"
    />
  </div>
</template>
```

## 注意事项

1. **必须提供 name 属性** - 即使有头像图片，也需要提供 name 作为降级显示
2. **图片加载失败自动降级** - 当图片加载失败时，会自动显示首字母头像
3. **离线环境友好** - 完全不依赖外部服务，适合内网或离线环境

## 迁移指南

### 从 ui-avatars.com 迁移

**之前：**
```vue
<img 
  :src="'https://ui-avatars.com/api/?name=' + userName" 
  class="w-8 h-8 rounded-full"
/>
```

**之后：**
```vue
<LocalAvatar 
  :name="userName" 
  :size="32" 
/>
```

### 从 Ant Design Avatar 迁移

如果你想要更多自定义选项，可以从 Ant Design 的 Avatar 迁移：

**之前：**
```vue
<a-avatar :size="32">
  {{ userName?.charAt(0) }}
</a-avatar>
```

**之后：**
```vue
<LocalAvatar 
  :name="userName" 
  :size="32" 
/>
```

