package com.lframework.xingyun.basedata.impl.storecenter;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.exceptions.impl.InputErrorException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.ObjectUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.xingyun.template.core.annotations.OpLog;
import com.lframework.xingyun.template.core.enums.DefaultOpLogType;
import com.lframework.starter.web.impl.BaseMpServiceImpl;
import com.lframework.starter.web.resp.PageResult;
import com.lframework.xingyun.template.core.utils.OpLogUtil;
import com.lframework.starter.web.utils.PageHelperUtil;
import com.lframework.starter.web.utils.PageResultUtil;
import com.lframework.starter.web.utils.IdUtil;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.mappers.StoreCenterMapper;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.basedata.vo.storecenter.CreateStoreCenterVo;
import com.lframework.xingyun.basedata.vo.storecenter.QueryStoreCenterSelectorVo;
import com.lframework.xingyun.basedata.vo.storecenter.QueryStoreCenterVo;
import com.lframework.xingyun.basedata.vo.storecenter.UpdateStoreCenterVo;
import com.lframework.xingyun.core.dto.dic.city.DicCityDto;
import com.lframework.xingyun.core.service.DicCityService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreCenterServiceImpl extends BaseMpServiceImpl<StoreCenterMapper, StoreCenter>
        implements StoreCenterService {

    @Autowired
    private DicCityService dicCityService;

    @Override
    public PageResult<StoreCenter> query(Integer pageIndex, Integer pageSize, QueryStoreCenterVo vo) {

        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<StoreCenter> datas = getBaseMapper().query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Cacheable(value = StoreCenter.CACHE_NAME, key = "@cacheVariables.tenantId() + #id", unless = "#result == null")
    @Override
    public StoreCenter findById(String id) {

        return getBaseMapper().selectById(id);
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "停用仓库，ID：{}", params = "#ids", loopFormat = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchUnable(Collection<String> ids) {

        if (CollectionUtil.isEmpty(ids)) {
            return;
        }

        Wrapper<StoreCenter> updateWrapper = Wrappers.lambdaUpdate(StoreCenter.class)
                .set(StoreCenter::getAvailable, Boolean.FALSE).in(StoreCenter::getId, ids);
        getBaseMapper().update(updateWrapper);
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "启用仓库，ID：{}", params = "#ids", loopFormat = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchEnable(Collection<String> ids) {

        if (CollectionUtil.isEmpty(ids)) {
            return;
        }

        Wrapper<StoreCenter> updateWrapper = Wrappers.lambdaUpdate(StoreCenter.class)
                .set(StoreCenter::getAvailable, Boolean.TRUE).in(StoreCenter::getId, ids);
        getBaseMapper().update(updateWrapper);
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "新增仓库，ID：{}, 编号：{}", params = {"#id", "#code"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateStoreCenterVo vo) {

        Wrapper<StoreCenter> checkWrapper = Wrappers.lambdaQuery(StoreCenter.class)
                .eq(StoreCenter::getCode, vo.getCode());
        if (getBaseMapper().selectCount(checkWrapper) > 0) {
            throw new DefaultClientException("编号重复，请重新输入！");
        }

        StoreCenter data = new StoreCenter();
        data.setId(IdUtil.getId());
        data.setCode(vo.getCode());
        data.setName(vo.getName());
        if (!StringUtil.isBlank(vo.getContact())) {
            data.setContact(vo.getContact());
        }
        if (!StringUtil.isBlank(vo.getTelephone())) {
            data.setTelephone(vo.getTelephone());
        }
        data.setAvailable(Boolean.TRUE);
        if (!StringUtil.isBlank(vo.getCityId())) {
            DicCityDto city = dicCityService.findById(vo.getCityId());
            if (!ObjectUtil.isNull(city)) {
                data.setCityId(vo.getCityId());
            }
        }

        if (!StringUtil.isBlank(vo.getAddress())) {
            data.setAddress(vo.getAddress());
        }

        if (vo.getPeopleNum() != null) {
            if (vo.getPeopleNum() < 0) {
                throw new InputErrorException("仓库人数不允许小于0！");
            }
            data.setPeopleNum(vo.getPeopleNum());
        }

        data.setDescription(StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

        getBaseMapper().insert(data);

        OpLogUtil.setVariable("id", data.getId());
        OpLogUtil.setVariable("code", vo.getCode());
        OpLogUtil.setExtra(vo);

        return data.getId();
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "修改仓库，ID：{}, 编号：{}", params = {"#id", "#code"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(UpdateStoreCenterVo vo) {

        StoreCenter data = getBaseMapper().selectById(vo.getId());
        if (ObjectUtil.isNull(data)) {
            throw new DefaultClientException("仓库不存在！");
        }

        Wrapper<StoreCenter> checkWrapper = Wrappers.lambdaQuery(StoreCenter.class)
                .eq(StoreCenter::getCode, vo.getCode()).ne(StoreCenter::getId, vo.getId());
        if (getBaseMapper().selectCount(checkWrapper) > 0) {
            throw new DefaultClientException("编号重复，请重新输入！");
        }

        LambdaUpdateWrapper<StoreCenter> updateWrapper = Wrappers.lambdaUpdate(StoreCenter.class)
                .set(StoreCenter::getCode, vo.getCode()).set(StoreCenter::getName, vo.getName())
                .set(StoreCenter::getContact, !StringUtil.isBlank(vo.getContact()) ? vo.getContact() : null)
                .set(StoreCenter::getTelephone, !StringUtil.isBlank(vo.getTelephone()) ? vo.getTelephone() : null)
                .set(StoreCenter::getAvailable, vo.getAvailable())
                .set(StoreCenter::getAddress, !StringUtil.isBlank(vo.getAddress()) ? vo.getAddress() : null)
                .set(StoreCenter::getDescription,
                        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription())
                .eq(StoreCenter::getId, vo.getId());

        if (!StringUtil.isBlank(vo.getCityId())) {
            DicCityDto city = dicCityService.findById(vo.getCityId());
            if (!ObjectUtil.isNull(city)) {
                updateWrapper.set(StoreCenter::getCityId, vo.getCityId());
            }
        } else {
            updateWrapper.set(StoreCenter::getCityId, null);
        }

        if (vo.getPeopleNum() != null) {
            if (vo.getPeopleNum() < 0) {
                throw new InputErrorException("仓库人数不允许小于0！");
            }
            updateWrapper.set(StoreCenter::getPeopleNum, vo.getPeopleNum());
        } else {
            updateWrapper.set(StoreCenter::getPeopleNum, null);
        }

        getBaseMapper().update(updateWrapper);

        OpLogUtil.setVariable("id", data.getId());
        OpLogUtil.setVariable("code", vo.getCode());
        OpLogUtil.setExtra(vo);
    }

    @Override
    public PageResult<StoreCenter> selector(Integer pageIndex, Integer pageSize, QueryStoreCenterSelectorVo vo) {

        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<StoreCenter> datas = getBaseMapper().selector(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @CacheEvict(value = StoreCenter.CACHE_NAME, key = "@cacheVariables.tenantId() + #key")
    @Override
    public void cleanCacheByKey(Serializable key) {

    }
}
