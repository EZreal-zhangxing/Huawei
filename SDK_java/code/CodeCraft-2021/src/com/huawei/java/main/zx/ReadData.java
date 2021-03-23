package com.huawei.java.main.zx;


import com.huawei.java.main.Pojo.*;

import java.io.*;

/**
 * @author zhangxing
 * @Date 2021/3/18
 */
public class ReadData {

    public static final String fileDir = "/Users/zhangxing/workspace/SDK_java/code/CodeCraft-2021/src/com/huawei/java/main/Data/";

    public static final String filename = "training-1.txt";

    public void getData(){
        int days=0;
        try {
//            BufferedReader reader = new BufferedReader(new FileReader(new File(fileDir+filename)));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            int countLine=0;//行数计数
            int lineSize = 1;//局部行数控制
            int step = -1; //step == 0 构建服务器 ==1 构建虚拟机 ==2 构建用户请求
            int day=0; //一共有多少天的请求
            int vmnum=0,senum=0,usernum=0;
            DailyRequest dailyRequest=null;
            while((line = reader.readLine().replaceAll(" ",""))!= null && !line.equals("")){
                countLine++;
                if(countLine == lineSize){
                    if(step<1){
                        lineSize = countLine + Integer.parseInt(line)+1;
                        step++;
                    }else{
                        if(days == 0){
                            days = Integer.parseInt(line);
                            int daynum = Integer.parseInt(reader.readLine());
                            lineSize = countLine + daynum +1;
                            ++day;step++;
                            dailyRequest = new DailyRequest();
                            dailyRequest.setNum(daynum);
                            dailyRequest.setDay(day);
                        }else{
                            //把前一天的数据放入数据池
                            PublicDataPool.dailyRequests.add(dailyRequest);
                            ++day;
                            int daynum = Integer.parseInt(line);
                            lineSize = countLine + daynum+1;
                            dailyRequest = new DailyRequest();
                            dailyRequest.setNum(daynum);
                            dailyRequest.setDay(day);
                        }
                    }
                    continue;
                }
                String[] data= dealString(line);
                if(step == 0){
                    //构建服务器列表
                    ServiceMachine serviceMachine = new ServiceMachine();
                    serviceMachine.setId(++senum);
                    serviceMachine.setServiceName(data[0]);
                    serviceMachine.setCpuNumber(Integer.parseInt(data[1]));
                    serviceMachine.setMemoryNumber(Integer.parseInt(data[2]));
                    serviceMachine.setCost(Integer.parseInt(data[3]));
                    serviceMachine.setDailyCost(Integer.parseInt(data[4]));
                    /**
                     * 性价比
                     * （硬件成本）/（核心数+内存数）
                     */
                    serviceMachine.flushCostPriceRate(days,day);
                    PublicDataPool.servicesToSale.add(serviceMachine);
                }
                if(step == 1){
                    //构建虚拟机列表
                    VirtualMachine virtualMachine = new VirtualMachine();
                    virtualMachine.setVmName(data[0]);
                    virtualMachine.setCpuNumber(Integer.parseInt(data[1]));
                    virtualMachine.setMemoryNumber(Integer.parseInt(data[2]));
                    virtualMachine.setType(Integer.parseInt(data[3]));
                    PublicDataPool.virtualToSale.add(virtualMachine);
                }
                if(step == 2){

                    //构建用户请求
                    UserRequest userRequest = new UserRequest();
                    userRequest.setOperationType(data[0]);
                    if(RequestEnum.ADD.getCode().equals(data[0])){
                        userRequest.setVirtualMachineType(data[1]);
                        userRequest.setVirtualMachineId(Integer.parseInt(data[2]));
                    }else{
                        userRequest.setVirtualMachineId(Integer.parseInt(data[1]));
                    }
                    dailyRequest.getRequests().add(userRequest);
                    if(day == days && countLine == lineSize-1){
                        break;
                    }
                }
            }
            if(day == days){
                //把当天生成的数据放入数据池
                PublicDataPool.dailyRequests.add(dailyRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PublicDataPool.allDays = days;
    }

    public String[] dealString(String line){
        line = line.substring(1,line.length()-1);
        return line.replaceAll(" ","").split(",");
    }

//    public static void main(String[] args) {
//        ReadData readData = new ReadData();
//
//        readData.getData();
//        System.out.println("可购买服务器列表size:["+PublicDataPool.servicesToSale.size()+"]");
//        System.out.println("可购买虚拟机列表size:["+PublicDataPool.virtualToSale.size()+"]");
//        System.out.println("每日请求列表size:["+PublicDataPool.dailyRequests.size()+"]");
//        PublicDataPool.dailyRequests.forEach(item ->{
//            System.out.println("\t["+(PublicDataPool.dailyRequests.indexOf(item)+1)+"]日请求量为：["+item.getRequests().size()+"]");
//        });
//    }
}
