package org.cland;

public class Case003 {

    /**
     * （1）instanceof 模式匹配（Java 16 正式）
     * 解决 Java 8 中instanceof判断后需手动强制类型转换的冗余问题，判断 + 转换一步完成；
     * */
    public static void main(String[] args) {
        Object obj="str";
        if(obj instanceof String){
            String objStr= (String)obj;
            System.out.println(objStr);
        }
        if(obj instanceof String objStr){
            System.out.println(objStr);
        }
    }
}
