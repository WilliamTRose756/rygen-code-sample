<script setup lang="ts">
import { toRefs } from 'vue'
const props = defineProps<{
  title: string
  groupName: string
  powerOn: boolean
  brightness: number
  activeLight: 'red' | 'yellow' | 'green'
}>()

const { title, groupName, powerOn, brightness, activeLight } = toRefs(props)

const emit = defineEmits<{
  (e: 'update:brightness', value: number): void
  (e: 'update:activeLight', value: 'red' | 'yellow' | 'green'): void
}>()

const brightnessToOpacity = (level: number) => {
  if (level <= 1) return 0.4
  if (level === 2) return 0.7
  return 1
}
</script>

<template>
  <div class="light-column">
    <div class="brightness-control">
      <label>
        Brightness (1–3):
        <input
          type="range"
          min="1"
          max="3"
          step="1"
          :value="brightness"
          @input="emit('update:brightness', Number(($event.target as HTMLInputElement).value))"
        />
      </label>
    </div>
    <div class="light-controller">
      <p>{{ title }}</p>
      <div class="light">
        <label>
          <input
            :style="{
              opacity: powerOn && activeLight === 'red' ? brightnessToOpacity(brightness) : 1,
            }"
            type="radio"
            value="red"
            class="red"
            :checked="activeLight === 'red'"
            :name="groupName"
            @change="emit('update:activeLight', 'red')"
            :disabled="!powerOn"
            @click.prevent
          />
          Red
        </label>
        <label>
          <input
            :style="{
              opacity: powerOn && activeLight === 'yellow' ? brightnessToOpacity(brightness) : 1,
            }"
            type="radio"
            value="yellow"
            class="yellow"
            :checked="activeLight === 'yellow'"
            :name="groupName"
            @change="emit('update:activeLight', 'yellow')"
            :disabled="!powerOn"
            @click.prevent
          />
          Yellow
        </label>
        <label>
          <input
            class="radio-brightness-green"
            :style="{
              opacity: powerOn && activeLight === 'green' ? brightnessToOpacity(brightness) : 1,
            }"
            type="radio"
            value="green"
            :checked="activeLight === 'green'"
            :name="groupName"
            @change="emit('update:activeLight', 'green')"
            :disabled="!powerOn"
            @click.prevent
          />
          Green
        </label>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Lights */

.light-column {
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

.light-controller p {
  font-weight: 600;
}

.light label {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  line-height: 1.1;
}

input[type='radio'] {
  margin: 0;
  transform: scale(1.3);
  transform-origin: center;
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
</style>
