package fudan.Pojo;

import java.util.HashMap;

/**
 * 服务器类
 * @author zhangxing
 * @Date 2021/3/18
 */
public class ServiceMachine {
    //服务器ID
    private Integer id;

    private String serviceName;

    private Integer cpuNumber;

    private Integer memoryNumber;
    //节点信息
    private HashMap<NodeType,ServiceNode> nodes;

    private Integer cost;

    private Integer dailyCost;

    //性价比
    private float costPriceRate;


    public ServiceMachine() {
        nodes = new HashMap<>(2);
        nodes.put(NodeType.A,new ServiceNode(NodeType.A));
        nodes.put(NodeType.B,new ServiceNode(NodeType.B));
    }

    public HashMap<NodeType, ServiceNode> getNodes() {
        return nodes;
    }

    public void setNodes(HashMap<NodeType, ServiceNode> nodes) {
        this.nodes = nodes;
    }

    public float getCostPriceRate() {
        return costPriceRate;
    }

    public void setCostPriceRate(float costPriceRate) {
        this.costPriceRate = costPriceRate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getCpuNumber() {
        return cpuNumber;
    }

    public void setCpuNumber(Integer cpuNumber) {
        nodes.get(NodeType.A).setCpuNumber(cpuNumber/2);
        nodes.get(NodeType.B).setCpuNumber(cpuNumber/2);
        this.cpuNumber = cpuNumber;
    }

    public Integer getMemoryNumber() {
        return memoryNumber;
    }

    public void setMemoryNumber(Integer memoryNumber) {
        nodes.get(NodeType.A).setMemoryNumber(memoryNumber/2);
        nodes.get(NodeType.B).setMemoryNumber(memoryNumber/2);
        this.memoryNumber = memoryNumber;
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

    public void flushCostPriceRate(int days){
        this.costPriceRate = (this.cost+this.dailyCost*days)/(0.5f * this.cpuNumber + 0.5f * this.memoryNumber);
    }
}
