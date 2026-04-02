import {createRouter, createWebHistory} from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import AddPicturePage from '@/pages/AddPicturePage.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue'
import PictureDetailPage from '@/pages/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/AddPictureBatchPage.vue'
import SpaceManagePage from '@/pages/admin/SpaceManagePage.vue'
import AddSpacePage from '@/pages/AddSpacePage.vue'
import MySpacePage from '@/pages/MySpacePage.vue'
import SpaceDetailPage from '@/pages/SpaceDetailPage.vue'
import SearchPicturePage from '@/pages/SearchPicturePage.vue'
import SpaceAnalyzePage from '@/pages/SpaceAnalyzePage.vue'
import SpaceUserManagePage from '@/pages/admin/SpaceUserManagePage.vue'
import UserExchangeVipPage from '@/pages/UserExchangeVipPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: 'User Login',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: 'User Register',
      component: UserRegisterPage,
    },
    {
      path: '/admin/userManage',
      name: 'User Management',
      component: UserManagePage,
    },
    {
      path: '/admin/pictureManage',
      name: 'Picture Management',
      component: PictureManagePage,
    },
    {
      path: '/admin/spaceManage',
      name: 'Space Management',
      component: SpaceManagePage,
    },
    {
      path: '/spaceUserManage/:id',
      name: 'Space Member Management',
      component: SpaceUserManagePage,
      props: true,
    },
    {
      path: '/add_picture',
      name: 'Create Picture',
      component: AddPicturePage,
    },
    {
      path: '/add_picture/batch',
      name: 'Batch Create Pictures',
      component: AddPictureBatchPage,
    },
    {
      path: '/picture/:id',
      name: 'Picture Detail',
      component: PictureDetailPage,
      props: true,
    },
    {
      path: '/add_space',
      name: 'Create Space',
      component: AddSpacePage,
    },
    {
      path: '/my_space',
      name: 'My Space',
      component: MySpacePage,
    },
    {
      path: '/space/:id',
      name: 'Space Detail',
      component: SpaceDetailPage,
      props: true,
    },
    {
      path: '/space_analyze',
      name: 'Space Analytics',
      component: SpaceAnalyzePage,
    },
    {
      path: '/search_picture',
      name: 'Picture Search',
      component: SearchPicturePage,
    },
    {
      path: '/user_exchange_vip',
      name: 'Redeem VIP',
      component: UserExchangeVipPage,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
