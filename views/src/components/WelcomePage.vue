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
  font-size: 40px;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 80vh;
}

.button-container {
  margin-top: 20px;
}

button {
  margin: 0 40px;
  padding: 20px 40px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 24px;
}

button:hover {
  background-color: #0056b3;
}
</style>
