<template>
  <div id="app">
    <component :is="currentComponent"
               @joinRoom="handleJoinRoom"
               @createRoom="handleCreateRoom"
               @roomEntered="handleRoomEntered"
               :players="players"
               :playerIndex="playerIndex"
               :roomId="roomId"
               ref="inputRoomNumberComponent"
               @errorMessage="setErrorMessage"
               @goBack="handleGoback"
    />
  </div>
</template>


<script>
import InputRoomNumber from './components/InputRoomNumber.vue'
import WaitingRoom from './components/WaitingRoom.vue'
import WelcomePage from "@/components/WelcomePage.vue";
import GameTable from "@/components/GameTable.vue";

export default {
  name: 'App',
  components: {
    WelcomePage,
    InputRoomNumber,
    WaitingRoom,
    GameTable,
  },
  data() {
    return {
      currentComponent:  WelcomePage,
      players: [],
      playerIndex: null,
      roomId: null,
      ScoresList: null,
    };
  },
  methods: {
    handleJoinRoom() {
      this.currentComponent = 'InputRoomNumber';
    },
    handleCreateRoom() {
      // 发送新建房间的请求到后端
      this.$ws.send(JSON.stringify({ type: 'createRoom',state: 'Waiting' }));
    },
    handleRoomEntered(roomId) {
      if(this.$ws){
        this.$ws.send(JSON.stringify({ type: 'joinRoom', roomId: roomId, state:'Waiting' }));
      }
    },
    setErrorMessage(message) {
      if (this.currentComponent === 'InputRoomNumber') {
        const inputRoomNumberComponent = this.$refs.inputRoomNumberComponent;
        inputRoomNumberComponent.setErrorMessage(message);
      }
    },
    handleGoback(){
      this.currentComponent = WelcomePage
    }
  },
  created() {
    // 直接在 created 钩子中访问全局属性 $ws
    if (this.$ws) {
      this.$ws.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log('Received message:', data)
        if (data.type === 'roomCreated') {
          this.roomId = data.roomId;
          this.players = data.players;
          this.playerIndex = 0;
          this.currentComponent = 'WaitingRoom';
        }
        else if (data.type === 'updateRoom'){
          this.players = data.players;
          this.playerIndex = data.playerIndex;
        }
        else if (data.type === 'joinRoomResponse') {
          if (data.state === 'roomJoined') {
            this.players = data.players;
            this.playerIndex = data.playerIndex;
            this.roomId = data.roomId;
            this.currentComponent = 'WaitingRoom';
          } else {
            // 显示错误信息，房间不存在
            this.setErrorMessage('房间不存在');
          }
        } else if (data.type === 'gameStart'){
          //开始游戏
          this.currentComponent = 'GameTable';
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