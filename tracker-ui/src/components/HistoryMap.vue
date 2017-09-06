<template>
  <div class="history-map"></div>
</template>

<script>
  import Vue from 'vue'

  export default {
    name: 'history-map',

    data () {
      return {
        currentPosition: {
          lat: 46.936742,
          lng: 16.7217174,
          _default: true
        },
        zoom: 5,

        points: [],

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
      }
    },

    created: function () {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(position => {
          Vue.loadScript(
            'https://github.com/ChadKillingsworth/geolocation-marker/releases/download/v2.0.5/geolocation-marker.js')
          .then(() => {
            new GeolocationMarker(this.$refs.map.$mapObject)
          })

          if (this.position._default) {
            this.position = {
              lat: position.coords.latitude,
              lng: position.coords.longitude
            }
            this.zoom = 15
          }
        })
      }
    }
  }
</script>

<style scoped>
  .history-map, #map {
    height: 100%;
    margin: 0;
    padding: 0;
  }
</style>
