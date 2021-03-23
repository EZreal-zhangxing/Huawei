package com.huawei.java.main.Pojo;

import com.huawei.java.main.zx.Start;

import java.util.List;
import java.util.Stack;

/**
 * @author zhangxing
 * @Date 2021/3/20
 * 工具类
 */
public class Utils {

    /**
     * 为了方便后后续虚拟机的查询 此处给与了一个ID值 可以设置查找方法，方便虚拟机的查找
     * @param vmName
     * @return value 字符串的ASCII码
     */
    public static int  getVmid(String vmName){
        int value = 0;
        char[] chars= vmName.toCharArray();
        for(char item : chars){
            value += item;
        }
        return value;
    }

    public static VirtualMachine getVirtualMachine(String vmName){
        List<VirtualMachine> machineList = PublicDataPool.virtualToSale;
        int left=0,right=machineList.size()-1,mid=0;
        while(left<=right){
            mid = (left+right)/2;
            VirtualMachine virtualMachine = machineList.get(mid);
            if(virtualMachine.getVmName().compareTo(vmName) == 0){
                return machineList.get(mid);
            }else if(virtualMachine.getVmName().compareTo(vmName) < 0){
                left = mid + 1;
            }else{
                right = mid - 1;
            }
        }
        return machineList.get(mid);
    }

//    public static void main(String[] args) {
//        Start start = new Start();
//        start.initData();
//        System.out.println(getVirtualMachine("vmD2KDE"));
//    }
}
