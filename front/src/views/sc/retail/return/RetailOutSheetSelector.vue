<template>
  <div>
    <dialog-table
      ref="selector"
      v-model="model"
      :request="getList"
      :load="getLoad"
      :show-sum="showSum"
      :request-params="_requestParams"
      :multiple="multiple"
      :placeholder="placeholder"
      :disabled="disabled"
      :before-open="beforeOpen"
      :option="{ label: 'code', value: 'id' }"
      :column-option="{ label: 'code', value: 'id' }"
      :table-column="[
        { field: 'code', title: '零售出库单号', width: 180 },
        { field: 'scCode', title: '仓库编号', minWidth: 100 },
        { field: 'scName', title: '仓库名称', minWidth: 120 },
        { field: 'memberCode', title: '会员编号', minWidth: 100 },
        { field: 'memberName', title: '会员名称', minWidth: 120 },
        { field: 'createTime', title: '操作时间', minWidth: 150 },
        { field: 'createBy', title: '操作人', minWidth: 100 }
      ]"
      @input="e => $emit('input', e)"
      @input-label="e => $emit('input-label', e)"
      @input-row="e => $emit('input-row', e)"
      @clear="e => $emit('clear', e)"
    >
      <template v-slot:form>
        <j-border>
          <j-form>
            <j-form-item v-if="$utils.isEmpty(requestParams.code)" label="零售出库单号">
              <a-input v-model="searchParams.code" allow-clear />
            </j-form-item>
            <j-form-item label="仓库">
              <store-center-selector
                v-if="$utils.isEmpty(requestParams.scId)"
                v-model="searchParams.scId"
              />
            </j-form-item>
            <j-form-item label="会员">
              <member-selector
                v-if="$utils.isEmpty(requestParams.memberId)"
                v-model="searchParams.memberId"
              />
            </j-form-item>
            <j-form-item label="操作人">
              <user-selector
                v-if="$utils.isEmpty(requestParams.createBy)"
                v-model="searchParams.createBy"
              />
            </j-form-item>
            <j-form-item label="操作日期" :content-nest="false" :span="12">
              <div class="date-range-container">
                <a-date-picker
                  v-model="searchParams.createStartTime"
                  placeholder=""
                  value-format="YYYY-MM-DD 00:00:00"
                />
                <span class="date-split">至</span>
                <a-date-picker
                  v-model="searchParams.createEndTime"
                  placeholder=""
                  value-format="YYYY-MM-DD 23:59:59"
                />
              </div>
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
    </dialog-table>
  </div>
</template>

<script>
import DialogTable from '@/components/DialogTable'
import { request } from '@/utils/request'
import StoreCenterSelector from '@/components/Selector/StoreCenterSelector'
import MemberSelector from '@/components/Selector/MemberSelector'
import UserSelector from '@/components/Selector/UserSelector'
import moment from 'moment'

export default {
  name: 'RetailOutSheetSelectorWithReturn',
  components: { DialogTable, StoreCenterSelector, MemberSelector, UserSelector },
  props: {
    value: { type: [Object, Array], required: true },
    multiple: { type: Boolean, default: false },
    placeholder: { type: String, default: '' },
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
    requestParams: {
      type: Object,
      default: e => {
        return {}
      }
    },
    showSum: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      searchParams: {
        code: '',
        scId: '',
        memberId: '',
        createBy: '',
        createStartTime: this.$utils.formatDateTime(this.$utils.getDateTimeWithMinTime(moment().subtract(1, 'M'))),
        createEndTime: this.$utils.formatDateTime(this.$utils.getDateTimeWithMaxTime(moment()))
      }
    }
  },
  computed: {
    model: {
      get() {
        return this.value
      },
      set() {}
    },
    _requestParams() {
      return Object.assign({}, this.searchParams, { available: true }, this.requestParams)
    }
  },
  methods: {
    getList(params) {
      const reqParams = {
        code: params.code,
        scId: params.scId || '',
        memberId: params.memberId || '',
        createBy: params.createBy || '',
        createStartTime: params.createStartTime,
        createEndTime: params.createEndTime
      }
      return request({
        url: '/retail/out/sheet/query/return',
        region: 'cloud-api',
        method: 'get',
        params: reqParams
      })
    },
    getLoad(ids) {
      return request({
        url: '/retail/out/sheet/query/return/load',
        region: 'cloud-api',
        method: 'post',
        dataType: 'json',
        data: ids
      })
    }
  }
}
</script>

<style lang="less">
</style>
