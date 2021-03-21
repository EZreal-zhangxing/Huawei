package fudan.Pojo;

/**
 * 用户请求
 * @author zhangxing
 * @Date 2021/3/20/13:18
 */
public class UserRequest {
    private String operationType;

    private String virtualMachineType;

    private static Integer virtualMachineId;

    public UserRequest() {
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getVirtualMachineType() {
        return virtualMachineType;
    }

    public void setVirtualMachineType(String virtualMachineType) {
        this.virtualMachineType = virtualMachineType;
    }

    public static Integer getVirtualMachineId() {
        return virtualMachineId;
    }

    public void setVirtualMachineId(Integer virtualMachineId) {
        this.virtualMachineId = virtualMachineId;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "operationType='" + operationType + '\'' +
                ", virtualMachineType='" + virtualMachineType + '\'' +
                ", virtualMachineId=" + virtualMachineId +
                '}';
    }
}
