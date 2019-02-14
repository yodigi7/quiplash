<template>
    <div>
        Voting:
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: 'Voting',
    props: {
        gameIdProp: Number
    },
    data() {
        return{
            question1: null,
            question2: null,
            gameId: null
        }
    },
    mounted() {
        this.$data.gameId = this.gameIdProp
    }
}
</script>

<style scoped>

</style>
