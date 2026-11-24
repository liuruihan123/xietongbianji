import { createApp } from "vue";
import type { App as VueApp } from "vue";
import App from "./App.vue";
import { router } from "./router";
import { setupStore } from "./stores";
import { i18n } from "./i18n";

// 引入分享重定向处理
import { initShareRedirect } from "./utils/shareRedirect";

// 样式导入
import "ant-design-vue/dist/antd.css";
import "./styles/antd-theme.scss"; // Ant Design Vue 主题覆盖
import "./styles/index.scss";
import 'vue-sonner/style.css'; // Vue Sonner 样式
import "./styles/toast.scss"; // 自定义 Toast 样式

// Ant Design Vue
import Antd from "ant-design-vue";

// 初始化分享重定向处理
initShareRedirect();

// 创建应用实例
const app: VueApp = createApp(App);

// 注册插件
app.use(router);
app.use(Antd);
app.use(i18n);

// 设置状态管理
setupStore(app);

// 挂载应用
app.mount("#app");
