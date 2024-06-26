<template>
  <div class="gen-container">
    <!-- 数据列表 -->
    <vxe-grid
      ref="grid"
      resizable
      show-overflow
      highlight-hover-row
      keep-source
      row-id="id"
      :row-config="{useKey: true}"
      :toolbar-config="toolbarConfig"
      :columns="tableColumn"
      :data="tableData"
      :loading="loading"
      :max-height="600"
    >
      <!-- 工具栏 -->
      <template v-slot:toolbar_buttons>
        <a-space>
          <a-button type="primary" icon="plus" @click="addRow">新增</a-button>
          <a-button type="danger" icon="delete" @click="deleteRow">删除</a-button>
        </a-space>
      </template>

      <!-- 排序 列自定义内容 -->
      <template v-slot:orderNo_default>
        <span class="sort-btn"><a-icon type="drag" /></span>
      </template>

      <!-- 名称 列自定义内容 -->
      <template v-slot:name_default="{ row }">
        <a-input v-model="row.name" />
      </template>

      <!-- 图标 列自定义内容 -->
      <template v-slot:icon_default="{ row }">
        <icon-picker v-model="row.icon" />
      </template>

      <!-- 显示类型 列自定义内容 -->
      <template v-slot:viewType_default="{ row }">
        <a-select v-model="row.viewType" placeholder="">
          <a-select-option v-for="item in $enums.GEN_CUSTOM_LIST_BTN_VIEW_TYPE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
        </a-select>
      </template>

      <!-- 按钮类型 列自定义内容 -->
      <template v-slot:btnType_default="{ row }">
        <a-select v-model="row.btnType" placeholder="">
          <a-select-option v-for="item in $enums.GEN_CUSTOM_LIST_BTN_TYPE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
        </a-select>
      </template>

      <!-- 按钮配置 列自定义内容 -->
      <template v-slot:btnConfig_default="{ row }">
        <a-input v-if="$enums.GEN_CUSTOM_LIST_BTN_TYPE.EXTERNAL.equalsCode(row.btnType)" v-model="row.btnConfig" />
        <a-input v-if="$enums.GEN_CUSTOM_LIST_BTN_TYPE.ROUTE.equalsCode(row.btnType)" v-model="row.btnConfig" />
        <gen-custom-form-selector v-if="$enums.GEN_CUSTOM_LIST_BTN_TYPE.CUSTOM_FORM.equalsCode(row.btnType)" v-model="row.customForm" :request-params="{ available: true, isDialog: true }" />
        <a v-if="$enums.GEN_CUSTOM_LIST_BTN_TYPE.EXCUTE_SCRIPT.equalsCode(row.btnType)" @click="e => $refs['excuteScriptEditor' + row.id].openDialog()">编辑脚本</a>
        <code-editor :ref="'excuteScriptEditor' + row.id" v-model="row.btnConfig" if="$enums.GEN_CUSTOM_LIST_BTN_TYPE.EXCUTE_SCRIPT.equalsCode(row.btnType)" mode="javascript" :description="`点击按钮后执行的JS代码，脚本会填充在function excute(_this){}中。`" />
      </template>

      <!-- 请求参数 列自定义内容 -->
      <template v-slot:requestParam_default="{ row }">
        <div v-if="$enums.GEN_CUSTOM_LIST_BTN_TYPE.CUSTOM_FORM.equalsCode(row.btnType)">
          <a @click="e => $refs['requestParamEditor' + row.id].openDialog()">编辑参数</a>
          <code-editor :ref="'requestParamEditor' + row.id" v-model="row.requestParam" mode="javascript" :description="`1、用于构建自定义表单的动态请求参数，脚本会填充在function build(_this){}中。\n2、返回值为对象，会做为自定义表单的请求参数。`" />
        </div>
      </template>
    </vxe-grid>
  </div>
</template>
<script>
import Sortable from 'sortablejs'
import GenCustomFormSelector from '@/components/Selector/GenCustomFormSelector'
import CodeEditor from './code-editor'

export default {
  // 使用组件
  components: {
    GenCustomFormSelector,
    CodeEditor
  },
  props: {
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
        { type: 'checkbox', width: 50 },
        { field: 'orderNo', title: '排序', width: 50, slots: { default: 'orderNo_default' }},
        { field: 'name', title: '显示名称', width: 160, slots: { default: 'name_default' }},
        { field: 'icon', title: '图标', width: 200, slots: { default: 'icon_default' }},
        { field: 'viewType', title: '显示类型', width: 120, slots: { default: 'viewType_default' }},
        { field: 'btnType', title: '按钮类型', width: 140, slots: { default: 'btnType_default' }},
        { field: 'btnConfig', title: '按钮配置', width: 260, slots: { default: 'btnConfig_default' }},
        { field: 'requestParam', title: '请求参数', width: 120, slots: { default: 'requestParam_default' }}
      ],
      tableData: []
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
    validDate() {
      if (this.$utils.isEmpty(this.tableData)) {
        return true
      }

      for (let i = 0; i < this.tableData.length; i++) {
        const column = this.tableData[i]
        if (this.$utils.isEmpty(column.name)) {
          this.$msg.error('第' + (i + 1) + '行显示名称不能为空')
          return false
        }
        if (this.$utils.isEmpty(column.viewType)) {
          this.$msg.error('第' + (i + 1) + '行显示类型不能为空')
          return false
        }
        if (this.$utils.isEmpty(column.btnType)) {
          this.$msg.error('第' + (i + 1) + '行按钮类型不能为空')
          return false
        }

        if (this.$enums.GEN_CUSTOM_LIST_BTN_TYPE.EXTERNAL.equalsCode(column.btnType) || this.$enums.GEN_CUSTOM_LIST_BTN_TYPE.ROUTE.equalsCode(column.btnType) || this.$enums.GEN_CUSTOM_LIST_BTN_TYPE.EXCUTE_SCRIPT.equalsCode(column.btnType)) {
          if (this.$utils.isEmpty(column.btnConfig)) {
            this.$msg.error('第' + (i + 1) + '行按钮配置不能为空')
            return false
          }
        } else if (this.$enums.GEN_CUSTOM_LIST_BTN_TYPE.CUSTOM_FORM.equalsCode(column.btnType)) {
          if (this.$utils.isEmpty(column.customForm)) {
            this.$msg.error('第' + (i + 1) + '行按钮配置不能为空')
            return false
          }
        }
      }
      return true
    },
    emptyLine() {
      return {
        id: this.$utils.uuid(),
        customForm: '',
        orderNo: '',
        name: '',
        viewType: '',
        btnType: '',
        btnConfig: '',
        requestParam: ''
      }
    },
    setTableData(datas) {
      this.tableData = datas || []
    },
    getTableData() {
      const that = this
      return this.tableData.map(item => {
        if (that.$enums.GEN_CUSTOM_LIST_BTN_TYPE.CUSTOM_FORM.equalsCode(item.btnType)) {
          return Object.assign({}, item, {
            btnConfig: item.customForm
          })
        } else {
          return item
        }
      })
    },
    rowDrop() {
      this.$nextTick(() => {
        const grid = this.$refs.grid
        this.sortable = Sortable.create(grid.$el.querySelector('.body--wrapper>.vxe-table--body tbody'), {
          handle: '.sort-btn',
          onEnd: ({ newIndex, oldIndex }) => {
            const currRow = this.tableData.splice(oldIndex, 1)[0]
            this.tableData.splice(newIndex, 0, currRow)
          }
        })
      })
    },
    addRow() {
      this.tableData.push(this.emptyLine())
    },
    deleteRow() {
      const records = this.$refs.grid.getCheckboxRecords()

      if (this.$utils.isEmpty(records)) {
        this.$msg.error('请选择要删除的行！')
        return
      }

      this.$msg.confirm('是否确定删除选择的行？').then(() => {
        const ids = records.map(t => t.id)
        this.tableData = this.tableData.filter(item => !ids.includes(item.id))
      })
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
