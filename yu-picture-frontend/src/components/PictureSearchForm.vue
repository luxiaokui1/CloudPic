<template>
  <div class="picture-search-form">
    <!-- Search form -->
    <a-form name="searchForm" layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Keyword" name="searchText">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="Search by name and description"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="category" label="Category">
        <a-auto-complete
          v-model:value="searchParams.category"
          style="min-width: 180px"
          placeholder="Enter category"
          :options="categoryOptions"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="tags" label="Tags">
        <a-select
          v-model:value="searchParams.tags"
          style="min-width: 180px"
          mode="tags"
          placeholder="Enter tags"
          :options="tagOptions"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Date" name="dateRange">
        <a-range-picker
          style="width: 400px"
          show-time
          v-model:value="dateRange"
          :placeholder="['Edit Start Time', 'Edit End Time']"
          format="YYYY/MM/DD HH:mm:ss"
          :presets="rangePresets"
          @change="onRangeChange"
        />
      </a-form-item>
      <a-form-item label="Name" name="name">
        <a-input v-model:value="searchParams.name" placeholder="Enter name" allow-clear />
      </a-form-item>
      <a-form-item label="Description" name="introduction">
        <a-input v-model:value="searchParams.introduction" placeholder="Enter description" allow-clear />
      </a-form-item>
      <a-form-item label="Width" name="picWidth">
        <a-input-number v-model:value="searchParams.picWidth" />
      </a-form-item>
      <a-form-item label="Height" name="picHeight">
        <a-input-number v-model:value="searchParams.picHeight" />
      </a-form-item>
      <a-form-item label="Format" name="picFormat">
        <a-input v-model:value="searchParams.picFormat" placeholder="Enter format" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit" style="width: 96px">Search</a-button>
          <a-button html-type="reset" @click="doClear">Reset</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import {listPictureTagCategoryUsingGet, listPictureVoByPageUsingPost} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

interface Props {
  onSearch?: (searchParams: API.PictureQueryRequest) => void
}

const props = defineProps<Props>()

// Search parameters
const searchParams = reactive<API.PictureQueryRequest>({})

// Search
const doSearch = () => {
  props.onSearch?.(searchParams)
}

// Tag and category options
const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

/**
 * Fetch tag and category options
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('Failed to fetch tag/category list: ' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})

const dateRange = ref<[]>([])

/**
 * Date range change handler
 */
const onRangeChange = (dates: any[], dateStrings: string[]) => {
  if (dates?.length >= 2) {
    searchParams.startEditTime = dates[0].toDate()
    searchParams.endEditTime = dates[1].toDate()
  } else {
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  }
}

// Date range presets
const rangePresets = ref([
  { label: 'Last 7 days', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: 'Last 14 days', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: 'Last 30 days', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: 'Last 90 days', value: [dayjs().add(-90, 'd'), dayjs()] },
])

// Clear
const doClear = () => {
  Object.keys(searchParams).forEach((key) => {
    searchParams[key] = undefined
  })
  dateRange.value = []
  props.onSearch?.(searchParams)
}
</script>

<style scoped>
.picture-search-form .ant-form-item {
  margin-top: 16px;
}
</style>
