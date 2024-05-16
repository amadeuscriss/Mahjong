<template>
  <div id="app">
    <component :is="currentComponent"
               @joinRoom="handleJoinRoom"
               @createRoom="handleCreateRoom"
               @roomEntered="handleRoomEntered"
               :players="players"
               :playerIndex="playerIndex"
               :roomId="roomId"
               @errorMessage="setErrorMessage"/>
  </div>
</template>


<script>
import InputRoomNumber from './components/InputRoomNumber.vue'
import WaitingRoom from './components/WaitingRoom.vue'
import WelcomePage from "@/components/WelcomePage.vue";


export default {
  name: 'App',
  components: {
    WelcomePage,
    InputRoomNumber,
    WaitingRoom
  },
  data() {
    return {
      currentComponent: 'WelcomePage',
      players: [],
      playerIndex: null,
      roomId: null,
    };
  },
  methods: {
    handleJoinRoom() {
      this.currentComponent = 'InputRoomNumber';
    },
    handleCreateRoom() {
      // 发送新建房间的请求到后端
      this.$ws.send(JSON.stringify({ type: 'createRoom' }));
    },
    handleRoomEntered(roomId) {
      if(this.$ws){
        this.$ws.send(JSON.stringify({ type: 'joinRoom', roomId }));
      }
    },
    setErrorMessage(message) {
      if (this.currentComponent === 'InputRoomNumber') {
        this.$children[0].setErrorMessage(message);
      }
    }
  },
    created() {
      // 直接在 created 钩子中访问全局属性 $ws
      if (this.$ws) {
        this.$ws.onmessage = (event) => {
          const data = JSON.parse(event.data);
          if (data.type === 'roomCreated') {
            this.roomId = data.roomId;
            this.players = [data.player]; // 新建房间，只有当前玩家
            this.playerIndex = 0;
            this.currentComponent = 'WaitingRoom';
          } else if (data.type === 'roomJoined') {
            if (data.success) {
              this.players = data.players;
              this.playerIndex = data.playerIndex;
              this.roomId = data.roomId;
              this.currentComponent = 'WaitingRoom';
            } else {
              // 显示错误信息，房间不存在
              alert('房间不存在');
            }
          }
        };

        this.$ws.onclose = () => {
          console.log('WebSocket connection closed');
        };

        this.$ws.onerror = (error) => {
          console.error('WebSocket error:', error);
        };
      }

  },
}
</script>

<style>
body {
  /* 使用 url() 函数设置背景图片 */
  background-image: url('./assets/desk.png');
  /* 设置其他背景属性 */
  background-size: cover; /* 覆盖整个屏幕 */
  background-attachment: fixed; /* 滚动时固定背景 */
  background-position: center; /* 将背景图片居中 */
  /* 根据需要添加其他样式 */
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

</style>