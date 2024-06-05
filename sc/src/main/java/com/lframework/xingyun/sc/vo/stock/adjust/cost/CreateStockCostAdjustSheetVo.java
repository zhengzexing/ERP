package com.lframework.xingyun.sc.vo.stock.adjust.cost;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.web.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateStockCostAdjustSheetVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 仓库ID
   */
  @ApiModelProperty(value = "仓库ID", required = true)
  @NotBlank(message = "请输入仓库ID！")
  private String scId;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;

  /**
   * 商品信息
   */
  @ApiModelProperty(value = "商品信息", required = true)
  @Valid
  @NotEmpty(message = "请录入商品！")
  private List<StockCostAdjustProductVo> products;

  public void validate() {

    int orderNo = 1;
    for (StockCostAdjustProductVo product : this.products) {
      if (NumberUtil.lt(product.getPrice(), BigDecimal.ZERO)) {
        throw new DefaultClientException("第" + orderNo + "行商品的调整后成本价不允许小于0！");
      }

      if (!NumberUtil.isNumberPrecision(product.getPrice(), 2)) {
        throw new DefaultClientException("第" + orderNo + "行商品的调整后成本价最多允许2位小数！");
      }
    }
  }
}
