package com.lframework.xingyun.sc.vo.sale.returned;

import com.lframework.starter.web.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApproveRefuseSaleReturnVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 退单ID
   */
  @ApiModelProperty(value = "退单ID", required = true)
  @NotBlank(message = "退单ID不能为空！")
  private String id;

  /**
   * 拒绝理由
   */
  @ApiModelProperty(value = "拒绝理由", required = true)
  @NotBlank(message = "拒绝理由不能为空！")
  private String refuseReason;
}
