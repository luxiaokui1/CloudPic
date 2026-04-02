<template>
  <div class="space-user-analyze">
    <a-card title="Space Picture User Analysis">
      <v-chart :option="options" style="height: 320px; max-width: 100%" :loading="loading" />
      <template #extra>
        <a-space>
          <a-segmented v-model:value="timeDimension" :options="timeDimensionOptions" />
          <a-input-search placeholder="Enter user ID" enter-button="Search User" @search="doSearch" />
        </a-space>
      </template>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import VChart from 'vue-echarts'
import 'echarts'
import { computed, ref, watchEffect } from 'vue'
import { getSpaceUserAnalyzeUsingPost } from '@/api/spaceAnalyzeController.ts'
import { message } from 'ant-design-vue'

interface Props {
  queryAll?: boolean
  queryPublic?: boolean
  spaceId?: number
}

const props = withDefaults(defineProps<Props>(), {
  queryAll: false,
  queryPublic: false,
})

// Time dimension options
const timeDimension = ref<'day' | 'week' | 'month'>('day')
const timeDimensionOptions = [
  {
    label: 'Day',
    value: 'day',
  },
  {
    label: 'Week',
    value: 'week',
  },
  {
    label: 'Month',
    value: 'month',
  },
]
// User option
const userId = ref<string>()
const doSearch = (value: string) => {
  userId.value = value
}

// Chart data
const dataList = ref<API.SpaceCategoryAnalyzeResponse>([])
// Loading state
const loading = ref(true)

// Fetch data
const fetchData = async () => {
  loading.value = true
  const res = await getSpaceUserAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
    timeDimension: timeDimension.value,
    userId: userId.value,
  })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('Failed to fetch data: ' + res.data.message)
  }
  loading.value = false
}

/**
 * Watch for parameter changes and reload data
 */
watchEffect(() => {
  fetchData()
})

// Chart options
const options = computed(() => {
  const periods = dataList.value.map((item) => item.period)
  const counts = dataList.value.map((item) => item.count)

  return {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: periods, name: 'Time Period' },
    yAxis: { type: 'value', name: 'Upload Count' },
    series: [
      {
        name: 'Upload Count',
        type: 'line',
        data: counts,
        smooth: true,
        emphasis: {
          focus: 'series',
        },
      },
    ],
  }
})
</script>

<style scoped></style>
