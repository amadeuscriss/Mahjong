import { createApp } from 'vue'
import Test from './Test.vue'

// 创建 Vue 应用程序
const app = createApp(Test);

// 设置 WebSocket 连接
const ws = new WebSocket('ws://localhost:8080/ws');
ws.onopen = () => {
    console.log('WebSocket 连接已建立');
    // 在连接建立后，您可以执行任何相关操作，如发送初始消息等
    // 将 WebSocket 对象添加到 Vue 应用程序的全局属性中，以便在组件中访问
    app.config.globalProperties.$ws = ws;

    // 挂载 Vue 应用程序
    app.mount('#app');
};