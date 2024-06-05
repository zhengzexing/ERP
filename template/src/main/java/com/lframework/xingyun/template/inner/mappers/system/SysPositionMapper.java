package com.lframework.xingyun.template.inner.mappers.system;

import com.lframework.xingyun.template.inner.entity.SysPosition;
import com.lframework.starter.web.mapper.BaseMapper;
import com.lframework.xingyun.template.inner.vo.system.position.QuerySysPositionVo;
import com.lframework.xingyun.template.inner.vo.system.position.SysPositionSelectorVo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 系统岗位 Mapper 接口
 * </p>
 *
 * @author zzx
 * @since 2021-06-30
 */
public interface SysPositionMapper extends BaseMapper<SysPosition> {

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<SysPosition> query(@Param("vo") QuerySysPositionVo vo);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  SysPosition findById(String id);

  /**
   * 选择器
   *
   * @param vo
   * @return
   */
  List<SysPosition> selector(@Param("vo") SysPositionSelectorVo vo);

}
