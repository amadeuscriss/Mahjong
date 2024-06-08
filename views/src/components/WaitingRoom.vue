<template>
  <div class="waiting-room">
    <h1>RoomID: {{ roomId }}</h1>
    <h2>player-list:</h2>
    <ul class="player-list">
      <li v-for="(player, index) in players" :key="index" class="player-item">
        player{{ index + 1 }}: {{ player }}
        <span v-if="index === this.players.indexOf(playerIndex)" class="current-player"> (you)</span>
      </li>
    </ul>
    <p>your name: {{ playerIndex }}</p>
  </div>
</template>


<script>
export default {
  name: 'WaitingRoom',
  props: {
    players: Array,
    playerIndex: null,
    roomId: String
  }
};
</script>

<style scoped>
.waiting-room {
  text-align: center;
  background-color: #2c3e50;
  padding: 20px;
  border-radius: 8px;
  color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  max-width: 400px;
  margin: 50px auto;
  font-family: 'Arial', sans-serif;
}

h1 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #e74c3c;
}

h2 {
  font-size: 20px;
  margin-bottom: 10px;
}

.player-list {
  list-style-type: none;
  padding: 0;
}

.player-item {
  background-color: #34495e;
  padding: 10px;
  margin: 5px 0;
  border-radius: 4px;
  font-size: 16px;
  transition: background-color 0.3s;
}

.player-item:hover {
  background-color: #3a536e;
}

.current-player {
  font-weight: bold;
  color: #2ecc71;
}

p {
  font-size: 16px;
  margin-top: 10px;
}
</style>
