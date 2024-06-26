<template>
  <a-modal v-model="visible" :mask-closable="false" width="40%" title="修改" :dialog-style="{ top: '20px' }" :footer="null">
    <div v-if="visible" v-permission="['base-data:member:modify']" v-loading="loading">
      <a-form-model ref="form" layout="vertical" :model="formData" :rules="rules">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="编号" prop="code">
              <a-input v-model.trim="formData.code" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model.trim="formData.name" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="性别" prop="gender">
              <a-select v-model="formData.gender" allow-clear>
                <a-select-option v-for="item in $enums.GENDER.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="会员手机号" prop="telephone">
              <a-input v-model.trim="formData.telephone" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="电子邮箱" prop="email">
              <a-input v-model.trim="formData.email" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="出生日期" prop="birthday">
              <a-date-picker
                v-model="formData.birthday"
                placeholder=""
                value-format="YYYY-MM-DD"
                :disabled-date="(current) => {
                  return current && current > moment().endOf('day');
                }"
              />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="注册门店" prop="shopId">
              <shop-selector v-model="formData.shopId" />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="所属导购" prop="guiderId">
              <user-selector v-model="formData.guiderId" />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="入会日期" prop="joinDay">
              <a-date-picker
                v-model="formData.joinDay"
                placeholder=""
                value-format="YYYY-MM-DD"
                :disabled-date="(current) => {
                  return current && current > moment().endOf('day');
                }"
              />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-model-item label="状态" prop="available">
              <a-select v-model="formData.available" allow-clear>
                <a-select-option v-for="item in $enums.AVAILABLE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-model-item label="备注" prop="description">
              <a-textarea v-model.trim="formData.description" />
            </a-form-model-item>
          </a-col>
        </a-row>
        <div class="form-modal-footer">
          <a-space>
            <a-button type="primary" :loading="loading" html-type="submit" @click="submit">保存</a-button>
            <a-button :loading="loading" @click="closeDialog">取消</a-button>
          </a-space>
        </div>
      </a-form-model>
    </div>
  </a-modal>
</template>
<script>
import moment from 'moment'
import { validCode, isEmail } from '@/utils/validate'
import ShopSelector from '@/components/Selector/ShopSelector'
import UserSelector from '@/components/Selector/UserSelector'

export default {
  // 使用组件
  components: {
    ShopSelector, UserSelector
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
      formData: {},
      // 表单校验规则
      rules: {
        code: [
          { required: true, message: '请输入编号' },
          { validator: validCode }
        ],
        name: [
          { required: true, message: '请输入名称' }
        ],
        gender: [
          { required: true, message: '请选择性别' }
        ],
        email: [
          {
            validator: (rule, value, callback) => {
              if (this.$utils.isEmpty(value) || isEmail(value)) {
                return callback()
              } else {
                return callback(new Error('邮箱地址格式不正确'))
              }
            }
          }
        ],
        joinDay: [
          { required: true, message: '请选择入会日期' }
        ],
        available: [
          { required: true, message: '请选择状态' }
        ]
      }
    }
  },
  computed: {
    moment() {
      return moment
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
        id: '',
        code: '',
        name: '',
        gender: '',
        telephone: '',
        email: '',
        shopId: '',
        guiderId: '',
        birthday: '',
        joinDay: '',
        available: '',
        description: ''
      }
    },
    // 提交表单事件
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.loading = true
          const params = Object.assign({}, this.formData)

          this.$api.baseData.member.modify(params).then(() => {
            this.$msg.success('修改成功！')
            this.$emit('confirm')
            this.visible = false
          }).finally(() => {
            this.loading = false
          })
        }
      })
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
      await this.$api.baseData.member.get(this.id).then(data => {
        this.formData = data
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
