# Pear 整改迭代记录

本文档记录三类子 Agent 的协作过程：

- Agent1：按 `docs/code-review-remediation-plan.md` 的顺序提出 issue 和整改意见。
- Agent2：根据 Agent1 的 issue 进行代码整改。
- Agent3：为 Agent2 完成的整改补充测试样例并验证。

每个 issue 按以下结构记录：

1. Issue 是什么。
2. 计划怎么解决。
3. 源代码原来是什么样子。
4. 修改后代码是什么样子。
5. 为什么这样修改。
6. 测试与验证结果。

## 迭代状态

| 序号 | Issue | 状态 | Agent1 | Agent2 | Agent3 |
| --- | --- | --- | --- | --- | --- |
| 1 | P0-1 CRUD 动态 SQL 注入风险 | 已完成 | 已提出 | 已整改 | 已测试 |
| 2 | P0-2 JWT 校验与用户上下文脱节 | 已完成 | 已提出 | 已整改 | 已测试 |
| 3 | P0-3 User 实体直接出接口导致敏感信息泄露 | 已完成 | 已提出 | 已整改 | 已测试 |
| 4 | P1-1 缓存模块并发与 LFU 逻辑问题 | 已完成 | 已提出 | 已整改 | 已测试 |
| 5 | P1-2 注解元数据构建缺陷 | 已完成 | 已提出 | 已整改 | 已测试 |
| 6 | P1-3 输入校验和异常处理不足 | 已完成 | 已提出 | 已整改 | 已测试 |
| 7 | P2-1 密码哈希强度不足 | 已完成 | 已提出 | 已整改 | 已测试 |
| 8 | P2-2 配置体系问题 | 已完成 | 已提出 | 已整改 | 已测试 |
| 9 | P2-3 测试覆盖不足 | 已完成 | 已提出 | 已整改 | 已测试 |
| 10 | P3 长期优化方向 | 已完成 | 已提出 | 已规划 | 已验证 |

## Issue 1: P0-1 CRUD 动态 SQL 注入风险

### 1. Issue 是什么

Agent1 提出：后台通用 CRUD 的动态 SQL 构造链路存在 SQL 注入风险，涉及查询、创建、更新、删除和按主键/唯一键查询。

主要范围：

- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/dao/AdminDAO.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/admin/impl/AdminServiceImpl.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/params/Filter.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/params/QueryForm.java`
- 必要时同步调整 `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/mapper/AdminMapper.java`

当前风险点：

- `filter.name`、`filter.value`、`keyword`、`orders.name`、`orders.op`、`viewFields`、`searchFields` 可影响 SQL。
- `selectFirst/create/update/delete/query` 当前大量使用字符串拼接值。
- `getPrimaryValues` 从 query 参数读取主键/唯一键值后直接进入 SQL。
- `LIKE`、`BETWEEN`、`IN`、`ORDER BY`、字段投影、表名等缺少统一白名单和参数绑定边界。

### 2. 计划怎么解决

Agent1 建议：

1. 建立字段白名单。
   - 查询展示字段只能来自 `adminObject.getShows()`。
   - 过滤字段只能来自 `adminObject.getFilterables()`。
   - 排序字段只能来自 `adminObject.getOrderables()`。
   - 搜索字段只能来自 `adminObject.getSearches()`。
   - 主键和唯一键只能来自 `adminObject.getPrimaryKeys()`、`adminObject.getUniqueKeys()`。
   - 创建和更新字段继续只能来自 `adminObject.getEdits()`。
2. 表名只能来自 `adminObject.getTableName()`。
3. SQL 值全部使用 MyBatis 参数绑定。
4. 分别安全处理 `LIKE`、`BETWEEN`、`IN`、排序、分页和字段投影。
5. 多个 filter 默认用 `AND` 组合，单个 filter 内多个 LIKE 值用 `OR`。
6. `keyword` 搜索与 filters 同时存在时使用 `AND` 连接。
7. 非法字段、非法操作符、非法排序方向、非法分页参数等返回 400。

### 3. 源代码原来是什么样子

原 `AdminDAO` 直接把用户传入值拼入 SQL：

```java
builder.append("`").append(entry.getKey()).append("`")
        .append(" = ").append("'").append(entry.getValue()).append("' ");
```

原列表查询把 filter、keyword、order 字段和值拼成 SQL 片段：

```java
String condition = String.format(
    "`%s`.`%s` LIKE '%%%s%%'",
    adminObject.getTableName(),
    filter.getName(),
    escapedValue
);
```

原 `update/delete/create` 使用路由中的 `model` 作为 DAO 表名来源：

```java
adminMapper.create(model, res);
adminMapper.update(model, keys, res);
adminMapper.delete(model, keys);
```

### 4. 修改后代码是什么样子

新增 `SqlQuery` 承载已校验 SQL 片段和绑定参数：

```java
public class SqlQuery {
    private String tableName;
    private String showClause;
    private String whereClause;
    private String orderClause;
    private int limit;
    private Map<String, Object> bindings = new HashMap<>();
}
```

DAO 值改为 MyBatis 参数绑定：

```java
builder.append("`")
        .append(entry.getKey())
        .append("` = #{keys['")
        .append(entry.getKey())
        .append("']} ");
```

查询条件由服务层构建，字段和排序方向先经过白名单校验：

```java
validateField(filter.getName(), allowedFilterFields, "filters");
String bindName = addBinding(sqlQuery, "%" + value + "%");
likes.add(column + " LIKE #{bindings." + bindName + "}");
```

所有 CRUD 表名改为来自 `AdminObject` 元数据：

```java
adminMapper.create(adminObject.getTableName(), res);
adminMapper.update(adminObject.getTableName(), keys, res);
adminMapper.delete(adminObject.getTableName(), keys);
adminMapper.selectFirst(adminObject.getTableName(), keys);
```

### 5. 为什么这样修改

这样修改把 SQL 中“可以拼接的部分”和“必须绑定的部分”拆开：

- 表名、字段名、排序方向属于 SQL 标识符，只允许来自 `AdminObject` 元数据和固定枚举。
- keyword、filter value、主键值、创建/更新值属于动态值，全部走 MyBatis `#{...}` 绑定。
- 多 filter 从原来的覆盖式拼接改成 `AND` 组合，LIKE 多值在单个 filter 内用 `OR`，keyword 与 filters 再用 `AND` 组合。
- 非法字段、非法操作符、非法排序方向会在服务层抛出 `IllegalException`，避免进入 DAO。

### 6. 测试与验证结果

Agent3 新增测试：

- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/admin/impl/AdminSqlInjectionSafetyTest.java`

覆盖点：

- `selectFirst/create/update/delete` 使用 `#{...}` 绑定，不内联注入值。
- query 中 `keyword`、`filter.value` 使用 `#{bindings...}` 绑定。
- 非法 filter 字段、非法 order 字段/方向、非法 `viewFields/searchFields` 抛 `IllegalException`。
- 多 filters 用 `AND`，LIKE 多值内部用 `OR`，keyword 与 filters 用 `AND`。
- `foreignMode=true` 时服务层把 `limit` 置为 `0` 后传入 mapper。

验证命令：

```bash
mvn test
```

验证结果：通过。主线程复跑 `mvn test`，全量 reactor `BUILD SUCCESS`，新增测试 5 个用例全部通过。

## Issue 2: P0-2 JWT 校验与用户上下文脱节

### 1. Issue 是什么

Agent1 提出：`JWTInterceptor` 只判断 token 是否可解析，没有把认证用户写入 request scope；而 `HookAspect` 通过 `UserService.currentUser(request)` 读取当前用户时主要依赖 session，导致纯 Authorization token 请求可能通过拦截器后又在权限切面中被判定为未登录。

同时，`JwtUtil` 还存在固定默认密钥、固定 token 前缀、`refreshToken` 返回错误值，以及解析 token 时未校验 issuer/subject 等问题。

主要范围：

- `pear-spring-boot-web/src/main/java/cn/muzisheng/pear/interceptor/JWTInterceptor.java`
- `pear-spring-boot-utils/src/main/java/cn/muzisheng/pear/JwtUtil.java`
- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/config/TokenConfig.java`
- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/properties/TokenProperties.java`
- `pear-spring-boot-constant/src/main/java/cn/muzisheng/pear/Constant.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/service/Impl/UserServiceImpl.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/service/UserService.java`

### 2. 计划怎么解决

1. 新增明确的 request scope key，用于保存 JWT 已认证用户。
2. `JWTInterceptor` 在 token 合法后根据 email 查询用户，并写入 request scope；缺失、过期、错误签名、错误前缀、用户不存在统一抛 `AuthorizationException`。
3. `UserService.currentUser(request)` 优先读取 request scope 中的当前用户，再兼容旧 request key 和 session fallback。
4. `JwtUtil` 改为使用 `TokenConfig` / `TokenProperties` 的密钥配置，token 前缀使用配置的 head，解析时校验 issuer 和 subject。
5. 修复 `refreshToken`，让它基于合法旧 token 重新生成新 token。
6. 保持 email/password 登录、authToken 登录、session fallback 和权限切面语义不变。

### 3. 源代码原来是什么样子

原 `JWTInterceptor` 只解析 token，不查询用户，也不写入当前用户上下文；失败时只返回 `false`：

```java
String token=request.getHeader("Authorization");
if(StringUtil.isEmpty(token)||jwtUtil.getEmailFromToken(token)==null){
    LOG.warn("Host with IP address "+request.getLocalAddr()+" was blocked. The token either does not exist or has expired.");
    return false;
}
return true;
```

原 `UserServiceImpl.currentUser` 主要依赖 session，request scope 也复用了 `SESSION_USER_ID`：

```java
Object objectUser = Context.getRequestScope(request,Constant.SESSION_USER_ID);
Object userId = Context.getSessionScope(request,Constant.SESSION_USER_ID);
```

原 `JwtUtil` 使用固定密钥和固定 Bearer 前缀，`refreshToken` 还把 email 当成 token 再解析：

```java
byte[] decode = Base64.getDecoder().decode(Constant.TOKEN_DEFAULT_SALT);
return token.replace(Constant.TOKEN_DEFAULT_SECRET_PREFIX,"");

public String refreshToken(String token) {
    String email = getEmailFromToken(token);
    return getEmailFromToken(email);
}
```

### 4. 修改后代码是什么样子

新增明确的请求级当前用户 key：

```java
public static final String REQUEST_CURRENT_USER = "_pear_current_user";
```

`JWTInterceptor` 在认证成功后查询用户并写入 request scope，失败时抛 `AuthorizationException`，交给全局异常处理返回 401：

```java
String token=request.getHeader("Authorization");
if(StringUtil.isEmpty(token)){
    throw new AuthorizationException("token missing");
}
String email = jwtUtil.getEmailFromToken(token);
User user = userDAO.getUserByEmail(email);
if (user == null) {
    throw new AuthorizationException("bad token");
}
Context.setRequestScope(request, Constant.REQUEST_CURRENT_USER, user);
return true;
```

`UserServiceImpl.currentUser` 改为统一读取顺序：新 request key、旧 request key、session fallback，并把查到的用户写回 request 缓存：

```java
User currentUser = resolveUserFromRequestValue(Context.getRequestScope(request, Constant.REQUEST_CURRENT_USER));
if (currentUser != null) {
    return cacheCurrentUser(request, currentUser);
}
User legacyRequestUser = resolveUserFromRequestValue(Context.getRequestScope(request, Constant.SESSION_USER_ID));
if (legacyRequestUser != null) {
    return cacheCurrentUser(request, legacyRequestUser);
}
HttpSession session = request.getSession(false);
```

`JwtUtil` 改为使用配置密钥、配置前缀，校验 issuer/subject，并正确刷新 token：

```java
this.KEY = buildKey(textOrDefault(tokenConfig.getSecret(), Constant.TOKEN_DEFAULT_SALT));

public String delTokenPrefix(String token){
    if (token == null || !token.startsWith(JWT_HEAD)) {
        throw new AuthorizationException("bad token prefix");
    }
    return token.substring(JWT_HEAD.length());
}

claims = Jwts.parser()
        .requireIssuer(JWT_ISSUE)
        .requireSubject(JWT_SUBJECT)
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(delTokenPrefix(token));

public String refreshToken(String token) {
    String email = getEmailFromToken(token);
    return generateTokenWithEmail(email);
}
```

### 5. 为什么这样修改

这样修改后，`/admin/**` 的认证入口和权限切面读取的是同一个用户上下文：JWT 拦截器负责认证并落 request scope，`HookAspect` 继续通过 `UserService.currentUser(request)` 获取当前用户，不需要改变权限判断语义。

同时保留 session fallback，避免破坏原有页面登录流程；JWT 请求不会创建 session，保持 token API 的无状态特征。

`JwtUtil` 使用 `pear.starter.token.secret` 后，不再把所有应用锁死在同一个默认签名密钥上；配置的 `head`、`issue`、`subject` 在生成和解析两端保持一致，`refreshToken` 也恢复为重新签发 token 的语义。

### 6. 测试与验证结果

Agent3 新增测试：

- `pear-spring-boot-utils/src/test/java/cn/muzisheng/pear/JwtUtilTest.java`
- `pear-spring-boot-web/src/test/java/cn/muzisheng/pear/interceptor/JWTInterceptorTest.java`
- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/service/Impl/UserServiceCurrentUserTest.java`

覆盖点：

- `JwtUtil` 生成、解析、自定义 head、错误前缀、issuer/subject 不匹配、弱密钥、refreshToken。
- `JWTInterceptor` 合法 token 写入 `REQUEST_CURRENT_USER`，缺失 token 和用户不存在返回认证异常，成功路径不创建 session。
- `UserService.currentUser` 优先 request 当前用户、兼容旧 request key、支持 session fallback 并写回 request 缓存。

验证命令：

```bash
mvn test
```

验证结果：通过。主线程复跑全量 reactor，`BUILD SUCCESS`；当前测试总数包括 `JwtUtilTest` 7 个、`JWTInterceptorTest` 3 个、`UserServiceCurrentUserTest` 4 个、`AdminSqlInjectionSafetyTest` 5 个，全部通过。

## Issue 3: P0-3 User 实体直接出接口导致敏感信息泄露

### 1. Issue 是什么

Agent1 提出：`/user/login` 和 `/user/info` 直接返回 `User` 实体，实体中包含 `password`、`token`、`lastLoginIp`、`profile` 等敏感或内部字段。后续只要实体新增字段，Jackson 也可能自动把字段带到外部响应中，扩大接口暴露面。

主要范围：

- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/entity/User.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/service/UserService.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/service/Impl/UserServiceImpl.java`
- `pear-spring-boot-web/src/main/java/cn/muzisheng/pear/api/UserApi.java`
- 必要时新增用户响应 DTO。

### 2. 计划怎么解决

1. 新增登录响应和当前用户响应 DTO，以白名单方式列出允许返回字段。
2. `UserService.login` 返回登录 DTO，保留 JWT token，但不再通过 `user.setToken(...)` 把 token 塞入实体。
3. `UserService.userInfo` 返回当前用户 DTO，默认不返回 token、password、lastLoginIp 等内部字段。
4. `UserService.currentUser(request)` 仍返回内部 `User` 实体，保持权限校验和 Issue 2 的认证上下文不变。
5. `User` 实体对 `password`、`token` 等字段加 Jackson 兜底保护。
6. 后台 Admin 元数据中默认隐藏、禁用搜索/过滤/排序/编辑敏感字段。

### 3. 源代码原来是什么样子

原用户接口直接把 `User` 实体作为响应数据：

```java
@PostMapping("login")
public ResponseEntity<Result<User>> login(HttpServletRequest request, @RequestBody LoginForm loginForm) {
    return userService.login(request, loginForm);
}
```

登录成功后把 token 临时写入实体，再返回实体：

```java
login(request, user);
user.setToken(jwtUtil.generateTokenWithEmail(user.getEmail()));
result.setData(user);
```

`User` 实体中的敏感字段没有 Jackson 序列化兜底保护，后台字段元数据也没有完全收紧：

```java
@PearField(isShow = false,isSearchable = false)
private String password;

@PearField
private String token;
```

### 4. 修改后代码是什么样子

新增白名单响应 DTO：

```java
public class LoginResponse {
    private Long id;
    private String email;
    private String displayName;
    private String firstName;
    private String lastName;
    private String avatar;
    private String locale;
    private String timezone;
    private String phone;
    private String token;
}
```

```java
public class CurrentUserResponse {
    private Long id;
    private String email;
    private String displayName;
    private String firstName;
    private String lastName;
    private String avatar;
    private String locale;
    private String timezone;
    private String phone;
}
```

`UserService` 和 `UserApi` 外部返回类型改为 DTO，`currentUser(request)` 仍返回内部实体：

```java
ResponseEntity<Result<LoginResponse>> login(HttpServletRequest request, LoginForm loginForm);
ResponseEntity<Result<CurrentUserResponse>> userInfo(HttpServletRequest request, String token);
User currentUser(HttpServletRequest request);
```

登录成功后生成 token 到 DTO，不再写入 `User.token`：

```java
login(request, user);
result.setData(LoginResponse.from(user, jwtUtil.generateTokenWithEmail(user.getEmail())));
```

实体增加 Jackson 兜底，并收紧后台 Admin 元数据：

```java
@JsonIgnore
@PearField(isShow = false, isSearchable = false, isFilterable = false, isOrderable = false)
private String password;

@JsonIgnore
@PearField(isShow = false, isEdit = false, isSearchable = false, isFilterable = false, isOrderable = false)
private String token;
```

### 5. 为什么这样修改

DTO 白名单响应把“数据库实体字段”和“外部接口字段”拆开，后续实体新增字段不会自动进入接口响应。

登录响应仍保留前端需要的 token 和公开用户资料；`/user/info` 默认不返回 token、password、lastLoginIp、profile、source 等敏感或内部字段。

`@JsonIgnore` 是第二道保护：即使未来误把 `User` 实体直接序列化，关键敏感字段也不会默认输出。后台 `@PearField` 同步收紧，避免用户表在 Admin 模块默认展示、搜索、过滤或排序敏感字段。

### 6. 测试与验证结果

Agent3 新增测试：

- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/params/UserResponseSerializationTest.java`
- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/service/Impl/UserServiceResponseTest.java`

覆盖点：

- `LoginResponse` 保留公开字段和 token，不包含 password。
- `CurrentUserResponse` 不包含 password、token、lastLoginIp、profile、source。
- `User` 实体直接 Jackson 序列化时隐藏 password、token、lastLoginIp、profile、source。
- `UserServiceImpl.login` 返回 `LoginResponse`，不写入实体 token，仍调用 `userDAO.setLastLogin`。
- `UserServiceImpl.userInfo` 返回 `CurrentUserResponse`，仍按当前用户 id 刷新查询数据库用户。

验证命令：

```bash
mvn test
```

验证结果：通过。主线程复跑全量 reactor，`BUILD SUCCESS`；新增 `UserResponseSerializationTest` 3 个用例、`UserServiceResponseTest` 2 个用例全部通过，Issue 2 的认证上下文测试仍通过。

## Issue 4: P1-1 缓存模块并发与 LFU 逻辑问题

### 1. Issue 是什么

Agent1 提出：本地缓存实现存在锁策略和 LFU 频率结构一致性问题。`LRUCacheTemplate` 在读锁下调用 access-order `LinkedHashMap.get`，实际会修改访问顺序；过期清理时还可能在读锁中调用写锁删除。`LFUCacheTemplate` 的 `cache`、`frequencyMap`、`frequencyKeysMap` 三份结构没有在同一写锁中维护，频率 bucket 写入存在错位，容量边界也可能超过上限。

主要范围：

- `pear-spring-boot-cache/src/main/java/cn/muzisheng/pear/cache/LRUCacheTemplate.java`
- `pear-spring-boot-cache/src/main/java/cn/muzisheng/pear/cache/LFUCacheTemplate.java`
- `pear-spring-boot-cache/src/main/java/cn/muzisheng/pear/CacheContainer.java`

### 2. 计划怎么解决

1. `LRUCacheTemplate.get` 改为写锁，命中过期 key 时直接在同一写锁下删除。
2. `LFUCacheTemplate.get/put/remove/clear` 统一使用写锁维护三份结构，`size/containsKey/toString` 使用读锁。
3. 修复 LFU 频率更新：`frequencyMap` 中的频率必须与 `frequencyKeysMap` bucket 一致。
4. 修复 LFU 容量判断：`capacity > 0 && size >= capacity` 时先淘汰再插入。
5. 增加内部无锁辅助方法，避免公共方法之间重复加锁。
6. 修复 `CacheContainer.createCache`，只依赖 `putIfAbsent` 的返回值，不再二次 `put` 覆盖。

### 3. 源代码原来是什么样子

原 LRU 在读锁下调用 access-order `LinkedHashMap.get`，并在过期分支中调用公共 `remove`：

```java
readLock.lock();
try{
    ExpiredCacheValue<V> expiredCacheValue= cache.get(key);
    if(System.currentTimeMillis()-expiredCacheValue.getLastTime()<expire * timeUnit.toMillis(1)||expire==0L){
        expiredCacheValue.setLastTime(System.currentTimeMillis());
        return expiredCacheValue.getVal();
    }else {
        remove(key);
        return null;
    }
}finally {
    readLock.unlock();
}
```

原 LFU 频率更新把 key 从错误 bucket 删除，又加入 `frequency + 1` bucket，导致 `frequencyMap` 与 `frequencyKeysMap` 不一致：

```java
int frequency = frequencyMap.getOrDefault(key, 0) + 1;
frequencyMap.put(key, frequency);
if (frequencyKeysMap.containsKey(frequency)) {
    frequencyKeysMap.get(frequency).remove(key);
}
frequencyKeysMap.computeIfAbsent(frequency + 1, k -> new LinkedHashSet<>()).add(key);
```

原 `CacheContainer.createCache` 在 `putIfAbsent` 成功后又执行一次 `put`：

```java
CacheTemplate<?, ?> existing = cacheTemplateMap.putIfAbsent(cacheName, cacheTemplate);
...
cacheTemplateMap.put(cacheName, cacheTemplate);
```

### 4. 修改后代码是什么样子

LRU `get` 改为写锁，并在同一锁内清理过期 key：

```java
writeLock.lock();
try{
    ExpiredCacheValue<V> expiredCacheValue= cache.get(key);
    if(expiredCacheValue==null) return null;
    if(System.currentTimeMillis()-expiredCacheValue.getLastTime()<expire * timeUnit.toMillis(1)||expire==0L){
        expiredCacheValue.setLastTime(System.currentTimeMillis());
        return expiredCacheValue.getVal();
    }else {
        cache.remove(key);
        return null;
    }
}finally {
    writeLock.unlock();
}
```

LFU 统一使用内部无锁辅助方法维护三份结构：

```java
private void incrementFrequencyInternal(K key) {
    Integer oldFrequency = frequencyMap.get(key);
    if (oldFrequency == null) {
        oldFrequency = 0;
    }
    if (oldFrequency > 0) {
        LinkedHashSet<K> oldKeys = frequencyKeysMap.get(oldFrequency);
        if (oldKeys != null) {
            oldKeys.remove(key);
            if (oldKeys.isEmpty()) {
                frequencyKeysMap.remove(oldFrequency);
            }
        }
    }
    int newFrequency = oldFrequency + 1;
    frequencyMap.put(key, newFrequency);
    frequencyKeysMap.computeIfAbsent(newFrequency, k -> new LinkedHashSet<>()).add(key);
}
```

插入新 key 前修复容量边界：

```java
if (capacity > 0 && cache.size() >= capacity) {
    evictLeastFrequentInternal();
}
```

`CacheContainer.createCache` 只使用 `putIfAbsent` 的插入结果，不再二次覆盖：

```java
CacheTemplate<?, ?> existing = cacheTemplateMap.putIfAbsent(cacheName, cacheTemplate);
if (existing != null) {
    if (!existing.getKeyClass().equals(keyClass)||!existing.getValueClass().equals(valueClass)) {
        throw new CacheException(...);
    }
    return true;
}
return true;
```

### 5. 为什么这样修改

access-order `LinkedHashMap.get` 会调整节点顺序，必须按写操作处理；过期删除也在同一写锁下完成，避免读锁升级写锁的死锁风险。

LFU 的 `cache`、`frequencyMap`、`frequencyKeysMap` 是同一个状态机的三份视图，必须在同一写锁内同步维护。通过 `removeInternal`、`incrementFrequencyInternal`、`evictLeastFrequentInternal` 拆出内部方法后，公共方法不再互相套锁，逻辑更直。

`putIfAbsent` 本身已经是并发插入动作，移除后续 `put` 可以避免并发场景覆盖其他线程刚创建的缓存实例。

### 6. 测试与验证结果

Agent3 新增测试：

- `pear-spring-boot-cache/src/test/java/cn/muzisheng/pear/cache/LRUCacheTemplateTest.java`
- `pear-spring-boot-cache/src/test/java/cn/muzisheng/pear/cache/LFUCacheTemplateTest.java`
- `pear-spring-boot-cache/src/test/java/cn/muzisheng/pear/CacheContainerTest.java`

覆盖点：

- LRU 容量淘汰、访问顺序刷新、过期清理、无限容量、并发 put/get/remove。
- LFU 最低频淘汰、同频稳定淘汰、get 增频、put 已存在 key 增频、过期清理、remove 缺失 key、无限容量、并发 put/get/remove。
- LFU 通过反射校验 `cache`、`frequencyMap`、`frequencyKeysMap` 三份结构一致。
- `CacheContainer.createCache` 重复同名同类型不覆盖已有缓存，同名不同类型抛 `CacheException`。

验证命令：

```bash
mvn test
```

验证结果：通过。主线程复跑全量 reactor，`BUILD SUCCESS`；缓存模块新增 15 个用例全部通过。

## Issue 5: P1-2 注解元数据构建缺陷

### 1. Issue 是什么

Agent1 提出：`@PearObject`、`@PearField`、JPA 注解、MyBatis-Plus 注解到 `AdminObject` / `AdminField` 的元数据构建链路存在缺陷。典型问题包括：`path/pluralName` 非空时没有写入，`@Entity(name="")` 会生成空表名，初始化阶段扫描父类字段但最终 `parseFields` 只扫描当前类字段，字段顺序不稳定，非数据库字段可能进入 SQL 白名单。

主要范围：

- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/initialize/PearBeanInitialization.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/initialize/AdminContainer.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/annotation/PearObject.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/annotation/PearField.java`

### 2. 计划怎么解决

1. 完整写入 `@PearObject` 的 path、pluralName、editPage、listPage、iconUrl、isInvisible、desc、group。
2. 统一表名解析顺序：`@Table(name)` > 非空 `@Entity(name)` > `@TableName` > `@PearObject.TableName` > 类名 snake_case。
3. 表名、字段名统一 trim、去反引号，空值不能覆盖默认值。
4. 建立统一字段扫描方法，支持继承字段，字段顺序稳定，子类同名字段覆盖父类。
5. 初始化白名单和 `AdminContainer.parseFields` 使用一致的字段名解析与字段集合。
6. `@Transient`、`@TableField(exist=false)` 标记为 `notColumn=true`，默认不进入 SQL 相关白名单。
7. `AdminContainer` 增加按 Class 注册和获取，修复自定义表名实体无法通过 Class 获取的问题。
8. 对空表名、重复表名、无主键/唯一键等严重元数据错误 fail-fast。

### 3. 源代码原来是什么样子

原 `@PearObject.path` 和 `pluralName` 只有为空时才写默认值，非空注解值没有写入：

```java
if (pearObjectAnnotation.path() == null || pearObjectAnnotation.path().isEmpty()) {
    object.setPath("/" + object.getName());
}
if (pearObjectAnnotation.pluralName() == null || pearObjectAnnotation.pluralName().isEmpty()) {
    object.setPluralName(PluralUtil.pluralize(object.getName()));
}
```

原表名解析只要存在 `@Entity` 就使用 `entity.name()`，默认空值会生成空表名：

```java
if (clazz.isAnnotationPresent(Entity.class)) {
    Entity entity = clazz.getAnnotation(Entity.class);
    object.setTableName(entity.name().replaceAll("`", ""));
}
```

原初始化字段使用 `HashSet<Field>`，顺序不稳定；`AdminContainer.parseFields` 只扫描当前类字段：

```java
HashSet<Field> fieldsSet = new HashSet<>();
...
for (Field field : model.getDeclaredFields()) {
```

### 4. 修改后代码是什么样子

`@PearObject` 元数据完整写入，空值才回落默认：

```java
object.setPath(defaultIfBlank(pearObjectAnnotation.path(), "/" + object.getName()));
object.setPluralName(defaultIfBlank(pearObjectAnnotation.pluralName(), PluralUtil.pluralize(object.getName())));
object.setEditPage(emptyToNull(pearObjectAnnotation.editPage()));
object.setListPage(emptyToNull(pearObjectAnnotation.listPage()));
object.setInvisible(pearObjectAnnotation.isInvisible());
```

新增统一表名解析：

```java
public static String resolveTableName(Class<?> clazz, PearObject pearObject) {
    String tableName = "";
    Table table = clazz.getAnnotation(Table.class);
    if (table != null) {
        tableName = normalizeIdentifier(table.name());
    }
    if (tableName.isEmpty()) {
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity != null) {
            tableName = normalizeIdentifier(entity.name());
        }
    }
    if (tableName.isEmpty()) {
        TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
        if (tableNameAnnotation != null) {
            tableName = normalizeIdentifier(tableNameAnnotation.value());
        }
    }
    ...
}
```

新增统一字段扫描和列名解析：

```java
public static List<Field> scanModelFields(Class<?> model) {
    LinkedList<Class<?>> hierarchy = new LinkedList<>();
    ...
    LinkedHashMap<String, Field> fields = new LinkedHashMap<>();
    ...
    return new ArrayList<>(fields.values());
}

public static String resolveColumnName(Field field) {
    Column column = field.getAnnotation(Column.class);
    ...
    return normalizeIdentifier(CamelToSnakeUtil.toSnakeCase(field.getName()));
}
```

非数据库字段不进入 SQL 白名单：

```java
if (AdminContainer.isNotColumn(field)) {
    continue;
}
```

`AdminContainer` 增加按类注册和获取，并对严重元数据错误 fail-fast：

```java
private static final Map<Class<?>, AdminObject> adminObjectClassMap = new HashMap<>();

public static void buildAdminObjects(List<AdminObject> objects) {
    for (AdminObject adminObject : objects) {
        build(adminObject);
    }
}
```

### 5. 为什么这样修改

统一表名、字段名和字段扫描规则后，初始化阶段的白名单和最终 `AdminField.fields` 来自同一套解析逻辑，避免 CRUD 安全白名单和前端元数据不一致。

非数据库字段默认从 SQL 白名单中排除，可以防止虚拟字段进入查询、过滤、排序、主键或唯一键链路。

按 class 注册 `AdminObject` 后，自定义表名实体不再依赖类名反推表名；重复表名、空表名、无主键/唯一键等严重问题直接启动期失败，避免带着不完整元数据继续运行。

### 6. 测试与验证结果

Agent3 新增测试：

- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/initialize/AdminMetadataInitializationTest.java`

覆盖点：

- `@PearObject` 配置值和默认值。
- 表名优先级、`@Entity` 空 name、`@TableName` 去反引号。
- 继承字段扫描、子类覆盖父类、字段顺序稳定。
- `@Column(name)`、`@TableField(value)` 空值不覆盖默认列名。
- `@Transient`、`@TableField(exist=false)` 标记 `notColumn=true` 且不进入 SQL 白名单/key。
- `@Id`、`@Column(unique=true)`、`@PearField(isPrimaryKey/isUniqueKey)` key 来源。
- 重复表名、无主键/唯一键 fail-fast，`AdminFactory(Class<?>)` 按类获取。
- 外键 `xxxId` 可解析继承字段。

验证命令：

```bash
mvn test
```

验证结果：通过。主线程复跑全量 reactor，`BUILD SUCCESS`；新增元数据测试 8 个用例全部通过。

## Issue 6: P1-3 输入校验和异常处理不足

### 1. Issue 是什么

Agent1 提出：后台 CRUD 接口允许空请求体，但服务层部分路径直接访问对象方法，容易 NPE；类型转换失败可能被静默转为 `0/null` 或包装成不准确异常；全局异常处理缺少 Spring MVC 常见请求错误，并且部分自定义异常映射到了非常规 HTTP 状态码。

主要范围：

- `pear-spring-boot-web/src/main/java/cn/muzisheng/pear/api/AdminApi.java`
- `pear-spring-boot-web/src/main/java/cn/muzisheng/pear/api/ExceptionApi.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/admin/impl/AdminServiceImpl.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/params/QueryForm.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/params/Filter.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/model/Order.java`

### 2. 计划怎么解决

1. 创建、更新空 body 返回 400，不再触发 NPE。
2. 查询无 body 且无主键/唯一键时执行默认列表查询；无 body 且带 key 时保留单条查询。
3. `QueryForm`、`Filter`、`Order` 增加显式校验方法，服务层在构建 SQL 前执行。
4. `convertValue` 增加字段名上下文，数字、日期、布尔等转换失败抛 `IllegalException`，返回 400。
5. 创建请求校验必填字段缺失。
6. model 不存在不再返回 HTTP 200，改为抛异常由全局异常处理统一响应。
7. `ExceptionApi` 增加 Spring MVC 常见请求异常处理，修正不合理 HTTP 状态码。
8. 保持 Issue 1 SQL 白名单、Issue 2 认证和 Issue 5 元数据语义不变。

### 3. 源代码原来是什么样子

原 Admin API 在 model 不存在时返回普通 `Result`，可能是 HTTP 200 + error body：

```java
Response<Object> response = new Response<>();
response.setError("Model not found");
response.setStatus(Constant.ILLEGAL_EXCEPTION);
return response.value();
```

原 update 空 body 会直接调用 `data.isEmpty()`：

```java
if (keys.isEmpty() || data.isEmpty()) {
    throw new IllegalException("The parameter or primary key is empty.");
}
```

原查询无 body 时固定走单条查询，缺少 key 会报 `invalid primary key`：

```java
if (request.getContentLength() <= 0) {
    return handleGetOne(request, model, adminObject, queryForm);
}
```

原类型转换会把非法布尔、空数字或解析失败静默转为 `0/null`：

```java
} else {
    val = 0;
}
...
} catch (TimeException e) {
    val = null;
}
```

### 4. 修改后代码是什么样子

model 不存在改为抛 `NotFoundException`：

```java
if(AdminContainer.existsAdminObject(model)) {
    return adminService.handleCreate(request, model, new AdminContainer.AdminFactory(model).getAdminObject(), data);
}
throw new NotFoundException("Model not found: " + model);
```

空 body 创建/更新统一返回 `IllegalException`：

```java
if (data == null || data.isEmpty()) {
    throw new IllegalException("The request body cannot be empty.");
}
```

无 body 查询根据 query key 分流；无 key 时走默认列表查询：

```java
if (request.getContentLength() <= 0) {
    if (!getPrimaryValues(request, adminObject).isEmpty()) {
        return handleGetOne(request, model, adminObject, queryForm);
    }
    queryForm = new QueryForm();
    AdminQueryResult result = queryObjects(request, model, adminObject, queryForm);
    response.setData(result);
    return response.value();
}
```

查询 DTO 增加结构校验：

```java
public void validate() {
    defaultPrepareQuery();
    validateFilters();
    validateOrders();
    validateFieldList(viewFields, "viewFields");
    validateFieldList(searchFields, "searchFields");
}
```

类型转换带字段名上下文，失败抛 400：

```java
private Object convertValue(String fieldName, Class<?> targetClass, Object val) {
    ...
} catch (RuntimeException e) {
    throw invalidFieldValue(fieldName);
}
```

异常处理统一 HTTP 状态码，并保留 `status/message/time` 响应结构：

```java
@ExceptionHandler(NotFoundException.class)
public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
    return response(Constant.NOT_FOUND_EXCEPTION, e.getMessage(), HttpStatus.NOT_FOUND);
}
```

### 5. 为什么这样修改

客户端输入错误现在在进入数据库之前就被拦截并返回 400，避免 NPE、静默转换或包装成 SQL 异常。无 body 查询的语义也更清晰：带 key 查单条，不带 key 查默认列表。

异常状态码从“能表达业务状态”改为“符合 HTTP 语义”：认证 401、权限 403、资源不存在 404、请求错误 400、服务端错误 500。响应结构保持兼容，避免额外破坏前端。

### 6. 测试与验证结果

Agent3 新增测试：

- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/params/QueryValidationTest.java`
- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/admin/impl/AdminInputValidationTest.java`
- `pear-spring-boot-web/src/test/java/cn/muzisheng/pear/api/AdminApiValidationTest.java`
- `pear-spring-boot-web/src/test/java/cn/muzisheng/pear/api/ExceptionApiTest.java`

覆盖点：

- `QueryForm`、`Filter`、`Order` 的非法结构校验。
- 创建/更新空 body，创建 required 字段缺失和自动字段豁免。
- `unmarshalFrom` 非法数字、日期、布尔转换失败返回 `IllegalException`，合法 `LocalDate` 返回 `LocalDate`。
- 无 body 无 key 查询走列表，有 key 查询走单条。
- Admin API model 不存在抛 `NotFoundException`。
- `ExceptionApi` 的 400/401/403/404/500 映射和兜底异常通用文案。

验证命令：

```bash
mvn test
```

验证结果：通过。主线程复跑全量 reactor，`BUILD SUCCESS`；新增 P1-3 测试全部通过，Issue 1/2/5 相关测试仍通过。

## Issue 7: P2-1 密码哈希强度不足

### 1. Issue 是什么

Agent1 提出：当前 `UserDAO.HashPassword` 使用 `SHA-256(passwordSalt + password)` 存储密码，不适合密码存储；全局盐不能替代每个密码独立 salt；空密码可能被写入；登录校验不支持算法升级；旧 hash 没有版本标记，后续迁移困难。

主要范围：

- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/dao/UserDAO.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/service/Impl/UserServiceImpl.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/initialize/PearApplicationInitialization.java`
- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/config/UserConfig.java`
- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/properties/UserProperties.java`
- 必要时新增密码服务和依赖。

### 2. 计划怎么解决

1. 引入 BCrypt 强哈希，新密码带算法版本标识，例如 `{bcrypt}`。
2. 保留旧 SHA-256 64 位 hex 校验能力，作为迁移兼容。
3. 老用户正确密码登录成功后自动迁移到新 BCrypt hash。
4. 新注册、重置密码、命令行创建超级管理员都使用新 hash。
5. 所有密码入口拒绝 null、空字符串和空白字符串。
6. `passwordSalt` 只作为 legacy SHA-256 兼容盐，不再用于生成新密码。
7. BCrypt strength 可配置，非法配置 fail-fast。

### 3. 源代码原来是什么样子

原 `UserDAO.HashPassword` 使用全局盐拼接密码后做 SHA-256，且空密码会返回空字符串：

```java
public String HashPassword(String password) {
    if("".equals(password)){
        return "";
    }
    MessageDigest md5=MessageDigest.getInstance("SHA-256");
    byte[] digest=md5.digest((userConfig.getPasswordSalt() + password).getBytes());
    return bytesToHex(digest);
}
```

原登录校验只做哈希字符串等值比较，不支持算法版本、独立 salt 或旧密码自动迁移：

```java
public boolean checkPassword(User user, String password) {
    return user.getPassword().equals(userDAO.HashPassword(password));
}
```

原命令行初始化超级管理员会直接接收 `-p` 参数，日志中还会输出明文密码：

```java
LOG.info("super user password: "+argMap.getOrDefault("p",""));
userDAO.createUser((String)argMap.getOrDefault("u","admin"),(String)argMap.getOrDefault("p",""));
```

### 4. 修改后代码是什么样子

新增 `PasswordService`，新密码统一生成带版本前缀的 BCrypt 哈希：

```java
public String encode(String raw) {
    validateRawPassword(raw);
    return BCRYPT_PREFIX + bcryptPasswordEncoder.encode(raw);
}
```

同一个服务兼容旧 SHA-256 哈希校验，便于老用户平滑迁移：

```java
public boolean matches(String raw, String stored) {
    validateRawPassword(raw);
    if (stored == null || stored.isBlank()) {
        return false;
    }
    if (stored.startsWith(BCRYPT_PREFIX)) {
        return bcryptPasswordEncoder.matches(raw, stored.substring(BCRYPT_PREFIX.length()));
    }
    if (isLegacySha256Hash(stored)) {
        return legacyHash(raw).equalsIgnoreCase(stored);
    }
    return false;
}
```

`UserDAO.HashPassword` 改为委托 `PasswordService.encode`，同时保留 `legacyHashPassword` 供兼容迁移和测试使用：

```java
public String HashPassword(String password) {
    return passwordService.encode(password);
}

public String legacyHashPassword(String password) {
    return passwordService.legacyHash(password);
}
```

`UserServiceImpl.checkPassword` 在旧 hash 匹配后自动升级为 BCrypt；升级失败只记录警告，不影响本次合法登录：

```java
public boolean checkPassword(User user, String password) {
    if (password == null || password.isBlank()) {
        throw new IllegalException("Password cannot be empty.");
    }
    String stored = user.getPassword();
    if (stored == null || stored.isBlank()) {
        return false;
    }
    if (userDAO.getPasswordService().matches(password, stored)) {
        upgradePasswordIfNeeded(user, password, stored);
        return true;
    }
    return stored.equals(userDAO.HashPassword(password));
}
```

`UserConfig`/`UserProperties` 新增 `bcryptStrength` 和 `passwordAlgorithm`，并对非法配置 fail-fast：

```java
if (bcryptStrength < 4 || bcryptStrength > 31) {
    throw new IllegalException("bcryptStrength must be between 4 and 31.");
}
if (!PASSWORD_ALGORITHM_BCRYPT.equalsIgnoreCase(passwordAlgorithm)) {
    throw new IllegalException("Unsupported passwordAlgorithm: " + passwordAlgorithm);
}
```

超级管理员初始化拒绝空白密码，日志不再输出明文密码：

```java
String password = (String) argMap.getOrDefault("p", "");
if (password.isBlank()) {
    throw new IllegalException("super user password cannot be empty");
}
LOG.info("super user password: [PROTECTED]");
userDAO.createUser(username, password);
```

### 5. 为什么这样修改

BCrypt 自带随机 salt 和成本因子，适合密码存储；`{bcrypt}` 前缀让存储值具备算法版本，后续再升级算法时可以按前缀分流。保留旧 SHA-256 校验能力，可以避免升级后老用户立刻无法登录；在登录成功后自动迁移，则能逐步把真实活跃用户升级到新格式。

拒绝空白密码可以防止空密码或空 hash 被写入。命令行初始化不输出明文密码，可以减少控制台、CI 日志或运维日志中的敏感信息泄露。

### 6. 测试与验证结果

Agent3 补充测试：

- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/service/PasswordServiceTest.java`
- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/dao/UserDAOPasswordTest.java`
- `pear-spring-boot-core/src/test/java/cn/muzisheng/pear/service/Impl/UserPasswordMigrationTest.java`

覆盖点：

- 新密码生成 `{bcrypt}` 前缀，不再是明文或旧 SHA-256 hex。
- BCrypt 正确密码匹配，错误密码拒绝。
- 旧 SHA-256 hash 可识别、可校验，并被标记为需要升级。
- `createUser`、`setPassword`、`HashPassword` 拒绝空白密码。
- 旧密码登录成功后迁移为 BCrypt，错误密码不会迁移。
- 登录接口仍返回 `LoginResponse` 和 Bearer token。
- `bcryptStrength` 与 `passwordAlgorithm` 非法配置抛 `IllegalException`。

验证命令：

```bash
mvn test
```

验证结果：通过。主线程复跑全量 reactor，`BUILD SUCCESS`；本次新增 10 个密码相关测试用例全部通过，前序 CRUD、JWT、缓存、注解元数据、输入校验和异常处理测试仍通过。

## Issue 8: P2-2 配置体系问题

### 1. Issue 是什么

Agent1 按计划继续提出：配置体系存在两个确定性问题。其一，`CacheProperties` 中时间单位缩写映射错误，例如 `ms` 被映射为秒，`s` 被映射为分钟，导致缓存过期时间和用户配置不一致。其二，`SqlAutoConfiguration` 使用 `spring.datasource.url=mysql` 精确匹配，真实 JDBC URL 一般是 `jdbc:mysql://...`，导致 MySQL schema 初始化基本不会触发。

主要范围：

- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/properties/CacheProperties.java`
- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/config/auto/SqlAutoConfiguration.java`
- 必要时补充配置属性/条件类和测试。

### 2. 计划怎么解决

1. 修正缓存时间单位缩写：`ms` 为毫秒，`s` 为秒，`m` 为分钟，`h` 为小时，`d` 为天。
2. 支持标准 `TimeUnit` 名称和常见缩写，非法 time-unit fail-fast。
3. 非法 cache strategy 不再静默忽略，改为明确启动错误。
4. SQL 初始化增加显式开关 `pear.starter.sql.init.enabled`：未配置时根据 `jdbc:mysql:` 自动启用，`true` 时强制启用，`false` 时禁用。
5. SQL 初始化属性尽早写入 Spring Environment，避免 `CommandLineRunner` 太晚才设置系统属性。

### 3. 源代码原来是什么样子

原 `CacheProperties` 将缩写整体错位映射，`us` 被映射为毫秒，`ms` 被映射为秒，`s` 被映射为分钟：

```java
switch (newTimeUnit) {
    case "ns" -> newTimeUnit = Constant.NANOSECONDS_TIME_UNIT;
    case "µs" -> newTimeUnit = Constant.MICROSECONDS_TIME_UNIT;
    case "us" -> newTimeUnit = Constant.MILLISECONDS_TIME_UNIT;
    case "ms" -> newTimeUnit = Constant.SECONDS_TIME_UNIT;
    case "s" -> newTimeUnit = Constant.MINUTES_TIME_UNIT;
    case "m" -> newTimeUnit = Constant.HOURS_TIME_UNIT;
    case "h" -> newTimeUnit = Constant.DAYS_TIME_UNIT;
}
```

原 cache strategy 非法时被静默忽略，用户以为配置生效，实际仍使用默认值：

```java
if (strategy != null&& !strategy.isEmpty()&& CacheStrategyEnum.contains(strategy)){
    config.setStrategy(CacheStrategyEnum.strategyOf( strategy));
}
```

原 SQL 初始化只在 `spring.datasource.url` 精确等于 `mysql` 时生效，并在 `CommandLineRunner` 阶段设置系统属性，时机太晚：

```java
@ConditionalOnProperty(
        prefix = "spring.datasource",
        name = "url",
        havingValue = "mysql"
)
public class SqlAutoConfiguration {
    public CommandLineRunner starterSqlInitProps() {
        System.setProperty("spring.sql.init.mode", "always");
        System.setProperty("spring.sql.init.schema-locations", "classpath:schema-mysql.sql");
        return args -> {};
    }
}
```

### 4. 修改后代码是什么样子

`CacheProperties` 改为显式解析缩写和标准 `TimeUnit` 名称，非法值抛 `GeneralException`：

```java
public TimeUnit parseTimeUnit(String value) {
    String normalized = value.trim().toLowerCase(Locale.ROOT);
    String timeUnitName = switch (normalized) {
        case "ns", "nano", "nanos", "nanosecond", "nanoseconds" -> Constant.NANOSECONDS_TIME_UNIT;
        case "µs", "us", "micro", "micros", "microsecond", "microseconds" -> Constant.MICROSECONDS_TIME_UNIT;
        case "ms", "milli", "millis", "millisecond", "milliseconds" -> Constant.MILLISECONDS_TIME_UNIT;
        case "s", "sec", "secs", "second", "seconds" -> Constant.SECONDS_TIME_UNIT;
        case "m", "min", "mins", "minute", "minutes" -> Constant.MINUTES_TIME_UNIT;
        case "h", "hr", "hrs", "hour", "hours" -> Constant.HOURS_TIME_UNIT;
        case "d", "day", "days" -> Constant.DAYS_TIME_UNIT;
        default -> value.trim().toUpperCase(Locale.ROOT);
    };
    return TimeUnit.valueOf(timeUnitName);
}
```

cache strategy 配置改为大小写兼容且非法 fail-fast：

```java
String normalizedStrategy = strategy.trim().toUpperCase(Locale.ROOT);
if (!CacheStrategyEnum.contains(normalizedStrategy)) {
    throw new GeneralException("Invalid cache strategy: " + strategy);
}
config.setStrategy(CacheStrategyEnum.strategyOf(normalizedStrategy));
```

新增 `PearSqlInitEnvironmentPostProcessor`，未显式配置时按标准 MySQL JDBC URL 自动启用；显式开关可覆盖自动判断；默认属性通过 Environment 提前注入，且不覆盖用户自定义 `spring.sql.init.*`：

```java
public static boolean isSqlInitEnabled(Environment environment) {
    Boolean configured = environment.getProperty(SQL_INIT_ENABLED_PROPERTY, Boolean.class);
    if (configured != null) {
        return configured;
    }
    return isMysqlJdbcUrl(environment.getProperty("spring.datasource.url"));
}

public static boolean isMysqlJdbcUrl(String datasourceUrl) {
    return datasourceUrl != null
            && datasourceUrl.trim().toLowerCase(Locale.ROOT).startsWith("jdbc:mysql:");
}
```

`spring.factories` 注册环境后处理器：

```properties
org.springframework.boot.env.EnvironmentPostProcessor=\
cn.muzisheng.pear.config.auto.PearSqlInitEnvironmentPostProcessor
```

### 5. 为什么这样修改

缓存配置属于启动期基础行为，错误单位会直接改变过期时间语义，静默忽略非法 strategy 也会让线上行为难以判断；因此改成明确映射和 fail-fast。

Spring SQL 初始化需要在 Boot 的初始化流程读取配置前就准备好属性，`CommandLineRunner` 太晚。使用 `EnvironmentPostProcessor` 可以在环境准备阶段写入默认值；同时只在 MySQL URL 或显式开启时生效，避免非 MySQL 数据源误执行 MySQL schema。

### 6. 测试与验证结果

Agent3 补充测试：

- `pear-spring-boot-basic/src/test/java/cn/muzisheng/pear/properties/CachePropertiesTest.java`
- `pear-spring-boot-basic/src/test/java/cn/muzisheng/pear/config/auto/PearSqlInitEnvironmentPostProcessorTest.java`

覆盖点：

- `ns/us/µs/ms/s/m/h/d` 和标准名称 `MILLISECONDS`、`seconds` 解析为正确 `TimeUnit`。
- 非法 time-unit 抛 `GeneralException`。
- 合法 strategy 大小写兼容，非法 strategy 抛 `GeneralException`。
- `jdbc:mysql://...` 未显式配置时写入 SQL 初始化默认属性。
- 显式 `pear.starter.sql.init.enabled=false` 禁用。
- 显式 `pear.starter.sql.init.enabled=true` 在非 MySQL URL 下也启用。
- 用户已配置的 `spring.sql.init.mode`、`spring.sql.init.schema-locations` 不被覆盖。

验证命令：

```bash
mvn test -pl pear-spring-boot-basic -am
```

验证结果：通过。新增 7 个配置相关测试用例全部通过；主线程随后复跑全量 `mvn test`，reactor `BUILD SUCCESS`，前序所有测试仍通过。

## Issue 9: P2-3 测试覆盖不足

### 1. Issue 是什么

Agent1 按计划继续提出：项目原先缺少有效回归测试，尤其是 CRUD 动态 SQL、JWT/权限、注解扫描、缓存并发、配置绑定和用户登录注册等关键路径。没有测试会导致安全修复和兼容性整改后难以及时发现回归。

涉及范围：

- CRUD 动态 SQL 与参数校验。
- 权限与认证。
- 注解扫描。
- 缓存并发。
- 配置绑定。
- 用户登录注册。

### 2. 计划怎么解决

本 issue 作为测试体系收口处理，不额外修改生产功能。前序每个 issue 已经按风险点补充测试，本轮确认覆盖清单和 Maven `test` 阶段执行情况：

1. P0 修复必须有回归测试。
2. 缓存并发测试需要稳定通过。
3. 配置绑定和非法配置需要 fail-fast 测试。
4. JWT、用户响应、登录注册、旧密码迁移需要认证和安全测试。
5. 注解扫描和 CRUD SQL 构造需要覆盖核心边界。

### 3. 源代码原来是什么样子

整改前项目基本没有有效测试目录，`mvn test` 多数模块显示 “No tests to run”，P0 安全修复缺少回归保障。

### 4. 修改后代码是什么样子

当前测试文件清单：

```text
pear-spring-boot-basic/src/test/java/cn/muzisheng/pear/config/auto/PearSqlInitEnvironmentPostProcessorTest.java
pear-spring-boot-basic/src/test/java/cn/muzisheng/pear/properties/CachePropertiesTest.java
pear-spring-boot-cache/src/test/java/cn/muzisheng/pear/CacheContainerTest.java
pear-spring-boot-cache/src/test/java/cn/muzisheng/pear/cache/LFUCacheTemplateTest.java
pear-spring-boot-cache/src/test/java/cn/muzisheng/pear/cache/LRUCacheTemplateTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/admin/impl/AdminInputValidationTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/admin/impl/AdminSqlInjectionSafetyTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/dao/UserDAOPasswordTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/initialize/AdminMetadataInitializationTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/params/QueryValidationTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/params/UserResponseSerializationTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/service/Impl/UserPasswordMigrationTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/service/Impl/UserServiceCurrentUserTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/service/Impl/UserServiceResponseTest.java
pear-spring-boot-core/src/test/java/cn/muzisheng/pear/service/PasswordServiceTest.java
pear-spring-boot-utils/src/test/java/cn/muzisheng/pear/JwtUtilTest.java
pear-spring-boot-web/src/test/java/cn/muzisheng/pear/api/AdminApiValidationTest.java
pear-spring-boot-web/src/test/java/cn/muzisheng/pear/api/ExceptionApiTest.java
pear-spring-boot-web/src/test/java/cn/muzisheng/pear/interceptor/JWTInterceptorTest.java
```

### 5. 为什么这样修改

这批测试围绕前序整改的真实风险写，而不是只追求覆盖率数字：

- `AdminSqlInjectionSafetyTest` 固定 SQL 注入防线。
- `JWTInterceptorTest`、`JwtUtilTest`、`UserServiceCurrentUserTest` 固定认证上下文。
- `UserResponseSerializationTest` 固定敏感字段不出接口。
- `LRUCacheTemplateTest`、`LFUCacheTemplateTest` 固定并发读写与淘汰结构一致性。
- `AdminMetadataInitializationTest` 固定注解元数据扫描。
- `CachePropertiesTest`、`PearSqlInitEnvironmentPostProcessorTest` 固定配置绑定语义。
- `PasswordServiceTest`、`UserDAOPasswordTest`、`UserPasswordMigrationTest` 固定密码哈希升级路径。

### 6. 测试与验证结果

验证命令：

```bash
mvn test
```

验证结果：通过。当前全量 reactor `BUILD SUCCESS`；`basic` 7 个测试、`utils` 7 个测试、`cache` 15 个测试、`core` 42 个测试、`web` 7 个测试全部通过。

## Issue 10: P3 长期优化方向

### 1. Issue 是什么

Agent1 最后提出：P3 是长期架构优化，覆盖 DAO 架构、事件与钩子、前端后台和可观测性。它不是单点 bug，而是后续演进路线。如果一次性重写 DAO、事件总线、前端和监控，容易引入功能行为变化，不符合本轮“修复问题且不改变原有功能”的目标。

### 2. 计划怎么解决

本轮将 P3 做成可执行路线图，并把已经由 P0-P2 落地的部分标记清楚：

1. DAO 安全第一阶段已落地，后续拆 `AdminSqlBuilder` 和数据库方言。
2. 事件发布点已有代码注释，后续抽象 Spring 事件或轻量事件总线。
3. 前端后台先记录删除确认、批量操作、权限裁剪和密码页面要求。
4. 可观测性先记录审计日志、结构化日志和指标输出设计。

### 3. 源代码原来是什么样子

P3 原文是方向性清单，没有对应的单个源代码片段：

```text
DAO 架构：迁移到安全 SQL Builder、拆分 AdminDAO、建立数据库方言适配层。
事件与钩子：注册、登录、CRUD 事件抽象成事件总线。
前端后台：删除确认、批量操作、表单校验、按权限裁剪字段。
可观测性：审计日志、结构化日志、指标输出。
```

### 4. 修改后代码是什么样子

新增路线图文档：

```text
docs/long-term-optimization-roadmap.md
```

该文档按四个方向记录当前状态、后续任务和验收标准。

### 5. 为什么这样修改

P3 属于架构演进，风险和改动面明显大于 P0-P2。当前版本已经完成安全、正确性、配置和测试整改，继续在同一轮中重构 DAO、事件、前端和指标会增加回归风险。因此先形成路线图，后续可以按独立任务逐步实现，每个任务都有明确验收标准和测试边界。

### 6. 测试与验证结果

P3 本轮只新增文档，不改变生产代码。验证方式沿用 Issue 9 的全量测试结果：

```bash
mvn test
```

验证结果：通过。全量 reactor `BUILD SUCCESS`。
