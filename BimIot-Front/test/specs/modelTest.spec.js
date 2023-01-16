import { mount } from '@vue/test-utils';
import Model from "@/components/Model";

test('render the play button',async () => {
    const wrapper = mount(Model);
    const button = wrapper.find('v-btn-play');
    expect(button)

})

test('render the stop button',async () => {
    const wrapper = mount(Model);
    const button = wrapper.find('v-btn-stop');
    expect(button)

})

