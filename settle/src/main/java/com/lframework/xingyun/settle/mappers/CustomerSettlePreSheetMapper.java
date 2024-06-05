package com.lframework.xingyun.settle.mappers;

import com.lframework.starter.web.mapper.BaseMapper;
import com.lframework.xingyun.sc.enums.SettleStatus;
import com.lframework.xingyun.settle.dto.pre.customer.CustomerSettlePreSheetFullDto;
import com.lframework.xingyun.settle.entity.CustomerSettlePreSheet;
import com.lframework.xingyun.settle.vo.pre.customer.QueryCustomerSettlePreSheetVo;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzx
 * @since 2021-12-02
 */
public interface CustomerSettlePreSheetMapper extends BaseMapper<CustomerSettlePreSheet> {

    /**
     * 查询列表
     *
     * @param vo
     * @return
     */
    List<CustomerSettlePreSheet> query(@Param("vo") QueryCustomerSettlePreSheetVo vo,
        @Param("dataPermission") String dataPermission);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    CustomerSettlePreSheetFullDto getDetail(String id);

    /**
     * 查询已审核列表
     *
     * @param customerId
     * @param startTime
     * @param endTime
     * @return
     */
    List<CustomerSettlePreSheet> getApprovedList(@Param("customerId") String customerId,
        @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
        @Param("settleStatus") SettleStatus settleStatus);
}
