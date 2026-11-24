/**
 * 复制文本到剪贴板
 * 优先使用现代 Clipboard API,失败后降级到 execCommand
 * @param text 要复制的文本
 * @returns Promise<void>
 */
export async function copyToClipboard(text: string): Promise<void> {
  // 方案1: 尝试使用现代 Clipboard API
  if (navigator.clipboard && navigator.clipboard.writeText) {
    try {
      await navigator.clipboard.writeText(text)
      console.log('✅ 使用 Clipboard API 复制成功')
      return
    } catch (error) {
      console.warn('⚠️ Clipboard API 复制失败,尝试降级方案:', error)
      // 继续尝试降级方案
    }
  }

  // 方案2: 降级到 execCommand (兼容旧浏览器和非HTTPS环境)
  try {
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.top = '0'
    textarea.style.left = '0'
    textarea.style.width = '1px'
    textarea.style.height = '1px'
    textarea.style.padding = '0'
    textarea.style.border = 'none'
    textarea.style.outline = 'none'
    textarea.style.boxShadow = 'none'
    textarea.style.background = 'transparent'
    textarea.setAttribute('readonly', 'readonly')

    document.body.appendChild(textarea)

    // 选中文本
    textarea.select()
    textarea.setSelectionRange(0, text.length)

    // 执行复制命令
    const successful = document.execCommand('copy')
    document.body.removeChild(textarea)

    if (successful) {
      console.log('✅ 使用 execCommand 复制成功')
      return
    } else {
      throw new Error('execCommand 返回 false')
    }
  } catch (error) {
    console.error('❌ 所有复制方案都失败了:', error)
    throw new Error('复制失败,请检查浏览器权限或手动复制')
  }
}

/**
 * 旧版本兼容函数
 * @deprecated 请使用 copyToClipboard
 */
export function copyToClip(text: string) {
  return copyToClipboard(text)
}
