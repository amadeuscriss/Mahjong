<template>
  <div id="app">
    <!-- 使用 v-if 控制组件的显示与隐藏 -->
    <InputRoomNumber v-if="!roomEntered" @roomEntered="handleRoomEntered" />
    <WaitingRoom v-else />
  </div>
</template>

<script>
import InputRoomNumber from './components/InputRoomNumber.vue'
import WaitingRoom from './components/WaitingRoom.vue'

export default {
  name: 'App',
  components: {
    InputRoomNumber,
    WaitingRoom
  },
  data() {
    return {
      roomEntered: false // 控制房间号输入组件与等待室组件的切换
    };
  },
  methods: {
    handleRoomEntered(roomNumber) {
      console.log('Room entered:', roomNumber);
      // 房间号已输入，切换组件
      this.roomEntered = true;
    }
  }
}
</script>

<style>
body {
  /* 使用 url() 函数设置背景图片 */
  background-image: url('./assets/desk.png');
  /* 设置其他背景属性 */
  background-size: cover; /* 覆盖整个屏幕 */
  background-attachment: fixed; /* 滚动时固定背景 */
  background-position: center; /* 将背景图片居中 */
  /* 根据需要添加其他样式 */
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

</style>