<template>
  <div class="room-number-page">
    <!-- 欢迎标题 -->
    <h1 class="welcome-title">欢迎来到麻将游戏</h1>
    <!-- 输入房间号提示 -->
    <h2 class="input-room-number-prompt">请输入房间号</h2>
    <!-- 房间号输入框 -->
    <input type="text" v-model="roomNumber" @input="handleInput" class="room-number-input" maxlength="5" />
    <!-- 矩形框 -->
    <div class="digit-container">
      <div v-for="(digit, index) in digits" :key="index" class="digit">
        {{ digit }}
      </div>
      <!-- 确定按钮 -->
      <div v-if="showConfirmButton" class="digit confirm-button" @click="handleConfirm">确定</div>
    </div>
  </div>
</template>

<script>
// import axios from "axios";

export default {
  name: 'InputRoomNumber',
  data() {
    return {
      roomNumber: '', // 记录用户输入的房间号
      digits: ['', '', '', '', ''], // 存储每个矩形框中的数字
      showConfirmButton: false // 控制确定按钮显示与隐藏
    };
  },
  methods: {
    handleInput() {
      // 过滤非数字字符并截取前5个字符
      this.roomNumber = this.roomNumber.replace(/\D/g, '').slice(0, 5);
      // 更新矩形框中的数字
      this.digits = this.roomNumber.split('');
      // 根据输入数字个数决定是否显示确定按钮
      this.showConfirmButton = this.roomNumber.length === 5;
    },
    handleConfirm() {
      // 保存输入的数字
      const roomNumber = parseInt(this.roomNumber);

      console.log('房间号已保存到后端:', roomNumber);
      // 触发自定义事件，通知父组件房间号已输入
      this.$emit('roomEntered', this.roomNumber);
    }
    // async handleConfirm() {
    //   try{
    //     // 保存输入的数字
    //     const roomNumber = parseInt(this.roomNumber);
    //
    //     // 发送POST请求将房间号发送到后端
    //     const response = await axios.post('https://your-backend-api-url.com/rooms', { roomNumber });
    //
    //     // 如果后端成功接收到房间号，继续执行下面的代码
    //     console.log('房间号已保存到后端:', response.data);
    //
    //     // 触发自定义事件，通知父组件房间号已输入
    //     this.$emit('roomEntered', roomNumber);
    //   }catch (error) {
    //     // 处理请求失败的情况
    //     console.error('保存房间号时出错:', error);
    //   }
    // }
  }
}
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
</style>
