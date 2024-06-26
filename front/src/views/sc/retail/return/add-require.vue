<template>
  <div class="app-container simple-app-container">
    <div v-permission="['retail:return:add']" v-loading="loading">
      <j-border>
        <j-form>
          <j-form-item label="仓库" required>
            <store-center-selector
              v-model="formData.scId"
              :before-open="beforeSelectSc"
            />
          </j-form-item>
          <j-form-item label="会员" :required="retailConfig.retailReturnRequireMember">
            <member-selector
              v-model="formData.memberId"
              :before-open="beforeSelectMember"
              @input="memberChange"
            />
          </j-form-item>
          <j-form-item label="销售员">
            <user-selector
              v-model="formData.salerId"
              :before-open="beforeSelectSaler"
            />
          </j-form-item>
          <j-form-item label="付款日期" required>
            <a-date-picker
              v-model="formData.paymentDate"
              placeholder=""
              value-format="YYYY-MM-DD"
              :disabled="!formData.allowModifyPaymentDate"
              :disabled-date="(current) => {
                return current && current < moment().endOf('day');
              }"
            />
          </j-form-item>
          <j-form-item label="零售出库单" required>
            <retail-out-sheet-selector
              v-model="formData.outSheetId"
              @input="outSheetChange"
            />
          </j-form-item>
        </j-form>
      </j-border>
      <!-- 数据列表 -->
      <vxe-grid
        ref="grid"
        resizable
        show-overflow
        highlight-hover-row
        keep-source
        row-id="id"
        height="500"
        :data="tableData"
        :columns="tableColumn"
        :toolbar-config="toolbarConfig"
      >
        <!-- 工具栏 -->
        <template v-slot:toolbar_buttons>
          <a-space>
            <a-button type="primary" icon="plus" @click="addProduct">新增</a-button>
            <a-button type="danger" icon="delete" @click="delProduct">删除</a-button>
            <a-button icon="plus" @click="openBatchAddProductDialog">批量添加商品</a-button>
            <a-button icon="number" @click="batchInputReturnNum">批量录入数量</a-button>
          </a-space>
        </template>

        <!-- 商品名称 列自定义内容 -->
        <template v-slot:productName_default="{ row, rowIndex }">
          <a-auto-complete
            v-if="!row.isFixed"
            v-model="row.productName"
            style="width: 100%;"
            placeholder=""
            value-key="productName"
            @search="e => queryProduct(e, row)"
            @select="e => handleSelectProduct(rowIndex, e, row)"
          >
            <template slot="dataSource">
              <a-select-option v-for="(item, index) in row.products" :key="index" :value="item.productId">
                {{ item.productCode }} {{ item.productName }}
              </a-select-option>
            </template>
          </a-auto-complete>
          <span v-else>{{ row.productName }}</span>
        </template>

        <!-- 是否赠品 列自定义内容 -->
        <template v-slot:isGift_default="{ row }">
          <span>{{ row.isGift ? '是' : '否' }}</span>
        </template>

        <!-- 剩余退货数量 列自定义内容 -->
        <template v-slot:remainNum_default="{ row }">
          <span v-if="$utils.isEmpty(row.remainNum)">-</span>
          <span v-else-if="$utils.isIntegerGeZero(row.returnNum)">{{ Math.max(0, $utils.sub(row.remainNum, row.returnNum)) }}</span>
          <span v-else>{{ row.remainNum }}</span>
        </template>

        <!-- 退货数量 列自定义内容 -->
        <template v-slot:returnNum_default="{ row }">
          <a-input v-model="row.returnNum" class="number-input" @input="e => returnNumInput(e.target.value)" />
        </template>

        <!-- 含税金额 列自定义内容 -->
        <template v-slot:taxAmount_default="{ row }">
          <span v-if="$utils.isFloatGeZero(row.taxPrice) && $utils.isIntegerGeZero(row.returnNum)">{{ $utils.mul(row.taxPrice, row.returnNum) }}</span>
        </template>

        <!-- 备注 列自定义内容 -->
        <template v-slot:description_default="{ row }">
          <a-input v-model="row.description" />
        </template>
      </vxe-grid>

      <j-border title="合计">
        <j-form label-width="140px">
          <j-form-item label="退货数量" :span="6">
            <a-input v-model="formData.totalNum" class="number-input" read-only />
          </j-form-item>
          <j-form-item label="赠品数量" :span="6">
            <a-input v-model="formData.giftNum" class="number-input" read-only />
          </j-form-item>
          <j-form-item label="含税总金额" :span="6">
            <a-input v-model="formData.totalAmount" class="number-input" read-only />
          </j-form-item>
        </j-form>
      </j-border>

      <j-border title="支付方式">
        <pay-type ref="payType" />
      </j-border>

      <j-border>
        <j-form label-width="140px">
          <j-form-item label="备注" :span="24" :content-nest="false">
            <a-textarea v-model.trim="formData.description" maxlength="200" />
          </j-form-item>
        </j-form>
      </j-border>

      <batch-add-product
        ref="batchAddProductDialog"
        :sc-id="formData.scId"
        :is-return="true"
        @confirm="batchAddProduct"
      />
      <div style="text-align: center; background-color: #FFFFFF;padding: 8px 0;">
        <a-space>
          <a-button v-permission="['retail:return:add']" type="primary" :loading="loading" @click="createOrder">保存</a-button>
          <a-button v-permission="['retail:return:approve']" type="primary" :loading="loading" @click="directApprovePassOrder">审核通过</a-button>
          <a-button :loading="loading" @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </div>
</template>
<script>
import StoreCenterSelector from '@/components/Selector/StoreCenterSelector'
import MemberSelector from '@/components/Selector/MemberSelector'
import UserSelector from '@/components/Selector/UserSelector'
import RetailOutSheetSelector from './RetailOutSheetSelector'
import BatchAddProduct from '@/views/sc/retail/batch-add-product'
import Moment from 'moment'
import PayType from '@/views/sc/pay-type/index'
export default {
  name: 'AddRetailReturnRequire',
  components: {
    StoreCenterSelector, MemberSelector, UserSelector, RetailOutSheetSelector, BatchAddProduct, PayType
  },
  data() {
    return {
      // 是否显示加载框
      loading: false,
      // 表单数据
      formData: {},
      // 工具栏配置
      toolbarConfig: {
        // 缩放
        zoom: false,
        // 自定义表头
        custom: false,
        // 右侧是否显示刷新按钮
        refresh: false,
        // 自定义左侧工具栏
        slots: {
          buttons: 'toolbar_buttons'
        }
      },
      // 列表数据配置
      tableColumn: [
        { type: 'checkbox', width: 40 },
        { field: 'productCode', title: '商品编号', width: 120 },
        { field: 'productName', title: '商品名称', width: 260, slots: { default: 'productName_default' }},
        { field: 'skuCode', title: '商品SKU编号', width: 120 },
        { field: 'externalCode', title: '商品外部编号', width: 120 },
        { field: 'unit', title: '单位', width: 80 },
        { field: 'spec', title: '规格', width: 80 },
        { field: 'categoryName', title: '商品类目', width: 120 },
        { field: 'brandName', title: '商品品牌', width: 120 },
        { field: 'retailPrice', title: '参考零售价（元）', align: 'right', width: 150 },
        { field: 'isGift', title: '是否赠品', width: 80, slots: { default: 'isGift_default' }},
        { field: 'discountRate', title: '折扣（%）', align: 'right', width: 120 },
        { field: 'taxPrice', title: '价格（元）', align: 'right', width: 120 },
        { field: 'outNum', title: '出库数量', align: 'right', width: 100, formatter: ({ cellValue }) => { return this.$utils.isEmpty(cellValue) ? '-' : cellValue } },
        { field: 'remainNum', title: '剩余退货数量', align: 'right', width: 120, slots: { default: 'remainNum_default' }},
        { field: 'returnNum', title: '退货数量', align: 'right', width: 100, slots: { default: 'returnNum_default' }},
        { field: 'taxAmount', title: '含税金额', align: 'right', width: 120, slots: { default: 'taxAmount_default' }},
        { field: 'taxRate', title: '税率（%）', align: 'right', width: 100 },
        { field: 'description', title: '备注', width: 200, slots: { default: 'description_default' }}
      ],
      tableData: [],
      retailConfig: {},
      canChangeMember: false
    }
  },
  computed: {
    moment() {
      return Moment
    }
  },
  created() {
    this.openDialog()
  },
  methods: {
    // 打开对话框 由父页面触发
    openDialog() {
      // 初始化表单数据
      this.initFormData()
    },
    // 关闭对话框
    closeDialog() {
      this.$utils.closeCurrentPage(this.$parent)
    },
    // 初始化表单数据
    async initFormData() {
      this.formData = {
        scId: '',
        memberId: '',
        outSheetId: '',
        salerId: '',
        paymentDate: this.$utils.formatDate(Moment().add(1, 'M')),
        totalNum: 0,
        giftNum: 0,
        totalAmount: 0,
        description: '',
        // 是否允许修改付款日期
        allowModifyPaymentDate: true
      }

      this.tableData = []
      this.canChangeMember = false
      await this.$api.sc.retail.retailConfig.get().then(data => {
        this.retailConfig = data
      })
    },
    emptyProduct() {
      return {
        id: this.$utils.uuid(),
        productId: '',
        productCode: '',
        productName: '',
        skuCode: '',
        externalCode: '',
        unit: '',
        spec: '',
        categoryName: '',
        brandName: '',
        retailPrice: 0,
        taxPrice: '',
        discountRate: 100,
        outNum: '',
        remainNum: '',
        returnNum: '',
        taxRate: '',
        isGift: true,
        taxAmount: '',
        description: '',
        isFixed: false,
        products: []
      }
    },
    // 新增商品
    addProduct() {
      if (this.$utils.isEmpty(this.formData.outSheetId)) {
        this.$msg.error('请先选择零售出库单！')
        return
      }
      this.tableData.push(this.emptyProduct())
    },
    // 搜索商品
    queryProduct(queryString, row) {
      if (this.$utils.isEmpty(queryString)) {
        row.products = []
        return
      }

      this.$api.sc.retail.outSheet.searchProduct(this.formData.scId, queryString, true).then(res => {
        row.products = res
      })
    },
    // 选择商品
    handleSelectProduct(index, value, row) {
      value = row ? row.products.filter(item => item.productId === value)[0] : value
      this.tableData[index] = Object.assign(this.tableData[index], value, {
        isGift: true,
        taxPrice: 0
      })

      this.taxPriceInput(this.tableData[index], this.tableData[index].taxPrice)
    },
    // 删除商品
    delProduct() {
      const records = this.$refs.grid.getCheckboxRecords()
      if (this.$utils.isEmpty(records)) {
        this.$msg.error('请选择要删除的商品数据！')
        return
      }

      for (let i = 0; i < records.length; i++) {
        if (records[i].isFixed) {
          this.$msg.error('第' + (i + 1) + '行商品是零售出库单中的商品，不允许删除！')
          return
        }
      }
      this.$msg.confirm('是否确定删除选中的商品？').then(() => {
        const tableData = this.tableData.filter(t => {
          const tmp = records.filter(item => item.id === t.id)
          return this.$utils.isEmpty(tmp)
        })

        this.tableData = tableData

        this.calcSum()
      })
    },
    openBatchAddProductDialog() {
      if (this.$utils.isEmpty(this.formData.outSheetId)) {
        this.$msg.error('请先选择零售出库单！')
        return
      }
      this.$refs.batchAddProductDialog.openDialog()
    },
    taxPriceInput(row, value) {
      this.calcSum()
    },
    returnNumInput(value) {
      this.calcSum()
    },
    // 计算汇总数据
    calcSum() {
      let totalNum = 0
      let giftNum = 0
      let totalAmount = 0

      this.tableData.filter(t => {
        return this.$utils.isFloatGeZero(t.taxPrice) && this.$utils.isIntegerGeZero(t.returnNum)
      }).forEach(t => {
        const num = parseInt(t.returnNum)
        if (t.isGift) {
          giftNum = this.$utils.add(giftNum, num)
        } else {
          totalNum = this.$utils.add(totalNum, num)
        }

        totalAmount = this.$utils.add(totalAmount, this.$utils.mul(num, t.taxPrice))
      })

      this.formData.totalNum = totalNum
      this.formData.giftNum = giftNum
      this.formData.totalAmount = totalAmount
    },
    // 批量录入数量
    batchInputReturnNum() {
      const records = this.$refs.grid.getCheckboxRecords()
      if (this.$utils.isEmpty(records)) {
        this.$msg.error('请选择商品数据！')
        return
      }

      this.$msg.prompt('请输入退货数量', {
        inputPattern: this.$utils.PATTERN_IS_INTEGER_GE_ZERO,
        inputErrorMessage: '退货数量必须为整数并且不小于0',
        title: '批量录入数量'
      }).then(({ value }) => {
        records.forEach(t => {
          t.returnNum = value

          this.returnNumInput(value)
        })
      })
    },
    // 批量新增商品
    batchAddProduct(productList) {
      productList.forEach(item => {
        this.tableData.push(this.emptyProduct())
        this.handleSelectProduct(this.tableData.length - 1, item)
      })
    },
    // 校验数据
    validData() {
      if (this.$utils.isEmpty(this.formData.scId)) {
        this.$msg.error('仓库不允许为空！')
        return false
      }

      if (this.retailConfig.retailReturnRequireMember && this.$utils.isEmpty(this.formData.memberId)) {
        this.$msg.error('会员不允许为空！')
        return false
      }

      if (this.formData.allowModifyPaymentDate) {
        if (this.$utils.isEmpty(this.formData.paymentDate)) {
          this.$msg.error('付款日期不允许为空！')
          return false
        }
      }

      if (this.$utils.isEmpty(this.formData.outSheetId)) {
        this.$msg.error('零售出库单不允许为空！')
        return false
      }

      if (this.$utils.isEmpty(this.tableData)) {
        this.$msg.error('请录入商品！')
        return false
      }

      for (let i = 0; i < this.tableData.length; i++) {
        const product = this.tableData[i]

        if (this.$utils.isEmpty(product.productId)) {
          this.$msg.error('第' + (i + 1) + '行商品不允许为空！')
          return false
        }

        if (product.isGift) {
          if (parseFloat(product.taxPrice) !== 0) {
            this.$msg.error('第' + (i + 1) + '行商品价格必须等于0！')
            return false
          }
        } else {
          if (!this.$utils.isFloatGtZero(product.taxPrice)) {
            this.$msg.error('第' + (i + 1) + '行商品价格必须大于0！')
            return false
          }
        }

        if (!this.$utils.isNumberPrecision(product.taxPrice, 2)) {
          this.$msg.error('第' + (i + 1) + '行商品价格最多允许2位小数！')
          return false
        }

        if (!this.$utils.isEmpty(product.returnNum)) {
          if (!this.$utils.isInteger(product.returnNum)) {
            this.$msg.error('第' + (i + 1) + '行商品退货数量必须为整数！')
            return false
          }

          if (product.isFixed) {
            if (!this.$utils.isIntegerGeZero(product.returnNum)) {
              this.$msg.error('第' + (i + 1) + '行商品退货数量不允许小于0！')
              return false
            }
          } else {
            if (!this.$utils.isIntegerGtZero(product.returnNum)) {
              this.$msg.error('第' + (i + 1) + '行商品退货数量必须大于0！')
              return false
            }
          }

          if (product.isFixed) {
            if (product.returnNum > product.remainNum) {
              this.$msg.error('第' + (i + 1) + '行商品累计退货数量为' + (product.outNum - product.remainNum) + '，剩余退货数量为' + product.remainNum + '，本次退货数量不允许大于' + product.remainNum + '！')
              return false
            }
          }
        } else {
          if (!product.isFixed) {
            this.$msg.error('第' + (i + 1) + '行商品退货数量不允许为空！')
            return false
          }
        }
      }

      if (this.tableData.filter(item => this.$utils.isIntegerGtZero(item.returnNum)).length === 0) {
        this.$msg.error('零售出库单中的商品必须全部或部分退货！')
        return false
      }

      if (!this.$refs.payType.validData()) {
        return false
      }

      const payTypes = this.$refs.payType.getTableData()
      const totalPayAmount = payTypes.reduce((tot, item) => this.$utils.add(tot, item.payAmount), 0)
      if (!this.$utils.eq(this.formData.totalAmount, totalPayAmount)) {
        this.$msg.error('所有支付方式的支付金额不等于含税总金额，请检查！')
        return false
      }

      return true
    },
    // 创建订单
    createOrder() {
      if (!this.validData()) {
        return
      }

      const params = {
        scId: this.formData.scId,
        memberId: this.formData.memberId,
        salerId: this.formData.salerId || '',
        paymentDate: this.formData.paymentDate || '',
        outSheetId: this.formData.outSheetId,
        description: this.formData.description,
        required: true,
        payTypes: this.$refs.payType.getTableData().map(t => {
          return {
            id: t.payTypeId,
            payAmount: t.payAmount,
            text: t.text
          }
        }),
        products: this.tableData.filter(t => this.$utils.isIntegerGtZero(t.returnNum)).map(t => {
          const product = {
            productId: t.productId,
            oriPrice: t.retailPrice,
            returnNum: t.returnNum,
            description: t.description
          }

          if (t.isFixed) {
            product.outSheetDetailId = t.id
          }

          return product
        })
      }

      this.loading = true
      this.$api.sc.retail.retailReturn.createOrder(params).then(res => {
        this.$msg.success('保存成功！')

        this.$emit('confirm')
        this.closeDialog()
      }).finally(() => {
        this.loading = false
      })
    },
    // 直接审核通过订单
    directApprovePassOrder() {
      if (!this.validData()) {
        return
      }

      const params = {
        scId: this.formData.scId,
        memberId: this.formData.memberId,
        salerId: this.formData.salerId,
        paymentDate: this.formData.paymentDate || '',
        outSheetId: this.formData.outSheetId,
        description: this.formData.description,
        payTypes: this.$refs.payType.getTableData().map(t => {
          return {
            id: t.payTypeId,
            payAmount: t.payAmount,
            text: t.text
          }
        }),
        products: this.tableData.filter(t => this.$utils.isIntegerGtZero(t.returnNum)).map(t => {
          const product = {
            productId: t.productId,
            returnNum: t.returnNum,
            description: t.description
          }

          if (t.isFixed) {
            product.outSheetDetailId = t.id
          }

          return product
        })
      }

      this.$msg.confirm('对零售退货单执行审核通过操作？').then(() => {
        this.loading = true
        this.$api.sc.retail.retailReturn.directApprovePassOrder(params).then(res => {
          this.$msg.success('审核通过！')

          this.$emit('confirm')
          this.closeDialog()
        }).finally(() => {
          this.loading = false
        })
      })
    },
    // 选择零售出库单
    outSheetChange(e) {
      // 只要选择了零售出库单，清空所有商品，然后将零售出库单中所有的明细列出来
      this.$refs.payType.setTableData([])
      if (!this.$utils.isEmpty(e)) {
        this.loading = true
        this.$api.sc.retail.outSheet.getWithReturn(e).then(res => {
          const tableData = this.tableData.filter(item => !item.isFixed)
          let outSheetDetails = res.details || []
          outSheetDetails = outSheetDetails.map(item => {
            item.isFixed = true
            return Object.assign(this.emptyProduct(), item)
          })

          this.tableData = [...outSheetDetails, ...tableData]

          this.formData.scId = res.scId

          this.formData.memberId = res.memberId

          this.canChangeMember = this.$utils.isEmpty(this.formData.memberId)

          if (!this.$utils.isEmpty(res.salerId)) {
            this.formData.salerId = res.salerId
          }

          this.memberChange(this.formData.memberId)
        }).finally(() => {
          this.loading = false
        })

        this.$api.selector.getOrderPayType({
          orderId: e
        }).then(res => {
          this.$refs.payType.setTableData(res || [])
        })
      }
    },
    beforeSelectSc() {
      if (!this.beforeSelectComponents()) {
        return false
      }

      this.$msg.error('由于“零售退货单关联零售出库单”，不允许修改仓库！')
      return false
    },
    beforeSelectMember() {
      if (!this.beforeSelectComponents()) {
        return false
      }

      if (this.canChangeMember) {
        return true
      }

      this.$msg.error('由于“零售退货单关联零售出库单”，不允许修改会员！')
      return false
    },
    beforeSelectSaler() {
      return this.beforeSelectComponents()
    },
    beforeSelectComponents() {
      if (this.$utils.isEmpty(this.formData.outSheetId)) {
        this.$msg.error('请先选择零售出库单！')
        return false
      }

      return true
    },
    // 会员改变时触发
    memberChange(memberId) {
      this.$api.sc.retail.outSheet.getPaymentDate(memberId).then(res => {
        this.formData.paymentDate = res.paymentDate || ''
        this.formData.allowModifyPaymentDate = res.allowModify
      })
    }
  }
}
</script>
<style>
</style>
