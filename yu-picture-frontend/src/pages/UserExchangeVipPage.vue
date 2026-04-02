<template>
  <div id="vipExchangePage">
    <h2 style="margin-bottom: 16px">VIP Code Redemption</h2>
    <!-- Redemption code form -->
    <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item name="vipCode" label="Redemption Code">
        <a-input
          v-model:value="formData.vipCode"
          placeholder="Please enter your VIP redemption code"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          Redeem
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { exchangeVipUsingPost } from '@/api/userController.ts'
import { useRouter } from 'vue-router'

// Form data
const formData = reactive<API.VipExchangeRequest>({
  vipCode: '',
})

// Task submission status
const loading = ref(false)

const router = useRouter()

/**
 * Submit form
 */
const handleSubmit = async () => {
  // Validate redemption code is not empty
  if (!formData.vipCode) {
    message.error('Please enter a redemption code')
    return
  }

  loading.value = true

  try {
    const res = await exchangeVipUsingPost({
      vipCode: formData.vipCode,
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('Redemption successful!')
      router.push({
        path: `/`,
      })
    } else {
      message.error('Redemption failed: ' + res.data.message)
    }
  } catch (error) {
    message.error('Redemption failed, please try again later')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
#vipExchangePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
