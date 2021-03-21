package fudan.sf;
import fudan.Pojo.*;

import java.io.*;
import java.util.*;

import  fudan.zx.*;
/*

问题很多就单独列了一个包，后面修改或者不要
 */
public class del {

    StockServiceInfo serviceInfo = PublicDataPool.stockServiceInfo;

    Map<Integer, List<Integer>> serviceToVMachine = PublicDataPool.serviceToVitualMachine;

    List<ServiceMachine> haveServices = PublicDataPool.haveServices;

    List<VirtualMachine> runingVms = PublicDataPool.runingVms;

    boolean delflag = false;  //标志服务器删除后是否为空

    public void delete(UserRequest userRequest) {

        if (userRequest.getOperationType().equals(RequestEnum.DEL.getCode())) {
            int vmid = userRequest.getVirtualMachineId();
            int serverid = findserverid(vmid, serviceToVMachine);
            ServiceMachine server = new ServiceMachine();
            StockService stockserver = new StockService();
            VirtualMachineOnService virtualMachineOnService = new VirtualMachineOnService();//应该是onService的vm，但是没有这个队列后面提到了
            if (serverid != -1) {
                serviceToVMachine.get(serverid).remove(vmid);
                if (serviceToVMachine.get(serverid).isEmpty()) {      //isempty能判断节点为空吗
                    delflag = true;
                    int dailycost = serviceInfo.getDailyCost();

                    findserviceMachine(serverid, server); //server是不是不会改变
                    findstockservice(serverid, stockserver);
                    findvirtualMachine(vmid,virtualMachineOnService.getVirtualMachine());//应该是onService的vm，但是没有这个队列后面提到了
                    dailycost -= server.getDailyCost();

                }

                updatestockserviceinfo(virtualMachineOnService);//server是不是不会改变
                updateServiceToVm(stockserver, vmid);
                updateStockService(stockserver,virtualMachineOnService);


            } else {
                System.out.println("delete error,no such virtualmachine");
            }


        }

    }


    /**
     * 根据虚拟机ID找服务器ID
     */
    public int findserverid(int vmid, Map<Integer, List<Integer>> treeMap) {

        Iterator<Integer> it1 = treeMap.keySet().iterator();
        while (it1.hasNext()) {
            Integer key = it1.next();
            List valuelist = treeMap.get(key);
            for (Iterator<Integer> it2 = valuelist.iterator(); it2.hasNext(); ) {
                if (it2.equals(vmid)) {
                    return key;

                }
            }

        }
        return -1;
    }

    /**
     * 根据服务器id找服务器整体信息
     */

    public void findserviceMachine(int serverid, ServiceMachine server) {

        for (Iterator<ServiceMachine> it1 = haveServices.iterator(); it1.hasNext(); ) {
            if (it1.next().equals(serverid)) {  //怎么比较it1指向的service的id和serverid
                server = (ServiceMachine) it1.next();
                break;
            }
        }
    }

    /**
     * 根据服务器id找服务器存量信息
     */

    public void findstockservice(int serverid, StockService stockserver) {

        for (Iterator<StockService> it1 = serviceInfo.getStockService().iterator(); it1.hasNext(); ) {
            if (it1.next().equals(serverid)) {  //同上
                stockserver = (StockService) it1.next();
                break;
            }
        }
    }

    /*
     *根据虚拟机ID找虚拟机，
     *
     * 需要正在运行的虚拟机列表而不是已经购买的虚拟机列表，不然找不到对应的分配方式！
     * 有问题，没有这个获得不了arrangetype，删除不了服务器上的。这里少了一块
     */

    public void findvirtualMachine(int vmid, VirtualMachine virtualMachine) {

        for (Iterator<VirtualMachine> it1 = runingVms.iterator(); it1.hasNext(); ) {
            if (it1.next().equals(vmid)) {
                virtualMachine = (VirtualMachine) it1.next();
                break;
            }
        }
    }



    /**
     * 映射
     */

    public void updateServiceToVm(StockService stockService, Integer vmId) {
        //更新映射关系
        if (!delflag) {
            serviceToVMachine.remove(stockService.getId());
        } else {
            serviceToVMachine.remove(stockService.getId(), stockService.getId().equals(vmId));//是不是有问题
        }
    }

    public void updatestockserviceinfo(VirtualMachineOnService virtualMachineOnService){
        serviceInfo.setCpusNumber(serviceInfo.getCpusNumber()+virtualMachineOnService.getVirtualMachine().getCpuNumber());
        serviceInfo.setMemoryNumber(serviceInfo.getMemoryNumber()+virtualMachineOnService.getVirtualMachine().getMemoryNumber());
    }

    /**
     * 存量
     */

    public void updateStockService(StockService stockService, VirtualMachineOnService virtualMachineOnService) {

        stockService.setCpuNumber(stockService.getCpuNumber()+virtualMachineOnService.getVirtualMachine().getCpuNumber());
        stockService.setMemoryNumber(stockService.getMemoryNumber()+ virtualMachineOnService.getVirtualMachine().getMemoryNumber());
        HashMap<NodeType,ServiceNode> map = stockService.getNodes();

        if(virtualMachineOnService.getArrangeType().equals('A')){
            map.get(NodeType.A).setCpuNumber(map.get(NodeType.A) .getCpuNumber() + virtualMachineOnService.getVirtualMachine().getCpuNumber());
            map.get(NodeType.A).setMemoryNumber(map.get(NodeType.A) .getMemoryNumber() + virtualMachineOnService.getVirtualMachine().getMemoryNumber());
        }
        else if (virtualMachineOnService.getArrangeType().equals('B')){

            map.get(NodeType.B).setCpuNumber(map.get(NodeType.B) .getCpuNumber() + virtualMachineOnService.getVirtualMachine().getCpuNumber());
            map.get(NodeType.B).setMemoryNumber(map.get(NodeType.B) .getMemoryNumber() + virtualMachineOnService.getVirtualMachine().getMemoryNumber());

        }else{

            map.get(NodeType.A).setCpuNumber(map.get(NodeType.A) .getCpuNumber() + virtualMachineOnService.getVirtualMachine().getCpuNumber()/2);
            map.get(NodeType.A).setMemoryNumber(map.get(NodeType.A) .getMemoryNumber() + virtualMachineOnService.getVirtualMachine().getMemoryNumber()/2);
            map.get(NodeType.B).setCpuNumber(map.get(NodeType.B) .getCpuNumber() + virtualMachineOnService.getVirtualMachine().getCpuNumber()/2);
            map.get(NodeType.B).setMemoryNumber(map.get(NodeType.B) .getMemoryNumber() + virtualMachineOnService.getVirtualMachine().getMemoryNumber()/2);
        }


    }


}




