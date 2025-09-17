import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'


export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', 
    port: 5173,
   watch: {
      usePolling: true // Para hot reload en Docker (especialmente Windows)
    }
  },
  build: {
    outDir: 'dist', // Vite usa 'dist' por defecto
    sourcemap: false, // Desactivar en producción
    minify: 'esbuild',
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom']
        }
      }
    }
  },
  preview: {
    host: '0.0.0.0',
    port: 4173
  }
})