<template>
  <div>
    <excel-importer ref="importer" :tip-msg="'如果编号不存在，那么就新增客户；如果编号已经存在，那么就修改客户。\n注：\n1、地区的格式为：省/市/区（县），例如：北京市/市辖区/朝阳区。文字请参考新增或修改功能中的地区选择。\n2、结账方式只是在新增时生效。'" :download-template-url="downloadTemplate" :upload-url="upload" @confirm="e => $emit('confirm', e)" />
  </div>
</template>

<script>
import ExcelImporter from '@/components/ExcelImporter'
import { request } from '@/utils/request'
export default {
  name: 'CustomerImporter',
  components: { ExcelImporter },
  data() {
    return {
    }
  },
  computed: {
  },
  methods: {
    openDialog() {
      this.$refs.importer.openDialog()
    },
    downloadTemplate() {
      return request({
        url: '/basedata/customer/import/template',
        region: 'cloud-api',
        method: 'get',
        responseType: 'blob'
      })
    },
    upload(params) {
      return request({
        url: '/basedata/customer/import',
        region: 'cloud-api',
        method: 'post',
        dataType: 'file',
        data: params
      })
    }
  }
}
</script>

<style lang="less">
</style>
