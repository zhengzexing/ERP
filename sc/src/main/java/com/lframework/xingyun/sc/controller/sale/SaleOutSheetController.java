package com.lframework.xingyun.sc.controller.sale;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
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
import com.lframework.xingyun.sc.bo.purchase.receive.GetPaymentDateBo;
import com.lframework.xingyun.sc.bo.sale.out.GetSaleOutSheetBo;
import com.lframework.xingyun.sc.bo.sale.out.PrintSaleOutSheetBo;
import com.lframework.xingyun.sc.bo.sale.out.QuerySaleOutSheetBo;
import com.lframework.xingyun.sc.bo.sale.out.QuerySaleOutSheetWithReturnBo;
import com.lframework.xingyun.sc.bo.sale.out.SaleOutSheetWithReturnBo;
import com.lframework.xingyun.sc.dto.purchase.receive.GetPaymentDateDto;
import com.lframework.xingyun.sc.dto.sale.out.SaleOutSheetFullDto;
import com.lframework.xingyun.sc.dto.sale.out.SaleOutSheetWithReturnDto;
import com.lframework.xingyun.sc.entity.SaleOutSheet;
import com.lframework.xingyun.sc.excel.sale.out.SaleOutSheetExportModel;
import com.lframework.xingyun.sc.service.sale.SaleOutSheetService;
import com.lframework.xingyun.sc.vo.sale.out.ApprovePassSaleOutSheetVo;
import com.lframework.xingyun.sc.vo.sale.out.ApproveRefuseSaleOutSheetVo;
import com.lframework.xingyun.sc.vo.sale.out.BatchApprovePassSaleOutSheetVo;
import com.lframework.xingyun.sc.vo.sale.out.BatchApproveRefuseSaleOutSheetVo;
import com.lframework.xingyun.sc.vo.sale.out.CreateSaleOutSheetVo;
import com.lframework.xingyun.sc.vo.sale.out.QuerySaleOutSheetVo;
import com.lframework.xingyun.sc.vo.sale.out.QuerySaleOutSheetWithReturnVo;
import com.lframework.xingyun.sc.vo.sale.out.UpdateSaleOutSheetVo;
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
 * 销售出库单管理
 *
 * @author zzx
 */
@Api(tags = "销售出库单管理")
@Validated
@RestController
@RequestMapping("/sale/out/sheet")
public class SaleOutSheetController extends DefaultBaseController {

  @Autowired
  private SaleOutSheetService saleOutSheetService;

  /**
   * 打印
   */
  @ApiOperation("打印")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"sale:out:query"})
  @GetMapping("/print")
  public InvokeResult<A4ExcelPortraitPrintBo<PrintSaleOutSheetBo>> print(
      @NotBlank(message = "订单ID不能为空！") String id) {

    SaleOutSheetFullDto data = saleOutSheetService.getDetail(id);
    if (data == null) {
      throw new DefaultClientException("销售出库单不存在！");
    }

    PrintSaleOutSheetBo result = new PrintSaleOutSheetBo(data);

    A4ExcelPortraitPrintBo<PrintSaleOutSheetBo> printResult = new A4ExcelPortraitPrintBo<>(
        "print/sale-out-sheet.ftl", result);

    return InvokeResultBuilder.success(printResult);
  }

  /**
   * 订单列表
   */
  @ApiOperation("订单列表")
  @HasPermission({"sale:out:query"})
  @GetMapping("/query")
  public InvokeResult<PageResult<QuerySaleOutSheetBo>> query(@Valid QuerySaleOutSheetVo vo) {

    PageResult<SaleOutSheet> pageResult = saleOutSheetService.query(getPageIndex(vo),
        getPageSize(vo), vo);

    List<SaleOutSheet> datas = pageResult.getDatas();
    List<QuerySaleOutSheetBo> results = null;

    if (!CollectionUtil.isEmpty(datas)) {
      results = datas.stream().map(QuerySaleOutSheetBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }

  /**
   * 导出
   */
  @ApiOperation("导出")
  @HasPermission({"sale:out:export"})
  @PostMapping("/export")
  public void export(@Valid QuerySaleOutSheetVo vo) {

    ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("销售出库单信息",
        SaleOutSheetExportModel.class);

    try {
      int pageIndex = 1;
      while (true) {
        PageResult<SaleOutSheet> pageResult = saleOutSheetService.query(pageIndex, getExportSize(),
            vo);
        List<SaleOutSheet> datas = pageResult.getDatas();
        List<SaleOutSheetExportModel> models = datas.stream().map(SaleOutSheetExportModel::new)
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
  @HasPermission({"sale:out:query"})
  @GetMapping
  public InvokeResult<GetSaleOutSheetBo> findById(
      @NotBlank(message = "订单ID不能为空！") String id) {

    SaleOutSheetFullDto data = saleOutSheetService.getDetail(id);

    GetSaleOutSheetBo result = new GetSaleOutSheetBo(data);

    return InvokeResultBuilder.success(result);
  }

  /**
   * 根据客户ID查询默认付款日期
   */
  @ApiOperation("根据客户ID查询默认付款日期")
  @ApiImplicitParam(value = "客户ID", name = "customerId", paramType = "query", required = true)
  @HasPermission({"sale:out:add", "sale:out:modify"})
  @GetMapping("/paymentdate")
  public InvokeResult<GetPaymentDateBo> getPaymentDate(
      @NotBlank(message = "客户ID不能为空！") String customerId) {

    GetPaymentDateDto data = saleOutSheetService.getPaymentDate(customerId);

    GetPaymentDateBo result = new GetPaymentDateBo(data);

    return InvokeResultBuilder.success(result);
  }

  /**
   * 根据ID查询（销售退货业务）
   */
  @ApiOperation("根据ID查询（销售退货业务）")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"sale:return:add", "sale:return:modify"})
  @GetMapping("/return")
  public InvokeResult<SaleOutSheetWithReturnBo> getWithReturn(
      @NotBlank(message = "出库单ID不能为空！") String id) {

    SaleOutSheetWithReturnDto data = saleOutSheetService.getWithReturn(id);
    SaleOutSheetWithReturnBo result = new SaleOutSheetWithReturnBo(data);

    return InvokeResultBuilder.success(result);
  }

  /**
   * 查询列表（销售退货业务）
   */
  @ApiOperation("查询列表（销售退货业务）")
  @HasPermission({"sale:return:add", "sale:return:modify"})
  @GetMapping("/query/return")
  public InvokeResult<PageResult<QuerySaleOutSheetWithReturnBo>> queryWithReturn(
      @Valid QuerySaleOutSheetWithReturnVo vo) {

    PageResult<SaleOutSheet> pageResult = saleOutSheetService.queryWithReturn(getPageIndex(vo),
        getPageSize(vo),
        vo);
    List<SaleOutSheet> datas = pageResult.getDatas();

    List<QuerySaleOutSheetWithReturnBo> results = null;

    if (!CollectionUtil.isEmpty(datas)) {
      results = datas.stream().map(QuerySaleOutSheetWithReturnBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }

  /**
   * 加载列表（销售退货业务）
   */
  @ApiOperation("加载列表（销售退货业务）")
  @HasPermission({"sale:return:add", "sale:return:modify"})
  @PostMapping("/query/return/load")
  public InvokeResult<List<QuerySaleOutSheetWithReturnBo>> loadWithReturn(
      @RequestBody(required = false) List<String> ids) {

    List<SaleOutSheet> datas = saleOutSheetService.listByIds(ids);

    List<QuerySaleOutSheetWithReturnBo> results = datas.stream()
        .map(QuerySaleOutSheetWithReturnBo::new)
        .collect(Collectors.toList());

    return InvokeResultBuilder.success(results);
  }

  /**
   * 创建
   */
  @ApiOperation("创建")
  @HasPermission({"sale:out:add"})
  @PostMapping
  public InvokeResult<String> create(@RequestBody @Valid CreateSaleOutSheetVo vo) {

    vo.validate();

    String id = saleOutSheetService.create(vo);

    return InvokeResultBuilder.success(id);
  }

  /**
   * 修改
   */
  @ApiOperation("修改")
  @HasPermission({"sale:out:modify"})
  @PutMapping
  public InvokeResult<Void> update(@RequestBody @Valid UpdateSaleOutSheetVo vo) {

    vo.validate();

    saleOutSheetService.update(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 审核通过
   */
  @ApiOperation("审核通过")
  @HasPermission({"sale:out:approve"})
  @PatchMapping("/approve/pass")
  public InvokeResult<Void> approvePass(@RequestBody @Valid ApprovePassSaleOutSheetVo vo) {

    saleOutSheetService.approvePass(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 批量审核通过
   */
  @ApiOperation("批量审核通过")
  @HasPermission({"sale:out:approve"})
  @PatchMapping("/approve/pass/batch")
  public InvokeResult<Void> batchApprovePass(
      @RequestBody @Valid BatchApprovePassSaleOutSheetVo vo) {

    saleOutSheetService.batchApprovePass(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 直接审核通过
   */
  @ApiOperation("直接审核通过")
  @HasPermission({"sale:out:approve"})
  @PostMapping("/approve/pass/direct")
  public InvokeResult<Void> directApprovePass(@RequestBody @Valid CreateSaleOutSheetVo vo) {

    saleOutSheetService.directApprovePass(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 审核拒绝
   */
  @ApiOperation("审核拒绝")
  @HasPermission({"sale:out:approve"})
  @PatchMapping("/approve/refuse")
  public InvokeResult<Void> approveRefuse(@RequestBody @Valid ApproveRefuseSaleOutSheetVo vo) {

    saleOutSheetService.approveRefuse(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 批量审核拒绝
   */
  @ApiOperation("批量审核拒绝")
  @HasPermission({"sale:out:approve"})
  @PatchMapping("/approve/refuse/batch")
  public InvokeResult<Void> batchApproveRefuse(
      @RequestBody @Valid BatchApproveRefuseSaleOutSheetVo vo) {

    saleOutSheetService.batchApproveRefuse(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 删除
   */
  @ApiOperation("删除")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"sale:out:delete"})
  @DeleteMapping
  public InvokeResult<Void> deleteById(@NotBlank(message = "销售出库单ID不能为空！") String id) {

    saleOutSheetService.deleteById(id);

    return InvokeResultBuilder.success();
  }

  /**
   * 批量删除
   */
  @ApiOperation("批量删除")
  @HasPermission({"sale:out:delete"})
  @DeleteMapping("/batch")
  public InvokeResult<Void> deleteByIds(
      @ApiParam(value = "ID", required = true) @RequestBody @NotEmpty(message = "请选择需要删除的销售出库单！") List<String> ids) {

    saleOutSheetService.deleteByIds(ids);

    return InvokeResultBuilder.success();
  }
}
