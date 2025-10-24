# Pear 单体应用快速开发框架

<div align="center">
    <img src="favicon.svg" alt="Pear Logo" width="200" height="200">
    <h1>Pear Spring Boot Starter</h1>
    <p>🚀 专为Java开发者设计的单体应用快速开发框架</p>
    <p>
        <a href="README.md">中文</a> | <a href="README_EN.md">English</a>
    </p>
    <p>
        <a href="https://spring.io/projects/spring-boot">
            <img src="https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen.svg" alt="Spring Boot">
        </a>
        <a href="https://www.oracle.com/java/">
            <img src="https://img.shields.io/badge/JDK-17+-green.svg" alt="JDK">
        </a>
        <a href="https://mybatis.org/">
            <img src="https://img.shields.io/badge/MyBatis-3.5.x-yellow.svg" alt="MyBatis">
        </a>
        <a href="https://maven.apache.org/">
            <img src="https://img.shields.io/badge/Maven-3.9+-purple.svg" alt="Maven">
        </a>
        <a href="https://github.com/MuziSuper/pear-spring-boot-starter/releases">
            <img src="https://img.shields.io/github/v/release/MuziSuper/pear-spring-boot-starter" alt="Release">
        </a>
        <a href="https://github.com/MuziSuper/pear-spring-boot-starter/blob/main/LICENSE">
            <img src="https://img.shields.io/github/license/MuziSuper/pear-spring-boot-starter" alt="License">
        </a>
    </p>
    <p>
        <a href="https://github.com/MuziSuper/pear-spring-boot-starter/stargazers">
            <img src="https://img.shields.io/github/stars/MuziSuper/pear-spring-boot-starter?style=social" alt="GitHub stars">
        </a>
        <a href="https://github.com/MuziSuper/pear-spring-boot-starter/network">
            <img src="https://img.shields.io/github/forks/MuziSuper/pear-spring-boot-starter?style=social" alt="GitHub forks">
        </a>
    </p>
</div>

## ✨ 特性

- 🛠️ **开箱即用**: 快速集成到Spring Boot项目中
- 🎨 **可视化后台**: 内置美观的管理界面
- ⚡ **高效开发**: 通过注解自动生成CRUD接口
- 🔄 **钩子机制**: 支持数据处理前后自定义逻辑
- 📊 **日志系统**: 多级别日志记录与归档
- 🔥 **缓存系统**: 支持LRU与LFU缓存策略与Redis模式
- 🔐 **权限控制**: 用户态和系统态权限分级校验
- 🧠 **智能配置**: 支持properties文件与配置类双模式配置

## 📦 快速开始

### Maven依赖

```properties
<dependency>
  <groupId>cn.muzisheng.pear</groupId>
  <artifactId>pear-spring-boot-starter</artifactId>
  <version>1.3.1</version>
</dependency>
```

## 🖥️ 界面预览

| 登录页面 | 注册页面 | 仪表盘 |
|---------|---------|--------|
| ![登录页面](login.png) | ![注册页面](register.png) | ![仪表盘](dashboard.png) |

**默认访问路径:**
- 登录: `http://localhost:8080/auth/login`
- 注册: `http://localhost:8080/auth/register`
- 仪表盘: `http://localhost:8080/auth/dashboard`

## 📚 使用指南

### 启用Pear
1. 通过在主启动类上添加`@PearApplication`注解启用Pear的所有条件化配置；
```java
@SpringBootApplication
@PearApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
2. 在properties配置文件中添加`spring.jpa.hibernate.ddl-auto= update`与MySQL数据库配置，会自动在该MySQL库中创建多个框架需要使用到的基础表结构；

### 实体类定义

使用[@PearObject](file:///Applications/LocalGit/pear-spring-boot-starter/pear-spring-boot-core/src/main/java/cn/muzisheng/pear/annotation/PearObject.java#L7-L46)和[@PearField](file:///Applications/LocalGit/pear-spring-boot-starter/pear-spring-boot-core/src/main/java/cn/muzisheng/pear/annotation/PearField.java#L9-L62)注解定义实体类，框架会自动生成对应的实体的增删改查接口，并在后台页面中提供数据管理，根据配置信息对应字段会有相应的状态。
```java
package cn.muzisheng.pear.example;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 示例类展示如何使用@PearObject和@PearField注解
 */
@Data
@Component
@TableName("example_demo")
@PearObject(
    TableName = "custom_table_name",  // 自定义表名
    group = "example",               // 分组
    desc = "这是一个示例类，用于展示Pear注解的使用",  // 描述
    path = "/example/demo",          // 访问路径
    editPage = "/demo/edit",         // 编辑页面地址
    listPage = "/demo/list",         // 列表页面地址
    pluralName = "demos",            // 复数名称
    iconUrl = "/icons/demo.png",     // 图标URL
    isInvisible = false              // 是否隐藏
)
public class ExampleDemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(
        isPrimaryKey = true,         // 主键
        isShow = true,               // 显示
        isEdit = false,              // 不可编辑
        isFilterable = true,         // 可过滤
        isOrderable = true,          // 可排序
        isSearchable = true,         // 可搜索
        isRequire = true,            // 必填
        placeholder = "自动生成ID",    // 提示信息
        label = "ID"                 // 显示标签
    )
    private Long id;

    @TableField(value = "demo_name")
    @PearField(
        isShow = true,
        isEdit = true,
        isFilterable = true,
        isOrderable = true,
        isSearchable = true,
        isRequire = true,
        placeholder = "请输入名称",
        label = "名称",
        isUniqueKey = true           // 唯一键
    )
    private String name;

    @PearField(
        isShow = true,
        isEdit = true,
        isFilterable = false,
        isOrderable = false,
        isSearchable = true,
        isRequire = false,
        placeholder = "请输入描述",
        label = "描述"
    )
    private String description;

    @PearField(
        isShow = true,
        isEdit = true,
        isFilterable = true,
        isOrderable = true,
        isSearchable = false,
        isRequire = false,
        placeholder = "是否激活",
        label = "状态"
    )
    private Boolean active;

    @TableField(fill = FieldFill.INSERT, value = "create_time")
    @PearField(
        isShow = true,
        isEdit = false,
        isAutoInsertTime = true,     // 自动插入时间
        label = "创建时间"
    )
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    @PearField(
        isShow = true,
        isEdit = false,
        isAutoUpdateTime = true,     // 自动更新时间
        label = "更新时间"
    )
    private LocalDateTime updateTime;

    @Transient
    @PearField(
        isShow = false,              // 不显示
        isEdit = false,              // 不可编辑
        notColumn = true             // 非数据库字段
    )
    private String tempField;
}
```


### 注解说明
#### PearObject 注解

PearObject 是一个用于标注实体类的注解，用于定义与数据库表相关的元数据信息。

#### 功能说明
- 标记实体类为 Pear 框架管理的对象
- 定义实体对应的数据库表名、访问路径等配置
- 支持配置前端展示相关属性，如页面地址、图标等

#### 属性列表

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| TableName | String | "" | 数据库表名 |
| group | String | "" | 模型分组 |
| desc | String | "" | 模型描述 |
| path | String | "" | 访问路径 |
| editPage | String | "" | 编辑页面地址 |
| listPage | String | "" | 列表页面地址 |
| pluralName | String | "" | 复数名称 |
| iconUrl | String | "" | 图URL |
| isInvisible | boolean | false | 是否隐藏 |

---

#### PearField 注解

PearField 是一个用于标注实体类字段的注解，用于定义字段在数据库和前端展示中的行为，必须依托PearObject 注解。

#### 功能说明
- 定义字段在数据库操作和前端展示中的各种属性
- 控制字段的显示、编辑、搜索等行为
- 支持自动时间戳功能

#### 属性列表

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| isShow | boolean | true | 是否显示 |
| isEdit | boolean | true | 是否可编辑 |
| isFilterable | boolean | true | 是否可过滤 |
| isOrderable | boolean | true | 是否可排序 |
| isSearchable | boolean | true | 是否可搜索 |
| isRequire | boolean | true | 是否必填 |
| isPrimaryKey | boolean | false | 是否主键 |
| isUniqueKey | boolean | false | 是否唯一键 |
| placeholder | String | "" | 默认值 |
| isAutoUpdateTime | boolean | false | 是否自动更新时间 |
| isAutoInsertTime | boolean | false | 是否自动插入时间 |
| label | String | "" | 客户端显示名称 |
### 初始化配置
当实体类被@PearObject注解，其类与字段信息就会封装成一个AdminObject对象，存储在AdminContainer容器中，可以在项目CommandLineRunner初始化时通过AdminObject.BuilderFactory对象对某个实体类的AdminObject对象进一步配置，如添加前后置钩子函数等：

```java
@Component
public class PearInitializer implements CommandLineRunner {

  @Override
  public void run(String... args) {
    // 1. 获取基础模型
    List<AdminObject> adminObjects = AdminContainer.getAllAdminObjects();
    
    // 2. 配置自定义模型
    AdminObject.BuilderFactory builder = new AdminObject.BuilderFactory(Product.class)
      .setBeforeCreate((request, product) -> {
        // 创建前逻辑
        product.setCreatedAt(LocalDateTime.now());
        return product;
      })
      .setBeforeUpdate((request, product) -> {
        // 更新前逻辑
        product.setUpdatedAt(LocalDateTime.now());
        return product;
      })
      .setOrder(new Order("createdAt", Constant.ORDER_OP_DESC));
    
    // 3. 构建所有模型
    AdminContainer.buildAdminObjects(adminObjects);
  }
}
```


## ⚙️ 系统配置

### 配置方式说明

Pear支持多种配置方式，包括：

1. **properties文件配置**：通过application.properties进行传统配置
2. **配置类配置**：通过Java配置类进行类型安全的配置

### properties配置示例
```properties
# ==================================
# 缓存配置 (Cache)
# ==================================
app.cacheFactory.expire=86400000  # 缓存过期时间(毫秒)，默认24小时
app.cacheFactory.capacity=20      # 缓存容量，默认20
app.cacheFactory.strategy=LRU     # 缓存策略，默认LRU

# ==================================
# 环境配置 (Site)
# ==================================
app.site.auth-prefix=/auth         # 认证路径前缀
app.site.login-url=/auth/login     # 登录页面URL
app.site.dashboard-url=/auth/dashboard  # 仪表盘URL

# ==================================
# 日志配置 (Log)
# ==================================
app.log.level=INFO                 # 日志级别
app.log.stdout-pattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %-17black(%thread) %-82green(%logger{70}-%line) %highlight(%msg){black} %highlight(%ex){red} \n  # 标准输出格式
app.log.file-pattern=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n  # 文件输出格式
app.log.file-path=log              # 日志文件路径
app.log.log-catalogue-address=log/log-day    # 日志目录
app.log.warn-catalogue-address=log/warn-day  # 警告日志目录
app.log.error-catalogue-address=log/error-day  # 错误日志目录

# ==================================
# Token配置
# ==================================
app.token.salt=pear_token_         # Token盐值
app.token.head=Bearer              # Token头前缀
app.token.expire=604800000         # Token过期时间(毫秒)，默认7天
app.token.issue=pear               # Token签发者
app.token.subject=authentication   # Token主题

# ==================================
# 用户密码配置
# ==================================
app.user.password.salt=PEAR_APPLICATION_SALT  # 密码加密盐值
```

### 配置类配置示例

```java
@Configuration
public class PearConfig {
  
    @Bean
    public CacheConfig cacheConfig() {
        return CacheConfig.builder()
            .expire(3600000)    // 1小时
            .capacity(500)      // 容量500
            .strategy(CacheStrategyEnum.LFU)  // 使用LFU策略
            .build();
    }
  
    @Bean
    public TokenConfig tokenConfig() {
        return TokenConfig.builder()
            .salt("custom_salt")
            .expire(86400000L)  // 24小时
            .head("Bearer ")
            .issue("pear_admin")
            .subject("auth")
            .build();
    }
  
    @Bean
    public UserConfig userConfig() {
        return UserConfig.builder()
            .passwordSalt("user_custom_salt")
            .build();
    }
    
    @Bean
    public SiteConfig siteConfig() {
        return SiteConfig.builder()
            .authPrefix("/admin")
            .loginUrl("/admin/login")
            .dashboardUrl("/admin/dashboard")
            .build();
    }
    
    @Bean
    public LogConfig logConfig() {
        return LogConfig.builder()
            .level("DEBUG")
            .stdoutPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n")
            .filePath("logs")
            .build();
    }
}

```


## 🏗️ 系统架构

### 后端架构

- **核心框架**: Spring Boot 3.3.x
- **ORM**: MyBatis-Plus 3.5.x
- **认证**: JWT Token
- **日志**: Logback
- **缓存**: 内置LRU/LFU缓存(可扩展Redis)

## 📌 系统特性详解

### 多策略缓存系统

Pear实现了基于策略模式的缓存系统，支持LRU和LFU两种缓存淘汰算法：

1. **LRU(最近最少使用)**：基于LinkedHashMap实现，适合访问具有时间局部性的场景
2. **LFU(最不经常使用)**：基于TreeMap和LinkedHashSet实现，基于访问频率进行淘汰
3. **Redis**: 基于Redis实现，缓存淘汰策略自动跟随Redis配置

```java
// 缓存接口定义
public interface CacheInterface<K,V> {
  V get(K key);
  void put(K key, V value);
  void remove(K key);
  int size();
  void clear();
  boolean containsKey(K key);
}

// 缓存策略模式
public class CacheStrategy<K, V> {
  private CacheInterface<K, V> cacheFactory;
  
  public void setCacheStrategy(CacheInterface<K, V> cacheFactory) {
    this.cacheFactory = cacheFactory;
  }
  
  // ...其他方法...
}

// LRU缓存实现
public class LRUCacheUtil<K,V> implements CacheInterface<K,V> {
  // 基于LinkedHashMap实现
}

// LFU缓存实现
public class LFUCacheUtil<K,V> implements CacheInterface<K,V> {
  // 基于TreeMap和LinkedHashSet实现
}
```


### 权限控制
Pear框架实现了基于角色的权限控制(RBAC)系统，采用双层权限校验机制：

#### 权限架构
- 系统级权限校验：基于用户角色和预定义权限进行校验
- 用户级权限校验：通过自定义钩子函数实现灵活的权限控制
#### 核心组件
- Permission(权限)：最小的权限单元，定义具体的操作权限
- Role(角色)：权限的集合，可分配给用户
- User(用户)：系统用户，通过角色获得相应权限
#### 权限校验机制
Pear框架通过@Verification注解实现权限控制：
```java
// 权限注解定义
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Verification {
    // 是否启用系统级权限校验，默认为启用
    boolean SystemVerify() default true;
    // 是否启用用户级权限校验，默认为启用
    boolean UserVerify() default true;
    // 权限代码
    String Permission() default Constant.ADMIN_NULL;
}

// 使用示例
@Verification(Permission = Constant.ADMIN_UPDATE)
public ResponseEntity<Result<Map<String, Object>>> handleUpdate(...) {
    // 方法实现
}

```
#### 内置权限类型
框架预定义了以下权限类型：
- ADMIN_CREATE：创建权限
- ADMIN_DELETE：删除权限
- ADMIN_UPDATE：更新权限
- ADMIN_SELECT：查询权限
- PERMISSION_WRITER：写操作权限
- PERMISSION_SYNC：锁定权限(推荐仅用于superuser角色，从而保证管理员超级权限不会被修改)
- ADMIN_NULL：无权限
#### 内置角色类型
框架预定义了以下角色：
- ROLE_SUPERUSER：超级管理员，拥有所有权限
- ROLE_STAFF：员工，可访问后台系统和查询数据
- ROLE_CUSTOMER：客户，无法登录后台系统
- ROLE_FINANCE：财务，可读写除权限外的所有数据
- ROLE_SECRETARY：秘书，可读写所有数据但不能修改管理员权限
权限校验流程
1. 系统级校验：
检查方法上的@Verification注解，根据用户角色获取其拥有的权限ID集合，验证用户是否拥有执行操作所需的权限
2. 用户级校验：
执行自定义的AccessCheck钩子函数，允许开发者实现复杂的权限逻辑
3. 权限缓存
为了提高性能，用户权限ID集合会被缓存：使用PearApplicationInitialization.PermissionTemplate缓存用户权限，当权限或角色发生变化时，自动清除相关缓存



## 🚧 待办事项
- [ ] 后端：优化第三代权限系统
- [ ] 后端：限流限速注解，限流通过配置类创建的线程池控制，需要限流时注入此类执行方法从而达到目的，限速通过令牌桶实现，需要限速时注入此类执行方法从而达到目的
- [ ] 后端：将Admin的DAO层重构，不依赖mybatis采用JDBC原始实现
- [ ] 后端：通过观察者模式对所有事件进行监听，当触发事件并且开发者绑定了观察者时进行事件存储与输出
- [ ] 后端：完善优化日志输出对warn级别添加黄色
- [ ] 后端：将项目中所有的直接引用（字符串，如所有admin的接口以及拦截器的拦截路径等）转换为从Config或者Env的符号引用
- [ ] 后端：增加埋点服务或者说优化日志模块，将信息收集存储到消息队列中并对外进行接口输出，使其可以通过观测器进行数据采集
- [ ] 前端：改进密码修改表单安全性
- [ ] 前端：优化前端删除操作体验
- [ ] 前端：添加前端对数据的修改

## 📝 各版本修改内容

- 1.3.1 解决了Admin无法处理用户自定义实体类继承的情况

## 🤝 参与贡献

1. Fork 本项目
2. 创建新分支 (`git checkout -b feature/your-feature`)
3. 提交更改 (`git commit -am 'Add some feature'`)
4. 推送到分支 (`git push origin feature/your-feature`)
5. 创建Pull Request

## 📄 许可证

[MIT License](https://github.com/MuziSuper/pear-spring-boot-starter/blob/main/LICENSE) © 2025 MuziSuper