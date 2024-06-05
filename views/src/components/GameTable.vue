<template>
  <div class="game-table">
    <!-- 显示房间号 -->
    <div class="room-id">
      房间号: {{ roomId }}
      玩家name{{playerIndex}}
      当前回合玩家name{{currentTurnPlayerName}}
      下家name{{getNextPlayerName(currentTurnPlayerName, 1)}}
    </div>


<!--    <div class="tiles">-->
<!--      <div v-for="(tile, index) in playerTiles" :key="index" class="tile-container">-->
<!--        <img src="@/assets/tiles_back/playerTiles.png" class="player-tiles-back" alt="tile back"/>-->
<!--        <img :src="getTileImage(tile)" @click="handleTileClick(tile)" class="tile-front" alt="tile front"/>-->
<!--        <img :src="getTileImage(tile)" @click="handleTileClick(tile)" class="tile-front" :class="{ 'drawn-tile': index === drawnTileIndex }" alt="tile front"/>-->
<!--      </div>-->
<!--    </div>-->


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

<!--    &lt;!&ndash; 操作按钮区域 &ndash;&gt;-->
<!--    <div class="action-buttons">-->
<!--      <button v-for="(action, index) in filteredActions" :key="index" @click="handleAction(action)">-->
<!--        {{ action }}-->
<!--      </button>-->
<!--    </div>-->

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



    // filteredActions() {
    //   console.log(this.playerActions);
    //   const actions = [];
    //   if (Array.isArray(this.playerActions)) {
    //     actions.push(this.playerActions.filter(action => action !== 'Discard'));
    //   } else if (typeof this.playerActions === 'string') {
    //     console.error("playerActions is a string:", this.playerActions);
    //   }
    //   return actions;
    // }

    //剔除'Discard', 'SelfKong'
    filteredActions() {
      const actions = [];
      if (Array.isArray(this.playerActions)) {
        this.playerActions.forEach(action => {
          if (!['Discard'].includes(action)) {
            actions.push(action);
          }
        });
      } else if (typeof this.playerActions === 'string') {
        if (!['Discard'].includes(this.playerActions)) {
          actions.push(this.playerActions);
        }
      } else {
        console.error("playerActions is neither an array nor a string:", this.playerActions);
      }

      if (actions.length > 0 && this.currentTurnPlayerName === this.playerIndex) {
        actions.push('Skip'); // 添加“跳过”按钮
      }

      console.log("filteredActions:", actions);
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
        this.drawnTile = message.drawnTile;
      }

      // this.playerTiles = message.playerTiles[this.playerIndex];

      // 如果该玩家不在回合内，且有抢占行为,增加跳过按钮
      if (this.filteredActions.length > 0 && this.currentTurnPlayerName !== this.playerIndex) {
        //10秒内没有点击则自动点击 Skip

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

    // // 处理牌面的点击事件
    // handleTileClick(tile) {
    //   if (this.currentTurnPlayerName === this.playerIndex){
    //     const tileIndex = this.playerTiles.indexOf(tile);
    //     const message = JSON.stringify({ type: 'action',
    //                                             behavior: 'Discard',
    //                                             state: 'Playing' ,
    //                                             data: tileIndex ,
    //                                             roomId: this.roomId ,
    //                                             playIndex: this.players.indexOf(this.playerIndex),
    //                                             nextPlayerName: this.getNextPlayerName(this.currentTurnPlayerName, 1)});
    //     this.$ws.send(message);
    //     this.playerActions = [];
    //     this.drawnTile = null;
    //   }
    // },

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
                                            nextPlayerName: this.getNextPlayerName(this.currentTurnPlayerName, 1),
                                            tilesToEatIndex : tilesIndex,
                                            skipType: skipType // 将 SkipType 添加到消息中
      });



      this.$ws.send(message);

      this.playerTiles = [];

      // 点击按钮后清除自动跳过的超时
      if (this.skipTimeout) {
        clearTimeout(this.skipTimeout);
        this.skipTimeout = null;
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
          break;
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