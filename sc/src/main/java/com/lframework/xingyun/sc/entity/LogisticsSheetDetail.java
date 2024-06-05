package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.entity.BaseEntity;
import com.lframework.starter.web.dto.BaseDto;
import com.lframework.xingyun.sc.enums.LogisticsSheetDetailBizType;
import lombok.Data;

/**
 * <p>
 * 物流单
 * </p>
 *
 * @author zzx
 * @since 2023-06-13
 */
@Data
@TableName("tbl_logistics_sheet_detail")
public class LogisticsSheetDetail extends BaseEntity implements BaseDto {

  public static final String CACHE_NAME = "LogisticsSheetDetail";

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 物流单ID
   */
  private String sheetId;

  /**
   * 业务单据ID
   */
  private String bizId;

  /**
   * 业务类型
   */
  private LogisticsSheetDetailBizType bizType;
}
