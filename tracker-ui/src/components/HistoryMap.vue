<template>
  <div class="history-map">
    <div class="form-group" id="rangeFormGroup">
      <label class="form-control-label" for="rangeInput">Изберете период:</label>
      <b-form-select v-model="selectedRange" :options="ranges" id="rangeInput"></b-form-select>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue'
  import moment from 'moment'

  export default {
    name: 'history-map',
    props: ['deviceId'],

    data () {
      return {
        selectedRange: null,
        ranges: [{
          value: null,
          text: 'Зареждане...',
          disabled: true
        }],

        currentPosition: {
          lat: 46.936742,
          lng: 16.7217174,
          _default: true
        },
        zoom: 5,

        points: [],

        geolocationMarker: undefined,

        infoContent: '',
        infoWindowPos: {
          lat: 0,
          lng: 0
        },
        infoWinOpen: false,
        currentMidx: null,
        infoOptions: {
          pixelOffset: {
            width: 0,
            height: -35
          }
        }
      }
    },

    methods: {
      toggleInfoWindow: function (marker, idx) {
        this.infoWindowPos = marker.position
        this.infoContent = marker.infoText

        // check if its the same marker that was selected if yes toggle
        if (this.currentMidx === idx) {
          this.infoWinOpen = !this.infoWinOpen
        } else {
          // if different marker set infowindow to open and reset current marker index
          this.infoWinOpen = true
          this.currentMidx = idx
        }
      },

      getRanges: function () {
        this.$http.get('history/ranges', {
          headers: {
            'X-Device-Id': this.deviceId
          }
        })
        .then(res => {
          this.ranges = res.data.map(range => ({
            value: range,
            text: `${moment(range.from).format('DD MMM HH:mm')} → ${moment(range.to).format('DD MMM HH:mm')}`
          }))

          this.ranges.reverse()

          this.ranges.unshift({
            value: null,
            text: 'Моля изберете',
            disabled: true
          })

        })
      }
    },

    created: function () {
      this.getRanges()
    },

    mounted: function () {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(position => {
          Vue.loadScript(
            '/static/geolocation-marker.js')
          .then(() => {
            this.geolocationMarker = new GeolocationMarker(this.$refs.map.$mapObject)
            this.geolocationMarker.setCircleOptions({
              fillColor: '#5385e8',
              strokeColor: '#0e3fa0'
            })
          })

          if (this.currentPosition._default) {
            this.currentPosition = {
              lat: position.coords.latitude,
              lng: position.coords.longitude
            }
            this.zoom = 15
          }
        })
      }
    },

    watch: {
      deviceId: function () {
        this.getLatestLocation(true, true)
      }
    },

    beforeDestroy: function () {
      clearInterval(this.updateIntervalObj)
      this.geolocationMarker.setMap(null)
    }
  }
</script>

<style scoped>
  #rangeFormGroup {
    width: 30%;
    min-width: 256px;
  }

  .history-map, #map {
    height: 100%;
    margin: 0;
    padding: 0;
  }

  strong.info-label {
    font-weight: bold;
  }
</style>
