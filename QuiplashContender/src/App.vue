<template>
  <div id="app">
    <JoinGame v-if="gameState === 'joining' || gameState == null" @startGame="startGame"></JoinGame>
    <AnswerQuestions v-if="gameState === 'answering questions'" v-bind:gameIdProp="gameId" v-bind:nameProp="name" @startVoting="startVoting"></AnswerQuestions>
    <Voting v-if="gameState === 'voting'" v-bind:gameIdProp="gameId" v-bind:nameProp="name"></Voting>
  </div>
</template>

<script>
import JoinGame from './components/JoinGame.vue'
import AnswerQuestions from './components/AnswerQuestions.vue'
import Voting from './components/Voting.vue'

export default {
  name: 'app',
  components: {
    JoinGame,
    AnswerQuestions,
    Voting
  },
  data () {
    return {
      gameId: null,
      name: null,
      gameState: null
    }
  },
  methods: {
    getSetGameState() {
      axios.get(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/get-phase')
        .then(response => {
          this.$data.gameState = response.data
        })
    },
    startGame(event) {
      this.$data.name = event.name
      this.$data.gameId = event.gameId
      console.log('STARTING GAME')
      this.$data.gameState = 'answering questions'
    }
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
