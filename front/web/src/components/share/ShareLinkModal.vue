<template>
  <a-modal
    v-model:visible="visible"
    title="分享链接"
    width="600px"
    :footer="null"
  >
    <div class="share-link-content">
      <!-- 分享链接 -->
      <div class="link-section">
        <div class="section-title">分享链接:</div>
        <a-input
          :value="shareInfo?.shareUrl"
          readonly
          class="link-input"
        >
          <template #suffix>
            <a-button type="link" @click="copyLink">
              <CopyOutlined /> 复制
            </a-button>
          </template>
        </a-input>
      </div>

      <!-- 访问密码 -->
      <div v-if="shareInfo?.plainPassword || shareInfo?.sharePassword" class="password-section">
        <div class="section-title">访问密码</div>
        <a-input
          :value="shareInfo.plainPassword || shareInfo.sharePassword"
          readonly
          class="password-input"
        >
          <template #suffix>
            <a-button type="link" @click="copyPassword">
              <CopyOutlined /> 复制
            </a-button>
          </template>
        </a-input>
      </div>

      <!-- 二维码 -->
      <!-- <div class="qrcode-section">
        <div class="section-title">扫码访问</div>
        <div class="qrcode-container">
          <canvas ref="qrcodeCanvas"></canvas>
        </div>
      </div> -->

      <!-- 分享信息 -->
      <div class="info-section">
        <a-descriptions :column="2" size="small">
          <a-descriptions-item label="有效期至">
            {{ getExpireText() }}
          </a-descriptions-item>
          <a-descriptions-item label="访问次数">
            {{ shareInfo?.viewCount || 0 }}
          </a-descriptions-item>
          <a-descriptions-item label="下载次数">
            {{ shareInfo?.downloadCount || 0 }}
          </a-descriptions-item>
          <a-descriptions-item label="创建时间">
            {{ formatTime(shareInfo?.createTime) }}
          </a-descriptions-item>
        </a-descriptions>
      </div>

      <!-- 操作按钮 -->
      <div class="action-section">
        <a-space>
          <a-button type="primary" @click="copyAll">
            <CopyOutlined /> 复制全部
          </a-button>
          <a-button @click="viewStatistics">
            <BarChartOutlined /> 查看统计
          </a-button>
          <a-button @click="handleClose">
            关闭
          </a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import { CopyOutlined, BarChartOutlined } from '@ant-design/icons-vue'
import type { ShareInfo } from '@/types/share'
import { ShareExpireType } from '@/types/share'
import { copyShareLink } from '@/api/share'
import { copyToClipboard } from '@/utils/copy'
import dayjs from 'dayjs'
import QRCode from 'qrcode'

const emit = defineEmits<{
  viewStatistics: [shareInfo: ShareInfo]
}>()

const visible = ref(false)
const shareInfo = ref<ShareInfo>()
const qrcodeCanvas = ref<HTMLCanvasElement>()

function open(info: ShareInfo) {
  console.log('========== ShareLinkModal.open 被调用 ==========')
  console.log('接收到的 ShareInfo:', JSON.stringify(info, null, 2))
  console.log('shareUrl 字段值:', info.shareUrl)
  console.log('shareCode 字段值:', info.shareCode)
  console.log('password 字段值:', info.sharePassword)

  visible.value = true
  shareInfo.value = info

  console.log('模态框 visible 状态:', visible.value)
  console.log('shareInfo.value 已设置:', shareInfo.value)

  nextTick(() => {
    console.log('nextTick 中，准备生成二维码')
    generateQRCode()
  })
}

async function generateQRCode() {
  if (!qrcodeCanvas.value || !shareInfo.value) {
    return
  }

  try {
    await QRCode.toCanvas(qrcodeCanvas.value, shareInfo.value.shareUrl, {
      width: 200,
      margin: 2,
    })
  } catch (error) {
    console.error('生成二维码失败:', error)
  }
}

async function copyLink() {
  if (!shareInfo.value) return

  try {
    await copyToClipboard(shareInfo.value.shareUrl)
    message.success('链接已复制')
  } catch (error: any) {
    console.error('复制链接失败:', error)
    message.error(error.message || '复制失败,请手动复制')
  }
}

async function copyPassword() {
  if (!shareInfo.value) return

  // 优先使用明文密码,如果没有则使用加密密码
  const password = shareInfo.value.plainPassword || shareInfo.value.sharePassword
  if (!password) return

  try {
    await copyToClipboard(password)
    message.success('密码已复制')
  } catch (error: any) {
    console.error('复制密码失败:', error)
    message.error(error.message || '复制失败,请手动复制')
  }
}

async function copyAll() {
  if (!shareInfo.value) return

  try {
    await copyShareLink(shareInfo.value)
    message.success('已复制到剪贴板')
  } catch (error: any) {
    console.error('复制全部失败:', error)
    message.error(error.message || '复制失败,请手动复制')
  }
}

function getExpireText(): string {
  // if (!shareInfo.value) return '-'

  // switch (shareInfo.value.expireType) {
  //   case ShareExpireType.PERMANENT:
  //     return '永久有效'
  //   case ShareExpireType.ONE_DAY:
  //     return '1天'
  //   case ShareExpireType.SEVEN_DAYS:
  //     return '7天'
  //   case ShareExpireType.THIRTY_DAYS:
  //     return '30天'
  //   case ShareExpireType.CUSTOM:
  //     return shareInfo.value.expireTime
  //       ? dayjs(shareInfo.value.expireTime).format('YYYY-MM-DD HH:mm:ss')
  //       : '-'
  //   default:
  //     return '-'
  // }
  return  shareInfo.value?.expireTime
      ? dayjs(shareInfo.value.expireTime).format('YYYY-MM-DD HH:mm:ss')
      : '-'
}

function formatTime(time?: string): string {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

function viewStatistics() {
  if (shareInfo.value) {
    emit('viewStatistics', shareInfo.value)
  }
}

function handleClose() {
  visible.value = false
}

defineExpose({
  open,
})
</script>

<style scoped lang="scss">
.share-link-content {
  .link-section,
  .password-section,
  .qrcode-section,
  .info-section {
    margin-bottom: 24px;
  }

  .section-title {
    margin-bottom: 12px;
    font-weight: 600;
    color: #333;
  }

  .link-input,
  .password-input {
    font-family: monospace;
  }

  .qrcode-container {
    display: flex;
    justify-content: center;
    padding: 16px;
    background: #f5f5f5;
    border-radius: 8px;
  }

  .action-section {
    display: flex;
    justify-content: center;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }
}
</style>

