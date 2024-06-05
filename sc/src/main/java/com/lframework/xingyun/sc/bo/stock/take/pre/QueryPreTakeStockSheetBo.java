package com.lframework.xingyun.sc.bo.stock.take.pre;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.sc.entity.PreTakeStockSheet;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * <p>
 * 预先盘点单 QueryBo
 * </p>
 *
 * @author zzx
 */
@Data
public class QueryPreTakeStockSheetBo extends BaseBo<PreTakeStockSheet> {

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
     * 盘点状态
     */
    @ApiModelProperty("盘点状态")
    private Integer takeStatus;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    public QueryPreTakeStockSheetBo() {

    }

    public QueryPreTakeStockSheetBo(PreTakeStockSheet dto) {

        super(dto);
    }

    @Override
    public BaseBo<PreTakeStockSheet> convert(PreTakeStockSheet dto) {

        return super.convert(dto, QueryPreTakeStockSheetBo::getTakeStatus);
    }

    @Override
    protected void afterInit(PreTakeStockSheet dto) {

        this.takeStatus = dto.getTakeStatus().getCode();

        StoreCenterService storeCenterService = ApplicationUtil.getBean(StoreCenterService.class);
        StoreCenter sc = storeCenterService.findById(dto.getScId());
        this.scCode = sc.getCode();
        this.scName = sc.getName();
    }
}
