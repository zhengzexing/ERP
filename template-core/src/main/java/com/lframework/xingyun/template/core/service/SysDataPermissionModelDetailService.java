package com.lframework.xingyun.template.core.service;

import com.lframework.starter.web.service.BaseMpService;
import com.lframework.xingyun.template.core.vo.permission.SysDataPermissionModelDetailVo;
import com.lframework.xingyun.template.core.entity.SysDataPermissionModelDetail;
import java.util.List;
import java.util.Map;

public interface SysDataPermissionModelDetailService extends
    BaseMpService<SysDataPermissionModelDetail> {

  /**
   * 解析SQL
   *
   * @param models
   * @return
   */
  String toSql(List<SysDataPermissionModelDetailVo> models);

  /**
   * 格式化SQL
   *
   * @param sqlTemplate
   * @param params      表别名Map Key：表名占位符，具体定义数据表中的table_name Value：具体SQL的别名
   * @return
   */
  String formatSql(String sqlTemplate, Map<String, String> params);

}
