<template>
  <div class="game-table">
    <!-- 显示房间号 -->
    <div class="room-id">
      房间号: {{ roomId }}
      玩家name{{playerIndex}}
      当前回合玩家name{{currentTurnPlayerName}}
      下家name{{getNextPlayerName(currentTurnPlayerName, 1)}}
    </div>

    <!-- 玩家手牌展示区 -->
    <div class="tiles">
      <div
          v-for="(tile, index) in playerTiles"
          :key="index"
          class="tile-container"
          :class="{
          'highlighted-tile': index === 13 && playerTiles.length === 14,
          'highlighted': highlightedTiles.includes(index)
        }"
          @mouseover="highlightTiles([index])"
          @mouseleave="resetHighlight()"
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

    <!-- 展示其他玩家手牌区 -->
    <div class="other-players-tiles">

      <!-- 右侧玩家手牌 -->
      <div class="other-players-right">
        <div v-for="(tile, index) in rightPlayerTiles" :key="index" class="other-players-tile-container">
          <img src="@/assets/tiles_back/otherPlayerRight.png" class="other-players-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- 上方玩家手牌 -->
      <div class="other-players-top">
        <div v-for="(tile, index) in topPlayerTiles" :key="index" class="other-players-tile-container">
          <img src="@/assets/tiles_back/otherPlayerTop.png" class="other-players-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- 左侧玩家手牌 -->
      <div class="other-players-left">
        <div v-for="(tile, index) in leftPlayerTiles" :key="index" class="other-players-tile-container">
          <img src="@/assets/tiles_back/otherPlayerLeft.png" class="other-players-tiles-back" alt="tile back"/>
        </div>
      </div>

    </div>


    <!-- 展示明牌区 -->
    <div class="shown-tiles">
      <!-- 下方玩家 -->
      <div class="shown-tiles-bottom">
        <div v-for="(tile, index) in showTiles[playerIndexInList]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- 右侧玩家 -->
      <div class="shown-tiles-right">
        <div v-for="(tile, index) in showTiles[(playerIndexInList + 1) % 4]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- 上方玩家 -->
      <div class="shown-tiles-top">
        <div v-for="(tile, index) in showTiles[(playerIndexInList + 2) % 4]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- 左侧玩家 -->
      <div class="shown-tiles-left">
        <div v-for="(tile, index) in showTiles[(playerIndexInList + 3) % 4]" :key="index" class="shown-tile-container">
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

    <div class="action-buttons">
      <button v-for="(action, index) in filteredActions" :key="index" @click="handleAction(action)">
        {{ action }}
      </button>
      <button v-for="(tiles, index) in tilesToEat" :key="'eat-' + index"
              @click="handleEatAction(index)"
              @mouseover="highlightTiles(tiles)"
              @mouseleave="resetHighlight()">
        吃牌 {{ index + 1 }}
      </button>
    </div>

    <!-- 玩家行为展示区 -->
    <div v-if="notification.show" class="notification" :class="notification.position">
      {{ notification.action }}
    </div>

    <!-- 结算结果窗口 -->
    <div v-if="showGameResults" class="results-overlay">
      <GameResults
          :players="players"
          :playerIndex="playerIndex"
          :roomId="roomId"
          :ScoresList="ScoresList"
          @goBack="hideGameResults"
      />
    </div>

  </div>
</template>

<script>
import GameResults from "@/components/GameResults.vue";

export default {
  name: 'GameTable',
  components: {
    GameResults,
  },
  props: {
    roomId: String,
    players: Array,
    playerIndex: null, // 玩家索引
  },
  data() {
    return {
      showGameResults: false,
      ScoresList: [],

      playerActions: [ ], // 玩家操作
      currentTurnPlayerName: null,

      tableTiles: [],
      playerTiles: [], // 玩家手牌

      rightPlayerTiles: [],
      topPlayerTiles: [],
      leftPlayerTiles: [],


      drawnTile: null,

      tilesToEat: [],
      highlightedTiles: [], // 高亮的牌索引
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
    playerIndexInList() {
      return this.players.indexOf(this.playerIndex);
    },

    //剔除'Chi'
    filteredActions() {
      const actions = [];
      if (Array.isArray(this.playerActions)) {
        this.playerActions.forEach(action => {
          if (!['Chi'].includes(action)) {
            actions.push(action);
          }
        });

      } else if (typeof this.playerActions === 'string') {
        if (!['Chi'].includes(this.playerActions)) {
          actions.push(this.playerActions);
        }
      } else {
        console.error("playerActions is neither an array nor a string:", this.playerActions);
      }

      return actions;
    }
  },


  methods: {
    // 根据当前玩家ID获取下一个玩家ID
    getNextPlayerName(currentTurnPlayerName, offset) {
      const currentIdx = this.players.indexOf(currentTurnPlayerName);
      const nextIdx = (currentIdx + offset) % 4;
      return this.players[nextIdx];
    },

    gameInitialization(message){
      console.log("gameInitialization" + this.playerActions);
      this.playerTiles = message.playerTiles[this.playerIndex];

      this.rightPlayerTiles = message.playerTiles[this.players[(this.playerIndexInList + 1) % 4]];
      this.topPlayerTiles = message.playerTiles[this.players[(this.playerIndexInList + 2) % 4]];
      this.leftPlayerTiles = message.playerTiles[this.players[(this.playerIndexInList + 3) % 4]];

      console.log("rightPlayerTiles " + this.rightPlayerTiles)
      console.log("topPlayerTiles " + this.topPlayerTiles)
      console.log("leftPlayerTiles " + this.leftPlayerTiles)
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
      this.tilesToEat = message.tilesToEat;

      if (message.state === "Draw"){
        this.playerTiles = message.playerTiles;
      } else if (message.state === "NoMoreTiles"){
        this.$ws.send(JSON.stringify({ type: 'gameEnd', roomId: this.roomId }));
      }



      //如果不是下家，删除吃牌操作
      if (this.getNextPlayerName(this.currentTurnPlayerName, 1) !== this.playerIndex){
        const chiIndex = this.playerActions.indexOf('Chi');
        if (chiIndex !== -1) {
          this.playerActions.splice(chiIndex, 1);
        }
        this.tilesToEat = [];
      }

      // 如果该玩家不在回合内，且有抢占行为,增加跳过按钮
      if (this.playerActions.length > 0 && this.currentTurnPlayerName !== this.playerIndex) {
        //10秒内没有点击则自动点击 Skip

        this.playerActions.push('Skip'); // 添加“跳过”按钮

        if (this.skipTimeout) {
          clearTimeout(this.skipTimeout);
        }

        this.skipTimeout = setTimeout(() => {
          this.handleAction('Skip');
        }, 10000);

      }

      //如果不能进行任何抢占操作,且当前玩家是下家，则15秒后自动点击 Skip
      if(this.getNextPlayerName(this.currentTurnPlayerName, 1) === this.playerIndex && this.filteredActions.length === 0){
        if (this.skipTimeout) {
          clearTimeout(this.skipTimeout);
        }

        this.skipTimeout = setTimeout(() => {
          this.handleAction('Skip');
        }, 15000);
      }
    },

    //在执行操作后更新手牌
    updateAfterActing(message){
      this.playerTiles = message.playerTiles[this.playerIndex];
    },

    // 显示玩家行为通知
    showNotification(action, performerIndex) {
      const positions = ['bottom', 'right', 'top', 'left'];
      const position = positions[(performerIndex - this.playerIndexInList + 4) % 4];

      this.notification = {
        show: true,
        action,
        position,
      };

      setTimeout(() => {
        this.notification.show = false;
      }, 5000);
    },
    //接收通知，更新明牌库
    updateShownTiles(message){
      this.tableTiles = message.tableTiles;

      if (this.playerIndex === this.players[message.performerIndex]){
        this.playerTiles = message.playernowtiles
      }

      this.showTiles[message.performerIndex] = message.showTiles;

      //接受通知，清空行为列表。避免有玩家吃牌后，还能杠
      this.playerActions = [];
      console.log("performerIndex " +  message.performerIndex);
      console.log(message.showTiles);
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
      // 如果当前玩家是当前回合的玩家
      if (this.currentTurnPlayerName === this.playerIndex) {
        // 如果没有在冷却中
        if (!this.clickCooldown) {
          const tileIndex = this.playerTiles.indexOf(tile);
          const message = JSON.stringify({
            type: 'action',
            behavior: 'Discard',
            state: 'Playing',
            data: tileIndex,
            roomId: this.roomId,
            playIndex: this.players.indexOf(this.playerIndex),
            nextPlayerName: this.getNextPlayerName(this.currentTurnPlayerName, 1),
            players: this.players,
          });

          // 发送消息
          this.$ws.send(message);

          // 设置冷却时间
          this.clickCooldown = true;
          setTimeout(() => {
            // 清除冷却状态
            this.clickCooldown = false;
          }, 15000); // 15秒冷却时间

          // 重置其他状态
          this.playerActions = [];
          this.drawnTile = null;
        } else {
          console.log('点击冷却中，无法再次点击。');
        }
      } else {
        console.log('当前玩家不是回合玩家，无法执行操作。');
      }
    },

    // 处理操作按钮的点击事件
    handleAction(action, tilesIndex = -1) {
      console.log('Action clicked:', action); // 调试信息

      let skipType = null;
      if (this.playerActions.includes("Win") && action === "Skip") {
        // 添加 SkipType
        skipType = "Hu";
      }

      const message = JSON.stringify({ type: 'action',
        behavior: action,
        state: 'Playing' ,
        roomId: this.roomId ,
        playIndex: this.players.indexOf(this.playerIndex),
        currentTurnPlayerName: this.currentTurnPlayerName,
        nextPlayerName: this.getNextPlayerName(this.currentTurnPlayerName, 1),
        tilesToEatIndex : tilesIndex,
        skipType: skipType // 将 SkipType 添加到消息中
      });

      if (!(action === "SelfKong" && this.currentTurnPlayerName !== this.playerIndex)){

        this.$ws.send(message);

        this.playerActions = [];
        this.tilesToEat = [];

        // 点击按钮后清除自动跳过的超时
        if (this.skipTimeout) {
          clearTimeout(this.skipTimeout);
          this.skipTimeout = null;
        }
      }
    },

    // 在事件处理程序中调用 handleAction
    handleEatAction(index) {
      this.handleAction('Chi', index);
    },

    highlightTiles(tiles) {
      this.highlightedTiles = tiles;
    },

    resetHighlight() {
      this.highlightedTiles = [];
    },


    hideGameResults() {
      this.showGameResults = false;
    },
    handleGameEnd(message) {
      this.ScoresList = message.ScoresList;
      this.showGameResults = true;
    },

    handleMessage(event) {
      const message = JSON.parse(event.data);
      switch (message.type) {
        case 'updateGame':
          console.log("updateGame")
          this.updateGame(message);
          break;
        case 'playerActions':
          this.handlePlayerActions(message);
          break;
        case 'notification':
          console.log("notification")
          this.updateShownTiles(message);
          break;
        case 'gameInitialization':
          this.gameInitialization(message);
          break;
        case 'Turn change':
          this.currentTurnPlayerName = message.currentTurnPlayerName;
          this.playerActions = []
          break;
        case 'gameEnd':
          this.handleGameEnd(message);
      }
    }
  },
  mounted() {
    if (this.$ws) {
      this.$ws.onmessage = this.handleMessage;
    } else {
      console.error("WebSocket is not defined.");
    }
  },
  beforeUnmount() {
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
  z-index: 10;
}

.tiles {
  position: fixed;
  bottom: 80px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  z-index: 40;
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
  z-index: 41;
  cursor: pointer;
}

.highlighted {
  transform: translateY(-10px);
  transition: transform 0.2s ease;
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
  z-index: 40;
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
  z-index: 30;
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
  z-index: 31;
}




.other-players-tiles {
  position: relative;
  z-index: 20;
}

.other-players-right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: -400px;
}

.other-players-top {
  position: absolute;
  top: -180px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  justify-content: flex-start; /* 将牌靠左对齐 */
  align-items: flex-start; /* 将牌靠上对齐 */
}

.other-players-left {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  left: -400px;
}

.other-players-tile-container {
  display: flex;
  margin: -3px; /* 根据需要调整间距 */
  flex-wrap: wrap;
}

.other-players-tiles-back {
  width: 40px; /* 根据需要调整牌背的宽度 */
  height: 60px; /* 根据需要调整牌背的高度 */
  margin: 2px; /* 根据需要调整牌背之间的间距 */
}

/* 右侧玩家的牌背样式 */
.other-players-right .other-players-tile-container .other-players-tiles-back {
  margin-bottom: -30px; /* 调整右侧玩家牌之间的垂直间距 */
}

/* 左侧玩家的牌背样式 */
.other-players-left .other-players-tile-container .other-players-tiles-back {
  margin-bottom: -30px; /* 调整左侧玩家牌之间的垂直间距 */
}


.shown-tiles {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none; /* 禁用指针事件 */
  z-index: 10;
}

.shown-tiles-bottom {
  position: absolute;
  bottom: 120px;
  display: flex;
}

.shown-tiles-right {
  position: absolute;
  right: 300px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
}

.shown-tiles-top {
  position: absolute;
  top: 200px;
  display: flex;
}

.shown-tiles-left {
  position: absolute;
  left: 300px;
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
  z-index: 11;
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
  z-index: 10;
}


.action-buttons {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 50;
}

.action-buttons button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  background: linear-gradient(135deg, #6b73ff 0%, #000dff 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.action-buttons button:hover {
  background: linear-gradient(135deg, #8c8eff 0%, #0014ff 100%);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.action-buttons button:active {
  background: linear-gradient(135deg, #4b52d8 0%, #000a99 100%);
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
  transform: translateY(0);
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
  z-index: 60;
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

.results-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}
</style>