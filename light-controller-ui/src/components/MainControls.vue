<script setup lang="ts">
const props = defineProps<{
  powerOn: boolean
  speedSetting: number
  powerLabel: string
}>()

const emit = defineEmits<{
  (e: 'update:powerOn', value: boolean): void
  (e: 'update:speedSetting', value: number): void
}>()
</script>

<template>
  <div class="my-component">
    <div class="main-controls">
      <label class="main-controls-label" style="margin-bottom: 5%" for="main-controls"
        >Main Controls:</label
      >
      <div class="main-controls-box">
        <div class="power-control">
          <label class="switch">
            <input
              type="checkbox"
              :checked="props.powerOn"
              @change="emit('update:powerOn', ($event.target as HTMLInputElement).checked)"
            />
            <span class="slider"></span>
          </label>
          <span class="power-label">Power: {{ props.powerLabel }}</span>
        </div>
        <div class="speed-control">
          <div class="speed-control">
            <label>
              Speed (Slow, Medium, Fast)
              <input
                type="range"
                min="1"
                max="3"
                step="1"
                :value="props.speedSetting"
                @input="
                  emit('update:speedSetting', Number(($event.target as HTMLInputElement).value))
                "
              />
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
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
