<template>
  <div class="game-results">
    <h1>房间号: {{ roomId }}</h1>
    <h2>结算结果:</h2>
    <ul class="results-list">
      <li v-for="(player, index) in players" :key="index" class="result-item">
        玩家 {{ index + 1 }}: {{ player }} - 分数: {{ ScoresList[index] }}
        <span v-if="index === playerIndex" class="current-player"> (当前玩家)</span>
      </li>
    </ul>
    <!-- 返回按钮 -->
    <button @click="goBack" class="return-button">返回</button>
  </div>
</template>

<script>
export default {
  name: 'GameResults',
  props: {
    players: Array,
    playerIndex: Number,
    roomId: String,
    ScoresList: Array,
  },

  methods: {
    goBack() {
      this.$emit('goBack');
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

</style>
