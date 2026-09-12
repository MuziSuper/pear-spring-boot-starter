# Pear 项目功能总览

## 定位

Pear 是一个基于 Spring Boot 的后台开发 Starter。它提供用户认证、RBAC 权限控制、注解驱动的动态 Admin CRUD、缓存和常用基础能力，并携带后台页面所需的静态资源与初始化脚本。

## 模块职责

| 模块 | 职责 |
| --- | --- |
| `pear-spring-boot-starter` | 对外 Starter；导入组件扫描、Mapper 扫描、实体扫描和静态资源。 |
| `pear-spring-boot-basic` | 配置属性、异常体系、断言工具、Processor 和自动配置。 |
| `pear-spring-boot-core` | 用户、角色、权限、动态 Admin、DAO/Mapper、领域实体及 DTO/VO。 |
| `pear-spring-boot-web` | REST API、全局异常处理、JWT 拦截器、Web MVC 配置。 |
| `pear-spring-boot-cache` | LRU、LFU、Redis 缓存实现和缓存容器。 |
| `pear-spring-boot-utils` | JWT、上下文、ID、字符串、时间和命名转换工具。 |
| `pear-spring-boot-constant` | 全项目共享常量。 |

## 启动与元数据流程

1. 使用方通过 `@PearApplication` 导入 `PearAutoConfig`。
2. Starter 扫描 `cn.muzisheng.pear` 下的组件、Mapper 和实体。
3. `PearBeanInitialization` 读取 `@PearObject` 与 `@PearField`，构造 `AdminObject` 元数据。
4. `PearApplicationInitialization` 初始化缓存、配置、角色和权限，并将 Admin 元数据注册进 `AdminContainer`。
5. Web 层注册 JWT 拦截器与全局异常处理器，对外提供用户和 Admin 接口。

## 用户与认证

### 注册

`POST /user/register` 接收 `RegisterUserRequestDTO`。服务校验用户信息、创建用户、更新展示信息和登录信息，最后返回 `ApiResponseVO<RegisterUserVO>`。

### 登录

`POST /user/login` 接收 `LoginRequestDTO`。可使用邮箱密码或已有 Token 完成认证。密码使用 BCrypt；历史 SHA-256 密码在验证成功后会尝试升级。成功后生成 JWT，返回 `ApiResponseVO<LoginVO>`。

### 当前用户

`POST /user/info` 接收 `CurrentUserQueryDTO`。JWT 拦截器解析 Token 并将当前用户写入 request scope；服务优先读取该上下文，再兼容 session 场景，返回不含密码和内部字段的 `CurrentUserVO`。

## 权限与 Admin

Admin 接口统一位于 `/admin`。`HookAspect` 会读取 `@Verification`，结合当前用户、角色、权限与模型的 `AccessCheck` 完成授权。

动态 Admin 的模型由实体注解驱动：

- `@PearObject` 定义模型路径、描述、分组和复数名称。
- `@PearField` 定义主键、展示、编辑、检索、筛选和排序能力。
- `AdminServiceImpl` 按白名单校验字段，并通过 MyBatis 参数绑定生成 SQL，避免将请求值直接拼入 SQL。

对外接口使用 `AdminQueryDTO`、`AdminCreateRequestDTO`、`AdminUpdateRequestDTO`。动态返回值通过 `AdminPayloadVO` 保持 VO 类型；其 JSON 序列化保持原始 Admin `data` 结构，避免破坏前端消费方式。

## 异常与响应

所有业务异常继承 `BaseException`，并携带 `ErrorCode`。全局异常处理器将其转换为 `ErrorVO`，统一包含状态码、消息和时间。`AssertUtil` 用于把常见前置条件统一为异常表达式，例如：

```java
AssertUtil.isTrue(user != null, new NotFoundException("User not found"));
```

## Processor 扩展能力

`BusinessProcessor` 是提供给 Starter 使用方的可选服务层编排能力，Pear 自身业务不依赖它。

调用方传入 `ProcessRequest<I>`、`ProcessResponse<O>` 和回调函数；回调返回 `void`，直接调用 `response.setData(...)` 写入结果。Processor 负责操作日志、traceId、耗时、异常包装和可选事务。事务模式通过 `TransactionSpec` 指定传播级别、隔离级别和超时；回调失败会写入出参并触发回滚。

## 缓存

缓存模块提供 LRU、LFU 和 Redis 策略。`CacheContainer` 负责缓存实例管理，`CacheFactory` 根据配置创建具体实现。缓存属性通过 `pear.starter.cache` 配置。

## 当前代码约定

- HTTP 入参使用 `XxxRequestDTO` 或 `XxxQueryDTO`。
- HTTP 出参使用 `ApiResponseVO<XxxVO>`；实体不会直接暴露。
- 旧的 `ExceptionResponse` 和 API 兼容重载已移除。
- `BaseEntity` 当前不声明数据库字段，避免在没有迁移方案时改变表结构或 Admin 元数据。
