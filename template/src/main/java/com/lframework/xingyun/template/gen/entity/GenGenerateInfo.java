package com.lframework.xingyun.template.gen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.xingyun.template.gen.enums.GenKeyType;
import com.lframework.xingyun.template.gen.enums.GenTemplateType;
import com.lframework.starter.web.entity.BaseEntity;
import com.lframework.starter.web.dto.BaseDto;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author zzx
 * @since 2021-12-08
 */
@Data
@TableName("gen_generate_info")
public class GenGenerateInfo extends BaseEntity implements BaseDto {

  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 生成模板类型
   */
  private GenTemplateType templateType;

  /**
   * 包名
   */
  private String packageName;

  /**
   * 模块名
   */
  private String moduleName;

  /**
   * 业务名
   */
  private String bizName;

  /**
   * 类名
   */
  private String className;

  /**
   * 类描述
   */
  private String classDescription;

  /**
   * 父级菜单ID
   */
  private String parentMenuId;

  /**
   * 主键类型
   */
  private GenKeyType keyType;

  /**
   * 作者
   */
  private String author;

  /**
   * 本级菜单编号
   */
  private String menuCode;

  /**
   * 本级菜单名称
   */
  private String menuName;

  /**
   * 详情页Span总数量
   */
  private Integer detailSpan;

  /**
   * 是否应用缓存
   */
  private Boolean isCache;

  /**
   * 是否内置删除功能
   */
  private Boolean hasDelete;
}
