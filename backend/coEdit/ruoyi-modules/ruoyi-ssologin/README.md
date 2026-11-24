# Google SSO 登录模块

## 概述

本模块提供 Google OAuth2 登录功能，允许用户使用 Google 账号快速登录系统。

## 功能特性

- 验证 Google ID Token
- 自动创建新用户或登录现有用户
- 集成现有的用户管理和权限系统
- 支持邀请码功能

## 配置要求

在 `chat_config` 表中需要配置以下 Google OAuth 相关参数：

| 配置项 | 类别 | 键名 | 说明 |
|--------|------|------|------|
| Google 客户端ID | google | clientId | Google OAuth2 客户端ID |

### 配置示例

```sql
INSERT INTO chat_config (category, config_key, config_value, description) 
VALUES ('google', 'clientId', 'your-google-client-id.apps.googleusercontent.com', 'Google OAuth2 客户端ID');
```

## API 接口

### Google 登录

**接口地址：** `POST /auth/googleLogin`

**请求参数：**

```json
{
  "idToken": "Google ID Token",
  "inviteCode": "邀请码（可选）"
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "userId": 123,
      "username": "user@gmail.com",
      "nickName": "Google用户",
      "email": "user@gmail.com",
      "userType": "sys_user",
      "subMsgCount": 100,
      "inviteCode": "ABC123"
    }
  }
}
```

## 前端集成示例

### 1. 引入 Google Sign-In JavaScript 库

```html
<script src="https://accounts.google.com/gsi/client" async defer></script>
```

### 2. 初始化 Google Sign-In

```javascript
function initializeGoogleSignIn() {
  google.accounts.id.initialize({
    client_id: 'your-google-client-id.apps.googleusercontent.com',
    callback: handleGoogleSignIn
  });
  
  google.accounts.id.renderButton(
    document.getElementById('google-signin-button'),
    { theme: 'outline', size: 'large' }
  );
}

function handleGoogleSignIn(response) {
  // 发送 ID Token 到后端
  fetch('/auth/googleLogin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      idToken: response.credential,
      inviteCode: '' // 可选
    })
  })
  .then(response => response.json())
  .then(data => {
    if (data.code === 200) {
      // 登录成功，保存 token
      localStorage.setItem('token', data.data.token);
      // 跳转到主页面
      window.location.href = '/dashboard';
    } else {
      alert('登录失败：' + data.msg);
    }
  })
  .catch(error => {
    console.error('登录错误：', error);
  });
}
```

### 3. HTML 按钮

```html
<div id="google-signin-button"></div>
```

## 用户创建逻辑

1. **新用户：** 如果邮箱不存在，自动创建新用户账号
   - 用户名设置为邮箱地址
   - 昵称设置为 Google 提供的姓名或默认为"Google用户"
   - 分配默认的使用次数
   - 生成邀请码

2. **现有用户：** 如果邮箱已存在，直接登录现有账号

## 安全说明

- 使用 Google 官方 tokeninfo API 验证 ID Token
- 验证 token 的有效期和客户端ID
- 集成现有的登录日志记录功能

## 依赖模块

- `ruoyi-common-core`：核心工具类
- `ruoyi-common-web`：Web 相关功能
- `ruoyi-common-security`：安全相关功能
- `ruoyi-system`：用户管理服务

## 注意事项

1. 确保 Google OAuth2 客户端ID 正确配置
2. 前端域名需要在 Google Console 中添加到授权域名列表
3. 建议在生产环境中使用 HTTPS
