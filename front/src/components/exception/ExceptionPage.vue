<template>
  <div class="exception-page">
    <div class="img">
      <img :src="config[type].img">
    </div>
    <div class="content">
      <h1>{{ config[type].title }}</h1>
      <div class="desc">{{ config[type].desc }}</div>
      <div class="action">
        <a-button type="primary" @click="onBackHome">返回首页</a-button>
      </div>
    </div>
  </div>
</template>

<script>
import Config from './typeConfig'

export default {
  name: 'ExceptionPage',
  props: {
    type: {
      type: String,
      default: ''
    },
    homeRoute: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      config: Config
    }
  },
  methods: {
    onBackHome() {
      let isTabView = false
      let parent = this.$parent
      while (parent) {
        if (parent.$options.name === 'TabsView') {
          isTabView = true
        }
        parent = parent.$parent
      }
      if (isTabView) {
        this.$utils.closeCurrentPage(this.$parent)
        this.$emit('backHome', this.type)
      } else {
        if (this.homeRoute) {
          this.$router.push(this.homeRoute)
        }
        this.$emit('backHome', this.type)
      }
    }
  }
}
</script>

<style lang="less" scoped>
  .exception-page{
    border-radius: 4px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: @base-bg-color;
    .img{
      padding-right: 52px;
      zoom: 1;
      img{
        max-width: 430px;
      }
    }
    .content{
      h1{
        color: #434e59;
        font-size: 72px;
        font-weight: 600;
        line-height: 72px;
        margin-bottom: 24px;
      }
      .desc{
        color: @text-color-second;
        font-size: 20px;
        line-height: 28px;
        margin-bottom: 16px;
      }
    }
  }

</style>
