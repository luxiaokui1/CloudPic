<template>
  <div class="space-user-analyze">
    <a-flex gap="middle">
      <a-card title="Storage Space" style="width: 50%">
        <div style="height: 320px; text-align: center">
          <h3>
            {{ formatSize(data.usedSize) }} /
            {{ data.maxSize ? formatSize(data.maxSize) : 'Unlimited' }}
          </h3>
          <a-progress type="dashboard" :percent="data.sizeUsageRatio ?? 0" />
        </div>
      </a-card>
      <a-card title="Picture Count" style="width: 50%">
        <div style="height: 320px; text-align: center">
          <h3>
            {{ data.usedCount }} / {{ data.maxCount ?? 'Unlimited' }}
          </h3>
          <a-progress type="dashboard" :percent="data.countUsageRatio ?? 0" />
        </div>
      </a-card>
    </a-flex>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import { getSpaceUsageAnalyzeUsingPost } from '@/api/spaceAnalyzeController.ts'
import { message } from 'ant-design-vue'
import { formatSize } from '@/utils'

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
const data = ref<API.SpaceUsageAnalyzeResponse>({})
// Loading state
const loading = ref(true)

// Fetch data
const fetchData = async () => {
  loading.value = true
  const res = await getSpaceUsageAnalyzeUsingPost({
    queryAll: props.queryAll,
    queryPublic: props.queryPublic,
    spaceId: props.spaceId,
  })
  if (res.data.code === 0 && res.data.data) {
    data.value = res.data.data
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
</script>

<style scoped></style>
