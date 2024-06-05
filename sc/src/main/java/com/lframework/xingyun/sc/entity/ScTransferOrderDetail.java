package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.entity.BaseEntity;
import com.lframework.starter.web.dto.BaseDto;
import java.math.BigDecimal;
import lombok.Data;

/**
 * <p>
 * 仓库调拨单明细
 * </p>
 *
 * @author zzx
 * @since 2023-04-18
 */
@Data
@TableName("tbl_sc_transfer_order_detail")
public class ScTransferOrderDetail extends BaseEntity implements BaseDto {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 调拨单ID
   */
  private String orderId;

  /**
   * 商品ID
   */
  private String productId;

  /**
   * 调拨数量
   */
  private Integer transferNum;

  /**
   * 成本价
   */
  private BigDecimal taxPrice;

  /**
   * 备注
   */
  private String description;

  /**
   * 排序
   */
  private Integer orderNo;

  /**
   * 已收货数量
   */
  private Integer receiveNum;

}
