# BOM、jinfra-all、发布链路与 CI 设计

日期：2026-05-21

## 背景

JInfra 已完成根工程骨架与模块聚合。当前阶段需要把版本治理、可消费聚合包、Maven Central 发布要求和 CI 校验收敛到统一流程。

当前仓库状态：

- 根 `groupId` 已固定为 `cn.refinex`。
- 根 POM 已包含多模块 reactor、基础插件管理、release profile 雏形。
- `jinfra-bom` 已存在，但需要进一步明确 BOM 只做版本治理。
- `jinfra-all` 已存在，但需要收紧为非 Spring、非 Starter、非 AutoConfiguration 的通用能力聚合。
- 仓库当前没有 `.github/workflows/ci.yml`。

## 目标

本次任务目标是：

1. 在 `jinfra-bom` 中统一内部模块与第三方依赖版本治理。
2. 在 `jinfra-all` 中明确聚合边界，只聚合普通 Java 可消费的非 Spring Boot Starter 能力。
3. 配置 Maven Enforcer、Source、Javadoc、GPG、Central Publishing 插件与 release profile。
4. 补充 GitHub Actions CI，覆盖 `validate`、`test`、`verify`。
5. 明确 Central 发布前置条件与未指定运营要素。

## 参考依据

设计依据：

- Sonatype Central 要求 jar 模块提供 sources、javadocs、GPG 签名文件和必要 POM 元数据。
- Central Publisher Portal Maven 插件负责打包上传和校验，但不会自动生成全部前置附件。
- Maven BOM 通过 `dependencyManagement` 和 import scope 承接集中版本治理。
- GitHub Actions 可通过 `actions/setup-java` 配置 JDK、Maven cache、Maven server 与 GPG 私钥。

## 方案选择

采用“收敛现有配置，补齐 CI 与发布预演”方案。

该方案保留当前根 POM 的版本属性、插件管理和 release profile 基础，只做必要收敛：

- 调整 `jinfra-bom` 为清晰的版本治理入口。
- 调整 `jinfra-all` 为普通 Java 用户可消费的聚合包。
- 补齐 release profile 对 Central 所需附件的生成能力。
- 增加 CI workflow，保证 validate、test、verify 可持续校验。

不采用更复杂的多 profile 拆分方案，避免当前骨架阶段引入过多发布开关。

## BOM 设计

`jinfra-bom` 保持 `packaging=pom`，只承担版本治理职责。

它在 `dependencyManagement` 中管理两类版本：

- 内部模块：使用 `${project.groupId}` 和 `${project.version}` 管理所有可消费 JInfra 模块版本。
- 第三方依赖：继续管理 Spring Boot BOM、Jackson BOM、JUnit BOM，以及未被 BOM 覆盖或需要显式锁定的依赖，例如 Apache POI、Lettuce、Redisson、Caffeine、SLF4J、Commons、Jakarta Validation、Hibernate Validator、AssertJ。

`jinfra-bom` 不声明普通 `<dependencies>`，避免用户 import BOM 时产生实际依赖。

## jinfra-all 设计

`jinfra-all` 定义为“非 Starter、非 AutoConfiguration、非测试辅助、非 Spring 适配”的普通 Java 能力聚合包。

保留聚合：

- 基础能力：`jinfra-core`、`jinfra-context`、`jinfra-id`、`jinfra-io`、`jinfra-codec`、`jinfra-crypto`、`jinfra-json`、`jinfra-http`、`jinfra-validation`
- Office 能力：`jinfra-office`、`jinfra-excel`、`jinfra-word`、`jinfra-ppt`
- Redis 非 Spring 能力：`jinfra-redis`、`jinfra-redis-lettuce`
- 锁能力：`jinfra-lock`、`jinfra-lock-redis`、`jinfra-lock-redisson`
- MQ 能力：`jinfra-mq`、`jinfra-mq-redis`
- 缓存能力：`jinfra-cache`、`jinfra-cache-caffeine`、`jinfra-cache-redis`

排除模块：

- `jinfra-spring`
- `jinfra-redis-spring`
- `jinfra-spring-boot-autoconfigure`
- `jinfra-spring-boot-starter`
- `jinfra-redis-spring-boot-starter`
- `jinfra-office-spring-boot-starter`
- `jinfra-test`

这样可以避免普通 Java 用户通过 `jinfra-all` 被动引入 Spring、Spring Boot 自动装配或测试辅助依赖。

## Release Profile 设计

根 POM 继续作为构建与发布治理中心。

默认构建：

- 执行 Maven Enforcer。
- 执行编译、测试、打包等基础生命周期。
- 不触发 GPG 签名。
- 不触发 Central 上传。
- 不执行会改写开发态 POM 的 flatten 逻辑。

`release` profile：

- `maven-source-plugin:jar-no-fork` 绑定 `verify`，生成 `-sources.jar`。
- `maven-javadoc-plugin:jar` 绑定 `verify`，生成 `-javadoc.jar`。
- `maven-gpg-plugin:sign` 绑定 `verify`，生成 `.asc` 签名附件。
- `central-publishing-maven-plugin` 保留 `extensions=true`，配置 `publishingServerId=central`、`autoPublish=false`、`waitUntil=validated`。
- `flatten-maven-plugin` 只在 release profile 中启用，避免普通构建污染开发态 POM。

验证命令：

```bash
mvn -P release -DskipTests verify
```

该命令在具备 GPG 环境时应能生成 sources、javadocs 和 signatures。若 GPG 密钥或 passphrase 缺失，签名失败是预期结果，必须标记为运营要素未指定，而不是在代码中伪造成功。

## CI 设计

新增 `.github/workflows/ci.yml`。

触发条件：

- `push`
- `pull_request`

环境：

- Ubuntu 最新稳定运行环境。
- JDK 17。
- Maven cache。

CI 步骤：

1. Checkout 代码。
2. 使用 `actions/setup-java` 安装 JDK 17 并启用 Maven cache。
3. 运行 `mvn -B -DskipTests validate`。
4. 运行 `mvn -B test`。
5. 运行 `mvn -B verify`。
6. 运行 release profile 的非签名发布预演，例如 `mvn -B -P release -DskipTests -Dgpg.skip=true verify`，用于验证 Source/Javadoc/release profile 模型，不上传 Central，不伪造 GPG 签名成功。

CI 不执行真实 Central 发布。真实发布需要单独提供 Central token 与 GPG 密钥后再配置手动发布或 tag 发布 workflow。

## 发布前置条件

以下运营要素不属于代码实现，当前均为未指定：

- Central Publisher Portal namespace 验证信息：未指定。
- Central Portal token username：未指定。
- Central Portal token password：未指定。
- GPG 私钥：未指定。
- GPG passphrase：未指定。
- GitHub Secrets 命名与注入策略：未指定。
- 是否自动发布 `autoPublish=true`：未指定，当前设计保持 `false`，走手动确认发布。

## 验收标准

代码验收：

- `jinfra-bom` 只做版本治理，不声明普通依赖。
- `jinfra-all` 不依赖 `jinfra-spring-boot-*`、`jinfra-spring-boot-autoconfigure`、`jinfra-spring`、`jinfra-redis-spring`、`jinfra-test`。
- 根 POM release profile 具备 sources、javadocs、GPG signatures 和 Central Publishing 配置。
- `.github/workflows/ci.yml` 存在，并覆盖 `validate`、`test`、`verify`。

本地验证命令：

```bash
mvn -DskipTests validate
mvn test
mvn verify
mvn -P release -DskipTests verify
```

说明：

- 前三个命令应在普通开发环境通过。
- 第四个命令需要 GPG 环境。若缺失 GPG 密钥或 passphrase，失败应归类为运营要素未指定。

## 交付输出

最终实现完成后，输出必须包含：

- 发布前置条件
- 变更文件
- 关键配置
- 本地验证命令
- 待运营补充项

## 不在本次范围内

- 实现业务 API。
- 编写真实自动装配类。
- 真实上传到 Maven Central。
- 创建或提交 Central token、GPG 私钥、passphrase。
- 历史重写或清理既有远程提交。
