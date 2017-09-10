<template>
  <div class="latest-location-map">
    <b-form-checkbox v-model="followLocation">
      Следвай местоположението по картата
    </b-form-checkbox>
    <p class="small text-muted">
      <b-badge pill variant="secondary">i</b-badge>
      Фокусира картата върху актуалното местоположение на автомобила.
    </p>

    <gmap-map id="gmapLocation"
              ref="gmapLocation"
              :center="currentPosition"
              :zoom="zoom">

      <gmap-info-window v-if="carMarker.rawState" :options="infoOptions" :position="infoWindowPos" :opened="infoWinOpen"
                        @closeclick="infoWinOpen=false">
        <strong class="info-label">Обновено {{carMarker.rawState.satelliteTime | moment("from") }}</strong>.
        <br>
        <strong class="info-label">Време</strong>: {{carMarker.rawState.satelliteTime | moment('LLLL')}}
        <br><br>

        <strong class="info-label">Координати</strong>:
        {{carMarker.rawState.location.latitude.toFixed(6)}},
        {{carMarker.rawState.location.longitude.toFixed(6)}}
        <br>

        <span v-if="carMarker.rawState.location.altitude">
          <strong class="info-label">Височина</strong>:
          {{carMarker.rawState.location.altitude.toFixed(2)}} m
          <br>
        </span>

        <strong class="info-label">Прецизност</strong>:
        <span>{{carMarker.rawState.satelliteCount}} sat.</span>
        <span v-if="carMarker.rawState.errLat">Lat±{{carMarker.rawState.errLat.toFixed(0)}} m</span>
        <span v-if="carMarker.rawState.errLon">Lon±{{carMarker.rawState.errLon.toFixed(0)}} m</span>
        <span v-if="carMarker.rawState.errAlt">Alt±{{carMarker.rawState.errAlt.toFixed(0)}} m</span>
        <br><br>

        <strong class="info-label">Скорост</strong>:
        {{ (carMarker.rawState.speed * 3.6).toFixed(2) }} km/h
        <br>

        <strong class="info-label">Курс</strong>:
        {{ carMarker.rawState.course.toFixed(2) }}&deg;
      </gmap-info-window>

      <gmap-marker v-if="carMarker.position" :position="carMarker.position" :clickable="true"
                   :icon="createCarSymbol(carMarker.course)"
                   @click="toggleInfoWindow(carMarker)"></gmap-marker>

      <gmap-circle v-if="carMarker.position && !!carMarker.accuracy" :center="carMarker.position"
                   :radius="carMarker.accuracy" :options="accuracyCircleOptions"
      ></gmap-circle>
    </gmap-map>
  </div>
</template>

<script>
  import Vue from 'vue'

  export default {
    name: 'latest-location-map',
    props: ['deviceId'],

    data () {
      return {
        currentPosition: {
          lat: 46.936742,
          lng: 16.7217174,
          _default: true
        },
        zoom: 5,

        carMarker: {},
        followLocation: true,
        geolocationMarker: undefined,

        infoContent: '',
        infoWindowPos: {
          lat: 0,
          lng: 0
        },
        infoWinOpen: false,
        infoOptions: {
          pixelOffset: {
            width: 0,
            height: -35
          }
        },
        accuracyCircleOptions: {
          fillOpacity: 1 / 3,
          strokeWeight: 1
        },
        updateIntervalObj: undefined
      }
    },

    methods: {
      toggleInfoWindow: function (marker) {
        this.infoWindowPos = marker.position
        this.infoContent = marker.infoText

        // check if its the same marker that was selected if yes toggle
        this.infoWinOpen = !this.infoWinOpen
      },

      createCarSymbol: function (course) {
        return {
          path: 'M5.879-23.516H-5.88c-3.117,0-5.643,3.467-5.643,6.584v34.804c0,3.116,2.526,5.644,5.643,5.644H5.879 c3.116,0,5.644-2.527,5.644-5.644v-34.804C11.521-20.049,8.995-23.516,5.879-23.516z M10.534-9.328V2.337L7.805,2.688v-4.806 L10.534-9.328z M9.102-12.743c-1.016,3.9-2.219,8.51-2.219,8.51H-6.885l-2.222-8.51C-9.106-12.743-0.223-15.761,9.102-12.743z M-7.775-1.803v4.492l-2.73-0.349V-9.014L-7.775-1.803z M-10.505,14.422V4.063l2.73,0.343v8.196L-10.505,14.422z M-8.948,17.366 l2.218-3.336H7.041l2.219,3.336H-8.948z M7.805,12.289V4.417l2.729-0.355V14.11L7.805,12.289z',
          fillColor: '#ff0000',
          strokeColor: '#720114',
          fillOpacity: 0.85,
          strokeWeight: 1.2,
          scale: 1,
          rotation: course
        }
      },

      getLatestLocation: function (follow, zoom) {
        this.$http.get('history/latest', {
          headers: {
            'X-Device-Id': this.deviceId
          }
        })
        .then(res => {
          const state = res.data

          this.carMarker = {
            position: {
              lat: state.location.latitude,
              lng: state.location.longitude
            },
            course: state.course,
            accuracy: (2 * state.errLat + state.errLon) / 3,
            rawState: state
          }

          this.currentPosition = this.carMarker.position

          if (zoom) {
            this.zoom = 17
          }

          if (follow) {
            this.$refs.gmapLocation.panTo(this.currentPosition)
          }
        })
        .catch(err => {
          this.carMarker = {}
        })
      }
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
            this.geolocationMarker.setMap(this.$refs.gmapLocation.$mapObject)

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

      this.getLatestLocation(true, true)
      this.updateIntervalObj = setInterval(() => this.getLatestLocation(this.followLocation, false), 5000)
    },

    watch: {
      deviceId: function () {
        this.getLatestLocation(true, true)
      }
    },

    beforeDestroy: function () {
      clearInterval(this.updateIntervalObj)
      if (this.geolocationMarker) {
        this.geolocationMarker.setMap(null)
        this.geolocationMarker = null
      }
    }
  }
</script>

<style scoped>
  .latest-location-map, #gmapLocation {
    height: 100%;
    margin-bottom: -35px;

    display: flex;
    flex-direction: column;
  }

  strong.info-label {
    font-weight: bold;
  }
</style>
