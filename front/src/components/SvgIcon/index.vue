<template>
  <div v-if="isExternal" :style="styleExternalIcon" class="svg-external-icon svg-icon" v-on="$listeners" />
  <a-icon v-else-if="!$utils.isEmpty(iconClass) && antdIcon()" :type="iconClass.substring(2, iconClass.length)" />
  <i v-else-if="!$utils.isEmpty(iconClass)" class="anticon">
    <svg :class="svgClass" aria-hidden="true" focusable="false" v-on="$listeners">
      <use :xlink:href="iconName" />
    </svg>
  </i>

</template>

<script>
import { isExternal } from '@/utils/validate'

export default {
  name: 'SvgIcon',
  props: {
    iconClass: {
      type: String,
      default: ''
    },
    className: {
      type: String,
      default: ''
    }
  },
  computed: {
    isExternal() {
      return isExternal(this.iconClass)
    },
    iconName() {
      return `#icon-${this.iconClass}`
    },
    svgClass() {
      if (this.className) {
        return 'svg-icon ' + this.className
      } else {
        return 'svg-icon'
      }
    },
    styleExternalIcon() {
      return {
        mask: `url(${this.iconClass}) no-repeat 50% 50%`,
        '-webkit-mask': `url(${this.iconClass}) no-repeat 50% 50%`
      }
    }
  },
  methods: {
    antdIcon() {
      // 约定：内置图标使用svg-icon时，需要加a-前缀
      return this.iconClass.startsWith('a-')
    }
  }
}
</script>

<style scoped>
.svg-icon {
  display: inline-block;
  width: 1em;
  height: 1em;
  vertical-align: -0.125em;
  fill: currentColor;
  overflow: hidden;
}

.svg-external-icon {
  background-color: currentColor;
  mask-size: cover!important;
  display: inline-block;
}
</style>
