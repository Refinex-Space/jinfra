# JInfra 全量模块初始化设计

日期：2026-05-20

## 背景

JInfra 是一个面向 Java 企业级应用开发的模块化基础设施工具包。README 已规划完整 Maven 模块树，并将能力划分为基础、Office、Redis、锁、消息队列、缓存、Spring Boot 和测试模块。

当前仓库已经有根 `pom.xml`，并在 `<modules>` 中声明了 README 的完整模块集合，但实际子模块目录尚未创建。因此当前构建的主要失败点不是业务代码，而是 Maven reactor 无法找到已声明模块。

本次初始化目标是按 README 的完整模块树创建全部模块，并确保根目录执行 `mvn clean verify` 可以通过。

## 范围

本次初始化包含 README 中的全部 32 个模块：

- `jinfra-bom`
- `jinfra-all`
- `jinfra-core`
- `jinfra-context`
- `jinfra-id`
- `jinfra-io`
- `jinfra-codec`
- `jinfra-crypto`
- `jinfra-json`
- `jinfra-http`
- `jinfra-validation`
- `jinfra-office`
- `jinfra-excel`
- `jinfra-word`
- `jinfra-ppt`
- `jinfra-redis`
- `jinfra-redis-lettuce`
- `jinfra-redis-spring`
- `jinfra-lock`
- `jinfra-lock-redis`
- `jinfra-lock-redisson`
- `jinfra-mq`
- `jinfra-mq-redis`
- `jinfra-cache`
- `jinfra-cache-caffeine`
- `jinfra-cache-redis`
- `jinfra-spring`
- `jinfra-spring-boot-autoconfigure`
- `jinfra-spring-boot-starter`
- `jinfra-redis-spring-boot-starter`
- `jinfra-office-spring-boot-starter`
- `jinfra-test`

不在本次范围内：

- 业务 API、SPI 接口或具体实现代码。
- Spring Boot 自动装配类和自动装配注册文件。
- 单元测试业务用例。
- Maven Central 发布验证。

## 推荐方案

采用“可构建骨架 + 最小依赖闭环”方案。

每个模块创建标准 Maven 子模块结构和 `pom.xml`，继承根 `jinfra` 父 POM，使用统一 `groupId` 和 `version`。除 `jinfra-bom` 使用 `pom` packaging 外，其余模块默认使用 `jar` packaging。

该方案的优点是：

- 与当前根 POM 的完整 `<modules>` 声明一致，改动面小。
- 能先解决整体编译问题，为后续 API 和实现开发建立稳定边界。
- 不提前定义未确认的业务接口，避免后续返工。

## 模块边界

依赖边界遵循 README 的设计原则：

- `jinfra-core` 是轻量底座，不依赖 Spring。
- 抽象模块先于实现模块，例如 `jinfra-lock` 是锁 SPI，`jinfra-lock-redis` 和 `jinfra-lock-redisson` 是实现适配。
- Office、Redis、Lock、MQ、Cache 等能力按公共抽象和具体实现拆分。
- Spring Boot Starter 只负责组合依赖，不进入 `jinfra-all`。
- 实现模块只依赖必要的上游抽象和第三方库，避免形成依赖环。

建议依赖关系：

- `jinfra-json` 依赖 `jinfra-core` 和 Jackson。
- `jinfra-office` 依赖 `jinfra-core` 和 Apache POI。
- `jinfra-excel`、`jinfra-word`、`jinfra-ppt` 依赖 `jinfra-office`。
- `jinfra-redis-lettuce` 依赖 `jinfra-redis` 和 Lettuce。
- `jinfra-lock-redis` 依赖 `jinfra-lock` 和 `jinfra-redis`。
- `jinfra-lock-redisson` 依赖 `jinfra-lock` 和 Redisson。
- `jinfra-mq-redis` 依赖 `jinfra-mq` 和 `jinfra-redis`。
- `jinfra-cache-caffeine` 依赖 `jinfra-cache` 和 Caffeine。
- `jinfra-cache-redis` 依赖 `jinfra-cache` 和 `jinfra-redis`。
- `jinfra-spring` 依赖 Spring Context。
- `jinfra-spring-boot-autoconfigure` 依赖 Spring Boot autoconfigure 及需要自动装配的基础能力模块。
- 三个 Spring Boot Starter 依赖对应 autoconfigure 和功能模块。

## BOM 与聚合包

`jinfra-bom` 是纯 BOM 模块，使用 `packaging=pom`。它在自己的 `dependencyManagement` 中导出 JInfra 内部模块和 README 规划的第三方依赖版本，使用户可以通过 `<type>pom</type>` 和 `<scope>import</scope>` 引入。

`jinfra-all` 是非 Spring Boot Starter 能力聚合包。它可以依赖基础、Office、Redis、Lock、MQ、Cache 和测试辅助模块，但不依赖以下 Starter：

- `jinfra-spring-boot-starter`
- `jinfra-redis-spring-boot-starter`
- `jinfra-office-spring-boot-starter`

## 构建配置

根 POM 继续负责 reactor、统一版本属性、依赖管理和插件管理。子模块只声明自身职责所需依赖，避免重复声明版本。

当前根 POM 已绑定 `flatten-maven-plugin` 到 `process-resources`，且配置了 `updatePomFile=true`。实现时需要检查该配置是否会在普通 `mvn clean verify` 中改写开发态 POM。如果会造成工作树污染，应将 flatten 行为调整为不覆盖开发态 POM，或仅在 release profile 中启用。

该调整属于本次范围，因为验收标准要求构建通过后不留下无关 POM 污染。

## 错误处理

本次不新增运行时异常体系。构建失败时按以下顺序排查：

1. 子模块目录是否与根 POM `<modules>` 一致。
2. 父子 POM 坐标是否一致。
3. 模块间是否存在依赖循环。
4. 第三方依赖版本是否可解析。
5. 插件配置是否在普通构建中改写开发态文件。

## 测试与验收

主要验收命令：

```bash
mvn clean verify
```

辅助定位命令：

```bash
mvn -DskipTests validate
mvn -DskipTests install
```

验收标准：

- README 中完整模块目录全部存在。
- 根 `pom.xml` 的 `<modules>` 与实际目录一致。
- 每个子模块都有可继承父 POM 的最小 `pom.xml`。
- `jinfra-bom` 可作为 BOM 导入。
- `jinfra-all` 不聚合 Spring Boot Starter。
- `mvn clean verify` 在 Java 17 和 Maven 3.9.x 下通过。
- 构建验证后不留下无关构建副产物或 POM 污染。

## 已确认决策

- 初始化范围选择 README 完整模块树，而不是只初始化 0.1.x 首发模块。
- 采用可构建骨架，不提前编写业务 API 或 SPI。
- 保留当前根 POM 中已有的项目元信息变更，例如 `groupId=refinex.cn`、MIT License 和 GitHub URL。
