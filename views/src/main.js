import { createApp } from 'vue'
import Test from './Test.vue'
// 创建 Vue 应用程序
const app = createApp(Test);

// 设置 WebSocket 连接
const ws = new WebSocket('ws://localhost:8081/ws');
ws.onopen = () => {
    console.log('WebSocket 连接已建立');
    // 在连接建立后，您可以执行任何相关操作，如发送初始消息等
    // 将 WebSocket 对象添加到 Vue 应用程序的全局属性中，以便在组件中访问
    app.config.globalProperties.$ws = ws;

    // 处理 WebSocket 消息
    ws.onmessage = (event) => {
        console.log('收到 WebSocket 消息:', event.data);
        try {
            const message = JSON.parse(event.data);
            console.log('解析后的消息:', message);
        } catch (error) {
            console.error('解析消息时出错:', error);
        }
    };

    // 处理 WebSocket 错误
    ws.onerror = (error) => {
        console.error('WebSocket 发生错误:', error);
    };

    // 处理 WebSocket 连接关闭
    ws.onclose = (event) => {
        console.log('WebSocket 连接已关闭:', event);
    };

    // 挂载 Vue 应用程序
    app.mount('#app');
};