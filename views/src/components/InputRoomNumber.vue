<template>
  <div class="room-number-page">
    <!-- 欢迎标题 -->
    <h1 class="welcome-title">欢迎来到麻将游戏</h1>
    <!-- 输入房间号提示 -->
    <h2 class="input-room-number-prompt">请输入房间号</h2>
    <!-- 房间号输入框 -->
    <input type="text" v-model="roomNumber" @input="handleInput" class="room-number-input" maxlength="6" />
    <!-- 矩形框 -->
    <div class="digit-container">
      <div v-for="(digit, index) in digits" :key="index" class="digit">
        {{ digit }}
      </div>
      <!-- 确定按钮 -->
      <div v-if="showConfirmButton" class="digit confirm-button" @click="handleConfirm">确定</div>
    </div>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <!-- 返回按钮 -->
    <button @click="goBack" class="return-button">返回</button>
  </div>
</template>


<script>
export default {
  name: 'InputRoomNumber',
  data() {
    return {
      roomNumber: '', // 记录用户输入的房间号
      digits: ['', '', '', '', '', ''], // 存储每个矩形框中的数字
      showConfirmButton: false, // 控制确定按钮显示与隐藏
      errorMessage: ''
    };
  },

  methods: {
    handleInput() {
      // 允许输入字母和数字，并截取前6个字符
      this.roomNumber = this.roomNumber.slice(0, 6);
      // 更新矩形框中的数字
      this.digits = this.roomNumber.split('').concat(Array(6).fill('')).slice(0, 6);
      // 根据输入数字个数决定是否显示确定按钮
      this.showConfirmButton = this.roomNumber.length === 6;
      // 清除错误信息
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
    handleEvent(event){
      const message = JSON.parse(event.data);
      if(message.type === 'roomEntered'){
        this.$emit('roomEntered', message.roomId);
      }else if(message.type === 'error'){
        this.setErrorMessage(message.message);
      }
    }
  }
};
</script>

<style scoped>
.room-number-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 设置容器高度，使其充满整个视口 */
}

.welcome-title {
  font-size: 60px; /* 增大标题字号 */
  margin-top: -100px; /* 上移标题 */
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
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}

</style>
