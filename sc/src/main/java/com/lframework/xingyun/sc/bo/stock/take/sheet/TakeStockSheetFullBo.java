package com.lframework.xingyun.sc.bo.stock.take.sheet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.xingyun.template.core.service.UserService;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.ProductBrand;
import com.lframework.xingyun.basedata.entity.ProductCategory;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.service.product.ProductBrandService;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.sc.dto.stock.take.plan.GetTakeStockPlanDetailProductDto;
import com.lframework.xingyun.sc.dto.stock.take.sheet.TakeStockSheetFullDto;
import com.lframework.xingyun.sc.entity.PreTakeStockSheet;
import com.lframework.xingyun.sc.entity.TakeStockConfig;
import com.lframework.xingyun.sc.entity.TakeStockPlan;
import com.lframework.xingyun.sc.enums.TakeStockPlanType;
import com.lframework.xingyun.sc.service.stock.take.PreTakeStockSheetService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockConfigService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockPlanDetailService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockPlanService;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * <p>
 * 盘点单详情 Bo
 * </p>
 *
 * @author zzx
 */
@Data
public class TakeStockSheetFullBo extends BaseBo<TakeStockSheetFullDto> {

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
   * 盘点任务ID
   */
  @ApiModelProperty("盘点任务ID")
  private String planId;

  /**
   * 盘点任务号
   */
  @ApiModelProperty("盘点任务号")
  private String planCode;

  /**
   * 预先盘点单ID
   */
  @ApiModelProperty("预先盘点单ID")
  private String preSheetId;

  /**
   * 预先盘点单号
   */
  @ApiModelProperty("预先盘点单号")
  private String preSheetCode;

  /**
   * 仓库名称
   */
  @ApiModelProperty("仓库名称")
  private String scName;

  /**
   * 盘点任务-盘点类别
   */
  @ApiModelProperty("盘点任务-盘点类别")
  private Integer takeType;

  /**
   * 业务名称
   */
  @ApiModelProperty("业务名称")
  private String bizName;

  /**
   * 盘点任务-盘点状态
   */
  @ApiModelProperty("盘点任务-盘点状态")
  private Integer takeStatus;

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
   * 拒绝理由
   */
  @ApiModelProperty("拒绝理由")
  private String refuseReason;

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

  /**
   * 明细
   */
  @ApiModelProperty("明细")
  private List<SheetDetailBo> details;

  public TakeStockSheetFullBo(TakeStockSheetFullDto dto) {

    super(dto);
  }

  @Override
  public <A> BaseBo<TakeStockSheetFullDto> convert(TakeStockSheetFullDto dto) {

    return super.convert(dto, TakeStockSheetFullBo::getTakeStatus, TakeStockSheetFullBo::getStatus);
  }

  @Override
  protected void afterInit(TakeStockSheetFullDto dto) {

    this.status = dto.getStatus().getCode();

    TakeStockPlanService takeStockPlanService = ApplicationUtil.getBean(TakeStockPlanService.class);
    TakeStockPlan plan = takeStockPlanService.getById(dto.getPlanId());
    this.planCode = plan.getCode();
    this.takeType = plan.getTakeType().getCode();
    this.takeStatus = plan.getTakeStatus().getCode();

    String bizId = plan.getBizId();
    if (plan.getTakeType() == TakeStockPlanType.CATEGORY) {
      ProductCategoryService productCategoryService = ApplicationUtil.getBean(
          ProductCategoryService.class);
      String[] categoryIds = bizId.split(",");
      StringBuilder builder = new StringBuilder();
      for (String categoryId : categoryIds) {
        ProductCategory productCategory = productCategoryService.findById(categoryId);
        builder.append(productCategory.getName()).append(StringPool.STR_SPLIT_CN);
      }

      if (builder.length() > 0) {
        builder.setLength(builder.length() - 1);
      }

      this.bizName = builder.toString();
    } else if (plan.getTakeType() == TakeStockPlanType.BRAND) {
      ProductBrandService productBrandService = ApplicationUtil.getBean(ProductBrandService.class);
      String[] brandIds = bizId.split(",");
      StringBuilder builder = new StringBuilder();
      for (String brandId : brandIds) {
        ProductBrand productBrand = productBrandService.findById(brandId);
        builder.append(productBrand.getName()).append(StringPool.STR_SPLIT_CN);
      }

      if (builder.length() > 0) {
        builder.setLength(builder.length() - 1);
      }

      this.bizName = builder.toString();
    }

    if (!StringUtil.isBlank(dto.getPreSheetId())) {
      PreTakeStockSheetService preTakeStockSheetService = ApplicationUtil.getBean(
          PreTakeStockSheetService.class);
      PreTakeStockSheet preSheet = preTakeStockSheetService.getById(dto.getPreSheetId());
      this.preSheetCode = preSheet.getCode();
    }

    StoreCenterService storeCenterService = ApplicationUtil.getBean(StoreCenterService.class);
    StoreCenter sc = storeCenterService.findById(dto.getScId());
    this.scName = sc.getName();

    UserService userService = ApplicationUtil.getBean(UserService.class);
    if (!StringUtil.isBlank(this.approveBy)) {
      this.approveBy = userService.findById(this.approveBy).getName();
    }

    this.details = dto.getDetails().stream().map(t -> new SheetDetailBo(t, this.planId))
        .collect(Collectors.toList());
  }

  @Data
  public static class SheetDetailBo extends BaseBo<TakeStockSheetFullDto.SheetDetailDto> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String productId;

    /**
     * 编号
     */
    @ApiModelProperty("编号")
    private String productCode;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String productName;

    /**
     * 类目名称
     */
    @ApiModelProperty("类目名称")
    private String categoryName;

    /**
     * 品牌名称
     */
    @ApiModelProperty("品牌名称")
    private String brandName;

    /**
     * SKU
     */
    @ApiModelProperty("SKU")
    private String skuCode;

    /**
     * 外部编号
     */
    @ApiModelProperty("外部编号")
    private String externalCode;

    /**
     * 规格
     */
    @ApiModelProperty("规格")
    private String spec;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String unit;

    /**
     * 库存数量
     */
    @ApiModelProperty("库存数量")
    private Integer stockNum;

    /**
     * 盘点数量
     */
    @ApiModelProperty("盘点数量")
    private Integer takeNum;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 盘点任务ID
     */
    @ApiModelProperty("盘点任务ID")
    private String planId;

    public SheetDetailBo(TakeStockSheetFullDto.SheetDetailDto dto, String planId) {

      this.planId = planId;

      this.init(dto);
    }

    @Override
    protected void afterInit(TakeStockSheetFullDto.SheetDetailDto dto) {

      ProductService productService = ApplicationUtil.getBean(ProductService.class);
      Product product = productService.findById(dto.getProductId());

      ProductCategoryService productCategoryService = ApplicationUtil.getBean(
          ProductCategoryService.class);
      ProductCategory productCategory = productCategoryService.findById(product.getCategoryId());

      ProductBrandService productBrandService = ApplicationUtil.getBean(ProductBrandService.class);
      ProductBrand productBrand = productBrandService.findById(product.getBrandId());

      this.productId = product.getId();
      this.productCode = product.getCode();
      this.productName = product.getName();
      this.categoryName = productCategory.getName();
      this.brandName = productBrand.getName();

      this.skuCode = product.getSkuCode();
      this.externalCode = product.getExternalCode();
      this.spec = product.getSpec();
      this.unit = product.getUnit();

      TakeStockConfigService takeStockConfigService = ApplicationUtil.getBean(
          TakeStockConfigService.class);
      TakeStockConfig config = takeStockConfigService.get();
      if (config.getShowStock()) {
        TakeStockPlanDetailService takeStockPlanDetailService = ApplicationUtil.getBean(
            TakeStockPlanDetailService.class);
        GetTakeStockPlanDetailProductDto planDetail = takeStockPlanDetailService.getByPlanIdAndProductId(
            this.planId, this.productId);
        this.stockNum = planDetail.getStockNum();
      }
    }
  }
}
