package fudan.Pojo;

/**
 * @author zhangxing
 * @Date 2021/3/20
 * 服务器节点信息类
 */
public class ServiceNode {
    //节点类型
    private NodeType nodeType;

    //剩余CPU
    private Integer cpuNumber;

    //剩余memory
    private Integer memoryNumber;

    public ServiceNode() {
    }
    public ServiceNode(NodeType nodeType){
        this.nodeType =  nodeType;
    }

    public ServiceNode(NodeType nodeType, Integer cpuNumber, Integer memoryNumber) {
        this.nodeType = nodeType;
        this.cpuNumber = cpuNumber;
        this.memoryNumber = memoryNumber;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getCpuNumber() {
        return cpuNumber;
    }

    public void setCpuNumber(Integer cpuNumber) {
        this.cpuNumber = cpuNumber;
    }

    /**
     * 数学性质上的修改可传入+-cpuNumber
     * cpuNumber+=cpuNumber
     * @param cpuNumber
     */
    public void setCpuNumberMath(Integer cpuNumber){
        this.cpuNumber+=cpuNumber;
    }

    /**
     * 数学性质上的修改可传入+-memoryNumber
     * memoryNumber+=memoryNumber
     * @param memoryNumber
     */
    public void setMemoryNumberMath(Integer memoryNumber){
        this.memoryNumber += memoryNumber;
    }

    public Integer getMemoryNumber() {
        return memoryNumber;
    }

    public void setMemoryNumber(Integer memoryNumber) {
        this.memoryNumber = memoryNumber;
    }
}
