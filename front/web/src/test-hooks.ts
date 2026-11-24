// 测试hooks功能的简单脚本
import { useAuth, useLanguage, useTheme, useAuthValidator } from '@/hooks'

// 测试认证hooks
export const testAuthHooks = () => {
  console.log('Testing Auth Hooks...')
  
  const { userInfo, isLoggedIn, login, logout } = useAuth()
  console.log('Auth hooks loaded:', { userInfo, isLoggedIn })
  
  // 测试登录
  const testLogin = async () => {
    try {
      await login({ userName: 'test@example.com', password: 'Test123456' })
      console.log('Login test passed')
    } catch (error) {
      console.log('Login test (expected to fail):', error)
    }
  }
  
  return { testLogin }
}

// 测试语言hooks
export const testLanguageHooks = () => {
  console.log('Testing Language Hooks...')
  
  const { currentLanguage, availableLanguages, setLanguage } = useLanguage()
  console.log('Language hooks loaded:', { currentLanguage, availableLanguages })
  
  // 测试语言切换
  const testLanguageSwitch = () => {
    const newLang = currentLanguage.value.key === 'zh-CN' ? 'en-US' : 'zh-CN'
    setLanguage(newLang)
    console.log('Language switched to:', newLang)
  }
  
  return { testLanguageSwitch }
}

// 测试主题hooks
export const testThemeHooks = () => {
  console.log('Testing Theme Hooks...')
  
  const { themeMode, themeConfig, setThemeMode, setPrimaryColor } = useTheme()
  console.log('Theme hooks loaded:', { themeMode, themeConfig })
  
  // 测试主题切换
  const testThemeSwitch = () => {
    const newMode = themeMode.value === 'light' ? 'dark' : 'light'
    setThemeMode(newMode)
    console.log('Theme switched to:', newMode)
  }
  
  // 测试颜色切换
  const testColorSwitch = () => {
    setPrimaryColor('#ff6b6b')
    console.log('Primary color changed to: #ff6b6b')
  }
  
  return { testThemeSwitch, testColorSwitch }
}

// 测试验证hooks
export const testValidatorHooks = () => {
  console.log('Testing Validator Hooks...')
  
  const { validateEmail, validatePassword, checkPasswordStrength } = useAuthValidator()
  
  // 测试邮箱验证
  const emailTest = validateEmail('test@example.com')
  console.log('Email validation test:', emailTest)
  
  // 测试密码验证
  const passwordTest = validatePassword('Test123456')
  console.log('Password validation test:', passwordTest)
  
  // 测试密码强度
  const strengthTest = checkPasswordStrength('Test123456!')
  console.log('Password strength test:', strengthTest)
  
  return { emailTest, passwordTest, strengthTest }
}

// 运行所有测试
export const runAllTests = () => {
  console.log('=== Running All Hook Tests ===')
  
  const authTests = testAuthHooks()
  const langTests = testLanguageHooks()
  const themeTests = testThemeHooks()
  const validatorTests = testValidatorHooks()
  
  return {
    auth: authTests,
    language: langTests,
    theme: themeTests,
    validator: validatorTests
  }
}
