package com.lframework.xingyun.sc.mappers;

import com.lframework.starter.web.mapper.BaseMapper;
import com.lframework.xingyun.sc.dto.retail.returned.RetailReturnFullDto;
import com.lframework.xingyun.sc.entity.RetailReturn;
import com.lframework.xingyun.sc.vo.retail.returned.QueryRetailReturnVo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzx
 * @since 2021-11-04
 */
public interface RetailReturnMapper extends BaseMapper<RetailReturn> {

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<RetailReturn> query(@Param("vo") QueryRetailReturnVo vo,
      @Param("dataPermission") String dataPermission);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  RetailReturnFullDto getDetail(String id);
}
