<script setup lang="ts">
type SpeedSetting = 'slow' | 'medium' | 'fast'
type LightPairId = 'roadA' | 'roadB'

type ConfigurationObject = {
  powerOn: boolean
  speed: SpeedSetting
  lightBrightness: Record<LightPairId, number>
}

const props = defineProps<{
  loading: boolean
  error: string | null
  config: ConfigurationObject | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()
</script>

<template>
  <div class="modal-backdrop" @click.self="emit('close')">
    <div class="modal-card">
      <div class="modal-header">
        <h2>Current Saved Configuration</h2>
        <button type="button" class="modal-close" @click="emit('close')">×</button>
      </div>
      <div class="modal-body">
        <p v-if="props.loading">Loading...</p>
        <p v-else-if="props.error" class="save-error">{{ props.error }}</p>
        <div v-else-if="props.config" class="modal-grid">
          <div>
            <span class="modal-label">Power</span>
            <span>{{ props.config.powerOn ? 'ON' : 'OFF' }}</span>
          </div>
          <div>
            <span class="modal-label">Speed</span>
            <span>{{ props.config.speed }}</span>
          </div>
          <div>
            <span class="modal-label">Road A Brightness</span>
            <span>{{ props.config.lightBrightness.roadA }}</span>
          </div>
          <div>
            <span class="modal-label">Road B Brightness</span>
            <span>{{ props.config.lightBrightness.roadB }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Modal */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: grid;
  place-items: center;
  z-index: 50;
}

.modal-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 22px;
  width: min(420px, 90vw);
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.18),
    0 2px 6px rgba(0, 0, 0, 0.12);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.modal-header h2 {
  font-size: 1.1rem;
  margin: 0;
}

.modal-close {
  border: none;
  background: transparent;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
  color: #666;
}

.modal-body {
  display: grid;
  gap: 10px;
}

.modal-grid {
  display: grid;
  gap: 10px;
}

.modal-grid > div {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.modal-label {
  font-weight: 600;
  color: #4a5568;
}
</style>
