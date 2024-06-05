package com.lframework.xingyun.template.gen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.entity.BaseEntity;
import com.lframework.starter.web.dto.BaseDto;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author zzx
 * @since 2021-12-10
 */
@Data
@TableName("gen_create_column_config")
public class GenCreateColumnConfig extends BaseEntity implements BaseDto {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 是否必填
   */
  private Boolean required;

  /**
   * 排序编号
   */
  private Integer orderNo;


}
