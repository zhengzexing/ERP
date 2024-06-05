package com.lframework.xingyun.sc.vo.retail.returned;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.exceptions.impl.InputErrorException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.starter.web.vo.BaseVo;
import com.lframework.xingyun.sc.dto.purchase.receive.GetPaymentDateDto;
import com.lframework.xingyun.sc.dto.retail.out.RetailOutSheetDetailLotDto;
import com.lframework.xingyun.sc.entity.RetailConfig;
import com.lframework.xingyun.sc.entity.RetailOutSheetDetail;
import com.lframework.xingyun.sc.service.retail.RetailConfigService;
import com.lframework.xingyun.sc.service.retail.RetailOutSheetDetailLotService;
import com.lframework.xingyun.sc.service.retail.RetailOutSheetDetailService;
import com.lframework.xingyun.sc.service.retail.RetailOutSheetService;
import com.lframework.xingyun.sc.vo.paytype.OrderPayTypeVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateRetailReturnVo implements BaseVo, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 仓库ID
   */
  @ApiModelProperty(value = "仓库ID", required = true)
  @NotBlank(message = "仓库ID不能为空！")
  private String scId;

  /**
   * 会员ID
   */
  @ApiModelProperty("会员ID")
  private String memberId;

  /**
   * 采购员ID
   */
  @ApiModelProperty("采购员ID")
  private String salerId;

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
   * 收货单ID
   */
  @ApiModelProperty("收货单ID")
  private String outSheetId;

  /**
   * 商品信息
   */
  @ApiModelProperty(value = "商品信息", required = true)
  @Valid
  @NotEmpty(message = "商品不能为空！")
  private List<RetailReturnProductVo> products;

  /**
   * 支付方式
   */
  @ApiModelProperty("支付方式")
  @Valid
  private List<OrderPayTypeVo> payTypes;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;

  /**
   * 是否关联零售出库单
   */
  @ApiModelProperty("是否关联零售出库单")
  private Boolean required;

  public void validate() {

    RetailConfigService retailConfigService = ApplicationUtil.getBean(RetailConfigService.class);
    RetailConfig retailConfig = retailConfigService.get();

    if (retailConfig.getRetailReturnRequireMember()) {
      if (StringUtil.isBlank(this.memberId)) {
        throw new DefaultClientException("请选择会员！");
      }
    }

    if (!retailConfig.getRetailReturnRequireOutStock().equals(this.required)) {
      throw new DefaultClientException("系统参数发生改变，请刷新页面后重试！");
    }

    this.validate(retailConfig.getRetailReturnRequireOutStock());
  }

  protected void validate(boolean requireOut) {

    RetailOutSheetService retailOutSheetService = ApplicationUtil.getBean(
        RetailOutSheetService.class);
    GetPaymentDateDto paymentDate = retailOutSheetService.getPaymentDate(this.getMemberId());
    if (paymentDate.getAllowModify()) {
      if (this.getPaymentDate() == null) {
        throw new InputErrorException("付款日期不能为空！");
      }
    }

    if (requireOut) {
      if (StringUtil.isBlank(this.getOutSheetId())) {
        throw new InputErrorException("零售出库单不能为空！");
      }
    }

    RetailOutSheetDetailService retailOutSheetDetailService = ApplicationUtil.getBean(
        RetailOutSheetDetailService.class);
    RetailOutSheetDetailLotService retailOutSheetDetailLotService = ApplicationUtil.getBean(
        RetailOutSheetDetailLotService.class);
    int orderNo = 1;
    for (RetailReturnProductVo product : this.products) {

      if (StringUtil.isBlank(product.getProductId())) {
        throw new InputErrorException("第" + orderNo + "行商品不能为空！");
      }

      if (product.getReturnNum() == null) {
        throw new InputErrorException("第" + orderNo + "行商品退货数量不能为空！");
      }

      if (product.getReturnNum() <= 0) {
        throw new InputErrorException("第" + orderNo + "行商品退货数量必须大于0！");
      }

      if (!requireOut) {

        if (product.getOriPrice() == null) {
          throw new InputErrorException("第" + orderNo + "行商品参考零售价不能为空！");
        }

        if (product.getTaxPrice() == null) {
          throw new InputErrorException("第" + orderNo + "行商品价格不能为空！");
        }

        if (product.getTaxPrice().doubleValue() < 0D) {
          throw new InputErrorException("第" + orderNo + "行商品价格不允许小于0！");
        }

        if (!NumberUtil.equal(product.getOriPrice(), 0D)) {
          // 由 根据原价和折扣率校验现价 更改为 根据原价、现价计算折扣率，即：不以传入的折扣率为准
          BigDecimal discountRate = NumberUtil.getNumber(
              NumberUtil.mul(NumberUtil.div(product.getTaxPrice(), product.getOriPrice()), 100), 2);
          product.setDiscountRate(discountRate);
        } else {
          //如果原价为0，折扣率固定为100
          product.setDiscountRate(BigDecimal.valueOf(100));
        }
      } else {
        if (StringUtil.isNotBlank(product.getOutSheetDetailId())) {
          RetailOutSheetDetailLotDto detailLot = retailOutSheetDetailLotService.findById(
              product.getOutSheetDetailId());
          RetailOutSheetDetail detail = retailOutSheetDetailService.getById(
              detailLot.getDetailId());
          product.setTaxPrice(detail.getTaxPrice());
        } else {
          product.setTaxPrice(BigDecimal.ZERO);
        }
      }

      orderNo++;
    }

    BigDecimal totalAmount = this.products.stream()
        .map(t -> NumberUtil.mul(t.getReturnNum(), t.getTaxPrice())).reduce(NumberUtil::add)
        .orElse(BigDecimal.ZERO);
    BigDecimal payTypeAmount = CollectionUtil.isEmpty(this.payTypes) ? BigDecimal.ZERO
        : this.payTypes.stream().map(OrderPayTypeVo::getPayAmount).reduce(NumberUtil::add)
            .orElse(BigDecimal.ZERO);
    if (!NumberUtil.equal(totalAmount, payTypeAmount)) {
      throw new InputErrorException("所有支付方式的支付金额不等于含税总金额，请检查！");
    }
  }
}
