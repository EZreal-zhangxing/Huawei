package com.huawei.java.main.Pojo;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangxing
 * @Date 2021/3/20
 * 日常请求封装类
 */
public class DailyRequest {
    //今天的请求数量
    private Integer num;
    //今天是第几天
    private Integer day;
    //用户请求列表
    private List<UserRequest> requests = new LinkedList<>();

    public DailyRequest() {
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public List<UserRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<UserRequest> requests) {
        this.requests = requests;
    }
}
