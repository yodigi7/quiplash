<template>
    <div>
        <div v-if="currentQuestionIndex < questions.length">
            Current question:<br>
            {{currentQuestion}}
            <input v-model="answer"><br>
            <button v-on:click="submitAnswer">Submit</button>
        </div>
        <div v-else>
            Waiting for others to finish up...
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'AnswerQuestions',
    props: {
        nameProp: String,
        gameIdProp: Number
    },
    data() {
        return {
            gameId: null,
            name: null,
            questions: [],
            answer: null,
            answers: [],
            currentQuestion: null,
            currentQuestionIndex: 0,
            gameState: null
        }
    },
    methods: {
        getSetQuestions() {
            var config = {
                headers: {
                    'gameId': this.$data.gameId
                }
            }
            axios.post(process.env.VUE_APP_BACKEND_BASE_URL + '/name/' + this.$data.name + '/get-questions', {}, config)
                .then(response => {
                    console.log(response.data)
                    var holderQuestions = []
                    for (var index = 0; index < response.data.length; index++) {
                        holderQuestions.push(response.data[index].question)
                    }
                    console.log(holderQuestions)
                    this.$data.questions = holderQuestions;
                    this.$data.currentQuestion = holderQuestions[0]
                })
        },
        submitAnswer() {
            if (this.$data.currentQuestionIndex + 1 < this.$data.questions.length){
                this.$data.currentQuestion = this.$data.questions[this.$data.currentQuestionIndex + 1]
            }
            this.$data.answers.push(this.$data.answer)
            this.$data.answer = null
            this.$data.currentQuestionIndex += 1
        },
        waitForAnswers() {
            if (this.$data.gameState === 'voting') {
                clearInterval(this.intervalId)
                this.$emit('startVoting')
            }
        }
    },
    created() {
        this.$data.gameId = this.gameIdProp
        this.$data.name = this.nameProp
        this.intervalId = setInterval(this.waitForAnswers, 2000)
        this.getSetQuestions()
    }
}
</script>

<style scoped>

</style>
