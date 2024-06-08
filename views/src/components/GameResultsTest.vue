<template>
  <div class="game-results">
    <h1>房间号: {{ roomId }}</h1>
    <h2>结算结果:</h2>
    <ul class="results-list">
      <li v-for="(player, index) in players" :key="index" class="result-item">
        玩家 {{ index + 1 }}: {{ player }} - 分数: {{ ScoresList[index] }}
        <span v-if="index === playerIndex" class="current-player"> (当前玩家)</span>
        <div class="tiles-container">
          <img v-for="tile in getPlayerTiles(index)" :key="tile" :src="fetchTileImage(tile)" class="tile" />
        </div>
      </li>
    </ul>
    <!-- 返回按钮 -->
    <button @click="goBack" class="return-button">返回</button>
  </div>
</template>

<script>
export default {
  name: 'GameResults',
  data() {
    return {
      players: ['0','1','2','3'],
      playerIndex: '2',
      roomId: '12345',
      ScoresList: [1,2,3,4],
      playerTiles: ["Bamboo 5","Bamboo 7","Bamboo 9","Character 6","Character 6","Character 7","Dot 1","Dot 7","Dot 9","Red","Green","West","East","Dot 3"],
      rightPlayerTiles: ["Bamboo 1","Bamboo 4","Bamboo 6","Bamboo 8","Character 5","Dot 1","Dot 2","Dot 4","Dot 8","Dot 9","Red","Green","East"],
      topPlayerTiles: ["Bamboo 1","Bamboo 2","Bamboo 4","Bamboo 9","Character 2","Character 3","Character 4","Dot 3","Dot 3","Dot 5","Dot 5","Dot 6","West"],
      leftPlayerTiles: ["Bamboo 2","Bamboo 3","Bamboo 7","Character 1","Character 1","Character 3","Character 3","Character 5","Character 7","Character 9","Dot 7","White","South"],
    };
  },
  methods: {
    goBack() {
      this.$emit('goBack');
    },
    getPlayerTiles(index) {
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
  padding: 10px;
  margin: 10px 0;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
}

.current-player {
  font-weight: bold;
  color: #d9534f; /* 当前玩家的颜色 */
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
  width: 45px; /* 根据需要调整大小 */
  height: 75px; /* 根据需要调整大小 */
  margin: 2px;
}
</style>

