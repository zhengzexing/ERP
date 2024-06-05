package com.lframework.xingyun.basedata.excel.product.category;

import com.alibaba.excel.context.AnalysisContext;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.constants.PatternPool;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.RegUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.components.excel.ExcelImportListener;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.starter.web.utils.IdUtil;
import com.lframework.xingyun.basedata.entity.ProductCategory;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductCategoryImportListener extends ExcelImportListener<ProductCategoryImportModel> {

  @Override
  protected void doInvoke(ProductCategoryImportModel data, AnalysisContext context) {
    if (StringUtil.isBlank(data.getCode())) {
      throw new DefaultClientException("第" + context.readRowHolder().getRowIndex() + "行“编号”不能为空");
    }
    if (!RegUtil.isMatch(PatternPool.PATTERN_CODE, data.getCode())) {
      throw new DefaultClientException(
          "第" + context.readRowHolder().getRowIndex() + "行“编号”必须由字母、数字、“-_.”组成，长度不能超过20位");
    }
    if (StringUtil.isBlank(data.getName())) {
      throw new DefaultClientException("第" + context.readRowHolder().getRowIndex() + "行“名称”不能为空");
    }
    ProductCategoryService productCategoryService = ApplicationUtil.getBean(
        ProductCategoryService.class);
    if (!StringUtil.isBlank(data.getParentCode())) {
      Wrapper<ProductCategory> queryParentWrapper = Wrappers.lambdaQuery(ProductCategory.class)
          .eq(ProductCategory::getCode, data.getParentCode());
      ProductCategory parent = productCategoryService.getOne(queryParentWrapper);
      if (parent == null) {
        // 检查是不是新导入的
        if (this.datas.stream().noneMatch(t -> t.getCode().equals(data.getParentCode()))) {
          throw new DefaultClientException(
              "第" + context.readRowHolder().getRowIndex() + "行“上级类目编号”不存在");
        }
      }

      // 不允许改变上级类目
      Wrapper<ProductCategory> queryWrapper = Wrappers.lambdaQuery(ProductCategory.class)
          .eq(ProductCategory::getCode, data.getCode());
      ProductCategory productCategory = productCategoryService.getOne(queryWrapper);
      if (productCategory != null) {
        ProductCategory parentCategory = StringUtil.isBlank(productCategory.getParentId()) ? null
            : productCategoryService.getById(productCategory.getParentId());
        if (parentCategory == null || !parentCategory.getCode().equals(data.getParentCode())) {
          throw new DefaultClientException(
              "第" + context.readRowHolder().getRowIndex() + "行“上级类目编号”有误，不允许修改类目的归属关系");
        }
      }
    }
  }

  @Override
  protected void afterAllAnalysed(AnalysisContext context) {

    ProductCategoryService productCategoryService = ApplicationUtil.getBean(
        ProductCategoryService.class);

    List<ProductCategoryImportModel> datas = this.getDatas();
    for (int i = 0; i < datas.size(); i++) {
      ProductCategoryImportModel data = datas.get(i);
      Wrapper<ProductCategory> checkNameWrapper = Wrappers.lambdaQuery(ProductCategory.class)
          .eq(ProductCategory::getName, data.getName())
          .ne(ProductCategory::getCode, data.getCode());
      if (productCategoryService.count(checkNameWrapper) > 0) {
        throw new DefaultClientException(
            "第" + (i + 1) + "行“名称”重复，请重新输入");
      }

      boolean isInsert = false;
      Wrapper<ProductCategory> queryWrapper = Wrappers.lambdaQuery(ProductCategory.class)
          .eq(ProductCategory::getCode, data.getCode());
      ProductCategory record = productCategoryService.getOne(queryWrapper);
      if (record == null) {
        record = new ProductCategory();

        record.setId(IdUtil.getId());
        isInsert = true;
      }

      record.setCode(data.getCode());
      record.setName(data.getName());
      if (!StringUtil.isBlank(data.getParentCode())) {
        Wrapper<ProductCategory> queryParentWrapper = Wrappers.lambdaQuery(ProductCategory.class)
            .eq(ProductCategory::getCode, data.getParentCode());
        ProductCategory parent = productCategoryService.getOne(queryParentWrapper);
        record.setParentId(parent.getId());
      }
      record.setDescription(data.getDescription());

      if (isInsert) {
        //这里要与上级类目的状态保持一致
        Boolean available = Boolean.TRUE;
        if (StringUtil.isNotBlank(record.getParentId())) {
          ProductCategory parentCategory = productCategoryService.findById(record.getParentId());
          available = parentCategory.getAvailable();
        }
        record.setAvailable(available);
      }

      productCategoryService.saveOrUpdateAllColumn(record);
      productCategoryService.saveRecursion(record.getId(), record.getParentId());
      data.setId(record.getId());

      this.setSuccessProcess(i);
    }
  }

  @Override
  protected void doComplete() {
    ProductCategoryService productCategoryService = ApplicationUtil.getBean(
        ProductCategoryService.class);
    for (ProductCategoryImportModel data : this.getDatas()) {
      productCategoryService.cleanCacheByKey(data.getId());
    }
  }
}
