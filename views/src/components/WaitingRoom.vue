<template>
  <div class="waiting-room">
    <h1>房间号: {{ roomId }}</h1>
    <h2>玩家列表:</h2>
    <ul>
      <li
          v-for="(player, index) in players" :key="index">{{ player }}
        玩家{{ index + 1 }}: {{ player }}
        <span v-if="index === playerIndex"> (当前玩家)</span>

      </li>
    </ul>
    <p>当前玩家索引: {{ playerIndex }}</p>
  </div>
</template>

<script>
export default {
  name: 'WaitingRoom',
  props: {
    players: Array,
    playerIndex: Number,
    roomId: String
  }
};
</script>

<style scoped>
.waiting-room {
  text-align: center;
  color: white; /* 字体颜色设置为白色 */
}

button {
  margin: 0 5px;
}

</style>