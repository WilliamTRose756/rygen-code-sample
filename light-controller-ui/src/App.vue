<script setup lang="ts">
import { ref, computed, watch, onUnmounted, onMounted, reactive } from 'vue'
import axios from 'axios'
import MainControls from './components/MainControls.vue'
import LightPair from './components/LightPair.vue'
import ConfigModal from './components/ConfigModal.vue'
import type { LightColor, ConfigurationObject } from './types'

const config = reactive<ConfigurationObject>({
  powerOn: true,
  speed: 'medium',
  lightBrightness: { roadA: 2, roadB: 2 },
})

const cloneConfig = (value: ConfigurationObject): ConfigurationObject =>
  JSON.parse(JSON.stringify(value)) as ConfigurationObject

const lastSavedConfig = ref<ConfigurationObject>(cloneConfig(config))
const isSaving = ref(false)
const saveError = ref<string | null>(null)
const saveSuccess = ref<string | null>(null)
let saveSuccessTimer: number | undefined
const showConfigModal = ref(false)
const modalConfig = ref<ConfigurationObject | null>(null)
const modalLoading = ref(false)
const modalError = ref<string | null>(null)
const hasUnsavedChanges = computed(
  () => JSON.stringify(cloneConfig(config)) !== JSON.stringify(lastSavedConfig.value),
)

const saveConfig = async () => {
  isSaving.value = true
  saveError.value = null
  saveSuccess.value = null
  try {
    const res = await axios.put('http://localhost:8080/config', config)
    if (res.status < 200 || res.status >= 300) {
      throw new Error(`Save failed with status ${res.status}`)
    }
    lastSavedConfig.value = cloneConfig(config)
    saveSuccess.value = 'Settings saved.'
    if (saveSuccessTimer !== undefined) {
      window.clearTimeout(saveSuccessTimer)
    }
    saveSuccessTimer = window.setTimeout(() => {
      saveSuccess.value = null
      saveSuccessTimer = undefined
    }, 1500)
  } catch (error) {
    Object.assign(config, cloneConfig(lastSavedConfig.value))
    saveError.value = 'Save failed. Reverted to last saved configuration.'
    console.error(error)
  } finally {
    isSaving.value = false
  }
}

const openConfigModal = async () => {
  showConfigModal.value = true
  modalLoading.value = true
  modalError.value = null
  try {
    const res = await axios.get('http://localhost:8080/config')
    modalConfig.value = res.data
  } catch (error) {
    modalError.value = 'Could not load current configuration.'
    console.error(error)
  } finally {
    modalLoading.value = false
  }
}

const closeConfigModal = () => {
  showConfigModal.value = false
}

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/config')
    Object.assign(config, res.data)
    lastSavedConfig.value = cloneConfig(config)
  } catch (error) {
    console.log(error)
  }
  window.addEventListener('beforeunload', handleBeforeUnload)
})

const powerOn = computed({
  get: () => config.powerOn,
  set: (v: boolean) => {
    config.powerOn = v
  },
})

const speedSetting = computed({
  get: () => (config.speed === 'slow' ? 1 : config.speed === 'medium' ? 2 : 3),
  set: (v: number) => {
    config.speed = v <= 1 ? 'slow' : v === 2 ? 'medium' : 'fast'
  },
})

const colors = ['green', 'yellow', 'red'] as const
const activeLight = ref<LightColor>('green')
const activeLightTwo = ref<LightColor>('red')
const powerLabel = computed(() => (powerOn.value ? 'ON' : 'OFF'))

type SpeedKey = 'slow' | 'medium' | 'fast'
// const speedSetting = ref<number>(2)
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

watch(
  powerOn,
  (isOn) => {
    if (isOn) startCycle()
    else stopCycle()
  },
  { immediate: true },
)

onUnmounted(() => {
  stopCycle()
  window.removeEventListener('beforeunload', handleBeforeUnload)
  if (saveSuccessTimer !== undefined) {
    window.clearTimeout(saveSuccessTimer)
    saveSuccessTimer = undefined
  }
})

// scale remaing time, keep in sync, and maintain timing ratio
watch(speedKey, () => {
  const newFactor = currentSpeedFactor()
  const ratio = newFactor / lastSpeedFactor.value
  remainingLightOne.value = Math.max(1, Math.round(remainingLightOne.value * ratio))
  remainingLightTwo.value = Math.max(1, Math.round(remainingLightTwo.value * ratio))
  lastSpeedFactor.value = newFactor
})

watch(
  config,
  () => {
    if (saveSuccess.value) saveSuccess.value = null
  },
  { deep: true },
)

const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  if (!hasUnsavedChanges.value) return
  event.preventDefault()
  event.returnValue = ''
}
</script>

<template>
  <header>
    <div class="wrapper">
      <h1>Intersection Light Controller</h1>
    </div>
  </header>

  <main>
    <div class="current-config-control">
      <button type="button" class="current-config-button" @click="openConfigModal">
        Current Saved Configuration
      </button>
    </div>

    <div class="main-controls">
      <MainControls
        v-model:powerOn="powerOn"
        v-model:speedSetting="speedSetting"
        :powerLabel="powerLabel"
      />
    </div>

    <div class="light-controllers-div">
      <label class="light-controllers-label">Lights:</label>
      <div class="lights-box">
        <LightPair
          title="Light Pair One"
          groupName="light"
          :powerOn="powerOn"
          :brightness="config.lightBrightness.roadA"
          :activeLight="activeLight"
          @update:brightness="(v) => (config.lightBrightness.roadA = v)"
          @update:activeLight="(v) => (activeLight = v)"
        />
        <LightPair
          title="Light Pair Two"
          groupName="lightTwo"
          :powerOn="powerOn"
          :brightness="config.lightBrightness.roadB"
          :activeLight="activeLightTwo"
          @update:brightness="(v) => (config.lightBrightness.roadB = v)"
          @update:activeLight="(v) => (activeLightTwo = v)"
        />
      </div>
    </div>

    <div class="save-control">
      <button
        type="button"
        class="save-button"
        :disabled="isSaving || !hasUnsavedChanges"
        @click="saveConfig"
      >
        {{ isSaving ? 'Saving...' : 'Save Settings' }}
      </button>
      <p v-if="hasUnsavedChanges" class="unsaved-warning">Unsaved changes</p>
      <p v-if="saveSuccess" class="save-success">{{ saveSuccess }}</p>
      <p v-if="saveError" class="save-error">{{ saveError }}</p>
    </div>

    <div class="config-modal">
      <ConfigModal
        v-if="showConfigModal"
        :loading="modalLoading"
        :error="modalError"
        :config="modalConfig"
        @close="closeConfigModal"
      />
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

@media (min-width: 1024px) {
  header {
    margin: calc(var(--section-gap) / 4);
  }

  .wrapper {
    text-align: center;
  }
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

.light-controllers-label {
  font-family: inherit;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 5%;
}

.lights-box {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

/* Current config button */
.current-config-control {
  display: flex;
  align-items: center;
  flex-direction: column;
  margin-bottom: 3%;
}

.current-config-button {
  padding: 0.6rem 1.2rem;
  font-size: 0.95rem;
  font-weight: 600;
  border: 1px solid #d0d7de;
  border-radius: 8px;
  background: #f9fbfc;
  color: #2c3e50;
  cursor: pointer;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease,
    background 0.15s ease;
}

.current-config-button:hover {
  transform: translateY(-1px);
  background: #eef2f5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* Save button */
.save-control {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  margin-top: 5%;
}

.save-button {
  padding: 0.9rem 2.2rem;
  font-size: 1rem;
  font-weight: 600;
  border: none;
  border-radius: 10px;
  background: #2dc937;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 6px 16px rgba(45, 201, 55, 0.25);
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease,
    background 0.15s ease;
}

.save-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(45, 201, 55, 0.3);
  background: #27b430;
}

.save-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.save-error {
  color: #c0392b;
  margin-top: 0.5rem;
  font-size: 0.9rem;
}

.save-success {
  color: #1e7e34;
  background: #e7f6ec;
  border: 1px solid #bfe7cb;
  border-radius: 999px;
  padding: 0.35rem 0.9rem;
  font-size: 0.85rem;
  font-weight: 600;
}

.unsaved-warning {
  color: #7a4e00;
  background: #fff4e5;
  border: 1px solid #f3d3a4;
  border-radius: 999px;
  padding: 0.35rem 0.9rem;
  font-size: 0.85rem;
  font-weight: 600;
}
</style>
