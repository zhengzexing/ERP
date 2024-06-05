package com.lframework.xingyun.settle.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.resp.PageResult;
import com.lframework.starter.web.utils.PageResultUtil;
import com.lframework.starter.web.controller.DefaultBaseController;
import com.lframework.starter.web.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.resp.InvokeResult;
import com.lframework.starter.web.resp.InvokeResultBuilder;
import com.lframework.starter.web.utils.ExcelUtil;
import com.lframework.xingyun.settle.bo.item.out.GetSettleOutItemBo;
import com.lframework.xingyun.settle.bo.item.out.QuerySettleOutItemBo;
import com.lframework.xingyun.settle.excel.item.out.SettleOutItemExportModel;
import com.lframework.xingyun.settle.entity.SettleOutItem;
import com.lframework.xingyun.settle.service.SettleOutItemService;
import com.lframework.xingyun.settle.vo.item.out.CreateSettleOutItemVo;
import com.lframework.xingyun.settle.vo.item.out.QuerySettleOutItemVo;
import com.lframework.xingyun.settle.vo.item.out.UpdateSettleOutItemVo;
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
import com.lframework.starter.web.annotations.security.HasPermission;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支出项目
 *
 * @author zzx
 */
@Api(tags = "支出项目")
@Validated
@RestController
@RequestMapping("/settle/item/out")
public class SettleOutItemController extends DefaultBaseController {

    @Autowired
    private SettleOutItemService settleOutItemService;

    /**
     * 支出项目列表
     */
    @ApiOperation("支出项目列表")
    @HasPermission({"settle:out-item:query","settle:out-item:add","settle:out-item:modify"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QuerySettleOutItemBo>> query(@Valid QuerySettleOutItemVo vo) {

        PageResult<SettleOutItem> pageResult = settleOutItemService.query(getPageIndex(vo), getPageSize(vo), vo);

        List<SettleOutItem> datas = pageResult.getDatas();
        List<QuerySettleOutItemBo> results = null;

        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QuerySettleOutItemBo::new).collect(Collectors.toList());
        }

        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 查询支出项目
     */
    @ApiOperation("查询支出项目")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"settle:out-item:query","settle:out-item:add","settle:out-item:modify"})
    @GetMapping
    public InvokeResult<GetSettleOutItemBo> get(@NotBlank(message = "ID不能为空！") String id) {

        SettleOutItem data = settleOutItemService.findById(id);
        if (data == null) {
            throw new DefaultClientException("支出项目不存在！");
        }

        GetSettleOutItemBo result = new GetSettleOutItemBo(data);

        return InvokeResultBuilder.success(result);
    }

    /**
     * 导出支出项目
     */
    @ApiOperation("导出支出项目")
    @HasPermission({"settle:out-item:export"})
    @PostMapping("/export")
    public void export(@Valid QuerySettleOutItemVo vo) {

        ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("支出项目信息",
                SettleOutItemExportModel.class);

        try {
            int pageIndex = 1;
            while (true) {
                PageResult<SettleOutItem> pageResult = settleOutItemService.query(pageIndex, getExportSize(), vo);
                List<SettleOutItem> datas = pageResult.getDatas();
                List<SettleOutItemExportModel> models = datas.stream().map(SettleOutItemExportModel::new)
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
     * 批量停用支出项目
     */
    @ApiOperation("批量停用支出项目")
    @HasPermission({"settle:out-item:modify"})
    @PatchMapping("/unable/batch")
    public InvokeResult<Void> batchUnable(
            @ApiParam(value = "ID", required = true) @NotEmpty(message = "请选择需要停用的支出项目！") @RequestBody List<String> ids) {

        settleOutItemService.batchUnable(ids);

        for (String id : ids) {
            settleOutItemService.cleanCacheByKey(id);
        }

        return InvokeResultBuilder.success();
    }

    /**
     * 批量启用支出项目
     */
    @ApiOperation("批量启用支出项目")
    @HasPermission({"settle:out-item:modify"})
    @PatchMapping("/enable/batch")
    public InvokeResult<Void> batchEnable(
            @ApiParam(value = "ID", required = true) @NotEmpty(message = "请选择需要启用的支出项目！") @RequestBody List<String> ids) {

        settleOutItemService.batchEnable(ids);

        for (String id : ids) {
            settleOutItemService.cleanCacheByKey(id);
        }

        return InvokeResultBuilder.success();
    }

    /**
     * 新增支出项目
     */
    @ApiOperation("新增支出项目")
    @HasPermission({"settle:out-item:add"})
    @PostMapping
    public InvokeResult<Void> create(@Valid CreateSettleOutItemVo vo) {

        settleOutItemService.create(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 修改支出项目
     */
    @ApiOperation("修改支出项目")
    @HasPermission({"settle:out-item:modify"})
    @PutMapping
    public InvokeResult<Void> update(@Valid UpdateSettleOutItemVo vo) {

        settleOutItemService.update(vo);

        settleOutItemService.cleanCacheByKey(vo.getId());

        return InvokeResultBuilder.success();
    }
}
