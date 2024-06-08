<template>
  <div class="room-number-page">
    <!-- Welcome title -->
    <h1 class="welcome-title">Welcome to mahjong</h1>
    <!-- Enter room number -->
    <h2 class="input-room-number-prompt">Please enter the room number</h2>
    <!-- Room number input field -->
    <input type="text" v-model="roomNumber" @input="handleInput" class="room-number-input" maxlength="6" />

    <div class="digit-container">
      <div v-for="(digit, index) in digits" :key="index" class="digit">
        {{ digit }}
      </div>
      <!-- join button -->
      <div v-if="showConfirmButton" class="digit confirm-button" @click="handleConfirm">join</div>
    </div>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <!-- go back button -->
    <button @click="goBack" class="return-button">Go Back</button>
  </div>
</template>


<script>
export default {
  name: 'InputRoomNumber',
  data() {
    return {
      roomNumber: '', // input room number
      digits: ['', '', '', '', '', ''], // store room number
      showConfirmButton: false, // display confirm button
      errorMessage: ''
    };
  },

  methods: {
    handleInput() {
      // Allow letters and numbers, and truncate the first 6 characters
      this.roomNumber = this.roomNumber.slice(0, 6);
      // Update the numbers in the box
      this.digits = this.roomNumber.split('').concat(Array(6).fill('')).slice(0, 6);
      // Show the OK button based on the number of digits entered
      this.showConfirmButton = this.roomNumber.length === 6;
      // Clear the error message
      this.errorMessage = '';
    },
    handleConfirm() {
      this.$emit('roomEntered', this.roomNumber);
    },
    setErrorMessage(message) {
      this.errorMessage = message;
    },
    goBack() {
      this.$emit('goBack');
    },
  }
};
</script>

<style scoped>
.room-number-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.welcome-title {
  font-size: 60px;
  margin-top: -100px;
}

.input-room-number-prompt {
  font-size: 30px;
}

.room-number-input {
  margin-top: 20px;
  font-size: 16px;
}

.digit-container {
  margin-top: 20px;
}

.digit {
  display: inline-block;
  width: 50px;
  height: 50px;
  border: 2px solid #000;
  background-color: #fff;
  line-height: 50px;
  font-size: 20px;
  margin-right: 10px;
}

.confirm-button {
  cursor: pointer;
  text-align: center;
  line-height: 50px;
  font-size: 16px;
}

.error-message {
  color: red;
  margin-top: 10px;
}

.return-button {
  margin-top: 20px;
  padding: 15px 30px;
  font-size: 28px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.return-button:hover {
  background-color: #218838;
}

</style>