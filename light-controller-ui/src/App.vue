<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
const activeLight = ref<string>('red')

const handleActiveLightChange = () => {

  axios.post('http://localhost:8080/intersections', { activeLight: activeLight.value })
    .then(console.log)
    .catch(console.error)
}

const activeLightTwo = ref('red')

const handleActiveLightChangeTwo = () => {

return
}

</script>

<template>
  <header>
    <div class="wrapper">
      <h1>Intersection Light Controller</h1>
    </div>
  </header>

  <main>
    <div class="light-controllers-div">
      <div class="light-controller">
        <p>Active light One: {{ activeLight }}</p>
        <div class="light">
          <label>
            <input type="radio" value="red" class="red" v-model="activeLight" name="light"
              @change="handleActiveLightChange" /> Red
          </label>
          <label>
            <input type="radio" value="yellow" class="yellow" v-model="activeLight" name="light"
              @change="handleActiveLightChange" /> Yellow
          </label>
          <label>
            <input type="radio" value="green" class="green" v-model="activeLight" name="light"
              @change="handleActiveLightChange" /> Green
          </label>
        </div>
      </div>

      <div class="light-two-controller">
        <p>Active light Two: {{ activeLightTwo }}</p>
        <div class="light-two">
          <label>
            <input type="radio" value="red" class="red" v-model="activeLightTwo" name="lightTwo"
              @change="handleActiveLightChangeTwo" /> Red
          </label>
          <label>
            <input type="radio" value="yellow" class="yellow" v-model="activeLightTwo" name="lightTwo"
              @change="handleActiveLightChangeTwo" /> Yellow
          </label>
          <label>
            <input type="radio" value="green" class="green" v-model="activeLightTwo" name="lightTwo"
              @change="handleActiveLightChangeTwo" /> Green
          </label>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    margin: calc(var(--section-gap) / 4);
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}

.light-controllers-div {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: space-between;
}

.light-controller {
  display: grid;
  place-items: center;
  gap: 1rem;

  .light {
    display: grid;
    gap: .5rem;
  }

}

.light-two-controller {
display: grid;
  place-items: center;
  gap: 1rem;

  .light-two {
    display: grid;
    gap: .5rem;
  }
}

input[type='radio'].red {
  accent-color: #cc3232;
}

input[type='radio'].yellow {
  accent-color: #e7b416;
}

input[type='radio'].green {
  accent-color: #2dc937;
}
</style>
