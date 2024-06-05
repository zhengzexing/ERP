package com.lframework.xingyun.settle.vo.pre;

import com.lframework.starter.common.exceptions.impl.InputErrorException;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSettlePreSheetVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 供应商ID
   */
  @ApiModelProperty(value = "供应商ID", required = true)
  @NotNull(message = "供应商ID不能为空！")
  private String supplierId;

  /**
   * 项目
   */
  @ApiModelProperty(value = "项目", required = true)
  @NotEmpty(message = "项目不能为空！")
  private List<SettlePreSheetItemVo> items;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;

  public void validate() {

    int orderNo = 1;
    for (SettlePreSheetItemVo item : this.items) {
      if (StringUtil.isBlank(item.getId())) {
        throw new InputErrorException("第" + orderNo + "行项目不能为空！");
      }

      if (item.getAmount() == null) {
        throw new InputErrorException("第" + orderNo + "行金额不能为空！");
      }

      if (NumberUtil.le(item.getAmount(), 0D)) {
        throw new InputErrorException("第" + orderNo + "行金额必须大于0！");
      }

      if (!NumberUtil.isNumberPrecision(item.getAmount(), 2)) {
        throw new InputErrorException("第" + orderNo + "行金额最多允许2位小数！");
      }

      if (this.items.stream().filter(t -> t.getId().equals(item.getId())).count() > 1) {
        throw new InputErrorException("第" + orderNo + "行项目与其他行重复，请检查！");
      }

      orderNo++;
    }
  }
}
