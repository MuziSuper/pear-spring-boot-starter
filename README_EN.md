# PEAR visual back-end management system
<div align="center">
    <p>
        <a href="README.md">中文</a> | <a href="README_EN.md">English</a>
    </p>
    <img src="favicon.svg" alt="Pear Logo" width="200" height="200">
    <p>🚀 Pear is a java middleware, which is convenient for the development of back-end personnel, with a built-in visual background management interface, which can add, delete, modify and check data, and provides hook methods, which can allow developers to perform some general operations before and after data processing, such as data encryption or desensitization, etc., and also provides an independent log system and cache system for log archiving and caching hot data, and more functions are still in development!
</p>
    <p>🛠️ Integration frameworks | 🎉 High versatility | 🖋️ Low operability | 🔑 High scalability</p>
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

## Maven dependency
```xml
<!-- maven dependency of pear -->
<dependency>
    <groupId>cn.muzisheng.pear</groupId>
    <artifactId>pear-spring-boot-starter</artifactId>
    <version>1.1.5</version>
</dependency>
```

## Page display
![img.png](src/main/resources/static/login.png)
![img.png](src/main/resources/static/dashboard.png)

# Use the tutorial
In a personal project, implement the CommandLineRunner interface to fill in the information of the customized model before the project starts, and pass it to the AdminContainer container.
```java
@Component
public class Initialized implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // Obtain the list of AdminObject containers for the built-in model of pear
        ArrayList<AdminObject> adminObjects = AdminFactory.getAdminContainer();
        // Populate the custom Cat model information into the AdminObject container
        AdminObject adminObject = new AdminObject();
        adminObject.setModel(Cat.class);
        adminObject.setName("cat");
        adminObject.setDesc("cat is an animal");
        adminObject.setPath("/cat");
        adminObject.setShows(new ArrayList<>(List.of(new String[]{"id", "name", "age"})));
        adminObject.setOrders(new ArrayList<>(List.of(new Order[]{new Order("id", "desc")})));
        adminObject.setBeforeCreate((request,admin) -> {
            if(admin instanceof Cat cat){
                if (request.getParameter("name")!=null){
                    cat.setName(request.getParameter("name"));
                }
                return cat;
            }
            return admin;
        });
        // Add to the list of AdminObject containers
        adminObjects.add(adminObject);
        // Assign it to AdminContainer to build the add, delete, modify, and query APIs
        AdminContainer.buildAdminObjects(adminObjects);
    }
}
```
## Configuration items
```properties
# User password salt
app.user.password.salt=salt
# Token encryption salt value
app.token.salt=salt
# Token prefix
app.token.head=Bearer 
# Token lifetime
app.token.expire=7*24*60*60*1000
# Log level
app.log.level=LOG
# Log output style
app.log.stdout-pattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %-17black(%thread) %-82green(%logger{70}-%line) %highlight(%msg){black} %highlight(%ex){red} \n
# Log archive input style
app.log.file-pattern=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
# The name of the directory where all levels of logs are archived
app.log.file=log
# The name of the log archive directory at each level
app.log.log-catalogue-address=log/log-day
app.log.warn-catalogue-address=log/log-day
app.log.error-catalogue-address=log/log-day
# Cache expiration time
app.cathe.expire=24*60*60*1000
```

## Sketch

### server
Based on the 'SpringBoot' framework, it integrates the 'mybatisplus' ORM persistence layer to process data with the database connection, and integrates the 'logback' to manage the output and style of the log. The user can choose to add a 'redis' dependency to handle the system's caching (to be completed), or use the system's built-in caching system. There are multiple basic models designed to provide developers with encapsulated CURD services, and a user verification layer is designed to intercept users who do not have keys or whose keys have expired, and jump to the login registration page. There is also the most important reflection system for the framework, which reflects the class objects annotated by the user, obtains properties and methods, and encapsulates the CURD service for back-end data management. There are also other built-in systems, such as **Message System**, which is used for the important transaction processing of the whole system to generate messages to other third-party services such as mailboxes and DingTalk, and these functions are under development for the time being. . .
### client
Use H5 to call the backend 'api' with jQuery and render the page, and use 'tailwindcss' to atomize the 'css'. The page includes a landing page, a registration page, and a console page --> displaying, filtering, deleting, modifying, and adding various database tables.


## Task analysis of each system



### Basic user system

#### Function introduction

1. Realize the user's login and registration functions, and can directly log in through the token or local session;
2. Generate tokens, parse tokens, and determine the expiration of tokens (similar to carrot); 
3. Provide an interface for obtaining the current logged in user data;

### Logging system

#### Feature introduction

1. Optimized the log output style;
2. Archiving records of logs at different levels;

### Caching system

#### Feature introduction

1. Provide a thread-safe cache container that can store multiple pairs of key-value pairs and carry the timestamp of the current deposit, and also set the expiration time of all data in this container;
2. Provide a get method to get the cache, and return null if it misses;
3. Provide the add method to add the cache, and the existing key name will be overwritten;
4. Provide a containsmethod to determine whether this key exists in the cache;
5. Provide the remove method to delete the cache of this key, if this key does not exist, it will not be operated;

### Core General Purpose System

#### Feature introduction

1. In the developer project, you need to call the 'getPearAdminObjects()' method in 'CommandLineRunner' to get the list of 'AdminObject' objects of the base model of pear;
2. The developer creates an 'AdminObject' object based on the custom model and fills in its model-related information.
3. Put all AdminObject objects into the AdminContainer.adminObjects list;

#### The 'AdminObject' model in detail
```java
package cn.muzisheng.pear.model;

import cn.muzisheng.pear.handler.*;
import cn.muzisheng.pear.test.handler.*;
import cn.muzisheng.pear.test.model.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Load the client-side model
 **/
@Data
public class AdminObject {
    /**
     * model
     **/
    private Class<?> model;
    /**
     * Model instances
     **/
    private Object modelElem;
    /**
     * The name of the group to which it belongs, and multiple related tables are grouped into one category
     **/
    private String group;
    /**
     * The name of the object
     **/
    private String name;
    /**
     * Object description
     **/
    private String desc;
    /**
     * The path prefix of its associated CURD
     **/
    private String path;
    /**
     * A list of fields can be displayed on the frontend
     **/
    private List<String> shows;
    /**
     * A list of all fields that are sorted by default
     **/
    private List<Order> Orders;
    /**
     * A list of editable fields
     **/
    private List<String> edits;
    /**
     * Fields that can be used for filtering
     **/
    private List<String> filterables;
    /**
     * Fields that can be used for sorting
     **/
    private List<String> Orderables;
    /**
     * Fields that can be used for search
     **/
    private List<String> searches;
    /**
     * Fields that must exist
     **/
    private List<String> requires;
    /**
     * A list of primary key fields
     **/
    private List<String> primaryKeys;
    /**
     * A list of unique key fields
     **/
    private List<String> uniqueKeys;
    /**
     * Plural names
     **/
    private String pluralName;
    /**
     * Detailed configuration of all fields in this table
     **/
    private List<AdminField> fields;
    /**
     * Edit the front-end routes for this table
     **/
    private String editPage;
    /**
     * This table displays the front-end routes
     **/
    private String listPage;
    /**
     * Script file object
     **/
    private List<AdminScript> AdminScripts;
    /**
     * A list of the source addresses of the style files that need to be downloaded on the frontend
     **/
    private List<String> styles;
    /**
     * Permission settings, the presence or absence of different operation permissions
     **/
    private Map<String, Boolean> permissions;
    /**
     * icon
     **/
    private AdminIcon icon;
    /**
     * Whether to hide the object
     **/
    private boolean invisible;
    /**
     * A detailed attribute configuration that describes a database table field
     **/
    private Map<String, AdminAttribute> Attributes;
    /**
     * The actual name of the database table
     **/
    private String tableName;
    /**
     * Fields that need to be ignored when performing certain database operations
     **/
    private Map<String, Boolean> ignores;
    /**
     * Mapping of primary key fields within the model
     **/
    private Map<String, String> primaryKeyMap;
    /**
     * Render page method
     **/
    private AdminViewOnSite adminViewOnSite;
    /**
     * Authentication method
     **/
    private AccessCheck accessCheck;
    /**
     * Pre-created hook methods
     **/
    private BeforeCreate beforeCreate;
    /**
     * Pre-update hook methods
     **/
    private BeforeUpdate beforeUpdate;
    /**
     * Pre-delete hook method
     **/
    private BeforeDelete beforeDelete;
    /**
     * Pre-render hook method
     **/
    private BeforeRender beforeRender;
}
```



### Other systems (under conception...) 





