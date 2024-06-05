package com.lframework.xingyun.template.gen.service;

import com.lframework.xingyun.template.gen.dto.gen.GenUpdateColumnConfigDto;
import com.lframework.xingyun.template.gen.entity.GenUpdateColumnConfig;
import com.lframework.xingyun.template.gen.vo.gen.UpdateUpdateColumnConfigVo;
import com.lframework.starter.web.service.BaseMpService;
import java.util.List;

public interface GenUpdateColumnConfigService extends BaseMpService<GenUpdateColumnConfig> {

  /**
   * 根据数据实体ID查询
   *
   * @param entityId
   * @return
   */
  List<GenUpdateColumnConfigDto> getByDataEntityId(String entityId);

  /**
   * 修改生成器配置信息
   *
   * @param vo
   */
  void updateGenerate(String entityId, List<UpdateUpdateColumnConfigVo> vo);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  GenUpdateColumnConfigDto findById(String id);

  /**
   * 根据ID删除
   *
   * @param id
   */
  void deleteById(String id);
}
