package com.lframework.xingyun.sc.service.stock.take;

import com.lframework.starter.web.service.BaseMpService;
import com.lframework.xingyun.sc.entity.TakeStockConfig;
import com.lframework.xingyun.sc.vo.stock.take.config.UpdateTakeStockConfigVo;

/**
 * 盘点参数 Service
 *
 * @author zzx
 */
public interface TakeStockConfigService extends BaseMpService<TakeStockConfig> {

  /**
   * 根据ID查询
   *
   * @return
   */
  TakeStockConfig get();


  /**
   * 修改
   *
   * @param vo
   */
  void update(UpdateTakeStockConfigVo vo);

}
