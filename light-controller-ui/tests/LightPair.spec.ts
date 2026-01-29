import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import LightPair from '../src/components/LightPair.vue'

describe('LightPair', () => {
  it('emits update:activeLight when a radio is selected', async () => {
    const wrapper = mount(LightPair, {
      props: {
        title: 'Light Pair One',
        groupName: 'light',
        powerOn: true,
        brightness: 2,
        activeLight: 'red',
      },
    })

    const yellow = wrapper.find('input[type="radio"][value="yellow"]')
    await yellow.setValue()

    expect(wrapper.emitted('update:activeLight')).toEqual([['yellow']])
  })

  it('emits update:brightness when the slider changes', async () => {
    const wrapper = mount(LightPair, {
      props: {
        title: 'Light Pair One',
        groupName: 'light',
        powerOn: true,
        brightness: 2,
        activeLight: 'red',
      },
    })

    const slider = wrapper.find('input[type="range"]')
    await slider.setValue('1')

    expect(wrapper.emitted('update:brightness')).toEqual([[1]])
  })
})
