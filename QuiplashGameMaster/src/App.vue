<template>
  <div id="app">
    {{gameId}}
    {{contenders}}
    {{gameState}}
    <StartGame v-if="gameState === 'joining'" @startGame="setGameId"></StartGame>
    <WaitingForAnswers v-if="gameState == 'answering questions'" v-bind:gameIdProp="gameId" @startVoting="startVoting"></WaitingForAnswers>
    <Voting v-bind:gameIdProp="gameId"></Voting>
  </div>
</template>

<script>
import StartGame from './components/StartGame.vue'
import WaitingForAnswers from './components/WaitingForAnswers.vue'
import axios from 'axios'

export default {
  name: 'app',
  components: {
    StartGame,
    WaitingForAnswers
  },
  data() {
      return {
          gameId: null,
          contenders: [],
          gameState: null
      }
  },
  methods: {
    setGameId(value) {
      this.$data.gameId = value;
      this.getSetContenders();
      this.getSetGameState();
    },
    getSetContenders() {
      axios.get(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/get-contender-names')
        .then(response => {
          this.$data.contenders = response.data;
        })
    },
    getSetGameState() {
      axios.get(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/get-phase')
        .then(response => {
          this.$data.gameState = response.data
        })
    },
    startVoting() {
      this.getSetGameState()
    }
  },
  mounted() {
    this.gameState = 'joining';
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
