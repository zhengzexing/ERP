package com.lframework.xingyun.settle.mappers;

import com.lframework.starter.web.mapper.BaseMapper;
import com.lframework.xingyun.settle.dto.check.SettleCheckSheetFullDto;
import com.lframework.xingyun.settle.entity.SettleCheckSheet;
import com.lframework.xingyun.settle.vo.check.QuerySettleCheckSheetVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzx
 * @since 2021-12-02
 */
public interface SettleCheckSheetMapper extends BaseMapper<SettleCheckSheet> {

    /**
     * 查询列表
     *
     * @param vo
     * @return
     */
    List<SettleCheckSheet> query(@Param("vo") QuerySettleCheckSheetVo vo,
        @Param("dataPermission") String dataPermission);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    SettleCheckSheetFullDto getDetail(String id);

    /**
     * 查询已审核列表
     *
     * @param supplierId
     * @param startTime
     * @param endTime
     * @return
     */
    List<SettleCheckSheet> getApprovedList(@Param("supplierId") String supplierId,
            @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
