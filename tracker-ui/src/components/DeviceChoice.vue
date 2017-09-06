<template>
  <div class="device-choice">
    <b-container>
      <b-jumbotron to="/"
                   bg-variant="dark" text-variant="light" border-variant="dark"
                   header="GPS Tracker"
                   lead="Система за сигурност с GPS проследяване за автомобил"></b-jumbotron>
    </b-container>

    <b-container>
      <h4>Устройства</h4>
      <device-list-group></device-list-group>
    </b-container>
  </div>
</template>

<script>
  import DeviceListGroup from './DeviceListGroup.vue'

  export default {
    components: {DeviceListGroup},
    name: 'device-choice'
  }
</script>
