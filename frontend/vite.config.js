import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { loadEnv } from 'vite';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  return {
    plugins: [react()],
    server: {
      host: '127.0.0.1',
      port: 5173,
      proxy: {
        '/api': {
          target: env.VITE_BACKEND_PROXY_TARGET || 'http://127.0.0.1:8081',
          changeOrigin: true,
        },
      },
    },
  };
});
