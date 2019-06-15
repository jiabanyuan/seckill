package com.dwj.seckill.enums;

public enum SeckillState {

    NU_SECKILL(0,"秒杀未开始"),
    SECKILLING(1,"秒杀已开启"),
    SECKILL_END(-1,"秒杀结束");

    private Integer code;
    private String desc;

    SeckillState(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
