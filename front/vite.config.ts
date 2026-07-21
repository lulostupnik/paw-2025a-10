import { fileURLToPath, URL } from "node:url";
import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  return {
    base: env.VITE_BASE_URL ?? "/",
    plugins: [react()],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url)),
        "@app": fileURLToPath(new URL("./src/app", import.meta.url)),
        "@components": fileURLToPath(new URL("./src/components", import.meta.url)),
        "@hooks": fileURLToPath(new URL("./src/hooks", import.meta.url)),
        "@lib": fileURLToPath(new URL("./src/lib", import.meta.url)),
        "@pages": fileURLToPath(new URL("./src/pages", import.meta.url)),
      },
    },
    server: {
      proxy: {
        "/app": {
          target: "http://localhost:8080",
          changeOrigin: true,
          configure: (proxy) => {
            proxy.on("proxyReq", (proxyReq) => {
              proxyReq.setHeader("origin", "http://localhost:8080");
            });
          },
        },
        "/webapp_war_exploded": {
          target: "http://localhost:8080",
          changeOrigin: true,
          configure: (proxy) => {
            proxy.on("proxyReq", (proxyReq) => {
              proxyReq.setHeader("origin", "http://localhost:8080");
            });
          },
        },
      },
    },
    test: {
      globals: true,
      environment: "jsdom",
      environmentOptions: { jsdom: { url: "http://localhost/" } },
      setupFiles: "./src/__test__/setup/setup.ts",
      testTimeout: 15000,
      hookTimeout: 15000,
      css: true,
    },
  }
});
