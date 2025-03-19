# pear可视化后台管理系统
<div align="center">
    <p>
        <a href="README.md">中文</a> | <a href="README_EN.md">English</a>
    </p>
    <img src="favicon.svg" alt="Pear Logo" width="200" height="200">
    <p>🚀 Pear是一个java中间件，便于后端人员的开发，内置可视化后台管理界面，可以对数据进行增删改查，并且提供了钩子方法，可以让开发者在数据处理的前后进行一些通用的操作，例如数据加密或去敏等，还提供独立的日志系统与缓存系统，用于日志归档与缓存热点数据，更多功能还在开发中！
</p>
    <p>🛠️ 集成框架 | 🎉 高通用性 | 🖋️ 低操作性 | 🔑 高扩展性</p>
    <p>
        <a href="https://spring.io/projects/spring-boot">
            <img src="https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen.svg" alt="Spring Boot">
        </a>
        <a href="https://www.oracle.com/java/">
            <img src="https://img.shields.io/badge/JDK-17+-green.svg" alt="JDK">
        </a>
        <a href="https://www.mysql.com/">
            <img src="https://img.shields.io/badge/MySQL-8.0+-blue.svg" alt="MySQL">
        </a>
        <a href="https://mybatis.org/">
            <img src="https://img.shields.io/badge/MyBatis-3.5.x-yellow.svg" alt="MyBatis">
        </a>
        <a href="https://maven.apache.org/">
            <img src="https://img.shields.io/badge/Maven-3.9+-purple.svg" alt="Maven">
        </a>
    </p>
    <p>
        <a href="https://github.com/heathcetide/hibiscus/stargazers">
            <img src="https://img.shields.io/github/stars/MuziSuper/pear-spring-boot-starter?style=flat-square" alt="GitHub stars">
        </a>
        <a href="https://github.com/heathcetide/hibiscus/network">
            <img src="https://img.shields.io/github/forks/MuziSuper/pear-spring-boot-starter?style=flat-square" alt="GitHub forks">
        </a>
        <a href="https://github.com/heathcetide/hibiscus/issues">
            <img src="https://img.shields.io/github/issues/MuziSuper/pear-spring-boot-starter?style=flat-square" alt="GitHub issues">
        </a>
    </p>
</div>

## maven依赖
```xml
<!-- maven dependency of pear -->
<dependency>
    <groupId>cn.muzisheng.pear</groupId>
    <artifactId>pear-spring-boot-starter</artifactId>
    <version>1.1.13</version>
</dependency>
```

## 页面展示
默认路径：http://localhost:8080/auth/login
![img.png](src/main/resources/static/login.png)
默认路径：http://localhost:8080/auth/register
![img.png](src/main/resources/static/register.png)
默认路径：http://localhost:8080/auth/dashboard
![img.png](src/main/resources/static/dashboard.png)

# 使用教程
## 模型定义 --注解的使用
### @PearObject
`@PearObject` 注解用于标注在类上，用于定义数据模型的元信息。

属性：

`TableName`：数据库表名，默认为空字符串。

`group`：模型分组，默认为空字符串。

`desc`：模型的描述信息，默认为空字符串。

`path`：模型的访问路径，默认为空字符串。

`editPage`：编辑页面的地址，默认为空字符串。

`listPage`：展示页面的地址，默认为空字符串。

`pluralName`：模型的复数形式，默认为空字符串。

`iconUrl`：模型图标的 URL 地址，默认为空字符串。

`isInvisible`：是否隐藏此模型，默认为 false
### @PearField
`@PearField` 注解用于标注在类的字段上，用于定义字段的元信息。

属性：

isShow：是否在页面上显示该字段，默认为 false。

isEdit：是否可编辑该字段，默认为 false。

isFilterable：是否可过滤该字段，默认为 false。

isOrderable：是否可排序该字段，默认为 false。

isSearchable：是否可搜索该字段，默认为 false。

isRequire：该字段是否为必填项，默认为 false。

isPrimaryKey：该字段是否为主键，默认为 false。

isUniqueKey：该字段是否为唯一键，默认为 false。

placeholder：字段的默认值，默认为空字符串。

label：字段的显示名称，默认为空字符串。
### 使用案例
```java
@Entity
@TableName("cat")
@Data
@Component
@PearObject(desc = "Cat is an animal.",pluralName = "cats")
public class Cat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true,isShow = true,isEdit = true,isRequire = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private Long id;
    @PearField(isShow = true,isEdit = true,isRequire = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private String name;
    @PearField(isShow = true,isEdit = true,isRequire = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private Integer age;
    @PearField(isShow = true,isEdit = true,isRequire = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private String color;
    @PearField(isShow = true,isEdit = true,isRequire = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private String breed;
    @PearField(isShow = true,isEdit = true,isRequire = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private String image;
    @PearField(isShow = true,isEdit = true,isRequire = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private String description;
    @Column(name = "`createdAt`")
    @PearField(isShow = true,isEdit = true,isRequire = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private LocalDateTime createdAt;
}
```

## 模型补充及其构建 --BuilderFactory的使用
在项目中通过实现`CommandLineRunner`接口创建初始化类，重写run方法，先获取AdminObjects列表，再通过`AdminObject.BuilderFactory(Class clazz)`填充附加信息
，例如钩子函数或字段默认排序规则等;最后使用`AdminContainer.buildAdminObjects(adminObjects)`方法构建所有AdminObject部署CRUD接口;
```java
@Component
public class Initialized implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // 获取pear内置模型的AdminObject容器列表
        List<AdminObject> adminObjects = AdminContainer.getAllAdminObjects();

        // 创建Cat模型的BuilderFactory
        AdminObject.BuilderFactory catBuilder = new AdminObject.BuilderFactory(Cat.class);

        // 设置删除前的回调函数
        catBuilder.setBeforeDelete((request, admin) -> {
            if (admin instanceof Cat cat) {
                if (request.getParameter("name") != null) {
                    cat.setName(request.getParameter("name"));
                }
                return cat;
            }
            return admin;
        });

        // 设置排序规则
        catBuilder.setOrder(new Order("name", Constant.ORDER_OP_ASC));

        // 构建AdminObject并添加到容器中
        AdminContainer.buildAdminObjects(adminObjects);
    }
}
```
## 配置项
```properties
# 用户密码盐值
app.user.password.salt=salt
# Token加密盐值
app.token.salt=salt
# Token前缀
app.token.head=Bearer 
# Token生存周期
app.token.expire=7*24*60*60*1000
# 日志等级
app.log.level=LOG
# 日志输出样式
app.log.stdout-pattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %-17black(%thread) %-82green(%logger{70}-%line) %highlight(%msg){black} %highlight(%ex){red} \n
# 日志归档输入样式
app.log.file-pattern=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
# 所有等级日志归档目录名
app.log.file=log
# 各等级日志归档目录名
app.log.log-catalogue-address=log/log-day
app.log.warn-catalogue-address=log/log-day
app.log.error-catalogue-address=log/log-day
# 缓存过期时间
app.cathe.expire=24*60*60*1000
```

## 梗概

### 后端

​	基于`SpringBoot`框架，集成`mybatisplus`ORM持久层与数据库连接处理数据，集成`logback`管理日志的输出与样式。用户可选择添加`redis`依赖来处理系统的缓存（待完成），或者使用系统内置的**缓存系统**。设计有多个基础的**模型**，来为开发者提供封装好的CURD服务，设计有用户校验层，对没有密钥或密钥过期的用户进行拦截，跳转到登陆注册页面。还有对于框架来说最重要的**反射系统**，将用户注解的类对象进行反射，获取属性与方法，封装CURD服务，用于后台数据管理。还内置有其他的系统，例如**消息系统**，用于整个系统重要事务处理产生消息传递给邮箱、钉钉等其他第三方服务，这些功能暂代开发中。。。

### 前端

​	使用H5,配合jQuery调用后台`api`与渲染页面，使用`tailwindcss`原子化`css`。页面有登陆页面、注册页面，控制台页面 --> 各个数据库表的展示、筛选、删除、修改、添加。 



## 各系统任务分析



### 基础用户系统 

#### 功能介绍

1. 实现用户的登陆与注册功能，并且可以通过Token或本地会话直接登录；
2. 产生Token，解析token，对token的过期判断 (仿照carrot)；
3. 提供一个接口用于获取当前登录的用户数据；


### 日志系统 

#### 功能介绍

1. 日志输出样式的优化；
2. 不同级别日志的归档记录；



### 缓存系统 

#### 功能介绍

1. 提供一个线程安全可以存入多对键值对并且携带当前存入的时间戳的缓存容器，还可以设置此容器所有数据的过期时间；
2. 提供获取缓存的get方法，未命中则返回null；
3. 提供添加缓存的add方法，已存在此键名则覆盖；
4. 提供判断缓存是否存在此键的contains方法；
5. 提供删除此键缓存的remove方法，不存在此键则不操作；


### 核心通用系统 

#### 功能介绍

1. 在开发者项目中需要在`CommandLineRunner`中调用`getPearAdminObjects()`方法获取pear基础模型的`AdminObject`对象列表；
2. 开发者根据自定义的模型创建`AdminObject`对象并填充其模型相关信息；
3. 将所有`AdminObject`对象放入`AdminContainer.adminObjects`列表中；

#### `AdminObject`模型详解

```java
/**
 * 加载客户端模型
 **/
@Data
public class AdminObject {
    /**
     * 模型
     **/
    private Class<?> model;
    /**
     * 模型实例
     **/
    private Object modelElem;
    /**
     * 所属组名，多个相关的表归为一类
     **/
    private String group;
    /**
     * 对象名称
     **/
    private String name;
    /**
     * 对象描述
     **/
    private String desc;
    /**
     * 其相关CURD的路径前缀
     **/
    private String path;
    /**
     * 可展示在前端的字段列表
     **/
    private List<String> shows;
    /**
     * 所有字段默认排序方式的列表
     **/
    private List<Order> Orders;
    /**
     * 可编辑的字段列表
     **/
    private List<String> edits;
    /**
     * 可用于过滤的字段
     **/
    private List<String> filterables;
    /**
     * 可用于排序的字段
     **/
    private List<String> Orderables;
    /**
     * 可用于搜索的字段
     **/
    private List<String> searches;
    /**
     * 必须存在的字段
     **/
    private List<String> requires;
    /**
     * 主键字段列表
     **/
    private List<String> primaryKeys;
    /**
     * 唯一键字段列表
     **/
    private List<String> uniqueKeys;
    /**
     * 复数名称
     **/
    private String pluralName;
    /**
     * 此表所有字段的详细配置
     **/
    private List<AdminField> fields;
    /**
     * 编辑此表的前端路由
     **/
    private String editPage;
    /**
     * 展示此表的前端路由
     **/
    private String listPage;
    /**
     * 脚本文件对象
     **/
    private List<AdminScript> AdminScripts;
    /**
     * 需要在前端下载的样式文件源地址列表
     **/
    private List<String> styles;
    /**
     * 权限设置,不同的操作权限的有无
     **/
    private Map<String, Boolean> permissions;
    /**
     * 图标
     **/
    private AdminIcon icon;
    /**
     * 是否隐藏该对象
     **/
    private boolean invisible;
    /**
     * 用于描述数据库表字段的详细属性配置
     **/
    private Map<String, AdminAttribute> Attributes;
    /**
     * 数据库表内实际名称
     **/
    private String tableName;
    /**
     * 执行某些数据库操作时需要忽略的字段
     **/
    private Map<String, Boolean> ignores;
    /**
     * 模型内主键字段的映射
     **/
    private Map<String, String> primaryKeyMap;
    /**
     * 渲染页面方法
     **/
    private AdminViewOnSite adminViewOnSite;
    /**
     * 身份验证方法
     **/
    private AccessCheck accessCheck;
    /**
     * 预创建钩子方法
     **/
    private BeforeCreate beforeCreate;
    /**
     * 预更新钩子方法
     **/
    private BeforeUpdate beforeUpdate;
    /**
     * 预删除钩子方法
     **/
    private BeforeDelete beforeDelete;
    /**
     * 预渲染钩子方法
     **/
    private BeforeRender beforeRender;
}
```



### 其他系统（构思中。。。）





