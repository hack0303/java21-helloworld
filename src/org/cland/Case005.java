package org.cland;

public class Case005 {

    /**
     * 4. 密封类（Sealed Classes，Java 17 正式）
     * 解决 Java 8 中类的继承 / 实现无限制的问题，开发者可显式指定哪些类能继承该类、哪些接口能实现该接口，提升代码的可维护性和封装性；
     * 核心关键字：sealed（修饰密封类 / 接口）、permits（指定允许的子类 / 实现类）、non-sealed（子类显式声明为非密封，允许继续被继承）；
     * */
    public static void main(String[] args) {
    }

    // 密封接口，仅允许A、B实现
    public sealed interface Shape permits Circle, Rectangle {
        double getArea();
    }
    // 允许的实现类（普通类，不可被继承）
    public final class Circle implements Shape {
        private double radius;
        @Override
        public double getArea() {
            return Math.PI * radius * radius;
        }
    }
    // 允许的实现类（非密封，可被继续继承）
    public non-sealed class Rectangle implements Shape {
        private double width;
        private double height;
        @Override
        public double getArea() {
            return width * height;
        }
    }
    // 密封类，仅允许Student、Teacher继承
    public sealed class Person permits Student, Teacher {
        private String name;
    }
    public final class Student extends Person {}
    public final class Teacher extends Person {}

}
