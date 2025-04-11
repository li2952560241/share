<template>
  <router-view />
</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'
import { handleThemeStyle } from '@/utils/theme'

onMounted(() => {
  nextTick(() => {
    // ��ʼ��������ʽ
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
