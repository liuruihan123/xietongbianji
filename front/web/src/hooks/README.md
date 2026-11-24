# Hooks 使用文档

本目录包含了项目中所有的自定义 Vue 3 Composition API hooks，提供了统一的业务逻辑接口。

## 目录结构

```
hooks/
├── index.ts              # 统一导出所有hooks
├── useAuth.ts           # 用户认证相关hooks
├── useLanguage.ts       # 国际化语言相关hooks
├── useTheme.ts          # 主题管理相关hooks
├── useApi.ts            # API请求相关hooks
├── useAuthValidator.ts  # 表单验证相关hooks
└── README.md           # 使用文档
```

## 1. useAuth - 用户认证

用于管理用户认证状态、登录、注册等功能。

### 基本用法

```vue
<script setup>
import { useAuth } from '@/hooks'

const {
  isAuthenticated,
  userInfo,
  login,
  logout,
  register
} = useAuth()

// 登录
const handleLogin = async () => {
  try {
    await login({
      userName: 'user@example.com',
      password: 'password123'
    })
    console.log('登录成功')
  } catch (error) {
    console.error('登录失败:', error)
  }
}

// 登出
const handleLogout = async () => {
  await logout()
  console.log('已退出登录')
}
</script>
```

### API

- `isAuthenticated`: 是否已认证
- `token`: 用户token
- `userInfo`: 用户信息
- `session`: 会话信息
- `login(form)`: 密码登录
- `phoneLogin(form)`: 手机验证码登录
- `emailLogin(form)`: 邮箱验证码登录
- `googleLogin(form)`: 谷歌登录
- `register(form)`: 注册
- `resetPassword(form)`: 重置密码
- `logout()`: 登出
- `fetchUserInfo()`: 获取用户信息
- `validateUserStatus()`: 验证用户状态

## 2. useLanguage - 国际化语言

用于管理多语言切换、URL语言前缀等功能。

### 基本用法

```vue
<script setup>
import { useLanguage } from '@/hooks'

const {
  currentLocale,
  currentLanguage,
  switchLanguage,
  getLanguageOptions
} = useLanguage()

// 切换语言
const handleLanguageChange = (languageKey) => {
  switchLanguage(languageKey)
}

// 获取语言选项
const languageOptions = getLanguageOptions()
</script>

<template>
  <div>
    <p>当前语言: {{ currentLanguage.nativeName }}</p>
    <select @change="handleLanguageChange($event.target.value)">
      <option 
        v-for="option in languageOptions" 
        :key="option.key"
        :value="option.key"
      >
        {{ option.flag }} {{ option.label }}
      </option>
    </select>
  </div>
</template>
```

### API

- `currentLocale`: 当前语言代码
- `currentLanguage`: 当前语言信息对象
- `currentLanguageLabel`: 当前语言显示名称
- `switchLanguage(key)`: 切换语言
- `switchLanguageWithUrl(key, router)`: 切换语言并更新URL
- `getLanguageOptions()`: 获取语言选项
- `isLanguage(key)`: 检查是否为指定语言
- `getLanguageDirection()`: 获取语言方向
- `getCurrentLanguagePrefix()`: 获取当前语言URL前缀
- `getLocalizedPath(path)`: 获取本地化路径

## 3. useTheme - 主题管理

用于管理主题切换、暗黑模式、主色调等功能。

### 基本用法

```vue
<script setup>
import { useTheme } from '@/hooks'

const {
  currentTheme,
  isDark,
  toggleDarkMode,
  setPrimaryColor,
  getPresetColors
} = useTheme()

// 切换暗黑模式
const handleToggleDark = () => {
  toggleDarkMode()
}

// 设置主色调
const handleColorChange = (color) => {
  setPrimaryColor(color)
}

const presetColors = getPresetColors()
</script>

<template>
  <div>
    <p>当前主题: {{ currentTheme }}</p>
    <button @click="handleToggleDark">
      {{ isDark ? '切换到亮色' : '切换到暗色' }}
    </button>
    
    <div>
      <span>主色调:</span>
      <button 
        v-for="color in presetColors"
        :key="color"
        :style="{ backgroundColor: color }"
        @click="handleColorChange(color)"
      />
    </div>
  </div>
</template>
```

### API

- `themeConfig`: 主题配置对象
- `currentTheme`: 当前主题模式
- `isDark`: 是否为暗黑模式
- `systemTheme`: 系统主题偏好
- `setThemeMode(mode)`: 设置主题模式
- `toggleDarkMode()`: 切换暗黑模式
- `setPrimaryColor(color)`: 设置主色调
- `setBorderRadius(radius)`: 设置圆角大小
- `setFontSize(size)`: 设置字体大小
- `resetTheme()`: 重置主题
- `getThemeOptions()`: 获取主题选项
- `getPresetColors()`: 获取预设颜色

## 4. useApi - API请求

用于统一管理API请求、错误处理、加载状态等。

### 基本用法

```vue
<script setup>
import { useApi, api } from '@/hooks'

// 使用useApi hook
const {
  data: userList,
  loading,
  error,
  execute: fetchUsers
} = useApi(
  () => api.get('/users'),
  {
    immediate: true,
    onSuccess: (data) => console.log('获取用户成功:', data),
    onError: (error) => console.error('获取用户失败:', error)
  }
)

// 直接使用api方法
const createUser = async (userData) => {
  try {
    const response = await api.post('/users', userData)
    console.log('创建用户成功:', response.data)
  } catch (error) {
    console.error('创建用户失败:', error)
  }
}
</script>

<template>
  <div>
    <div v-if="loading">加载中...</div>
    <div v-else-if="error">错误: {{ error.message }}</div>
    <div v-else>
      <div v-for="user in userList" :key="user.id">
        {{ user.name }}
      </div>
    </div>
    <button @click="fetchUsers">刷新</button>
  </div>
</template>
```

### API

- `useApi(apiFunction, options)`: 通用API请求hook
- `useFileUpload()`: 文件上传hook
- `usePagination(apiFunction, params)`: 分页数据hook
- `api.get/post/put/delete/patch`: 简化的API请求方法

## 5. useAuthValidator - 表单验证

用于表单验证、密码强度检查等功能。

### 基本用法

```vue
<script setup>
import { reactive } from 'vue'
import { useAuthValidator } from '@/hooks'

const {
  validateEmail,
  validatePassword,
  checkPasswordStrength,
  createFormValidator,
  getLoginRules
} = useAuthValidator()

const form = reactive({
  email: '',
  password: ''
})

// 创建表单验证器
const { errors, isValid, validate } = createFormValidator(getLoginRules())

// 提交表单
const handleSubmit = () => {
  if (validate(form)) {
    console.log('表单验证通过')
    // 提交表单
  } else {
    console.log('表单验证失败:', errors.value)
  }
}

// 检查密码强度
const passwordStrength = computed(() => {
  return checkPasswordStrength(form.password)
})
</script>

<template>
  <form @submit.prevent="handleSubmit">
    <div>
      <input 
        v-model="form.email" 
        type="email" 
        placeholder="邮箱"
        :class="{ error: errors.email }"
      />
      <span v-if="errors.email" class="error-message">
        {{ errors.email }}
      </span>
    </div>
    
    <div>
      <input 
        v-model="form.password" 
        type="password" 
        placeholder="密码"
        :class="{ error: errors.password }"
      />
      <div class="password-strength">
        <div 
          class="strength-bar"
          :style="{ 
            width: `${passwordStrength.score * 25}%`,
            backgroundColor: passwordStrength.color
          }"
        />
        <span>{{ passwordStrength.level }}</span>
      </div>
      <span v-if="errors.password" class="error-message">
        {{ errors.password }}
      </span>
    </div>
    
    <button type="submit" :disabled="!isValid">
      登录
    </button>
  </form>
</template>
```

### API

- `validateField(value, rules)`: 验证单个字段
- `validateEmail(email)`: 邮箱验证
- `validatePhone(phone)`: 手机号验证
- `validateUsername(username)`: 用户名验证
- `validatePassword(password)`: 密码验证
- `validateConfirmPassword(password, confirm)`: 确认密码验证
- `validateVerificationCode(code, length)`: 验证码验证
- `checkPasswordStrength(password)`: 密码强度检查
- `createFormValidator(fields)`: 创建表单验证器
- `getLoginRules()`: 获取登录表单验证规则
- `getRegisterRules()`: 获取注册表单验证规则

## 最佳实践

1. **统一导入**: 从 `@/hooks` 统一导入所需的hooks
2. **错误处理**: 始终处理异步操作的错误情况
3. **加载状态**: 使用loading状态提供用户反馈
4. **类型安全**: 在TypeScript项目中充分利用类型定义
5. **性能优化**: 合理使用computed和watch避免不必要的计算
6. **清理资源**: 在组件卸载时清理定时器、事件监听器等资源

## 扩展开发

如需添加新的hooks，请遵循以下规范：

1. 文件命名使用 `use` 前缀，如 `useNewFeature.ts`
2. 导出的函数名与文件名保持一致
3. 提供完整的TypeScript类型定义
4. 添加详细的JSDoc注释
5. 在 `index.ts` 中导出新的hook
6. 更新本文档添加使用说明
