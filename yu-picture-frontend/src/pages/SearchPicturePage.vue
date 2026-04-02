<template>
  <div id="searchPicturePage">
    <h2 style="margin-bottom: 16px">Search by Image</h2>
    <h3 style="margin-bottom: 16px">Original Image</h3>
    <a-card hoverable style="width: 240px">
      <template #cover>
        <img
          :alt="picture.name"
          :src="picture.thumbnailUrl ?? picture.url"
          style="height: 180px; object-fit: cover"
        />
      </template>
    </a-card>
    <h3 style="margin: 16px 0">Search Results</h3>
    <!-- Picture result list -->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="dataList"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <a :href="picture.fromUrl" target="_blank">
            <!-- Single picture -->
            <a-card hoverable>
              <template #cover>
                <img
                  :alt="picture.name"
                  :src="picture.thumbUrl"
                  style="height: 180px; object-fit: cover"
                />
              </template>
            </a-card>
          </a>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  getPictureVoByIdUsingGet,
  searchPictureByPictureUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const pictureId = computed(() => {
  return route.query?.pictureId
})
const picture = ref<API.PictureVO>({})

// Fetch picture detail
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: pictureId.value,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('Failed to fetch picture detail: ' + res.data.message)
    }
  } catch (e: any) {
    message.error('Failed to fetch picture detail: ' + e.message)
  }
}

onMounted(() => {
  fetchPictureDetail()
})

// Search by image results
const dataList = ref<API.ImageSearchResult[]>([])
const loading = ref<boolean>(true)
// Fetch search results
const fetchResultData = async () => {
  loading.value = true;
  try {
    const res = await searchPictureByPictureUsingPost({
      pictureId: pictureId.value,
    })
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data ?? []
    } else {
      message.error('Failed to fetch data: ' + res.data.message)
    }
  } catch (e: any) {
    message.error('Failed to fetch data: ' + e.message)
  }
  loading.value = false;
}

onMounted(() => {
  fetchResultData()
})
</script>

<style scoped>
#searchPicturePage {
  margin-bottom: 16px;
}
</style>
