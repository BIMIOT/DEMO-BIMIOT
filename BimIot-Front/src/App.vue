<template>
    <div id="app">
        <Model></Model>
    </div>
</template>

<script>
import Model from './components/Model.vue'

export default {
    name: 'app',
    components: {
        Model
    },
    created() {
        document.title = 'Vue IFC.js Example'
    }
}
</script>

<style>

html {
    overflow-y: hidden !important;
}

#app {
    margin: 0;
    padding: 0;
    border: 0;
    height: 100%;
}

</style>
