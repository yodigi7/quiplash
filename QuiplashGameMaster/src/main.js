import Vue from 'vue'
import VueRouter from 'vue-router'
import App from './App.vue'
import StartGame from './components/StartGame.vue'

Vue.config.productionTip = false

// Vue.use(VueRouter);

// const routes = [
//   { path: '/start-game', component: StartGame }
// ];

new Vue({
  render: h => h(App),
}).$mount('#app')
