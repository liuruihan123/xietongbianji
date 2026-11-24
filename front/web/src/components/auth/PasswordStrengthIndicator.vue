<template>
  <div class="password-strength-indicator">
    <div class="strength-bar-container">
      <div 
        class="strength-bar"
        :class="strengthClass"
        :style="{ width: `${(strength.score / 4) * 100}%` }"
      />
    </div>
    <div class="strength-info">
      <span class="strength-level" :style="{ color: strength.color }">
        {{ strengthText }}
      </span>
      <div v-if="showFeedback && strength.feedback.length > 0" class="strength-feedback">
        <ul>
          <li v-for="(feedback, index) in strength.feedback" :key="index">
            {{ feedback }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthValidator } from '@/hooks'
import { useI18n } from 'vue-i18n'

interface Props {
  password: string
  showFeedback?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showFeedback: true
})

const { checkPasswordStrength } = useAuthValidator()
const { t } = useI18n()

const strength = computed(() => checkPasswordStrength(props.password))

const strengthText = computed(() => {
  switch (strength.value.level) {
    case 'weak':
      return t('auth.passwordStrength.weak', '很弱')
    case 'fair':
      return t('auth.passwordStrength.fair', '较弱')
    case 'good':
      return t('auth.passwordStrength.good', '一般')
    case 'strong':
      return t('auth.passwordStrength.strong', '强')
    case 'very-strong':
      return t('auth.passwordStrength.veryStrong', '很强')
    default:
      return ''
  }
})

const strengthClass = computed(() => {
  return `strength-${strength.value.level}`
})
</script>

<style scoped>
.password-strength-indicator {
  margin-top: 8px;
}

.strength-bar-container {
  width: 100%;
  height: 4px;
  background-color: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 8px;
}

.strength-bar {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.strength-bar.strength-weak {
  background-color: #ff4d4f;
}

.strength-bar.strength-fair {
  background-color: #ff7a45;
}

.strength-bar.strength-good {
  background-color: #faad14;
}

.strength-bar.strength-strong {
  background-color: #73d13d;
}

.strength-bar.strength-very-strong {
  background-color: #52c41a;
}

.strength-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  font-size: 12px;
}

.strength-level {
  font-weight: 500;
}

.strength-feedback {
  flex: 1;
  margin-left: 16px;
}

.strength-feedback ul {
  margin: 0;
  padding: 0;
  list-style: none;
  color: #666;
}

.strength-feedback li {
  margin-bottom: 2px;
}

.strength-feedback li:before {
  content: '• ';
  color: #999;
}
</style>
