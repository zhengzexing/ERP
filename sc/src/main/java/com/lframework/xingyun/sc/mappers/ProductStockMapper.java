package com.lframework.xingyun.sc.mappers;

import com.lframework.starter.web.mapper.BaseMapper;
import com.lframework.xingyun.sc.entity.ProductStock;
import com.lframework.xingyun.sc.vo.stock.QueryProductStockVo;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzx
 * @since 2021-09-12
 */
public interface ProductStockMapper extends BaseMapper<ProductStock> {

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<ProductStock> query(@Param("vo") QueryProductStockVo vo,
      @Param("dataPermission") String dataPermission);

  /**
   * 根据商品ID、仓库ID查询
   *
   * @param productId
   * @param scId
   * @return
   */
  ProductStock getByProductIdAndScId(@Param("productId") String productId,
      @Param("scId") String scId);

  /**
   * 根据商品ID、仓库ID查询
   *
   * @param productIds
   * @param scId
   * @return
   */
  List<ProductStock> getByProductIdsAndScId(@Param("productIds") List<String> productIds,
      @Param("scId") String scId, @Param("productType") Integer productType);

  /**
   * 入库
   *
   * @param productId
   * @param scId
   * @param stockNum
   * @param taxAmount
   * @param oriStockNum
   * @param oriTaxAmount
   * @return
   */
  int addStock(@Param("productId") String productId, @Param("scId") String scId,
      @Param("stockNum") Integer stockNum,
      @Param("taxAmount") BigDecimal taxAmount,
      @Param("oriStockNum") Integer oriStockNum, @Param("oriTaxAmount") BigDecimal oriTaxAmount,
      @Param("reCalcCostPrice") boolean reCalcCostPrice);

  /**
   * 出库
   *
   * @param productId
   * @param scId
   * @param stockNum
   * @param taxAmount
   * @param oriStockNum
   * @param oriTaxAmount
   * @return
   */
  int subStock(@Param("productId") String productId, @Param("scId") String scId,
      @Param("stockNum") Integer stockNum,
      @Param("taxAmount") BigDecimal taxAmount,
      @Param("oriStockNum") Integer oriStockNum, @Param("oriTaxAmount") BigDecimal oriTaxAmount,
      @Param("reCalcCostPrice") boolean reCalcCostPrice);

  /**
   * 库存成本调整
   *
   * @param productId
   * @param scId
   * @param taxPrice
   */
  void stockCostAdjust(@Param("productId") String productId, @Param("scId") String scId,
      @Param("taxPrice") BigDecimal taxPrice);
}
