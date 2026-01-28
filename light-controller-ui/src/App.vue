<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue'
import axios from 'axios'

const colors = ['green', 'yellow', 'red'] as const
type LightColor = (typeof colors)[number]

const activeLight = ref<LightColor>('green')
const activeLightTwo = ref<LightColor>('red')

const powerOn = ref(false)
const powerLabel = computed(() => (powerOn.value ? 'ON' : 'OFF'))

const stoplightConfiguration: Record<LightColor, number> = {
  "green":4,
  "yellow": 2,
  "red": 6
}

const remainingLightOne = ref<number>(stoplightConfiguration[activeLight.value])
const remainingLightTwo = ref<number>(stoplightConfiguration[activeLightTwo.value])

const tickMs = 1000
let cycleTimer: number | undefined

// Iterate over  light cycle
const advanceLight = (current: LightColor) => {
  const currentIndex = colors.indexOf(current)
  const nextIndex = (currentIndex + 1) % colors.length
  return colors[nextIndex]
}

const tickLights = () => {
 remainingLightOne.value -= 1
 if (remainingLightOne.value <=0) {
  activeLight.value = advanceLight(activeLight.value)
  remainingLightOne.value = stoplightConfiguration[activeLight.value]
 }

 remainingLightTwo.value -= 1
 if (remainingLightTwo.value <=0) {
  activeLightTwo.value = advanceLight(activeLightTwo.value)
  remainingLightTwo.value = stoplightConfiguration[activeLightTwo.value]
 }
}

// call tickLights() every tick to update remaining time
const startCycle = () => {
if (cycleTimer !== undefined) return
cycleTimer = window.setInterval(tickLights, tickMs)

}

// clears  interval to stop, then reset cycleTimer
const stopCycle = () => {
  if (cycleTimer === undefined) return
  window.clearInterval(cycleTimer)
  cycleTimer = undefined
}

const handleActiveLightChange = () => {
  if (!powerOn.value) return

  axios
    .post('http://localhost:8080/intersections', { activeLight: activeLight.value })
    .then(console.log)
    .catch(console.error)
}

const handleActiveLightChangeTwo = () => {
  return
}

watch(powerOn, (isOn) => {
  if (isOn) {
    startCycle()
    return
  }
  stopCycle()
})

onUnmounted(() => {
  stopCycle()
})
</script>

<template>
  <header>
    <div class="wrapper">
      <h1>Intersection Light Controller</h1>
    </div>
  </header>

  <main>
    <div class="power-control">
      <label class="switch">
        <input type="checkbox" v-model="powerOn" />
        <span class="slider"></span>
      </label>
      <span class="power-label">Power: {{ powerLabel }}</span>
    </div>
    <div class="light-controllers-div">
      <div class="light-controller">
        <p> Light One</p>
        <div class="light">
          <label>
            <input type="radio" value="red" class="red" v-model="activeLight" name="light"
              @change="handleActiveLightChange" :disabled="!powerOn" /> Red
          </label>
          <label>
            <input type="radio" value="yellow" class="yellow" v-model="activeLight" name="light"
              @change="handleActiveLightChange" :disabled="!powerOn" /> Yellow
          </label>
          <label>
            <input type="radio" value="green" class="green" v-model="activeLight" name="light"
              @change="handleActiveLightChange" :disabled="!powerOn" /> Green
          </label>
        </div>
      </div>

      <div class="light-two-controller">
        <p>Light Two</p>
        <div class="light-two">
          <label>
            <input type="radio" value="red" class="red" v-model="activeLightTwo" name="lightTwo"
              @change="handleActiveLightChangeTwo" :disabled="!powerOn" /> Red
          </label>
          <label>
            <input type="radio" value="yellow" class="yellow" v-model="activeLightTwo" name="lightTwo"
              @change="handleActiveLightChangeTwo" :disabled="!powerOn" /> Yellow
          </label>
          <label>
            <input type="radio" value="green" class="green" v-model="activeLightTwo" name="lightTwo"
              @change="handleActiveLightChangeTwo" :disabled="!powerOn" /> Green
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


.power-control {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.power-label {
  font-weight: 600;
}

.switch {
  position: relative;
  display: inline-block;
  width: 56px;
  height: 28px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #c7c7c7;
  transition: 0.2s;
  border-radius: 999px;
}

.slider::before {
  position: absolute;
  content: "";
  height: 22px;
  width: 22px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.2s;
  border-radius: 50%;
}

.switch input:checked + .slider {
  background-color: #2dc937;
}

.switch input:checked + .slider::before {
  transform: translateX(28px);
}
</style>
