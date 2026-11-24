<template>
  <div class="captcha-test-page">
    <div class="test-container">
      <h1>滑动验证码测试页面</h1>
      
      <div class="test-section">
        <h2>基本功能测试</h2>
        <div class="test-controls">
          <button @click="showBasicCaptcha" class="test-btn">显示验证码</button>
          <button @click="resetBasicCaptcha" class="test-btn secondary">重置状态</button>
        </div>
        <div class="test-status">
          <p>验证状态: <span :class="basicStatus.class">{{ basicStatus.text }}</span></p>
          <p>失败次数: {{ basicCaptcha.state.attempts }}/{{ basicCaptcha.state.maxAttempts }}</p>
        </div>
      </div>

      <div class="test-section">
        <h2>集成测试 - 模拟登录流程</h2>
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label>用户名:</label>
            <input v-model="loginForm.username" type="text" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label>密码:</label>
            <input v-model="loginForm.password" type="password" placeholder="请输入密码" />
          </div>
          <button type="submit" class="test-btn" :disabled="!isLoginFormValid">
            {{ loginCaptcha.state.loading ? '登录中...' : '登录' }}
          </button>
        </form>
        <div class="test-status">
          <p>登录状态: <span :class="loginStatus.class">{{ loginStatus.text }}</span></p>
          <p>验证状态: <span :class="loginCaptchaStatus.class">{{ loginCaptchaStatus.text }}</span></p>
        </div>
      </div>

      <div class="test-section">
        <h2>配置测试</h2>
        <div class="config-controls">
          <div class="config-item">
            <label>宽度: {{ captchaConfig.width }}px</label>
            <input 
              v-model.number="captchaConfig.width" 
              type="range" 
              min="300" 
              max="500" 
              step="10"
            />
          </div>
          <div class="config-item">
            <label>高度: {{ captchaConfig.height }}px</label>
            <input 
              v-model.number="captchaConfig.height" 
              type="range" 
              min="150" 
              max="300" 
              step="10"
            />
          </div>
          <div class="config-item">
            <label>拼图大小: {{ captchaConfig.puzzleSize }}px</label>
            <input 
              v-model.number="captchaConfig.puzzleSize" 
              type="range" 
              min="40" 
              max="80" 
              step="5"
            />
          </div>
          <div class="config-item">
            <label>容错范围: {{ captchaConfig.tolerance }}px</label>
            <input 
              v-model.number="captchaConfig.tolerance" 
              type="range" 
              min="5" 
              max="20" 
              step="1"
            />
          </div>
        </div>
        <button @click="showConfigCaptcha" class="test-btn">测试自定义配置</button>
      </div>

      <!-- 日志区域 -->
      <div class="test-section">
        <h2>操作日志</h2>
        <div class="log-container">
          <div v-for="(log, index) in logs" :key="index" class="log-item" :class="log.type">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
        </div>
        <button @click="clearLogs" class="test-btn secondary">清空日志</button>
      </div>
    </div>

    <!-- 验证码组件 -->
    <SlideCaptcha
      v-model:visible="currentCaptcha.state.visible"
      :width="captchaConfig.width"
      :height="captchaConfig.height"
      :puzzle-size="captchaConfig.puzzleSize"
      :tolerance="captchaConfig.tolerance"
      @success="handleCaptchaSuccess"
      @fail="handleCaptchaFail"
      @close="handleCaptchaClose"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import SlideCaptcha from '@/components/common/SlideCaptcha.vue'
import { useCaptcha } from '@/hooks/useCaptcha'

// 验证码实例
const basicCaptcha = useCaptcha(3)
const loginCaptcha = useCaptcha(5)
const configCaptcha = useCaptcha(3)

// 当前活动的验证码
const currentCaptcha = ref(basicCaptcha)
const currentType = ref<'basic' | 'login' | 'config'>('basic')

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 验证码配置
const captchaConfig = reactive({
  width: 350,
  height: 200,
  puzzleSize: 60,
  tolerance: 10
})

// 日志
const logs = ref<Array<{time: string, message: string, type: string}>>([])

// 计算属性
const isLoginFormValid = computed(() => {
  return loginForm.username.trim() !== '' && loginForm.password.trim() !== ''
})

const basicStatus = computed(() => {
  if (basicCaptcha.state.verified) {
    return { text: '已验证', class: 'status-success' }
  } else if (basicCaptcha.state.attempts > 0) {
    return { text: '验证失败', class: 'status-error' }
  } else {
    return { text: '未验证', class: 'status-pending' }
  }
})

const loginStatus = computed(() => {
  if (loginCaptcha.state.loading) {
    return { text: '登录中...', class: 'status-loading' }
  } else if (loginCaptcha.state.verified && isLoginFormValid.value) {
    return { text: '登录成功', class: 'status-success' }
  } else {
    return { text: '未登录', class: 'status-pending' }
  }
})

const loginCaptchaStatus = computed(() => {
  if (loginCaptcha.state.verified) {
    return { text: '已验证', class: 'status-success' }
  } else if (loginCaptcha.state.attempts > 0) {
    return { text: '验证失败', class: 'status-error' }
  } else {
    return { text: '未验证', class: 'status-pending' }
  }
})

// 方法
const addLog = (message: string, type: string = 'info') => {
  const now = new Date()
  const time = now.toLocaleTimeString()
  logs.value.unshift({ time, message, type })
  
  // 限制日志数量
  if (logs.value.length > 50) {
    logs.value = logs.value.slice(0, 50)
  }
}

const showBasicCaptcha = () => {
  currentCaptcha.value = basicCaptcha
  currentType.value = 'basic'
  basicCaptcha.showCaptcha()
  addLog('显示基本验证码')
}

const resetBasicCaptcha = () => {
  basicCaptcha.resetCaptcha()
  addLog('重置基本验证码状态')
}

const handleLogin = () => {
  if (!isLoginFormValid.value) {
    addLog('登录表单验证失败', 'error')
    return
  }

  if (loginCaptcha.needsCaptcha()) {
    currentCaptcha.value = loginCaptcha
    currentType.value = 'login'
    loginCaptcha.showCaptcha()
    addLog('需要验证码验证，显示登录验证码')
    return
  }

  executeLogin()
}

const executeLogin = async () => {
  loginCaptcha.setLoading(true)
  addLog('开始执行登录...')
  
  try {
    // 模拟登录API调用
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    // 模拟登录成功
    addLog(`用户 ${loginForm.username} 登录成功`, 'success')
    loginCaptcha.resetCaptcha()
  } catch (error) {
    addLog('登录失败', 'error')
    loginCaptcha.resetCaptcha()
  } finally {
    loginCaptcha.setLoading(false)
  }
}

const showConfigCaptcha = () => {
  currentCaptcha.value = configCaptcha
  currentType.value = 'config'
  configCaptcha.showCaptcha()
  addLog(`显示自定义配置验证码 (${captchaConfig.width}x${captchaConfig.height})`)
}

const handleCaptchaSuccess = () => {
  currentCaptcha.value.onCaptchaSuccess()
  addLog(`${currentType.value} 验证码验证成功`, 'success')
  
  if (currentType.value === 'login') {
    executeLogin()
  }
}

const handleCaptchaFail = () => {
  currentCaptcha.value.onCaptchaFail()
  addLog(`${currentType.value} 验证码验证失败 (${currentCaptcha.value.state.attempts}/${currentCaptcha.value.state.maxAttempts})`, 'error')
}

const handleCaptchaClose = () => {
  currentCaptcha.value.onCaptchaClose()
  addLog(`${currentType.value} 验证码已关闭`)
}

const clearLogs = () => {
  logs.value = []
  addLog('日志已清空')
}

// 初始化日志
addLog('验证码测试页面已加载')
</script>

<style scoped>
.captcha-test-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.test-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.test-container h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
}

.test-section {
  margin-bottom: 40px;
  padding: 20px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background: #fafafa;
}

.test-section h2 {
  color: #555;
  margin-bottom: 20px;
  font-size: 20px;
}

.test-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.test-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #4fc3f7, #29b6f6);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.test-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.test-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.test-btn.secondary {
  background: linear-gradient(135deg, #90a4ae, #607d8b);
}

.test-status {
  background: white;
  padding: 15px;
  border-radius: 6px;
  border-left: 4px solid #2196f3;
}

.test-status p {
  margin: 5px 0;
  font-size: 14px;
}

.status-success {
  color: #4caf50;
  font-weight: bold;
}

.status-error {
  color: #f44336;
  font-weight: bold;
}

.status-pending {
  color: #ff9800;
  font-weight: bold;
}

.status-loading {
  color: #2196f3;
  font-weight: bold;
}

.login-form {
  background: white;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 15px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus {
  outline: none;
  border-color: #4fc3f7;
  box-shadow: 0 0 0 2px rgba(79, 195, 247, 0.2);
}

.config-controls {
  background: white;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 15px;
}

.config-item {
  margin-bottom: 15px;
}

.config-item label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.config-item input[type="range"] {
  width: 100%;
  margin: 5px 0;
}

.log-container {
  background: #1e1e1e;
  color: #fff;
  padding: 15px;
  border-radius: 6px;
  max-height: 300px;
  overflow-y: auto;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  margin-bottom: 15px;
}

.log-item {
  margin-bottom: 5px;
  padding: 2px 0;
}

.log-item.success {
  color: #4caf50;
}

.log-item.error {
  color: #f44336;
}

.log-item.info {
  color: #2196f3;
}

.log-time {
  color: #888;
  margin-right: 10px;
}

.log-message {
  color: inherit;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .test-container {
    margin: 10px;
    padding: 20px;
  }
  
  .test-controls {
    flex-direction: column;
  }
  
  .test-btn {
    width: 100%;
  }
}
</style>
