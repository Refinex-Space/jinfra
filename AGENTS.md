# JInfra

JInfra 是面向 Java 企业级应用开发的模块化基础设施工具包，通过 Maven 多模块提供低侵入、可按需引入的基础能力。

## Agent Rules

- 默认用中文回复；代码上下文、类名、方法名和 Maven 坐标保持英文。
- Java 注释和 Javadoc 默认使用中文，除非所在文件已有明确英文风格。
- 解释先给推理证据，再给结论；保持简洁、数据驱动。
- 代码审查优先级：安全 > 正确性 > 性能 > 可读性。
- 涉及库/API 文档、代码生成、配置或 setup 步骤时，优先使用 Context7 MCP 获取当前官方文档。
- 展示改动时优先给 diff，不做整文件重写式说明。

## Tech Stack

- **Language:** Java 17
- **Build:** Maven multi-module reactor，最低 Maven 3.9.0
- **Group:** `cn.refinex`
- **Versioning:** `${revision}`，当前 `0.1.0-SNAPSHOT`
- **Spring:** Spring Boot 3.5.14 dependency management
- **Office:** Apache POI 5.5.1
- **Redis:** Lettuce 7.5.2.RELEASE，Redisson 4.4.0
- **Cache:** Caffeine 3.2.4
- **JSON:** Jackson 2.21.3
- **Testing:** JUnit Jupiter 5.14.4，AssertJ 3.27.7

## Current State

当前仓库是“可构建 Maven 骨架”：所有 README 规划模块已有目录和 `pom.xml`，但非 `target/` 下 Java 源码数量为 0。不要把 `README.md`、`PLANS.md` 或 `docs/superpowers/` 中的未来 API/SPI 当作已实现事实。

## Architecture

根 `pom.xml` 是 reactor、版本属性、依赖管理和插件管理入口。`jinfra-bom` 导出版本治理；`jinfra-all` 聚合非 Spring Boot Starter 能力；能力模块按“抽象模块 -> 实现/适配模块 -> Spring Boot Starter”推进。

详见 `docs/architecture.md`。

### Dependency Direction

`core` -> 基础工具模块 -> 抽象 SPI 模块 -> 实现适配模块 -> Spring / Spring Boot 集成模块 -> Starter 聚合模块

实现模块可以依赖上游抽象；上游抽象不得反向依赖具体实现或 Starter。

## Module Map

| Directory | Purpose |
|---|---|
| `jinfra-bom/` | BOM，统一导出内部模块和第三方依赖版本 |
| `jinfra-all/` | 非 Spring Boot Starter 的通用能力聚合 |
| `jinfra-core/` | 核心异常、断言、工具、结果模型底座 |
| `jinfra-context/` `jinfra-id/` `jinfra-io/` `jinfra-codec/` `jinfra-crypto/` `jinfra-json/` `jinfra-http/` `jinfra-validation/` | 基础能力模块 |
| `jinfra-office/` `jinfra-excel/` `jinfra-word/` `jinfra-ppt/` | Office 公共抽象和文档能力 |
| `jinfra-redis/` `jinfra-redis-lettuce/` `jinfra-redis-spring/` | Redis 抽象与适配 |
| `jinfra-lock/` `jinfra-lock-redis/` `jinfra-lock-redisson/` | 分布式锁抽象与实现 |
| `jinfra-mq/` `jinfra-mq-redis/` | MQ 抽象与 Redis Streams 实现 |
| `jinfra-cache/` `jinfra-cache-caffeine/` `jinfra-cache-redis/` | 缓存抽象与实现 |
| `jinfra-spring/` `jinfra-spring-boot-autoconfigure/` | Spring / Spring Boot 集成 |
| `jinfra-spring-boot-starter/` `jinfra-redis-spring-boot-starter/` `jinfra-office-spring-boot-starter/` | Starter 依赖聚合 |
| `jinfra-test/` | 测试辅助模块 |

## Hard Invariants

1. 子模块继承根 `cn.refinex:jinfra:${revision}`，不单独声明内部版本。
2. 根 `pom.xml` 的 `<modules>` 必须与实际模块目录保持一致。
3. `jinfra-core` 和基础抽象模块不得依赖 Spring Boot Starter。
4. `jinfra-all` 不聚合 `jinfra-*-spring-boot-starter`。
5. 抽象模块不得依赖其具体实现模块，例如 `jinfra-lock` 不依赖 `jinfra-lock-redis`。
6. 受父 POM 管理的依赖在子模块中不写 `<version>`。
7. 普通构建不得改写开发态 POM；flatten/GPG/Central 发布行为只在 release 流程中使用。

## Build & Test Commands

```bash
mvn -q -DskipTests validate
mvn clean verify
mvn -DskipTests install
mvn -P release -DskipTests verify
```

## Conventions

- Java 包名使用小写反向域名，匹配 `cn.refinex.jinfra.*`。
- 公共 API 新增前先明确所在模块边界，避免跨模块循环依赖。
- 新增可替换能力时优先定义抽象/SPI，再添加默认实现模块。
- Spring Boot Starter 只做依赖聚合；自动装配逻辑放入 `jinfra-spring-boot-autoconfigure`。
- 公共类、接口和方法需要 Javadoc；复杂逻辑用少量中文注释解释原因。
- 不提交 `target/`、IDE 元数据或发布凭证。

## Key Documentation

- `README.md` — 项目定位、模块树和使用方式。
- `PLANS.md` — 阶段规划和实现提示，属于计划，不等同于当前代码事实。
- `docs/superpowers/` — 历史规格和任务计划。
- `docs/architecture.md` — 当前 agent 架构索引。
