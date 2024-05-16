<template>
  <el-button @click="sendDataToServer">给后台发送消息</el-button>
</template>

<script>
export default {
  name: "WebSocketComponent",
  data() {
    return {
      wsIsRun: false,
      webSocket: null,
      ws: 'ws://localhost:8080/ws',  // 修改为本地地址和端口
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

    //处理服务器消息
    wsMessageHandler(event) {
      console.log('接收到服务器消息:', event.data);
      try {
        const data = JSON.parse(event.data);

        if (data.type === 'test'){
          console.log('收到来自服务器的消息：', data.message);
        }
      }catch (error) {
        console.error('解析消息时出错:', error);
      }
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