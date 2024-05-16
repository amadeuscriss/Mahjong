<template>
  <div class="welcome">
    <h1>欢迎来到麻将游戏</h1>
    <div class="button-container">
      <button @click="$emit('joinRoom')">加入房间</button>
      <button @click="$emit('createRoom')">新建房间</button>
    </div>
  </div>
</template>
<script>
export default {
  name: 'WelcomePage',
};
</script>

<style scoped>
.welcome {
  display: flex;
  flex-direction: column; /* 将子元素垂直排列 */
  justify-content: center;
  align-items: center;
  height: 80vh;
}

.button-container {
  margin-top: 20px; /* 设置顶部边距 */
}

button {
  margin: 0 10px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>