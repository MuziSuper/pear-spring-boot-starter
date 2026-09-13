# Pear Spring Boot Starter

<div align="center">
  <img src="favicon.svg" alt="Pear Logo" width="160" height="160">
  <p>面向 Spring Boot 单体应用的快速后台管理 starter</p>
  <p><a href="README.md">中文</a> | <a href="README_EN.md">English</a></p>
  <p>
    <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen.svg" alt="Spring Boot"></a>
    <a href="https://www.oracle.com/java/"><img src="https://img.shields.io/badge/JDK-17+-green.svg" alt="JDK"></a>
    <a href="https://mybatis.org/"><img src="https://img.shields.io/badge/MyBatis--Plus-3.5.x-yellow.svg" alt="MyBatis-Plus"></a>
    <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.9+-purple.svg" alt="Maven"></a>
  </p>
</div>

## 项目简介

Pear 是一个用于快速构建单体应用后台管理能力的 Spring Boot starter。业务项目引入 starter 后，可以通过 `@PearApplication` 启用自动配置，并使用 `@PearObject`、`@PearField` 标注实体类，让框架自动收集模型元数据，生成统一的后台 CRUD 能力、权限校验、用户登录注册页面和基础配置。

核心能力：

- 注解驱动的后台模型注册
- 内置登录、注册、仪表盘页面
- 通用 `/admin/{model}` CRUD 接口
- RBAC 角色权限与 `@Verification` 校验切面
- LRU、LFU、Redis 三类缓存实现
- 可通过 properties 或自定义 Bean 覆盖默认配置
- Maven Central 发布所需的 sources、javadoc、GPG 签名流程

## 模块结构

| 模块 | 说明 |
| --- | --- |
| `pear-spring-boot-starter` | 对外 starter，包含自动配置入口、页面模板、静态资源和 SQL 脚本 |
| `pear-spring-boot-basic` | 配置对象、配置属性、异常与自动配置 |
| `pear-spring-boot-core` | 注解、实体、DAO、Service、AdminObject 容器、权限切面 |
| `pear-spring-boot-web` | Web API、页面路由、JWT 拦截器 |
| `pear-spring-boot-cache` | 缓存容器、缓存工厂、LRU/LFU/Redis 实现 |
| `pear-spring-boot-utils` | JWT、时间、字符串、ID、命名转换等工具 |
| `pear-spring-boot-constant` | 框架常量 |

## 环境要求

- JDK 17 或更高版本
- Maven 3.9 或更高版本
- Spring Boot 3.3.x
- MySQL 或其他兼容 JPA/MyBatis-Plus 的数据源

项目统一使用 Java 17 作为发布字节码基线，父 POM 通过 `maven.compiler.release=17` 固定输出版本；即使 Maven 运行在 JDK 21、JDK 25 等较新 JDK 上，也会产出 Java 17 兼容的 jar。

## 快速开始

### 1. 添加依赖

```xml
<dependency>
  <groupId>cn.muzisheng.pear</groupId>
  <artifactId>pear-spring-boot-starter</artifactId>
  <version>1.3.1</version>
</dependency>
```

### 2. 启用 Pear

```java
import cn.muzisheng.pear.PearApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@PearApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 3. 配置数据源

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pear_demo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### 4. 创建超级管理员

启动应用时传入 `-u` 和 `-p` 参数即可创建或更新超级管理员密码：

```bash
java -jar demo.jar -u admin@example.com -p your_password
```

默认页面：

| 页面 | 地址 |
| --- | --- |
| 登录 | `http://localhost:8080/auth/login` |
| 注册 | `http://localhost:8080/auth/register` |
| 仪表盘 | `http://localhost:8080/auth/dashboard` |
| 设置 | `http://localhost:8080/auth/settings` |

## 实体接入

实体类使用 `@PearObject` 标记为后台模型，字段使用 `@PearField` 描述前端展示、编辑、过滤、排序和搜索行为。实体仍可同时使用 JPA 和 MyBatis-Plus 注解。

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

@Data
@Component
@Entity(name = "product")
@TableName("product")
@PearObject(
    group = "shop",
    desc = "Product information.",
    path = "/product",
    pluralName = "products"
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true, isEdit = false, label = "ID")
    private Long id;

    @Column(nullable = false)
    @PearField(label = "Name", placeholder = "Product name")
    private String name;

    @PearField(label = "Price", isFilterable = true, isOrderable = true)
    private Double price;

    @TableField(fill = FieldFill.INSERT, value = "gmt_created")
    @Column(name = "gmt_created")
    @PearField(isEdit = false, isAutoInsertTime = true, label = "Created At")
    private LocalDateTime gmtCreated;

    @Transient
    private String displayText;
}
```

### `@PearObject`

| 属性 | 默认值 | 说明 |
| --- | --- | --- |
| `TableName` | `""` | 数据库表名。优先级低于 JPA `@Entity(name=...)` 和 MyBatis-Plus `@TableName` |
| `group` | `""` | 后台模型分组 |
| `desc` | `""` | 模型描述 |
| `path` | `""` | 模型访问路径 |
| `editPage` | `""` | 自定义编辑页面地址 |
| `listPage` | `""` | 自定义列表页面地址 |
| `pluralName` | `""` | 模型复数名称 |
| `iconUrl` | `""` | 模型图标地址 |
| `isInvisible` | `false` | 是否在后台隐藏 |
| `isLoad` | `true` | 是否加载进 Pear 后台模型容器 |

### `@PearField`

| 属性 | 默认值 | 说明 |
| --- | --- | --- |
| `isShow` | `true` | 是否在页面展示 |
| `isEdit` | `true` | 是否允许编辑 |
| `isFilterable` | `true` | 是否允许过滤 |
| `isOrderable` | `true` | 是否允许排序 |
| `isSearchable` | `true` | 是否参与关键字搜索 |
| `isRequire` | `true` | 是否必填 |
| `isPrimaryKey` | `false` | 是否主键 |
| `isUniqueKey` | `false` | 是否唯一键 |
| `placeholder` | `""` | 表单提示文本 |
| `isAutoUpdateTime` | `false` | 更新时自动写入当前时间 |
| `isAutoInsertTime` | `false` | 创建时自动写入当前时间 |
| `label` | `""` | 前端显示名称 |

### 启动后补充模型配置

Pear 会在 Bean 初始化阶段把 `@PearObject` 实体收集到 `AdminContainer`。如果需要补充钩子、排序、展示字段等配置，可以在 `CommandLineRunner` 中使用 `AdminContainer.AdminFactory`。

```java
import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.initialize.AdminContainer;
import cn.muzisheng.pear.model.Order;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PearAdminInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) {
        AdminContainer.AdminFactory factory = new AdminContainer.AdminFactory("product");

        factory.addOrder(new Order("gmt_created", Constant.ORDER_OP_DESC));
        factory.setBeforeCreate((request, adminObject, data) -> {
            data.putIfAbsent("name", "Untitled product");
        });
        factory.setAccessCheck((request, adminObject, user) -> user.getEnabled());
    }
}
```

## 配置项

所有默认配置都可以通过 `application.properties` 覆盖，也可以在业务项目中注入同类型 Bean 覆盖自动配置。

```properties
# Cache
pear.starter.cache.expire=86400000
pear.starter.cache.capacity=200
pear.starter.cache.strategy=LRU
pear.starter.cache.time-unit=MILLISECONDS

# Token
pear.starter.token.head=Bearer 
pear.starter.token.expire=604800000
pear.starter.token.issue=pear
pear.starter.token.subject=authentication

# User
pear.starter.user.password-salt=PEAR_APPLICATION_SALT

# Site
pear.starter.site.auth-prefix=/auth

```

### Pear 日志配置

引入 Pear starter 后，框架默认加载 `logback-spring.xml`：日志同时输出到控制台，并按 INFO、WARN、ERROR 分类异步写入文件；文件按天滚动，默认保留 30 天。可使用以下配置项覆盖默认值：

| 配置项 | 默认值 | 作用 |
| --- | --- | --- |
| `pear.starter.log.level` | `INFO` | 全局根日志级别，例如 `DEBUG`、`INFO`、`WARN`、`ERROR` |
| `pear.starter.log.stdout-pattern` | `%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{40} - %msg%n` | Pear 控制台日志输出格式 |
| `pear.starter.log.file-pattern` | `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{40} - %msg%n` | 日志文件输出格式 |
| `pear.starter.log.file-path` | `log` | 当前 INFO、WARN、ERROR 日志文件的根目录 |
| `pear.starter.log.info-directory` | `${pear.starter.log.file-path}/info` | INFO 滚动日志目录 |
| `pear.starter.log.warn-directory` | `${pear.starter.log.file-path}/warn` | WARN 滚动日志目录 |
| `pear.starter.log.error-directory` | `${pear.starter.log.file-path}/error` | ERROR 滚动日志目录 |
| `pear.starter.log.max-history` | `30` | 每类滚动日志保留天数 |
| `pear.starter.log.queue-size` | `512` | 每个异步日志队列的容量 |

例如，将日志写入应用指定目录并保留 14 天：

```properties
pear.starter.log.file-path=/var/log/demo
pear.starter.log.max-history=14
```

业务项目可通过 `logging.config` 指向自定义 Logback 文件，完整覆盖 Pear 默认日志配置；也可以在自身 `logback-spring.xml` 中复用或调整同名 appender 和 root logger。

自定义配置 Bean 示例：

```java
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.config.TokenConfig;
import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PearConfig {
    @Bean
    public CacheConfig cacheConfig() {
        return new CacheConfig.Builder()
            .expire(3600000)
            .capacity(500)
            .strategy(CacheStrategyEnum.LFU)
            .build();
    }

    @Bean
    public TokenConfig tokenConfig() {
        return TokenConfig.builder()
            .head("Bearer ")
            .expire(86400000L)
            .issue("pear-admin")
            .subject("authentication")
            .build();
    }

    @Bean
    public UserConfig userConfig() {
        return new UserConfig.Builder()
            .passwordSalt("custom-user-salt")
            .build();
    }
}
```

## API 概览

### 用户接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/user/register` | 注册用户 |
| `POST` | `/user/login` | 登录并返回 token |
| `POST` | `/user/info` | 获取当前用户信息 |

### 后台接口

后台接口由 `JWTInterceptor` 拦截，需要在请求头中携带 `Authorization`。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/admin/` | 获取静态资源和后台模型信息 |
| `POST` | `/admin/json` | 获取当前用户、站点信息和模型元数据 |
| `POST` | `/admin/{model}` | 查询列表；无请求体时按主键/唯一键获取单条 |
| `PUT` | `/admin/{model}` | 创建数据 |
| `PATCH` | `/admin/{model}` | 更新数据 |
| `DELETE` | `/admin/{model}` | 删除数据 |

## 权限模型

Pear 内置 RBAC 权限体系，并通过 `@Verification` 切面执行两层校验：

- 系统级校验：检查当前用户是否拥有方法要求的权限码。
- 用户级校验：执行模型上的 `AccessCheck` 钩子，用于业务自定义授权。

内置权限码：

| 权限 | 说明 |
| --- | --- |
| `ADMIN_CREATE` | 创建权限 |
| `ADMIN_DELETE` | 删除权限 |
| `ADMIN_UPDATE` | 更新权限 |
| `ADMIN_SELECT` | 查询权限 |
| `PERMISSION_WRITER` | 权限写操作 |
| `PERMISSION_SYNC` | 权限锁定 |
| `ADMIN_NULL` | 空权限 |

内置角色：

| 角色 | 说明 |
| --- | --- |
| `superuser` | 超级管理员，拥有全部管理权限 |
| `staff` | 员工，可访问后台并查询数据 |
| `customer` | 客户，默认无法登录后台 |
| `finance` | 财务，可读写除权限外的数据 |
| `secretary` | 秘书，可读写数据但不能修改管理员权限 |

## 构建与验证

```bash
mvn test
mvn -DskipTests package
mvn -DskipTests -Dgpg.skip verify
```

`package` 阶段会为每个 jar 模块生成：

- 主 jar
- `-sources.jar`
- `-javadoc.jar`

## 发布到 Maven Central

项目使用 Sonatype Central Publisher Portal 的 `central-publishing-maven-plugin`。发布前请确认：

- Central Portal 已完成命名空间验证。
- `~/.m2/settings.xml` 中存在 id 为 `central` 的 server。
- 本机或 CI 已配置可用 GPG 私钥。
- 当前 release 版本号尚未发布过。

`settings.xml` 示例：

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>your-token-username</username>
      <password>your-token-password</password>
    </server>
  </servers>
</settings>
```

发布命令：

```bash
mvn clean deploy
```

## 界面预览

| 登录页面 | 注册页面 | 仪表盘 |
| --- | --- | --- |
| ![登录页面](login.png) | ![注册页面](register.png) | ![仪表盘](dashboard.png) |

## 版本记录

| 版本 | 说明 |
| --- | --- |
| `1.3.1` | 修复 Admin 处理用户自定义实体继承场景的问题；统一 Java 17 构建与 Maven Central 发布配置 |

## 待办事项

- 优化第三代权限系统。
- 将 Admin DAO 层逐步改造为更安全的参数化 SQL 实现。
- 引入事件观察机制，用于注册、登录、数据变更等事件扩展。
- 完善日志归档与观测能力。
- 优化前端数据编辑、删除和密码修改体验。

## 贡献

1. Fork 本项目。
2. 创建功能分支：`git checkout -b feature/your-feature`。
3. 提交更改：`git commit -m "Add your feature"`。
4. 推送分支：`git push origin feature/your-feature`。
5. 创建 Pull Request。

## 许可证

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) © 2025 MuziSuper
