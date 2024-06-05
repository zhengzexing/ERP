package com.lframework.xingyun.settle.bo.check.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.DateUtil;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.xingyun.template.core.service.UserService;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Customer;
import com.lframework.xingyun.basedata.service.customer.CustomerService;
import com.lframework.xingyun.settle.dto.check.customer.CustomerSettleCheckBizItemDto;
import com.lframework.xingyun.settle.dto.check.customer.CustomerSettleCheckSheetFullDto;
import com.lframework.xingyun.settle.service.CustomerSettleCheckSheetService;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class GetCustomerSettleCheckSheetBo extends BaseBo<CustomerSettleCheckSheetFullDto> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 单号
     */
    @ApiModelProperty("单号")
    private String code;

    /**
     * 客户ID
     */
    @ApiModelProperty("客户ID")
    private String customerId;

    /**
     * 客户编号
     */
    @ApiModelProperty("客户编号")
    private String customerCode;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String customerName;

    /**
     * 总金额
     */
    @ApiModelProperty("总金额")
    private BigDecimal totalAmount;

    /**
     * 应付金额
     */
    @ApiModelProperty("应付金额")
    private BigDecimal totalPayAmount;

    /**
     * 已付金额
     */
    @ApiModelProperty("已付金额")
    private BigDecimal totalPayedAmount;

    /**
     * 优惠金额
     */
    @ApiModelProperty("优惠金额")
    private BigDecimal totalDiscountAmount;

    /**
     * 未付金额
     */
    @ApiModelProperty("未付金额")
    private BigDecimal totalUnPayAmount;

    /**
     * 起始时间
     */
    @ApiModelProperty("起始时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    @ApiModelProperty("截止时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime endTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 创建人ID
     */
    @ApiModelProperty("创建人ID")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime createTime;

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

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 拒绝原因
     */
    @ApiModelProperty("拒绝原因")
    private String refuseReason;

    private List<SheetDetailBo> details;

    public GetCustomerSettleCheckSheetBo() {

    }

    public GetCustomerSettleCheckSheetBo(CustomerSettleCheckSheetFullDto dto) {

        super(dto);
    }

    @Override
    public BaseBo<CustomerSettleCheckSheetFullDto> convert(CustomerSettleCheckSheetFullDto dto) {

        return super.convert(dto, GetCustomerSettleCheckSheetBo::getStatus,
            GetCustomerSettleCheckSheetBo::getDetails);
    }

    @Override
    protected void afterInit(CustomerSettleCheckSheetFullDto dto) {

        CustomerService customerService = ApplicationUtil.getBean(CustomerService.class);
        Customer customer = customerService.findById(dto.getCustomerId());
        this.customerCode = customer.getCode();
        this.customerName = customer.getName();

        this.status = dto.getStatus().getCode();

        this.totalUnPayAmount = NumberUtil.sub(dto.getTotalPayAmount(), dto.getTotalPayedAmount(),
            dto.getTotalDiscountAmount());

        UserService userService = ApplicationUtil.getBean(UserService.class);

        if (!StringUtil.isBlank(dto.getApproveBy())) {
            this.approveBy = userService.findById(dto.getApproveBy()).getName();
        }

        this.startTime = DateUtil.toLocalDateTime(dto.getStartDate());
        this.endTime = DateUtil.toLocalDateTimeMax(dto.getEndDate());

        if (!CollectionUtil.isEmpty(dto.getDetails())) {
            this.details = dto.getDetails().stream().map(SheetDetailBo::new)
                .collect(Collectors.toList());
        }
    }

    @Data
    public static class SheetDetailBo extends
        BaseBo<CustomerSettleCheckSheetFullDto.SheetDetailDto> {

        /**
         * 明细ID
         */
        @ApiModelProperty("明细ID")
        private String id;

        /**
         * 单据ID
         */
        @ApiModelProperty("单据ID")
        private String bizId;

        /**
         * 单据号
         */
        @ApiModelProperty("单据号")
        private String bizCode;

        /**
         * 业务类型
         */
        @ApiModelProperty("业务类型")
        private Integer bizType;

        /**
         * 计算类型
         */
        @ApiModelProperty("计算类型")
        private Integer calcType;

        /**
         * 审核时间
         */
        @ApiModelProperty("审核时间")
        @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
        private LocalDateTime approveTime;

        /**
         * 单据金额
         */
        @ApiModelProperty("单据金额")
        private BigDecimal totalAmount;

        /**
         * 应付金额
         */
        @ApiModelProperty("应付金额")
        private BigDecimal payAmount;

        /**
         * 单据备注
         */
        @ApiModelProperty("单据备注")
        private String description;

        public SheetDetailBo() {

        }

        public SheetDetailBo(CustomerSettleCheckSheetFullDto.SheetDetailDto dto) {

            super(dto);
        }

        @Override
        public <A> BaseBo<CustomerSettleCheckSheetFullDto.SheetDetailDto> convert(
            CustomerSettleCheckSheetFullDto.SheetDetailDto dto) {

            return super.convert(dto, CustomerSettleCheckSheetFullDto.SheetDetailDto::getBizType,
                CustomerSettleCheckSheetFullDto.SheetDetailDto::getCalcType);
        }

        @Override
        protected void afterInit(CustomerSettleCheckSheetFullDto.SheetDetailDto dto) {

            CustomerSettleCheckSheetService settleCheckSheetService = ApplicationUtil.getBean(
                CustomerSettleCheckSheetService.class);
            CustomerSettleCheckBizItemDto item = settleCheckSheetService.getBizItem(dto.getBizId(),
                dto.getBizType());
            this.bizCode = item.getCode();
            this.approveTime = item.getApproveTime();
            this.bizType = dto.getBizType().getCode();
            this.totalAmount = item.getTotalAmount();
            this.calcType = dto.getCalcType().getCode();
        }
    }
}
