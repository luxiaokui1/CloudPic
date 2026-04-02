<template>
  <div class="batch-edit-picture-modal">
    <a-modal v-model:visible="visible" title="Batch Edit Pictures" :footer="false" @cancel="closeModal">
      <a-typography-paragraph type="secondary">* Only applies to pictures on the current page</a-typography-paragraph>
      <!-- Batch edit form -->
      <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
        <a-form-item name="category" label="Category">
          <a-auto-complete
            v-model:value="formData.category"
            placeholder="Enter category"
            :options="categoryOptions"
            allow-clear
          />
        </a-form-item>
        <a-form-item name="tags" label="Tags">
          <a-select
            v-model:value="formData.tags"
            mode="tags"
            placeholder="Enter tags"
            :options="tagOptions"
            allow-clear
          />
        </a-form-item>
        <a-form-item name="nameRule" label="Naming Rule">
          <a-input
            v-model:value="formData.nameRule"
            placeholder="Enter naming rule, use {index} for auto-generated sequence"
            allow-clear
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">Submit</a-button>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import {
  editPictureByBatchUsingPost,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

interface Props {
  pictureList: API.PictureVO[]
  spaceId: number
  onSuccess: () => void
}

const props = withDefaults(defineProps<Props>(), {})

const visible = ref(false)

const openModal = () => {
  visible.value = true
}

const closeModal = () => {
  visible.value = false
}

defineExpose({
  openModal,
})

const formData = reactive<API.PictureEditByBatchRequest>({
  category: '',
  tags: [],
  nameRule: '',
})

/**
 * Submit form
 */
const handleSubmit = async (values: any) => {
  if (!props.pictureList) {
    return
  }
  const res = await editPictureByBatchUsingPost({
    pictureIdList: props.pictureList.map((picture) => picture.id),
    spaceId: props.spaceId,
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('Operation successful')
    closeModal()
    props.onSuccess?.()
  } else {
    message.error('Operation failed: ' + res.data.message)
  }
}

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
</script>
