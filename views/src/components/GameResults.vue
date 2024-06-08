<template>
  <div class="game-results">
    <h1>RoomID: {{ roomId }}</h1>
    <h2>Game-results:</h2>
    <ul class="results-list">
      <li v-for="(player, index) in players" :key="index" class="result-item">
        Player {{ index + 1 }}: {{ player }} - Scores: {{ ScoresList[index] }}
        <span v-if="index === playerIndex" class="current-player"> (you)</span>
        <div class="tiles-container">
          <img v-for="tile in getPlayerTiles(index)" :key="tile" :src="fetchTileImage(tile)" class="tile" />
        </div>
      </li>
    </ul>

    <button @click="goBack" class="return-button">New Game</button>
  </div>
</template>

<script>
export default {
  name: 'GameResults',
  props: {
    players: Array,
    playerIndex: null,
    roomId: String,
    ScoresList: Array,
    playerTiles:Array,
    rightPlayerTiles:Array,
    topPlayerTiles:Array,
    leftPlayerTiles:Array
  },

  methods: {
    goBack() {
      this.$emit('newGame');
    },
    getPlayerTiles(index) {
      console.log(this.playerTiles)
      console.log(this.rightPlayerTiles)
      console.log(this.leftPlayerTiles)
      console.log(this.topPlayerTiles)
      switch (index) {
        case 0:
          return this.playerTiles;
        case 1:
          return this.rightPlayerTiles;
        case 2:
          return this.topPlayerTiles;
        case 3:
          return this.leftPlayerTiles;
        default:
          return [];
      }
    },
    fetchTileImage(tile) {
      try {
        return require(`@/assets/tiles_front/${tile}.png`);
      } catch (e) {
        return '';
      }
    },
  },

};
</script>

<style scoped>
.game-results {
  text-align: center;
  padding: 20px;
  color: #333;
  background-color: #f8f8f8;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.results-list {
  list-style-type: none;
  padding: 0;
}

.result-item {
  padding: 8px;
  margin: 8px 0;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
}

.current-player {
  font-weight: bold;
  color: #d9534f;
}

h1, h2 {
  margin-bottom: 20px;
}

.return-button {
  margin-top: 20px;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}

.tiles-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  margin-top: 10px;
}

.tile {
  width: 45px;
  height: 75px;
  margin: 2px;
}
</style>