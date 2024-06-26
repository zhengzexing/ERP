import { request } from '@/utils/request'

const user = {

  /**
   * 获取初始化参数
   * @param params
   * @returns {AxiosPromise}
   */
  getInit: () => {
    return request({
      url: '/auth/init',
      region: 'cloud-api',
      method: 'get'
    })
  },

  /**
   * 注册
   * @param params
   * @returns {AxiosPromise}
   */
  regist: (params) => {
    return request({
      url: '/auth/regist',
      region: 'cloud-api',
      method: 'post',
      data: params
    })
  },
  /**
   * 登录
   * @param data
   * @returns {AxiosPromise}
   */
  login: (data) => {
    return request({
      url: '/auth/login',
      region: 'cloud-api',
      method: 'post',
      data
    })
  },
  /**
   * 获取用户信息
   * @returns {AxiosPromise}
   */
  getInfo: () => {
    return request({
      url: '/auth/info',
      region: 'cloud-api',
      method: 'get'
    })
  },
  /**
   * 退出登录
   * @returns {AxiosPromise}
   */
  logout: () => {
    return request({
      url: '/auth/logout',
      region: 'cloud-api',
      method: 'post'
    })
  },
  /**
   * 获取登录验证码
   * @returns {AxiosPromise}
   */
  getCaptcha: () => {
    return request({
      url: '/auth/captcha',
      region: 'cloud-api',
      method: 'get'
    })
  },
  /**
   * 获取菜单
   * @returns {AxiosPromise}
   */
  getMenus: () => {
    return request({
      url: '/auth/menus',
      region: 'cloud-api',
      method: 'get'
    })
  },
  /**
   * 收藏菜单
   * @param menuId
   * @returns {AxiosPromise}
   */
  collectMenu: (menuId) => {
    return request({
      url: '/menu/collect',
      region: 'cloud-api',
      method: 'post',
      data: {
        menuId: menuId
      }
    })
  },
  /**
   * 取消收藏菜单
   * @param menuId
   * @returns {AxiosPromise}
   */
  cancelCollectMenu: (menuId) => {
    return request({
      url: '/menu/collect/cancel',
      region: 'cloud-api',
      method: 'post',
      data: {
        menuId: menuId
      }
    })
  },
  /**
   * 忘记密码时，根据用户名获取信息
   * @param username
   * @returns {*}
   */
  forgetUsername: (username) => {
    return request({
      url: '/auth/forget/username',
      region: 'cloud-api',
      method: 'get',
      params: {
        username: username
      }
    })
  },
  /**
   * 获取邮箱验证码
   * @returns {*}
   */
  getMailCaptcha: (username) => {
    return request({
      url: '/auth/forget/mail/code',
      region: 'cloud-api',
      method: 'get',
      params: {
        username: username
      }
    })
  },
  /**
   * 获取邮箱验证码
   * @returns {*}
   */
  resetPswByMail: (params) => {
    return request({
      url: '/auth/forget/mail',
      region: 'cloud-api',
      method: 'post',
      data: params
    })
  },
  /**
   * 获取短信验证码
   * @returns {*}
   */
  getSmsCaptcha: (username) => {
    return request({
      url: '/auth/forget/sms/code',
      region: 'cloud-api',
      method: 'get',
      params: {
        username: username
      }
    })
  },
  /**
   * 获取短信验证码
   * @returns {*}
   */
  resetPswBySms: (params) => {
    return request({
      url: '/auth/forget/sms',
      region: 'cloud-api',
      method: 'post',
      data: params
    })
  },
  /**
   * 手机号登录时发送短信验证码
   * @param params
   * @returns {AxiosPromise}
   */
  getTelephoneLoginSmsCaptcha: (params) => {
    return request({
      url: '/auth/login/telephone/captcha',
      region: 'cloud-api',
      method: 'get',
      params: params
    })
  },
  /**
   * 手机号登录
   * @param params
   * @returns {AxiosPromise}
   */
  telephoneLogin: (params) => {
    return request({
      url: '/auth/login/telephone',
      region: 'cloud-api',
      method: 'post',
      data: params
    })
  },
  /**
   * 手机号绑定用户
   * @param params
   * @returns {AxiosPromise}
   */
  telephoneBindUser: (params) => {
    return request({
      url: '/auth/bind/telephone',
      region: 'cloud-api',
      method: 'post',
      data: params
    })
  }
}
export default user
