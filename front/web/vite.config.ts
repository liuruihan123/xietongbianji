import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import type { UserConfig, ConfigEnv } from 'vite'

// 自定义插件：动态设置 HTML 标题
function htmlTitlePlugin(env: ImportMetaEnv) {
  return {
    name: 'html-title',
    transformIndexHtml(html: string) {
      const title = env.VITE_APP_TITLE || 'DocLogic'
      return html.replace(
        /<title>(.*?)<\/title>/,
        `<title>${title}</title>`
      )
    }
  }
}

// https://vite.dev/config/
export default defineConfig((configEnv: ConfigEnv): UserConfig => {
  const { mode } = configEnv
  const env = loadEnv(mode, process.cwd()) as unknown as ImportMetaEnv

  return {
    plugins: [vue(), htmlTitlePlugin(env)],

    resolve: {
      alias: {
        '@': resolve(__dirname, 'src'),
        '@components': resolve(__dirname, 'src/components'),
        '@pages': resolve(__dirname, 'src/pages'),
        '@stores': resolve(__dirname, 'src/stores'),
        '@hooks': resolve(__dirname, 'src/hooks'),
        '@utils': resolve(__dirname, 'src/utils'),
        '@styles': resolve(__dirname, 'src/styles'),
        '@assets': resolve(__dirname, 'src/assets'),
        '@types': resolve(__dirname, 'src/types'),
        '@api': resolve(__dirname, 'src/api'),
      },
    },

    server: {
      host: env.VITE_DEV_SERVER_HOST || '0.0.0.0',
      port: Number(env.VITE_DEV_SERVER_PORT) || 5173,
      open: true,
      cors: true,
      allowedHosts:['p4c896da.natappfree.cc'],
      // 代理配置
      proxy: {
        // API 代理
        [env.VITE_GLOB_API_URL]: {
          target: env.VITE_APP_API_BASE_URL,
          changeOrigin: true,
          ws: true, // 支持 WebSocket
          rewrite: (path) => path.replace(new RegExp(`^${env.VITE_GLOB_API_URL}`), ''),
        },
        // 上传文件代理
        [env.VITE_UPLOAD_URL || '/startTask']: {
          target: env.VITE_APP_API_BASE_URL,
          changeOrigin: true,
        },
      },
    },

    build: {
      target: 'es2015',
      cssTarget: 'chrome83',
      outDir: 'dist',
      assetsDir: 'assets',
      sourcemap: mode === 'development',
      // 生产环境移除 console
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: env.VITE_BUILD_DROP_CONSOLE === 'true',
          drop_debugger: true,
        },
      },
      // 分包策略
      rollupOptions: {
        output: {
          chunkFileNames: 'assets/js/[name]-[hash].js',
          entryFileNames: 'assets/js/[name]-[hash].js',
          assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
          manualChunks: {
            vue: ['vue', 'vue-router', 'pinia'],
            antd: ['ant-design-vue', '@ant-design/icons-vue'],
            utils: ['axios', 'mitt'],
          },
        },
      },
    },

    // 环境变量
    define: {
      __APP_INFO__: JSON.stringify({
        pkg: {
          name: 'DocLogic',
          version: env.VITE_APP_VERSION || '1.0.0',
        },
        lastBuildTime: new Date().toISOString(),
      }),
    },
  }
})
