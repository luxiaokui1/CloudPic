<template>
  <div id="spaceManagePage">
    <a-flex justify="space-between">
      <h2>Space Management</h2>
      <a-space>
        <a-button type="primary" href="/add_space" target="_blank">+ Create Space</a-button>
        <a-button type="primary" ghost href="/space_analyze?queryPublic=1" target="_blank"
          >Analyze Public Gallery</a-button
        >
        <a-button type="primary" ghost href="/space_analyze?queryAll=1" target="_blank"
          >Analyze All Spaces</a-button
        >
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <!-- Search form -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Space Name">
        <a-input v-model:value="searchParams.spaceName" placeholder="Enter space name" allow-clear />
      </a-form-item>
      <a-form-item name="spaceLevel" label="Space Level">
        <a-select
          v-model:value="searchParams.spaceLevel"
          style="min-width: 180px"
          placeholder="Select space level"
          :options="SPACE_LEVEL_OPTIONS"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Space Type" name="spaceType">
        <a-select
          v-model:value="searchParams.spaceType"
          :options="SPACE_TYPE_OPTIONS"
          placeholder="Select space type"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="User ID">
        <a-input v-model:value="searchParams.userId" placeholder="Enter user ID" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">Search</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px" />
    <!-- Table -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'spaceLevel'">
          <div>{{ SPACE_LEVEL_MAP[record.spaceLevel] }}</div>
        </template>
        <!-- Space type -->
        <template v-if="column.dataIndex === 'spaceType'">
          <a-tag>{{ SPACE_TYPE_MAP[record.spaceType] }}</a-tag>
        </template>
        <template v-if="column.dataIndex === 'spaceUseInfo'">
          <div>Size: {{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}</div>
          <div>Count: {{ record.totalCount }} / {{ record.maxCount }}</div>
        </template>
        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="link" :href="`/space_analyze?spaceId=${record.id}`" target="_blank">
              Analyze
            </a-button>
            <a-button type="link" :href="`/add_space?id=${record.id}`" target="_blank">
              Edit
            </a-button>
            <a-button danger @click="doDelete(record.id)">Delete</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteSpaceUsingPost, listSpaceByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  SPACE_LEVEL_MAP,
  SPACE_LEVEL_OPTIONS,
  SPACE_TYPE_MAP,
  SPACE_TYPE_OPTIONS,
} from '../../constants/space.ts'
import { formatSize } from '../../utils'

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: 'Space Name',
    dataIndex: 'spaceName',
  },
  {
    title: 'Space Level',
    dataIndex: 'spaceLevel',
  },
  {
    title: 'Space Type',
    dataIndex: 'spaceType',
  },
  {
    title: 'Usage',
    dataIndex: 'spaceUseInfo',
  },
  {
    title: 'User ID',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: 'Created At',
    dataIndex: 'createTime',
  },
  {
    title: 'Edited At',
    dataIndex: 'editTime',
  },
  {
    title: 'Action',
    key: 'action',
  },
]

const dataList = ref<API.Space[]>([])
const total = ref(0)

// Search parameters
const searchParams = reactive<API.SpaceQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// Fetch data
const fetchData = async () => {
  const res = await listSpaceByPageUsingPost({
    ...searchParams,
  })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('Failed to fetch data: ' + res.data.message)
  }
}

onMounted(() => {
  fetchData()
})

// Pagination
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total) => `Total ${total} items`,
  }
})

// Table change handler
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// Search
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}

// Delete
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteSpaceUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Deleted successfully')
    fetchData()
  } else {
    message.error('Delete failed')
  }
}
</script>
