package com.lframework.xingyun.basedata.excel.address;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.lframework.starter.web.annotations.excel.ExcelRequired;
import com.lframework.starter.web.components.excel.ExcelModel;
import com.lframework.xingyun.basedata.enums.AddressEntityType;
import com.lframework.xingyun.basedata.enums.AddressType;
import lombok.Data;

@Data
public class AddressImportModel implements ExcelModel {

  /**
   * 实体编号
   */
  @ExcelRequired
  @ExcelProperty("实体编号")
  private String entityCode;

  /**
   * 实体ID
   */
  @ExcelIgnore
  private String entityId;

  /**
   * 实体类型
   */
  @ExcelRequired
  @ExcelProperty("实体类型")
  private String entityType;

  /**
   * 实体类型枚举
   */
  @ExcelIgnore
  private AddressEntityType entityTypeEnum;

  /**
   * 地址类型
   */
  @ExcelRequired
  @ExcelProperty("地址类型")
  private String addressType;

  /**
   * 地址类型枚举
   */
  @ExcelIgnore
  private AddressType addressTypeEnum;

  /**
   * 姓名
   */
  @ExcelRequired
  @ExcelProperty("姓名")
  private String name;

  /**
   * 联系电话
   */
  @ExcelRequired
  @ExcelProperty("联系电话")
  private String telephone;

  /**
   * 地区
   */
  @ExcelRequired
  @ExcelProperty("地区")
  private String city;

  /**
   * 地区ID
   */
  @ExcelIgnore
  private String areaId;

  /**
   * 详细地址
   */
  @ExcelRequired
  @ExcelProperty("详细地址")
  private String address;

  /**
   * 是否默认地址
   */
  @ExcelRequired
  @ExcelProperty("是否默认地址")
  private String isDefault;
}
