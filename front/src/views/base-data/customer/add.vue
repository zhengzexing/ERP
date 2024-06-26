<template>
  <a-modal v-model="visible" :mask-closable="false" width="40%" title="新增" :dialog-style="{ top: '20px' }" :footer="null">
    <div v-if="visible" v-permission="['base-data:customer:add']" v-loading="loading">
      <a-form-model ref="form" layout="vertical" :model="formData" :rules="rules">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="编号" prop="code">
              <a-input v-model.trim="formData.code" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model.trim="formData.name" allow-clear @input="e => changeName(e.target.value)" />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="助记码" prop="mnemonicCode">
              <a-input v-model.trim="formData.mnemonicCode" allow-clear />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="联系人" prop="contact">
              <a-input v-model.trim="formData.contact" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="联系电话" prop="telephone">
              <a-input v-model.trim="formData.telephone" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="电子邮箱" prop="email">
              <a-input v-model.trim="formData.email" allow-clear />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="邮编" prop="zipCode">
              <a-input v-model.trim="formData.zipCode" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="传真" prop="fax">
              <a-input v-model.trim="formData.fax" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="地区" prop="city">
              <city-selector v-model="formData.city" :only-final="true" />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="地址" prop="address">
              <a-input v-model.trim="formData.address" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="结账方式" prop="settleType">
              <a-select v-model="formData.settleType" allow-clear>
                <a-select-option v-for="item in $enums.SETTLE_TYPE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="统一社会信用代码" prop="creditCode">
              <a-input v-model.trim="formData.creditCode" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="纳税人识别号" prop="taxIdentifyNo">
              <a-input v-model.trim="formData.taxIdentifyNo" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="开户银行" prop="bankName">
              <a-input v-model.trim="formData.bankName" allow-clear />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-model-item label="户名" prop="accountName">
              <a-input v-model.trim="formData.accountName" allow-clear />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="银行账号" prop="accountNo">
              <a-input v-model.trim="formData.accountNo" allow-clear />
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
import CitySelector from '@/components/Selector/CitySelector'
import { validCode, isEmail } from '@/utils/validate'
export default {
  components: {
    CitySelector
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
        mnemonicCode: [
          { required: true, message: '请输入助记码' }
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
        settleType: [
          { required: true, message: '请选择结账方式' }
        ]
      }
    }
  },
  computed: {
  },
  created() {
    // 初始化表单数据
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
        code: '',
        name: '',
        mnemonicCode: '',
        contact: '',
        telephone: '',
        email: '',
        zipCode: '',
        fax: '',
        city: [],
        address: '',
        receiver: '',
        receiveTelephone: '',
        receiveAddress: '',
        settleType: '',
        creditCode: '',
        taxIdentifyNo: '',
        bankName: '',
        accountName: '',
        accountNo: '',
        description: ''
      }
    },
    // 提交表单事件
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$msg.confirm('新增后结账方式不允许修改，请仔细核对结账方式是否正确').then(() => {
            this.loading = true
            const params = Object.assign({}, this.formData)
            params.cityId = this.$utils.isEmpty(params.city) ? '' : params.city[params.city.length - 1]
            delete params.city
            this.$api.baseData.customer.create(params).then(() => {
              this.$msg.success('新增成功！')
              // 初始化表单数据
              this.initFormData()
              this.$emit('confirm')
              this.visible = false
            }).finally(() => {
              this.loading = false
            })
          })
        }
      })
    },
    // 页面显示时触发
    open() {
      // 初始化表单数据
      this.initFormData()
    },
    // 名称改变
    changeName(e) {
      this.formData.mnemonicCode = this.$utils.getCamelCharsUpperCase(e)
    }
  }
}
</script>
