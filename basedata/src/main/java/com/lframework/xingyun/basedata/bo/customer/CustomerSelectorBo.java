package com.lframework.xingyun.basedata.bo.customer;

import com.lframework.starter.web.bo.BaseBo;
import com.lframework.xingyun.basedata.entity.Customer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerSelectorBo extends BaseBo<Customer> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 编号
     */
    @ApiModelProperty("编号")
    private String code;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Boolean available;

    public CustomerSelectorBo() {

    }

    public CustomerSelectorBo(Customer dto) {

        super(dto);
    }
}
