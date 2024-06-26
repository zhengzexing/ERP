<template>
  <div>
    <dialog-tree
      ref="selector"
      v-model="model"
      :request="getList"
      :load="getLoad"
      :show-sum="showSum"
      :only-final="onlyFinal"
      :disabled="disabled"
      :before-open="beforeOpen"
      :multiple="multiple"
      :placeholder="placeholder"
      :handle-search="handleSearch"
      @input="e => $emit('input', e)"
      @input-label="e => $emit('input-label', e)"
      @input-row="e => $emit('input-row', e)"
      @clear="e => $emit('clear', e)"
    >
      <template v-slot:form>
        <!-- 查询条件 -->
        <j-border>
          <j-form>
            <j-form-item v-if="$utils.isEmpty(requestParams.name)" label="名称">
              <a-input v-model="searchParams.name" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.available)" label="状态">
              <a-select v-model="searchParams.available" placeholder="全部" allow-clear>
                <a-select-option v-for="item in $enums.AVAILABLE.values()" :key="item.code" :value="item.code">{{ item.desc }}</a-select-option>
              </a-select>
            </j-form-item>
          </j-form>
        </j-border>
      </template>
      <!-- 工具栏 -->
      <template v-slot:toolbar_buttons>
        <a-space class="operator">
          <a-button type="primary" icon="search" @click="$refs.selector.search()">查询</a-button>
        </a-space>
      </template>
    </dialog-tree>
  </div>
</template>

<script>
import DialogTree from '@/components/DialogTree'
import { request } from '@/utils/request'

export default {
  name: 'ProductCategorySelector',
  components: { DialogTree },
  props: {
    value: { type: [Object, Array], required: true },
    placeholder: { type: String, default: '' },
    requestParams: {
      type: Object,
      default: e => {
        return {}
      }
    },
    onlyFinal: {
      type: Boolean,
      default: true
    },
    disabled: {
      type: Boolean,
      default: false
    },
    beforeOpen: {
      type: Function,
      default: e => {
        return () => {
          return true
        }
      }
    },
    multiple: { type: Boolean, default: false },
    showSum: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      searchParams: { name: '', available: this.$enums.AVAILABLE.ENABLE.code }
    }
  },
  computed: {
    model: {
      get() {
        return this.value
      },
      set() {}
    }
  },
  methods: {
    getList(params) {
      return request({
        url: '/selector/category',
        region: 'cloud-api',
        method: 'get',
        params: params
      })
    },
    getLoad(ids) {
      return request({
        url: '/selector/category/load',
        region: 'cloud-api',
        method: 'post',
        dataType: 'json',
        data: ids
      })
    },
    handleSearch(datas) {
      const filterName = this.$utils.toString(this.searchParams.name).trim()
      const isFilterName = !this.$utils.isEmpty(filterName)
      const filterAvailable = this.$utils.toString(this.searchParams.available).trim()
      const isFilterAvailable = !this.$utils.isEmpty(this.searchParams.available)
      if (isFilterName || isFilterAvailable) {
        const options = { key: 'id', parentKey: 'parentId', children: 'children', strict: true }
        let tableData = this.$utils.searchTree(datas, item => {
          let filterResult = true

          if (isFilterName) {
            filterResult &= this.$utils.toString(item['name']).indexOf(filterName) > -1
          }

          return filterResult
        }, options)

        if (isFilterAvailable) {
          tableData = this.$utils.toTreeArray(tableData, options)
          tableData = tableData.filter(item => this.$utils.isEqualWithStr(item['available'], filterAvailable))
          tableData = this.$utils.toArrayTree(tableData, options)
        }

        return tableData
      } else {
        return datas
      }
    }
  }
}
</script>

<style lang="less">
</style>
