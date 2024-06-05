package com.lframework.xingyun.sc.service.purchase;

import com.lframework.starter.web.resp.PageResult;
import com.lframework.starter.web.service.BaseMpService;
import com.lframework.xingyun.sc.vo.purchase.QueryPurchaseProductVo;
import com.lframework.xingyun.sc.dto.purchase.PurchaseOrderFullDto;
import com.lframework.xingyun.sc.dto.purchase.PurchaseOrderWithReceiveDto;
import com.lframework.xingyun.sc.dto.purchase.PurchaseProductDto;
import com.lframework.xingyun.sc.entity.PurchaseOrder;
import com.lframework.xingyun.sc.vo.purchase.ApprovePassPurchaseOrderVo;
import com.lframework.xingyun.sc.vo.purchase.ApproveRefusePurchaseOrderVo;
import com.lframework.xingyun.sc.vo.purchase.BatchApprovePassPurchaseOrderVo;
import com.lframework.xingyun.sc.vo.purchase.BatchApproveRefusePurchaseOrderVo;
import com.lframework.xingyun.sc.vo.purchase.CreatePurchaseOrderVo;
import com.lframework.xingyun.sc.vo.purchase.PurchaseOrderSelectorVo;
import com.lframework.xingyun.sc.vo.purchase.QueryPurchaseOrderVo;
import com.lframework.xingyun.sc.vo.purchase.QueryPurchaseOrderWithRecevieVo;
import com.lframework.xingyun.sc.vo.purchase.UpdatePurchaseOrderVo;
import java.util.List;

public interface PurchaseOrderService extends BaseMpService<PurchaseOrder> {

  /**
   * 查询列表
   *
   * @param pageIndex
   * @param pageSize
   * @param vo
   * @return
   */
  PageResult<PurchaseOrder> query(Integer pageIndex, Integer pageSize, QueryPurchaseOrderVo vo);

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<PurchaseOrder> query(QueryPurchaseOrderVo vo);

  /**
   * 选择器
   *
   * @param pageIndex
   * @param pageSize
   * @param vo
   * @return
   */
  PageResult<PurchaseOrder> selector(Integer pageIndex, Integer pageSize,
      PurchaseOrderSelectorVo vo);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  PurchaseOrderFullDto getDetail(String id);

  /**
   * 根据ID查询（收货业务）
   *
   * @param id
   * @return
   */
  PurchaseOrderWithReceiveDto getWithReceive(String id);

  /**
   * 查询列表（收货业务）
   *
   * @param pageIndex
   * @param pageSize
   * @param vo
   * @return
   */
  PageResult<PurchaseOrder> queryWithReceive(Integer pageIndex, Integer pageSize,
      QueryPurchaseOrderWithRecevieVo vo);

  /**
   * 创建订单
   *
   * @param vo
   * @return
   */
  String create(CreatePurchaseOrderVo vo);

  /**
   * 修改订单
   *
   * @param vo
   */
  void update(UpdatePurchaseOrderVo vo);

  /**
   * 审核通过
   *
   * @param vo
   */
  void approvePass(ApprovePassPurchaseOrderVo vo);

  /**
   * 批量审核通过
   *
   * @param vo
   */
  void batchApprovePass(BatchApprovePassPurchaseOrderVo vo);

  /**
   * 直接审核通过
   *
   * @param vo
   */
  String directApprovePass(CreatePurchaseOrderVo vo);

  /**
   * 审核拒绝
   *
   * @param vo
   */
  void approveRefuse(ApproveRefusePurchaseOrderVo vo);

  /**
   * 批量审核拒绝
   *
   * @param vo
   */
  void batchApproveRefuse(BatchApproveRefusePurchaseOrderVo vo);

  /**
   * 根据ID删除
   *
   * @param id
   */
  void deleteById(String id);

  /**
   * 根据IDs删除
   *
   * @param ids
   */
  void deleteByIds(List<String> ids);

  /**
   * 取消审核
   *
   * @param id
   */
  void cancelApprovePass(String id);

  /**
   * 根据关键字查询采购商品信息
   *
   * @param pageIndex
   * @param pageSize
   * @param condition
   * @return
   */
  PageResult<PurchaseProductDto> queryPurchaseByCondition(Integer pageIndex, Integer pageSize, String condition);

  /**
   * 查询可采购商品信息
   *
   * @param pageIndex
   * @param pageSize
   * @param vo
   * @return
   */
  PageResult<PurchaseProductDto> queryPurchaseList(Integer pageIndex, Integer pageSize, QueryPurchaseProductVo vo);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  PurchaseProductDto getPurchaseById(String id);
}
