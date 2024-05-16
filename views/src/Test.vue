<template>
  <div id="app">
    <WelcomePage v-if="currentComponent === 'WelcomePage'" @joinRoom="switchToInputRoomNumber" @createRoom="switchToWaitingRoom" />
    <InputRoomNumber v-if="currentComponent === 'InputRoomNumber'" @roomEntered="switchToWaitingRoom" />
    <WaitingRoom v-if="currentComponent === 'WaitingRoom'" />
  </div>
</template>

<script>
import InputRoomNumber from './components/InputRoomNumber.vue'
import WaitingRoom from './components/WaitingRoom.vue'
import WelcomePage from "@/components/WelcomePage.vue";


export default {
  name: 'App',
  components: {
    WelcomePage,
    InputRoomNumber,
    WaitingRoom
  },
  data() {
    return {
      currentComponent: 'WelcomePage'
    };
  },
  methods: {
    switchToInputRoomNumber() {
      this.currentComponent = 'InputRoomNumber';
    },
    switchToWaitingRoom() {
      this.currentComponent = 'WaitingRoom';
    },
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