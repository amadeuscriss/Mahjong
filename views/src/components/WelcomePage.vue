<template>
  <div class="welcome">
    <h1>Welcome to mahjong</h1>
    <div class="button-container">
      <button @click="$emit('joinRoom')">Join Room</button>
      <button @click="$emit('createRoom')">Create Room</button>
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
  font-size: 40px; /* 增大标题字号 */
  flex-direction: column; /* 将子元素垂直排列 */
  justify-content: center;
  align-items: center;
  height: 80vh;
}

.button-container {
  margin-top: 20px; /* 设置顶部边距 */
}

button {
  margin: 0 40px;
  padding: 20px 40px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 24px; /* 调整按钮字体大小 */
}

button:hover {
  background-color: #0056b3;
}
</style>
