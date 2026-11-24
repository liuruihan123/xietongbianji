<template>
  <a-modal
    :visible="visible"
    title="选择工作空间封面"
    :width="800"
    :footer="null"
    @cancel="handleCancel"
  >
    <div class="cover-select-container">
      <div class="cover-grid">
        <div
          v-for="(cover, index) in coverList"
          :key="index"
          :class="['cover-item', { selected: selectedCover === cover }]"
          @click="handleSelectCover(cover)"
        >
          <img :src="cover" :alt="`封面 ${index + 1}`" class="cover-image" />
          <div v-if="selectedCover === cover" class="selected-icon">
            <i class="fa fa-check-circle"></i>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <a-button @click="handleCancel">取消</a-button>
        <a-button type="primary" @click="handleConfirm" :disabled="!selectedCover">
          确定
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  visible: boolean
  currentCover?: string
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  'select': [cover: string]
}>()

// 预设封面列表
const coverList = ref<string[]>([])

// 选中的封面
const selectedCover = ref<string>('')

// 初始化封面列表
function initCoverList() {
  const covers: string[] = []
  for (let i = 1; i <= 11; i++) {
    try {
      const coverUrl = new URL(`../../../assets/work_icon/work_icons (${i}).jpg`, import.meta.url).href
      covers.push(coverUrl)
    } catch (error) {
      console.error(`加载封面 ${i} 失败:`, error)
    }
  }
  coverList.value = covers
}

// 监听visible变化,初始化选中状态
watch(() => props.visible, (val) => {
  if (val) {
    selectedCover.value = props.currentCover || ''
    if (coverList.value.length === 0) {
      initCoverList()
    }
  }
})

// 选择封面
function handleSelectCover(cover: string) {
  selectedCover.value = cover
}

// 确认选择
function handleConfirm() {
  if (selectedCover.value) {
    emit('select', selectedCover.value)
    emit('update:visible', false)
  }
}

// 取消
function handleCancel() {
  emit('update:visible', false)
}

// 组件挂载时初始化
initCoverList()
</script>

<style scoped lang="scss">
.cover-select-container {
  .cover-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin-bottom: 24px;
    max-height: 500px;
    overflow-y: auto;
    padding: 8px;

    .cover-item {
      position: relative;
      aspect-ratio: 16 / 9;
      border-radius: 8px;
      overflow: hidden;
      cursor: pointer;
      border: 2px solid transparent;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      &.selected {
        border-color: #1890ff;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
      }

      .cover-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .selected-icon {
        position: absolute;
        top: 8px;
        right: 8px;
        width: 28px;
        height: 28px;
        background: #1890ff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 16px;
      }
    }
  }

  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }
}
</style>

