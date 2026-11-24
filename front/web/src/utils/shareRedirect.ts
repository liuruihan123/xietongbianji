/**
 * 分享链接重定向处理工具
 * 用于处理后端 /api/coedit/share/open/ 路由的重定向问题
 */

/**
 * 检查当前URL是否为后端分享链接
 */
export function isBackendShareUrl(): boolean {
  return window.location.pathname.includes('/api/coedit/share/open/')
}

/**
 * 从后端分享URL中提取分享码和参数
 */
export function parseBackendShareUrl(): {
  shareCode: string
  params: URLSearchParams
} | null {
  const pathname = window.location.pathname
  const search = window.location.search
  
  // 匹配 /api/coedit/share/open/{shareCode} 格式
  const match = pathname.match(/\/api\/coedit\/share\/open\/([^\/]+)/)
  
  if (!match) {
    return null
  }
  
  const shareCode = match[1]
  const params = new URLSearchParams(search)
  
  return { shareCode, params }
}

/**
 * 重定向到前端分享编辑器页面
 */
export function redirectToShareEditor(): void {
  const parsed = parseBackendShareUrl()
  
  if (!parsed) {
    console.error('无法解析后端分享URL')
    return
  }
  
  const { shareCode, params } = parsed
  
  // 构造前端分享编辑器URL
  const frontendUrl = `/share/editor/${shareCode}?${params.toString()}`
  
  console.log('重定向到前端分享编辑器:', frontendUrl)
  
  // 使用 replace 避免在历史记录中留下后端URL
  window.location.replace(frontendUrl)
}

/**
 * 初始化分享重定向处理
 * 在应用启动时调用
 */
export function initShareRedirect(): void {
  // 检查是否为后端分享URL
  if (isBackendShareUrl()) {
    console.log('检测到后端分享URL，准备重定向')
    redirectToShareEditor()
  }
}

/**
 * 创建一个全局错误处理脚本
 * 用于在HTML页面中直接处理JavaScript错误
 */
export function createGlobalErrorHandler(): string {
  return `
    <script>
      // 全局错误处理
      window.addEventListener('error', function(event) {
        console.error('JavaScript错误:', event.error);
        
        // 检查是否为分享页面的配置错误
        if (event.message && event.message.includes('config is not defined')) {
          console.log('检测到config未定义错误，尝试重定向到前端页面');
          
          // 提取分享码
          const pathname = window.location.pathname;
          const match = pathname.match(/\\/api\\/coedit\\/share\\/open\\/([^\\/]+)/);
          
          if (match) {
            const shareCode = match[1];
            const search = window.location.search;
            const frontendUrl = '/share/editor/' + shareCode + search;
            
            console.log('重定向到:', frontendUrl);
            window.location.replace(frontendUrl);
          }
        }
      });
      
      // 检查是否为后端分享URL
      if (window.location.pathname.includes('/api/coedit/share/open/')) {
        console.log('检测到后端分享URL，立即重定向');
        
        const pathname = window.location.pathname;
        const match = pathname.match(/\\/api\\/coedit\\/share\\/open\\/([^\\/]+)/);
        
        if (match) {
          const shareCode = match[1];
          const search = window.location.search;
          const frontendUrl = '/share/editor/' + shareCode + search;
          
          window.location.replace(frontendUrl);
        }
      }
    </script>
  `;
}
