# Pear 代码审查与整改路线

本文档记录当前代码审查发现的问题和建议整改顺序。目标是先处理安全与数据正确性风险，再改善模块架构、可维护性和测试覆盖。

## 整体优先级

| 优先级 | 阶段 | 目标 |
| --- | --- | --- |
| P0 | 安全底线 | 修复 SQL 注入、认证上下文错位、敏感信息泄露 |
| P1 | 稳定性 | 修复缓存并发、注解元数据、输入校验等运行时问题 |
| P2 | 架构质量 | 改善密码体系、配置体系、测试体系和发布质量 |
| P3 | 长期演进 | 优化 DAO 架构、事件机制、前端体验和可观测性 |

## P0-1 CRUD 动态 SQL 注入风险

### 涉及文件

- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/dao/AdminDAO.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/admin/impl/AdminServiceImpl.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/params/Filter.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/params/QueryForm.java`

### 问题描述

CRUD 当前使用字符串拼接生成 SQL。表名、字段名、过滤值、关键字、排序字段、主键 query 参数都可能进入 SQL 字符串。虽然 `model` 会通过 `AdminContainer.existsAdminObject(model)` 校验，但 `filter.name`、`filter.value`、`keyword`、`orders.name` 等请求体字段没有严格白名单校验。

### 风险影响

- SQL 注入。
- 越权查询未暴露字段。
- 绕过过滤条件或破坏数据。
- 动态拼接语句难以维护和测试。

### 整改方案

1. 建立字段白名单：
   - 查询字段只能来自 `adminObject.getShows()`。
   - 过滤字段只能来自 `adminObject.getFilterables()`。
   - 排序字段只能来自 `adminObject.getOrderables()`。
   - 搜索字段只能来自 `adminObject.getSearches()`。
   - 主键和唯一键只能来自 `adminObject.getPrimaryKeys()`、`adminObject.getUniqueKeys()`。
2. 表名只允许使用 `adminObject.getTableName()`，禁止从请求参数直接进入 DAO。
3. SQL 值全部使用 MyBatis 参数绑定，不再拼接单引号。
4. 对 `LIKE`、`BETWEEN`、`IN`、排序、分页分别建立安全构造逻辑。
5. 为非法字段、非法操作符、非法分页值返回 400。

### 验收标准

- 使用包含 `' OR '1'='1` 的 keyword/filter/value 不会改变 SQL 语义。
- 请求未声明可过滤字段作为 filter 时返回 400。
- 请求未声明可排序字段作为 order 时返回 400。
- 所有 CRUD 集成测试通过。

## P0-2 JWT 校验与用户上下文脱节

### 涉及文件

- `pear-spring-boot-web/src/main/java/cn/muzisheng/pear/interceptor/JWTInterceptor.java`
- `pear-spring-boot-utils/src/main/java/cn/muzisheng/pear/JwtUtil.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/aspect/HookAspect.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/service/Impl/UserServiceImpl.java`

### 问题描述

`JWTInterceptor` 只验证 token 是否可解析，没有把用户写入 request 或统一安全上下文。`HookAspect` 通过 `UserService.currentUser()` 从 session 中取用户，两者不是同一个认证来源。纯 token 请求可能通过拦截器后仍被切面判定未登录。

`JwtUtil` 还存在固定默认密钥、固定 token 前缀、`refreshToken` 返回错误值等问题。

### 风险影响

- token 认证语义不一致。
- API 客户端无法稳定使用纯 token。
- 固定 JWT 密钥导致所有应用默认同密钥。
- 自定义 token head 配置可能失效。

### 整改方案

1. 明确认证模型：
   - 推荐使用无状态 JWT 作为 `/admin/**` 认证来源。
   - session 只用于页面登录态，或也统一由 token 派生。
2. `JWTInterceptor` 解析 token 后查用户，并写入 request scope。
3. `UserService.currentUser()` 优先从 request scope 获取已认证用户，其次再 fallback 到 session。
4. `JwtUtil` 使用 `TokenConfig` 中的密钥配置，不再使用硬编码默认盐。
5. `delTokenPrefix` 使用配置的 head，并严格校验前缀。
6. 修复 `refreshToken`，应根据 email 生成新 token。

### 验收标准

- 携带合法 Authorization token 访问 `/admin/json` 可通过权限切面。
- 不携带 token 访问 `/admin/**` 返回 401。
- 修改 token head 配置后，生成和解析逻辑一致。
- 使用错误签名、过期 token、错误前缀均返回明确 401。

## P0-3 User 实体直接出接口导致敏感信息泄露

### 涉及文件

- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/entity/User.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/service/Impl/UserServiceImpl.java`
- `pear-spring-boot-web/src/main/java/cn/muzisheng/pear/api/UserApi.java`

### 问题描述

登录和用户信息接口直接返回 `User` 实体。`User` 包含 `password`、`token`、内部状态等字段，可能把密码哈希和内部字段暴露给前端或第三方调用者。

### 风险影响

- 密码哈希泄露。
- 内部字段泄露。
- 后续实体字段变更可能无意扩大 API 响应面。

### 整改方案

1. 新增 `UserDTO` 或 `CurrentUserResponse`。
2. 登录接口只返回必要用户资料和 token。
3. `/user/info` 不返回 password、内部 token 字段。
4. 在实体敏感字段上增加 `@JsonIgnore` 作为第二道保护。
5. 后台 `AdminObject` 对用户表默认隐藏 password/token 字段。

### 验收标准

- 登录响应不包含 `password`。
- `/user/info` 响应不包含 `password`。
- 序列化 `User` 时默认不输出敏感字段。

## P1-1 缓存模块并发与 LFU 逻辑问题

### 涉及文件

- `pear-spring-boot-cache/src/main/java/cn/muzisheng/pear/cache/LRUCacheTemplate.java`
- `pear-spring-boot-cache/src/main/java/cn/muzisheng/pear/cache/LFUCacheTemplate.java`
- `pear-spring-boot-cache/src/main/java/cn/muzisheng/pear/CacheContainer.java`

### 问题描述

LRU 使用 access-order `LinkedHashMap`，但 `get` 在读锁下会改变访问顺序。过期删除时在读锁中调用写锁删除，存在锁升级死锁风险。

LFU 的频率更新存在结构错位问题，`frequencyMap` 与 `frequencyKeysMap` 使用的频率值不一致，删除时可能出现空指针或淘汰错误。

### 风险影响

- 高并发下死锁。
- 缓存数据结构损坏。
- LFU 淘汰不符合预期。
- 运行时偶发 NPE。

### 整改方案

1. LRU 的 `get` 改用写锁，或拆分读检查与写更新，但不能在读锁下修改 access-order map。
2. 过期删除必须释放读锁后获取写锁。
3. LFU 重新定义频率更新：
   - 旧频率集合删除 key。
   - 新频率集合加入 key。
   - `frequencyMap` 与 `frequencyKeysMap` 存储一致频率。
4. `capacity` 边界修复，新增元素前应在 `size >= capacity` 时淘汰。
5. 中长期考虑接入 Caffeine 替代自研本地缓存。

### 验收标准

- 多线程并发 `put/get/remove` 压测不死锁。
- LRU 淘汰最近最久未使用元素。
- LFU 淘汰访问频率最低元素，同频时按插入或访问顺序稳定淘汰。
- 过期 key 获取后被清理。

## P1-2 注解元数据构建缺陷

### 涉及文件

- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/initialize/PearBeanInitialization.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/initialize/AdminContainer.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/annotation/PearObject.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/annotation/PearField.java`

### 问题描述

`PearBeanInitialization` 只在 `path`、`pluralName` 为空时设置默认值，非空时没有写入注解值。`@Entity` 默认 `name=""` 时会被当作表名，可能产生空表名。最终 `AdminContainer.parseFields` 只扫描当前类字段，不包含继承字段。

### 风险影响

- 注解配置不生效。
- 空表名导致启动或 CRUD 异常。
- 继承实体字段无法正常出现在后台模型。
- 元数据与数据库结构不一致。

### 整改方案

1. 完整写入 `@PearObject` 的 path、pluralName、editPage、listPage、iconUrl、isInvisible。
2. 表名解析 fallback 顺序：
   - JPA `@Table(name=...)`
   - JPA `@Entity(name=...)` 且非空
   - MyBatis-Plus `@TableName`
   - `@PearObject.TableName`
   - 类名 snake_case
3. `parseFields` 支持继承字段，与 Bean 初始化阶段一致。
4. 对重复表名、空表名、无主键/唯一键启动时 fail-fast。
5. 明确 `@Transient`、`@TableField(exist=false)` 的处理。

### 验收标准

- 注解 path/pluralName 非空时在 `AdminObject` 中生效。
- `@Entity` 未指定 name 时不会产生空表名。
- 继承自父类的 `@PearField` 能进入最终 fields。
- 重复表名启动失败并给出明确错误。

## P1-3 输入校验和异常处理不足

### 涉及文件

- `pear-spring-boot-web/src/main/java/cn/muzisheng/pear/api/AdminApi.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/admin/impl/AdminServiceImpl.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/params/QueryForm.java`
- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/params/Filter.java`

### 问题描述

`QueryForm` 允许为空，但服务层直接调用方法。更新接口允许空 body，但服务层直接调用 `data.isEmpty()`。过滤、排序、分页、日期类型转换缺少统一校验。

### 风险影响

- 400 场景变成 500。
- API 行为不稳定。
- 前端难以处理错误。

### 整改方案

1. Controller 层使用 `@Valid` 和参数对象校验。
2. `QueryForm` 为空时创建默认实例。
3. 更新、创建请求 body 为空时返回 400。
4. 统一异常响应结构，区分 400、401、403、500。
5. 日期、数字、布尔转换失败时返回字段级错误。

### 验收标准

- 空 body 更新返回 400。
- 空查询体查询列表不抛 NPE。
- 非法日期、数字格式返回 400。
- 异常响应结构稳定。

## P2-1 密码哈希强度不足

### 涉及文件

- `pear-spring-boot-core/src/main/java/cn/muzisheng/pear/dao/UserDAO.java`
- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/config/UserConfig.java`

### 问题描述

当前密码哈希是 `SHA-256(salt + password)`，不适合作为用户密码存储方案。

### 风险影响

- 密码哈希被泄露后更容易被离线爆破。
- 无法灵活升级算法成本。

### 整改方案

1. 使用 BCrypt、Argon2 或 PBKDF2。
2. 保存哈希算法版本。
3. 登录成功时支持旧哈希自动迁移到新算法。
4. 空密码应直接拒绝，不应返回空哈希。

### 验收标准

- 新注册用户密码使用强哈希。
- 老 SHA-256 用户仍可登录，并在登录后迁移。
- 空密码注册或设置被拒绝。

## P2-2 配置体系问题

### 涉及文件

- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/properties/CacheProperties.java`
- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/config/auto/SqlAutoConfiguration.java`
- `pear-spring-boot-basic/src/main/java/cn/muzisheng/pear/config/auto/*`

### 问题描述

`CacheProperties` 中时间单位缩写映射存在错误，例如 `ms` 被映射为 `SECONDS`。`SqlAutoConfiguration` 要求 `spring.datasource.url=mysql` 精确匹配，真实 JDBC URL 一般是 `jdbc:mysql://...`，自动 SQL 初始化基本不会触发。

### 风险影响

- 缓存过期时间配置含义错误。
- MySQL schema 初始化失效。
- 用户配置后行为与预期不一致。

### 整改方案

1. 修正时间单位缩写映射。
2. 支持标准 `TimeUnit` 名称和常见缩写。
3. SQL 初始化改为条件匹配 JDBC URL 前缀，或提供显式开关 `pear.starter.sql.init.enabled`。
4. 配置非法时 fail-fast，不要静默忽略。

### 验收标准

- `pear.starter.cache.time-unit=ms` 实际为毫秒。
- `jdbc:mysql://...` 能触发或可配置触发 schema 初始化。
- 非法 time-unit 返回明确启动错误。

## P2-3 测试覆盖不足

### 涉及范围

- CRUD 动态 SQL 与参数校验
- 权限与认证
- 注解扫描
- 缓存并发
- 配置绑定
- 用户登录注册

### 整改方案

1. 单元测试：
   - `JwtUtil`
   - `CacheProperties`
   - `PearBeanInitialization`
   - `AdminContainer`
   - `LRUCacheTemplate`、`LFUCacheTemplate`
2. 集成测试：
   - 使用 H2 或 Testcontainers MySQL 验证 CRUD。
   - 验证 `/admin/**` 的 401、403、200 场景。
3. 安全测试：
   - SQL 注入 payload。
   - 敏感字段响应检查。
4. 并发测试：
   - 缓存多线程读写。

### 验收标准

- Maven `test` 阶段有实际测试运行。
- P0 修复都有回归测试。
- 并发缓存测试稳定通过。

## P3 长期优化方向

### DAO 架构

- 从字符串 SQL 拼接迁移到安全 SQL Builder 或参数化 Mapper。
- 拆分 `AdminDAO` 的 create/query/update/delete 构造逻辑。
- 对不同数据库方言建立适配层。

### 事件与钩子

- 将注册、登录、CRUD 事件抽象成事件总线。
- 支持业务侧订阅事件，例如审计日志、消息通知、异步任务。
- 钩子异常要区分业务拒绝和系统错误。

### 前端后台

- 强化删除确认、批量操作、表单校验。
- 密码修改流程避免明文回显。
- 后台元数据接口支持按权限裁剪字段和操作。

### 可观测性

- 增加审计日志。
- 为认证失败、权限拒绝、CRUD 变更提供结构化日志。
- 提供可选指标输出，例如缓存命中率、CRUD 延迟、权限拒绝次数。

## 建议执行顺序

1. P0-1 CRUD 动态 SQL 注入风险。
2. P0-2 JWT 校验与用户上下文脱节。
3. P0-3 User 敏感信息泄露。
4. P1-3 输入校验和异常处理不足。
5. P1-2 注解元数据构建缺陷。
6. P1-1 缓存模块并发与 LFU 逻辑问题。
7. P2-1 密码哈希强度不足。
8. P2-2 配置体系问题。
9. P2-3 测试覆盖不足。
10. P3 长期优化方向。

## 每项整改的通用完成标准

- 有明确代码修复。
- 有对应单元测试或集成测试。
- `mvn test` 通过。
- 对外行为变化更新 README 或新增文档。
- 不引入与当前模块职责无关的重构。
