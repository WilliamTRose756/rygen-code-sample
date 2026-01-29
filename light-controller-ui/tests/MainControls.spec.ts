import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import MainControls from '../src/components/MainControls.vue'

describe('MainControls', () => {
  it('emits update:powerOn when toggled', async () => {
    const wrapper = mount(MainControls, {
      props: {
        powerOn: false,
        speedSetting: 2,
        powerLabel: 'OFF',
      },
    })

    const checkbox = wrapper.find('input[type="checkbox"]')
    await checkbox.setValue(true)

    expect(wrapper.emitted('update:powerOn')).toEqual([[true]])
  })

  it('emits update:speedSetting when slider changes', async () => {
    const wrapper = mount(MainControls, {
      props: {
        powerOn: true,
        speedSetting: 2,
        powerLabel: 'ON',
      },
    })

    const slider = wrapper.find('input[type="range"]')
    await slider.setValue('3')

    expect(wrapper.emitted('update:speedSetting')).toEqual([[3]])
  })
})
