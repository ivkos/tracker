import Vue from 'vue'
import Router from 'vue-router'
import Home from '../components/pages/home/Home.vue'
import Device from '../components/pages/device/Device.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },

    {
      path: '/device/:id',
      name: 'device',
      component: Device
    }
  ]
})
