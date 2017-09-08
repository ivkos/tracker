import Vue from 'vue'
import Router from 'vue-router'
import Home from '../components/Home.vue'
import Device from '../components/Device.vue'

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
      name: 'Device',
      component: Device
    }
  ]
})
