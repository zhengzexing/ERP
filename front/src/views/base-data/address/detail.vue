<template>
  <a-modal v-model="visible" :mask-closable="false" width="40%" title="查看" :dialog-style="{ top: '20px' }" :footer="null">
    <div v-if="visible" v-permission="['base-data:address:query']" v-loading="loading">
      <a-descriptions :column="4" bordered>
        <a-descriptions-item label="实体类型" :span="2">
          {{ $enums.ADDRESS_ENTITY_TYPE.getDesc(formData.entityType) }}
        </a-descriptions-item>
        <a-descriptions-item label="实体" :span="2">
          {{ formData.entityName }}
        </a-descriptions-item>
        <a-descriptions-item label="地址类型" :span="2">
          {{ $enums.ADDRESS_TYPE.getDesc(formData.addressType) }}
        </a-descriptions-item>
        <a-descriptions-item label="姓名" :span="2">
          {{ formData.name }}
        </a-descriptions-item>
        <a-descriptions-item label="手机号" :span="2">
          {{ formData.telephone }}
        </a-descriptions-item>
        <a-descriptions-item label="地区" :span="2">
          {{ formData.areaName }}
        </a-descriptions-item>
        <a-descriptions-item label="详细地址" :span="4">
          {{ formData.address }}
        </a-descriptions-item>
        <a-descriptions-item label="默认地址" :span="4">
          {{ formData.isDefault ? '是' : '否' }}
        </a-descriptions-item>
      </a-descriptions>
    </div>
  </a-modal>
</template>
<script>
export default {
  // 使用组件
  components: {
  },

  props: {
    id: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      // 是否可见
      visible: false,
      // 是否显示加载框
      loading: false,
      // 表单数据
      formData: {}
    }
  },
  created() {
    this.initFormData()
  },
  methods: {
    // 打开对话框 由父页面触发
    openDialog() {
      this.visible = true

      this.$nextTick(() => this.open())
    },
    // 关闭对话框
    closeDialog() {
      this.visible = false
      this.$emit('close')
    },
    // 初始化表单数据
    initFormData() {
      this.formData = {
        areaName: ''
      }
    },
    // 页面显示时触发
    open() {
      // 初始化数据
      this.initFormData()

      // 查询数据
      this.loadFormData()
    },
    // 查询数据
    async loadFormData() {
      this.loading = true
      await this.$api.baseData.address.get(this.id).then(data => {
        this.formData = data
        this.formData.areaName = this.formData.provinceName + ' / ' + this.formData.cityName + ' / ' + this.formData.districtName
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
