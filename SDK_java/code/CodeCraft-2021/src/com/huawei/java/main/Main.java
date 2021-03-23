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
    }
}