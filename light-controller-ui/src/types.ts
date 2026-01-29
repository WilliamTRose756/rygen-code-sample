export type SpeedSetting = 'slow' | 'medium' | 'fast'
export type LightPairId = 'roadA' | 'roadB'
export type LightColor = 'green' | 'yellow' | 'red'

export type ConfigurationObject = {
  powerOn: boolean
  speed: SpeedSetting
  lightBrightness: Record<LightPairId, number> // 1-3
}
