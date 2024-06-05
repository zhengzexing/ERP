package com.lframework.xingyun.settle.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.lframework.starter.web.enums.BaseEnum;

public enum SettleCheckSheetCalcType implements BaseEnum<Integer> {
    ADD(1, "加"), SUB(2, "减");

    @EnumValue
    private final Integer code;

    private final String desc;

    SettleCheckSheetCalcType(Integer code, String desc) {

        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {

        return this.code;
    }

    @Override
    public String getDesc() {

        return this.desc;
    }
}
