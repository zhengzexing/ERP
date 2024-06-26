<template>
  <div>
    <!-- 数据列表 -->
    <vxe-grid
      ref="grid"
      stripe
      resizable
      show-overflow
      highlight-hover-row
      keep-source
      row-id="id"
      :row-config="{useKey: true, isCurrent: true, isHover: true}"
      :columns="tableColumn"
      :toolbar-config="toolbarConfig"
      :data="columns"
      :loading="loading"
      :max-height="600"
    >

      <!-- 工具栏 -->
      <template v-slot:toolbar_buttons>
        <a-space>
          <a-button type="primary" icon="plus" @click="addLine">新增</a-button>
          <a-button type="danger" icon="delete" @click="delLine">删除</a-button>
        </a-space>
      </template>

      <!-- 排序 列自定义内容 -->
      <template v-slot:orderNo_default>
        <span class="sort-btn"><a-icon type="drag" /></span>
      </template>

      <!-- 主表字段 列自定义内容 -->
      <template v-slot:mainTableDetailIds_default="{ row }">
        <gen-data-entity-detail-selector v-model="row.mainTableDetailIds" :entity-id="mainTableId" />
      </template>

      <!-- 关联类型 列自定义内容 -->
      <template v-slot:relaType_default="{ row }">
        <a-select v-model="row.relaType" allow-clear>
          <a-select-option v-for="item in $enums.GEN_RELA_TYPE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
        </a-select>
      </template>

      <!-- 关联方式 列自定义内容 -->
      <template v-slot:relaMode_default="{ row }">
        <a-select v-model="row.relaMode" allow-clear>
          <a-select-option v-for="item in $enums.GEN_RELA_MODE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
        </a-select>
      </template>

      <!-- 子表 列自定义内容 -->
      <template v-slot:subTable_default="{ row }">
        <gen-data-entity-selector v-model="row.subTableId" @input="e => changeSubTable(e, row)" />
      </template>

      <!-- 子表别名 列自定义内容 -->
      <template v-slot:subTableAlias_default="{ row }">
        <a-input v-model="row.subTableAlias" allow-clear />
      </template>

      <!-- 子表字段 列自定义内容 -->
      <template v-slot:subTableDetailIds_default="{row}">
        <gen-data-entity-detail-selector v-if="!$utils.isEmpty(row.subTableId)" v-model="row.subTableDetailIds" :entity-id="row.subTableId" />
      </template>
    </vxe-grid>
  </div>
</template>
<script>
import Sortable from 'sortablejs'
import GenDataEntitySelector from '@/components/Selector/GenDataEntitySelector'
import GenDataEntityDetailSelector from '@/components/Selector/GenDataEntityDetailSelector'

export default {
  // 使用组件
  components: {
    GenDataEntitySelector, GenDataEntityDetailSelector
  },

  props: {
    mainTableId: {
      type: String,
      default: ''
    },
    columns: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      // 是否显示加载框
      loading: false,
      // 工具栏配置
      toolbarConfig: {
        // 自定义左侧工具栏
        slots: {
          buttons: 'toolbar_buttons'
        }
      },
      tableColumn: [
        { field: 'checkbox', type: 'checkbox', width: 50 },
        { field: 'orderNo', title: '排序', width: 50, slots: { default: 'orderNo_default' }},
        { field: 'mainTableDetailIds', title: '主表字段', minWidth: 260, slots: { default: 'mainTableDetailIds_default' }},
        { field: 'relaType', title: '关联类型', width: 150, slots: { default: 'relaType_default' }},
        { field: 'relaMode', title: '关联方式', width: 150, slots: { default: 'relaMode_default' }},
        { field: 'subTable', title: '子表', width: 180, slots: { default: 'subTable_default' }},
        { field: 'subTableAlias', title: '子表别名', width: 180, slots: { default: 'subTableAlias_default' }},
        { field: 'subTableDetailIds', title: '子表字段', minWidth: 260, slots: { default: 'subTableDetailIds_default' }}
      ]
    }
  },
  computed: {
  },
  created() {
    this.rowDrop()
  },
  beforeDestroy() {
    if (this.sortable) {
      this.sortable.destroy()
    }
  },
  methods: {
    emptyLine() {
      return {
        id: this.$utils.uuid(),
        mainTableDetailIds: [],
        relaType: '',
        relaMode: '',
        subTableId: '',
        subTableAlias: '',
        subTableDetailIds: []
      }
    },
    addLine() {
      if (this.$utils.isEmpty(this.mainTableId)) {
        this.$msg.error('请先选择主表')
        return
      }
      this.columns.push(this.emptyLine())
    },
    delLine() {
      const records = this.$refs.grid.getCheckboxRecords()
      if (this.$utils.isEmpty(records)) {
        this.$msg.error('请选择要删除的行！')
        return
      }
      this.$msg.confirm('是否确定删除选中的行？').then(() => {
        const columns = this.columns.filter(t => {
          const tmp = records.filter(item => item.id === t.id)
          return this.$utils.isEmpty(tmp)
        })

        this.columns = columns
      })
    },
    changeSubTable(e, row) {
      row.subTableId = ''
      row.subTableDetailIds = []
      this.$nextTick(() => {
        row.subTableId = e
      })
    },
    validDate() {
      for (let i = 0; i < this.columns.length; i++) {
        const column = this.columns[i]

        if (this.$utils.isEmpty(column.mainTableDetailIds)) {
          this.$msg.error('第' + (i + 1) + '行主表字段不能为空！')
          return false
        }

        if (this.$utils.isEmpty(column.relaType)) {
          this.$msg.error('第' + (i + 1) + '行关联类型不能为空！')
          return false
        }

        if (this.$utils.isEmpty(column.relaMode)) {
          this.$msg.error('第' + (i + 1) + '行关联方式不能为空！')
          return false
        }

        if (this.$utils.isEmpty(column.subTableId)) {
          this.$msg.error('第' + (i + 1) + '行子表不能为空！')
          return false
        }

        if (this.$utils.isEmpty(column.subTableAlias)) {
          this.$msg.error('第' + (i + 1) + '行子表别名不能为空！')
          return false
        }

        if (this.$utils.isEmpty(column.subTableDetailIds)) {
          this.$msg.error('第' + (i + 1) + '行子表字段不能为空！')
          return false
        }
      }

      return true
    },
    rowDrop() {
      this.$nextTick(() => {
        const grid = this.$refs.grid
        this.sortable = Sortable.create(grid.$el.querySelector('.body--wrapper>.vxe-table--body tbody'), {
          handle: '.sort-btn',
          onEnd: ({ newIndex, oldIndex }) => {
            const currRow = this.columns.splice(oldIndex, 1)[0]
            this.columns.splice(newIndex, 0, currRow)
          }
        })
      })
    },
    getColumns() {
      const columns = this.columns.map(item => {
        return Object.assign({}, item)
      })

      return columns
    }
  }
}
</script>
<style scoped>
.sort-btn {
  margin: 0 5px;
  cursor: pointer;
}
</style>
