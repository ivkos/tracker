<template>
  <div class="device-list-group">
    <b-list-group ref="list-group">
      <b-list-group-item :to="{ name: 'device', params: { id: device.id }}"
                         v-for="device in devices"
                         :key="device.id"
                         v-if="device.lastSeen"
      >
        <div class="d-flex w-100 justify-content-between">
          <h6 v-if="concise" class="mb-1">{{device.name}}</h6>
          <h5 v-else class="mb-1">{{device.name}}</h5>

          <small>
            <template v-if="!device.active">
              <span class="d-none d-sm-inline" v-if="!concise">активно</span>
              <span :title="device.lastSeen | moment('LLLL')">{{device.lastSeen | moment("from") }}</span>
            </template>
            <span :class="device.active ? 'bullet-active' : 'bullet-inactive'">●</span>
          </small>
        </div>

        <template v-if="!concise">
          <small>
            Регистрирано
            <span :title="device.dateCreated | moment('LLLL')">{{device.dateCreated | moment("from")}}</span>
          </small>
          <br>
          <small>ID: <code>{{device.id}}</code></small>
        </template>
      </b-list-group-item>
    </b-list-group>
  </div>
</template>

<script>
  import moment from 'moment'

  export default {
    name: 'device-list-group',
    props: ['concise'],

    data () {
      return {
        devices: [],
        updateIntervalObj: undefined
      }
    },

    methods: {
      getDevices: function () {
        this.$http.get('devices').then(res => {
          this.devices = res.data.map(d => {
            d.active = moment(d.lastSeen).diff() >= -120 * 1000
            return d
          })
        })
      }
    },

    created: function () {
      this.getDevices()
      this.updateIntervalObj = setInterval(() => this.getDevices(), 5000)
    },

    beforeDestroy: function () {
      clearInterval(this.updateIntervalObj)
    }
  }
</script>

<style scoped>
  .bullet-active {
    color: #00e234
  }

  .bullet-inactive {
    color: #ff6060
  }
</style>
