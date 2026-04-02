<template>
  <div id="addPictureBatchPage">
    <h2 style="margin-bottom: 16px">Batch Create</h2>
    <!-- Picture info form -->
    <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item name="searchText" label="Keyword">
        <a-input v-model:value="formData.searchText" placeholder="Please enter a keyword" allow-clear />
      </a-form-item>
      <a-form-item name="count" label="Fetch Count">
        <a-input-number
          v-model:value="formData.count"
          placeholder="Please enter count"
          style="min-width: 180px"
          :min="1"
          :max="30"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="namePrefix" label="Name Prefix">
        <a-input
          v-model:value="formData.namePrefix"
          placeholder="Please enter a name prefix, sequence numbers will be appended automatically"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          Execute Task
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
  uploadPictureByBatchUsingPost,
} from '@/api/pictureController.ts'
import { useRoute, useRouter } from 'vue-router'

const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})
// Task submission status
const loading = ref(false)

const router = useRouter()

/**
 * Submit form
 */
const handleSubmit = async (values: any) => {
  loading.value = true
  const res = await uploadPictureByBatchUsingPost({
    ...formData,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success(`Created successfully, ${res.data.data} items total`)
    router.push({
      path: `/`,
    })
  } else {
    message.error('Creation failed: ' + res.data.message)
  }
  loading.value = false
}
</script>

<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
