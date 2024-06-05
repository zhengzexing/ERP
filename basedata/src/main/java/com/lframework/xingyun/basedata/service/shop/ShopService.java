package com.lframework.xingyun.basedata.service.shop;

import com.lframework.starter.web.resp.PageResult;
import com.lframework.starter.web.service.BaseMpService;
import com.lframework.xingyun.basedata.entity.Shop;
import com.lframework.xingyun.basedata.vo.shop.CreateShopVo;
import com.lframework.xingyun.basedata.vo.shop.QueryShopVo;
import com.lframework.xingyun.basedata.vo.shop.UpdateShopVo;
import java.util.List;

/**
 * 门店 Service
 *
 * @author zzx
 */
public interface ShopService extends BaseMpService<Shop> {

  /**
   * 查询列表
   * @return
   */
  PageResult<Shop> query(Integer pageIndex, Integer pageSize, QueryShopVo vo);

  /**
   * 查询列表
   * @param vo
   * @return
   */
  List<Shop> query(QueryShopVo vo);

  /**
   * 根据ID查询
   * @param id
   * @return
   */
  Shop findById(String id);

  /**
   * 创建
   * @param vo
   * @return
   */
  String create(CreateShopVo vo);

  /**
   * 修改
   * @param vo
   */
  void update(UpdateShopVo vo);

}
