<template>
  <div class="waiting-room">
    <h1 class="title">等待房间</h1>
    <div v-for="(player, index) in players" :key="index" class="player-box">
      {{ player.name }}
      <!-- 在最后一个玩家的矩形下方显示按钮 -->
      <div v-if="index === players.length - 1" class="player-buttons">
        <button @click="startGame">开始</button>
        <button @click="exitRoom">退出</button>
      </div>
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
  },
  methods: {
    startGame() {
      this.$emit('startGame');
    },
    exitRoom() {
      this.$emit('exitRoom');
    }
  }
};
</script>

<style scoped>
.waiting-room {
  text-align: center;
  color: white; /* 字体颜色设置为白色 */
}

.title {
  font-size: 24px;
  margin-bottom: 20px;
}

.player-box {
  display: inline-block;
  width: 100px;
  height: 100px;
  margin: 10px;
  border: 2px solid #000;
  line-height: 100px;
  font-size: 18px;
  position: relative; /* 相对定位以便放置按钮 */
}

.player-buttons {
  position: absolute;
  bottom: -50px; /* 确保按钮在矩形下方 */
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  justify-content: space-around;
  width: 100%;
}

button {
  margin: 0 5px;
}

</style>