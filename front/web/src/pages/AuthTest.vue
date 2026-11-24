<template>
  <div class="auth-test">
    <h1>认证系统测试</h1>
    
    <div class="test-section">
      <h2>当前状态</h2>
      <p>已认证: {{ isAuthenticated }}</p>
      <p>Token: {{ token ? '存在' : '不存在' }}</p>
      <p>用户信息: {{ userInfo ? JSON.stringify(userInfo) : '无' }}</p>
      <p>加载中: {{ loading }}</p>
    </div>

    <div class="test-section">
      <h2>密码登录测试</h2>
      <form @submit.prevent="testLogin">
        <div>
          <label>邮箱:</label>
          <input v-model="loginForm.userName" type="email" placeholder="test@example.com" />
        </div>
        <div>
          <label>密码:</label>
          <input v-model="loginForm.password" type="password" placeholder="password" />
        </div>
        <button type="submit" :disabled="loading">登录</button>
      </form>
    </div>

    <div class="test-section">
      <h2>邮箱验证码登录测试</h2>
      <form @submit.prevent="testEmailLogin">
        <div>
          <label>邮箱:</label>
          <input v-model="emailLoginForm.email" type="email" placeholder="test@example.com" />
        </div>
        <div>
          <label>验证码:</label>
          <input v-model="emailLoginForm.emailCode" type="text" placeholder="123456" />
          <button type="button" @click="testSendCode" :disabled="loading">发送验证码</button>
        </div>
        <button type="submit" :disabled="loading">邮箱登录</button>
      </form>
    </div>

    <div class="test-section">
      <h2>注册测试</h2>
      <form @submit.prevent="testRegister">
        <div>
          <label>邮箱:</label>
          <input v-model="registerForm.userName" type="email" placeholder="test@example.com" />
        </div>
        <div>
          <label>密码:</label>
          <input v-model="registerForm.password" type="password" placeholder="password" />
        </div>
        <div>
          <label>确认密码:</label>
          <input v-model="registerForm.confirmPassword" type="password" placeholder="password" />
        </div>
        <div>
          <label>验证码:</label>
          <input v-model="registerForm.verificationCode" type="text" placeholder="123456" />
        </div>
        <button type="submit" :disabled="loading">注册</button>
      </form>
    </div>

    <div class="test-section">
      <h2>操作</h2>
      <button @click="testGetUserInfo" :disabled="loading">获取用户信息</button>
      <button @click="testGetSession" :disabled="loading">获取会话</button>
      <button @click="testLogout" :disabled="loading">退出登录</button>
    </div>

    <div class="test-section">
      <h2>日志</h2>
      <div class="log-container">
        <div v-for="(log, index) in logs" :key="index" class="log-item">
          <span class="log-time">{{ log.time }}</span>
          <span :class="`log-level log-level--${log.level}`">{{ log.level }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuth } from '@/hooks/useAuth'
import type { LoginForm, EmailLoginForm, RegisterForm } from '@/types/auth'

const { 
  isAuthenticated, 
  userInfo, 
  token, 
  loading, 
  login, 
  emailLogin, 
  register, 
  sendEmailCode, 
  fetchUserInfo, 
  getSession, 
  logout 
} = useAuth()

const logs = ref<Array<{time: string, level: string, message: string}>>([])

const loginForm = reactive<LoginForm>({
  userName: '',
  password: ''
})

const emailLoginForm = reactive<EmailLoginForm>({
  email: '',
  emailCode: ''
})

const registerForm = reactive<RegisterForm>({
  userName: '',
  password: '',
  confirmPassword: '',
  verificationCode: ''
})

const addLog = (level: string, message: string) => {
  logs.value.unshift({
    time: new Date().toLocaleTimeString(),
    level,
    message
  })
  if (logs.value.length > 50) {
    logs.value = logs.value.slice(0, 50)
  }
}

const testLogin = async () => {
  try {
    addLog('info', `尝试登录: ${loginForm.userName}`)
    const result = await login(loginForm)
    addLog('success', `登录成功: ${JSON.stringify(result)}`)
  } catch (error: any) {
    addLog('error', `登录失败: ${error.message}`)
  }
}

const testEmailLogin = async () => {
  try {
    addLog('info', `尝试邮箱登录: ${emailLoginForm.email}`)
    const result = await emailLogin(emailLoginForm)
    addLog('success', `邮箱登录成功: ${JSON.stringify(result)}`)
  } catch (error: any) {
    addLog('error', `邮箱登录失败: ${error.message}`)
  }
}

const testRegister = async () => {
  try {
    addLog('info', `尝试注册: ${registerForm.userName}`)
    const result = await register(registerForm)
    addLog('success', `注册成功: ${JSON.stringify(result)}`)
  } catch (error: any) {
    addLog('error', `注册失败: ${error.message}`)
  }
}

const testSendCode = async () => {
  try {
    addLog('info', `发送验证码到: ${emailLoginForm.email}`)
    const result = await sendEmailCode(emailLoginForm.email)
    addLog('success', `验证码发送成功: ${JSON.stringify(result)}`)
  } catch (error: any) {
    addLog('error', `发送验证码失败: ${error.message}`)
  }
}

const testGetUserInfo = async () => {
  try {
    addLog('info', '获取用户信息')
    const result = await fetchUserInfo()
    addLog('success', `获取用户信息成功: ${JSON.stringify(result)}`)
  } catch (error: any) {
    addLog('error', `获取用户信息失败: ${error.message}`)
  }
}

const testGetSession = async () => {
  try {
    addLog('info', '获取会话信息')
    const result = await getSession()
    addLog('success', `获取会话成功: ${JSON.stringify(result)}`)
  } catch (error: any) {
    addLog('error', `获取会话失败: ${error.message}`)
  }
}

const testLogout = async () => {
  try {
    addLog('info', '退出登录')
    await logout()
    addLog('success', '退出登录成功')
  } catch (error: any) {
    addLog('error', `退出登录失败: ${error.message}`)
  }
}
</script>

<style scoped>
.auth-test {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.test-section h2 {
  margin-top: 0;
  color: #333;
}

.test-section form div {
  margin-bottom: 10px;
}

.test-section label {
  display: inline-block;
  width: 80px;
  font-weight: bold;
}

.test-section input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 10px;
}

.test-section button {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 10px;
}

.test-section button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.log-container {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  padding: 10px;
  background: #f9f9f9;
}

.log-item {
  margin-bottom: 5px;
  font-family: monospace;
  font-size: 12px;
}

.log-time {
  color: #666;
  margin-right: 10px;
}

.log-level {
  padding: 2px 6px;
  border-radius: 3px;
  margin-right: 10px;
  font-weight: bold;
}

.log-level--info {
  background: #d1ecf1;
  color: #0c5460;
}

.log-level--success {
  background: #d4edda;
  color: #155724;
}

.log-level--error {
  background: #f8d7da;
  color: #721c24;
}
</style>
