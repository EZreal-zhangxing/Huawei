package com.huawei.java.main.Pojo;

import java.util.*;

/**
 * @author zhangxing
 * @Date 2021/3/18
 * 数据池
 */
public class PublicDataPool {
    //每日可以迁移的数量
    public static Integer dailyMoveNum=0;
    //虚拟机数量
    public static Integer virtualNumber = 0;
    //需要运行的天数
    public static Integer allDays=0;
    //当前运行到第几天
    public static Integer day= 0 ;
    //已经购买服务器的数量
    public static Integer alreadlyBuyService = 0;
    //可以购买的服务器列表
    public static List<ServiceMachine> servicesToSale = new ArrayList<>();
    //可以代售的虚拟机列表
    public static List<VirtualMachine> virtualToSale = new ArrayList<>();

    //已经购买的服务器列表
    public static List<ServiceMachine> haveServices = new ArrayList<>();
    //已经购买的虚拟机列表
    public static List<VirtualMachine> runingVms = new ArrayList<>();
    /**
     * 服务器与虚拟机的映射关系
     * -----      ---    ---
     * |    |--->|  |-->|  |
     * -----     ---    ---
     * |    |
     * -----
     * <服务器ID,虚拟机id list>
     */
    public static Map<Integer,List<Integer>> serviceToVitualMachine = new TreeMap<>();

    //存量服务器信息
    public static StockServiceInfo stockServiceInfo = new StockServiceInfo();
    //日常请求
    public static List<DailyRequest> dailyRequests = new LinkedList<>();
    //每天的结果
    public static Map<Integer,Map<String,String>> result = new HashMap<>();

    public static List<ArrangeType> arrangeTypes = new ArrayList<>();

    public static List<PreBuyService> preBuyServices;
}
