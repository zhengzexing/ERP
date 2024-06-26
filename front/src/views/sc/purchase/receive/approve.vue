<template>
  <div class="app-container simple-app-container">
    <div v-permission="['purchase:receive:approve']" v-loading="loading">
      <j-border>
        <j-form>
          <j-form-item label="仓库">
            {{ formData.scName }}
          </j-form-item>
          <j-form-item label="供应商">
            {{ formData.supplierName }}
          </j-form-item>
          <j-form-item label="采购员">
            {{ formData.purchaserName }}
          </j-form-item>
          <j-form-item label="付款日期">
            {{ formData.paymentDate }}
          </j-form-item>
          <j-form-item label="实际到货日期">
            {{ formData.receiveDate }}
          </j-form-item>
          <j-form-item label="采购订单">
            <div v-if="!$utils.isEmpty(formData.purchaseOrderCode)">
              <a v-permission="['purchase:order:query']" @click="e => $refs.viewPurchaseOrderDetailDialog.openDialog()">{{ formData.purchaseOrderCode }}</a>
              <span v-no-permission="['purchase:order:query']">{{ formData.purchaseOrderCode }}</span>
            </div>
          </j-form-item>
          <j-form-item label="状态">
            <span v-if="$enums.RECEIVE_SHEET_STATUS.APPROVE_PASS.equalsCode(formData.status)" style="color: #52C41A;">{{ $enums.RECEIVE_SHEET_STATUS.getDesc(formData.status) }}</span>
            <span v-else-if="$enums.RECEIVE_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" style="color: #F5222D;">{{ $enums.RECEIVE_SHEET_STATUS.getDesc(formData.status) }}</span>
            <span v-else style="color: #303133;">{{ $enums.RECEIVE_SHEET_STATUS.getDesc(formData.status) }}</span>
          </j-form-item>
          <j-form-item label="拒绝理由" :span="16" :content-nest="false">
            <a-input v-if="$enums.RECEIVE_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" v-model="formData.refuseReason" read-only />
          </j-form-item>
          <j-form-item label="操作人">
            <span>{{ formData.createBy }}</span>
          </j-form-item>
          <j-form-item label="操作时间" :span="16">
            <span>{{ formData.createTime }}</span>
          </j-form-item>
          <j-form-item v-if="$enums.RECEIVE_SHEET_STATUS.APPROVE_PASS.equalsCode(formData.status) || $enums.RECEIVE_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" label="审核人">
            <span>{{ formData.approveBy }}</span>
          </j-form-item>
          <j-form-item v-if="$enums.RECEIVE_SHEET_STATUS.APPROVE_PASS.equalsCode(formData.status) || $enums.RECEIVE_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" label="审核时间" :span="16">
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
      >

        <!-- 含税金额 列自定义内容 -->
        <template v-slot:taxAmount_default="{ row }">
          <span v-if="$utils.isFloatGeZero(row.purchasePrice) && $utils.isIntegerGeZero(row.receiveNum)">{{ $utils.mul(row.purchasePrice, row.receiveNum) }}</span>
        </template>
      </vxe-grid>

      <order-time-line :id="id" />

      <j-border title="合计">
        <j-form label-width="140px">
          <j-form-item label="收货数量" :span="6">
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

      <div v-if="$enums.RECEIVE_SHEET_STATUS.CREATED.equalsCode(formData.status) || $enums.RECEIVE_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)" style="text-align: center; background-color: #FFFFFF;padding: 8px 0;">
        <a-space>
          <a-button v-permission="['purchase:receive:approve']" type="primary" :loading="loading" @click="approvePassOrder">审核通过</a-button>
          <a-button v-if="$enums.RECEIVE_SHEET_STATUS.CREATED.equalsCode(formData.status)" v-permission="['purchase:receive:approve']" type="danger" :loading="loading" @click="approveRefuseOrder">审核拒绝</a-button>
          <a-button :loading="loading" @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>

    <approve-refuse ref="approveRefuseDialog" @confirm="doApproveRefuse" />

    <!-- 采购订单查看窗口 -->
    <purchase-order-detail :id="formData.purchaseOrderId" ref="viewPurchaseOrderDetailDialog" />
  </div>
</template>
<script>
import ApproveRefuse from '@/components/ApproveRefuse'
import PurchaseOrderDetail from '@/views/sc/purchase/order/detail'

export default {
  components: {
    ApproveRefuse, PurchaseOrderDetail
  },
  data() {
    return {
      id: this.$route.params.id,
      // 是否显示加载框
      loading: false,
      // 表单数据
      formData: {},
      // 列表数据配置
      tableColumn: [
        { type: 'seq', width: 40 },
        { field: 'productCode', title: '商品编号', width: 120 },
        { field: 'productName', title: '商品名称', width: 260 },
        { field: 'skuCode', title: '商品SKU编号', width: 120 },
        { field: 'externalCode', title: '商品外部编号', width: 120 },
        { field: 'unit', title: '单位', width: 80 },
        { field: 'spec', title: '规格', width: 80 },
        { field: 'categoryName', title: '商品类目', width: 120 },
        { field: 'brandName', title: '商品品牌', width: 120 },
        { field: 'isGift', title: '是否赠品', width: 80, formatter: ({ cellValue }) => { return cellValue ? '是' : '否' } },
        { field: 'purchasePrice', title: '采购价（元）', align: 'right', width: 120 },
        { field: 'taxCostPrice', title: '含税成本价（元）', align: 'right', width: 140 },
        { field: 'stockNum', title: '库存数量', align: 'right', width: 100 },
        { field: 'orderNum', title: '采购数量', align: 'right', width: 100, formatter: ({ cellValue }) => { return this.$utils.isEmpty(cellValue) ? '-' : cellValue } },
        { field: 'remainNum', title: '剩余收货数量', align: 'right', width: 120, formatter: ({ cellValue }) => { return this.$utils.isEmpty(cellValue) ? '-' : cellValue } },
        { field: 'receiveNum', title: '收货数量', align: 'right', width: 100 },
        { field: 'taxAmount', title: '含税金额', align: 'right', width: 120, slots: { default: 'taxAmount_default' }},
        { field: 'taxRate', title: '税率（%）', align: 'right', width: 100 },
        { field: 'description', title: '备注', width: 200 }
      ],
      tableData: []
    }
  },
  computed: {
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
        scName: '',
        supplierName: '',
        purchaserName: '',
        paymentDate: '',
        receiveDate: '',
        purchaseOrderId: '',
        purchaseOrderCode: '',
        totalNum: 0,
        giftNum: 0,
        totalAmount: 0,
        description: ''
      }

      this.tableData = []
    },
    // 加载数据
    loadData() {
      this.loading = true
      this.$api.sc.purchase.receiveSheet.get(this.id).then(res => {
        if (!this.$enums.RECEIVE_SHEET_STATUS.CREATED.equalsCode(res.status) && !this.$enums.RECEIVE_SHEET_STATUS.APPROVE_REFUSE.equalsCode(res.status)) {
          this.$msg.error('采购收货单已审核通过，无需重复审核！')
          this.closeDialog()
          return
        }
        this.formData = {
          scName: res.scName,
          supplierName: res.supplierName,
          purchaserName: res.purchaserName || '',
          paymentDate: res.paymentDate || '',
          receiveDate: res.receiveDate,
          purchaseOrderId: res.purchaseOrderId || '',
          purchaseOrderCode: res.purchaseOrderCode || '',
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
        }
        this.tableData = res.details || []

        this.calcSum()
      }).finally(() => {
        this.loading = false
      })
    },
    // 计算汇总数据
    calcSum() {
      let totalNum = 0
      let giftNum = 0
      let totalAmount = 0

      this.tableData.filter(t => {
        return this.$utils.isFloatGeZero(t.purchasePrice) && this.$utils.isIntegerGeZero(t.receiveNum)
      }).forEach(t => {
        const num = parseInt(t.receiveNum)
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
    // 审核通过
    approvePassOrder() {
      this.$msg.confirm('对采购收货单执行审核通过操作？').then(() => {
        this.loading = true
        this.$api.sc.purchase.receiveSheet.approvePassOrder({
          id: this.id,
          description: this.formData.description
        }).then(res => {
          this.$msg.success('审核通过！')

          this.$emit('confirm')
          this.closeDialog()
        }).finally(() => {
          this.loading = false
        })
      })
    },
    // 审核拒绝
    approveRefuseOrder() {
      this.$refs.approveRefuseDialog.openDialog()
    },
    // 开始审核拒绝
    doApproveRefuse(reason) {
      this.loading = true
      this.$api.sc.purchase.receiveSheet.approveRefuseOrder({
        id: this.id,
        refuseReason: reason
      }).then(() => {
        this.$msg.success('审核拒绝！')

        this.$emit('confirm')
        this.closeDialog()
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
<style>
</style>
