package com.lframework.xingyun.basedata.bo.shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Shop;
import com.lframework.xingyun.template.core.dto.DeptDto;
import com.lframework.xingyun.template.core.service.DeptService;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 门店 QueryBo
 * </p>
 *
 * @author zzx
 */
@Data
public class QueryShopBo extends BaseBo<Shop> {

  /**
   * ID
   */
  @ApiModelProperty("ID")
  private String id;

  /**
   * 编号
   */
  @ApiModelProperty("编号")
  private String code;

  /**
   * 名称
   */
  @ApiModelProperty("名称")
  private String name;

  /**
   * 所属部门名称
   */
  @ApiModelProperty("所属部门名称")
  private String deptName;

  /**
   * 状态
   */
  @ApiModelProperty("状态")
  private Boolean available;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;

  /**
   * 创建人
   */
  @ApiModelProperty("创建人")
  private String createBy;

  /**
   * 创建时间
   */
  @ApiModelProperty("创建时间")
  @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
  private LocalDateTime createTime;

  public QueryShopBo() {

  }

  public QueryShopBo(Shop dto) {

    super(dto);
  }

  @Override
  protected void afterInit(Shop dto) {
    if (!StringUtil.isBlank(dto.getDeptId())) {
      DeptService deptService = ApplicationUtil.getBean(DeptService.class);

      DeptDto dept = deptService.findById(dto.getDeptId());

      this.deptName = dept.getName();
    }
  }
}
