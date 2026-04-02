<template>
  <div id="app">
    <a-config-provider :locale="enUS">
      <BasicLayout />
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import enUS from 'ant-design-vue/es/locale/en_US';
import dayjs from 'dayjs';
import 'dayjs/locale/en';
dayjs.locale('en');

import BasicLayout from '@/layouts/BasicLayout.vue'
</script>

<style scoped></style>
