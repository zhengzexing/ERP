package com.lframework.xingyun.basedata.mappers;

import com.lframework.starter.web.mapper.BaseMapper;
import com.lframework.xingyun.basedata.entity.Address;
import com.lframework.xingyun.basedata.vo.address.AddressSelectorVo;
import com.lframework.xingyun.basedata.vo.address.QueryAddressVo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzx
 * @since 2023-03-26
 */
public interface AddressMapper extends BaseMapper<Address> {

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<Address> query(@Param("vo") QueryAddressVo vo);

  /**
   * 选择器
   *
   * @return
   */
  List<Address> selector(@Param("vo") AddressSelectorVo vo);
}
