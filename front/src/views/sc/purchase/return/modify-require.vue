<template>
  <div class="app-container simple-app-container">
    <div v-permission="['purchase:return:modify']" v-loading="loading">
      <j-border>
        <j-form>
          <j-form-item label="仓库" required>
            {{ formData.sc.name }}
          </j-form-item>
          <j-form-item label="供应商" required>
            {{ formData.supplier.name }}
          </j-form-item>
          <j-form-item label="采购员">
            <user-selector
              v-model="formData.purchaserId"
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
          <j-form-item label="采购收货单" required>
            {{ formData.receiveSheet.code }}
          </j-form-item>
          <j-form-item />
          <j-form-item label="状态">
            <span v-if="$enums.PURCHASE_RETURN_STATUS.APPROVE_PASS.equalsCode(formData.status)" style="color: #52C41A;">{{ $enums.PURCHASE_RETURN_STATUS.getDesc(formData.status) }}</span>
            <span v-else-if="$enums.PURCHASE_RETURN_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" style="color: #F5222D;">{{ $enums.PURCHASE_RETURN_STATUS.getDesc(formData.status) }}</span>
            <span v-else style="color: #303133;">{{ $enums.PURCHASE_RETURN_STATUS.getDesc(formData.status) }}</span>
          </j-form-item>
          <j-form-item :span="16" :content-nest="false" label="拒绝理由">
            <a-input v-if="$enums.PURCHASE_RETURN_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" v-model="formData.refuseReason" read-only />
          </j-form-item>
          <j-form-item label="操作人">
            <span>{{ formData.createBy }}</span>
          </j-form-item>
          <j-form-item label="操作时间" :span="16">
            <span>{{ formData.createTime }}</span>
          </j-form-item>
          <j-form-item v-if="$enums.PURCHASE_RETURN_STATUS.APPROVE_PASS.equalsCode(formData.status) || $enums.PURCHASE_RETURN_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" label="审核人">
            <span>{{ formData.approveBy }}</span>
          </j-form-item>
          <j-form-item v-if="$enums.PURCHASE_RETURN_STATUS.APPROVE_PASS.equalsCode(formData.status) || $enums.PURCHASE_RETURN_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" label="审核时间" :span="16">
            <span>{{ formData.approveTime }}</span>
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

        <!-- 采购价 列自定义内容 -->
        <template v-slot:purchasePrice_default="{ row }">
          <span>{{ row.purchasePrice }}</span>
        </template>

        <!-- 库存数量 列自定义内容 -->
        <template v-slot:stockNum_default="{ row }">
          <span v-if="checkStockNum(row)">{{ row.stockNum }}</span>
          <span v-else style="color: #F5222D;">{{ row.stockNum }}</span>
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
          <span v-if="$utils.isFloatGeZero(row.purchasePrice) && $utils.isIntegerGeZero(row.returnNum)">{{ $utils.mul(row.purchasePrice, row.returnNum) }}</span>
        </template>

        <!-- 备注 列自定义内容 -->
        <template v-slot:description_default="{ row }">
          <a-input v-model="row.description" />
        </template>
      </vxe-grid>

      <order-time-line :id="id" />

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

      <j-border>
        <j-form label-width="140px">
          <j-form-item label="备注" :span="24" :content-nest="false">
            <a-textarea v-model.trim="formData.description" maxlength="200" />
          </j-form-item>
        </j-form>
      </j-border>
      <batch-add-product
        ref="batchAddProductDialog"
        :sc-id="formData.sc.id"
        @confirm="batchAddProduct"
      />

      <div style="text-align: center; background-color: #FFFFFF;padding: 8px 0;">
        <a-space>
          <a-button v-permission="['purchase:return:modify']" type="primary" :loading="loading" @click="updateOrder">保存</a-button>
          <a-button :loading="loading" @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </div>
</template>
<script>
import BatchAddProduct from '@/views/sc/purchase/batch-add-product'
import UserSelector from '@/components/Selector/UserSelector'
import Moment from 'moment'
export default {
  name: 'ModifyPurchaseReturnRequire',
  components: {
    UserSelector, BatchAddProduct
  },
  data() {
    return {
      id: this.$route.params.id,
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
        { field: 'isGift', title: '是否赠品', width: 80, formatter: ({ cellValue }) => { return cellValue ? '是' : '否' } },
        { field: 'purchasePrice', title: '采购价（元）', align: 'right', width: 120, slots: { default: 'purchasePrice_default' }},
        { field: 'taxCostPrice', title: '含税成本价（元）', align: 'right', width: 140 },
        { field: 'stockNum', title: '库存数量', align: 'right', width: 100, slots: { default: 'stockNum_default' }},
        { field: 'receiveNum', title: '收货数量', align: 'right', width: 100, formatter: ({ cellValue }) => { return this.$utils.isEmpty(cellValue) ? '-' : cellValue } },
        { field: 'remainNum', title: '剩余退货数量', align: 'right', width: 120, slots: { default: 'remainNum_default' }},
        { field: 'returnNum', title: '退货数量', align: 'right', width: 100, slots: { default: 'returnNum_default' }},
        { field: 'taxAmount', title: '含税金额', align: 'right', width: 120, slots: { default: 'taxAmount_default' }},
        { field: 'taxRate', title: '税率（%）', align: 'right', width: 100 },
        { field: 'description', title: '备注', width: 200, slots: { default: 'description_default' }}
      ],
      tableData: []
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
      this.loadData()
    },
    // 关闭对话框
    closeDialog() {
      this.$utils.closeCurrentPage(this.$parent)
    },
    // 初始化表单数据
    initFormData() {
      this.formData = {
        sc: {},
        supplier: {},
        receiveSheet: {},
        purchaserId: '',
        paymentDate: '',
        totalNum: 0,
        giftNum: 0,
        totalAmount: 0,
        description: '',
        // 是否允许修改付款日期
        allowModifyPaymentDate: false
      }

      this.tableData = []
    },
    // 加载数据
    loadData() {
      this.loading = true
      this.$api.sc.purchase.purchaseReturn.get(this.id).then(res => {
        if (!this.$enums.PURCHASE_RETURN_STATUS.CREATED.equalsCode(res.status) && !this.$enums.PURCHASE_RETURN_STATUS.APPROVE_REFUSE.equalsCode(res.status)) {
          this.$msg.error('采购退货单已审核通过，无法修改！')
          this.closeDialog()
          return
        }
        this.formData = Object.assign(this.formData, {
          sc: {
            id: res.scId,
            name: res.scName
          },
          supplier: {
            id: res.supplierId,
            name: res.supplierName
          },
          purchaserId: res.purchaserId || '',
          paymentDate: res.paymentDate || '',
          receiveSheet: {
            id: res.receiveSheetId,
            code: res.receiveSheetCode
          },
          description: res.description,
          status: res.status,
          createBy: res.createBy,
          createTime: res.createTime,
          approveBy: res.approveBy,
          approveTime: res.approveTime,
          refuseReason: res.refuseReason,
          totalNum: 0,
          giftNum: 0,
          totalAmount: 0
        })

        const tableData = res.details || []
        tableData.forEach(item => {
          item.isFixed = !this.$utils.isEmpty(item.receiveSheetDetailId)

          if (item.isFixed) {
            // 接口返回的剩余退货数量是最新的数据，应加上当前退货数量
            item.remainNum = this.$utils.add(item.remainNum, item.returnNum)
          }

          return item
        })
        this.tableData = tableData.map(item => Object.assign(this.emptyProduct(), item))

        this.supplierChange(this.formData.supplier.id)

        this.calcSum()
      }).finally(() => {
        this.loading = false
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
        purchasePrice: 0,
        taxCostPrice: '',
        stockNum: '',
        receiveNum: '',
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
      if (this.$utils.isEmpty(this.formData.receiveSheet)) {
        this.$msg.error('请先选择采购收货单！')
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

      this.$api.sc.purchase.purchaseOrder.searchProduct(this.formData.sc.id, queryString).then(res => {
        row.products = res
      })
    },
    // 选择商品
    handleSelectProduct(index, value, row) {
      this.tableData[index] = Object.assign(this.tableData[index], row ? row.products.filter(item => item.productId === value)[0] : value, {
        isGift: true,
        purchasePrice: 0
      })

      this.purchasePriceInput(this.tableData[index], this.tableData[index].purchasePrice)
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
          this.$msg.error('第' + (i + 1) + '行商品是采购收货单中的商品，不允许删除！')
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
      if (this.$utils.isEmpty(this.formData.receiveSheet)) {
        this.$msg.error('请先选择采购收货单！')
        return
      }
      this.$refs.batchAddProductDialog.openDialog()
    },
    purchasePriceInput(row, value) {
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
        return this.$utils.isFloatGeZero(t.purchasePrice) && this.$utils.isIntegerGeZero(t.returnNum)
      }).forEach(t => {
        const num = parseInt(t.returnNum)
        if (t.isGift) {
          giftNum = this.$utils.add(giftNum, num)
        } else {
          totalNum = this.$utils.add(totalNum, num)
        }

        totalAmount = this.$utils.add(totalAmount, this.$utils.mul(num, t.purchasePrice))
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
      if (this.$utils.isEmpty(this.formData.sc.id)) {
        this.$msg.error('仓库不允许为空！')
        return false
      }

      if (this.$utils.isEmpty(this.formData.supplier.id)) {
        this.$msg.error('供应商不允许为空！')
        return false
      }

      if (this.formData.allowModifyPaymentDate) {
        if (this.$utils.isEmpty(this.formData.paymentDate)) {
          this.$msg.error('付款日期不允许为空！')
          return false
        }
      }

      if (this.$utils.isEmpty(this.formData.receiveSheet.id)) {
        this.$msg.error('采购收货单不允许为空！')
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

        if (this.$utils.isEmpty(product.purchasePrice)) {
          this.$msg.error('第' + (i + 1) + '行商品采购价不允许为空！')
          return false
        }

        if (!this.$utils.isFloat(product.purchasePrice)) {
          this.$msg.error('第' + (i + 1) + '行商品采购价必须为数字！')
          return false
        }

        if (product.isGift) {
          if (parseFloat(product.purchasePrice) !== 0) {
            this.$msg.error('第' + (i + 1) + '行商品采购价必须等于0！')
            return false
          }
        } else {
          if (!this.$utils.isFloatGtZero(product.purchasePrice)) {
            this.$msg.error('第' + (i + 1) + '行商品采购价必须大于0！')
            return false
          }
        }

        if (!this.$utils.isNumberPrecision(product.purchasePrice, 2)) {
          this.$msg.error('第' + (i + 1) + '行商品采购价最多允许2位小数！')
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
              this.$msg.error('第' + (i + 1) + '行商品累计退货数量为' + (product.receiveNum - product.remainNum) + '，剩余退货数量为' + product.remainNum + '，本次退货数量不允许大于' + product.remainNum + '！')
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
        this.$msg.error('采购收货单中的商品必须全部或部分退货！')
        return false
      }

      return true
    },
    // 修改订单
    updateOrder() {
      if (!this.validData()) {
        return
      }

      const params = {
        id: this.id,
        scId: this.formData.sc.id,
        supplierId: this.formData.supplier.id,
        purchaserId: this.formData.purchaserId || '',
        paymentDate: this.formData.paymentDate || '',
        receiveSheetId: this.formData.receiveSheet.id,
        description: this.formData.description,
        products: this.tableData.filter(t => this.$utils.isIntegerGtZero(t.returnNum)).map(t => {
          const product = {
            productId: t.productId,
            returnNum: t.returnNum,
            description: t.description
          }

          if (t.isFixed) {
            product.receiveSheetDetailId = t.receiveSheetDetailId
          }

          return product
        })
      }

      this.loading = true
      this.$api.sc.purchase.purchaseReturn.updateOrder(params).then(res => {
        this.$msg.success('保存成功！')

        this.$emit('confirm')
        this.closeDialog()
      }).finally(() => {
        this.loading = false
      })
    },
    // 供应商改变时触发
    supplierChange(supplierId) {
      this.$api.sc.purchase.receiveSheet.getPaymentDate(supplierId).then(res => {
        if (res.allowModify) {
          // 如果允许修改付款日期
          if (this.$utils.isEmpty(this.formData.paymentDate)) {
            this.formData.paymentDate = res.paymentDate || ''
          }
        } else {
          // 不允许修改则按默认日期
          this.formData.paymentDate = res.paymentDate || ''
        }

        this.formData.allowModifyPaymentDate = res.allowModify
      })
    },
    // 检查库存数量
    checkStockNum(row) {
      const checkArr = this.tableData.filter(item => item.productId === row.productId).map(item => item.returnNum)
      if (this.$utils.isEmpty(checkArr)) {
        checkArr.push(0)
      }
      const totalReturnNum = checkArr.reduce((total, item) => {
        const returnNum = this.$utils.isIntegerGtZero(item) ? item : 0
        return this.$utils.add(total, returnNum)
      }, 0)

      return totalReturnNum <= row.stockNum
    }
  }
}
</script>
<style>
</style>
