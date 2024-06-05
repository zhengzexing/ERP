package com.lframework.xingyun.sc.dto.stock.take.sheet;

import com.lframework.starter.web.dto.BaseDto;
import com.lframework.xingyun.sc.enums.TakeStockSheetStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * <p>
 * 盘点单详情 Dto
 * </p>
 *
 * @author zzx
 */
@Data
public class TakeStockSheetFullDto implements BaseDto, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 业务单据号
   */
  private String code;

  /**
   * 盘点任务ID
   */
  private String planId;

  /**
   * 预先盘点单ID
   */
  private String preSheetId;

  /**
   * 仓库ID
   */
  private String scId;

  /**
   * 状态
   */
  private TakeStockSheetStatus status;

  /**
   * 备注
   */
  private String description;

  /**
   * 拒绝理由
   */
  private String refuseReason;

  /**
   * 创建人
   */
  private String createBy;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 修改人
   */
  private String updateBy;

  /**
   * 修改时间
   */
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
   * 明细
   */
  private List<SheetDetailDto> details;


  @Data
  public static class SheetDetailDto implements BaseDto, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    private String id;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 盘点数量
     */
    private Integer takeNum;

    /**
     * 备注
     */
    private String description;
  }
}
