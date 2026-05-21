# JInfra Core 核心抽象与通用工具设计

## 背景

`jinfra-core` 是 JInfra 所有模块的基础依赖，需要提供稳定、轻量、可复用的核心抽象。当前模块只有 POM，没有 Java 源码和单元测试。本次设计聚焦异常、断言、结果模型、字符串/集合/对象工具与统一错误语义。

## 目标

- 为下游模块提供统一错误码抽象、基础异常层级和断言入口。
- 提供不可变结果模型 `Result<T>`，支持跨模块表达成功或失败返回。
- 提供最小但完整的字符串、集合、对象和异常工具。
- 所有公开 API 补齐 Javadoc。
- 补齐 JUnit Jupiter 单元测试，覆盖正常路径、边界条件和异常路径。
- 保持 `jinfra-core` 轻量，不引入 Spring、Lombok、Jackson 或 Web 语义。

## 非目标

- 不实现 HTTP status、traceId、timestamp 或 Web 错误响应。
- 不预置 `NotFound`、`Unauthorized`、`BusinessException` 等业务/Web 异常。
- 不实现分页、排序、响应体协议、序列化模型等框架级能力。
- 不扩展大而全工具包，只提供本次验收所需的基础方法。

## 设计决策

### 错误码模型

错误码采用“接口 + 核心枚举 + 字符串外露”的方式。

- `ErrorCode` 是下游模块的扩展点。
- `CoreErrorCode` 是 core 自带的基础错误码枚举。
- 异常和结果模型对外只暴露 `String code`，避免下游被 core 枚举类型锁死。

核心错误码范围：

- `SUCCESS`
- `INVALID_ARGUMENT`
- `NULL_ARGUMENT`
- `ILLEGAL_STATE`
- `INTERNAL_ERROR`

错误码名称使用大写下划线。默认消息为英文开发者消息，业务层可自行包装用户文案。

### 异常层级

异常层级保持基础、无业务语义。

- `JInfraException extends RuntimeException`：JInfra 统一根异常，持有 `String code`。
- `JInfraRuntimeException extends JInfraException`：运行期异常分层，为后续模块扩展预留语义空间。
- `CoreException extends JInfraRuntimeException`：`jinfra-core` 内部断言和基础工具使用的具体异常。

断言失败统一抛 `CoreException`，不直接抛裸 `IllegalArgumentException` 或 `IllegalStateException`，便于下游统一读取错误码。

### 结果模型

`Result<T>` 使用 Java 17 `record` 实现不可变模型，字段固定为：

- `boolean success`
- `String code`
- `String message`
- `T data`

约束：

- 成功结果默认使用 `CoreErrorCode.SUCCESS`。
- 失败结果不允许使用 `SUCCESS` 错误码。
- `Result<T>` 不捕获异常，不提供 `try/catch` 包装能力。
- `traceId`、`timestamp`、HTTP status 不进入 core。

### 包结构

包根：`cn.refinex.jinfra.core`

```text
cn.refinex.jinfra.core
├── constant
│   └── CoreConstants
├── error
│   ├── ErrorCode
│   └── CoreErrorCode
├── exception
│   ├── JInfraException
│   ├── JInfraRuntimeException
│   └── CoreException
├── lang
│   └── Result
└── util
    ├── AssertUtils
    ├── StringUtils
    ├── CollectionUtils
    ├── ObjectUtils
    └── ExceptionUtils
```

## API 摘要

### `ErrorCode`

- `String code()`
- `String message()`

### `Result<T>`

- `success()`
- `success(T data)`
- `failure(String code, String message)`
- `failure(ErrorCode errorCode)`
- `failure(ErrorCode errorCode, String message)`

### `AssertUtils`

- `notNull(Object value, ErrorCode errorCode)`
- `notNull(Object value, String message)`
- `hasText(String value, ErrorCode errorCode)`
- `hasText(String value, String message)`
- `isTrue(boolean expression, ErrorCode errorCode)`
- `isTrue(boolean expression, String message)`
- `state(boolean expression, ErrorCode errorCode)`
- `state(boolean expression, String message)`
- `notEmpty(Collection<?> collection, ErrorCode errorCode)`
- `notEmpty(Collection<?> collection, String message)`

### `StringUtils`

- `isBlank(String value)`
- `isNotBlank(String value)`
- `trimToEmpty(String value)`
- `defaultIfBlank(String value, String defaultValue)`
- `equals(String left, String right)`

### `CollectionUtils`

- `isEmpty(Collection<?> collection)`
- `isNotEmpty(Collection<?> collection)`
- `nullToEmptyList(List<T> list)`
- `nullToEmptySet(Set<T> set)`
- `nullToEmptyMap(Map<K, V> map)`
- `emptyToNull(Collection<T> collection)`
- `first(Collection<T> collection)`

返回空集合时使用 JDK 不可变空集合。`first` 返回 `Optional<T>`。

### `ObjectUtils`

- `defaultIfNull(T value, T defaultValue)`
- `equals(Object left, Object right)`
- `hashCode(Object value)`
- `requireNonNull(T value, ErrorCode errorCode)`
- `requireNonNull(T value, String message)`

### `ExceptionUtils`

- `rootCause(Throwable throwable)`
- `rootCauseMessage(Throwable throwable)`
- `stackTraceToString(Throwable throwable)`
- `isCausedBy(Throwable throwable, Class<? extends Throwable> causeType)`

## 依赖边界

允许依赖：

- JDK 17
- `commons-lang3`
- `slf4j-api` 维持现有 POM，不在本次 API 中强制使用日志
- JUnit Jupiter、AssertJ 作为测试依赖

禁止引入：

- Spring / Spring Boot
- Lombok
- Jackson
- Web/HTTP 相关依赖

## 测试设计

测试包路径与源码包路径保持一致。每个公开类型对应一个测试类。

覆盖范围：

- 正常路径：成功结果、失败结果、默认值、非空字符串、非空集合、对象相等。
- 边界条件：`null`、空字符串、blank 字符串、空集合、没有 cause 的异常。
- 异常路径：断言失败、`Result` 使用成功错误码构造失败结果、错误码为空或消息为空。

验证命令：

```bash
mvn -pl jinfra-core -am test
mvn -pl jinfra-core -am verify
```

额外结构检查：

```bash
rg -n "springframework|spring-boot" jinfra-core/pom.xml jinfra-core/src/main/java jinfra-core/src/test/java
```

该命令应无匹配。

## 兼容性风险

- `Result<T>` 使用 Java record，公开构造器形态一旦发布不宜频繁调整。
- `CoreErrorCode` 名称和 code 值会被下游依赖，后续应只新增不随意重命名。
- `AssertUtils` 统一抛 `CoreException`，下游如果期待 JDK 原生异常，需要适配。
- 工具类方法容易膨胀，本次只实现最小集合，后续新增方法需证明跨模块复用价值。

## 验收标准

- `jinfra-core` 不依赖 Spring。
- 所有公开类、接口、枚举、record 与公开方法都有 Javadoc。
- 单元测试覆盖正常路径、边界条件和异常路径。
- `mvn -pl jinfra-core -am test` 通过。
- `mvn -pl jinfra-core -am verify` 通过。
- 下游模块可以直接复用 `ErrorCode`、`CoreErrorCode`、`CoreException` 和 `Result<T>`。
