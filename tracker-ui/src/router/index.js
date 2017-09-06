import Vue from 'vue'
import Router from 'vue-router'
import DeviceChoice from '../components/DeviceChoice.vue'
import Device from '../components/Device.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'DeviceChoice',
      component: DeviceChoice
    },

    {
      path: '/device/:id',
      name: 'Device',
      component: Device
    }
  ]
})
