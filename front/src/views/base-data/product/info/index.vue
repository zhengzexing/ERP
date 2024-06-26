<template>
  <div>
    <div v-show="visible" v-permission="['base-data:product:info:query']" class="app-container">
      <!-- 数据列表 -->
      <vxe-grid
        id="ProductInfo"
        ref="grid"
        resizable
        show-overflow
        highlight-hover-row
        keep-source
        row-id="id"
        :proxy-config="proxyConfig"
        :columns="tableColumn"
        :toolbar-config="toolbarConfig"
        :pager-config="{}"
        :loading="loading"
        :height="$defaultTableHeight"
      >
        <template v-slot:form>
          <j-border>
            <j-form @collapse="$refs.grid.refreshColumn()">
              <j-form-item label="编号">
                <a-input v-model="searchFormData.code" allow-clear />
              </j-form-item>
              <j-form-item label="名称">
                <a-input v-model="searchFormData.name" allow-clear />
              </j-form-item>
              <j-form-item label="简称">
                <a-input v-model="searchFormData.shortName" allow-clear />
              </j-form-item>
              <j-form-item label="SKU编号">
                <a-input v-model="searchFormData.skuCode" allow-clear />
              </j-form-item>
              <j-form-item label="类目">
                <product-category-selector v-model="searchFormData.categoryId" />
              </j-form-item>
              <j-form-item label="品牌">
                <product-brand-selector v-model="searchFormData.brandId" />
              </j-form-item>
              <j-form-item label="商品类型">
                <a-select v-model="searchFormData.productType" placeholder="全部" allow-clear>
                  <a-select-option v-for="item in $enums.PRODUCT_TYPE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
                </a-select>
              </j-form-item>
              <j-form-item label="创建日期" :content-nest="false">
                <div class="date-range-container">
                  <a-date-picker v-model="searchFormData.startTime" placeholder="" value-format="YYYY-MM-DD 00:00:00" />
                  <span class="date-split">至</span>
                  <a-date-picker
                    v-model="searchFormData.endTime"
                    placeholder=""
                    value-format="YYYY-MM-DD 23:59:59"
                  />
                </div>
              </j-form-item>
              <j-form-item label="状态">
                <a-select v-model="searchFormData.available" placeholder="全部" allow-clear>
                  <a-select-option v-for="item in $enums.AVAILABLE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
                </a-select>
              </j-form-item>
            </j-form>
          </j-border>
        </template>
        <!-- 工具栏 -->
        <template v-slot:toolbar_buttons>
          <a-space>
            <a-button type="primary" icon="search" @click="search">查询</a-button>
            <a-button v-permission="['base-data:product:info:add']" type="primary" icon="plus" @click="$router.push('/product/info/add')">新增</a-button>
            <a-button v-permission="['base-data:product:info:import']" icon="cloud-upload" @click="$refs.importer.openDialog()">导入Excel</a-button>
          </a-space>
        </template>

        <!-- 状态 列自定义内容 -->
        <template v-slot:available_default="{ row }">
          <available-tag :available="row.available" />
        </template>

        <!-- 操作 列自定义内容 -->
        <template v-slot:action_default="{ row }">
          <a-button v-permission="['base-data:product:info:query']" type="link" @click="e => { id = row.id;$nextTick(() => $refs.viewDialog.openDialog()) }">查看</a-button>
          <a-button v-permission="['base-data:product:info:modify']" type="link" @click="$router.push('/product/info/modify/' + row.id)">修改</a-button>
        </template>
      </vxe-grid>

      <!-- 查看窗口 -->
      <detail :id="id" ref="viewDialog" />
    </div>

    <product-importer ref="importer" @confirm="search" />
  </div>
</template>

<script>
import ProductBrandSelector from '@/components/Selector/ProductBrandSelector'
import ProductCategorySelector from '@/components/Selector/ProductCategorySelector'
import AvailableTag from '@/components/Tag/Available'
import Detail from './detail'
import ProductImporter from '@/components/Importer/ProductImporter'

export default {
  name: 'ProductInfo',
  components: {
    ProductBrandSelector, ProductCategorySelector, Detail, AvailableTag, ProductImporter
  },
  data() {
    return {
      loading: false,
      visible: true,
      // 当前行数据
      id: '',
      ids: [],
      // 查询列表的查询条件
      searchFormData: {
        code: '',
        name: '',
        skuCode: '',
        categoryId: '',
        brandId: '',
        startTime: '',
        endTime: '',
        available: this.$enums.AVAILABLE.ENABLE.code
      },
      // 工具栏配置
      toolbarConfig: {
        // 自定义左侧工具栏
        slots: {
          buttons: 'toolbar_buttons'
        }
      },
      // 列表数据配置
      tableColumn: [
        { type: 'checkbox', width: 40 },
        { field: 'code', title: '编号', width: 120 },
        { field: 'name', title: '名称', minWidth: 160 },
        { field: 'shortName', title: '简称', width: 140 },
        { field: 'skuCode', title: 'SKU编号', width: 120 },
        { field: 'categoryName', title: '类目', width: 120 },
        { field: 'brandName', title: '品牌', width: 120 },
        { field: 'productType', title: '商品类型', width: 120, formatter: ({ cellValue }) => { return this.$enums.PRODUCT_TYPE.getDesc(cellValue) } },
        { field: 'available', title: '状态', width: 80, slots: { default: 'available_default' }},
        { field: 'createTime', title: '创建时间', width: 170 },
        { field: 'updateTime', title: '修改时间', width: 170 },
        { title: '操作', width: 120, fixed: 'right', slots: { default: 'action_default' }}
      ],
      // 请求接口配置
      proxyConfig: {
        props: {
          // 响应结果列表字段
          result: 'datas',
          // 响应结果总条数字段
          total: 'totalCount'
        },
        ajax: {
          // 查询接口
          query: ({ page, sorts, filters }) => {
            return this.$api.baseData.product.info.query(this.buildQueryParams(page))
          }
        }
      }
    }
  },
  created() {
  },
  methods: {
    // 列表发生查询时的事件
    search() {
      this.$refs.grid.commitProxy('reload')
    },
    // 查询前构建查询参数结构
    buildQueryParams(page) {
      return Object.assign({
        pageIndex: page.currentPage,
        pageSize: page.pageSize
      }, this.buildSearchFormData())
    },
    // 查询前构建具体的查询参数
    buildSearchFormData() {
      return Object.assign({}, this.searchFormData)
    },
    handleCommand({ key }) {
      if (key === 'batchEnable') {
        this.batchEnable()
      } else if (key === 'batchUnable') {
        this.batchUnable()
      }
    },
    // 批量停用
    batchUnable() {
      const records = this.$refs.grid.getCheckboxRecords()

      if (this.$utils.isEmpty(records)) {
        this.$msg.error('请选择要停用的品牌！')
        return
      }

      this.$msg.confirm('是否确定停用选择的品牌？').then(() => {
        this.loading = true
        const ids = records.map(t => t.id)
        this.$api.baseData.product.info.batchUnable(ids).then(data => {
          this.$msg.success('停用成功！')
          this.search()
        }).finally(() => {
          this.loading = false
        })
      })
    },
    // 批量启用
    batchEnable() {
      const records = this.$refs.grid.getCheckboxRecords()

      if (this.$utils.isEmpty(records)) {
        this.$msg.error('请选择要启用的品牌！')
        return
      }

      this.$msg.confirm('是否确定启用选择的品牌？').then(() => {
        this.loading = true
        const ids = records.map(t => t.id)
        this.$api.baseData.product.info.batchEnable(ids).then(data => {
          this.$msg.success('启用成功！')
          this.search()
        }).finally(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>
<style scoped>
</style>
