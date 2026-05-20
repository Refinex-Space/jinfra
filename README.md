# JInfra

**JInfra: A modular Java infrastructure toolkit for enterprise application development.**

JInfra 是一个面向 Java 企业级应用开发的模块化基础设施工具包，目标是把日常项目中高频、可复用、可标准化的基础能力沉淀为稳定、低侵入、可按需引入的 Maven 模块。

它不是一个大而全的业务框架，而是一组围绕 Java 基础设施层的通用能力集合：Office 文档操作、Redis 分布式能力、Redis 消息队列、分布式锁、缓存、JSON、HTTP、加密、ID、上下文、校验、Spring Boot 自动装配以及常用工具类。

---

## 项目定位

JInfra 的核心定位是：

> 面向 Java 企业级应用的模块化基础设施工具包。

设计目标：

- **模块化**：每种能力都可以单独引入，也可以通过 `jinfra-all` 全量引入。
- **低侵入**：核心模块不依赖 Spring，不强制绑定 Web、数据库或特定框架。
- **工程化**：提供 BOM、Starter、自动装配、统一版本治理和 Maven Central 发布能力。
- **可替换**：Redis、Lock、MQ、Cache 等能力优先抽象 SPI，再提供默认实现。
- **生产可用**：优先封装企业项目中真正高频、容易重复造轮子的基础能力。

---

## 快速开始

### 方式一：使用 BOM 管理版本

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.refinexcn</groupId>
            <artifactId>jinfra-bom</artifactId>
            <version>0.1.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

然后按需引入模块：

```xml
<dependency>
    <groupId>io.github.refinexcn</groupId>
    <artifactId>jinfra-excel</artifactId>
</dependency>

<dependency>
    <groupId>io.github.refinexcn</groupId>
    <artifactId>jinfra-lock-redis</artifactId>
</dependency>
```

### 方式二：全量引入非 Spring 基础能力

```xml
<dependency>
    <groupId>io.github.refinexcn</groupId>
    <artifactId>jinfra-all</artifactId>
    <version>0.1.0</version>
</dependency>
```

> 说明：`jinfra-all` 建议只聚合非 Spring Boot Starter 类能力，避免普通 Java 项目被动引入 Spring Boot 自动装配体系。Spring Boot 使用者建议单独引入对应 Starter。

### 方式三：Spring Boot 项目引入 Starter

```xml
<dependency>
    <groupId>io.github.refinexcn</groupId>
    <artifactId>jinfra-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

Redis 能力：

```xml
<dependency>
    <groupId>io.github.refinexcn</groupId>
    <artifactId>jinfra-redis-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

Office 能力：

```xml
<dependency>
    <groupId>io.github.refinexcn</groupId>
    <artifactId>jinfra-office-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

---

## 推荐技术栈

| 类别 | 推荐版本 | 说明 |
|---|---:|---|
| JDK | 17+ | JInfra 的最低兼容版本 |
| Maven | 3.9.0+ | 推荐使用 Maven 3.9.x 构建与发布 |
| Spring Boot | 3.5.14 | Spring Boot 3 生态建议版本 |
| Apache POI | 5.5.1 | Office OOXML 文档处理基础库 |
| Jackson | 2.21.3 | JSON 序列化与反序列化基础库 |
| Lettuce | 7.5.2.RELEASE | Redis 独立客户端，支持同步、异步、响应式模式 |
| Redisson | 4.4.0 | 可选分布式锁适配实现 |
| Caffeine | 3.2.4 | 本地缓存实现 |
| SLF4J | 2.0.18 | 日志门面 |
| JUnit Jupiter | 5.14.4 | 单元测试框架 |
| AssertJ | 3.27.7 | 断言库 |

---

## 完整模块树

```text
jinfra
├── jinfra-bom
├── jinfra-all
│
├── jinfra-core
├── jinfra-context
├── jinfra-id
├── jinfra-io
├── jinfra-codec
├── jinfra-crypto
├── jinfra-json
├── jinfra-http
├── jinfra-validation
│
├── jinfra-office
├── jinfra-excel
├── jinfra-word
├── jinfra-ppt
│
├── jinfra-redis
├── jinfra-redis-lettuce
├── jinfra-redis-spring
│
├── jinfra-lock
├── jinfra-lock-redis
├── jinfra-lock-redisson
│
├── jinfra-mq
├── jinfra-mq-redis
│
├── jinfra-cache
├── jinfra-cache-caffeine
├── jinfra-cache-redis
│
├── jinfra-spring
├── jinfra-spring-boot-autoconfigure
├── jinfra-spring-boot-starter
├── jinfra-redis-spring-boot-starter
├── jinfra-office-spring-boot-starter
│
└── jinfra-test
```

---

## 模块说明

### 基础模块

| 模块 | 职责 |
|---|---|
| `jinfra-bom` | 统一管理 JInfra 内部模块和外部依赖版本 |
| `jinfra-all` | 聚合非 Spring Boot Starter 的通用能力 |
| `jinfra-core` | 核心异常、断言、对象、字符串、集合、结果模型、基础工具 |
| `jinfra-context` | TraceId、TenantId、UserId、MDC、线程上下文传播 |
| `jinfra-id` | UUID、ULID、Snowflake 等 ID 生成能力 |
| `jinfra-io` | 文件、流、临时文件、压缩、路径处理 |
| `jinfra-codec` | Base64、Hex、URL 编解码 |
| `jinfra-crypto` | 摘要、HMAC、AES、RSA、密码哈希等常用加密能力 |
| `jinfra-json` | Jackson 封装、ObjectMapper 工厂、JSON 工具 |
| `jinfra-http` | 基于 JDK HttpClient 的轻量 HTTP 客户端封装 |
| `jinfra-validation` | Jakarta Validation 辅助封装 |

### Office 模块

| 模块 | 职责 |
|---|---|
| `jinfra-office` | Office 公共抽象、模板模型、文档处理基础能力 |
| `jinfra-excel` | Excel 读写、模板填充、流式导入导出、样式封装 |
| `jinfra-word` | Word 模板填充、段落、表格、占位符替换 |
| `jinfra-ppt` | PowerPoint 模板填充、文本框、形状、图片替换 |

### Redis、锁、消息队列

| 模块 | 职责 |
|---|---|
| `jinfra-redis` | Redis 抽象层、序列化、Key 规范、Lua 脚本执行接口 |
| `jinfra-redis-lettuce` | 基于 Lettuce 的独立 Redis 客户端实现 |
| `jinfra-redis-spring` | Spring Data Redis 适配层 |
| `jinfra-lock` | 分布式锁 SPI、锁上下文、锁异常、锁执行模板 |
| `jinfra-lock-redis` | 基于 Redis Lua 的分布式锁实现，建议支持 fencing token |
| `jinfra-lock-redisson` | Redisson 可选适配实现 |
| `jinfra-mq` | 消息队列 SPI、消息模型、重试、消费确认抽象 |
| `jinfra-mq-redis` | 基于 Redis Streams 的轻量可靠消息队列实现 |

### 缓存模块

| 模块 | 职责 |
|---|---|
| `jinfra-cache` | 缓存 SPI、CacheManager、TTL、空值缓存、防击穿抽象 |
| `jinfra-cache-caffeine` | 基于 Caffeine 的本地缓存实现 |
| `jinfra-cache-redis` | 基于 Redis 的分布式缓存实现 |

### Spring Boot 模块

| 模块 | 职责 |
|---|---|
| `jinfra-spring` | Spring 通用辅助类、Bean 工具、环境工具 |
| `jinfra-spring-boot-autoconfigure` | JInfra 自动装配核心模块 |
| `jinfra-spring-boot-starter` | 通用 Starter |
| `jinfra-redis-spring-boot-starter` | Redis、Lock、MQ 相关 Starter |
| `jinfra-office-spring-boot-starter` | Office 相关 Starter |

### 测试模块

| 模块 | 职责 |
|---|---|
| `jinfra-test` | 测试基类、随机数据、临时文件、断言增强、测试容器封装预留 |

---

## 构建

```bash
mvn -U clean verify
```

发布到 Maven Central 前建议执行：

```bash
mvn -P release clean deploy
```

发布配置需要准备：

- Maven Central Portal 账号与 namespace
- GPG 签名密钥
- `settings.xml` 中的 `central` server 配置
- 完整的 `sources.jar`、`javadoc.jar`、`.asc` 签名文件

---

## 设计原则

1. **核心模块不依赖 Spring**：`jinfra-core` 必须保持轻量。
2. **抽象和实现分离**：Redis、Lock、MQ、Cache 先定义 SPI，再提供默认实现。
3. **Starter 不进入 all 包**：避免普通 Java 项目被动引入 Spring Boot。
4. **Office 优先支持 OOXML**：优先处理 `.xlsx`、`.docx`、`.pptx`，不建议首发支持旧版 `.xls`、`.doc`、`.ppt`。
5. **Redis MQ 优先 Redis Streams**：可靠队列优先使用 Streams，Pub/Sub 只适合广播场景。
6. **统一版本治理**：所有第三方依赖版本由根 POM 和 BOM 管理。
7. **默认可用，允许替换**：提供默认实现，但允许用户替换 Redis 客户端、序列化、锁实现、缓存实现。

---

## License

JInfra is released under the [MIT License](https://opensource.org/licenses/MIT).
