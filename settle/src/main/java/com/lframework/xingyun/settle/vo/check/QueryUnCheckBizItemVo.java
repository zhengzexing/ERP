package com.lframework.xingyun.settle.vo.check;

import com.lframework.starter.web.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryUnCheckBizItemVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 供应商ID
   */
  @ApiModelProperty("供应商ID")
  @NotNull(message = "供应商ID不能为空！")
  private String supplierId;

  /**
   * 起始时间
   */
  @ApiModelProperty("起始时间")
  private LocalDateTime startTime;

  /**
   * 截至时间
   */
  @ApiModelProperty("截至时间")
  private LocalDateTime endTime;
}
