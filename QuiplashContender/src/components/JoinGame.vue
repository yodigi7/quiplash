<template>
    <div>
        <div v-if="!inputData">
            Join Game<br>
            Name: 
            <input v-model="name"/><br>
            Game id: 
            <input v-model="gameId"/><br>
            <button v-on:click="joinGame">Join Game</button>
        </div>
        <div v-else>
            Waiting for game to start...<br>
            <button v-on:click="startGame">Start Now</button>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: 'JoinGame',
    data() {
        return {
            gameId: 1,
            name: 'Anthony',
            inputData: false
        }
    },
    methods: {
        joinGame() {
            this.$data.gameId = parseInt(this.$data.gameId, 10)
            var config = {
                headers: {
                    'gameId': this.$data.gameId,
                    'name': this.$data.name
                }
            }
            axios.post(process.env.VUE_APP_BACKEND_BASE_URL + '/join', {}, config)
            this.$data.inputData = true
            this.intervalId = setInterval(this.checkStartingGame, 2000)
        },
        startGame() {
            axios.post(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/start-game')
        },
        checkStartingGame() {
            axios.post(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/get-phase')
                .then(response => {
                    this.$data.gameState = response.data
                })
            console.log('checkingGamestate')
            if (this.$data.gameState === 'answering questions') {
                clearInterval(this.intervalId)
                this.$emit('startGame', {'gameId': this.$data.gameId, 'name': this.$data.name})
            }
        }
    }
}
</script>

<style scoped>

</style>
