<script setup lang="ts">
import { ref, computed, watch, onUnmounted, reactive } from 'vue'
import axios from 'axios'

const colors = ['green', 'yellow', 'red'] as const
const lightPairConfig = reactive<Record<LightPairId, LightPairConfig>>({
  roadA: { brightness: 2 },
  roadB: { brightness: 2 },
})
const activeLight = ref<LightColor>('green')
const activeLightTwo = ref<LightColor>('red')
const powerOn = ref(false)
const powerLabel = computed(() => (powerOn.value ? 'ON' : 'OFF'))

type SpeedKey = 'slow' | 'medium' | 'fast'
const speedSetting = ref<number>(2)
const speedConfig: Record<SpeedKey, number> = {
  slow: 2,
  medium: 1,
  fast: 0.5,
}
const speedKey = computed<SpeedKey>(() => {
  if (speedSetting.value <= 1) return 'slow'
  if (speedSetting.value === 2) return 'medium'
  return 'fast'
})
const currentSpeedFactor = () => speedConfig[speedKey.value]
const lastSpeedFactor = ref<number>(currentSpeedFactor())

const stoplightConfiguration: Record<LightColor, number> = {
  green: 4,
  yellow: 2,
  red: 6,
}

const getDurationSeconds = (color: LightColor) =>
  Math.max(1, Math.round(stoplightConfiguration[color] * speedConfig[speedKey.value]))

const remainingLightOne = ref<number>(getDurationSeconds(activeLight.value))
const remainingLightTwo = ref<number>(getDurationSeconds(activeLightTwo.value))
const tickMs = 1000
let cycleTimer: number | undefined

type LightColor = (typeof colors)[number]
type LightPairId = 'roadA' | 'roadB'
type LightPairConfig = {
  brightness: number // 1-3
}

const brightnessToOpacity = (level: number) => {
  if (level <= 1) return 0.4
  if (level === 2) return 0.7
  return 1
}
// Iterate over  light cycle
const advanceLight = (current: LightColor) => {
  const currentIndex = colors.indexOf(current)
  const nextIndex = (currentIndex + 1) % colors.length
  return colors[nextIndex]
}

// decrement
const tickLights = () => {
  remainingLightOne.value -= 1
  if (remainingLightOne.value <= 0) {
    activeLight.value = advanceLight(activeLight.value)
    remainingLightOne.value = getDurationSeconds(activeLight.value)
  }

  remainingLightTwo.value -= 1
  if (remainingLightTwo.value <= 0) {
    activeLightTwo.value = advanceLight(activeLightTwo.value)
    remainingLightTwo.value = getDurationSeconds(activeLightTwo.value)
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

watch(speedKey, () => {
  const newFactor = currentSpeedFactor()
  const ratio = newFactor / lastSpeedFactor.value
  remainingLightOne.value = Math.max(1, Math.round(remainingLightOne.value * ratio))
  remainingLightTwo.value = Math.max(1, Math.round(remainingLightTwo.value * ratio))
  lastSpeedFactor.value = newFactor
})
</script>

<template>
  <header>
    <div class="wrapper">
      <h1>Intersection Light Controller</h1>
    </div>
  </header>

  <main>
    <div class="main-controls">
      <label class="main-controls-label" style="margin-bottom: 5%" for="main-controls"
        >Main Controls:</label
      >
      <div class="main-controls-box">
        <div class="power-control">
          <label class="switch">
            <input type="checkbox" v-model="powerOn" />
            <span class="slider"></span>
          </label>
          <span class="power-label">Power: {{ powerLabel }}</span>
        </div>
        <div class="speed-control">
          <div class="speed-control">
            <label>
              Speed (Slow, Medium, Fast)
              <input type="range" min="1" max="3" step="1" v-model.number="speedSetting" />
            </label>
          </div>
        </div>
      </div>
    </div>
    <div class="light-controllers-div">
      <label class="main-controls-label">Lights:</label>
      <div class="lights-box">
        <div class="light-column">
          <div class="brightness-control">
            <label>
              Brightness (1–3):
              <input
                type="range"
                min="1"
                max="3"
                step="1"
                v-model.number="lightPairConfig.roadA.brightness"
              />
            </label>
          </div>
          <div class="light-controller">
            <p>Light Pair One</p>
            <div class="light">
              <label>
                <input
                  :style="{
                    opacity:
                      powerOn && activeLight === 'red'
                        ? brightnessToOpacity(lightPairConfig.roadA.brightness)
                        : 1,
                  }"
                  type="radio"
                  value="red"
                  class="red"
                  v-model="activeLight"
                  name="light"
                  @change="handleActiveLightChange"
                  :disabled="!powerOn"
                  @click.prevent
                />
                Red
              </label>
              <label>
                <input
                  :style="{
                    opacity:
                      powerOn && activeLight === 'yellow'
                        ? brightnessToOpacity(lightPairConfig.roadA.brightness)
                        : 1,
                  }"
                  type="radio"
                  value="yellow"
                  class="yellow"
                  v-model="activeLight"
                  name="light"
                  @change="handleActiveLightChange"
                  :disabled="!powerOn"
                  @click.prevent
                />
                Yellow
              </label>
              <label>
                <input
                  class="radio-brightness-green"
                  :style="{
                    opacity:
                      powerOn && activeLight === 'green'
                        ? brightnessToOpacity(lightPairConfig.roadA.brightness)
                        : 1,
                  }"
                  type="radio"
                  value="green"
                  v-model="activeLight"
                  name="light"
                  @change="handleActiveLightChange"
                  :disabled="!powerOn"
                  @click.prevent
                />
                Green
              </label>
            </div>
          </div>
        </div>
        <div></div>
        <div class="light-column">
          <div class="brightness-control">
            <label>
              Brightness (1–3):
              <input
                type="range"
                min="1"
                max="3"
                step="1"
                v-model.number="lightPairConfig.roadB.brightness"
              />
            </label>
          </div>
          <div class="light-two-controller">
            <p>Light Pair Two</p>
            <div class="light-two">
              <label>
                <input
                  :style="{
                    opacity:
                      powerOn && activeLightTwo === 'red'
                        ? brightnessToOpacity(lightPairConfig.roadB.brightness)
                        : 1,
                  }"
                  type="radio"
                  value="red"
                  class="red"
                  v-model="activeLightTwo"
                  name="lightTwo"
                  @change="handleActiveLightChange"
                  :disabled="!powerOn"
                  @click.prevent
                />
                Red
              </label>
              <label>
                <input
                  :style="{
                    opacity:
                      powerOn && activeLightTwo === 'yellow'
                        ? brightnessToOpacity(lightPairConfig.roadB.brightness)
                        : 1,
                  }"
                  type="radio"
                  value="yellow"
                  class="yellow"
                  v-model="activeLightTwo"
                  name="lightTwo"
                  @change="handleActiveLightChange"
                  :disabled="!powerOn"
                  @click.prevent
                />
                Yellow
              </label>
              <label>
                <input
                  class="radio-brightness-green"
                  :style="{
                    opacity:
                      powerOn && activeLightTwo === 'green'
                        ? brightnessToOpacity(lightPairConfig.roadB.brightness)
                        : 1,
                  }"
                  type="radio"
                  value="green"
                  v-model="activeLightTwo"
                  name="lightTwo"
                  @change="handleActiveLightChange"
                  :disabled="!powerOn"
                  @click.prevent
                />
                Green
              </label>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
header {
  line-height: 1.5;
}
.wrapper {
  text-align: center;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    margin: calc(var(--section-gap) / 4);
  }

  .wrapper {
    text-align: center;
  }
}

/* Main Controls Box */
.main-controls {
  display: flex;
  flex-direction: column;
  margin-bottom: 10%;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.12),
    0 1px 2px rgba(0, 0, 0, 0.24);
}
.main-controls-label {
  font-family: inherit;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 5%;
}

.main-controls-box {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

/* Power Switch */
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
  content: '';
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

/* Lights Box */
.light-controllers-div {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.12),
    0 1px 2px rgba(0, 0, 0, 0.24);
}

/* Lights */

.lights-column {
  max-width: 50%;
}
.light-controller {
  display: grid;
  place-items: center;
  gap: 1rem;

  .light {
    display: grid;
    gap: 0.5rem;
  }
}

.light-two-controller {
  display: grid;
  place-items: center;
  gap: 1rem;

  .light-two {
    display: grid;
    gap: 0.5rem;
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

.lights-box {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.radio-brightness-green {
  opacity: var(--o, 1);
  filter: opacity(var(--o, 1));
  accent-color: #2dc937;
}

/* Brightness */
.brightness-control {
  display: grid;
  gap: 0.4rem;
  max-width: 90%;
}

.brightness-control label {
  font-weight: 600;
}

.brightness-control input[type='range'] {
  width: 100%;
  accent-color: #009443;
}

.brightness-control span {
  font-size: 0.9rem;
  color: #666;
}

/* Speed */
.speed-control {
  display: grid;
  gap: 0.4rem;
  width: 220px;
}

.speed-control label {
  font-weight: 600;
}

.speed-control input[type='range'] {
  width: 100%;
  accent-color: #009443;
}

.speed-control span {
  font-size: 0.9rem;
  color: #666;
}
</style>
