# Java 8 迁移 Java 21 核心新特性全解析
Java 21 是**长期支持版（LTS）**（继 Java 8、11 后的第三个 LTS 版本），从 Java 8 迁移至 Java 21 可享受语法简化、性能提升、并发增强、新工具链等全方位升级，以下按**语法特性、并发编程、集合增强、新工具/API、平台特性、废弃/移除内容**分类梳理核心特性，兼顾实用性和迁移重点：

## 一、核心语法简化特性（开发效率大幅提升）
### 1. var 局部变量类型推断（Java 10 引入，Java 11 标准化）
- 替代 Java 8 中重复的局部变量类型声明，编译器自动推断类型，代码更简洁；
- **限制**：仅适用于**局部变量**（方法内、for循环中），不可用于成员变量、方法参数、返回值；
- 示例：
  ```java
  // Java 8
  String str = "Java 8";
  List<String> list = new ArrayList<>();
  // Java 21
  var str = "Java 21"; // 推断为String
  var list = new ArrayList<String>(); // 推断为ArrayList<String>
  for (var num : Arrays.asList(1,2,3)) { // 循环中使用
      System.out.println(num);
  }
  ```

### 2. 文本块（Text Blocks，Java 15 预览，Java 17 正式）
- 解决 Java 8 中多行字符串（如JSON、SQL、HTML）需要拼接、转义`\n`/`"`的痛点，原生支持多行文本，自动处理换行和缩进；
- 语法：使用**三个双引号`"""`**包裹多行内容，支持`%s`/`%d`格式化和`\s`忽略多余空格；
- 示例：
  ```java
  // Java 8（繁琐的拼接+转义）
  String sql = "SELECT id, name FROM user " +
               "WHERE age > 18 " +
               "ORDER BY create_time DESC";
  // Java 21（文本块，简洁易读）
  String sql = """
      SELECT id, name FROM user
      WHERE age > 18
      ORDER BY create_time DESC
      """;
  // 格式化文本块
  String info = String.format("""
      Name: %s
      Age: %d
      """, "Tom", 25);
  ```

### 3. 模式匹配（Pattern Matching，分阶段引入，Java 21 增强）
#### （1）instanceof 模式匹配（Java 16 正式）
- 解决 Java 8 中`instanceof`判断后需**手动强制类型转换**的冗余问题，判断+转换一步完成；
- 示例：
  ```java
  // Java 8
  if (obj instanceof String) {
      String s = (String) obj; // 重复转换
      System.out.println(s.length());
  }
  // Java 21
  if (obj instanceof String s) { // 判断同时完成转换，s仅在分支内有效
      System.out.println(s.length());
  }
  ```

#### （2）switch 模式匹配（Java 21 正式，核心特性）
- 彻底重构 Java 8 中简陋的 switch 语法，支持**类型匹配、值匹配、多案例合并**，可直接返回值，解决传统 switch 的`break`穿透、类型限制问题；
- 特性：支持任意类型（不再仅局限于基本类型/枚举/String）、模式变量、箭头语法`->`（无需break）；
- 示例：
  ```java
  // Java 8（传统switch，需break，仅支持有限类型）
  public static String getType(Object obj) {
      String type;
      switch (obj) {
          case Integer i: type = "整数"; break;
          case String s: type = "字符串"; break;
          default: type = "未知";
      }
      return type;
  }
  // Java 21（switch模式匹配，直接返回，支持所有类型）
  public static String getType(Object obj) {
      return switch (obj) {
          case Integer i -> "整数：" + i;
          case String s -> "字符串：" + s;
          case Double d -> "浮点数：" + d;
          default -> "未知类型";
      };
  }
  ```

### 4. 密封类（Sealed Classes，Java 17 正式）
- 解决 Java 8 中类的继承/实现**无限制**的问题，开发者可**显式指定**哪些类能继承该类、哪些接口能实现该接口，提升代码的可维护性和封装性；
- 核心关键字：`sealed`（修饰密封类/接口）、`permits`（指定允许的子类/实现类）、`non-sealed`（子类显式声明为非密封，允许继续被继承）；
- 示例：
  ```java
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
  ```

## 二、并发编程增强（性能和易用性双提升）
### 1. 虚拟线程（Virtual Threads，Java 21 正式，核心特性）
- **Java 21 最重磅特性**，解决传统平台线程（Platform Thread）的资源瓶颈：平台线程与操作系统内核线程1:1映射，创建成本高、数量受限（通常几千个），而虚拟线程是**JVM 管理的轻量级线程**，与内核线程M:N映射，支持**百万级并发**，创建、切换、销毁成本极低；
- 适用场景：IO密集型应用（如微服务、接口调用、数据库操作、网络请求），无需修改业务代码即可将并发能力提升几个数量级；
- 核心优势：
    1. 轻量级：单个JVM可创建百万级虚拟线程，内存占用仅几KB；
    2. 无侵入：兼容现有`Runnable`/`Callable`接口，可直接替换平台线程；
    3. 自动挂起：IO操作时虚拟线程自动挂起，不占用内核线程资源；
- 示例（三种创建方式）：
  ```java
  // 方式1：直接启动虚拟线程
  Thread.startVirtualThread(() -> {
      System.out.println("虚拟线程执行：" + Thread.currentThread().getName());
      // 模拟IO操作（数据库/网络请求）
      try { Thread.sleep(1000); } catch (InterruptedException e) {}
  });
  // 方式2：通过ThreadBuilder创建
  Thread vt = Thread.ofVirtual().name("my-virtual-thread-1").unstarted(() -> {
      // 业务逻辑
  });
  vt.start();
  // 方式3：结合线程池（推荐，管理虚拟线程生命周期）
  ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
  // 提交100万个任务，无资源压力
  for (int i = 0; i < 1_000_000; i++) {
      executor.submit(() -> {
          Thread.sleep(1000);
          return i;
      });
  }
  ```

### 2. 结构化并发（Structured Concurrency，Java 21 预览）
- 解决传统并发中**线程管理混乱**的问题（如子线程异常未捕获、主线程提前退出、资源泄漏），将多线程任务纳入**结构化的作用域**中，确保“子任务完成，主线程才退出；任一子任务异常，所有相关任务都被终止”；
- 核心类：`StructuredTaskScope`，支持两种模式：
    - `ShutdownOnFailure`：任一子任务失败，立即关闭作用域，终止所有子任务；
    - `ShutdownOnSuccess`：任一子任务成功，立即关闭作用域，终止所有子任务；
- 示例：
  ```java
  // 结构化并发：同时执行两个子任务，任一失败则全部终止
  try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      // 提交子任务1：获取用户信息
      Future<User> userFuture = scope.fork(() -> userService.getUserById(1));
      // 提交子任务2：获取用户订单
      Future<List<Order>> orderFuture = scope.fork(() -> orderService.getOrdersByUserId(1));
      
      scope.join(); // 等待所有子任务完成
      scope.throwIfFailed(); // 若有子任务失败，抛出异常
      
      // 所有子任务成功，获取结果
      User user = userFuture.resultNow();
      List<Order> orders = orderFuture.resultNow();
      System.out.println("用户：" + user + "，订单：" + orders);
  } catch (Exception e) {
      // 统一处理所有子任务的异常
      e.printStackTrace();
  }
  ```

### 3. Stream API 增强（Java 9+ 逐步完善）
Java 8 引入的 Stream API 在后续版本中持续增强，补充了多个实用方法，简化流式处理：
1. `Stream.ofNullable(T t)`：支持传入null，避免空指针（Java 9）；
   ```java
   // Java 8：需手动判断null，否则报NPE
   Stream<String> stream = str == null ? Stream.empty() : Stream.of(str);
   // Java 21：直接支持null
   Stream<String> stream = Stream.ofNullable(str);
   ```
2. `Stream.takeWhile(Predicate p)`/`dropWhile(Predicate p)`：按条件截取/丢弃元素（Java 9）；
   ```java
   // takeWhile：取满足条件的元素，直到第一个不满足的元素（有序流）
   List<Integer> list = Arrays.asList(1,2,3,4,5);
   list.stream().takeWhile(n -> n < 4).forEach(System.out::print); // 输出123
   // dropWhile：丢弃满足条件的元素，直到第一个不满足的元素
   list.stream().dropWhile(n -> n < 4).forEach(System.out::print); // 输出45
   ```
3. `Collectors.teeing(Collector a, Collector b, BiFunction f)`：将流分成两个分支，分别用不同收集器处理，最后合并结果（Java 12）；
   ```java
   // 一次流处理：同时统计列表的最大值和最小值
   Map<String, Integer> result = list.stream()
       .collect(Collectors.teeing(
           Collectors.maxBy(Integer::compare), // 收集器1：找最大值
           Collectors.minBy(Integer::compare), // 收集器2：找最小值
           (max, min) -> {
               Map<String, Integer> map = new HashMap<>();
               map.put("max", max.orElse(0));
               map.put("min", min.orElse(0));
               return map;
           }
       ));
   ```

## 三、集合框架增强（便捷性提升）
### 1. 不可变集合工厂方法（Java 9 正式）
Java 8 中创建不可变集合需通过`Collections.unmodifiableXXX()`，底层仍为可变集合，且代码繁琐；Java 9 引入**原生不可变集合工厂方法**，直接创建不可变、不可修改、线程安全的集合，性能更优；
- 语法：`List.of()`、`Set.of()`、`Map.of()`/`Map.ofEntries()`；
- 特性：不允许null元素（Map的key和value都不允许）、大小固定、支持批量创建；
- 示例：
  ```java
  // Java 8（繁琐的不可变集合创建）
  List<String> immutableList = Collections.unmodifiableList(new ArrayList<>(Arrays.asList("a","b","c")));
  Map<String, Integer> immutableMap = Collections.unmodifiableMap(new HashMap<String, Integer>() {{
      put("a",1);
      put("b",2);
  }});
  // Java 21（原生工厂方法，简洁高效）
  List<String> immutableList = List.of("a", "b", "c");
  Set<Integer> immutableSet = Set.of(1,2,3);
  Map<String, Integer> immutableMap1 = Map.of("a",1, "b",2); // 适用于少量键值对
  Map<String, Integer> immutableMap2 = Map.ofEntries( // 适用于大量键值对
      Map.entry("a",1),
      Map.entry("b",2),
      Map.entry("c",3)
  );
  ```

### 2. 集合迭代器增强（Java 9）
`Iterator`新增`forEachRemaining(Consumer action)`方法，可一次性遍历剩余所有元素，简化迭代代码：
```java
// Java 8
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    System.out.println(it.next());
}
// Java 21
Iterator<String> it = list.iterator();
it.forEachRemaining(System.out::println); // 一次性遍历剩余元素
```

## 四、新工具与核心API增强
### 1. HttpClient 异步HTTP客户端（Java 11 正式）
替代 Java 8 中老旧的`HttpURLConnection`（同步、API繁琐、性能差），提供**同步+异步**两种请求方式，支持HTTP/1.1、HTTP/2、HTTPS，API简洁且性能优异；
- 核心特性：异步非阻塞、支持请求/响应拦截、超时配置、文件上传下载；
- 示例（异步GET请求）：
  ```java
  // Java 21 异步HTTP请求
  HttpClient client = HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2) // 启用HTTP/2
          .connectTimeout(Duration.ofSeconds(5)) // 连接超时
          .build();
  // 构建请求
  HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://www.baidu.com"))
          .GET()
          .build();
  // 异步发送请求
  client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(HttpResponse::body) // 处理响应体
          .thenAccept(System.out::println) // 打印结果
          .exceptionally(e -> { // 异常处理
              e.printStackTrace();
              return null;
          });
  // 主线程等待，避免程序提前退出
  Thread.sleep(3000);
  ```

### 2. 模块系统（Module System，Java 9 正式，Project Jigsaw）
Java 9 引入的重大架构变更，将JDK和应用程序拆分为**模块**（Module），每个模块显式声明**导出的包**（对外暴露的API）和**依赖的模块**，解决了Java 8中“类路径地狱”（Classpath Hell）、JDK体积过大的问题；
- 核心优势：
    1. 封装性：模块内的非导出包对外不可见，避免API被非法调用；
    2. 依赖管理：显式声明模块依赖，简化项目配置；
    3. 轻量化：可构建自定义JRE（`jlink`工具），仅包含应用所需的模块，大幅减小部署包体积；
- 核心文件：`module-info.java`（模块描述文件，放在源码根目录）；
- 示例：
  ```java
  // 定义一个模块：com.example.demo
  module com.example.demo {
      // 导出对外暴露的包（其他模块可访问）
      exports com.example.demo.service;
      exports com.example.demo.controller;
      // 依赖其他模块（JDK内置模块）
      requires java.base; // 所有模块默认依赖，可省略
      requires java.net.http; // 依赖HTTP客户端模块
      requires java.sql; // 依赖JDBC模块
      // 依赖第三方模块（如Spring）
      requires spring.context;
  }
  ```

### 3. jlink 工具：自定义轻量级JRE
基于模块系统，`jlink`可将**应用所需的JDK模块**打包为自定义JRE，体积远小于完整JDK（可从几百MB减小到几十MB），大幅简化应用部署（无需目标机器安装完整JDK）；
- 核心命令：
  ```bash
  # 生成自定义JRE，仅包含java.base、java.sql、java.net.http模块
  jlink --module-path $JAVA_HOME/jmods --add-modules java.base,java.sql,java.net.http --output my-jre
  ```
- 优势：跨平台、体积小、启动快、安全性高（仅包含必要模块，减少攻击面）。

### 4. 其他API增强
1. `Optional`增强（Java 9+）：新增`or(Supplier)`、`ifPresentOrElse(Consumer, Runnable)`、`stream()`方法，简化空值处理；
   ```java
   Optional<String> opt = Optional.ofNullable(null);
   // or：若为空，返回指定Optional
   opt.or(() -> Optional.of("default")).ifPresent(System.out::println); // 输出default
   // ifPresentOrElse：有值则消费，无值则执行兜底逻辑
   opt.ifPresentOrElse(s -> System.out.println("值：" + s), () -> System.out.println("无值"));
   // stream：将Optional转为Stream，方便流式处理
   opt.stream().forEach(System.out::println);
   ```
2. `Date/Time` API 完善：Java 8 引入的新时间API（`java.time`）在后续版本中补充了更多实用方法，如`Duration.between()`、`LocalDate.plusWeeks()`等，彻底替代老旧的`Date`/`Calendar`；
3. 数字类型增强：`Integer`/`Long`新增`parseUnsignedInt()`/`toUnsignedLong()`方法，支持无符号数字处理（Java 8 部分支持，Java 9+完善）。

## 五、平台与性能特性
### 1. ZGC 垃圾收集器（Java 15 正式，Java 21 优化）
- 一款**低延迟垃圾收集器**，目标是“**停顿时间不超过10ms**”，支持TB级堆内存，适用于高并发、低延迟的生产环境（如微服务、电商、金融）；
- 相比 Java 8 的 G1GC，ZGC 停顿时间大幅降低，且堆内存越大，优势越明显；
- 启用方式：JVM参数`-XX:+UseZGC`（Java 21 中无需额外解锁，直接启用）。

### 2. Shenandoah GC 垃圾收集器（Java 17 正式）
- 另一款低延迟GC，与ZGC目标类似，采用**并发压缩**算法，停顿时间独立于堆大小，适用于对延迟要求极高的场景；
- 启用方式：JVM参数`-XX:+UseShenandoahGC`。

### 3. 即时编译优化（JIT）
Java 21 对 C2 编译器进行了大量优化，同时引入了**AOT编译（jaotc）** 支持（Java 9 引入，后续逐步完善），可将Java字节码编译为本地机器码，提升应用启动速度和运行性能。

## 六、废弃与移除的内容（迁移重点关注）
从 Java 8 迁移至 Java 21，需注意以下被**废弃（Deprecated）** 或**永久移除**的内容，避免使用：
1. 永久移除：`Applet API`（Java 11 移除）、`CORBA API`（Java 11 移除）、`Java EE`相关模块（`java.xml.ws`、`java.xml.bind`等，Java 11 移除，可通过Maven/Gradle引入第三方依赖）；
2. 废弃：`Finalizer`（Java 9 废弃，Java 21 标记为将被移除）、`System.runFinalizersOnExit()`、`Runtime.runFinalizersOnExit()`；
3. 其他：`sun.misc.Unsafe`部分方法被废弃，推荐使用JDK官方提供的替代API；`Thread.stop()`/`Thread.suspend()`等危险方法仍被标记为废弃，不可使用。

## 七、迁移关键注意事项
1. **LTS 版本选择**：Java 21 是LTS版本，支持至2031年，与Java 8（2030年停止免费更新）、Java 11（2026年停止免费更新）相比，生命周期更长，适合长期项目迁移；
2. **模块系统适配**：若项目需构建自定义JRE或适配模块系统，需添加`module-info.java`，显式声明导出包和依赖；
3. **第三方依赖兼容**：确保项目使用的第三方框架/依赖（如Spring、MyBatis、Netty）支持Java 21（主流框架均已支持）；
4. **虚拟线程使用场景**：虚拟线程适合IO密集型场景，**不适合CPU密集型场景**（CPU密集型场景仍推荐使用平台线程池，控制核心线程数为CPU核心数）；
5. **废弃API替换**：替换Java 8中老旧的API（如`HttpURLConnection`→`HttpClient`、`Date`→`java.time`、`Collections.unmodifiableXXX`→`List/Set/Map.of()`）。

## 核心特性总结
| 特性分类       | 核心特性                     | 引入版本 | 核心价值                     |
|----------------|------------------------------|----------|------------------------------|
| 语法简化       | var类型推断、文本块          | 10/17    | 减少样板代码，提升开发效率   |
| 语法简化       | instanceof/switch模式匹配    | 16/21    | 简化类型判断，避免冗余转换   |
| 类设计         | 密封类                       | 17       | 限制继承/实现，提升封装性    |
| 并发编程       | 虚拟线程                     | 21       | 百万级并发，解决IO密集型瓶颈 |
| 并发编程       | 结构化并发                   | 21（预览）| 规范线程管理，避免资源泄漏   |
| 集合框架       | 不可变集合工厂方法           | 9        | 简洁创建不可变、线程安全集合 |
| 网络编程       | 新HttpClient                 | 11       | 同步+异步，支持HTTP/2        |
| 架构设计       | 模块系统                     | 9        | 解决类路径地狱，支持自定义JRE|
| 性能优化       | ZGC/Shenandoah GC            | 15/17    | 低延迟GC，支持TB级堆内存     |

Java 21 是Java生态的一次重大升级，从Java 8迁移后，不仅能享受**语法上的简洁性**，更能通过**虚拟线程、模块系统、低延迟GC**获得**性能和可维护性的双重提升**，是企业级项目长期发展的最佳选择。