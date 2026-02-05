package org.cland;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Case001 {


    /**
     * var 局部变量类型推断（Java 10 引入，Java 11 标准化）
     *
     *
     */
    public static void main(String[] args) {
        // Java 8
        String str_ = "Java 8";
        List<String> list_ = new ArrayList<>();
        // Java 21
        var str = "Java 21"; // 推断为String
        var list = new ArrayList<String>(); // 推断为ArrayList<String>
        for (var num : Arrays.asList(1, 2, 3)) { // 循环中使用
            System.out.println(num);
        }
    }
}
