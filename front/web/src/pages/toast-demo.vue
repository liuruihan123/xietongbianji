<template>
  <div class="toast-demo-container">
    <div class="demo-header">
      <h1>🎉 新的通知系统演示</h1>
      <p>替代丑陋的 alert，使用现代化的 Toast 通知</p>
    </div>

    <div class="demo-grid">
      <div class="demo-section">
        <h3>基础通知类型</h3>
        <div class="button-group">
          <button @click="showSuccessDemo" class="demo-btn success">
            ✅ 成功通知
          </button>
          <button @click="showErrorDemo" class="demo-btn error">
            ❌ 错误通知
          </button>
          <button @click="showWarningDemo" class="demo-btn warning">
            ⚠️ 警告通知
          </button>
          <button @click="showInfoDemo" class="demo-btn info">
            ℹ️ 信息通知
          </button>
        </div>
      </div>

      <div class="demo-section">
        <h3>交互式通知</h3>
        <div class="button-group">
          <button @click="showConfirmDemo" class="demo-btn confirm">
            🤔 确认对话框
          </button>
          <button @click="showLoadingDemo" class="demo-btn loading">
            ⏳ 加载通知
          </button>
          <button @click="showPromiseDemo" class="demo-btn promise">
            🔄 Promise 通知
          </button>
        </div>
      </div>

      <div class="demo-section">
        <h3>实际应用场景</h3>
        <div class="button-group">
          <button @click="simulateSubscriptionSuccess" class="demo-btn scenario">
            📧 订阅成功
          </button>
          <button @click="simulateLoginError" class="demo-btn scenario">
            🔐 登录失败
          </button>
          <button @click="simulateVipUpgrade" class="demo-btn scenario">
            💎 VIP 升级确认
          </button>
        </div>
      </div>
    </div>

    <div class="demo-footer">
      <p>这些美观的通知已经替换了原来的 alert() 调用</p>
      <p>支持自定义样式、位置、持续时间等配置</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { 
  showSuccess, 
  showError, 
  showWarning, 
  showInfo, 
  showConfirm, 
  showLoading,
  showPromise,
  Toast 
} from '@/utils/toast'

// 基础通知演示
const showSuccessDemo = () => {
  showSuccess('操作成功完成！', { duration: 3000 })
}

const showErrorDemo = () => {
  showError('操作失败，请重试', { duration: 4000 })
}

const showWarningDemo = () => {
  showWarning('请注意：此操作不可撤销', { duration: 3500 })
}

const showInfoDemo = () => {
  showInfo('这是一条信息提示', { duration: 3000 })
}

// 交互式通知演示
const showConfirmDemo = () => {
  showConfirm(
    '确定要删除这个项目吗？此操作不可撤销。',
    () => {
      showSuccess('项目已删除')
    },
    () => {
      showInfo('操作已取消')
    }
  )
}

const showLoadingDemo = () => {
  const loadingToast = showLoading('正在处理中...')
  
  // 3秒后自动关闭并显示成功
  setTimeout(() => {
    Toast.dismiss(loadingToast)
    showSuccess('处理完成！')
  }, 3000)
}

const showPromiseDemo = () => {
  const mockPromise = new Promise((resolve, reject) => {
    setTimeout(() => {
      Math.random() > 0.5 ? resolve('成功') : reject(new Error('失败'))
    }, 2000)
  })

  showPromise(mockPromise, {
    loading: '正在执行异步操作...',
    success: '异步操作成功完成！',
    error: '异步操作失败了'
  })
}

// 实际应用场景演示
const simulateSubscriptionSuccess = () => {
  showSuccess('Binance Telegram 订阅成功！您将开始接收通知。')
}

const simulateLoginError = () => {
  showError('登录失败：用户名或密码错误，请重试')
}

const simulateVipUpgrade = () => {
  showConfirm(
    '此功能仅限 VIP 用户使用，请先开通 VIP 会员。是否立即前往开通 VIP？',
    () => {
      showInfo('正在跳转到 VIP 购买页面...')
    }
  )
}
</script>

<style scoped lang="scss">
.toast-demo-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.demo-header {
  text-align: center;
  margin-bottom: 3rem;
  
  h1 {
    font-size: 2.5rem;
    color: #1890ff;
    margin-bottom: 0.5rem;
  }
  
  p {
    font-size: 1.1rem;
    color: #666;
  }
}

.demo-grid {
  display: grid;
  gap: 2rem;
  margin-bottom: 3rem;
}

.demo-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  
  h3 {
    margin-bottom: 1rem;
    color: #333;
    font-size: 1.2rem;
  }
}

.button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.demo-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
  
  &.success {
    background: linear-gradient(135deg, #52c41a, #73d13d);
    color: white;
  }
  
  &.error {
    background: linear-gradient(135deg, #ff4d4f, #ff7875);
    color: white;
  }
  
  &.warning {
    background: linear-gradient(135deg, #faad14, #ffc53d);
    color: white;
  }
  
  &.info {
    background: linear-gradient(135deg, #1890ff, #40a9ff);
    color: white;
  }
  
  &.confirm {
    background: linear-gradient(135deg, #722ed1, #9254de);
    color: white;
  }
  
  &.loading {
    background: linear-gradient(135deg, #13c2c2, #36cfc9);
    color: white;
  }
  
  &.promise {
    background: linear-gradient(135deg, #eb2f96, #f759ab);
    color: white;
  }
  
  &.scenario {
    background: linear-gradient(135deg, #595959, #8c8c8c);
    color: white;
  }
}

.demo-footer {
  text-align: center;
  padding: 2rem;
  background: #f0f2f5;
  border-radius: 12px;
  
  p {
    margin: 0.5rem 0;
    color: #666;
  }
}

@media (max-width: 768px) {
  .toast-demo-container {
    padding: 1rem;
  }
  
  .demo-header h1 {
    font-size: 2rem;
  }
  
  .button-group {
    flex-direction: column;
  }
  
  .demo-btn {
    width: 100%;
  }
}
</style>
