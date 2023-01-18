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

<template>
  <div id="app">
    <Model />
  </div>
</template>

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
