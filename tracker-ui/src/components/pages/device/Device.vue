<template>
  <div class="device">
    <b-row>
      <b-col md="4" lg="4" xl="2">
        <device-list-group concise="true" class="d-none d-md-block d-lg-block d-xl-block"></device-list-group>
      </b-col>

      <b-col>
        <b-card no-body>
          <b-tabs ref="tabs" small lazy card>
            <b-tab title="Местоположение" active class="map-container">
              <latest-location-map :device-id="$route.params.id"></latest-location-map>
            </b-tab>

            <b-tab title="История" class="map-container">
              <history-map :device-id="$route.params.id"></history-map>
            </b-tab>

            <b-tab title="Настройки">
              Tab Contents 2
            </b-tab>

          </b-tabs>
        </b-card>
      </b-col>
    </b-row>
  </div>
</template>

<script>
  import DeviceListGroup from '../../DeviceListGroup.vue'
  import LatestLocationMap from './tabs/LatestLocationMap.vue'
  import HistoryMap from './tabs/HistoryMap.vue'

  export default {
    components: {
      HistoryMap,
      LatestLocationMap,
      DeviceListGroup
    },
    name: 'device'
  }
</script>

<style scoped>
  .map-container {
    height: 80vh;
    padding-bottom: 2em;
  }
</style>
