<template>
    <div>
        <div v-if="voted">
            {{question}}<br>
            Option 1: {{answer1.answer}}<br>
            <button>Vote Option 1</button><br>
            Option 2: {{answer2.answer}}
            <button>Vote Option 2</button>
        </div>
        <div v-else>
            Submitted, waiting...
        </div>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: 'Voting',
    props: {
        gameIdProp: Number,
        nameProp: String
    },
    data() {
        return {
            gameId: null,
            name: null,
            question: null,
            answer1: null,
            answer2: null,
            voted: false,
            phase: "voting"
        }
    },
    methods: {
        getSetAnswers() {
            axios.post(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId + '/questions-to-score')
                .then(response => {
                    this.$data.answer1 = response.data[0]
                    this.$data.answer2 = response.data[1]
                    this.$data.question = this.$data.answer1.question
                })
        },
        voteAnswer1() {
            this.vote(answer1.id)
        },
        voteAnswer2() {
            this.vote(answer2.id)
        },
        vote(answerId) {
            this.$data.voted = true
            var config = {
                headers: {
                    'gameId': this.$data.gameId,
                    'questionAnswerId': answerId
                }
            }
            axios.post(process.env.VUE_APP_BACKEND_BASE_URL + '/name/' + this.$data.name + '/vote', {}, config)
            this.intervalId = setInterval(waitAfterSubmit, 2000)
        },
        waitAfterSubmit() {
            axios.post(process.env.VUE_APP_BACKEND_BASE_URL + '/game/' + this.$data.gameId + '/get-phase')
                .then(response => {
                    if (response.data === "voting" && this.$data.phase !== "voting"){
                        clearInterval(this.intervalId)
                        this.getSetAnswers()
                    } else if (response.data !== "voting" && response.data !== "showing votes"){
                        clearInterval(this.intervalId)
                        this.$emit("next round")
                    }
                    this.$data.phase = response.data
                })
        }
    },
    created() {
        this.$data.gameId = this.gameIdProp
        this.$data.name = this.nameProp
        this.getSetAnswers()
    }
}
</script>

<style scoped>

</style>
