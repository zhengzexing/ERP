package com.lframework.xingyun.sc.vo.sale;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSaleOrderVo extends CreateSaleOrderVo implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 订单ID
   */
  @ApiModelProperty(value = "订单ID", required = true)
  @NotBlank(message = "订单ID不能为空！")
  private String id;
}
