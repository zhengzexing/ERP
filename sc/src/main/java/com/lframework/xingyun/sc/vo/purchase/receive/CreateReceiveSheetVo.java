package com.lframework.xingyun.sc.vo.purchase.receive;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.exceptions.impl.InputErrorException;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.starter.web.vo.BaseVo;
import com.lframework.xingyun.sc.dto.purchase.receive.GetPaymentDateDto;
import com.lframework.xingyun.sc.entity.PurchaseConfig;
import com.lframework.xingyun.sc.entity.PurchaseOrderDetail;
import com.lframework.xingyun.sc.service.purchase.PurchaseConfigService;
import com.lframework.xingyun.sc.service.purchase.PurchaseOrderDetailService;
import com.lframework.xingyun.sc.service.purchase.ReceiveSheetService;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReceiveSheetVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 仓库ID
   */
  @ApiModelProperty(value = "仓库ID", required = true)
  @NotBlank(message = "仓库ID不能为空！")
  private String scId;

  /**
   * 供应商ID
   */
  @ApiModelProperty(value = "供应商ID", required = true)
  @NotBlank(message = "供应商ID不能为空！")
  private String supplierId;

  /**
   * 采购员ID
   */
  @ApiModelProperty("采购员ID")
  private String purchaserId;

  /**
   * 付款日期
   */
  @ApiModelProperty("付款日期")
  private LocalDate paymentDate;

  /**
   * 是否允许修改付款日期
   */
  @ApiModelProperty("是否允许修改付款日期")
  private Boolean allowModifyPaymentDate = Boolean.FALSE;

  /**
   * 到货日期
   */
  @ApiModelProperty(value = "到货日期", required = true)
  @NotNull(message = "到货日期不能为空！")
  private LocalDate receiveDate;

  /**
   * 采购订单ID
   */
  @ApiModelProperty("采购订单ID")
  private String purchaseOrderId;

  /**
   * 商品信息
   */
  @ApiModelProperty(value = "商品信息", required = true)
  @Valid
  @NotEmpty(message = "商品不能为空！")
  private List<ReceiveProductVo> products;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;

  /**
   * 是否关联采购订单
   */
  @ApiModelProperty("是否关联采购订单")
  private Boolean required;

  public void validate() {

    PurchaseConfigService purchaseConfigService = ApplicationUtil.getBean(
        PurchaseConfigService.class);
    PurchaseConfig purchaseConfig = purchaseConfigService.get();

    if (!purchaseConfig.getReceiveRequirePurchase().equals(this.required)) {
      throw new DefaultClientException("系统参数发生改变，请刷新页面后重试！");
    }

    this.validate(purchaseConfig.getReceiveRequirePurchase());
  }

  protected void validate(boolean requirePurchase) {

    ReceiveSheetService receiveSheetService = ApplicationUtil.getBean(ReceiveSheetService.class);
    GetPaymentDateDto paymentDate = receiveSheetService.getPaymentDate(this.supplierId);
    if (paymentDate.getAllowModify()) {
      if (this.getPaymentDate() == null) {
        throw new InputErrorException("付款日期不能为空！");
      }
    }

    if (requirePurchase) {
      if (StringUtil.isBlank(this.getPurchaseOrderId())) {
        throw new InputErrorException("采购订单不能为空！");
      }
    }

    PurchaseOrderDetailService purchaseOrderDetailService = ApplicationUtil.getBean(
        PurchaseOrderDetailService.class);

    int orderNo = 1;
    for (ReceiveProductVo product : this.products) {

      if (StringUtil.isBlank(product.getProductId())) {
        throw new InputErrorException("第" + orderNo + "行商品不能为空！");
      }

      if (product.getReceiveNum() == null) {
        throw new InputErrorException("第" + orderNo + "行商品收货数量不能为空！");
      }

      if (product.getReceiveNum() <= 0) {
        throw new InputErrorException("第" + orderNo + "行商品收货数量必须大于0！");
      }

      if (!requirePurchase) {
        if (product.getPurchasePrice() == null) {
          throw new InputErrorException("第" + orderNo + "行商品采购价不能为空！");
        }

        if (product.getPurchasePrice().doubleValue() < 0D) {
          throw new InputErrorException("第" + orderNo + "行商品采购价不允许小于0！");
        }

        if (!NumberUtil.isNumberPrecision(product.getPurchasePrice(), 2)) {
          throw new InputErrorException("第" + orderNo + "行商品采购价最多允许2位小数！");
        }
      } else {
        if (StringUtil.isNotBlank(product.getPurchaseOrderDetailId())) {
          PurchaseOrderDetail orderDetail = purchaseOrderDetailService.getById(
              product.getPurchaseOrderDetailId());
          product.setPurchasePrice(orderDetail.getTaxPrice());
        } else {
          product.setPurchasePrice(BigDecimal.ZERO);
        }
      }

      orderNo++;
    }
  }
}
