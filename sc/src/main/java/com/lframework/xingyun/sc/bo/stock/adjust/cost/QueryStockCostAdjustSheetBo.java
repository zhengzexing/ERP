package com.lframework.xingyun.sc.bo.stock.adjust.cost;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.xingyun.template.core.service.UserService;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.sc.entity.StockCostAdjustSheet;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 库存成本调整单 QueryBo
 * </p>
 *
 * @author zzx
 */
@Data
public class QueryStockCostAdjustSheetBo extends BaseBo<StockCostAdjustSheet> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 业务单据号
     */
    @ApiModelProperty("业务单据号")
    private String code;

    /**
     * 仓库编号
     */
    @ApiModelProperty("仓库编号")
    private String scCode;

    /**
     * 仓库名称
     */
    @ApiModelProperty("仓库名称")
    private String scName;

    /**
     * 调价品种数
     */
    @ApiModelProperty("调价品种数")
    private Integer productNum;

    /**
     * 库存调价差额
     */
    @ApiModelProperty("库存调价差额")
    private BigDecimal diffAmount;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 审核人
     */
    @ApiModelProperty("审核人")
    private String approveBy;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime approveTime;

    public QueryStockCostAdjustSheetBo() {

    }

    public QueryStockCostAdjustSheetBo(StockCostAdjustSheet dto) {

        super(dto);
    }

    @Override
    public BaseBo<StockCostAdjustSheet> convert(StockCostAdjustSheet dto) {

        return super.convert(dto, QueryStockCostAdjustSheetBo::getStatus);
    }

    @Override
    protected void afterInit(StockCostAdjustSheet dto) {

        this.status = dto.getStatus().getCode();

        StoreCenterService storeCenterService = ApplicationUtil.getBean(StoreCenterService.class);
        StoreCenter sc = storeCenterService.findById(dto.getScId());
        this.scCode = sc.getCode();
        this.scName = sc.getName();

        UserService userService = ApplicationUtil.getBean(UserService.class);
        if (!StringUtil.isBlank(dto.getApproveBy())) {
            this.approveBy = userService.findById(dto.getApproveBy()).getName();
        }
    }
}
