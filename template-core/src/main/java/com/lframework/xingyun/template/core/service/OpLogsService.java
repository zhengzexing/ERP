package com.lframework.xingyun.template.core.service;

import com.lframework.starter.web.resp.PageResult;
import com.lframework.starter.web.service.BaseMpService;
import com.lframework.xingyun.template.core.entity.OpLogs;
import com.lframework.xingyun.template.core.vo.CreateOpLogsVo;
import com.lframework.xingyun.template.core.vo.QueryOpLogsVo;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 操作日志Service
 *
 * @author zzx
 */
public interface OpLogsService extends BaseMpService<OpLogs> {

  /**
   * 创建
   *
   * @param vo
   * @return
   */
  String create(CreateOpLogsVo vo);

  /**
   * 创建
   *
   * @param list
   * @return
   */
  void create(Collection<CreateOpLogsVo> list);

  /**
   * 查询列表
   *
   * @param pageIndex
   * @param pageSize
   * @param vo
   * @return
   */
  PageResult<OpLogs> query(Integer pageIndex, Integer pageSize, QueryOpLogsVo vo);

  /**
   * 根据ID查询
   *
   * @param id
   * @return
   */
  OpLogs findById(String id);

  /**
   * 根据截止时间删除日志
   *
   * @param endTime
   */
  void clearLogs(LocalDateTime endTime);
}
