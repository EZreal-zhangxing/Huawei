package fudan.zx;

import fudan.Pojo.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangxing
 * @Date 2021/3/18
 */
public class Start {

    StockServiceInfo serviceInfo = PublicDataPool.stockServiceInfo;

    Map<Integer,List<Integer>> serviceToVMachine = PublicDataPool.serviceToVitualMachine;

    List<ServiceMachine> servicesToSale = PublicDataPool.servicesToSale;


    /**
     * 对数据进行初始化操作
     */
    public void initData(){
        //读取文本数据
        ReadData readData = new ReadData();
        readData.getData();
        //对服务器进行性价比排序操作
        PublicDataPool.servicesToSale = PublicDataPool.servicesToSale.stream().sorted(Comparator.comparingDouble(ServiceMachine::getCostPriceRate)).collect(Collectors.toList());
        //对虚拟机进行排序操作
        PublicDataPool.virtualToSale = PublicDataPool.virtualToSale.stream().sorted(Comparator.comparing(VirtualMachine::getVmName)).collect(Collectors.toList());
    }

    /**
     * 处理每天的数据
     */
    public void dealDailyRequest(DailyRequest dailyRequest){
        System.out.println("处理第["+dailyRequest.getDay()+"]天数据["+dailyRequest.getNum()+"]条");
        for(UserRequest userRequest : dailyRequest.getRequests()){
            System.out.println("\t处理请求：["+userRequest+"]");
            if(userRequest.getOperationType().equals(RequestEnum.ADD.getCode())){
                //添加的请求
                //更具请求的虚拟机找出的虚拟机配置信息
                VirtualMachine virtualMachine = Utils.getVirtualMachine(userRequest.getVirtualMachineType());
                StockService stockService = checkStockService(virtualMachine);
                if(stockService != null){
                    //存量服务器足够 分配并更新存量服务器信息
                    System.out.println("分配虚拟机id["+userRequest.getVirtualMachineId()+"],type["+virtualMachine.getVmName()+"] --> 服务器["+stockService.getId()+"]");
                    updateStockService(stockService,virtualMachine,userRequest.getVirtualMachineId());
                    updateServiceToVm(stockService,userRequest.getVirtualMachineId());
                }else{

                }
            }else{
                //删除的请求
            }
        }

    }




    /**
     * 更具服务器性价比购买服务器
     */
    public void buyService(){

    }


    /**
     * 更新虚拟机和服务器之间的关系
     * @param stockService
     * @param vmId
     */
    public void updateServiceToVm(StockService stockService,Integer vmId){
        //更新映射关系
        if(serviceToVMachine.containsKey(stockService.getId())){
            serviceToVMachine.get(stockService.getId()).add(vmId);
        }else{
            serviceToVMachine.put(stockService.getId(),Arrays.asList(vmId));
        }
    }

    /**
     * 更具虚拟机更新存量服务器
     * @param virtualMachine
     */
    public void updateStockService(StockService stockService,VirtualMachine virtualMachine,Integer vmId){
        //创建存量服务器上的虚拟机信息
        VirtualMachineOnService onService = new VirtualMachineOnService();
        onService.setVmid(vmId);
        ArrangeType arrangeType = putVirtualToservice(virtualMachine,stockService);
        onService.setArrangeType(arrangeType);
        onService.setVirtualMachine(virtualMachine);
        //更新存量服务器上的虚拟机信息
        stockService.getVirtualMachines().add(onService);
    }

    /**
     * 将虚拟机 部署到 指定服务器
     * 返回部署类型
     * @param virtualMachine
     * @param stockService
     * @return
     */
    public ArrangeType putVirtualToservice(VirtualMachine virtualMachine,StockService stockService){
        //更新存量服务器单个的统计信息 cpu+memory
        stockService.setCpuNumber(stockService.getCpuNumber()-virtualMachine.getCpuNumber());
        stockService.setMemoryNumber(stockService.getMemoryNumber() - virtualMachine.getMemoryNumber());
        HashMap<NodeType,ServiceNode> map = stockService.getNodes();

        if(virtualMachine.getType() == 0){
            //单节点
            if(map.get(NodeType.A).getCpuNumber() > virtualMachine.getCpuNumber() &&
                    map.get(NodeType.A).getMemoryNumber() > virtualMachine.getMemoryNumber()){
                map.get(NodeType.A).setCpuNumber(map.get(NodeType.A) .getCpuNumber() - virtualMachine.getCpuNumber());
                map.get(NodeType.A).setMemoryNumber(map.get(NodeType.A) .getMemoryNumber() - virtualMachine.getMemoryNumber());
                return ArrangeType.A;
            }else{
                map.get(NodeType.B).setCpuNumber(map.get(NodeType.B) .getCpuNumber() - virtualMachine.getCpuNumber());
                map.get(NodeType.B).setMemoryNumber(map.get(NodeType.B) .getMemoryNumber() - virtualMachine.getMemoryNumber());
                return ArrangeType.B;
            }
        }else{
            //双节点
            map.get(NodeType.A).setCpuNumber(map.get(NodeType.A) .getCpuNumber() - virtualMachine.getCpuNumber()/2);
            map.get(NodeType.A).setMemoryNumber(map.get(NodeType.A) .getMemoryNumber() - virtualMachine.getMemoryNumber()/2);
            map.get(NodeType.B).setCpuNumber(map.get(NodeType.B) .getCpuNumber() - virtualMachine.getCpuNumber()/2);
            map.get(NodeType.B).setMemoryNumber(map.get(NodeType.B) .getMemoryNumber() - virtualMachine.getMemoryNumber()/2);
            return ArrangeType.ALL;
        }
    }

    /**
     * 检查存量服务器是否足够
     * @param virtualMachine
     * @return
     */
    public StockService checkStockService(VirtualMachine virtualMachine){
        if(serviceInfo.getCpusNumber() < virtualMachine.getCpuNumber() || serviceInfo.getMemoryNumber() < virtualMachine.getMemoryNumber()){
            //如果存量服务器的Cpus/Memorys小于虚拟机要求的
            return null;
        }else{
            //遍历一遍是否能够放入并找到
            StockService stockService = getStockService(virtualMachine);
            return stockService;
        }
    }

    /**
     * 判断存量服务器能否找到满足虚拟机要求的服务器
     * 返回第一个符合条件的
     * @return StockService 存量服务器
     */
    public StockService getStockService(VirtualMachine virtualMachine){
        //遍历一遍是否能够放入并找到
        Optional<StockService> stockService = serviceInfo.getStockService().stream().filter(item ->{
            if(virtualMachine.getType() == 0){
                //单节点部署
                if((item.getNodes().get(NodeType.A).getCpuNumber() > virtualMachine.getCpuNumber() &&
                        item.getNodes().get(NodeType.A).getMemoryNumber() > virtualMachine.getMemoryNumber()) ||
                        (item.getNodes().get(NodeType.B).getCpuNumber() > virtualMachine.getCpuNumber() &&
                                item.getNodes().get(NodeType.B).getMemoryNumber() > virtualMachine.getMemoryNumber())){
                    return true;
                }
                return false;
            }else{
                //双节点
                if((item.getNodes().get(NodeType.A).getCpuNumber() > virtualMachine.getCpuNumber()/2 &&
                        item.getNodes().get(NodeType.A).getMemoryNumber() > virtualMachine.getMemoryNumber()/2) &&
                        (item.getNodes().get(NodeType.B).getCpuNumber() > virtualMachine.getCpuNumber()/2 &&
                                item.getNodes().get(NodeType.B).getMemoryNumber() > virtualMachine.getMemoryNumber()/2)){
                    return true;
                }
                return false;
            }
        }).findFirst();
        return stockService.isPresent()?stockService.get():null;
    }

    public static void main(String[] args) {
        Start start = new Start();
        start.initData();

        VirtualMachine vm = Utils.getVirtualMachine("vmFS8Q0");
        System.out.println(vm);
    }
}
