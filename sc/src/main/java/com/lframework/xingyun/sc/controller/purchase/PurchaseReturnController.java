package com.lframework.xingyun.sc.controller.purchase;

import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.resp.PageResult;
import com.lframework.starter.web.utils.PageResultUtil;
import com.lframework.starter.web.annotations.security.HasPermission;
import com.lframework.starter.web.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.controller.DefaultBaseController;
import com.lframework.starter.web.resp.InvokeResult;
import com.lframework.starter.web.resp.InvokeResultBuilder;
import com.lframework.starter.web.utils.ExcelUtil;
import com.lframework.xingyun.core.bo.print.A4ExcelPortraitPrintBo;
import com.lframework.xingyun.sc.bo.purchase.returned.GetPurchaseReturnBo;
import com.lframework.xingyun.sc.bo.purchase.returned.PrintPurchaseReturnBo;
import com.lframework.xingyun.sc.bo.purchase.returned.QueryPurchaseReturnBo;
import com.lframework.xingyun.sc.dto.purchase.returned.PurchaseReturnFullDto;
import com.lframework.xingyun.sc.entity.PurchaseReturn;
import com.lframework.xingyun.sc.excel.purchase.returned.PurchaseReturnExportModel;
import com.lframework.xingyun.sc.service.purchase.PurchaseReturnService;
import com.lframework.xingyun.sc.vo.purchase.returned.ApprovePassPurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.ApproveRefusePurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.BatchApprovePassPurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.BatchApproveRefusePurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.CreatePurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.QueryPurchaseReturnVo;
import com.lframework.xingyun.sc.vo.purchase.returned.UpdatePurchaseReturnVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 采购退单管理
 *
 * @author zzx
 */
@Api(tags = "采购退单管理")
@Validated
@RestController
@RequestMapping("/purchase/return")
public class PurchaseReturnController extends DefaultBaseController {

  @Autowired
  private PurchaseReturnService purchaseReturnService;

  /**
   * 打印
   */
  @ApiOperation("打印")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"purchase:return:query"})
  @GetMapping("/print")
  public InvokeResult<A4ExcelPortraitPrintBo<PrintPurchaseReturnBo>> print(
      @NotBlank(message = "退单ID不能为空！") String id) {

    PurchaseReturnFullDto data = purchaseReturnService.getDetail(id);

    PrintPurchaseReturnBo result = new PrintPurchaseReturnBo(data);
    A4ExcelPortraitPrintBo<PrintPurchaseReturnBo> printResult = new A4ExcelPortraitPrintBo<>(
        "print/purchase-return.ftl", result);

    return InvokeResultBuilder.success(printResult);
  }

  /**
   * 退单列表
   */
  @ApiOperation("退单列表")
  @HasPermission({"purchase:return:query"})
  @GetMapping("/query")
  public InvokeResult<PageResult<QueryPurchaseReturnBo>> query(@Valid QueryPurchaseReturnVo vo) {

    PageResult<PurchaseReturn> pageResult = purchaseReturnService.query(getPageIndex(vo),
        getPageSize(vo), vo);

    List<PurchaseReturn> datas = pageResult.getDatas();
    List<QueryPurchaseReturnBo> results = null;

    if (!CollectionUtil.isEmpty(datas)) {

      results = datas.stream().map(QueryPurchaseReturnBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }

  /**
   * 导出
   */
  @ApiOperation("导出")
  @HasPermission({"purchase:return:export"})
  @PostMapping("/export")
  public void export(@Valid QueryPurchaseReturnVo vo) {

    ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("采购退货单信息",
        PurchaseReturnExportModel.class);

    try {
      int pageIndex = 1;
      while (true) {
        PageResult<PurchaseReturn> pageResult = purchaseReturnService.query(pageIndex,
            getExportSize(), vo);
        List<PurchaseReturn> datas = pageResult.getDatas();
        List<PurchaseReturnExportModel> models = datas.stream().map(PurchaseReturnExportModel::new)
            .collect(Collectors.toList());
        builder.doWrite(models);

        if (!pageResult.isHasNext()) {
          break;
        }
        pageIndex++;
      }
    } finally {
      builder.finish();
    }
  }

  /**
   * 根据ID查询
   */
  @ApiOperation("根据ID查询")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"purchase:return:query"})
  @GetMapping
  public InvokeResult<GetPurchaseReturnBo> findById(@NotBlank(message = "退单ID不能为空！") String id) {

    PurchaseReturnFullDto data = purchaseReturnService.getDetail(id);

    GetPurchaseReturnBo result = new GetPurchaseReturnBo(data);

    return InvokeResultBuilder.success(result);
  }

  /**
   * 创建
   */
  @ApiOperation("创建")
  @HasPermission({"purchase:return:add"})
  @PostMapping
  public InvokeResult<String> create(@RequestBody @Valid CreatePurchaseReturnVo vo) {

    vo.validate();

    String id = purchaseReturnService.create(vo);

    return InvokeResultBuilder.success(id);
  }

  /**
   * 修改
   */
  @ApiOperation("修改")
  @HasPermission({"purchase:return:modify"})
  @PutMapping
  public InvokeResult<Void> update(@RequestBody @Valid UpdatePurchaseReturnVo vo) {

    vo.validate();

    purchaseReturnService.update(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 审核通过
   */
  @ApiOperation("审核通过")
  @HasPermission({"purchase:return:approve"})
  @PatchMapping("/approve/pass")
  public InvokeResult<Void> approvePass(@RequestBody @Valid ApprovePassPurchaseReturnVo vo) {

    purchaseReturnService.approvePass(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 批量审核通过
   */
  @ApiOperation("批量审核通过")
  @HasPermission({"purchase:return:approve"})
  @PatchMapping("/approve/pass/batch")
  public InvokeResult<Void> batchApprovePass(
      @RequestBody @Valid BatchApprovePassPurchaseReturnVo vo) {

    purchaseReturnService.batchApprovePass(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 直接审核通过
   */
  @ApiOperation("直接审核通过")
  @HasPermission({"purchase:return:approve"})
  @PostMapping("/approve/pass/direct")
  public InvokeResult<Void> directApprovePass(@RequestBody @Valid CreatePurchaseReturnVo vo) {

    purchaseReturnService.directApprovePass(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 审核拒绝
   */
  @ApiOperation("审核拒绝")
  @HasPermission({"purchase:return:approve"})
  @PatchMapping("/approve/refuse")
  public InvokeResult<Void> approveRefuse(@RequestBody @Valid ApproveRefusePurchaseReturnVo vo) {

    purchaseReturnService.approveRefuse(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 批量审核拒绝
   */
  @ApiOperation("批量审核拒绝")
  @HasPermission({"purchase:return:approve"})
  @PatchMapping("/approve/refuse/batch")
  public InvokeResult<Void> batchApproveRefuse(
      @RequestBody @Valid BatchApproveRefusePurchaseReturnVo vo) {

    purchaseReturnService.batchApproveRefuse(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 删除
   */
  @ApiOperation("删除")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"purchase:return:delete"})
  @DeleteMapping
  public InvokeResult<Void> deleteById(@NotBlank(message = "采购退货单ID不能为空！") String id) {

    purchaseReturnService.deleteById(id);

    return InvokeResultBuilder.success();
  }

  /**
   * 批量删除
   */
  @ApiOperation("批量删除")
  @HasPermission({"purchase:return:delete"})
  @DeleteMapping("/batch")
  public InvokeResult<Void> deleteByIds(
      @ApiParam(value = "ID", required = true) @RequestBody @NotEmpty(message = "请选择需要删除的采购退货单！") List<String> ids) {

    purchaseReturnService.deleteByIds(ids);

    return InvokeResultBuilder.success();
  }
}
