# JInfra Context / ID / IO / Codec 首版能力设计

## 背景

`jinfra-context`、`jinfra-id`、`jinfra-io`、`jinfra-codec` 是 JInfra 核心能力底座中的第一组基础模块。当前四个模块已有 Maven POM，但没有 Java 源码。本次设计目标是在保持纯 Java、低侵入、可测试的前提下，为上下文传播、ID 生成、文件/流处理与编解码提供首版可用 API。

## 目标

- 明确四个模块的边界和依赖方向，避免基础模块互相耦合。
- `jinfra-context` 提供纯 Java 当前线程上下文、快照传播和可选 MDC bridge。
- `jinfra-id` 提供 UUID、ULID 默认能力，并为 Snowflake 提供显式配置与扩展点。
- `jinfra-io` 提供文件、流、临时文件、路径和 zip 的常用安全封装。
- `jinfra-codec` 提供 Base64、Hex、URL 编解码工具。
- 所有公开 API 提供中文 Javadoc。
- 补齐 JUnit Jupiter + AssertJ 单元测试，覆盖正常路径、边界条件和异常路径。

## 非目标

- 不引入 Spring、Spring Boot、Reactor、CompletableFuture 执行器框架绑定或 Web 语义。
- `jinfra-context` 不负责生成 TraceId，也不强制 MDC 必须存在。
- `jinfra-id` 不内置 Snowflake 默认 epoch、位宽、workerId 或节点分配规则。
- `jinfra-io` 不做 JSON、对象序列化、Office 文件解析或网络 IO。
- `jinfra-codec` 不做摘要、HMAC、加密、签名或密码哈希，这些能力留给 `jinfra-crypto`。

## 总体架构

四个模块均只依赖 `jinfra-core` 和各自 POM 已声明的第三方库，不互相依赖。

```text
jinfra-core
  ├── jinfra-context
  ├── jinfra-id
  ├── jinfra-io
  └── jinfra-codec
```

依赖边界：

- `jinfra-context`：只处理上下文值、当前线程存储、快照传播和 MDC 同步，不依赖 `jinfra-id`。
- `jinfra-id`：只处理 ID 生成抽象和默认生成器，不依赖 `jinfra-codec`。
- `jinfra-io`：只处理 JDK NIO、流和 zip，不依赖 `jinfra-codec` 或 JSON。
- `jinfra-codec`：只处理编码转换，不依赖 `jinfra-io` 或 `jinfra-crypto`。

## `jinfra-context` 设计

### 包结构

```text
cn.refinex.jinfra.context
├── ContextException
├── ContextKey
├── ContextSnapshot
├── JInfraContext
├── JInfraContextHolder
├── ContextScope
├── ContextPropagation
└── mdc
    └── MdcContextBridge
```

### 核心 API

- `ContextKey<T>`：类型化 key。首版内置 `TRACE_ID`、`TENANT_ID`、`USER_ID`，值类型统一为 `String`。
- `JInfraContext`：不可变上下文，内部保存 `Map<String, String>`。
  - `empty()`
  - `of(String key, String value)`
  - `get(ContextKey<String> key)`
  - `put(ContextKey<String> key, String value)`
  - `remove(ContextKey<String> key)`
  - `asMap()`
- `ContextSnapshot`：不可变快照，支持跨线程传递。
  - `from(JInfraContext context)`
  - `context()`
- `JInfraContextHolder`：ThreadLocal 当前上下文入口。
  - `current()`
  - `set(JInfraContext context)`
  - `clear()`
  - `capture()`
  - `open(ContextSnapshot snapshot)`
- `ContextScope implements AutoCloseable`：恢复打开 scope 前的旧上下文。
- `ContextPropagation`：显式包装任务。
  - `wrap(Runnable runnable)`
  - `wrap(Callable<T> callable)`
- `MdcContextBridge`：可选同步 MDC。
  - `put(ContextSnapshot snapshot)`
  - `clearKnownKeys()`
  - `withMdc(ContextSnapshot snapshot)`

### 线程安全

- `JInfraContext` 和 `ContextSnapshot` 不可变，线程安全。
- `JInfraContextHolder` 基于 `ThreadLocal`，只保证当前线程隔离。
- 跨线程传播必须显式 `capture()` 或 `wrap(...)`，不自动绑定执行器。
- `ContextScope` 只能在打开它的线程关闭，测试覆盖恢复语义。

### 异常语义

- null key、blank key、null context、null snapshot 抛 `CoreException`。
- MDC adapter 抛出的异常包装为 `ContextException`。
- `ContextScope.close()` 多次调用保持幂等。

### 测试范围

- 当前线程 `set/current/clear`。
- `ContextScope` try-with-resources 后恢复旧上下文。
- `ContextSnapshot` 跨线程可传播。
- `ContextPropagation.wrap(Runnable/Callable)` 带入捕获时上下文。
- `MdcContextBridge` 写入、清理和 scope 恢复。
- null/blank key 与 null 参数边界。

## `jinfra-id` 设计

### 包结构

```text
cn.refinex.jinfra.id
├── IdGenerationException
├── IdGenerator
├── IdGenerators
├── IdType
├── uuid
│   └── UuidGenerator
├── ulid
│   ├── Ulid
│   └── UlidGenerator
└── snowflake
    ├── ClockBackwardsStrategy
    ├── SnowflakeConfig
    └── SnowflakeIdGenerator
```

### 核心 API

- `IdGenerator<T>`：统一生成器接口。
  - `T nextId()`
- `IdType`：`UUID`、`UUID_WITH_HYPHEN`、`ULID`、`SNOWFLAKE`。
- `IdGenerators`：门面工厂。
  - `uuid()`
  - `uuidWithHyphen()`
  - `ulid()`
  - `ulid(Clock clock, SecureRandom random)`
  - `snowflake(SnowflakeConfig config, Clock clock)`
- `UuidGenerator`：
  - 默认生成无横线 UUID。
  - 可配置是否保留横线。
- `Ulid`：
  - 26 位字符串。
  - 遵循 48-bit millisecond timestamp + 80-bit randomness + Crockford Base32。
- `UlidGenerator`：
  - 支持传入 `Clock` 与 `SecureRandom`。
  - 同一毫秒内使用单调随机部分，保证单生成器内有序。
- `SnowflakeConfig`：
  - `epochMillis`
  - `timestampBits`
  - `workerIdBits`
  - `sequenceBits`
  - `workerId`
  - `ClockBackwardsStrategy`
- `ClockBackwardsStrategy`：
  - 首版只实现 `FAIL_FAST`。
- `SnowflakeIdGenerator`：
  - 必须显式传入完整 `SnowflakeConfig` 和 `Clock`。
  - 不提供默认位宽、默认 epoch、默认 workerId。

### 线程安全

- UUID 生成器无共享可变状态，线程安全。
- ULID 生成器同步保护上一毫秒与随机部分，线程安全。
- Snowflake 生成器同步保护上一时间戳与序列，线程安全。

### 异常语义

- null 参数、非法位宽、非法 workerId 抛 `CoreException` 或 `IdGenerationException`。
- ULID 同一毫秒随机部分溢出抛 `IdGenerationException`。
- Snowflake 时钟回拨在 `FAIL_FAST` 下抛 `IdGenerationException`。

### 测试范围

- UUID 标准格式、无横线格式和基本唯一性。
- ULID 长度、字符集、固定 Clock、同毫秒单调递增。
- Snowflake 配置参数校验。
- Snowflake 显式配置后可生成正 long。
- Snowflake 时钟回拨 `FAIL_FAST` 抛异常。

## `jinfra-io` 设计

### 包结构

```text
cn.refinex.jinfra.io
├── IoException
├── PathUtils
├── FileUtils
├── StreamUtils
├── TempFileUtils
└── ZipUtils
```

### 核心 API

- `PathUtils`
  - `normalize(Path path)`
  - `ensureInside(Path baseDir, Path target)`
  - `extension(Path path)`
  - `fileName(Path path)`
- `FileUtils`
  - `readString(Path path, Charset charset)`
  - `writeString(Path path, String content, Charset charset)`
  - `readBytes(Path path)`
  - `writeBytes(Path path, byte[] bytes)`
  - `createDirectories(Path path)`
  - `deleteIfExists(Path path)`
- `StreamUtils`
  - `copy(InputStream input, OutputStream output)`
  - `toByteArray(InputStream input)`
  - `toString(InputStream input, Charset charset)`
- `TempFileUtils`
  - `createTempFile(String prefix, String suffix)`
  - `createTempDirectory(String prefix)`
- `ZipUtils`
  - `zipDirectory(Path sourceDir, Path targetZip)`
  - `unzip(Path zipFile, Path targetDir)`

### 安全边界

- `ZipUtils.unzip(...)` 必须通过 `PathUtils.ensureInside(...)` 防止 Zip Slip。
- `PathUtils.ensureInside(...)` 统一使用 normalized absolute path 判断目标是否在 base 下。
- 所有 IO 方法不吞异常；JDK `IOException` 统一包装。

### 异常语义

- JDK `IOException` 包装为 `IoException extends CoreException`。
- null 参数通过 core 断言抛 `CoreException`。
- Zip Slip 或非法目标路径抛 `IoException`。

### 测试范围

- 文件字符串和字节读写。
- 目录创建、删除不存在文件。
- 流复制、流转 byte[]、流转字符串。
- 临时文件和临时目录创建。
- 路径 normalize、extension、fileName、ensureInside。
- zip/unzip 正常路径。
- Zip Slip 防护。

## `jinfra-codec` 设计

### 包结构

```text
cn.refinex.jinfra.codec
├── CodecException
├── Base64Utils
├── HexUtils
└── UrlCodecUtils
```

### 核心 API

- `Base64Utils`
  - `encode(byte[] bytes)`
  - `decode(String text)`
  - `encodeUrlSafe(byte[] bytes)`
  - `decodeUrlSafe(String text)`
- `HexUtils`
  - `encode(byte[] bytes)`
  - `decode(String hex)`
- `UrlCodecUtils`
  - `encode(String text, Charset charset)`
  - `decode(String text, Charset charset)`

### 异常语义

- 非法 Base64、Hex、URL 输入包装为 `CodecException extends CoreException`。
- null 参数通过 core 断言抛 `CoreException`。
- 空 byte 数组、空字符串按 JDK/commons-codec 的安全语义返回空结果。

### 测试范围

- Base64 标准编码/解码。
- URL-safe Base64 编码/解码。
- Base64 空数组与非法输入。
- Hex 大小写、空数组、奇数字符、非法字符。
- URL 中文、空字符串、非法百分号编码。

## ServiceLoader 与扩展策略

首版代码以直接工厂方法为主，不强制 ServiceLoader 参与运行路径。`IdGenerator<T>` 保留为扩展接口，后续需要第三方生成器发现时，可在 `IdGenerators` 中增加基于 `ServiceLoader` 的查找方法。

设计依据：

- JDK `ServiceLoader` 适合服务提供者发现，但它会引入 classpath/modulepath 约定和加载顺序语义。
- 当前首版目标是最小可用能力，不需要为了扩展性提前引入隐式加载路径。

## 依赖边界

允许依赖：

- Java 17 标准库。
- `jinfra-core`。
- `jinfra-context` 现有 `slf4j-api`。
- `jinfra-io` 现有 `commons-io`。
- `jinfra-codec` 现有 `commons-codec`。
- JUnit Jupiter、AssertJ 测试依赖。

禁止引入：

- Spring / Spring Boot。
- Reactor、RxJava、Netty 等异步框架。
- Jackson、JSON、HTTP、Redis、数据库相关依赖。
- Snowflake workerId 或 datacenterId 自动发现依赖。

## 验收标准

- 四个模块均可在纯 Java 环境使用。
- 四个模块都依赖 `jinfra-core`，但彼此不互相依赖。
- `jinfra-context` 支持当前线程上下文、快照传播和可选 MDC bridge。
- `jinfra-id` 支持 UUID、ULID；Snowflake 需要显式配置，且配置可测试。
- `jinfra-io` 覆盖文件、流、临时文件、zip 和路径安全能力。
- `jinfra-codec` 覆盖 Base64、Hex、URL 编解码。
- 所有公开 API 有中文 Javadoc。
- 单元测试覆盖正常路径、边界条件和异常路径。
- 以下命令通过：

```bash
mvn -pl jinfra-context,jinfra-id,jinfra-io,jinfra-codec -am test
mvn -pl jinfra-context,jinfra-id,jinfra-io,jinfra-codec -am verify
rg -n "springframework|spring-boot" jinfra-context jinfra-id jinfra-io jinfra-codec
```

## 兼容性风险

- `ContextKey` 与内置 key 名称发布后不宜随意修改。
- `JInfraContext` 只存 `String` 值，后续若支持泛型对象上下文，需要新增 API 而不是改变现有行为。
- ULID 单调生成规则会影响排序语义，后续应保持稳定。
- Snowflake 配置模型一旦发布，字段含义必须稳定；位宽和 workerId 规则仍由使用方显式指定。
- `ZipUtils.unzip(...)` 的 Zip Slip 防护可能拒绝部分历史 zip 包中的危险路径，这是安全优先的预期行为。
