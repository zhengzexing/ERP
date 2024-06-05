package com.lframework.xingyun.sc.excel.purchase;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.DateUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.starter.web.components.excel.ExcelModel;
import com.lframework.xingyun.basedata.entity.PayType;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.entity.Supplier;
import com.lframework.xingyun.basedata.service.paytype.PayTypeService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.basedata.service.supplier.SupplierService;
import com.lframework.xingyun.sc.entity.OrderPayType;
import com.lframework.xingyun.sc.entity.PurchaseOrder;
import com.lframework.xingyun.sc.service.paytype.OrderPayTypeService;
import com.lframework.xingyun.template.core.dto.UserDto;
import com.lframework.xingyun.template.core.service.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class PurchaseOrderExportModel extends BaseBo<PurchaseOrder> implements ExcelModel {

  /**
   * 单号
   */
  @ExcelProperty("业务单据号")
  private String code;

  /**
   * 仓库名称
   */
  @ExcelProperty("仓库名称")
  private String scName;

  /**
   * 供应商名称
   */
  @ExcelProperty("供应商名称")
  private String supplierName;

  /**
   * 采购员姓名
   */
  @ExcelProperty("采购员")
  private String purchaserName;

  /**
   * 预计到货日期
   */
  @ExcelProperty("预计到货日期")
  @DateTimeFormat(StringPool.DATE_PATTERN)
  private Date expectArriveDate;

  /**
   * 采购数量
   */
  @ExcelProperty("采购数量")
  private Integer purchaseNum;

  /**
   * 赠品数量
   */
  @ExcelProperty("赠品数量")
  private Integer giftNum;

  /**
   * 采购金额
   */
  @ExcelProperty("采购金额")
  private BigDecimal purchaseAmount;

  /**
   * 约定支付
   */
  @ExcelProperty("约定支付")
  private String payTypeStr;

  /**
   * 备注
   */
  @ExcelProperty("订单备注")
  private String description;

  /**
   * 创建人
   */
  @ExcelProperty("创建人")
  private String createBy;

  /**
   * 创建时间
   */
  @ExcelProperty("创建时间")
  @DateTimeFormat(StringPool.DATE_TIME_PATTERN)
  private Date createTime;

  /**
   * 审核人
   */
  @ExcelProperty("审核人")
  private String approveBy;

  /**
   * 审核时间
   */
  @ExcelProperty("审核时间")
  @DateTimeFormat(StringPool.DATE_TIME_PATTERN)
  private Date approveTime;

  /**
   * 状态
   */
  @ExcelProperty("状态")
  private String status;

  /**
   * 拒绝原因
   */
  @ExcelProperty("拒绝原因")
  private String refuseReason;

  public PurchaseOrderExportModel() {

  }

  public PurchaseOrderExportModel(PurchaseOrder dto) {

    super(dto);
  }

  @Override
  public <A> BaseBo<PurchaseOrder> convert(PurchaseOrder dto) {

    return this;
  }

  @Override
  protected void afterInit(PurchaseOrder dto) {

    StoreCenterService storeCenterService = ApplicationUtil.getBean(StoreCenterService.class);
    StoreCenter sc = storeCenterService.findById(dto.getScId());

    SupplierService supplierService = ApplicationUtil.getBean(SupplierService.class);
    Supplier supplier = supplierService.findById(dto.getSupplierId());

    UserService userService = ApplicationUtil.getBean(UserService.class);

    UserDto approveBy = null;
    if (!StringUtil.isBlank(dto.getApproveBy())) {
      approveBy = userService.findById(dto.getApproveBy());
    }

    this.setCode(dto.getCode());
    this.setScName(sc.getName());
    this.setSupplierName(supplier.getName());
    if (!StringUtil.isBlank(dto.getPurchaserId())) {
      UserDto purchaser = userService.findById(dto.getPurchaserId());
      this.setPurchaserName(purchaser.getName());
    }
    this.setExpectArriveDate(DateUtil.toDate(dto.getExpectArriveDate()));
    this.setPurchaseNum(dto.getTotalNum());
    this.setGiftNum(dto.getTotalGiftNum());
    this.setPurchaseAmount(dto.getTotalAmount());
    this.setDescription(dto.getDescription());
    this.setCreateTime(DateUtil.toDate(dto.getCreateTime()));
    if (approveBy != null) {
      this.setApproveBy(approveBy.getName());
    }

    if (dto.getApproveTime() != null) {
      this.setApproveTime(DateUtil.toDate(dto.getApproveTime()));
    }

    this.setStatus(dto.getStatus().getDesc());
    this.setRefuseReason(dto.getRefuseReason());

    OrderPayTypeService orderPayTypeService = ApplicationUtil.getBean(OrderPayTypeService.class);
    List<OrderPayType> orderPayTypes = orderPayTypeService.findByOrderId(dto.getId());

    PayTypeService payTypeService = ApplicationUtil.getBean(PayTypeService.class);

    if (CollectionUtil.isNotEmpty(orderPayTypes)) {
      this.payTypeStr = orderPayTypes.stream().map(orderPayType -> {
        PayType payType = payTypeService.findById(orderPayType.getPayTypeId());
        return StringUtil.format("{}：{}元", payType.getName(), orderPayType.getPayAmount());
      }).collect(Collectors.joining("；"));
    }
  }
}
