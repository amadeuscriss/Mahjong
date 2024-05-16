<template>
  <div>
    <el-button @click="sendDataToServer">给后台发送消息</el-button>
  </div>
</template>

<script>
export default {
  name: "WebSocketComponent",
  data() {
    return {
      wsIsRun: false,
      webSocket: null,
      ws: 'ws://localhost:8080/ws',
      wsTimer: null,
    }
  },
  mounted() {
    this.wsIsRun = true;
    this.wsInit();
  },
  methods: {
    sendDataToServer() {
      if (this.webSocket.readyState === WebSocket.OPEN) {
        this.webSocket.send('来自前端的数据');
      } else {
        console.error('服务未连接');
      }
    },
    wsInit() {
      if (!this.wsIsRun) return;
      this.wsDestroy();
      this.webSocket = new WebSocket(this.ws);
      this.webSocket.onopen = this.wsOpenHandler;
      this.webSocket.onmessage = this.wsMessageHandler;
      this.webSocket.onerror = this.wsErrorHandler;
      this.webSocket.onclose = this.wsCloseHandler;
      clearInterval(this.wsTimer);
      this.wsTimer = setInterval(() => {
        if (this.webSocket.readyState === WebSocket.OPEN) {
          clearInterval(this.wsTimer);
        } else {
          console.log('尝试重新建立WebSocket连接...');
          this.wsInit();
        }
      }, 3000);
    },
    wsOpenHandler(event) {
      console.log('WebSocket连接成功', event);
    },
    wsMessageHandler(event) {
      console.log('接收到服务器消息:', event.data);
    },
    wsErrorHandler(event) {
      console.error('WebSocket错误', event);
    },
    wsCloseHandler(event) {
      console.log('WebSocket连接关闭', event);
      this.wsInit();
    },
    wsDestroy() {
      if (this.webSocket) {
        this.webSocket.onopen = null;
        this.webSocket.onmessage = null;
        this.webSocket.onerror = null;
        this.webSocket.onclose = null;
        this.webSocket.close();
        this.webSocket = null;
        clearInterval(this.wsTimer);
      }
    },
  },
}
</script>

<style scoped>
</style>
