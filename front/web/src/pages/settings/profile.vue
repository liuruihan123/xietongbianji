<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>个人中心</h2>
    </div>

    <a-tabs v-model:activeKey="activeTab">
      <!-- 基本信息 -->
      <a-tab-pane key="basic" tab="基本信息">
        <div class="tab-content">
          <!-- 头像 -->
          <div class="avatar-section">
            <div class="avatar-label">头像</div>
            <div class="avatar-content">
              <a-avatar :size="100" :src="userProfile.avatar">
                <template #icon><UserOutlined /></template>
              </a-avatar>
              <a-upload
                :show-upload-list="false"
                :before-upload="beforeAvatarUpload"
                :custom-request="handleAvatarUpload"
                accept="image/jpeg,image/jpg,image/png"
              >
                <a-button type="link">更换头像</a-button>
              </a-upload>
            </div>
          </div>

          <!-- 基本信息表单 -->
          <a-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 12 }"
          >
            <a-form-item label="用户名">
              <a-input v-model:value="userProfile.username" disabled />
            </a-form-item>

            <a-form-item label="昵称" name="nickname">
              <a-input v-model:value="profileForm.nickname" placeholder="请输入昵称" />
            </a-form-item>

            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="profileForm.email" placeholder="请输入邮箱" />
            </a-form-item>

            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="profileForm.phone" placeholder="请输入手机号" />
            </a-form-item>

            <a-form-item label="性别" name="gender">
              <a-radio-group v-model:value="profileForm.gender">
                <a-radio
                  v-for="option in GENDER_OPTIONS"
                  :key="option.value"
                  :value="option.value"
                >
                  {{ option.label }}
                </a-radio>
              </a-radio-group>
            </a-form-item>

            <a-form-item label="生日" name="birthday">
              <a-date-picker
                v-model:value="profileForm.birthday"
                placeholder="请选择生日"
                style="width: 100%"
              />
            </a-form-item>

            <a-form-item label="个性签名" name="signature">
              <a-textarea
                v-model:value="profileForm.signature"
                placeholder="请输入个性签名"
                :rows="4"
                :maxlength="200"
                show-count
              />
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 4, span: 12 }">
              <a-space>
                <a-button type="primary" :loading="profileLoading" @click="handleUpdateProfile">
                  保存
                </a-button>
                <a-button @click="handleResetProfile">重置</a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </div>
      </a-tab-pane>

      <!-- 安全设置 -->
      <a-tab-pane key="security" tab="安全设置">
        <div class="tab-content">
          <a-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 12 }"
          >
            <a-form-item label="旧密码" name="oldPassword">
              <a-input-password
                v-model:value="passwordForm.oldPassword"
                placeholder="请输入旧密码"
              />
            </a-form-item>

            <a-form-item label="新密码" name="newPassword">
              <a-input-password
                v-model:value="passwordForm.newPassword"
                placeholder="请输入新密码(6-20位)"
              />
            </a-form-item>

            <a-form-item label="确认密码" name="confirmPassword">
              <a-input-password
                v-model:value="passwordForm.confirmPassword"
                placeholder="请再次输入新密码"
              />
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 4, span: 12 }">
              <a-space>
                <a-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">
                  修改密码
                </a-button>
                <a-button @click="handleResetPassword">重置</a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </div>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { getUserProfile, updateUserProfile, updatePassword, uploadAvatar, updateAvatar } from '@/api/settings'
import { Gender, GENDER_OPTIONS, type UserProfile, type UpdateProfileParams } from '@/types/settings'
import type { UploadProps } from 'ant-design-vue'
import dayjs, { type Dayjs } from 'dayjs'
import { useRouter } from 'vue-router'

const router = useRouter()

// 状态
const activeTab = ref('basic')
const profileLoading = ref(false)
const passwordLoading = ref(false)
const profileFormRef = ref()
const passwordFormRef = ref()

// 用户信息
const userProfile = ref<UserProfile>({
  id: 0,
  username: '',
  nickname: '',
  avatar: '',
  email: '',
  phone: '',
  gender: Gender.UNKNOWN,
  birthday: '',
  signature: '',
  createTime: '',
  updateTime: '',
})

// 个人信息表单
const profileForm = reactive<UpdateProfileParams & { birthday: Dayjs | null }>({
  nickname: '',
  email: '',
  phone: '',
  gender: Gender.UNKNOWN,
  birthday: null,
  signature: '',
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 个人信息表单验证规则
const profileRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { required: false },
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' },
  ],
  phone: [
    { required: false },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
}

// 密码表单验证规则
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' },
    {
      validator: (_rule: any, value: string) => {
        if (value && value === passwordForm.oldPassword) {
          return Promise.reject('新密码不能与旧密码相同')
        }
        return Promise.resolve()
      },
      trigger: 'blur',
    },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string) => {
        if (value && value !== passwordForm.newPassword) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur',
    },
  ],
}

/**
 * 加载用户信息
 */
async function loadUserProfile() {
  try {
    userProfile.value = await getUserProfile()

    // 填充表单
    profileForm.nickname = userProfile.value.nickname
    profileForm.email = userProfile.value.email
    profileForm.phone = userProfile.value.phone
    profileForm.gender = userProfile.value.gender
    profileForm.birthday = userProfile.value.birthday ? dayjs(userProfile.value.birthday) : null
    profileForm.signature = userProfile.value.signature
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

/**
 * 头像上传前验证
 */
function beforeAvatarUpload(file: File) {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/jpg' || file.type === 'image/png'
  if (!isImage) {
    message.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }

  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }

  return true
}

/**
 * 头像上传
 */
async function handleAvatarUpload(options: any) {
  try {
    const { file } = options
    const result = await uploadAvatar(file)
    await updateAvatar({ avatar: result.url })

    message.success('头像上传成功')
    await loadUserProfile()
  } catch (error) {
    message.error('头像上传失败')
    console.error('头像上传失败:', error)
  }
}

/**
 * 更新个人信息
 */
async function handleUpdateProfile() {
  try {
    await profileFormRef.value.validate()

    profileLoading.value = true
    const params: UpdateProfileParams = {
      nickname: profileForm.nickname,
      email: profileForm.email,
      phone: profileForm.phone,
      gender: profileForm.gender,
      birthday: profileForm.birthday ? profileForm.birthday.format('YYYY-MM-DD') : '',
      signature: profileForm.signature,
    }

    await updateUserProfile(params)
    message.success('个人信息更新成功')
    await loadUserProfile()
  } catch (error: any) {
    if (error.errorFields) {
      // 表单验证错误
      return
    }
    message.error('个人信息更新失败')
    console.error('个人信息更新失败:', error)
  } finally {
    profileLoading.value = false
  }
}

/**
 * 重置个人信息表单
 */
function handleResetProfile() {
  profileForm.nickname = userProfile.value.nickname
  profileForm.email = userProfile.value.email
  profileForm.phone = userProfile.value.phone
  profileForm.gender = userProfile.value.gender
  profileForm.birthday = userProfile.value.birthday ? dayjs(userProfile.value.birthday) : null
  profileForm.signature = userProfile.value.signature
  profileFormRef.value?.clearValidate()
}

/**
 * 修改密码
 */
async function handleUpdatePassword() {
  try {
    await passwordFormRef.value.validate()

    passwordLoading.value = true
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword,
    })

    Modal.success({
      title: '密码修改成功',
      content: '请重新登录',
      onOk: () => {
        // 清除登录信息,跳转到登录页
        localStorage.removeItem('token')
        router.push('/login')
      },
    })
  } catch (error: any) {
    if (error.errorFields) {
      // 表单验证错误
      return
    }
    message.error('密码修改失败')
    console.error('密码修改失败:', error)
  } finally {
    passwordLoading.value = false
  }
}

/**
 * 重置密码表单
 */
function handleResetPassword() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

/**
 * 组件挂载
 */
onMounted(() => {
  loadUserProfile()
})
</script>

<style scoped lang="scss">
.profile-page {
  padding: 24px;

  .page-header {
    margin-bottom: 24px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }

  .tab-content {
    padding: 24px 0;
  }

  .avatar-section {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #f0f0f0;

    .avatar-label {
      width: 16.666667%;
      padding-right: 8px;
      text-align: right;
      color: rgba(0, 0, 0, 0.85);

      &::before {
        display: inline-block;
        margin-right: 4px;
        color: #ff4d4f;
        font-size: 14px;
        font-family: SimSun, sans-serif;
        line-height: 1;
        content: '*';
      }
    }

    .avatar-content {
      display: flex;
      align-items: center;
      gap: 16px;
    }
  }
}
</style>

