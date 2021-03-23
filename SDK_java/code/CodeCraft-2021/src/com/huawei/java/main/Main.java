package com.huawei.java.main;

import com.huawei.java.main.zx.Start;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // TODO: read standard input
        // TODO: process
        // TODO: write standard output
        // TODO: System.out.flush()
        Start start = new Start();
        try {
            start.begin();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Map<String,String> map = new TreeMap<>();
//        map.put("a","1");
//        Map<String,String> map2 = new TreeMap<>();
//        map2.put("a","2");
//        map.putAll(map2);
//        System.out.println(map);
    }
}