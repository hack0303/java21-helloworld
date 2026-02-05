package org.cland;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Case006 {

    /**
     * 1. 虚拟线程（Virtual Threads，Java 21 正式，核心特性）
     * Java 21 最重磅特性，解决传统平台线程（Platform Thread）的资源瓶颈：平台线程与操作系统内核线程 1:1 映射，创建成本高、数量受限（通常几千个），而虚拟线程是JVM 管理的轻量级线程，与内核线程 M:N 映射，支持百万级并发，创建、切换、销毁成本极低；
     * */
    public static void main(String[] args) {
        Thread.startVirtualThread(()-> {
            System.out.println("直接启动");
        });
        Thread.ofVirtual().name("test").unstarted(()->{
            System.out.println("未启动");
        });
        try(var pool = Executors.newVirtualThreadPerTaskExecutor()){
            for(var i=0;i<100_000_000;i++){
                int finalI = i;
                pool.execute(()->{
                    System.out.println(finalI);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
