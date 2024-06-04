<template>
  <div class="game-table">
    <!-- 显示房间号 -->
    <div class="room-id">
      房间号: {{ roomId }}
    </div>

<!--    &lt;!&ndash; 玩家手牌展示区 &ndash;&gt;-->
<!--    <div class="tiles">-->
<!--      <div v-for="(tile, index) in playerTiles" :key="index" class="tile-container">-->
<!--        <img src="@/assets/tiles_back/playerTiles.png" class="player-tiles-back" alt="tile back"/>-->
<!--        <img :src="getTileImage(tile)" @click="handleTileClick(tile)" class="tile-front" alt="tile front"/>-->
<!--        <img :src="getTileImage(tile)" @click="handleTileClick(tile)" class="tile-front" :class="{ 'drawn-tile': index === drawnTileIndex }" alt="tile front"/>-->
<!--      </div>-->
<!--    </div>-->

    <div class="tiles">
      <div
          v-for="(tile, index) in playerTiles"
          :key="index"
          class="tile-container"
          :class="{ 'highlighted-tile': tile === drawnTile }"
      >
        <img
            src="@/assets/tiles_back/playerTiles.png"
            class="player-tiles-back"
            alt="tile back"
        />
        <img
            :src="getTileImage(tile)"
            @click="handleTileClick(tile)"
            class="tile-front"
            alt="tile front"
        />
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
      playerActions: [ ], // 玩家操作
      currentTurnPlayerName: null,

      tableTiles: [],
      playerTiles: [], // 玩家手牌
      drawnTile: null,

      showTiles: [[], [], [], []], // 玩家的明牌

      notification: {
        show: false,
        action: '',
        position: '',
      },

      skipTimeout: null, // 跟踪自动跳过的超时
    };
  },
  computed: {
    filteredActions() {
      const actions = [];
      if (Array.isArray(this.playerActions)) {
        this.playerActions.forEach(action => {
          if (!['Discard', 'SelfKong', 'Win'].includes(action)) {
            actions.push(action);
          }
        });
      } else if (typeof this.playerActions === 'string') {
        if (!['Discard', 'SelfKong', 'Win'].includes(this.playerActions)) {
          actions.push(this.playerActions);
        }
      } else {
        console.error("playerActions is neither an array nor a string:", this.playerActions);
      }

      if (actions.length > 0) {
        actions.push('Skip'); // 添加“跳过”按钮
      }

      console.log("filteredActions:", actions);
      return actions;
    }
  },


  methods: {
    gameInitialization(message){
      console.log("gameInitialization" + this.playerActions);
      this.playerTiles = message.playerTiles[this.playerIndex];
      this.currentTurnPlayerName = message.currentTurnPlayerName;
      if(this.currentTurnPlayerName === this.playerIndex){
        this.$ws.send(JSON.stringify({ type: 'startGame',state: this.currentTurnPlayerName , roomId: this.roomId}));
      }

    },
    //更新手牌
    updateGame(message) {
      this.playerTiles = message.discardedTile;
    },
    //获取玩家行为
    handlePlayerActions(message) {
      this.playerActions = message.playerActions;

      if (message.state === "Draw"){
        this.playerTiles = message.playerTiles;
        this.drawnTile = message.drawnTile;
      }

      // this.playerTiles = message.playerTiles[this.playerIndex];

      // 如果 playerActions 有超过2个操作，5秒内没有点击则自动点击 Skip
      if (this.filteredActions.length > 0 || this.currentTurnPlayerName !== this.playerIndex) {
        if (this.skipTimeout) {
          clearTimeout(this.skipTimeout);
        }

        this.skipTimeout = setTimeout(() => {
          this.handleAction('Skip');
        }, 5000);
      }
      //如果当前玩家是下一个玩家，则自动点击 Skip
      if(this.getNextPlayerName(this.currentTurnPlayerName) === this.playerIndex ){
        if (this.skipTimeout) {
          clearTimeout(this.skipTimeout);
        }

        this.skipTimeout = setTimeout(() => {
          this.handleAction('Skip');
        }, 7000);
      }
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
    // 根据当前玩家ID获取下一个玩家ID
    getNextPlayerName(playerIndex, offset) {
      const currentIdx = this.players.indexOf(playerIndex);
      const nextIdx = (currentIdx + offset) % 4;
      return this.players[nextIdx];
    },
    // 处理牌面的点击事件
    handleTileClick(tile) {
      if (this.playerActions.length === 1 &&
          this.playerActions[0] === 'Discard'){
        const tileIndex = this.playerTiles.indexOf(tile);
        const message = JSON.stringify({ type: 'action',
                                                behavior: 'Discard',
                                                state: 'Playing' ,
                                                data: tileIndex ,
                                                roomId: this.roomId ,
                                                playIndex: this.playerIndex,
                                                nextPlayerName: this.getNextPlayerName(this.playerIndex, 1)});
        this.$ws.send(message);
        this.playerActions = [];
      }
    },
    // 处理操作按钮的点击事件
    handleAction(action) {
      console.log('Action clicked:', action); // 调试信息
      const message = JSON.stringify({ type: 'action',
                                            behavior: action, state: 'Playing' ,
                                            roomId: this.roomId ,
                                            playIndex: this.playerIndex ,
                                            nextPlayerName: this.getNextPlayerName(this.playerIndex, 1)});
      this.$ws.send(message);

      // 点击按钮后清除自动跳过的超时
      if (this.skipTimeout) {
        clearTimeout(this.skipTimeout);
        this.skipTimeout = null;
      }
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
          this.tableTiles = message.tableTiles;
          break;
        case 'gameInitialization':
          this.gameInitialization(message);
          break;
        case 'Turn change':
          this.currentTurnPlayerName = message.currentTurnPlayerName;
          break;
      }
    }
  },
  mounted() {
    // 使用全局 WebSocket 连接
    this.$ws.onmessage = this.handleMessage;
  },
  beforeUnmount() {
    // 清除任何未清除的超时
    if (this.skipTimeout) {
      clearTimeout(this.skipTimeout);
    }
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
  margin: 0 33px;
  transition: transform 0.3s ease; /* 添加过渡效果 */
}

.tile-container:hover {
  transform: translateY(-30px); /* 悬停时上移20px，根据需要调整 */
}

.tile-front {
  width: 45px; /* 根据需要调整大小 */
  height: 75px; /* 根据需要调整大小 */
  position: absolute;
  top: 8px;
  left: 2px;
  z-index: 2;
  cursor: pointer;
}

.highlighted-tile {
  transform: translateY(-10px); /* 或者根据需要调整上移的距离 */
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
  bottom: 110px;
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
  width: 22px;
  height: 37px;
  z-index: 2;
  transition: transform 0.3s ease; /* 添加过渡效果 */
}

/* 右边展示手牌逆时针旋转90度 */
.shown-tiles-right .shown-tile {
  transform: rotate(-90deg);
  transform-origin: center; /* 绕中心旋转 */
  margin: -8px; /* 调整每张牌之间的间距 */
}

.shown-tiles-right .shown-tiles-back {
  transform: rotate(-90deg);
  transform-origin: center; /* 绕中心旋转 */
  margin: -8px; /* 调整每张牌之间的间距 */
  top: 0px; /* 调整牌背的垂直位置 */
}

/* 左边展示手牌顺时针旋转90度 */
.shown-tiles-left .shown-tile {
  transform: rotate(90deg);
  transform-origin: center; /* 绕中心旋转 */
  margin: -8px; /* 调整每张牌之间的间距 */
}

.shown-tiles-left .shown-tiles-back {
  transform: rotate(90deg);
  transform-origin: center; /* 绕中心旋转 */
  margin: -8px; /* 调整每张牌之间的间距 */
  top: 0px; /* 调整牌背的垂直位置 */
}


.shown-tiles-back{
  width: 25px; /* 根据需要调整大小 */
  height: 45px; /* 根据需要调整大小 */

  position: absolute;
  top: 4px;
  left: -1px;
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
  top: 50px;
  left: 50%;
  transform: translateX(-50%);
}

.notification.left {
  left: 200px;
  top: 50%;
  transform: translateY(-50%);
}

</style>