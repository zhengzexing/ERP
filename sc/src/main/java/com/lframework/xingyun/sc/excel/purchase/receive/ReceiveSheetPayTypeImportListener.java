package com.lframework.xingyun.sc.excel.purchase.receive;

import com.alibaba.excel.context.AnalysisContext;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.NumberUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.components.excel.ExcelImportListener;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.PayType;
import com.lframework.xingyun.basedata.service.paytype.PayTypeService;
import com.lframework.xingyun.sc.entity.ReceiveSheet;
import com.lframework.xingyun.sc.enums.ReceiveSheetStatus;
import com.lframework.xingyun.sc.service.paytype.OrderPayTypeService;
import com.lframework.xingyun.sc.service.purchase.ReceiveSheetService;
import com.lframework.xingyun.sc.vo.paytype.OrderPayTypeVo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReceiveSheetPayTypeImportListener extends
    ExcelImportListener<ReceiveSheetPayTypeImportModel> {

  @Override
  protected void doInvoke(ReceiveSheetPayTypeImportModel data, AnalysisContext context) {
    if (StringUtil.isBlank(data.getCode())) {
      throw new DefaultClientException(
          "第" + context.readRowHolder().getRowIndex() + "行“单据号”不能为空");
    }
    ReceiveSheetService receiveSheetService = ApplicationUtil.getBean(ReceiveSheetService.class);
    Wrapper<ReceiveSheet> queryWrapper = Wrappers.lambdaQuery(ReceiveSheet.class)
        .eq(ReceiveSheet::getCode, data.getCode());
    ReceiveSheet order = receiveSheetService.getOne(queryWrapper);
    if (order == null) {
      throw new DefaultClientException(
          "第" + context.readRowHolder().getRowIndex() + "行“单据号”不存在");
    }
    if (order.getStatus() == ReceiveSheetStatus.APPROVE_PASS) {
      throw new DefaultClientException("第" + context.readRowHolder().getRowIndex() + "行“单据号”的状态为“"
          + ReceiveSheetStatus.APPROVE_PASS.getDesc() + "”，不允许导入支付方式");
    }
    data.setId(order.getId());
    data.setTotalAmount(order.getTotalAmount());

    if (StringUtil.isBlank(data.getPayTypeCode())) {
      throw new DefaultClientException(
          "第" + context.readRowHolder().getRowIndex() + "行“支付方式编号”不能为空");
    }

    PayTypeService payTypeService = ApplicationUtil.getBean(PayTypeService.class);
    Wrapper<PayType> queryPayTypeWrapper = Wrappers.lambdaQuery(PayType.class)
        .eq(PayType::getCode, data.getPayTypeCode());
    PayType payType = payTypeService.getOne(queryPayTypeWrapper);
    if (payType == null) {
      throw new DefaultClientException(
          "第" + context.readRowHolder().getRowIndex() + "行“支付方式编号”不存在");
    }

    data.setPayTypeId(payType.getId());

    if (data.getPayAmount() == null) {
      throw new DefaultClientException(
          "第" + context.readRowHolder().getRowIndex() + "行“支付金额”不能为空");
    }

    if (NumberUtil.le(data.getPayAmount(), BigDecimal.ZERO)) {
      // 如果 <= 0
      throw new DefaultClientException(
          "第" + context.readRowHolder().getRowIndex() + "行“支付金额”必须大于0");
    }

    if (!NumberUtil.isNumberPrecision(data.getPayAmount(), 2)) {
      throw new DefaultClientException(
          "第" + context.readRowHolder().getRowIndex() + "行“支付金额”最多允许2位小数");
    }

    if (payType.getRecText()) {
      if (StringUtil.isBlank(data.getText())) {
        throw new DefaultClientException(
            "第" + context.readRowHolder().getRowIndex() + "行“支付内容”不能为空");
      }
    } else {
      data.setText(null);
    }
  }

  @Override
  protected void afterAllAnalysed(AnalysisContext context) {

    // 根据ID 分组
    Map<String, List<ReceiveSheetPayTypeImportModel>> groupByMap = this.getDatas().stream()
        .collect(
            Collectors.groupingBy(ReceiveSheetPayTypeImportModel::getId));

    OrderPayTypeService orderPayTypeService = ApplicationUtil.getBean(
        OrderPayTypeService.class);

    int index = 0;
    for (Entry<String, List<ReceiveSheetPayTypeImportModel>> entry : groupByMap.entrySet()) {
      List<ReceiveSheetPayTypeImportModel> value = entry.getValue();
      String id = entry.getKey();

      BigDecimal totalAmount = value.stream().map(ReceiveSheetPayTypeImportModel::getPayAmount)
          .reduce(NumberUtil::add).orElse(BigDecimal.ZERO);
      if (!NumberUtil.equal(totalAmount, value.get(0).getTotalAmount())) {
        throw new DefaultClientException(
            "单据号：" + value.get(0).getCode() + "所有支付方式的支付金额不等于含税总金额，请检查");
      }

      orderPayTypeService.create(id, value.stream().map(t -> {
        OrderPayTypeVo orderPayTypeVo = new OrderPayTypeVo();
        orderPayTypeVo.setId(t.getPayTypeId());
        orderPayTypeVo.setPayAmount(t.getPayAmount());
        orderPayTypeVo.setText(t.getText());

        return orderPayTypeVo;
      }).collect(Collectors.toList()));

      index++;
      this.setSuccessProcessByIndex(index);
    }
  }

  @Override
  protected void doComplete() {

  }
}
