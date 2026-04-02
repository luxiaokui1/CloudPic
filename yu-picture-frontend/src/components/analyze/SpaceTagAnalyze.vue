<template>
  <div class="space-tag-analyze">
    <a-card title="Space Picture Tag Analysis">
      <v-chart :option="options" style="height: 320px; max-width: 100%" :loading="loading" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import VChart from 'vue-echarts'
import 'echarts'
import 'echarts-wordcloud'
import { computed, ref, watchEffect } from 'vue'
import { getSpaceTagAnalyzeUsingPost } from '@/api/spaceAnalyzeController.ts'
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

// Chart data
const dataList = ref<API.SpaceCategoryAnalyzeResponse>([])
// Loading state
const loading = ref(true)

// Fetch data
const fetchData = async () => {
  loading.value = true
  const res = await getSpaceTagAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
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
const options =computed(() => {
  const tagData = dataList.value.map((item) => ({
    name: item.tag,
    value: item.count,
  }))

  return {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => `${params.name}: ${params.value} times`,
    },
    series: [
      {
        type: 'wordCloud',
        gridSize: 10,
        sizeRange: [12, 50],
        rotationRange: [-90, 90],
        shape: 'circle',
        textStyle: {
          color: () =>
              `rgb(${Math.round(Math.random() * 255)}, ${Math.round(
                  Math.random() * 255,
              )}, ${Math.round(Math.random() * 255)})`,
        },
        data: tagData,
      },
    ],
  }
})
</script>

<style scoped></style>
