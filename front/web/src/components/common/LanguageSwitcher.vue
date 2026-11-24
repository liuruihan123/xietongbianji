<template>
  <div class="language-switcher">
    <a-dropdown :trigger="['click']" placement="bottomRight">
      <a class="language-trigger" @click.prevent>
        <span class="current-flag">{{ currentLanguage.flag }}</span>
        <span class="current-name">{{ currentLanguage.nativeName }}</span>
        <DownOutlined class="dropdown-icon" />
      </a>
      <template #overlay>
        <a-menu @click="handleLanguageChange">
          <a-menu-item 
            v-for="option in languageOptions" 
            :key="option.key"
            :class="{ active: option.key === currentLocale }"
          >
            <span class="language-option">
              <span class="flag">{{ option.flag }}</span>
              <span class="name">{{ option.label }}</span>
            </span>
          </a-menu-item>
        </a-menu>
      </template>
    </a-dropdown>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { useLanguage } from '@/hooks'
import { useRouter } from 'vue-router'

const { 
  currentLocale, 
  currentLanguage, 
  getLanguageOptions,
  switchLanguageWithUrl 
} = useLanguage()

const router = useRouter()

const languageOptions = computed(() => getLanguageOptions())

const handleLanguageChange = ({ key }: { key: string }) => {
  if (key !== currentLocale.value) {
    switchLanguageWithUrl(key, router)
  }
}
</script>

<style scoped>
.language-switcher {
  display: inline-block;
}

.language-trigger {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 6px;
  color: inherit;
  text-decoration: none;
  transition: all 0.2s ease;
  cursor: pointer;
}

.language-trigger:hover {
  background-color: rgba(0, 0, 0, 0.04);
  color: inherit;
  text-decoration: none;
}

.current-flag {
  font-size: 16px;
  margin-right: 8px;
}

.current-name {
  margin-right: 8px;
  font-size: 14px;
}

.dropdown-icon {
  font-size: 12px;
  color: #999;
}

.language-option {
  display: flex;
  align-items: center;
  min-width: 120px;
}

.language-option .flag {
  font-size: 16px;
  margin-right: 8px;
}

.language-option .name {
  font-size: 14px;
}

:deep(.ant-menu-item.active) {
  background-color: #e6f7ff;
  color: #1890ff;
}

:deep(.ant-menu-item.active::after) {
  border-right-color: #1890ff;
}
</style>
