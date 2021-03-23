package com.huawei.java.main.Pojo;

public class PreBuyService {
    private ServiceMachine serviceMachine;

    private Integer acpu;
    private Integer bcpu;

    private Integer amemory;
    private Integer bmemory;

    public PreBuyService() {
    }

    public ServiceMachine getServiceMachine() {
        return serviceMachine;
    }

    public void setServiceMachine(ServiceMachine serviceMachine) {
        this.serviceMachine = serviceMachine;
    }

    public Integer getAcpu() {
        return acpu;
    }

    public void setAcpu(Integer acpu) {
        this.acpu = acpu;
    }

    public void setAcpuMath(Integer acpu) {
        this.acpu += acpu;
    }
    public Integer getBcpu() {
        return bcpu;
    }


    public void setBcpu(Integer bcpu) {
        this.bcpu = bcpu;
    }
    public void setBcpuMath(Integer bcpu) {
        this.bcpu += bcpu;
    }

    public Integer getAmemory() {
        return amemory;
    }

    public void setAmemory(Integer amemory) {
        this.amemory = amemory;
    }

    public void setAmemoryMath(Integer amemory) {
        this.amemory += amemory;
    }

    public Integer getBmemory() {
        return bmemory;
    }

    public void setBmemory(Integer bmemory) {
        this.bmemory = bmemory;
    }
    public void setBmemoryMath(Integer bmemory) {
        this.bmemory += bmemory;
    }
}
