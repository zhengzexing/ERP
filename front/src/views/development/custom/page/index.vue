<template>
  <div>
    <div v-show="visible" class="app-container">
      <a-row>
        <a-col :span="4" :style="{height: $defaultTableHeight + 'px'}">
          <category-tree :height="$defaultTableHeight" @change="e => doSearch(e)" />
        </a-col>
        <a-col :span="20">
          <!-- 数据列表 -->
          <vxe-grid
            id="CustomPage"
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
                <j-form label-width="60px" @collapse="$refs.grid.refreshColumn()">
                  <j-form-item label="页面ID" :span="6">
                    <a-input v-model="searchFormData.id" allow-clear />
                  </j-form-item>
                  <j-form-item label="名称" :span="6">
                    <a-input v-model="searchFormData.name" allow-clear />
                  </j-form-item>
                </j-form>
              </j-border>
            </template>
            <!-- 工具栏 -->
            <template v-slot:toolbar_buttons>
              <a-space>
                <a-button type="primary" icon="search" @click="search">查询</a-button>
                <a-button type="primary" icon="plus" @click="$refs.addDialog.openDialog()">新增</a-button>
                <a-button type="danger" icon="delete" @click="batchDelete">批量删除</a-button>
              </a-space>
            </template>

            <!-- 操作 列自定义内容 -->
            <template v-slot:action_default="{ row }">
              <a-button type="link" @click="e => { id = row.id;$nextTick(() => $refs.viewDialog.openDialog()) }">查看</a-button>
              <a-button type="link" @click="e => { id = row.id;$nextTick(() => $refs.updateDialog.openDialog()) }">修改</a-button>
              <a-button type="link" class="ant-btn-link-danger" @click="e => { deleteRow(row) }">删除</a-button>
            </template>
          </vxe-grid>
        </a-col>
      </a-row>

      <!-- 新增窗口 -->
      <add ref="addDialog" @confirm="search" />

      <!-- 修改窗口 -->
      <modify :id="id" ref="updateDialog" @confirm="search" />

      <!-- 查看窗口 -->
      <detail :id="id" ref="viewDialog" />
    </div>
  </div>
</template>

<script>
import Add from './add'
import Modify from './modify'
import Detail from './detail'
import CategoryTree from './category-tree'

export default {
  name: 'CustomPage',
  // 使用组件
  components: {
    Add, Modify, Detail, CategoryTree
  },
  data() {
    return {
      // 当前行数据
      id: '',
      // 是否显示加载框
      loading: false,
      visible: true,
      // 查询列表的查询条件
      searchFormData: {
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
        { field: 'id', title: '页面ID', width: 120 },
        { field: 'name', title: '名称', minWidth: 180 },
        { field: 'categoryName', title: '分类', width: 120 },
        { field: 'description', title: '备注', minWidth: 200 },
        { field: 'createBy', title: '创建人', width: 100 },
        { field: 'createTime', title: '创建时间', width: 170 },
        { title: '操作', width: 160, fixed: 'right', slots: { default: 'action_default' }}
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
            return this.$api.development.customPage.query(this.buildQueryParams(page))
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
    doSearch(categoryId) {
      if (!this.$utils.isEmpty(categoryId)) {
        if (this.$utils.isEqualWithStr(0, categoryId)) {
          this.searchFormData.categoryId = ''
        } else {
          this.searchFormData.categoryId = categoryId
        }
      } else {
        this.searchFormData.categoryId = ''
      }

      this.search()
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
      return Object.assign({ }, this.searchFormData)
    },
    // 删除
    deleteRow(row) {
      this.$msg.confirm('是否确定删除该自定义页面？').then(() => {
        this.loading = true
        this.$api.development.customPage.deleteById(row.id).then(() => {
          this.$msg.success('删除成功！')
          this.search()
        }).finally(() => {
          this.loading = false
        })
      })
    },
    // 批量删除
    batchDelete() {
      const records = this.$refs.grid.getCheckboxRecords()

      if (this.$utils.isEmpty(records)) {
        this.$msg.error('请选择要删除的自定义页面！')
        return
      }

      this.$msg.confirm('是否确定删除选择的自定义页面？').then(() => {
        this.loading = true
        const ids = records.map(t => t.id)
        this.$api.development.customPage.batchDelete(ids).then(data => {
          this.$msg.success('删除成功！')
          this.search()
        }).finally(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>
