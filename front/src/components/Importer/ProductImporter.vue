<template>
  <div>
    <excel-importer ref="importer" :tip-msg="'如果商品编号不存在，那么就新增商品；如果商品编号已经存在，那么就修改商品。\n注：\n1、如果商品类目发生变化，关联的商品属性会清空。\n2、只支持导入普通商品。'" :download-template-url="downloadTemplate" :upload-url="upload" @confirm="e => $emit('confirm', e)" />
  </div>
</template>

<script>
import ExcelImporter from '@/components/ExcelImporter'
import { request } from '@/utils/request'
export default {
  name: 'ProductImporter',
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
        url: '/basedata/product/import/template',
        region: 'cloud-api',
        method: 'get',
        responseType: 'blob'
      })
    },
    upload(params) {
      return request({
        url: '/basedata/product/import',
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
