package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.entity.BaseEntity;
import com.lframework.starter.web.dto.BaseDto;
import com.lframework.xingyun.sc.enums.ReceiveSheetStatus;
import com.lframework.xingyun.sc.enums.SettleStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author zzx
 * @since 2021-10-09
 */
@Data
@TableName("tbl_receive_sheet")
public class ReceiveSheet extends BaseEntity implements BaseDto {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 单号
   */
  private String code;

  /**
   * 仓库ID
   */
  private String scId;

  /**
   * 供应商ID
   */
  private String supplierId;

  /**
   * 采购员ID
   */
  private String purchaserId;

  /**
   * 付款日期
   */
  private LocalDate paymentDate;

  /**
   * 到货日期
   */
  private LocalDate receiveDate;

  /**
   * 采购单ID
   */
  private String purchaseOrderId;

  /**
   * 商品数量
   */
  private Integer totalNum;

  /**
   * 赠品数量
   */
  private Integer totalGiftNum;

  /**
   * 收货金额
   */
  private BigDecimal totalAmount;

  /**
   * 备注
   */
  private String description;

  /**
   * 创建人ID 新增时赋值
   */
  @TableField(fill = FieldFill.INSERT)
  private String createById;

  /**
   * 创建人 新增时赋值
   */
  @TableField(fill = FieldFill.INSERT)
  private String createBy;

  /**
   * 创建时间 新增时赋值
   */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 修改人 新增和修改时赋值
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private String updateBy;

  /**
   * 修改人ID 新增和修改时赋值
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private String updateById;

  /**
   * 修改时间 新增和修改时赋值
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  /**
   * 审核人
   */
  private String approveBy;

  /**
   * 审核时间
   */
  private LocalDateTime approveTime;

  /**
   * 状态
   */
  private ReceiveSheetStatus status;

  /**
   * 拒绝原因
   */
  private String refuseReason;

  /**
   * 结算状态
   */
  private SettleStatus settleStatus;
}
