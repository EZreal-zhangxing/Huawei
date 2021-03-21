package fudan.Pojo;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangxing
 * @Date 2021/3/20
 * 封装了存量服务器的类
 * 包括其他的信息
 */
public class StockServiceInfo {
    //存量服务器列表
    private static List<StockService> stockService = new LinkedList<>();

    /**
     * cost = hardCost + dailyCost * daily
     * 总消耗的价值
     */
    private Integer cost=0;

    //日耗
    private Integer dailyCost=0;

    //存量服务器总的剩下的CPU
    private Integer cpusNumber=0;

    //存量服务器总的剩下的内存
    private Integer memoryNumber=0;

    /**
     * 数学性质上的修改可传入+-cost
     * cost+=parm
     * @param cost
     */
    public void setCostMath(Integer cost){
        this.cost += cost;
    }

    /**
     * 数学性质上的修改可传入+-dailyCost
     * dailyCost+=parm
     * @param dailyCost
     */
    public void setDailyCostMath(Integer dailyCost){
        this.dailyCost += dailyCost;
    }

    /**
     * 数学性质上的修改可传入+-cpusNumber
     * cpusNumber+=parm
     * @param cpusNumber
     */
    public void setCpusNumberMath(Integer cpusNumber){
        this.cpusNumber += cpusNumber;
    }

    /**
     * 数学性质上的修改可传入+-memoryNumber
     * memoryNumber+=parm
     * @param memoryNumber
     */
    public void setMemoryNumberMath(Integer memoryNumber){
        this.memoryNumber += memoryNumber;
    }

    public void setCpusNumber(Integer cpusNumber) {
        this.cpusNumber = cpusNumber;
    }

    public Integer getMemoryNumber() {
        return memoryNumber;
    }

    public void setMemoryNumber(Integer memoryNumber) {
        this.memoryNumber = memoryNumber;
    }

    public Integer getCpusNumber() {
        return cpusNumber;
    }

    public StockServiceInfo() {
    }

    public static List<StockService> getStockService() {
        return stockService;
    }

    public static void setStockService(List<StockService> stockService) {
        StockServiceInfo.stockService = stockService;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getDailyCost() {
        return dailyCost;
    }

    public void setDailyCost(Integer dailyCost) {
        this.dailyCost = dailyCost;
    }
}
