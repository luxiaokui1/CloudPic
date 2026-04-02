<template>
  <div id="spaceAnalyzePage">
    <h2>
      Space Gallery Analytics -
      <span v-if="queryAll">All Spaces</span>
      <span v-else-if="queryPublic">Public Gallery</span>
      <span v-else>
        <a :href="`/space/${spaceId}`" target="_blank">Space ID: {{ spaceId }}</a>
      </span>
    </h2>
    <div style="margin-bottom: 16px" />
    <a-row :gutter="[16, 16]">
      <!-- Space usage analysis -->
      <a-col :xs="24" :md="12">
        <SpaceUsageAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- Space category analysis -->
      <a-col :xs="24" :md="12">
        <SpaceCategoryAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- Tag analysis -->
      <a-col :xs="24" :md="12">
        <SpaceTagAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- Picture size segment analysis -->
      <a-col :xs="24" :md="12">
        <SpaceSizeAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- User upload behavior analysis -->
      <a-col :xs="24" :md="12">
        <SpaceUserAnalyze :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
      <!-- Space usage ranking analysis -->
      <a-col :xs="24" :md="12">
        <SpaceRankAnalyze v-if="isAdmin" :spaceId="spaceId" :queryAll="queryAll" :queryPublic="queryPublic" />
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import SpaceUsageAnalyze from '@/components/analyze/SpaceUsageAnalyze.vue'
import SpaceCategoryAnalyze from '@/components/analyze/SpaceCategoryAnalyze.vue'
import SpaceTagAnalyze from '@/components/analyze/SpaceTagAnalyze.vue'
import SpaceSizeAnalyze from '@/components/analyze/SpaceSizeAnalyze.vue'
import SpaceUserAnalyze from '@/components/analyze/SpaceUserAnalyze.vue'
import SpaceRankAnalyze from '@/components/analyze/SpaceRankAnalyze.vue'
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const route = useRoute()

// Space id
const spaceId = computed(() => {
  return route.query?.spaceId as string
})

// Whether to query all spaces
const queryAll = computed(() => {
  return !!route.query?.queryAll
})

// Whether to query public spaces
const queryPublic = computed(() => {
  return !!route.query?.queryPublic
})

// Check if user is admin
const loginUserStore = useLoginUserStore()
const loginUser = loginUserStore.loginUser
const isAdmin = computed(() => {
  return loginUser.userRole === 'admin'
})
</script>

<style scoped>
#spaceAnalyzePage {
  margin-bottom: 16px;
}
</style>
