# JInfra Architecture

本文记录当前仓库已经落地的架构事实，供 agent 在实现模块时快速判断边界。它不是路线图；未出现在当前 `pom.xml`、模块目录或源码中的能力，不应被当作已实现功能。

## Current Snapshot

- 根工程是 Maven reactor，`packaging=pom`。
- 当前版本由根属性 `${revision}` 管理，值为 `0.1.0-SNAPSHOT`。
- JDK 基线为 Java 17，Maven 基线为 3.9.0。
- README 规划的 32 个模块目录和子 `pom.xml` 已存在。
- 非 `target/` 下当前没有 Java 源码文件。
- 当前没有 GitHub Actions workflow。

## Module Groups

### Governance

- `jinfra-bom`：BOM 模块，导出 JInfra 内部模块和第三方依赖版本。
- `jinfra-all`：非 Spring Boot Starter 的通用能力聚合包。

### Core And Utilities

- `jinfra-core`：核心异常、断言、对象/字符串/集合工具、结果模型和基础工具。
- `jinfra-context`：Trace、Tenant、User、MDC 与线程上下文传播。
- `jinfra-id`：UUID、ULID、Snowflake 风格 ID。
- `jinfra-io`：文件、流、临时文件、压缩、路径处理。
- `jinfra-codec`：Base64、Hex、URL 编解码。
- `jinfra-crypto`：摘要、HMAC、AES、RSA、密码哈希。
- `jinfra-json`：Jackson ObjectMapper 工厂与 JSON 工具。
- `jinfra-http`：基于 JDK HttpClient 的轻量 HTTP 客户端封装。
- `jinfra-validation`：Jakarta Validation 辅助能力。

### Office

- `jinfra-office`：Office 公共抽象、模板模型、文档处理基础能力。
- `jinfra-excel`：Excel 读写、模板填充、流式导入导出、样式封装。
- `jinfra-word`：Word 模板填充、段落、表格、占位符替换。
- `jinfra-ppt`：PowerPoint 模板填充、文本框、形状、图片替换。

### Redis, Lock, MQ, Cache

- `jinfra-redis`：Redis 抽象层、序列化、Key 规范、Lua 脚本执行接口。
- `jinfra-redis-lettuce`：基于 Lettuce 的独立 Redis 客户端实现。
- `jinfra-redis-spring`：Spring Data Redis 适配层。
- `jinfra-lock`：分布式锁 SPI、锁上下文、锁异常、锁执行模板。
- `jinfra-lock-redis`：基于 Redis Lua 的分布式锁实现。
- `jinfra-lock-redisson`：Redisson 可选适配实现。
- `jinfra-mq`：消息队列 SPI、消息模型、重试、消费确认抽象。
- `jinfra-mq-redis`：基于 Redis Streams 的轻量可靠消息队列实现。
- `jinfra-cache`：缓存 SPI、CacheManager、TTL、空值缓存、防击穿抽象。
- `jinfra-cache-caffeine`：基于 Caffeine 的本地缓存实现。
- `jinfra-cache-redis`：基于 Redis 的分布式缓存实现。

### Spring Integration

- `jinfra-spring`：Spring 通用辅助类、Bean 工具、环境工具。
- `jinfra-spring-boot-autoconfigure`：JInfra 自动装配核心模块。
- `jinfra-spring-boot-starter`：通用 Starter。
- `jinfra-redis-spring-boot-starter`：Redis、Lock、MQ 相关 Starter。
- `jinfra-office-spring-boot-starter`：Office 相关 Starter。

### Testing

- `jinfra-test`：测试基类、随机数据、临时文件、断言增强、测试容器封装预留。

## Dependency Model

依赖方向遵循从稳定底座到具体集成的单向流动：

```text
jinfra-core
  -> base utility modules
  -> abstraction modules
  -> implementation / adapter modules
  -> spring integration
  -> spring boot starters
```

模块实现时遵守以下边界：

- 抽象模块定义契约，不依赖具体实现。
- 实现模块依赖对应抽象模块和必要第三方库。
- Spring 适配依赖 Spring 生态，但基础模块不反向依赖 Spring。
- Starter 负责依赖聚合，复杂自动装配逻辑放在 `jinfra-spring-boot-autoconfigure`。
- `jinfra-all` 聚合普通能力，不聚合 Spring Boot Starter。

## Build Model

根 `pom.xml` 负责：

- reactor `<modules>`；
- 公共属性和版本号；
- `dependencyManagement`；
- `pluginManagement`；
- Java/Maven 版本校验；
- release profile 中的 flatten、sources、javadocs、GPG 和 Central 发布配置。

子模块 `pom.xml` 负责：

- 继承根 POM；
- 声明自身 `artifactId`、`name`、`description`；
- 对 jar 模块声明 `automatic.module.name`；
- 声明本模块直接依赖，不写受父 POM 管理的版本号。

## Implementation Guidance

新增 Java 代码时先确认模块职责，再确认包路径和依赖方向。公共 API 优先放在抽象模块，具体实现放在适配模块；不要为了让当前模块编译通过而把上层依赖拉入底层模块。

公共类、接口和公共方法应提供中文 Javadoc。涉及资源管理、并发、加密、Redis、Office 大文件处理或 Spring Boot 自动装配时，先查当前官方文档或 Context7，再写实现。

## Known Gaps

- Java API/SPI 尚未落地，不能从源码提取命名、异常、日志和测试模式。
- CI workflow 尚未存在。
- 没有模块级 `AGENTS.md`；待各能力模块形成稳定源码结构后再补充。
- README 中部分能力描述属于目标状态，AGENTS 只应引用已经落地的构建和模块事实。
