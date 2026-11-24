import axios from "axios";
import type { ApiResponse } from "@/types/auth";

// 401 错误处理防抖标志
let isHandling401 = false;

// 简单的消息提示函数，替代 Element Plus 的 ElMessage
function showMessage(message: string, type: "success" | "error" = "success") {
  console.log(`[${type.toUpperCase()}] ${message}`);

  // 创建简单的消息提示
  const messageDiv = document.createElement("div");
  messageDiv.className = `toast toast--${type}`;
  messageDiv.textContent = message;
  messageDiv.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 12px 20px;
    border-radius: 6px;
    color: white;
    font-weight: 500;
    z-index: 9999;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    font-size: 14px;
    ${type === "success" ? "background: #52c41a;" : "background: #ff4d4f;"}
  `;

  document.body.appendChild(messageDiv);

  // 错误消息显示更长时间(5秒),成功消息3秒
  const duration = type === "error" ? 5000 : 3000;

  setTimeout(() => {
    if (document.body.contains(messageDiv)) {
      document.body.removeChild(messageDiv);
    }
  }, duration);
}

// 处理 401 未授权错误
function handle401Error(message: string = "登录已过期,请重新登录") {
  // 防止重复处理
  if (isHandling401) {
    console.warn("⚠️ 401 错误正在处理中,跳过重复处理");
    return;
  }

  isHandling401 = true;

  console.error("🚫 401 认证失败:", {
    message,
    currentToken: localStorage.getItem("ACCESS_TOKEN")?.substring(0, 20) + "...",
    currentUrl: window.location.href,
    timestamp: new Date().toISOString()
  });

  // 清除本地存储
  localStorage.removeItem("ACCESS_TOKEN");
  localStorage.removeItem("USER_INFO");

  // 显示错误消息(延长显示时间)
  showMessage(message, "error");

  // 延迟跳转,让用户有足够时间看到错误信息
  console.log("⏱️ 3秒后将跳转到登录页...");
  setTimeout(() => {
    console.log("🔄 正在跳转到登录页...");
    window.location.href = "/signin";
    // 重置标志
    setTimeout(() => {
      isHandling401 = false;
    }, 1000);
  }, 3000); // 从 500ms 改为 3000ms
}

// 创建axios实例
const service: any = (axios as any).create({
  baseURL: import.meta.env.VITE_GLOB_API_URL || "/api",
  timeout: 15000,
  headers: {
    "Content-Type": "application/json;charset=utf-8",
  },
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem("ACCESS_TOKEN");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }

    // 记录请求信息（开发环境）
    if (import.meta.env.DEV) {
      console.log(`[请求] ${config.method?.toUpperCase()} ${config.url}`, {
        params: config.params,
        data: config.data,
        hasToken: !!token,
        tokenPreview: token ? `${token.substring(0, 10)}...` : 'null',
        authHeader: config.headers["Authorization"] ? `${config.headers["Authorization"].substring(0, 20)}...` : 'null'
      });
    }

    return config;
  },
  (error) => {
    console.error("请求错误:", error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response: any): any => {
    const { data, config, headers } = response;

    // 如果是下载文件或其他特殊类型
    if (response.config.responseType === "blob") {
      return response;
    }

    // 检查是否返回了 HTML 页面(通常是认证失败重定向到登录页)
    if (typeof data === 'string' && (data.trim().startsWith('<!DOCTYPE') || data.trim().startsWith('<html'))) {
      console.error("❌ 服务器返回了 HTML 页面,可能是认证失败:", {
        url: config.url,
        method: config.method,
        status: response.status,
        contentType: headers['content-type'],
        dataPreview: data.substring(0, 200),
        currentToken: localStorage.getItem("ACCESS_TOKEN")?.substring(0, 20) + "..."
      });

      const errorMessage = `认证失败: 服务器返回了 HTML 页面而不是 JSON 数据。
这通常表示:
1. Token 无效或已过期
2. 后端认证配置有问题
3. API 路径不正确

请检查控制台日志获取详细信息。`;

      handle401Error(errorMessage);
      return Promise.reject(new Error("认证失败,服务器返回了登录页面"));
    }

    // 记录响应信息（开发环境）
    if (import.meta.env.DEV) {
      console.log(`[响应] ${config.method?.toUpperCase()} ${config.url}`, {
        status: response.status,
        contentType: headers['content-type'],
        dataType: typeof data,
        code: data?.code,
        msg: data?.msg,
        hasData: !!data?.data
      });
    }

    // 检查响应数据是否存在
    if (!data) {
      console.error("响应数据为空:", response);
      showMessage("服务器响应异常", "error");
      return Promise.reject(new Error("服务器响应数据为空"));
    }

    // 检查数据类型是否正确(应该是对象)
    if (typeof data !== 'object' || Array.isArray(data)) {
      console.error("❌ 响应数据格式错误:", {
        url: config.url,
        dataType: typeof data,
        isArray: Array.isArray(data),
        data: data
      });
      showMessage("服务器响应格式错误", "error");
      return Promise.reject(new Error("服务器响应格式错误"));
    }

    // 检查业务状态码
    if (data.code === 200) {
      return data;
    } else if (data.code === 401 || data.code === 4001) {
      // token过期或未授权
      handle401Error(data.msg || "登录已过期，请重新登录");
      return Promise.reject(new Error(data.msg || "登录已过期"));
    } else {
      // 其他业务错误
      const errorMsg = data.msg || "请求失败";
      console.error(`[业务错误] ${config.url}:`, {
        code: data.code,
        msg: errorMsg,
        data: data
      });
      showMessage(errorMsg, "error");
      return Promise.reject(new Error(errorMsg));
    }
  },
  (error) => {
    // 记录完整的错误信息
    console.error("响应错误:", {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    });

    let message = "网络错误";

    if (error.response) {
      const { status, data } = error.response;

      // 处理 HTTP 401 错误
      if (status === 401) {
        handle401Error(data?.msg || "登录已过期，请重新登录");
        return Promise.reject(new Error("登录已过期"));
      }

      switch (status) {
        case 400:
          message = data?.msg || "请求参数错误";
          break;
        case 403:
          message = data?.msg || "权限不足，访问被拒绝";
          break;
        case 404:
          message = data?.msg || "请求的资源不存在";
          break;
        case 500:
          message = data?.msg || "服务器内部错误";
          break;
        case 502:
          message = "网关错误";
          break;
        case 503:
          message = "服务暂时不可用";
          break;
        case 504:
          message = "网关超时";
          break;
        default:
          message = data?.msg || `请求失败 (${status})`;
      }
    } else if (error.code === "ECONNABORTED") {
      message = "请求超时，请稍后重试";
    } else if (error.message.includes("Network Error")) {
      message = "网络连接异常，请检查网络";
    } else if (error.message.includes("timeout")) {
      message = "请求超时，请稍后重试";
    }

    showMessage(message, "error");
    return Promise.reject(error);
  }
);

// 导出请求方法
export function request<T = any>(
  config: any
): Promise<ApiResponse<T>> {
  return service(config);
}

// 定义请求选项接口
export interface HttpOption {
  url: string;
  data?: any;
  params?: any;
  method?: string;
  headers?: any;
}

// GET请求
export function get<T = any>(options: HttpOption): Promise<ApiResponse<T>> {
  console.log(options)
  return service.get(options.url, {
    params: options.data,
    headers: options.headers,
  });
}

// POST请求
export function post<T = any>(options: HttpOption): Promise<ApiResponse<T>> {
  return service.post(options.url, options.data, {
    headers: options.headers,
  });
}

// PUT请求
export function put<T = any>(options: HttpOption | string, data?: any, headers?: any): Promise<ApiResponse<T>> {
  if (typeof options === 'string') {
    // 兼容旧的调用方式
    return service.put(options, data, { headers });
  }
  if (options.params) {
    return service.put(options.url, options.data, {
      params: options.params,
      headers: options.headers,
    });
  }
  return service.put(options.url, options.data, {
    headers: options.headers,
  });
}

// DELETE请求
export function del<T = any>(options: HttpOption | string): Promise<ApiResponse<T>> {
  if (typeof options === 'string') {
    // 兼容旧的调用方式
    return service.delete(options);
  }
  if (options.params) {
    return service.delete(options.url, {
      params: options.params,
      headers: options.headers,
    });
  }
  return service.delete(options.url, {
    data: options.data,
    headers: options.headers,
  });
}

// 文件上传
export function upload<T = any>(
  url: string,
  formData: FormData
): Promise<ApiResponse<T>> {
  return service.post(url, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

// 文件下载
export function download(url: string, params?: any): Promise<any> {
  return service.get(url, {
    params,
    responseType: "blob",
  });
}

export default service;
