package com.huawei.java.main.Pojo;

/**
 * @author zhangxing
 * @Date 2021/3/20
 * 请求类型
 */
public enum RequestEnum {
    ADD(1,"add"),
    DEL(2,"del");

    private Integer num;

    private String code;

    RequestEnum(Integer num, String code) {
        this.num = num;
        this.code = code;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
