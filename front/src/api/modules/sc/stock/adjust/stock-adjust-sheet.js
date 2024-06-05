import { request } from '@/utils/request'

export default {

  /**
   * 查询列表
   * @param params
   * @returns {AxiosPromise}
   */
  query: (params) => {
    return request({
      url: '/stock/adjust/query',
      region: 'cloud-api',
      method: 'get',
      params: params
    })
  },

  /**
   * 根据ID查询
   * @param id
   * @returns {AxiosPromise}
   */
  get: (id) => {
    return request({
      url: '/stock/adjust/detail',
      region: 'cloud-api',
      method: 'get',
      params: {
        id: id
      }
    })
  },

  /**
   * 新增
   * @param params
   * @returns {AxiosPromise}
   */
  create: (params) => {
    return request({
      url: '/stock/adjust',
      region: 'cloud-api',
      method: 'post',
      dataType: 'json',
      data: params
    })
  },

  /**
   * 修改
   * @param params
   * @returns {AxiosPromise}
   */
  modify: (params) => {
    return request({
      url: '/stock/adjust',
      region: 'cloud-api',
      method: 'put',
      dataType: 'json',
      data: params
    })
  },

  /**
   * 删除
   * @param params
   * @returns {AxiosPromise}
   */
  deleteById: (id) => {
    return request({
      url: '/stock/adjust',
      region: 'cloud-api',
      method: 'delete',
      data: {
        id: id
      }
    })
  },

  /**
   * 根据关键字查询商品
   * @param params
   * @returns {AxiosPromise}
   */
  searchProduct: (params) => {
    return request({
      url: '/stock/adjust/product/search',
      region: 'cloud-api',
      method: 'get',
      params: params
    })
  },
  /**
   * 查询商品列表
   * @param params
   * @returns {AxiosPromise}
   */
  queryProduct: (params) => {
    return request({
      url: '/stock/adjust/product/list',
      region: 'cloud-api',
      method: 'get',
      params: params
    })
  },
  /**
   * 导出列表
   * @param params
   */
  exportList: (params) => {
    return request({
      url: '/stock/adjust/export',
      region: 'cloud-api',
      method: 'post',
      responseType: 'blob',
      data: params
    })
  },
  /**
   * 直接审核通过
   * @param params
   * @returns {*}
   */
  directApprovePass: (params) => {
    return request({
      url: '/stock/adjust/approve/pass/direct',
      region: 'cloud-api',
      method: 'post',
      dataType: 'json',
      data: params
    })
  },
  /**
   * 审核通过
   * @param params
   * @returns {*}
   */
  approvePass: (params) => {
    return request({
      url: '/stock/adjust/approve/pass',
      region: 'cloud-api',
      method: 'patch',
      dataType: 'json',
      data: params
    })
  },
  /**
   * 审核拒绝
   * @param params
   * @returns {*}
   */
  approveRefuse: (params) => {
    return request({
      url: '/stock/adjust/approve/refuse',
      region: 'cloud-api',
      method: 'patch',
      dataType: 'json',
      data: params
    })
  },
  // 批量删除订单
  batchDelete: (params) => {
    return request({
      url: '/stock/adjust/batch',
      region: 'cloud-api',
      method: 'delete',
      dataType: 'json',
      data: params
    })
  },
  /**
   * 批量审核通过订单
   * @param params
   * @returns {*}
   */
  batchApprovePass: (params) => {
    return request({
      url: '/stock/adjust/approve/pass/batch',
      region: 'cloud-api',
      method: 'patch',
      dataType: 'json',
      data: params
    })
  },
  /**
   * 批量审核拒绝
   * @param params
   * @returns {*}
   */
  batchApproveRefuse: (params) => {
    return request({
      url: '/stock/adjust/approve/refuse/batch',
      region: 'cloud-api',
      method: 'patch',
      dataType: 'json',
      data: params
    })
  }
}
