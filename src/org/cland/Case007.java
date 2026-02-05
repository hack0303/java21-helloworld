package org.cland;

import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;

public class Case007 {

    /**
     * 入结构化的作用域中，确保 “子任务完成，主线程才退出；任一子任务异常，所有相关任务都被终止”；
     * 核心类：StructuredTaskScope，支持两种模式：
     * ShutdownOnFailure：任一子任务失败，立即关闭作用域，终止所有子任务；
     * ShutdownOnSuccess：任一子任务成功，立即关闭作用域，终止所有子任务；
     *
     * */
    public static void main(String[] args){
        // 结构化并发：同时执行两个子任务，任一失败则全部终止
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // 提交子任务1：获取用户信息
            StructuredTaskScope.Subtask<Long> userFuture = scope.fork(() -> 1L);
            // 提交子任务2：获取用户订单
            StructuredTaskScope.Subtask<Long> orderFuture = scope.fork(() -> 2L);

            scope.join(); // 等待所有子任务完成
            scope.throwIfFailed(); // 若有子任务失败，抛出异常

            // 所有子任务成功，获取结果
            Long user = userFuture.get();
            Long orders = orderFuture.get();
            System.out.println("用户：" + user + "，订单：" + orders);
        } catch (Exception e) {
            // 统一处理所有子任务的异常
            e.printStackTrace();
        }

    }
}
