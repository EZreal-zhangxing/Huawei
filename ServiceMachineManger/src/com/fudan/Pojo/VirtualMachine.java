package fudan.Pojo;

/**
 * @author zhangxing
 * @Date 2021/3/18
 * 虚拟机类
 */
public class VirtualMachine {
    //虚拟机类型
    private String vmName;

    private Integer cpuNumber;

    private Integer memoryNumber;

    //虚拟机部署类型 0 单节点 1双节点
    private Integer type;

    public VirtualMachine() {
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public Integer getCpuNumber() {
        return cpuNumber;
    }

    public void setCpuNumber(Integer cpuNumber) {
        this.cpuNumber = cpuNumber;
    }

    public Integer getMemoryNumber() {
        return memoryNumber;
    }

    public void setMemoryNumber(Integer memoryNumber) {
        this.memoryNumber = memoryNumber;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VirtualMachine{" +
                "vmName='" + vmName + '\'' +
                ", cpuNumber=" + cpuNumber +
                ", memoryNumber=" + memoryNumber +
                ", type=" + type +
                '}';
    }
}
