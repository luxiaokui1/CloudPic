import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'

// Whether this is the first time fetching login user
let firstFetchLoginUser = true

/**
 * Global permission check, executed on every page navigation
 */
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  // Ensure login user info is fetched before checking permissions on first load
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false
  }
  const toUrl = to.fullPath
  // Permission check: only admins can access /admin pages
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('No permission')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
