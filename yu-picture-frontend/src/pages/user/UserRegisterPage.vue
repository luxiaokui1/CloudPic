<template>
  <div id="userRegisterPage">
    <h2 class="title">CloudPic - Register</h2>
    <div class="desc">Enterprise-grade Intelligent Collaborative Cloud Gallery</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: 'Please enter your account' }]">
        <a-input v-model:value="formState.userAccount" placeholder="Please enter your account" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: 'Please enter your password' },
          { min: 8, message: 'Password must be at least 8 characters' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="Please enter your password" />
      </a-form-item>
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: 'Please confirm your password' },
          { min: 8, message: 'Confirm password must be at least 8 characters' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="Please confirm your password" />
      </a-form-item>
      <div class="tips">
        Already have an account?
        <RouterLink to="/user/login">Login</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">Register</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { userRegisterUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

// Form input values
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const loginUserStore = useLoginUserStore()

/**
 * Submit form
 */
const handleSubmit = async (values: any) => {
  // Check if the two passwords match
  if (values.userPassword !== values.checkPassword) {
    message.error('The two passwords do not match')
    return
  }
  const res = await userRegisterUsingPost(values)
  // On success, redirect to login page
  if (res.data.code === 0 && res.data.data) {
    message.success('Registration successful')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('Registration failed: ' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
  max-width: 360px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  color: #bbb;
  text-align: right;
  font-size: 13px;
  margin-bottom: 16px;
}
</style>
