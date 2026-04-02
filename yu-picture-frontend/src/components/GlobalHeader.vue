<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">CloudPic</div>
          </div>
        </router-link>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <!-- User info display -->
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? 'Anonymous' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item>
                    <router-link to="/my_space">
                      <UserOutlined />
                      My Space
                    </router-link>
                  </a-menu-item>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    Logout
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">Login</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { HomeOutlined, LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '@/api/userController.ts'

const loginUserStore = useLoginUserStore()

// Unfiltered menu items
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: 'Home',
    title: 'Home',
  },
  {
    key: '/add_picture',
    label: 'Create Picture',
    title: 'Create Picture',
  },
  {
    key: '/admin/userManage',
    label: 'User Management',
    title: 'User Management',
  },
  {
    key: '/admin/pictureManage',
    label: 'Picture Management',
    title: 'Picture Management',
  },
  {
    key: '/admin/spaceManage',
    label: 'Space Management',
    title: 'Space Management',
  },
]

// Filter menu items by permission
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    // Only admins can see /admin menu items
    if (menu?.key?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// Menu items displayed in the nav
const items = computed(() => filterMenus(originItems))

const router = useRouter()
// Current highlighted menu item
const current = ref<string[]>([])
// Watch route changes to update highlighted menu
router.afterEach((to, from, next) => {
  current.value = [to.path]
})

// Menu click handler
const doMenuClick = ({ key }) => {
  router.push({
    path: key,
  })
}

// User logout
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: 'Not Logged In',
    })
    message.success('Logged out successfully')
    await router.push('/user/login')
  } else {
    message.error('Logout failed: ' + res.data.message)
  }
}
</script>

<style scoped>
#globalHeader .title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>
