<template>
  <div class="waiting-room">
    <h1 class="title">等待房间</h1>
    <div v-for="(player, index) in players" :key="index" class="player-box">
      {{ player.name }}
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      players: [
        { name: '玩家1' },
        { name: '玩家2' },
        { name: '玩家3' },
        { name: '玩家4' }
      ]
    };
  }
};
</script>

<style scoped>
.waiting-room {
  color: white; /* 设置字体颜色为白色 */
}

.player-box {
  display: inline-block;
  width: 100px;
  height: 100px;
  margin: 10px;
  border: 2px solid #000;
  line-height: 100px;
  font-size: 18px;
}

</style>