package org.cland;

public class Case002 {

    //    文本块（Text Blocks，Java 15 预览，Java 17 正式）
    public static void main(String[] args) {
    // Java 8（繁琐的拼接+转义）
        String sql_ = "SELECT id, name FROM user \n" +
                "WHERE age > 18 \n" +
                "ORDER BY create_time DESC";
        System.out.println(sql_);
    // Java 21（文本块，简洁易读）
        String sql = """
                SELECT id, name FROM user
                WHERE age > 18
                ORDER BY create_time DESC
                """;
        var testBlockStr = """
                你好
                哈哈
                """;
    // 格式化文本块
        String info = String.format("""
                Name: %s
                Age: %d
                """, "Tom", 25);
        System.out.println(sql);

    }
}
