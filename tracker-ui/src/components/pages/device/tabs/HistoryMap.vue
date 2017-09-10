<template>
  <div class="history-map">
    <div class="form-group" id="rangeFormGroup">
      <label class="form-control-label" for="rangeInput">Изберете период:</label>
      <b-form-select v-model="selectedRange" :options="ranges" id="rangeInput"></b-form-select>
    </div>

    <gmap-map id="gmapHistory"
              ref="gmapHistory"
              :center="currentPosition"
              :zoom="zoom">

      <gmap-info-window v-if="infoCtx" :options="infoOptions" :position="infoWindowPos" :opened="infoWinOpen"
                        @closeclick="infoWinOpen=false">
        <strong class="info-label">Обновено {{infoCtx.rawState.satelliteTime | moment("from") }}</strong>.
        <br>
        <strong class="info-label">Време</strong>: {{infoCtx.rawState.satelliteTime | moment('LLLL')}}
        <br><br>

        <strong class="info-label">Координати</strong>:
        {{infoCtx.rawState.location.latitude.toFixed(6)}},
        {{infoCtx.rawState.location.longitude.toFixed(6)}}
        <br>

        <span v-if="infoCtx.rawState.location.altitude">
          <strong class="info-label">Височина</strong>:
          {{infoCtx.rawState.location.altitude.toFixed(2)}} m
          <br>
        </span>

        <strong class="info-label">Прецизност</strong>:
        <span>{{infoCtx.rawState.satelliteCount}} sat.</span>
        <span v-if="infoCtx.rawState.errLat">Lat±{{infoCtx.rawState.errLat.toFixed(0)}} m,</span>
        <span v-if="infoCtx.rawState.errLon">Lon±{{infoCtx.rawState.errLon.toFixed(0)}} m,</span>
        <span v-if="infoCtx.rawState.errAlt">Alt±{{infoCtx.rawState.errAlt.toFixed(0)}} m</span>
        <br><br>

        <strong class="info-label">Скорост</strong>:
        {{ (infoCtx.rawState.speed * 3.6).toFixed(2) }} km/h
        <br>

        <strong class="info-label">Курс</strong>:
        {{ infoCtx.rawState.course.toFixed(2) }}&deg;
      </gmap-info-window>

      <gmap-circle v-if="infoWinOpen" :center="infoWindowPos"
                   :radius="infoCtx.accuracy" :options="accuracyCircleOptions"></gmap-circle>

      <gmap-polyline :path="points" :options="polylineOptions"></gmap-polyline>
      <gmap-marker :key="i" v-for="(p,i) in points" :position="{lat: p.lat, lng: p.lng}"
                   :icon="polylineMarkerIcon"
                   :clickable="true" @click="toggleInfoWindow(p,i)"></gmap-marker>
    </gmap-map>
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
        ranges: [],

        currentPosition: {
          lat: 46.936742,
          lng: 16.7217174,
          _default: true
        },
        zoom: 5,

        points: [],

        geolocationMarker: undefined,

        infoCtx: undefined,
        infoWindowPos: {
          lat: 0,
          lng: 0
        },
        infoWinOpen: false,
        currentMidx: null,
        infoOptions: {
          pixelOffset: {
            width: 0,
            height: 0
          }
        },
        polylineOptions: {
          clickable: false,
          strokeColor: '#FF0000',
          strokeOpacity: 1.0,
          strokeWeight: 3
        },
        polylineMarkerIcon: {
          url: 'https://maps.gstatic.com/intl/en_us/mapfiles/markers2/measle_blue.png',
          size: {width: 7, height: 7},
          anchor: {x: 4, y: 4}
        },
        accuracyCircleOptions: {
          fillOpacity: 1 / 3,
          strokeWeight: 1
        }
      }
    },

    methods: {
      toggleInfoWindow: function (marker, idx) {
        this.infoWindowPos = {lat: marker.lat, lng: marker.lng}
        this.infoCtx = marker

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
        this.ranges = [
          {
            value: null,
            text: 'Зареждане...',
            disabled: true
          }
        ]
        this.selectedRange = null

        this.$http.get('history/ranges', {
          headers: {
            'X-Device-Id': this.deviceId
          }
        })
        .then(res => {
          this.ranges = res.data.map(range => ({
            value: `${range.from}>>>${range.to}`,
            text: `${moment(range.from).format('DD MMM HH:mm')} → ${moment(range.to)
            .format('DD MMM HH:mm')} (${moment.duration(moment(range.from).diff(moment(range.to))).humanize()})`
          }))

          this.ranges.reverse()

          this.ranges.unshift({
            value: null,
            text: (this.ranges.length > 0) ? 'Моля изберете' : 'Няма история',
            disabled: true
          })
        })
      },

      getHistory: function (from, to) {
        this.$http.get(`history/ranges/from/${from}/to/${to}`, {
          headers: {
            'X-Device-Id': this.deviceId
          }
        })
        .then(res => {
          this.points = res.data.map(state => ({
            lat: state.location.latitude,
            lng: state.location.longitude,
            accuracy:  (2 * state.errLat + state.errLon) / 3,
            rawState: state
          }))

          const bounds = new google.maps.LatLngBounds()
          this.points.forEach(p => bounds.extend(p))

          const theMap = this.$refs.gmapHistory
          theMap.fitBounds(bounds)
        })
      },

      onPolylineClick: function (polyMouseEvent) {
        console.log(polyMouseEvent)
      }
    },

    created: function () {
      this.getRanges()
    },

    mounted: function () {
      if (navigator.geolocation) {
        setTimeout(() => {
          Vue.loadScript('/static/geolocation-marker.js')
          .then(() => {
            this.geolocationMarker = new GeolocationMarker()
            this.geolocationMarker.setCircleOptions({
              fillColor: '#5385e8',
              strokeColor: '#0e3fa0'
            })
            this.geolocationMarker.setMap(this.$refs.gmapHistory.$mapObject)

            const self = this
            google.maps.event.addListenerOnce(self.geolocationMarker, 'position_changed', function () {
              if (self.currentPosition._default) {
                self.currentPosition = this.getPosition()
                self.zoom = 15
              }
            })
          })
        }, 0) // magic happens here
      }
    },

    watch: {
      deviceId: function () {
        this.points = []
        this.getRanges()
      },

      selectedRange: function (val) {
        if (val === null) return

        const [from, to] = this.selectedRange.split('>>>')
        this.getHistory(from, to)
      }
    },

    beforeDestroy: function () {
      clearInterval(this.updateIntervalObj)

      if (this.geolocationMarker) {
        this.geolocationMarker.setMap(null)
        this.geolocationMarker = null
      }

      this.selectedRange = null
      this.ranges = []
    }
  }
</script>

<style scoped>
  #rangeFormGroup {
    width: 20%;
    min-width: 256px;
  }

  .history-map, #gmapHistory {
    height: 100%;
    margin-bottom: -35px;

    display: flex;
    flex-direction: column;
  }

  strong.info-label {
    font-weight: bold;
  }
</style>
