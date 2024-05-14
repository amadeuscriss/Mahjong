import './plugins/axios'
import { createApp } from 'vue'
import App from './App.vue'
import axios from "./plugins/axios";

createApp(App).mount('#app')
import websocket from 'vue-native-websocket';

Vue.use(websocket, '', {
    connectManually: true, // 手动连接
    format: 'json', // json格式
    reconnection: true, // 是否自动重连
    reconnectionAttempts: 5, // 自动重连次数
    reconnectionDelay: 2000, // 重连间隔时间
});
