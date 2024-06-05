package com.lframework.xingyun.core.service;

import com.lframework.starter.web.service.BaseMpService;
import com.lframework.xingyun.core.entity.OrderTimeLine;
import java.util.List;

/**
 * @author zzx
 * @since 2022/8/10
 */
public interface OrderTimeLineService extends BaseMpService<OrderTimeLine> {

  /**
   * 根据订单查询
   * @param orderId
   * @return
   */
  List<OrderTimeLine> getByOrder(String orderId);

  /**
   * 根据订单删除
   * @param orderId
   */
  void deleteByOrder(String orderId);
}
