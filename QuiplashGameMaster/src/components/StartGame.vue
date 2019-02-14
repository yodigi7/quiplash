<template>
    <div>
        <div class="gameId">
            Game Id: {{ gameId }}
        </div>
        <div>
            <li v-for="(data, index) in contenders" :key="index"> {{data}} </li>
        </div>
    </div>
</template>

<script>
/* eslint-disable*/
import axios from 'axios'
export default {
    name: 'StartGame',
    data() {
        return {
            gameId: null,
            contenders: []
        }
    },
    methods: {
        initGame: function () {
            axios.get(process.env.VUE_APP_BACKEND_BASE_URL + '/init')
                .then(response => {
                    this.$data.gameId = response.data;
                    this.waitForStart()
                    this.intervalId = setInterval(this.waitForStart, 1000);
                })
        },
        checkForNewContenders: function() {
            axios.get(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/get-contender-names')
                .then(response => {
                    this.$data.contenders = response.data
                })
        },
        checkForGameState: function() {
            axios.get(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId.toString() + '/get-phase')
                .then(response => {
                    console.log(response.data)
                    if (response.data === "answering questions") {
                        clearInterval(this.intervalId)
                        this.$emit('startGame', this.$data.gameId)
                    }
                })
        },
        waitForStart: function() {
            console.log("here");
            if (this.checkForGameState()) {
                clearInterval(this.intervalId);
            }
            this.checkForNewContenders();
        }
    },
    created() {
        this.$data.gameId = this.gameId
        this.initGame()
    }
}
</script>

<style scoped>

</style>

}