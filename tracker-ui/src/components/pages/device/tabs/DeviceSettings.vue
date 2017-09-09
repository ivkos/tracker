<template>
  <div id="device-settings">
    <div class="alert-modal">
      <b-modal ok-only v-model="modalVisible"
               id="alert-modal"
               ref="alert-modal"
               title="Заявката е приета">
        Настройката или действието ще влезе в сила до 1 минута, ако устройството е активно.
      </b-modal>
    </div>

    <h5>Предпочитания</h5>
    <form>
      <b-form-group label="Име на устройството:">
        <b-input-group>
          <b-form-input v-model="deviceName"></b-form-input>
          <b-input-group-button slot="right">
            <b-btn variant="outline-primary" @click="setDeviceName(deviceName)" :disabled="setDeviceNameButtonDisabled">
              Запази
            </b-btn>
          </b-input-group-button>
        </b-input-group>
      </b-form-group>
    </form>
    <br>

    <h5>Настройки и управление</h5>
    <form>
      <p class="small text-muted">
        <b-badge pill variant="secondary">i</b-badge>
        Настройките и действията по управление влизат в сила до 1 минута след прилагането им, ако устройството е активно.
        При рестартиране или изключване на устройството, потребителските настройки се заместват с препоръчителните им.
        Стойностите, показани тук, може да не отговарят на актуалните настройки на устройството.
      </p>

      <b-form-group label="Интервал на локиране:">
        <b-input-group>
          <b-form-select v-model="selectedTrackInterval" :options="trackIntervals"></b-form-select>
          <b-input-group-button slot="right">
            <b-button variant="outline-primary" ref="setTrackIntervalButton" @click="setTrackInterval">Запази</b-button>
          </b-input-group-button>
        </b-input-group>

        <b-form-text>
          <b-badge pill variant="secondary">i</b-badge>
          През колко време устройството да запазва информация
          за текущото си местоположение от GPS. Препоръчват се 10 секунди.
        </b-form-text>
      </b-form-group>
      <br>

      <b-form-group label="Интервал на докладване:">
        <b-input-group>
          <b-form-select v-model="selectedReportInterval" :options="reportIntervals"></b-form-select>
          <b-input-group-button slot="right">
            <b-button variant="outline-primary" ref="setReportIntervalButton" @click="setReportInterval">Запази
            </b-button>
          </b-input-group-button>
        </b-input-group>

        <b-form-text>
          <b-badge pill variant="secondary">i</b-badge>
          През колко време устройството да докладва събраната информация за местоположението си. Препоръчват се 2 минути.
        </b-form-text>
      </b-form-group>
      <br>

      <h6>Захранване</h6>
      <div class="form-group">
        <b-button variant="outline-warning" ref="rebootButton" @click="doReboot">Рестартиране</b-button>
        <b-button variant="outline-danger" ref="shutdownButton" @click="doShutdown">Изключване</b-button>

        <b-form-text>
          <b-badge pill variant="warning">!</b-badge>
          Изключването на устройството не дава възможност то да бъде включено отдалечено. За да бъде включено
          отново, трябва захранването му да бъде моментно прекъснато и след това възстановено.
        </b-form-text>
      </div>
    </form>
  </div>
</template>

<script>
  export default {
    name: 'device-settings',
    props: ['deviceId'],

    data () {
      return {
        selectedTrackInterval: 10 * 1000,
        trackIntervals: [
          {value: 5 * 1000, text: '5 секунди'},
          {value: 10 * 1000, text: '10 секунди'},
          {value: 15 * 1000, text: '15 секунди'},
          {value: 30 * 1000, text: '30 секунди'},
          {value: 60 * 1000, text: '1 минута'}
        ],

        selectedReportInterval: 120 * 1000,
        reportIntervals: [
          {value: 5 * 1000, text: '5 секунди'},
          {value: 10 * 1000, text: '10 секунди'},
          {value: 30 * 1000, text: '30 секунди'},
          {value: 60 * 1000, text: '1 минута'},
          {value: 120 * 1000, text: '2 минути'},
          {value: 300 * 1000, text: '5 минути'}
        ],

        setDeviceNameButtonDisabled: false,
        deviceName: undefined,

        modalVisible: false
      }
    },

    methods: {
      updateDeviceName: function () {
        this.$http.get('devices/' + this.deviceId).then(res => {
          this.deviceName = res.data.name
        })
      },

      setDeviceName: function (newName) {
        this.setDeviceNameButtonDisabled = true

        this.$http.patch('devices/' + this.deviceId, {
          name: newName
        }, {responseType: 'json'})
        .then(res => {
          this.setDeviceNameButtonDisabled = false
        })
      },

      sendCommand: function (commandType, args) {
        return this.$http.put('commands', {
          type: commandType,
          arguments: args
        }, {
          headers: {
            'X-Device-Id': this.deviceId
          }
        })
        .then(x => {
          this.modalVisible = true
          return x
        })
      },

      setTrackInterval: function () {
        this.$refs.setTrackIntervalButton.disabled = true
        this.sendCommand('TRACK_INTERVAL', this.selectedTrackInterval)
        .then(() => {
          this.$refs.setTrackIntervalButton.disabled = false
        })
      },

      setReportInterval: function () {
        this.$refs.setReportIntervalButton.disabled = true
        this.sendCommand('REPORT_INTERVAL', this.selectedReportInterval)
        .then(() => {
          this.$refs.setReportIntervalButton.disabled = false
        })
      },

      doReboot: function () {
        this.$refs.rebootButton.disabled = true
        this.sendCommand('REBOOT')
        .then(() => {
          this.$refs.rebootButton.disabled = false
        })
      },

      doShutdown: function () {
        this.$refs.shutdownButton.disabled = true
        this.sendCommand('SHUTDOWN')
        .then(() => {
          this.$refs.shutdownButton.disabled = false
        })
      }
    },

    created: function () {
      this.updateDeviceName()
    },

    mounted: function () {

    },

    watch: {
      deviceId: function () {
        this.updateDeviceName()
      }
    },

    beforeDestroy: function () {

    }
  }
</script>

<style scoped>
  #device-settings {
    max-width: 512px;
  }
</style>
