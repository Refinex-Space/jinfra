# BOM, jinfra-all, Release, and CI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Tighten JInfra version governance, define the non-Spring aggregate package, add Maven Central release readiness, add CI, and document the first-time Maven Central publishing workflow.

**Architecture:** Keep the root POM as the release and plugin governance point, keep `jinfra-bom` as a pure dependency-management BOM, and keep `jinfra-all` as a normal Java aggregate that excludes Spring, auto-configuration, starters, and test helpers. CI validates normal build phases and a non-signing release-profile dry run; the publish guide documents the manual Central Portal and GPG steps separately from code.

**Tech Stack:** Java 17, Maven 3.9.x, Maven Enforcer, Maven Source Plugin, Maven Javadoc Plugin, Maven GPG Plugin, Central Publishing Maven Plugin, GitHub Actions, actions/setup-java Maven cache.

---

## File Structure

Modify:

- `/Users/refinex/develop/project/jinfra/pom.xml` — release plugin configuration, Central publishing options, Enforcer rules, skip properties.
- `/Users/refinex/develop/project/jinfra/jinfra-bom/pom.xml` — pure BOM dependency management.
- `/Users/refinex/develop/project/jinfra/jinfra-all/pom.xml` — non-Spring aggregate dependency boundary.

Create:

- `/Users/refinex/develop/project/jinfra/.github/workflows/ci.yml` — CI workflow for validate, test, verify, and release-profile dry run.
- `/Users/refinex/develop/project/jinfra/docs/release/maven-central-publishing.md` — user-facing first-time Maven Central publishing guide.

No Java source files are modified in this plan.

## Design Constraints

- `groupId` remains `cn.refinex`.
- `jinfra-all` excludes `jinfra-spring`, `jinfra-redis-spring`, `jinfra-spring-boot-autoconfigure`, every `jinfra-*-spring-boot-starter`, and `jinfra-test`.
- Real Central publishing is not run in CI.
- Credentials, tokens, GPG private key, passphrase, and namespace state are documented as `未指定`.
- Do not commit secrets.
- The release guide goes under `/Users/refinex/develop/project/jinfra/docs/release/maven-central-publishing.md`, not only under `docs/superpowers`.

### Task 1: Tighten `jinfra-all` Aggregate Boundary

**Files:**
- Modify: `/Users/refinex/develop/project/jinfra/jinfra-all/pom.xml`

- [ ] **Step 1: Inspect current aggregate dependencies**

Run:

```bash
rg -n "<artifactId>jinfra-(spring|redis-spring|spring-boot|.*spring-boot-starter|test)</artifactId>" jinfra-all/pom.xml
```

Expected current output includes at least:

```text
jinfra-all/pom.xml:<line>:            <artifactId>jinfra-redis-spring</artifactId>
jinfra-all/pom.xml:<line>:            <artifactId>jinfra-spring</artifactId>
jinfra-all/pom.xml:<line>:            <artifactId>jinfra-test</artifactId>
```

- [ ] **Step 2: Remove Spring and test dependencies from `jinfra-all`**

Edit `/Users/refinex/develop/project/jinfra/jinfra-all/pom.xml` so its `<dependencies>` contains exactly these artifactIds in this order:

```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-core</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-context</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-id</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-io</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-codec</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-crypto</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-json</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-http</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-validation</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-office</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-excel</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-word</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-ppt</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-redis</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-redis-lettuce</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-lock</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-lock-redis</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-lock-redisson</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-mq</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-mq-redis</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-cache</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-cache-caffeine</artifactId>
</dependency>
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>jinfra-cache-redis</artifactId>
</dependency>
```

- [ ] **Step 3: Verify excluded modules are absent**

Run:

```bash
rg -n "<artifactId>jinfra-(spring|redis-spring|spring-boot|.*spring-boot-starter|test)</artifactId>" jinfra-all/pom.xml
```

Expected:

```text
No matches.
```

- [ ] **Step 4: Validate the aggregate module**

Run:

```bash
mvn -pl jinfra-all -am -DskipTests validate
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 5: Commit aggregate boundary change**

Run:

```bash
git add jinfra-all/pom.xml
git commit -m "build: 收紧jinfra-all聚合边界"
```

Expected:

```text
Commit succeeds and includes only jinfra-all/pom.xml.
```

### Task 2: Normalize BOM Version Governance

**Files:**
- Modify: `/Users/refinex/develop/project/jinfra/jinfra-bom/pom.xml`

- [ ] **Step 1: Verify BOM has no normal dependencies**

Run:

```bash
rg -n "^    <dependencies>|^    </dependencies>" jinfra-bom/pom.xml
```

Expected:

```text
No matches.
```

- [ ] **Step 2: Keep BOM dependency management complete and ordered**

Edit `/Users/refinex/develop/project/jinfra/jinfra-bom/pom.xml` so `dependencyManagement` has this order:

1. Imported BOMs:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-dependencies</artifactId>
    <version>${spring-boot.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson</groupId>
    <artifactId>jackson-bom</artifactId>
    <version>${jackson.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
<dependency>
    <groupId>org.junit</groupId>
    <artifactId>junit-bom</artifactId>
    <version>${junit-jupiter.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

2. JInfra modules:

```text
jinfra-all
jinfra-core
jinfra-context
jinfra-id
jinfra-io
jinfra-codec
jinfra-crypto
jinfra-json
jinfra-http
jinfra-validation
jinfra-office
jinfra-excel
jinfra-word
jinfra-ppt
jinfra-redis
jinfra-redis-lettuce
jinfra-redis-spring
jinfra-lock
jinfra-lock-redis
jinfra-lock-redisson
jinfra-mq
jinfra-mq-redis
jinfra-cache
jinfra-cache-caffeine
jinfra-cache-redis
jinfra-spring
jinfra-spring-boot-autoconfigure
jinfra-spring-boot-starter
jinfra-redis-spring-boot-starter
jinfra-office-spring-boot-starter
jinfra-test
```

Each JInfra module entry uses:

```xml
<groupId>${project.groupId}</groupId>
<version>${project.version}</version>
```

3. Explicit third-party dependencies:

```text
org.slf4j:slf4j-api:${slf4j.version}
org.apache.commons:commons-lang3:${commons-lang3.version}
commons-io:commons-io:${commons-io.version}
commons-codec:commons-codec:${commons-codec.version}
org.apache.poi:poi-ooxml:${apache-poi.version}
io.lettuce:lettuce-core:${lettuce.version}
org.redisson:redisson:${redisson.version}
com.github.ben-manes.caffeine:caffeine:${caffeine.version}
jakarta.validation:jakarta.validation-api:${jakarta-validation.version}
org.hibernate.validator:hibernate-validator:${hibernate-validator.version}
org.assertj:assertj-core:${assertj.version}
```

The current file already mostly matches this structure. Only change it if ordering or missing entries differ.

- [ ] **Step 3: Verify BOM remains dependency-management only**

Run:

```bash
rg -n "^    <dependencies>|^    </dependencies>" jinfra-bom/pom.xml
mvn -pl jinfra-bom -DskipTests validate
```

Expected:

```text
The rg command prints no matches.
The Maven command ends with BUILD SUCCESS.
```

- [ ] **Step 4: Commit BOM governance changes if any**

If Step 2 changed the file, run:

```bash
git add jinfra-bom/pom.xml
git commit -m "build: 规范BOM版本治理"
```

Expected when changed:

```text
Commit succeeds and includes only jinfra-bom/pom.xml.
```

Expected when unchanged:

```text
Skip this commit step.
```

### Task 3: Harden Root Release Profile

**Files:**
- Modify: `/Users/refinex/develop/project/jinfra/pom.xml`

- [ ] **Step 1: Inspect current release profile**

Run:

```bash
rg -n "gpg.skip|central.skip|maven-source-plugin|maven-javadoc-plugin|maven-gpg-plugin|central-publishing-maven-plugin|flatten-maven-plugin|requireJavaVersion|requireMavenVersion|requirePluginVersions" pom.xml
```

Expected:

```text
The output shows existing enforcer rules, source, javadoc, gpg, central publishing, and flatten plugin configuration.
```

- [ ] **Step 2: Add Central skip property**

In `/Users/refinex/develop/project/jinfra/pom.xml`, add this property next to `gpg.skip`:

```xml
<central.skip>true</central.skip>
```

Keep:

```xml
<gpg.skip>true</gpg.skip>
```

- [ ] **Step 3: Configure GPG for non-interactive release use**

In `pluginManagement`, update `maven-gpg-plugin` configuration to:

```xml
<configuration>
    <skip>${gpg.skip}</skip>
    <gpgArguments>
        <arg>--batch</arg>
        <arg>--pinentry-mode</arg>
        <arg>loopback</arg>
    </gpgArguments>
</configuration>
```

Do not hardcode a passphrase.

- [ ] **Step 4: Configure Central Publishing skip switch**

In `pluginManagement`, update `central-publishing-maven-plugin` configuration to include:

```xml
<skipPublishing>${central.skip}</skipPublishing>
```

Keep:

```xml
<extensions>true</extensions>
```

- [ ] **Step 5: Make release profile explicit**

In the `release` profile properties, set:

```xml
<gpg.skip>false</gpg.skip>
<central.skip>false</central.skip>
<skipTests>false</skipTests>
```

In the release profile `central-publishing-maven-plugin` configuration, include:

```xml
<skipPublishing>${central.skip}</skipPublishing>
<publishingServerId>central</publishingServerId>
<autoPublish>false</autoPublish>
<waitUntil>validated</waitUntil>
```

- [ ] **Step 6: Verify a non-signing release dry run works**

Run:

```bash
mvn -B -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify
```

Expected:

```text
BUILD SUCCESS
```

This command must generate source and javadoc jars while skipping GPG signing and Central upload.

- [ ] **Step 7: Verify release attachments exist for representative modules**

Run:

```bash
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-sources.jar
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-javadoc.jar
test -f jinfra-all/target/jinfra-all-0.1.0-SNAPSHOT-sources.jar
test -f jinfra-all/target/jinfra-all-0.1.0-SNAPSHOT-javadoc.jar
```

Expected:

```text
All test commands exit 0.
```

- [ ] **Step 8: Commit release profile hardening**

Run:

```bash
git add pom.xml
git commit -m "build: 完善Central发布配置"
```

Expected:

```text
Commit succeeds and includes only pom.xml.
```

### Task 4: Add GitHub Actions CI

**Files:**
- Create: `/Users/refinex/develop/project/jinfra/.github/workflows/ci.yml`

- [ ] **Step 1: Create workflow directory**

Run:

```bash
mkdir -p .github/workflows
```

Expected:

```text
Directory .github/workflows exists.
```

- [ ] **Step 2: Create CI workflow**

Create `/Users/refinex/develop/project/jinfra/.github/workflows/ci.yml` with:

```yaml
name: CI

on:
  push:
    branches:
      - main
      - dev
  pull_request:
    branches:
      - main
      - dev

permissions:
  contents: read

jobs:
  build:
    name: Maven Build
    runs-on: ubuntu-latest
    timeout-minutes: 30

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven

      - name: Validate Maven model
        run: mvn -B -DskipTests validate

      - name: Run tests
        run: mvn -B test

      - name: Verify build
        run: mvn -B verify

      - name: Release profile dry run without signing or publishing
        run: mvn -B -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify
```

- [ ] **Step 3: Verify workflow syntax enough for YAML parsing**

Run:

```bash
test -f .github/workflows/ci.yml
rg -n "mvn -B -DskipTests validate|mvn -B test|mvn -B verify|mvn -B -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify|cache: maven" .github/workflows/ci.yml
```

Expected:

```text
All five patterns are printed.
```

- [ ] **Step 4: Commit CI workflow**

Run:

```bash
git add .github/workflows/ci.yml
git commit -m "ci: 添加Maven构建校验"
```

Expected:

```text
Commit succeeds and includes only .github/workflows/ci.yml.
```

### Task 5: Document First-Time Maven Central Publishing Steps

**Files:**
- Create: `/Users/refinex/develop/project/jinfra/docs/release/maven-central-publishing.md`

- [ ] **Step 1: Create release docs directory**

Run:

```bash
mkdir -p docs/release
```

Expected:

```text
Directory docs/release exists.
```

- [ ] **Step 2: Create Maven Central publishing guide**

Create `/Users/refinex/develop/project/jinfra/docs/release/maven-central-publishing.md` with:

```markdown
# Maven Central 发布操作手册

## 适用范围

本文面向第一次将 JInfra 发布到 Maven Central 的维护者。代码侧已经配置 Central Publisher Portal 发布链路，但真实发布仍依赖账号、命名空间、GPG 密钥和 GitHub Secrets。

## 当前未指定项

- Central Publisher Portal namespace 验证信息：未指定。
- Central Portal token username：未指定。
- Central Portal token password：未指定。
- GPG 私钥：未指定。
- GPG passphrase：未指定。
- GitHub Secrets 命名与注入策略：未指定。
- 是否自动发布 `autoPublish=true`：未指定，当前保持手动确认发布。

## 1. 注册并验证 Central Publisher Portal

1. 打开 [Central Publisher Portal](https://central.sonatype.com/)。
2. 使用你的 Sonatype 账号登录；如果没有账号，先注册。
3. 创建或选择 namespace。
4. 为 `cn.refinex` 完成域名所有权验证。
5. 等待 Portal 显示 namespace 验证通过。

如果 `cn.refinex` 的域名验证尚未完成，不要发布 `cn.refinex:*` 坐标。

## 2. 生成 Central Portal Token

1. 在 Central Publisher Portal 中打开账号或 token 设置。
2. 生成发布 token。
3. 记录 token username 和 token password。
4. 不要把 token 写入仓库。

建议本地 `~/.m2/settings.xml` 使用 server id `central`：

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>未指定</username>
      <password>未指定</password>
    </server>
  </servers>
</settings>
```

## 3. 准备 GPG 签名

1. 检查是否已有 GPG：

```bash
gpg --version
gpg --list-secret-keys --keyid-format LONG
```

2. 如果没有密钥，生成新密钥：

```bash
gpg --full-generate-key
```

3. 导出公钥并发布到 keyserver：

```bash
gpg --armor --export <KEY_ID> > public-key.asc
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
```

4. 本地发布时确保私钥可用，并准备 passphrase。

`<KEY_ID>` 是你自己的 GPG key id，当前未指定。

## 4. 本地发布前验证

普通构建验证：

```bash
mvn -DskipTests validate
mvn test
mvn verify
```

Release profile 非签名预演：

```bash
mvn -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify
```

具备 GPG 后验证附件和签名：

```bash
mvn -P release -DskipTests -Dcentral.skip=true verify
```

检查代表性附件：

```bash
ls jinfra-core/target/*-sources.jar
ls jinfra-core/target/*-javadoc.jar
ls jinfra-core/target/*.asc
```

## 5. 发布到 Central Portal

确认以下条件满足后再执行：

- namespace `cn.refinex` 已验证。
- `~/.m2/settings.xml` 中存在 server id `central`。
- GPG 私钥和 passphrase 可用。
- 当前版本不是错误的本地临时版本。

执行：

```bash
mvn -P release -DskipTests deploy
```

当前配置 `autoPublish=false`，构件上传后需要在 Central Publisher Portal 中手动检查并发布。

## 6. GitHub Actions Secrets

如果后续要在 GitHub Actions 中发布，建议配置这些 secrets：

- `CENTRAL_TOKEN_USERNAME`：未指定。
- `CENTRAL_TOKEN_PASSWORD`：未指定。
- `MAVEN_GPG_PRIVATE_KEY`：未指定。
- `MAVEN_GPG_PASSPHRASE`：未指定。

CI 当前只做构建校验和 release profile 非签名预演，不做真实发布。

## 7. 常见失败与处理

### 缺少 GPG 签名

现象：`maven-gpg-plugin` 在 `verify` 阶段失败。

处理：

- 确认 `gpg --list-secret-keys` 能看到私钥。
- 确认 passphrase 可用。
- 本地预演可使用 `-Dgpg.skip=true`，但真实发布不能跳过签名。

### Central 认证失败

现象：Central publishing 插件提示 401 或认证失败。

处理：

- 确认 `settings.xml` 的 server id 是 `central`。
- 确认 token username/password 来自 Central Publisher Portal。
- 确认没有把账号密码写反。

### Namespace 未验证

现象：Central Portal 拒绝 `cn.refinex` 坐标。

处理：

- 回到 Portal 完成域名所有权验证。
- 验证完成前不要发布。

## 8. 发布后检查

1. 在 Central Publisher Portal 确认 deployment 状态。
2. 如果状态为 validated，人工发布。
3. 等待同步到 Maven Central 搜索。
4. 新建一个空 Maven 项目，使用 `jinfra-bom` import 和一个子模块依赖验证消费路径。
```

- [ ] **Step 3: Verify the guide includes required operator steps**

Run:

```bash
rg -n "Central Publisher Portal|namespace|settings.xml|gpg --list-secret-keys|mvn -P release -DskipTests deploy|CENTRAL_TOKEN_USERNAME|MAVEN_GPG_PRIVATE_KEY|未指定" docs/release/maven-central-publishing.md
```

Expected:

```text
All listed topics are present.
```

- [ ] **Step 4: Commit publishing guide**

Run:

```bash
git add docs/release/maven-central-publishing.md
git commit -m "docs: 添加Maven Central发布手册"
```

Expected:

```text
Commit succeeds and includes only docs/release/maven-central-publishing.md.
```

### Task 6: Full Verification and Release Readiness Report

**Files:**
- Modify only if verification exposes a real configuration bug: `/Users/refinex/develop/project/jinfra/pom.xml`, `/Users/refinex/develop/project/jinfra/jinfra-bom/pom.xml`, `/Users/refinex/develop/project/jinfra/jinfra-all/pom.xml`, `/Users/refinex/develop/project/jinfra/.github/workflows/ci.yml`, or `/Users/refinex/develop/project/jinfra/docs/release/maven-central-publishing.md`

- [ ] **Step 1: Run normal Maven validation**

Run:

```bash
mvn -DskipTests validate
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 2: Run normal Maven tests**

Run:

```bash
mvn test
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 3: Run normal Maven verify**

Run:

```bash
mvn verify
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 4: Run release profile dry run without signing or publishing**

Run:

```bash
mvn -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 5: Verify release attachments**

Run:

```bash
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-sources.jar
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-javadoc.jar
test -f jinfra-all/target/jinfra-all-0.1.0-SNAPSHOT-sources.jar
test -f jinfra-all/target/jinfra-all-0.1.0-SNAPSHOT-javadoc.jar
```

Expected:

```text
All commands exit 0.
```

- [ ] **Step 6: Verify `jinfra-all` boundary**

Run:

```bash
rg -n "<artifactId>jinfra-(spring|redis-spring|spring-boot|.*spring-boot-starter|test)</artifactId>" jinfra-all/pom.xml
```

Expected:

```text
No matches.
```

- [ ] **Step 7: Verify CI coverage**

Run:

```bash
rg -n "mvn -B -DskipTests validate|mvn -B test|mvn -B verify|mvn -B -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify" .github/workflows/ci.yml
```

Expected:

```text
All four Maven commands are present.
```

- [ ] **Step 8: Check worktree cleanliness**

Run:

```bash
git status --short
```

Expected:

```text
No output.
```

- [ ] **Step 9: Commit verification fixes if any were required**

If any verification step required fixes, run:

```bash
git add pom.xml jinfra-bom/pom.xml jinfra-all/pom.xml .github/workflows/ci.yml docs/release/maven-central-publishing.md
git commit -m "build: 修复发布链路校验"
```

Expected when fixes were required:

```text
Commit succeeds and includes only configuration or documentation corrections.
```

Expected when no fixes were required:

```text
Skip this commit step.
```

## Final Response Requirements

When implementation finishes, the final response must include these sections:

- 发布前置条件
- 变更文件
- 关键配置
- 本地验证命令
- 待运营补充项

Mention that `mvn -P release -DskipTests verify` requires GPG configuration to create `.asc` signatures, while `mvn -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify` is the no-secret dry run.

## Self-Review

Spec coverage:

- BOM version governance is covered by Task 2.
- `jinfra-all` boundary is covered by Task 1 and Task 6.
- Enforcer, Source, Javadoc, GPG, Central publishing profile is covered by Task 3.
- GitHub Actions CI is covered by Task 4.
- First-time Maven Central publishing instructions are covered by Task 5.
- Verification and final output expectations are covered by Task 6 and Final Response Requirements.

Placeholder scan:

- The plan intentionally uses `未指定` only inside user-facing release documentation for credentials and namespace state.
- The plan contains no unfinished implementation markers.

Type and naming consistency:

- Maven artifactIds match the existing module names.
- Properties `gpg.skip` and `central.skip` are used consistently.
- CI commands match the local verification commands.
