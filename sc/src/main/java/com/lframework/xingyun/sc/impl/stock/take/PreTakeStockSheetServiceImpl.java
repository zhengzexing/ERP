package com.lframework.xingyun.sc.impl.stock.take;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.ObjectUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.xingyun.template.core.annotations.OpLog;
import com.lframework.xingyun.template.core.components.permission.DataPermissionHandler;
import com.lframework.xingyun.template.core.enums.DefaultOpLogType;
import com.lframework.starter.web.impl.BaseMpServiceImpl;
import com.lframework.starter.web.resp.PageResult;
import com.lframework.xingyun.template.core.utils.OpLogUtil;
import com.lframework.starter.web.utils.PageHelperUtil;
import com.lframework.starter.web.utils.PageResultUtil;
import com.lframework.starter.web.service.GenerateCodeService;
import com.lframework.starter.web.utils.EnumUtil;
import com.lframework.starter.web.utils.IdUtil;
import com.lframework.xingyun.core.components.permission.DataPermissionPool;
import com.lframework.xingyun.sc.vo.stock.take.pre.QueryPreTakeStockProductVo;
import com.lframework.xingyun.sc.components.code.GenerateCodeTypePool;
import com.lframework.xingyun.sc.dto.stock.take.pre.PreTakeStockProductDto;
import com.lframework.xingyun.sc.dto.stock.take.pre.PreTakeStockSheetFullDto;
import com.lframework.xingyun.sc.dto.stock.take.pre.QueryPreTakeStockSheetProductDto;
import com.lframework.xingyun.sc.entity.PreTakeStockSheet;
import com.lframework.xingyun.sc.entity.PreTakeStockSheetDetail;
import com.lframework.xingyun.sc.enums.PreTakeStockSheetStatus;
import com.lframework.xingyun.sc.mappers.PreTakeStockSheetMapper;
import com.lframework.xingyun.sc.service.stock.take.PreTakeStockSheetDetailService;
import com.lframework.xingyun.sc.service.stock.take.PreTakeStockSheetService;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetService;
import com.lframework.xingyun.sc.vo.stock.take.pre.CreatePreTakeStockSheetVo;
import com.lframework.xingyun.sc.vo.stock.take.pre.PreTakeStockProductVo;
import com.lframework.xingyun.sc.vo.stock.take.pre.PreTakeStockSheetSelectorVo;
import com.lframework.xingyun.sc.vo.stock.take.pre.QueryPreTakeStockSheetVo;
import com.lframework.xingyun.sc.vo.stock.take.pre.UpdatePreTakeStockSheetVo;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreTakeStockSheetServiceImpl extends
    BaseMpServiceImpl<PreTakeStockSheetMapper, PreTakeStockSheet>
    implements PreTakeStockSheetService {

  @Autowired
  private PreTakeStockSheetDetailService preTakeStockSheetDetailService;

  @Autowired
  private GenerateCodeService generateCodeService;

  @Autowired
  private TakeStockSheetService takeStockSheetService;

  @Override
  public PageResult<PreTakeStockSheet> query(Integer pageIndex, Integer pageSize,
      QueryPreTakeStockSheetVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<PreTakeStockSheet> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<PreTakeStockSheet> query(QueryPreTakeStockSheetVo vo) {

    return getBaseMapper().query(vo,
        DataPermissionHandler.getDataPermission(DataPermissionPool.ORDER,
            Arrays.asList("order"), Arrays.asList("tb")));
  }

  @Override
  public PageResult<PreTakeStockSheet> selector(Integer pageIndex, Integer pageSize,
      PreTakeStockSheetSelectorVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<PreTakeStockSheet> datas = getBaseMapper().selector(vo,
        DataPermissionHandler.getDataPermission(DataPermissionPool.ORDER,
            Arrays.asList("order"), Arrays.asList("tb")));

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public PreTakeStockSheetFullDto getDetail(String id) {

    PreTakeStockSheetFullDto data = getBaseMapper().getDetail(id);
    if (data == null) {
      throw new DefaultClientException("预先盘点单不存在！");
    }

    return data;
  }

  @Override
  public List<QueryPreTakeStockSheetProductDto> getProducts(String id, String planId) {

    return getBaseMapper().getProducts(id, planId);
  }

  @OpLog(type = DefaultOpLogType.OTHER, name = "新增预先盘点单，ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreatePreTakeStockSheetVo vo) {

    PreTakeStockSheet data = new PreTakeStockSheet();
    data.setId(IdUtil.getId());
    data.setCode(generateCodeService.generate(GenerateCodeTypePool.PRE_TAKE_STOCK_SHEET));
    data.setScId(vo.getScId());
    data.setDescription(
        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());
    data.setTakeStatus(EnumUtil.getByCode(PreTakeStockSheetStatus.class, vo.getTakeStatus()));

    getBaseMapper().insert(data);

    int orderNo = 1;
    for (PreTakeStockProductVo product : vo.getProducts()) {
      PreTakeStockSheetDetail detail = new PreTakeStockSheetDetail();
      detail.setId(IdUtil.getId());
      detail.setSheetId(data.getId());
      detail.setProductId(product.getProductId());
      detail.setFirstNum(product.getFirstNum());
      detail.setSecondNum(product.getSecondNum());
      detail.setRandNum(product.getRandNum());
      detail.setOrderNo(orderNo++);

      preTakeStockSheetDetailService.save(detail);
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);

    return data.getId();
  }

  @OpLog(type = DefaultOpLogType.OTHER, name = "修改预先盘点单，ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(UpdatePreTakeStockSheetVo vo) {

    PreTakeStockSheet data = getBaseMapper().selectById(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("预先盘点单不存在！");
    }

    LambdaUpdateWrapper<PreTakeStockSheet> updateWrapper = Wrappers.lambdaUpdate(
            PreTakeStockSheet.class)
        .set(PreTakeStockSheet::getScId, vo.getScId()).set(PreTakeStockSheet::getDescription,
            StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription())
        .set(PreTakeStockSheet::getTakeStatus,
            EnumUtil.getByCode(PreTakeStockSheetStatus.class, vo.getTakeStatus()))
        .eq(PreTakeStockSheet::getId, vo.getId());

    getBaseMapper().update(updateWrapper);

    Wrapper<PreTakeStockSheetDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            PreTakeStockSheetDetail.class)
        .eq(PreTakeStockSheetDetail::getSheetId, data.getId());
    preTakeStockSheetDetailService.remove(deleteDetailWrapper);

    int orderNo = 1;
    for (PreTakeStockProductVo product : vo.getProducts()) {
      PreTakeStockSheetDetail detail = new PreTakeStockSheetDetail();
      detail.setId(IdUtil.getId());
      detail.setSheetId(data.getId());
      detail.setProductId(product.getProductId());
      detail.setFirstNum(product.getFirstNum());
      detail.setSecondNum(product.getSecondNum());
      detail.setRandNum(product.getRandNum());
      detail.setOrderNo(orderNo++);

      preTakeStockSheetDetailService.save(detail);
    }

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setExtra(vo);
  }

  @OpLog(type = DefaultOpLogType.OTHER, name = "删除预先盘点单，ID：{}", params = {"#id"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteById(String id) {

    if (takeStockSheetService.hasRelatePreTakeStockSheet(id)) {
      throw new DefaultClientException("已有库存盘点单关联此预先盘点单，不允许执行删除操作！");
    }

    getBaseMapper().deleteById(id);

    Wrapper<PreTakeStockSheetDetail> deleteDetailWrapper = Wrappers.lambdaQuery(
            PreTakeStockSheetDetail.class)
        .eq(PreTakeStockSheetDetail::getSheetId, id);
    preTakeStockSheetDetailService.remove(deleteDetailWrapper);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void batchDelete(List<String> ids) {

    for (String id : ids) {
      getThis(this.getClass()).deleteById(id);
    }
  }

  @Override
  public PageResult<PreTakeStockProductDto> queryPreTakeStockByCondition(Integer pageIndex,
      Integer pageSize, String condition) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);

    List<PreTakeStockProductDto> datas = getBaseMapper().queryPreTakeStockByCondition(condition,
        DataPermissionHandler.getDataPermission(
            DataPermissionPool.PRODUCT,
            Arrays.asList("product", "brand", "category"),
            Arrays.asList("g", "b", "c")));
    PageResult<PreTakeStockProductDto> pageResult = PageResultUtil.convert(new PageInfo<>(datas));

    return pageResult;
  }

  @Override
  public PageResult<PreTakeStockProductDto> queryPreTakeStockList(Integer pageIndex,
      Integer pageSize, QueryPreTakeStockProductVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);

    List<PreTakeStockProductDto> datas = getBaseMapper().queryPreTakeStockList(vo,
        DataPermissionHandler.getDataPermission(
            DataPermissionPool.PRODUCT,
            Arrays.asList("product", "brand", "category"),
            Arrays.asList("g", "b", "c")));
    PageResult<PreTakeStockProductDto> pageResult = PageResultUtil.convert(new PageInfo<>(datas));

    return pageResult;
  }

  @Override
  public void cleanCacheByKey(Serializable key) {

  }
}
