<template>
  <a-modal
    class="image-cropper"
    v-model:visible="visible"
    title="Edit Picture"
    :footer="false"
    @cancel="closeModal"
  >
    <!-- Image cropper component -->
    <vue-cropper
      ref="cropperRef"
      :img="imageUrl"
      output-type="png"
      :info="true"
      :can-move-box="true"
      :fixed-box="false"
      :auto-crop="true"
      :center-box="true"
    />
    <div style="margin-bottom: 16px" />
    <!-- Collaborative editing actions -->
    <div class="image-edit-actions" v-if="isTeamSpace">
      <a-space>
        <a-button v-if="editingUser" disabled>{{ editingUser.userName }} is editing</a-button>
        <a-button v-if="canEnterEdit" type="primary" ghost @click="enterEdit">Enter Edit</a-button>
        <a-button v-if="canExitEdit" danger ghost @click="exitEdit">Exit Edit</a-button>
      </a-space>
    </div>
    <div style="margin-bottom: 16px" />
    <!-- Picture actions -->
    <div class="image-cropper-actions">
      <a-space>
        <a-button @click="rotateLeft" :disabled="!canEdit">Rotate Left</a-button>
        <a-button @click="rotateRight" :disabled="!canEdit">Rotate Right</a-button>
        <a-button @click="changeScale(1)" :disabled="!canEdit">Zoom In</a-button>
        <a-button @click="changeScale(-1)" :disabled="!canEdit">Zoom Out</a-button>
        <a-button type="primary" :loading="loading" :disabled="!canEdit" @click="handleConfirm"
          >Confirm
        </a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { computed, onUnmounted, ref, watchEffect } from 'vue'
import { uploadPictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import PictureEditWebSocket from '@/utils/pictureEditWebSocket.ts'
import { PICTURE_EDIT_ACTION_ENUM, PICTURE_EDIT_MESSAGE_TYPE_ENUM } from '@/constants/picture.ts'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'

interface Props {
  imageUrl?: string
  picture?: API.PictureVO
  spaceId?: number
  space?: API.SpaceVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

// Whether it's a team space
const isTeamSpace = computed(() => {
  return props.space?.spaceType === SPACE_TYPE_ENUM.TEAM
})

// Cropper ref
const cropperRef = ref()

// Scale
const changeScale = (num) => {
  cropperRef.value?.changeScale(num)
  if (num > 0) {
    editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_IN)
  } else {
    editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT)
  }
}

// Rotate left
const rotateLeft = () => {
  cropperRef.value.rotateLeft()
  editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT)
}

// Rotate right
const rotateRight = () => {
  cropperRef.value.rotateRight()
  editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT)
}

// Confirm crop
const handleConfirm = () => {
  cropperRef.value.getCropBlob((blob: Blob) => {
    const fileName = (props.picture?.name || 'image') + '.png'
    const file = new File([blob], fileName, { type: blob.type })
    handleUpload({ file })
  })
}

const loading = ref(false)

/**
 * Upload picture
 */
const handleUpload = async ({ file }: any) => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = props.picture ? { id: props.picture.id } : {}
    params.spaceId = props.spaceId
    const res = await uploadPictureUsingPost(params, {}, file)
    if (res.data.code === 0 && res.data.data) {
      message.success('Picture uploaded successfully')
      props.onSuccess?.(res.data.data)
      closeModal()
    } else {
      message.error('Picture upload failed: ' + res.data.message)
    }
  } catch (error) {
    console.error('Picture upload failed', error)
    message.error('Picture upload failed: ' + error.message)
  }
  loading.value = false
}

const visible = ref(false)

const openModal = () => {
  visible.value = true
}

const closeModal = () => {
  visible.value = false
  if (websocket) {
    websocket.disconnect()
  }
  editingUser.value = undefined
}

defineExpose({
  openModal,
})

// Real-time editing
const loginUserStore = useLoginUserStore()
const loginUser = loginUserStore.loginUser

// Currently editing user
const editingUser = ref<API.UserVO>()
// Whether current user can enter edit
const canEnterEdit = computed(() => {
  return !editingUser.value
})
// Whether current user can exit edit (only the editing user can)
const canExitEdit = computed(() => {
  return editingUser.value?.id === loginUser.id
})
// Whether the edit action buttons are enabled
const canEdit = computed(() => {
  // Not a team space, can always edit
  if (!isTeamSpace.value) {
    return true
  }
  // Team space, only the editor can edit
  return editingUser.value?.id === loginUser.id
})

// WebSocket logic
let websocket: PictureEditWebSocket | null

// Initialize WebSocket connection
const initWebsocket = () => {
  const pictureId = props.picture?.id
  if (!pictureId || !visible.value) {
    return
  }
  if (websocket) {
    websocket.disconnect()
  }
  websocket = new PictureEditWebSocket(pictureId)
  websocket.connect()

  // Listen to events
  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.INFO, (msg) => {
    console.log('Received notification:', msg)
    message.info(msg.message)
  })

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ERROR, (msg) => {
    console.log('Received error:', msg)
    message.info(msg.message)
  })

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT, (msg) => {
    console.log('Received enter edit message:', msg)
    message.info(msg.message)
    editingUser.value = msg.user
  })

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION, (msg) => {
    console.log('Received edit action message:', msg)
    message.info(msg.message)
    switch (msg.editAction) {
      case PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT:
        rotateLeft()
        break
      case PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT:
        rotateRight()
        break
      case PICTURE_EDIT_ACTION_ENUM.ZOOM_IN:
        changeScale(1)
        break
      case PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT:
        changeScale(-1)
        break
    }
  })

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT, (msg) => {
    console.log('Received exit edit message:', msg)
    message.info(msg.message)
    editingUser.value = undefined
  })
}

// Watch for property and visibility changes to init WebSocket
watchEffect(() => {
  if (isTeamSpace.value) {
    initWebsocket()
  }
})

// Disconnect WebSocket on component unmount
onUnmounted(() => {
  if (websocket) {
    websocket.disconnect()
  }
  editingUser.value = undefined
})

// Enter edit mode
const enterEdit = () => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT,
    })
  }
}

// Exit edit mode
const exitEdit = () => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT,
    })
  }
}

// Edit picture action
const editAction = (action: string) => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION,
      editAction: action,
    })
  }
}
</script>

<style>
.image-cropper {
  text-align: center;
}

.image-cropper .vue-cropper {
  height: 400px !important;
}
</style>
