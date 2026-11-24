import {
  createRouter,
  createWebHistory,
  type RouteRecordRaw,
} from "vue-router";
import { nextTick } from "vue";
import { setupPageGuard } from "./permission";
import { URL_LANGUAGE_MAP, LANGUAGE_URL_MAP } from "@/hooks/useLanguage";

// Import pages (will be created in next tasks)
const Home = () => import("../pages/Home.vue");
const Contact = () => import("../pages/Contact.vue");
const SignIn = () => import("../pages/SignIn.vue");
const SignUp = () => import("../pages/SignUp.vue");
const EmailLogin = () => import("../pages/EmailLogin.vue");
const ForgotPassword = () => import("../pages/ForgotPassword.vue");
const AuthTest = () => import("../pages/AuthTest.vue");
const CaptchaTest = () => import("../pages/CaptchaTest.vue");
const SsoLogin = () => import("../pages/SsoLogin.vue");

const Privacy = () => import("../pages/Privacy.vue");
const LoginModalTest = () => import("../pages/LoginModalTest.vue");
const NotFound = () => import("../pages/404.vue");

// 工作空间相关页面
const WorkspaceDetail = () => import("../pages/workspace/detail.vue");

// 文件管理相关页面
const FileManagement = () => import("../pages/file/index.vue");

// 编辑器相关页面
const EditorPage = () => import("../pages/editor/index.vue");

// 分享相关页面
const ShareManagement = () => import("../pages/share/index.vue");
const ShareAccess = () => import("../pages/share/access.vue");
const ShareEditor = () => import("../pages/share/editor.vue");

// 搜索相关页面
const SearchPage = () => import("../pages/search/index.vue");

// 回收站页面
const TrashPage = () => import("../pages/trash/index.vue");

// 语言前缀列表
const LANGUAGE_PREFIXES = Object.keys(URL_LANGUAGE_MAP);

// 生成带语言前缀的路由
const createLanguageRoutes = (
  baseRoutes: RouteRecordRaw[]
): RouteRecordRaw[] => {
  const languageRoutes: RouteRecordRaw[] = [];

  LANGUAGE_PREFIXES.forEach((prefix) => {
    baseRoutes.forEach((route) => {
      // 为每个语言前缀创建路由
      const languageRoute: RouteRecordRaw = {
        ...route,
        path: prefix + (route.path === "/" ? "" : route.path),
        name: `${prefix.slice(1)}-${String(route.name)}`,
        meta: {
          ...route.meta,
          language: URL_LANGUAGE_MAP[prefix],
        },
      };

      // 确保组件正确导入
      if (route.component) {
        languageRoute.component = route.component;
      }

      languageRoutes.push(languageRoute);
    });
  });

  return languageRoutes;
};

// 基础路由定义（不包含语言前缀）
const baseRoutes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "Home",
    component: Home,
    meta: { title: "协同编辑系统  - Arbitrage Trading" },
  },
  {
    path: "/home2",
    name: "Home2",
    component: Home, // Can be different component later
    meta: { title: "协同编辑系统  - Home Style v2" },
  },

  {
    path: "/contacts",
    name: "Contact",
    component: Contact,
    meta: { title: "Contact Us - 协同编辑系统 " },
  },
  {
    path: "/signin",
    name: "SignIn",
    component: SignIn,
    meta: { title: "Sign In - 协同编辑系统 " },
  },
  {
    path: "/signup",
    name: "SignUp",
    component: SignUp,
    meta: { title: "Sign Up - 协同编辑系统 " },
  },
  {
    path: "/email-login",
    name: "EmailLogin",
    component: EmailLogin,
    meta: { title: "Email Login - 协同编辑系统 " },
  },
  {
    path: "/forgot",
    name: "ForgotPassword",
    component: ForgotPassword,
    meta: { title: "Forgot Password - 协同编辑系统 " },
  },
  {
    path: "/sso-login",
    name: "SsoLogin",
    component: SsoLogin,
    meta: { title: "SSO 登录 - 协同编辑系统 " },
  },
  {
    path: "/auth-test",
    name: "AuthTest",
    component: AuthTest,
    meta: { title: "Auth Test - 协同编辑系统 " },
  },
  {
    path: "/captcha-test",
    name: "CaptchaTest",
    component: CaptchaTest,
    meta: { title: "Captcha Test - 协同编辑系统 " },
  },

  {
    path: "/privacy",
    name: "Privacy",
    component: Privacy,
    meta: { title: "Privacy Policy - 协同编辑系统 " },
  },

  {
    path: "/login-test",
    name: "LoginModalTest",
    component: LoginModalTest,
    meta: { title: "登录弹窗测试 - 协同编辑系统 " },
  },

  // 工作空间路由
  {
    path: "/workspace/:id",
    name: "WorkspaceDetail",
    component: WorkspaceDetail,
    meta: { title: "工作空间详情 - 协同编辑系统 ", requiresAuth: true },
  },

  // 文件管理路由
  {
    path: "/workspace/:workspaceId/files",
    name: "FileManagement",
    component: FileManagement,
    meta: { title: "文件管理 - 协同编辑系统 ", requiresAuth: true },
  },

  // 编辑器路由
  {
    path: "/editor/:fileId",
    name: "Editor",
    component: EditorPage,
    meta: { title: "文档编辑 - 协同编辑系统 ", requiresAuth: true },
  },

  // 分享路由
  {
    path: "/share",
    name: "ShareManagement",
    component: ShareManagement,
    meta: { title: "我的分享 - 协同编辑系统 ", requiresAuth: true },
  },
  {
    path: "/s/:code",
    name: "ShareAccess",
    component: ShareAccess,
    meta: { title: "访问分享 - 协同编辑系统 " },
  },
  // 分享编辑器路由 - 处理后端 /api/coedit/share/open/ 重定向
  {
    path: "/share/editor/:code",
    name: "ShareEditor",
    component: ShareEditor,
    meta: { title: "分享编辑器 - 协同编辑系统 " },
  },
  // 兼容后端生成的分享链接格式 /share/:code,重定向到 /s/:code
  {
    path: "/share/:code",
    redirect: (to) => {
      return `/s/${to.params.code}`
    },
  },

  // 搜索路由
  {
    path: "/search",
    name: "Search",
    component: SearchPage,
    meta: { title: "搜索结果 - 协同编辑系统 ", requiresAuth: true },
  },

  // 回收站路由
  {
    path: "/trash",
    name: "Trash",
    component: TrashPage,
    meta: { title: "回收站 - 协同编辑系统 ", requiresAuth: true },
  },

  // 文档转换路由
  {
    path: "/convert",
    name: "Convert",
    component: () => import("@/pages/convert/index.vue"),
    meta: { title: "文档转换 - 协同编辑系统 ", requiresAuth: true },
  },

  // 系统设置路由
  {
    path: "/settings",
    name: "Settings",
    component: () => import("@/pages/settings/index.vue"),
    redirect: "/settings/profile",
    meta: { title: "系统设置 - 协同编辑系统 ", requiresAuth: true },
    children: [
      {
        path: "profile",
        name: "SettingsProfile",
        component: () => import("@/pages/settings/profile.vue"),
        meta: { title: "个人中心 - 协同编辑系统 ", requiresAuth: true },
      },
      {
        path: "system",
        name: "SettingsSystem",
        component: () => import("@/pages/settings/system.vue"),
        meta: { title: "系统配置 - 协同编辑系统 ", requiresAuth: true },
      },
      {
        path: "logs",
        name: "SettingsLogs",
        component: () => import("@/pages/settings/logs.vue"),
        meta: { title: "操作日志 - 协同编辑系统 ", requiresAuth: true },
      },
    ],
  },

  {
    path: "/404",
    name: "NotFound",
    component: NotFound,
    meta: { title: "404 - Page Not Found" },
  },
];

// 生成所有语言路由
const languageRoutes = createLanguageRoutes(baseRoutes);

const routes: RouteRecordRaw[] = [
  // 根路由重定向到默认语言
  {
    path: "/",
    name: "Root",
    redirect: () => {
      // 检测浏览器语言或使用默认中文
      const browserLang = navigator.language || "zh-CN";
      let targetLang = "zh-CN";

      if (browserLang.startsWith("en")) targetLang = "en-US";

      return LANGUAGE_URL_MAP[targetLang] || "/cn";
    },
  },

  // 只使用带语言前缀的路由
  ...languageRoutes,

  // 404 捕获所有未匹配的路由
  {
    path: "/:pathMatch(.*)*",
    name: "NotFoundCatchAll",
    component: NotFound,
    meta: { title: "404 - Page Not Found" },
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, _from, savedPosition) {
    // 如果有保存的位置（浏览器前进/后退），则恢复到该位置
    if (savedPosition) {
      return savedPosition;
    }
    // 如果有锚点，则滚动到锚点
    if (to.hash) {
      return { el: to.hash, behavior: "smooth" };
    }
    // 默认滚动到顶部
    return { top: 0, behavior: "smooth" };
  },
});

// 语言检测和重定向 - 必须在权限守卫之前
router.beforeEach((to, _from, next) => {
  const pathSegments = to.path.split("/").filter(Boolean);

  // 检查是否有语言前缀
  if (pathSegments.length > 0) {
    const firstSegment = `/${pathSegments[0]}`;
    const detectedLanguage = URL_LANGUAGE_MAP[firstSegment];

    if (detectedLanguage) {
      // 如果检测到语言前缀，设置语言
      import("@/i18n").then(({ i18n, setStoredLocale }) => {
        if (i18n.global.locale.value !== detectedLanguage) {
          i18n.global.locale.value = detectedLanguage as any;
          setStoredLocale(detectedLanguage);
        }
      });
      next();
    } else {
      // 如果没有语言前缀，重定向到带语言前缀的路径
      const currentLanguage = localStorage.getItem("app-locale") || "zh-CN";
      const languagePrefix = LANGUAGE_URL_MAP[currentLanguage] || "/cn";

      // 特殊处理根路径
      if (to.path === "/") {
        next(languagePrefix);
      } else {
        // 为其他路径添加语言前缀
        next(languagePrefix + to.path);
      }
    }
  } else {
    // 根路径，重定向到默认语言
    const currentLanguage = localStorage.getItem("app-locale") || "zh-CN";
    const languagePrefix = LANGUAGE_URL_MAP[currentLanguage] || "/cn";
    next(languagePrefix);
  }
});

// 设置权限守卫 - 在语言检测之后
setupPageGuard(router);

router.afterEach((to) => {
  // 设置页面标题
  const appTitle = "协同编辑系统 ";
  if (to.meta?.title) {
    document.title = `${to.meta.title}`;
  } else {
    document.title = appTitle;
  }

  // 页面加载完成后的处理
  nextTick(() => {
    // 可以在这里添加页面分析、埋点等逻辑
  });
});

// 路由工具函数
export const routeUtils = {
  // 获取当前语言前缀
  getCurrentLanguagePrefix(): string {
    const route = router.currentRoute.value;
    const pathSegments = route.path.split("/").filter(Boolean);
    if (pathSegments.length > 0) {
      const firstSegment = `/${pathSegments[0]}`;
      if (URL_LANGUAGE_MAP[firstSegment]) {
        return firstSegment;
      }
    }
    return "/cn"; // 默认中文
  },

  // 获取不带语言前缀的路径
  getPathWithoutLanguage(): string {
    const route = router.currentRoute.value;
    const languagePrefix = this.getCurrentLanguagePrefix();
    return route.path.replace(languagePrefix, "") || "/";
  },

  // 切换到指定语言
  switchToLanguage(languageKey: string) {
    const targetPrefix = LANGUAGE_URL_MAP[languageKey];
    if (!targetPrefix) return;

    const currentPath = this.getPathWithoutLanguage();
    const newPath = targetPrefix + (currentPath === "/" ? "" : currentPath);

    return router.push(newPath);
  },

  // 获取带当前语言前缀的路径
  getLocalizedPath(path: string): string {
    const languagePrefix = this.getCurrentLanguagePrefix();

    // 如果路径已经有语言前缀，直接返回
    const pathSegments = path.split("/").filter(Boolean);
    if (pathSegments.length > 0) {
      const firstSegment = `/${pathSegments[0]}`;
      if (URL_LANGUAGE_MAP[firstSegment]) {
        return path;
      }
    }

    // 如果是根路径，返回语言前缀
    if (path === "/") {
      return languagePrefix;
    }

    // 为路径添加当前语言前缀
    return languagePrefix + path;
  },
};
