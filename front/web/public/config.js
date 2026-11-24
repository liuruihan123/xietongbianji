/**
 * 运行时配置文件
 * 此文件可以在部署后动态修改，无需重新打包
 * 
 * 使用说明：
 * 1. 部署时根据实际环境修改此文件中的配置
 * 2. 所有 IP 地址和服务端点都可以在此文件中配置
 * 3. 修改后刷新页面即可生效，无需重新构建
 */

window.__APP_CONFIG__ = {
  /**
   * 应用基础信息
   * Docker 部署时可通过环境变量 ${APP_TITLE} 等进行替换
   */
  APP_TITLE: '${APP_TITLE}',
  APP_VERSION: '${APP_VERSION}',
  APP_ENV: '${APP_ENV}', // 'development' | 'production' | 'staging' | 'testing'

  /**
   * API 配置
   * 后端服务器地址
   * Docker 部署时通过环境变量 ${API_BASE_URL} 替换
   */
  API_BASE_URL: '${API_BASE_URL}',

  /**
   * API 路径前缀
   * 生产环境通常使用 /api
   */
  API_URL_PREFIX: '/api',

  /**
   * WebSocket 配置
   * 用于实时协作功能
   * Docker 部署时通过环境变量 ${WS_URL} 替换
   */
  WS_URL: '${WS_URL}',

  /**
   * 文件上传配置
   */
  UPLOAD_URL: '/startTask',

  /**
   * OnlyOffice Document Server 配置
   * 文档编辑服务器地址
   * Docker 部署时通过环境变量 ${ONLYOFFICE_URL} 替换
   */
  ONLYOFFICE_URL: '${ONLYOFFICE_URL}',

  /**
   * 第三方服务配置
   */
  // Google 登录客户端 ID
  GOOGLE_CLIENT_ID: '${GOOGLE_CLIENT_ID:-your-production-google-client-id.apps.googleusercontent.com}',

  /**
   * 功能开关
   */
  // 是否启用 PWA
  ENABLE_PWA: true,
  // 是否启用懒加载
  ENABLE_LAZY_LOADING: true,
  // 是否在控制台输出日志（生产环境建议关闭）
  ENABLE_CONSOLE: false,

  /**
   * 调试配置（生产环境建议全部关闭）
   */
  DEBUG: {
    // 是否启用调试模式
    ENABLED: false,
    // 是否显示请求日志
    LOG_REQUEST: false,
    // 是否显示响应日志
    LOG_RESPONSE: false,
  },

  /**
   * 超时配置（毫秒）
   */
  TIMEOUT: {
    // HTTP 请求超时时间
    REQUEST: 15000,
    // WebSocket 连接超时时间
    WEBSOCKET: 10000,
  },

  /**
   * 部署环境配置示例
   * 
   * 开发环境：
   * API_BASE_URL: 'http://34.200.2.14:8500'
   * ONLYOFFICE_URL: 'http://34.200.2.14:9000/'
   * ENABLE_CONSOLE: true
   * DEBUG.ENABLED: true
   * 
   * 测试环境：
   * API_BASE_URL: 'http://test-api.your-domain.com'
   * ONLYOFFICE_URL: 'http://test-onlyoffice.your-domain.com:9000/'
   * ENABLE_CONSOLE: true
   * DEBUG.ENABLED: true
   * 
   * 预发布环境：
   * API_BASE_URL: 'https://staging-api.your-domain.com'
   * ONLYOFFICE_URL: 'https://staging-onlyoffice.your-domain.com/'
   * ENABLE_CONSOLE: false
   * DEBUG.ENABLED: false
   * 
   * 生产环境：
   * API_BASE_URL: 'https://api.your-domain.com'
   * ONLYOFFICE_URL: 'https://onlyoffice.your-domain.com/'
   * ENABLE_CONSOLE: false
   * DEBUG.ENABLED: false
   */
}

