import { test } from 'vitest'

import { mount } from '@vue/test-utils';
import App from "../../App.vue";

test('render the play button',async () => {
    const wrapper = mount(App);
    const button = wrapper.find('play');
    expect(button)

})

test('render the stop button',async () => {
    const wrapper = mount(App);
    const button = wrapper.find('stop');
    expect(button)

})