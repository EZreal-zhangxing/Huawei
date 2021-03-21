package fudan.Pojo;

/**
 * @author zhangxing
 * @Date 2021/3/20
 * 服务器上的虚拟机信息
 */
public class VirtualMachineOnService {
    //虚拟机ID
    private Integer vmid;

    //虚拟机信息
    private VirtualMachine virtualMachine;

    //部署信息 A,B,ALL
    private ArrangeType arrangeType;

    public VirtualMachineOnService() {
    }

    public Integer getVmid() {
        return vmid;
    }

    public void setVmid(Integer vmid) {
        this.vmid = vmid;
    }

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public ArrangeType getArrangeType() {
        return arrangeType;
    }

    public void setArrangeType(ArrangeType arrangeType) {
        this.arrangeType = arrangeType;
    }

    @Override
    public String toString() {
        return "VirtualMachineOnService{" +
                "vmid=" + vmid +
                ", virtualMachine=" + virtualMachine +
                ", arrangeType=" + arrangeType +
                '}';
    }
}

