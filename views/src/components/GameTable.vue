<template>
  <div class="game-table">
    <!-- 显示房间号 -->
    <div class="room-id">
      房间号: {{ roomId }}
    </div>

    <!-- 玩家手牌展示区 -->
    <div class="tiles">
      <div v-for="(tile, index) in playerTiles" :key="index" class="tile-container">
        <img src="@/assets/tiles_back/playerTiles.png" class="player-tiles-back" alt="tile back"/>
        <img :src="getTileImage(tile)" @click="handleTileClick(tile)" class="tile-front" alt="tile front"/>
      </div>
    </div>

    <!-- 展示明牌区 -->
    <div class="shown-tiles">
      <!-- 下方玩家 -->
      <div class="shown-tiles-bottom">
        <div v-for="(tile, index) in showTiles[playerIndex]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- 右侧玩家 -->
      <div class="shown-tiles-right">
        <div v-for="(tile, index) in showTiles[(playerIndex + 1) % 4]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- 上方玩家 -->
      <div class="shown-tiles-top">
        <div v-for="(tile, index) in showTiles[(playerIndex + 2) % 4]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- 左侧玩家 -->
      <div class="shown-tiles-left">
        <div v-for="(tile, index) in showTiles[(playerIndex + 3) % 4]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

    </div>

    <!-- 桌面上打出去的牌展示区 -->
    <div class="table-tiles">
      <div v-for="(tile, index) in tableTiles" :key="index" class="table-tile-container">
        <img :src="getTileImage(tile)" class="table-tile" alt="tile"/>
        <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-buttons">
      <button v-for="(action, index) in filteredActions" :key="index" @click="handleAction(action)">
        {{ action }}
      </button>
    </div>

    <!-- 玩家行为展示区 -->
    <div v-if="notification.show" class="notification" :class="notification.position">
      {{ notification.action }}
    </div>

  </div>
</template>

<script>
export default {
  name: 'GameTable',
  props: {
    roomId: String,
    players: Array,
    playerIndex: null, // 玩家索引
  },
  data() {
    return {
      playAction: [], // 玩家操作
      currentTurnPlayerName: null, // 当前回合玩家id

      tableTiles: [],
      playerTiles: [], // 玩家手牌

      showTiles: [[], [], [], []], // 玩家的明牌

      notification: {
        show: false,
        action: '',
        position: '',
      },
    };
  },
  computed: {
    filteredActions() {
      const actions = this.playAction.filter(action => action !== 'Discard');
      if (actions.length > 0) {
        actions.push('Skip'); // 添加“跳过”按钮
      }
      return actions;
    }
  },
  methods: {
    gameInitialization(message){

      this.playerTiles = message.playerTiles[this.playerIndex];
      this.currentTurnPlayerName = message.currentTurnPlayerName;
      if(this.currentTurnPlayerName === this.playerIndex){
        this.$ws.send(JSON.stringify({ type: 'startGame',state: this.currentTurnPlayerName , roomId: this.roomId}));
      }

    },
    //更新当前回合玩家，更新桌面
    updateGame(message) {
      this.currentTurnPlayerName = message.currentTurnPlayerId;
      this.tableTiles = message.tableTiles;
    },
    //获取玩家行为
    handlePlayerActions(message) {
      this.playAction = message.playAction;
      this.playerTiles = message.playerTiles[this.playerIndex];
    },
    //在执行操作后更新手牌
    updateAfterActing(message){
      this.playerTiles = message.playerTiles[this.playerIndex];
    },
    // 显示玩家行为通知
    showNotification(action, performerIndex) {
      const positions = ['bottom', 'right', 'top', 'left'];
      const position = positions[(performerIndex - this.playerIndex + 4) % 4];

      this.notification = {
        show: true,
        action,
        position,
      };

      setTimeout(() => {
        this.notification.show = false;
      }, 2000);
    },
    //接收通知，更新明牌库
    updateShownTiles(message){
      this.showTiles[message.performerIndex] = message.showTiles;
      this.showNotification(message.action, message.performerIndex);
    },
    // 动态获取图片路径
    getTileImage(tile) {
      try {
        return require(`@/assets/tiles_front/${tile}.png`);
      } catch (e) {
        return '';
      }
    },
    // 处理牌面的点击事件
    handleTileClick(tile) {
      if (this.players[this.playerIndex]=== this.currentTurnPlayerName){
        const tileIndex = this.playerTiles.indexOf(tile);
        const message = JSON.stringify({ type: 'action', behavior: 'Discard', state: 'Playing' , data: tileIndex , roomId: this.roomId , playIndex: this.playerIndex});
        this.$ws.send(message);
      }
    },
    // 处理操作按钮的点击事件
    handleAction(action) {
      console.log('Action clicked:', action); // 调试信息
      const message = JSON.stringify({ type: 'action', behavior: action, state: 'Playing' , roomId: this.roomId , playIndex: this.playerIndex});
      this.$ws.send(message);
    },

    handleMessage(event) {
      const message = JSON.parse(event.data);
      console.log("Gamestart");
      switch (message.type) {
        case 'updateGame':
          console.log("updateGame")
          this.updateGame(message);
          break;
        case 'playerActions':
          this.handlePlayerActions(message);
          break;
        case 'done':
          this.updateAfterActing(message);
          break;
        case 'notification':
          this.updateShownTiles(message);
          break;
        case 'gameInitialization':
          console.log("gameInitialization")
          this.gameInitialization(message);
          break;
      }
    }
  },
  mounted() {
    // 使用全局 WebSocket 连接
    this.$ws.onmessage = this.handleMessage;
  }
}
</script>

<style scoped>
.game-table {
  display: flex;
  flex-direction: column; /* 将子元素垂直排列 */
  justify-content: center;
  align-items: center;
  height: 80vh;
}

.room-id {
  position: fixed;
  top: 10px; /* 距离顶部 10px */
  left: 50%;
  transform: translateX(-50%);
  font-size: 20px; /* 根据需要调整大小 */
  font-weight: bold; /* 字体加粗 */
  color: white; /* 白色字体 */
}

.tiles {
  position: fixed;
  bottom: 80px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
}

.tile-container {
  position: relative;
  margin: 0 30px;
}

.tile-front {
  width: 45px; /* 根据需要调整大小 */
  height: 75px; /* 根据需要调整大小 */

  position: absolute;
  top: 8px;
  left: 0;
  z-index: 2;
  cursor: pointer;
}


.player-tiles-back{
  width: 70px; /* 根据需要调整大小 */
  height: 100px; /* 根据需要调整大小 */

  position: absolute;
  top: -15px;
  left: -10px;
  z-index: 1;
}

.table-tiles {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: grid;
  grid-template-columns: repeat(15, 1fr);
  grid-auto-rows: auto;
  gap: 5px;
}

.table-tile-container {
  position: relative;
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.table-tile {
  width: 25px; /* 根据需要调整大小 */
  height: 40px; /* 根据需要调整大小 */
  z-index: 2;
}


.shown-tiles {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none; /* 禁用指针事件 */
}

.shown-tiles-bottom {
  position: absolute;
  bottom: 90px;
  display: flex;
}

.shown-tiles-right {
  position: absolute;
  right: 175px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
}

.shown-tiles-top {
  position: absolute;
  top: 125px;
  display: flex;
}

.shown-tiles-left {
  position: absolute;
  left: 175px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
}

.shown-tile-container {
  position: relative;
  margin: 0; /* 根据需要调整间距 */
  display: flex;
}

.shown-tile {
  width: 25px;
  height: 40px;
  z-index: 2;
}

.shown-tiles-back{
  width: 25px; /* 根据需要调整大小 */
  height: 45px; /* 根据需要调整大小 */

  position: absolute;
  top: 4px;
  left: 0;
  z-index: 1;
}

.action-buttons {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-buttons button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}


/* 玩家行为展示区样式 */
.notification {
  position: fixed;
  font-size: 24px;
  font-weight: bold;
  color: red;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 10px 20px;
  border-radius: 5px;
}

.notification.bottom {
  bottom: 150px;
  left: 50%;
  transform: translateX(-50%);
}

.notification.right {
  right: 200px;
  top: 50%;
  transform: translateY(-50%);
}

.notification.top {
  top: 150px;
  left: 50%;
  transform: translateX(-50%);
}

.notification.left {
  left: 200px;
  top: 50%;
  transform: translateY(-50%);
}

</style>