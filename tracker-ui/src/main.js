// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import VueResource from 'vue-resource'
import BootstrapVue from 'bootstrap-vue'
import * as VueGoogleMaps from 'vue2-google-maps'
import LoadScript from 'vue-plugin-load-script'
import App from './App'
import router from './router'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.use(VueResource)
Vue.use(BootstrapVue)
Vue.use(require('vue-moment'))
Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyBeXet0VdnoCPjoIZLTktl938qivzfqmNE',
    libraries: 'places,drawing,visualization'
  }
})
Vue.use(LoadScript)

Vue.config.productionTip = false

Vue.http.options.root = 'https://api.tracker.ivkos.com'

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
