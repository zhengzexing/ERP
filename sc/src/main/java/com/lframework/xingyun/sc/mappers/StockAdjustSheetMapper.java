package com.lframework.xingyun.sc.mappers;

import com.lframework.starter.web.mapper.BaseMapper;
import com.lframework.xingyun.sc.dto.stock.adjust.stock.StockAdjustProductDto;
import com.lframework.xingyun.sc.dto.stock.adjust.stock.StockAdjustSheetFullDto;
import com.lframework.xingyun.sc.entity.StockAdjustSheet;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.QueryStockAdjustProductVo;
import com.lframework.xingyun.sc.vo.stock.adjust.stock.QueryStockAdjustSheetVo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 库存调整单 Mapper 接口
 * </p>
 *
 * @author zzx
 */
public interface StockAdjustSheetMapper extends BaseMapper<StockAdjustSheet> {

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<StockAdjustSheet> query(@Param("vo") QueryStockAdjustSheetVo vo,
      @Param("dataPermission") String dataPermission);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  StockAdjustSheetFullDto getDetail(@Param("id") String id);

  /**
   * 根据关键字查询库存成本调整单商品信息
   *
   * @param scId
   * @param condition
   * @return
   */
  List<StockAdjustProductDto> queryStockAdjustByCondition(
      @Param("scId") String scId, @Param("condition") String condition,
      @Param("dataPermission") String dataPermission);

  /**
   * 查询库存成本调整单商品信息
   *
   * @param vo
   * @return
   */
  List<StockAdjustProductDto> queryStockAdjustList(
      @Param("vo") QueryStockAdjustProductVo vo,
      @Param("dataPermission") String dataPermission);
}
