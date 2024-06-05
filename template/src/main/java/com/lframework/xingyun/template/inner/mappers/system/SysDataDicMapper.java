package com.lframework.xingyun.template.inner.mappers.system;

import com.lframework.xingyun.template.inner.entity.SysDataDic;
import com.lframework.starter.web.mapper.BaseMapper;
import com.lframework.xingyun.template.inner.vo.system.dic.QuerySysDataDicVo;
import com.lframework.xingyun.template.inner.vo.system.dic.SysDataDicSelectorVo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author zzx
 */
public interface SysDataDicMapper extends BaseMapper<SysDataDic> {

  /**
   * 查询列表
   *
   * @return
   */
  List<SysDataDic> query(@Param("vo") QuerySysDataDicVo vo);

  /**
   * 选择器
   *
   * @return
   */
  List<SysDataDic> selector(@Param("vo") SysDataDicSelectorVo vo);
}
