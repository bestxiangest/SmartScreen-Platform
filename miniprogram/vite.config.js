import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni()],
  define: {
    __VUE_OPTIONS_API__: true,
    __VUE_PROD_DEVTOOLS__: false
  },
  server: {
    port: 3000,
    host: '0.0.0.0',
    open: false
  },
  build: {
    target: 'es6',
    cssTarget: 'chrome61'
  }
})