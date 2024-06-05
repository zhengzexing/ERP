package com.lframework.xingyun.basedata.excel.product.category;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.annotations.excel.ExcelRequired;
import com.lframework.starter.web.components.excel.ExcelModel;
import lombok.Data;

@Data
public class ProductCategoryImportModel implements ExcelModel {

  /**
   * ID
   */
  @ExcelIgnore
  private String id;

  /**
   * 编号
   */
  @ExcelRequired
  @ExcelProperty("编号")
  private String code;

  /**
   * 名称
   */
  @ExcelRequired
  @ExcelProperty("名称")
  private String name;

  /**
   * 上级类目编号
   */
  @ExcelProperty("上级类目编号")
  private String parentCode;

  /**
   * 上级类目ID
   */
  @ExcelIgnore
  private String parentId;

  /**
   * 备注
   */
  @ExcelProperty("备注")
  private String description;

}
