<template>
  <div id="app">
    <b-navbar toggleable="md" type="dark" variant="dark">
      <b-nav-toggle target="nav_collapse"></b-nav-toggle>
      <b-navbar-brand>GPS Tracker</b-navbar-brand>
      <b-collapse is-nav id="nav_collapse">
        <b-nav is-nav-bar>
          <b-nav-item to="/" exact>Начало</b-nav-item>
          <b-nav-item to="/device" disabled :active="$route.path.startsWith('/device')">Проследяване</b-nav-item>
        </b-nav>
      </b-collapse>
    </b-navbar>

    <b-container fluid id="main-container">
      <router-view></router-view>
    </b-container>
  </div>
</template>

<script>
  import moment from 'moment'

  moment.locale('bg-BG')

  export default {
    name: 'app'
  }
</script>

<style>
  #app {
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  #main-container {
    padding-top: 25px;
  }
</style>
