package com.lframework.xingyun.settle.service;

import com.lframework.starter.web.resp.PageResult;
import com.lframework.starter.web.service.BaseMpService;
import com.lframework.xingyun.settle.entity.SettleOutItem;
import com.lframework.xingyun.settle.vo.item.out.CreateSettleOutItemVo;
import com.lframework.xingyun.settle.vo.item.out.QuerySettleOutItemVo;
import com.lframework.xingyun.settle.vo.item.out.SettleOutItemSelectorVo;
import com.lframework.xingyun.settle.vo.item.out.UpdateSettleOutItemVo;

import java.util.Collection;
import java.util.List;

public interface SettleOutItemService extends BaseMpService<SettleOutItem> {

    /**
     * 查询列表
     *
     * @return
     */
    PageResult<SettleOutItem> query(Integer pageIndex, Integer pageSize, QuerySettleOutItemVo vo);

    /**
     * 查询列表
     *
     * @param vo
     * @return
     */
    List<SettleOutItem> query(QuerySettleOutItemVo vo);

    /**
     * 选择器
     *
     * @param pageIndex
     * @param pageSize
     * @param vo
     * @return
     */
    PageResult<SettleOutItem> selector(Integer pageIndex, Integer pageSize, SettleOutItemSelectorVo vo);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    SettleOutItem findById(String id);

    /**
     * 根据ID停用
     *
     * @param ids
     */
    void batchUnable(Collection<String> ids);

    /**
     * 根据ID启用
     *
     * @param ids
     */
    void batchEnable(Collection<String> ids);

    /**
     * 创建
     *
     * @param vo
     * @return
     */
    String create(CreateSettleOutItemVo vo);

    /**
     * 修改
     *
     * @param vo
     */
    void update(UpdateSettleOutItemVo vo);
}
