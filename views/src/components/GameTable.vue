<template>
  <div class="game-table">
    <!-- room id -->
    <div class="room-id">
      RoomID: {{ roomId }}
      your name: {{playerIndex}}
      current Turn Player Name: {{currentTurnPlayerName}}
      nest player name: {{getNextPlayerName(currentTurnPlayerName, 1)}}
    </div>

    <!-- player tiles -->
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

    <!-- other players tiles -->
    <div class="other-players-tiles">

      <!-- right player tiles -->
      <div class="other-players-right">
        <div v-for="(tile, index) in rightPlayerTiles" :key="index" class="other-players-tile-container">
          <img src="@/assets/tiles_back/otherPlayerRight.png" class="other-players-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- top player tiles -->
      <div class="other-players-top">
        <div v-for="(tile, index) in topPlayerTiles" :key="index" class="other-players-tile-container">
          <img src="@/assets/tiles_back/otherPlayerTop.png" class="other-players-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- left player tiles -->
      <div class="other-players-left">
        <div v-for="(tile, index) in leftPlayerTiles" :key="index" class="other-players-tile-container">
          <img src="@/assets/tiles_back/otherPlayerLeft.png" class="other-players-tiles-back" alt="tile back"/>
        </div>
      </div>

    </div>


    <!-- shown tiles -->
    <div class="shown-tiles">
      <!-- bottom player -->
      <div class="shown-tiles-bottom">
        <div v-for="(tile, index) in showTiles[playerIndexInList]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- right player -->
      <div class="shown-tiles-right">
        <div v-for="(tile, index) in showTiles[(playerIndexInList + 1) % 4]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- top player -->
      <div class="shown-tiles-top">
        <div v-for="(tile, index) in showTiles[(playerIndexInList + 2) % 4]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

      <!-- left player -->
      <div class="shown-tiles-left">
        <div v-for="(tile, index) in showTiles[(playerIndexInList + 3) % 4]" :key="index" class="shown-tile-container">
          <img :src="getTileImage(tile)" class="shown-tile" alt="tile"/>
          <img src="@/assets/tiles_back/showedTiles.png" class="shown-tiles-back" alt="tile back"/>
        </div>
      </div>

    </div>

    <!-- table tiles -->
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

    <!-- popup notification -->
    <div v-if="notification.show" class="notification" :class="notification.position">
      {{ notification.action }}
    </div>

    <!-- results overlay -->
    <div v-if="showGameResults" class="results-overlay">
      <GameResults
          :players="players"
          :playerIndex="playerIndex"
          :roomId="roomId"
          :ScoresList="ScoresList"
          :playerTiles="playerTiles"
          :rightPlayerTiles="rightPlayerTiles"
          :topPlayerTiles="topPlayerTiles"
          :leftPlayerTiles="leftPlayerTiles"
          @newGame="hideGameResults"
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
    playerIndex: null,
  },
  data() {
    return {
      showGameResults: false,
      ScoresList: [],

      playerActions: [ ],
      currentTurnPlayerName: null,

      tableTiles: [],
      playerTiles: [],

      rightPlayerTiles: [],
      topPlayerTiles: [],
      leftPlayerTiles: [],


      tilesToEat: [],
      highlightedTiles: [],
      showTiles: [[], [], [], []],

      notification: {
        show: false,
        action: '',
        position: '',
      },

      skipTimeout: null,
    };
  },
  computed: {
    playerIndexInList() {
      return this.players.indexOf(this.playerIndex);
    },

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
    // Get next player ID based on current player ID
    getNextPlayerName(currentTurnPlayerName, offset) {
      const currentIdx = this.players.indexOf(currentTurnPlayerName);
      const nextIdx = (currentIdx + offset) % 4;
      return this.players[nextIdx];
    },

    gameInitialization(message){

      //update player tiles
      this.playerTiles = message.playerTiles[this.playerIndex];

      //update other players' tiles
      this.rightPlayerTiles = message.playerTiles[this.players[(this.playerIndexInList + 1) % 4]];
      this.topPlayerTiles = message.playerTiles[this.players[(this.playerIndexInList + 2) % 4]];
      this.leftPlayerTiles = message.playerTiles[this.players[(this.playerIndexInList + 3) % 4]];


      this.currentTurnPlayerName = message.currentTurnPlayerName;
      if(this.currentTurnPlayerName === this.playerIndex){
        this.$ws.send(JSON.stringify({ type: 'startGame',state: this.currentTurnPlayerName , roomId: this.roomId}));
      }

    },

    //update player tiles after discarding a tile
    updateGame(message) {
      this.playerTiles = message.discardedTile;
    },

    //get player actions
    handlePlayerActions(message) {
      this.playerActions = message.playerActions;
      this.tilesToEat = message.tilesToEat;

      //if the player is the current turn player, update the player tiles
      if (message.state === "Draw"){
        this.playerTiles = message.playerTiles;
      } else if (message.state === "NoMoreTiles"){
        //if no more tiles, end the game
        this.$ws.send(JSON.stringify({ type: 'gameEnd', roomId: this.roomId }));
      }



      //if the player is not the next turn player, remove the 'Chi' action
      if (this.getNextPlayerName(this.currentTurnPlayerName, 1) !== this.playerIndex){
        const chiIndex = this.playerActions.indexOf('Chi');
        if (chiIndex !== -1) {
          this.playerActions.splice(chiIndex, 1);
        }
        this.tilesToEat = [];
      }

      // Add a skip button if the player is not in turn and there is preemption
      if (this.playerActions.length > 0 && this.currentTurnPlayerName !== this.playerIndex) {
        // If not clicked within 10 seconds, click Skip

        this.playerActions.push('Skip'); // Add a "Skip" button

        if (this.skipTimeout) {
          clearTimeout(this.skipTimeout);
        }

        this.skipTimeout = setTimeout(() => {
          this.handleAction('Skip');
        }, 10000);

      }

      // If no preemption can be done and the current player is next, click Skip after 13 seconds
      if(this.getNextPlayerName(this.currentTurnPlayerName, 1) === this.playerIndex && this.filteredActions.length === 0){
        if (this.skipTimeout) {
          clearTimeout(this.skipTimeout);
        }

        this.skipTimeout = setTimeout(() => {
          this.handleAction('Skip');
        }, 13000);
      }
    },


    // Show player action notifications
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

    // Receive notification to update the card library
    updateShownTiles(message){
      this.tableTiles = message.tableTiles;

      if (this.playerIndex === this.players[message.performerIndex]){
        this.playerTiles = message.playernowtiles
      }

      this.showTiles[message.performerIndex] = message.showTiles;

      // Accept the notification and clear the list of actions. Avoid players eat cards, but also bar
      this.playerActions = [];
      console.log("performerIndex " +  message.performerIndex);
      console.log(message.showTiles);
      this.showNotification(message.action, message.performerIndex);
    },

    // Get image path dynamically
    getTileImage(tile) {
      try {
        return require(`@/assets/tiles_front/${tile}.png`);
      } catch (e) {
        return '';
      }
    },

    // Handle the click event on the card
    handleTileClick(tile) {
      if (this.currentTurnPlayerName === this.playerIndex) {
        // if not cooling
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

          this.$ws.send(message);

          // the cooldown
          this.clickCooldown = true;
          setTimeout(() => {
            //reset the cooldown
            this.clickCooldown = false;
          }, 13000); // 13 seconds

          this.playerActions = [];
          this.drawnTile = null;
        } else {
          console.log('Click cooling, can not click again');
        }
      } else {
        console.log('The current player is not a turn player and cannot perform an action.');
      }
    },

    // Handle the action button click event
    handleAction(action, tilesIndex = -1) {
      console.log('Action clicked:', action);

      let skipType = null;
      if (this.playerActions.includes("Win") && action === "Skip") {
        // add SkipType
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
        skipType: skipType
      });

      if (!(action === "SelfKong" && this.currentTurnPlayerName !== this.playerIndex)){

        this.$ws.send(message);

        this.playerActions = [];
        this.tilesToEat = [];

        // Clear auto-skipped timeouts after clicking the button
        if (this.skipTimeout) {
          clearTimeout(this.skipTimeout);
          this.skipTimeout = null;
        }
      }
    },

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
      this.$emit('newGame');
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
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 80vh;
}

.room-id {
  position: fixed;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 20px;
  font-weight: bold;
  color: white;
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
  transition: transform 0.3s ease;
}

.tile-container:hover {
  transform: translateY(-30px);
}

.tile-front {
  width: 45px;
  height: 75px;
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
  transform: translateY(-10px);
}

.player-tiles-back{
  width: 70px;
  height: 100px;

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
  width: 25px;
  height: 40px;
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
  justify-content: flex-start;
  align-items: flex-start;
}

.other-players-left {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  left: -400px;
}

.other-players-tile-container {
  display: flex;
  margin: -3px;
  flex-wrap: wrap;
}

.other-players-tiles-back {
  width: 40px;
  height: 60px;
  margin: 2px;
}


.other-players-right .other-players-tile-container .other-players-tiles-back {
  margin-bottom: -30px;
}


.other-players-left .other-players-tile-container .other-players-tiles-back {
  margin-bottom: -30px;
}


.shown-tiles {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
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
  top: 245px;
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
  margin: 0;
  display: flex;
}

.shown-tile {
  width: 22px;
  height: 37px;
  z-index: 11;
  transition: transform 0.3s ease;
}


.shown-tiles-right .shown-tile {
  transform: rotate(-90deg);
  transform-origin: center;
  margin: -8px;
}

.shown-tiles-right .shown-tiles-back {
  transform: rotate(-90deg);
  transform-origin: center;
  margin: -8px;
  top: 0px;
}


.shown-tiles-left .shown-tile {
  transform: rotate(90deg);
  transform-origin: center;
  margin: -8px;
}

.shown-tiles-left .shown-tiles-back {
  transform: rotate(90deg);
  transform-origin: center;
  margin: -8px;
  top: 0px;
}


.shown-tiles-back{
  width: 25px;
  height: 45px;

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