package org.cland;

public class Case004 {

    /**
     * 彻底重构 Java 8 中简陋的 switch 语法，支持类型匹配、值匹配、多案例合并，可直接返回值，解决传统 switch 的break穿透、类型限制问题；
     * 特性：支持任意类型（不再仅局限于基本类型 / 枚举 / String）、模式变量、箭头语法->（无需 break）；
     * */
    public static void main(String[] args) {
        System.out.println(getTypeJAVA8(1L));
        System.out.println(getTypeJAVA21(1L));
//        switch (Integer.valueOf(1)){
//            case Integer i -> "整形";
//            default -> "defalut";
//        }

    }

    // Java 8（传统switch，需break，仅支持有限类型）
    public static String getTypeJAVA8(Object obj) {
        String type;
        switch (obj) {
            case Integer i: type = "整数"; break;
            case String s: type = "字符串"; break;
            default: type = "未知";
        }
        return type;
    }
    // Java 21（switch模式匹配，直接返回，支持所有类型）
    public static String getTypeJAVA21(Object obj) {
        return switch (obj) {
            case Integer i -> "整数：" + i;
            case String s -> "字符串：" + s;
            case Double d -> "浮点数：" + d;
            default -> "未知类型";
        };
    }
}
