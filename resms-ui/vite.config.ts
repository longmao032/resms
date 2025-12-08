import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      // 代理所有后端 API 请求到后端服务。
      // 注意：不要代理前端页面路由（例如 /system/...），否则直接在地址栏访问这些路径会被转发到后端。
      // 因此移除了 `system`，避免与前端路由冲突。
      '^/(auth|user|house|customer|transaction|commission|team|menu|permission|role)': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },

  },
})
