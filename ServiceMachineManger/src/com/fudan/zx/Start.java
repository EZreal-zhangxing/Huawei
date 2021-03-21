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

    //已经购买的服务器列表
    List<ServiceMachine> haveServices = PublicDataPool.haveServices;


    /**
     * 对数据进行初始化操作
     */
    public void initData(){
        //读取文本数据
        ReadData readData = new ReadData();
        readData.getData();
        //对服务器进行性价比排序操作
        PublicDataPool.servicesToSale = PublicDataPool.servicesToSale.stream().sorted(Comparator.comparingDouble(ServiceMachine::getCostPriceRate)).collect(Collectors.toList());
        //输出服务器列表信息
        servicesToSale.stream().forEach(System.out::println);
        //对虚拟机进行排序操作
        PublicDataPool.virtualToSale = PublicDataPool.virtualToSale.stream().sorted(Comparator.comparing(VirtualMachine::getVmName)).collect(Collectors.toList());
    }

    /**
     * 处理每天的数据
     */
    public void dealDailyRequest(DailyRequest dailyRequest,int day){
        System.out.println("处理第["+dailyRequest.getDay()+"]天数据["+dailyRequest.getNum()+"]条");
        //初始化迁移操作次数
        PublicDataPool.dailyMoveNum = (int) Math.floor(serviceInfo.getStockService().stream().collect(Collectors.summingInt(item -> {return item.getVirtualMachines().size();})) * 0.005);
        for(UserRequest userRequest : dailyRequest.getRequests()){
            System.out.println("\t处理请求：["+userRequest+"]");
            if(userRequest.getOperationType().equals(RequestEnum.ADD.getCode())){
                //添加的请求
                //更具请求的虚拟机找出的虚拟机配置信息
                VirtualMachine virtualMachine = Utils.getVirtualMachine(userRequest.getVirtualMachineType());
                StockService stockService = checkStockService(virtualMachine);
                if(stockService != null){
                    //存量服务器足够 分配并更新存量服务器信息
                    System.out.println("\t分配虚拟机id["+userRequest.getVirtualMachineId()+"],type["+virtualMachine.getVmName()+"] --> 服务器["+stockService.getId()+"]");
                    updateStockService(stockService,virtualMachine,userRequest.getVirtualMachineId());
                    updateServiceToVm(stockService,userRequest.getVirtualMachineId());
                }else{
                    //总容量够不够
                    if(checkAllStockService(virtualMachine)){
                        //总容量够考虑迁移算法
                        System.out.println("\t迁移算法开始...");
                        int movenum = moveService(day);
                        if(movenum > 0){
                            //迁移成功一次
                            //开始分配
                            StockService stockServiceAgain = checkStockService(virtualMachine);
                            if(stockServiceAgain != null){
                                //存量服务器足够 分配并更新存量服务器信息
                                System.out.println("\t分配虚拟机id["+userRequest.getVirtualMachineId()+"],type["+virtualMachine.getVmName()+"] --> 服务器["+stockService.getId()+"]");
                                updateStockService(stockService,virtualMachine,userRequest.getVirtualMachineId());
                                updateServiceToVm(stockService,userRequest.getVirtualMachineId());
                            }
                        }else{
                            //并不能迁移出合适的服务器
                            System.out.println("\t迁移失败!!!");
//                            StockService service = buyService();
//                            System.out.println("\t购买服务器["+service+"]");
//                            System.out.println("\t分配虚拟机id["+userRequest.getVirtualMachineId()+"],type["+virtualMachine.getVmName()+"] --> 服务器["+service.getId()+"]");
//                            putVirtualToservice(virtualMachine,service);
                        }
                    }
                    //总量不够
                    //购买服务器
                    StockService service = buyService();
                    ServiceMachine sale = servicesToSale.get(service.getId());
                    System.out.println("\t购买服务器["+service+"]");
                    System.out.println("\t分配虚拟机id["+userRequest.getVirtualMachineId()+"],type["+virtualMachine.getVmName()+"] --> 服务器["+service.getId()+"]");
                    putVirtualToservice(virtualMachine,service);
                }
            }else{
                //删除的请求
                System.out.println("\t处理请求：["+userRequest+"]");
                int key=0;//serviceid
                for(Map.Entry<Integer,List<Integer>> entry:serviceToVMachine.entrySet()){
                    if(entry.getValue().contains(userRequest.getVirtualMachineId())){
                        key = entry.getKey();
                        break;
                    }
                }
                StockService target = null;
                for(StockService service:serviceInfo.getStockService()){
                    if(service.getId() == key){
                        target = service;
                        break;
                    }
                }
                VirtualMachineOnService machineOnService = null;
                for(VirtualMachineOnService onService:target.getVirtualMachines()){
                    if(onService.getVmid() == userRequest.getVirtualMachineId()){
                        machineOnService = onService;
                        break;
                    }
                }
                delVirtualMachine(target,machineOnService);
                System.out.println("\t从服务器["+target.getId()+"] 上删除虚拟机实例["+machineOnService.getVmid()+"]");
            }
        }
    }

    /**
     * 删除调存量服务器上的虚拟机
     * @param stockService
     * @param onService
     */
    public void delVirtualMachine(StockService stockService,VirtualMachineOnService onService){
        //如果删除的虚拟机位于A节点
        if(onService.getArrangeType() == ArrangeType.A){
            //释放A节点资源
            freeResource(NodeType.A,stockService,onService.getVirtualMachine().getCpuNumber(),onService.getVirtualMachine().getMemoryNumber());
        }else if(onService.getArrangeType() == ArrangeType.B){
            //如果删除的虚拟机位于B节点
            freeResource(NodeType.B,stockService,onService.getVirtualMachine().getCpuNumber(),onService.getVirtualMachine().getMemoryNumber());
        }else{
            //双节点部署
            freeResource(NodeType.A,stockService,onService.getVirtualMachine().getCpuNumber()/2,onService.getVirtualMachine().getMemoryNumber()/2);
            freeResource(NodeType.B,stockService,onService.getVirtualMachine().getCpuNumber()/2,onService.getVirtualMachine().getMemoryNumber()/2);
        }
        //移除掉虚拟机
        stockService.getVirtualMachines().remove(onService);
    }


    /**
     * 更具节点类型 释放指定服务器上的资源
     * @param nodeType
     * @param stockService
     * @param cpu
     * @param memory
     */
    public void freeResource(NodeType nodeType,StockService stockService,Integer cpu,Integer memory){
        //节点资源修改
        stockService.getNodes().get(nodeType).setMemoryNumberMath(memory);
        stockService.getNodes().get(nodeType).setCpuNumberMath(cpu);
        //单个存量服务器资源修改
        stockService.setMemoryNumberMath(memory);
        stockService.setCpuNumberMath(cpu);
        //总资源修改
        serviceInfo.setMemoryNumberMath(memory);
        serviceInfo.setCpusNumberMath(cpu);
    }

    /**
     * 对服务器进行迁移
     * 返回迁移的次数
     * @return
     */
    public int moveService(int day){
        List<StockService> list = serviceInfo.getStockService();
        int movenum=0;
        for(int i=0;i<list.size() && movenum < PublicDataPool.dailyMoveNum;i++){
            StockService targetstockService = list.get(i);
            if(targetstockService.getIsFull() == 0){
                //服务器没使用完
                Map<String,Object> result = getVirtualMachine(targetstockService,i+1);
                if(result.containsKey("vm")){
                    movenum++;
                    VirtualMachineOnService onService = (VirtualMachineOnService) result.get("vm");
                    StockService service = (StockService) result.get("sm");
                    //开始迁移
                    //删除调原始资源上的信息
                    delVirtualMachine(service,onService);
                    //判断service是否置空 置空减掉日耗
                    checkServiceIsEmpy(service);
                    //将虚拟机放入指定服务器
                    updateStockService(targetstockService,onService.getVirtualMachine(),onService.getVmid());
                    System.out.println("将服务器 service["+service.getId()+"] 上的虚拟机["+onService.getVmid()+","+onService.getVirtualMachine().getVmName()+"] ---> ["+targetstockService.getId()+"]");
                }else{
                    //并没找到合适的虚拟机
                }
            }
        }
        //设置迁移多少次
        return movenum;
    }

    /**
     * 判断服务器是否置空
     */
    public void checkServiceIsEmpy(StockService stockService){
        if(stockService.getVirtualMachines().size() == 0){
            //将日耗减掉
            serviceInfo.setDailyCostMath(-servicesToSale.get(stockService.getId()).getDailyCost());
        }
    }

    /**
     * 从索引开始查找合适的虚拟机填充
     * 返回合适的虚拟机
     * @param start
     * @return
     */
    public Map<String,Object> getVirtualMachine(StockService target,int start){
        Integer Acpu = target.getNodes().get(NodeType.A).getCpuNumber();
        Integer Amemory = target.getNodes().get(NodeType.A).getMemoryNumber();
        Integer Bcpu = target.getNodes().get(NodeType.B).getCpuNumber();
        Integer Bmemory = target.getNodes().get(NodeType.B).getMemoryNumber();

        HashMap<String,Object> result = new HashMap<>(2);

        List<StockService> list = serviceInfo.getStockService();
        int differentASum=Integer.MAX_VALUE,differentBSum=Integer.MAX_VALUE;//A,B节点CPU、内存分别与目标区域的差值
        int sum = 0;
        HashMap<String,String> map = new HashMap<>(list.size() - start);
        for(int i=start;i<list.size();i++){
            sum = 0;
            StockService stockService = list.get(i);
            List<VirtualMachineOnService> onServiceList = stockService.getVirtualMachines();
            System.out.println("\t\t开始查找存量服务器["+stockService.getId()+"]");
            for(int j=0;j<onServiceList.size();j++){
                VirtualMachineOnService machineOnService = onServiceList.get(j);
                System.out.println("\t\t\t开始查找虚拟机["+machineOnService.getVmid()+"]["+machineOnService.getVirtualMachine()+"]");
                int asum=0,bsum=0;
                if(machineOnService.getVirtualMachine().getType() == 0){
                    //单节点部署
                    //A节点可以满足
                    if(Acpu >= machineOnService.getVirtualMachine().getCpuNumber() && Amemory >= machineOnService.getVirtualMachine().getMemoryNumber()){
                        asum += (Amemory - machineOnService.getVirtualMachine().getMemoryNumber());
                        asum += (Acpu - machineOnService.getVirtualMachine().getCpuNumber());
                        bsum = Bmemory+Bcpu;
                    }else if(Bcpu >= machineOnService.getVirtualMachine().getCpuNumber() && Bmemory >= machineOnService.getVirtualMachine().getMemoryNumber()){
                        //B节点可以满足
                        asum = 0;bsum = 0;//初始化
                        bsum += (Bmemory - machineOnService.getVirtualMachine().getMemoryNumber());
                        bsum += (Bcpu - machineOnService.getVirtualMachine().getCpuNumber());
                        asum = Acpu +Bmemory;
                    }else{
                        asum = -1;
                        bsum = -1;
                    }
                }else{
                    //双节点部署
                    if((Acpu >= machineOnService.getVirtualMachine().getCpuNumber()/2 && Amemory >= machineOnService.getVirtualMachine().getMemoryNumber()/2) &&
                            (Bcpu >= machineOnService.getVirtualMachine().getCpuNumber()/2 && Bmemory >= machineOnService.getVirtualMachine().getMemoryNumber()/2)){
                        //双节点满足条件
                        asum += Acpu - machineOnService.getVirtualMachine().getCpuNumber()/2;
                        asum += Amemory - machineOnService.getVirtualMachine().getMemoryNumber()/2;
                        bsum += Bcpu - machineOnService.getVirtualMachine().getCpuNumber()/2;
                        bsum += Bmemory - machineOnService.getVirtualMachine().getMemoryNumber()/2;
                    }else{
                        asum = -1;
                        bsum = -1;
                    }
                }
                map.put("服务器id["+i+"]_虚拟机["+j+"]","A剩余["+asum+"]_B剩余["+bsum+"]");
                System.out.println("\t\t服务器id["+i+"]_虚拟机["+j+"]"+"A剩余["+asum+"]_B剩余["+bsum+"]");
                if(asum >=0 && bsum >=0 && asum <= differentASum && bsum <= differentBSum){
                    differentASum = asum;
                    differentBSum = bsum;
                    result.put("vm",machineOnService);
                    result.put("sm",stockService);
                    System.out.println("\t\t最适合虚拟机为["+i+"]["+j+"]");
                }
            }
        }
//        map.forEach((k,v)->{
//            System.out.println("key is :"+k+".value is :"+v);
//        });
        return result;
    }




    /**
     * 更具服务器性价比购买服务器
     */
    public StockService buyService(){
        ServiceMachine serviceMachine = servicesToSale.get(0);
        StockService stockService = new StockService();
        stockService.setId(serviceMachine.getId());
        stockService.setCpuNumber(serviceMachine.getCpuNumber());
        stockService.setMemoryNumber(serviceMachine.getMemoryNumber());
        stockService.getNodes().put(NodeType.A,new ServiceNode(NodeType.A,serviceMachine.getCpuNumber()/2,serviceMachine.getMemoryNumber()/2));
        stockService.getNodes().put(NodeType.B,new ServiceNode(NodeType.B,serviceMachine.getCpuNumber()/2,serviceMachine.getMemoryNumber()/2));
        //把资源添加到存量服务器
        serviceInfo.getStockService().add(stockService);
        //更新耗费
        serviceInfo.setCostMath(serviceMachine.getCost());
        serviceInfo.setDailyCostMath(serviceMachine.getDailyCost());
        serviceInfo.setCpusNumberMath(serviceMachine.getCpuNumber());
        serviceInfo.setMemoryNumberMath(serviceMachine.getMemoryNumber());

        //把服务器添加到已经购买的服务器列表
        haveServices.add(serviceMachine);
        //从售卖服务器列表中移除
//        servicesToSale.remove(0);

        return stockService;
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
            serviceToVMachine.put(stockService.getId(),new ArrayList<>(Arrays.asList(vmId)));
        }
    }

    /**
     * 根据虚拟机更新存量服务器
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
        System.out.println("\t存量服务器["+stockService.getId()+"] cpu["+stockService.getCpuNumber()+"] memory["+stockService.getMemoryNumber()+"]");
        //更新存量服务器单个的统计信息 cpu+memory
        stockService.setCpuNumberMath(-virtualMachine.getCpuNumber());
        stockService.setMemoryNumberMath(-virtualMachine.getMemoryNumber());

        HashMap<NodeType,ServiceNode> map = stockService.getNodes();
        //更新整体存量服务器的信息
        serviceInfo.setCpusNumberMath(-virtualMachine.getCpuNumber());
        serviceInfo.setMemoryNumberMath(-virtualMachine.getMemoryNumber());
        System.out.println("\t整体服务器 cpu["+serviceInfo.getCpusNumber()+"] memory["+serviceInfo.getMemoryNumber()+"]");

        if(virtualMachine.getType() == 0){
            //单节点 优先部署A节点
            if(map.get(NodeType.A).getCpuNumber() >= virtualMachine.getCpuNumber() &&
                    map.get(NodeType.A).getMemoryNumber() >= virtualMachine.getMemoryNumber()){
                map.get(NodeType.A).setCpuNumberMath(-virtualMachine.getCpuNumber());
                map.get(NodeType.A).setMemoryNumberMath(-virtualMachine.getMemoryNumber());
                return ArrangeType.A;
            }else{
                map.get(NodeType.B).setCpuNumberMath(-virtualMachine.getCpuNumber());
                map.get(NodeType.B).setMemoryNumberMath(-virtualMachine.getMemoryNumber());
                return ArrangeType.B;
            }
        }else{
            //双节点
            map.get(NodeType.A).setCpuNumberMath(-virtualMachine.getCpuNumber()/2);
            map.get(NodeType.A).setMemoryNumberMath(virtualMachine.getMemoryNumber()/2);
            map.get(NodeType.B).setCpuNumberMath(-virtualMachine.getCpuNumber()/2);
            map.get(NodeType.B).setMemoryNumberMath(virtualMachine.getMemoryNumber()/2);
            return ArrangeType.ALL;
        }
    }

    /**
     * 判断总容量是否满足
     * @param virtualMachine
     * @return
     */
    public boolean checkAllStockService(VirtualMachine virtualMachine){
        if(serviceInfo.getCpusNumber() < virtualMachine.getCpuNumber() || serviceInfo.getMemoryNumber() < virtualMachine.getMemoryNumber()){
            //如果存量服务器的Cpus/Memorys小于虚拟机要求的
            return false;
        }else{
            return true;
        }
    }

    /**
     * 检查存量服务器是否足够
     * @param virtualMachine
     * @return
     */
    public StockService checkStockService(VirtualMachine virtualMachine){
        if(checkAllStockService(virtualMachine)){
            //遍历一遍是否能够放入并找到
            StockService stockService = getStockService(virtualMachine);
            return stockService;
        }else{
            //如果存量服务器的Cpus/Memorys小于虚拟机要求的
            return null;
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
        start.dealDailyRequest(PublicDataPool.dailyRequests.get(0),0);
    }
}
