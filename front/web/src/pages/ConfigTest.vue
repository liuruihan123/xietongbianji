<template>
  <div class="config-test-page">
    <div class="container">
      <h1>🔧 运行时配置测试页面</h1>
      
      <a-alert
        message="配置加载状态"
        :description="configStatus"
        :type="hasRuntimeConfig ? 'success' : 'warning'"
        show-icon
        style="margin-bottom: 24px;"
      />

      <div class="config-section">
        <h2>📋 当前配置信息</h2>
        
        <a-descriptions bordered :column="1">
          <a-descriptions-item label="应用标题">
            <a-tag color="blue">{{ config.APP_TITLE }}</a-tag>
          </a-descriptions-item>
          
          <a-descriptions-item label="应用版本">
            <a-tag color="green">{{ config.APP_VERSION }}</a-tag>
          </a-descriptions-item>
          
          <a-descriptions-item label="运行环境">
            <a-tag :color="envColor">{{ config.APP_ENV }}</a-tag>
          </a-descriptions-item>
          
          <a-descriptions-item label="API 基础地址">
            <a-typography-text copyable>{{ config.API_BASE_URL }}</a-typography-text>
          </a-descriptions-item>
          
          <a-descriptions-item label="OnlyOffice 地址">
            <a-typography-text copyable>{{ config.ONLYOFFICE_URL }}</a-typography-text>
          </a-descriptions-item>
          
          <a-descriptions-item label="WebSocket 地址">
            <a-typography-text copyable>{{ config.WS_URL }}</a-typography-text>
          </a-descriptions-item>
          
          <a-descriptions-item label="上传 URL">
            {{ config.UPLOAD_URL }}
          </a-descriptions-item>
          
          <a-descriptions-item label="Google Client ID">
            <a-typography-text copyable>{{ config.GOOGLE_CLIENT_ID }}</a-typography-text>
          </a-descriptions-item>
        </a-descriptions>
      </div>

      <div class="config-section">
        <h2>⚙️ 功能开关</h2>
        
        <a-space direction="vertical" style="width: 100%;">
          <div class="switch-item">
            <span>PWA 启用:</span>
            <a-tag :color="config.ENABLE_PWA ? 'success' : 'default'">
              {{ config.ENABLE_PWA ? '是' : '否' }}
            </a-tag>
          </div>
          
          <div class="switch-item">
            <span>懒加载启用:</span>
            <a-tag :color="config.ENABLE_LAZY_LOADING ? 'success' : 'default'">
              {{ config.ENABLE_LAZY_LOADING ? '是' : '否' }}
            </a-tag>
          </div>
          
          <div class="switch-item">
            <span>控制台日志:</span>
            <a-tag :color="config.ENABLE_CONSOLE ? 'success' : 'default'">
              {{ config.ENABLE_CONSOLE ? '启用' : '禁用' }}
            </a-tag>
          </div>
        </a-space>
      </div>

      <div class="config-section">
        <h2>🐛 调试配置</h2>
        
        <a-descriptions bordered :column="1">
          <a-descriptions-item label="调试模式">
            <a-tag :color="config.DEBUG.ENABLED ? 'warning' : 'default'">
              {{ config.DEBUG.ENABLED ? '启用' : '禁用' }}
            </a-tag>
          </a-descriptions-item>
          
          <a-descriptions-item label="请求日志">
            <a-tag :color="config.DEBUG.LOG_REQUEST ? 'warning' : 'default'">
              {{ config.DEBUG.LOG_REQUEST ? '启用' : '禁用' }}
            </a-tag>
          </a-descriptions-item>
          
          <a-descriptions-item label="响应日志">
            <a-tag :color="config.DEBUG.LOG_RESPONSE ? 'warning' : 'default'">
              {{ config.DEBUG.LOG_RESPONSE ? '启用' : '禁用' }}
            </a-tag>
          </a-descriptions-item>
        </a-descriptions>
      </div>

      <div class="config-section">
        <h2>⏱️ 超时配置</h2>
        
        <a-descriptions bordered :column="1">
          <a-descriptions-item label="HTTP 请求超时">
            {{ config.TIMEOUT.REQUEST }}ms
          </a-descriptions-item>
          
          <a-descriptions-item label="WebSocket 超时">
            {{ config.TIMEOUT.WEBSOCKET }}ms
          </a-descriptions-item>
        </a-descriptions>
      </div>

      <div class="config-section">
        <h2>📄 完整配置 JSON</h2>
        <pre class="json-display">{{ JSON.stringify(config, null, 2) }}</pre>
      </div>

      <div class="config-section">
        <h2>💡 使用说明</h2>
        <a-alert
          message="Docker 部署配置修改"
          type="info"
          show-icon
        >
          <template #description>
            <p>在 Docker 容器中，可以通过环境变量动态修改配置：</p>
            <pre class="code-block">docker run -d \
  -e API_BASE_URL=http://your-server:8500 \
  -e ONLYOFFICE_URL=http://your-onlyoffice:9000/ \
  -e WS_URL=ws://your-server:8500/ws \
  coedit-frontend:latest</pre>
            <p>修改后重启容器，刷新此页面即可看到新配置。</p>
          </template>
        </a-alert>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import runtimeConfig from '@/utils/runtimeConfig'

const config = runtimeConfig

const hasRuntimeConfig = computed(() => {
  return window.__APP_CONFIG__ !== undefined
})

const configStatus = computed(() => {
  if (hasRuntimeConfig.value) {
    return '✅ 运行时配置已成功加载 (window.__APP_CONFIG__)'
  } else {
    return '⚠️ 运行时配置未找到，使用环境变量作为回退方案'
  }
})

const envColor = computed(() => {
  const colors: Record<string, string> = {
    development: 'blue',
    production: 'green',
    staging: 'orange',
    testing: 'purple',
  }
  return colors[config.APP_ENV] || 'default'
})
</script>

<style scoped>
.config-test-page {
  min-height: 100vh;
  background: #f0f2f5;
  padding: 24px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h1 {
  margin-bottom: 24px;
  color: #1890ff;
}

h2 {
  margin-top: 0;
  margin-bottom: 16px;
  color: #333;
  font-size: 18px;
  border-bottom: 2px solid #1890ff;
  padding-bottom: 8px;
}

.config-section {
  margin-bottom: 32px;
}

.switch-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 4px;
}

.json-display {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 4px;
  overflow-x: auto;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.6;
}

.code-block {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  margin: 12px 0;
}
</style>

