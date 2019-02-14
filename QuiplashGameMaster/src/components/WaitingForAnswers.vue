<template>
    <div>
        Waiting for your answers...<br>
        Seconds remaining: {{countdown}}
        {{gameState}}
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: 'WaitingForAnswers',
    props: {
        gameIdProp: Number
    },
    data() {
        return{
            gameState: null,
            countdown: 60,
            gameId: null
        }
    },
    methods: {
        getGameState() {
            axios.get(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/get-phase')
                .then(response => {
                    this.$data.gameState = response.data
                })
        },
        startVoting() {
            axios.post(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/start-voting')
        },
        waitForAnswers() {
            console.log(this.$data.countdown)
            this.getGameState()
            if (this.$data.countdown <= 0) {
                this.startVoting()
                this.$data.gameState = 'voting'
            }
            if (this.$data.gameState === 'voting') {
                clearInterval(this.intervalId)
                this.$emit('startVoting')
            }
            this.$data.countdown -= 1
        }
    },
    mounted() {
        this.$data.gameId = this.gameIdProp
        this.waitForAnswers()
        this.intervalId = setInterval(this.waitForAnswers, 1000)
    }
}
</script>

<style scoped>

</style>
