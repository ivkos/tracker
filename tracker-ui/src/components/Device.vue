<template>
  <div class="device">
    <b-row>
      <b-col md="4" lg="4" xl="3">
        <device-list-group concise="true" class="d-none d-md-block d-lg-block d-xl-block"></device-list-group>
      </b-col>

      <b-col id="map-container">
        <latest-location-map :device-id="$route.params.id"></latest-location-map>
      </b-col>
    </b-row>
  </div>
</template>

<script>
  import DeviceListGroup from './DeviceListGroup.vue'
  import LatestLocationMap from './LatestLocationMap.vue'

  export default {
    components: {
      LatestLocationMap,
      DeviceListGroup
    },
    name: 'device'
  }
</script>

<style scoped>
  #map-container {
    height: 85vh;
  }
</style>
