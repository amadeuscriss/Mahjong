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
               @newGame="handleNewGame"
    />
  </div>
</template>


<script>
import InputRoomNumber from './components/InputRoomNumber.vue'
import WaitingRoom from './components/WaitingRoom.vue'
import WelcomePage from "@/components/WelcomePage.vue";
import GameTable from "@/components/GameTable.vue";
import GameResults from "@/components/GameResults.vue";



export default {
  name: 'App',
  components: {
    WelcomePage,
    InputRoomNumber,
    WaitingRoom,
    GameTable,
    GameResults
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
    },
    handleNewGame(){
      window.location.reload();
    }
  },
  created() {
    // Access the global property $ws directly from the created hook
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
            this.setErrorMessage('房间不存在');
          }
        } else if (data.type === 'gameStart'){
          //开始游戏
          this.currentComponent = 'GameTable';
        } else if (data.type === 'gameEnd'){
          //游戏结束
          this.ScoresList = data.ScoresList;
          this.currentComponent = 'GameResults';
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
  background-image: url('./assets/desk.png');
  background-size: cover;
  background-attachment: fixed;
  background-position: center;
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