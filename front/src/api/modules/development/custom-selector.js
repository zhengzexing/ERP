import { request } from '@/utils/request'

const data = {
  /**
   * 自定义选择器分类
   * @returns {AxiosPromise}
   */
  queryCategories: () => {
    return request({
      url: '/gen/custom/selector/category/query',
      region: 'cloud-api',
      method: 'get'
    })
  },
  /**
   * 新增自定义选择器分类
   * @param params
   * @returns {AxiosPromise}
   */
  createCategory: (params) => {
    return request({
      url: '/gen/custom/selector/category',
      region: 'cloud-api',
      method: 'post',
      data: params
    })
  },
  /**
   * 修改自定义选择器分类
   * @param params
   * @returns {AxiosPromise}
   */
  modifyCategory: (params) => {
    return request({
      url: '/gen/custom/selector/category',
      region: 'cloud-api',
      method: 'put',
      data: params
    })
  },
  /**
   * 根据ID查询自定义选择器分类
   * @param id
   * @returns {AxiosPromise}
   */
  getCategory: (id) => {
    return request({
      url: '/gen/custom/selector/category',
      region: 'cloud-api',
      method: 'get',
      params: {
        id: id
      }
    })
  },
  /**
   * 删除自定义选择器分类
   * @param id
   * @returns {*}
   */
  removeCategory: (id) => {
    return request({
      url: '/gen/custom/selector/category',
      region: 'cloud-api',
      method: 'delete',
      data: {
        id: id
      }
    })
  },
  query: (data) => {
    return request({
      url: '/gen/custom/selector/query',
      region: 'cloud-api',
      method: 'get',
      params: data
    })
  },
  add: (data) => {
    return request({
      url: '/gen/custom/selector',
      region: 'cloud-api',
      method: 'post',
      dataType: 'json',
      data
    })
  },
  get: (id) => {
    return request({
      url: '/gen/custom/selector',
      region: 'cloud-api',
      method: 'get',
      params: {
        id: id
      }
    })
  },
  modify: (data) => {
    return request({
      url: '/gen/custom/selector',
      region: 'cloud-api',
      dataType: 'json',
      method: 'put',
      data
    })
  },
  deleteById: (id) => {
    return request({
      url: '/gen/custom/selector',
      region: 'cloud-api',
      method: 'delete',
      data: {
        id: id
      }
    })
  },
  batchDelete: (ids) => {
    return request({
      url: '/gen/custom/selector/batch',
      region: 'cloud-api',
      method: 'delete',
      dataType: 'json',
      data: ids
    })
  },
  batchEnable: (ids) => {
    return request({
      url: '/gen/custom/selector/enable/batch',
      region: 'cloud-api',
      method: 'patch',
      dataType: 'json',
      data: ids
    })
  },
  batchUnable: (ids) => {
    return request({
      url: '/gen/custom/selector/unable/batch',
      region: 'cloud-api',
      method: 'patch',
      dataType: 'json',
      data: ids
    })
  }
}

export default data
