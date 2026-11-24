/**
 * 认证相关的存储辅助函数
 */

const TOKEN_KEY = "ACCESS_TOKEN";
const USER_INFO_KEY = "USER_INFO";

/**
 * 获取存储的token
 */
export function getToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch (error) {
    console.error("获取token失败:", error);
    return null;
  }
}

/**
 * 设置token
 */
export function setToken(token: string): void {
  try {
    localStorage.setItem(TOKEN_KEY, token);
  } catch (error) {
    console.error("设置token失败:", error);
  }
}

/**
 * 移除token
 */
export function removeToken(): void {
  try {
    localStorage.removeItem(TOKEN_KEY);
  } catch (error) {
    console.error("移除token失败:", error);
  }
}

/**
 * 获取存储的用户信息
 */
export function getUserInfo(): any {
  try {
    const userInfo = localStorage.getItem(USER_INFO_KEY);
    console.log(userInfo)
    return userInfo ? JSON.parse(userInfo) : null;
  } catch (error) {
    console.error("获取用户信息失败2:", error);
    return null;
  }
}

/**
 * 设置用户信息
 */
export function setUserInfo(userInfo: any): void {
  try {
    console.log("设置用户信息",JSON.stringify(userInfo))
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
  } catch (error) {
    console.error("设置用户信息失败:", error);
  }
}

/**
 * 移除用户信息
 */
export function removeUserInfo(): void {
  try {
    localStorage.removeItem(USER_INFO_KEY);
  } catch (error) {
    console.error("移除用户信息失败:", error);
  }
}

/**
 * 清除所有认证相关的存储
 */
export function clearAuthStorage(): void {
  removeToken();
  removeUserInfo();
}
