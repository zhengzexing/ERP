<template>
  <div class="analysis">
    <a-row style="margin-top: 0" :gutter="[24, 24]">
      <a-col :sm="24" :md="12" :xl="6" @click="handleSetLineChartData('今日订单', 'today', formData.today.order.charts)">
        <chart-card :loading="loading" title="今日订单" :total="'￥ ' + formData.today.order.totalAmount" style="cursor: pointer;">
          <a-tooltip slot="action" title="今日销售订单、零售出库单、采购订单数据">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>数量<span> {{ formData.today.order.totalNum }}</span></div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" @click="handleSetLineChartData('今日退单', 'today', formData.today.returned.charts)">
        <chart-card :loading="loading" title="今日退单" :total="'￥ ' + formData.today.returned.totalAmount" style="cursor: pointer;">
          <a-tooltip slot="action" title="今日销售退单、零售退单、采购退单数据">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>数量<span> {{ formData.today.returned.totalNum }}</span></div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" @click="handleSetLineChartData('本月订单', 'sameMonth', formData.sameMonth.order.charts)">
        <chart-card :loading="loading" title="本月订单" :total="'￥ ' + formData.sameMonth.order.totalAmount" style="cursor: pointer;">
          <a-tooltip slot="action" title="本月销售订单、零售出库单、采购订单数据">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>数量<span> {{ formData.sameMonth.order.totalNum }}</span></div>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" @click="handleSetLineChartData('本月退单', 'sameMonth', formData.sameMonth.returned.charts)">
        <chart-card :loading="loading" title="本月退单" :total="'￥ ' + formData.sameMonth.returned.totalAmount" style="cursor: pointer;">
          <a-tooltip slot="action" title="本月销售退单、零售退单、采购退单数据">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>数量<span> {{ formData.sameMonth.returned.totalNum }}</span></div>
        </chart-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import ChartCard from '@/components/card/ChartCard'

export default {
  components: {
    ChartCard
  },
  data() {
    return {
      loading: false,
      formData: {
        today: {
          order: {
            totalAmount: '',
            totalNum: '',
            charts: []
          },
          returned: {
            totalAmount: '',
            totalNum: '',
            charts: []
          }
        },
        sameMonth: {
          order: {
            totalAmount: '',
            totalNum: '',
            charts: []
          },
          returned: {
            totalAmount: '',
            totalNum: '',
            charts: []
          }
        }
      }
    }
  },
  watch: {
    loading(val) {
      this.$emit('loading', val)
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    handleSetLineChartData(title, type, bizType) {
      this.$emit('handleSetLineChartData', title, type, bizType)
    },
    loadData() {
      this.loading = true
      this.$api.chart.orderChart.get().then(res => {
        this.formData = res

        this.handleSetLineChartData('今日订单', 'today', this.formData.today.order.charts)
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style lang="less" scoped>
</style>
