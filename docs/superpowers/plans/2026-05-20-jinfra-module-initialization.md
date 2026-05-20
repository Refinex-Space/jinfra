# JInfra Module Initialization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Initialize all 32 README-defined Maven modules so the root project builds with `mvn clean verify`.

**Architecture:** Keep the root POM as the reactor and version governance point, add one child POM per module, and express only the minimum module dependencies needed to keep boundaries clear. `jinfra-bom` exports dependency management, `jinfra-all` aggregates non-Starter capabilities, and Spring Boot starters remain separate from the non-Spring aggregate.

**Tech Stack:** Java 17, Maven 3.9.x, Maven multi-module reactor, Spring Boot 3.5.14 dependency management, Apache POI, Jackson, Lettuce, Redisson, Caffeine, JUnit Jupiter, AssertJ.

---

## File Structure

Modify:

- `/Users/refinex/develop/project/jinfra/pom.xml` — keep root metadata, protect development POM from flatten rewrite during normal builds, and keep plugin governance centralized.

Create:

- `/Users/refinex/develop/project/jinfra/jinfra-bom/pom.xml` — BOM module with `packaging=pom`.
- `/Users/refinex/develop/project/jinfra/jinfra-all/pom.xml` — non-Spring-Boot-Starter aggregate module.
- `/Users/refinex/develop/project/jinfra/jinfra-core/pom.xml` — core lightweight base module.
- `/Users/refinex/develop/project/jinfra/jinfra-context/pom.xml` — context propagation module.
- `/Users/refinex/develop/project/jinfra/jinfra-id/pom.xml` — ID generation module.
- `/Users/refinex/develop/project/jinfra/jinfra-io/pom.xml` — IO helper module.
- `/Users/refinex/develop/project/jinfra/jinfra-codec/pom.xml` — codec helper module.
- `/Users/refinex/develop/project/jinfra/jinfra-crypto/pom.xml` — crypto helper module.
- `/Users/refinex/develop/project/jinfra/jinfra-json/pom.xml` — Jackson JSON module.
- `/Users/refinex/develop/project/jinfra/jinfra-http/pom.xml` — JDK HttpClient wrapper module.
- `/Users/refinex/develop/project/jinfra/jinfra-validation/pom.xml` — Jakarta Validation helper module.
- `/Users/refinex/develop/project/jinfra/jinfra-office/pom.xml` — Office common module.
- `/Users/refinex/develop/project/jinfra/jinfra-excel/pom.xml` — Excel module.
- `/Users/refinex/develop/project/jinfra/jinfra-word/pom.xml` — Word module.
- `/Users/refinex/develop/project/jinfra/jinfra-ppt/pom.xml` — PowerPoint module.
- `/Users/refinex/develop/project/jinfra/jinfra-redis/pom.xml` — Redis abstraction module.
- `/Users/refinex/develop/project/jinfra/jinfra-redis-lettuce/pom.xml` — Lettuce Redis implementation module.
- `/Users/refinex/develop/project/jinfra/jinfra-redis-spring/pom.xml` — Spring Data Redis adapter module.
- `/Users/refinex/develop/project/jinfra/jinfra-lock/pom.xml` — lock abstraction module.
- `/Users/refinex/develop/project/jinfra/jinfra-lock-redis/pom.xml` — Redis lock implementation module.
- `/Users/refinex/develop/project/jinfra/jinfra-lock-redisson/pom.xml` — Redisson lock adapter module.
- `/Users/refinex/develop/project/jinfra/jinfra-mq/pom.xml` — MQ abstraction module.
- `/Users/refinex/develop/project/jinfra/jinfra-mq-redis/pom.xml` — Redis Streams MQ implementation module.
- `/Users/refinex/develop/project/jinfra/jinfra-cache/pom.xml` — cache abstraction module.
- `/Users/refinex/develop/project/jinfra/jinfra-cache-caffeine/pom.xml` — Caffeine cache implementation module.
- `/Users/refinex/develop/project/jinfra/jinfra-cache-redis/pom.xml` — Redis cache implementation module.
- `/Users/refinex/develop/project/jinfra/jinfra-spring/pom.xml` — Spring common helper module.
- `/Users/refinex/develop/project/jinfra/jinfra-spring-boot-autoconfigure/pom.xml` — Spring Boot autoconfigure module.
- `/Users/refinex/develop/project/jinfra/jinfra-spring-boot-starter/pom.xml` — common starter dependency module.
- `/Users/refinex/develop/project/jinfra/jinfra-redis-spring-boot-starter/pom.xml` — Redis/Lock/MQ starter dependency module.
- `/Users/refinex/develop/project/jinfra/jinfra-office-spring-boot-starter/pom.xml` — Office starter dependency module.
- `/Users/refinex/develop/project/jinfra/jinfra-test/pom.xml` — test helper module.

Create local source directories for each non-BOM module:

- `src/main/java`
- `src/test/java`

Do not add business Java classes in this plan.

## Shared Child POM Rules

Every child POM uses this parent block:

```xml
<parent>
    <groupId>cn.refinex</groupId>
    <artifactId>jinfra</artifactId>
    <version>${revision}</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

Every child POM declares its own `artifactId`, `name`, and `description`.

Every jar child POM declares a sanitized automatic module name:

```xml
<properties>
    <automatic.module.name>cn.refinex.jinfra.core</automatic.module.name>
</properties>
```

Use the artifact-specific value from the dependency matrix below. Do not use hyphens in `automatic.module.name`.

## Dependency Matrix

Use these dependencies exactly. Omit the `<version>` element for all dependencies because the root POM manages versions.

| Module | Dependencies |
|---|---|
| `jinfra-bom` | `spring-boot-dependencies`, `jackson-bom`, `junit-bom`, all 31 JInfra non-BOM modules, `slf4j-api`, `commons-lang3`, `commons-io`, `commons-codec`, `poi-ooxml`, `lettuce-core`, `redisson`, `caffeine`, `jakarta.validation-api`, `hibernate-validator`, `assertj-core` |
| `jinfra-all` | `jinfra-core`, `jinfra-context`, `jinfra-id`, `jinfra-io`, `jinfra-codec`, `jinfra-crypto`, `jinfra-json`, `jinfra-http`, `jinfra-validation`, `jinfra-office`, `jinfra-excel`, `jinfra-word`, `jinfra-ppt`, `jinfra-redis`, `jinfra-redis-lettuce`, `jinfra-redis-spring`, `jinfra-lock`, `jinfra-lock-redis`, `jinfra-lock-redisson`, `jinfra-mq`, `jinfra-mq-redis`, `jinfra-cache`, `jinfra-cache-caffeine`, `jinfra-cache-redis`, `jinfra-spring`, `jinfra-test` |
| `jinfra-core` | `slf4j-api`, `commons-lang3` |
| `jinfra-context` | `jinfra-core`, `slf4j-api` |
| `jinfra-id` | `jinfra-core` |
| `jinfra-io` | `jinfra-core`, `commons-io` |
| `jinfra-codec` | `jinfra-core`, `commons-codec` |
| `jinfra-crypto` | `jinfra-core`, `commons-codec` |
| `jinfra-json` | `jinfra-core`, `com.fasterxml.jackson.core:jackson-databind` |
| `jinfra-http` | `jinfra-core`, `jinfra-json` |
| `jinfra-validation` | `jinfra-core`, `jakarta.validation-api`, `hibernate-validator` |
| `jinfra-office` | `jinfra-core`, `jinfra-io`, `poi-ooxml` |
| `jinfra-excel` | `jinfra-office` |
| `jinfra-word` | `jinfra-office` |
| `jinfra-ppt` | `jinfra-office` |
| `jinfra-redis` | `jinfra-core`, `jinfra-json` |
| `jinfra-redis-lettuce` | `jinfra-redis`, `lettuce-core` |
| `jinfra-redis-spring` | `jinfra-redis`, `jinfra-spring`, `org.springframework.data:spring-data-redis` |
| `jinfra-lock` | `jinfra-core` |
| `jinfra-lock-redis` | `jinfra-lock`, `jinfra-redis` |
| `jinfra-lock-redisson` | `jinfra-lock`, `redisson` |
| `jinfra-mq` | `jinfra-core` |
| `jinfra-mq-redis` | `jinfra-mq`, `jinfra-redis` |
| `jinfra-cache` | `jinfra-core` |
| `jinfra-cache-caffeine` | `jinfra-cache`, `caffeine` |
| `jinfra-cache-redis` | `jinfra-cache`, `jinfra-redis` |
| `jinfra-spring` | `jinfra-core`, `org.springframework:spring-context` |
| `jinfra-spring-boot-autoconfigure` | `jinfra-core`, `jinfra-spring`, `jinfra-redis-spring`, `jinfra-office`, `jinfra-lock`, `jinfra-mq`, `jinfra-cache`, `org.springframework.boot:spring-boot-autoconfigure` |
| `jinfra-spring-boot-starter` | `jinfra-spring-boot-autoconfigure`, `org.springframework.boot:spring-boot-starter` |
| `jinfra-redis-spring-boot-starter` | `jinfra-spring-boot-autoconfigure`, `jinfra-redis-spring`, `jinfra-lock-redis`, `jinfra-mq-redis`, `org.springframework.boot:spring-boot-starter`, `org.springframework.boot:spring-boot-starter-data-redis` |
| `jinfra-office-spring-boot-starter` | `jinfra-spring-boot-autoconfigure`, `jinfra-excel`, `jinfra-word`, `jinfra-ppt`, `org.springframework.boot:spring-boot-starter` |
| `jinfra-test` | `jinfra-core`, `junit-jupiter`, `assertj-core` |

Automatic module names:

| Artifact | Automatic module name |
|---|---|
| `jinfra-all` | `cn.refinex.jinfra.all` |
| `jinfra-core` | `cn.refinex.jinfra.core` |
| `jinfra-context` | `cn.refinex.jinfra.context` |
| `jinfra-id` | `cn.refinex.jinfra.id` |
| `jinfra-io` | `cn.refinex.jinfra.io` |
| `jinfra-codec` | `cn.refinex.jinfra.codec` |
| `jinfra-crypto` | `cn.refinex.jinfra.crypto` |
| `jinfra-json` | `cn.refinex.jinfra.json` |
| `jinfra-http` | `cn.refinex.jinfra.http` |
| `jinfra-validation` | `cn.refinex.jinfra.validation` |
| `jinfra-office` | `cn.refinex.jinfra.office` |
| `jinfra-excel` | `cn.refinex.jinfra.excel` |
| `jinfra-word` | `cn.refinex.jinfra.word` |
| `jinfra-ppt` | `cn.refinex.jinfra.ppt` |
| `jinfra-redis` | `cn.refinex.jinfra.redis` |
| `jinfra-redis-lettuce` | `cn.refinex.jinfra.redis.lettuce` |
| `jinfra-redis-spring` | `cn.refinex.jinfra.redis.spring` |
| `jinfra-lock` | `cn.refinex.jinfra.lock` |
| `jinfra-lock-redis` | `cn.refinex.jinfra.lock.redis` |
| `jinfra-lock-redisson` | `cn.refinex.jinfra.lock.redisson` |
| `jinfra-mq` | `cn.refinex.jinfra.mq` |
| `jinfra-mq-redis` | `cn.refinex.jinfra.mq.redis` |
| `jinfra-cache` | `cn.refinex.jinfra.cache` |
| `jinfra-cache-caffeine` | `cn.refinex.jinfra.cache.caffeine` |
| `jinfra-cache-redis` | `cn.refinex.jinfra.cache.redis` |
| `jinfra-spring` | `cn.refinex.jinfra.spring` |
| `jinfra-spring-boot-autoconfigure` | `cn.refinex.jinfra.spring.boot.autoconfigure` |
| `jinfra-spring-boot-starter` | `cn.refinex.jinfra.spring.boot.starter` |
| `jinfra-redis-spring-boot-starter` | `cn.refinex.jinfra.redis.spring.boot.starter` |
| `jinfra-office-spring-boot-starter` | `cn.refinex.jinfra.office.spring.boot.starter` |
| `jinfra-test` | `cn.refinex.jinfra.test` |

### Task 1: Protect Normal Builds From Flatten Rewrites

**Files:**
- Modify: `/Users/refinex/develop/project/jinfra/pom.xml`

- [ ] **Step 1: Inspect current root POM plugin placement**

Run:

```bash
rg -n "flatten-maven-plugin|updatePomFile|Automatic-Module-Name" pom.xml
```

Expected:

```text
flatten-maven-plugin appears in pluginManagement and root build plugins.
updatePomFile appears in the root build plugin configuration.
Automatic-Module-Name uses ${project.groupId}.${project.artifactId}.
```

- [ ] **Step 2: Update root manifest module-name property**

Modify `/Users/refinex/develop/project/jinfra/pom.xml` so the root `<properties>` contains:

```xml
<automatic.module.name>${project.groupId}.${project.artifactId}</automatic.module.name>
```

Modify the `maven-jar-plugin` manifest entry to:

```xml
<Automatic-Module-Name>${automatic.module.name}</Automatic-Module-Name>
```

- [ ] **Step 3: Move flatten execution out of the default build**

Remove this plugin from the root default `<build><plugins>` section:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <configuration>
        <flattenMode>ossrh</flattenMode>
        <updatePomFile>true</updatePomFile>
    </configuration>
    <executions>
        <execution>
            <id>flatten</id>
            <phase>process-resources</phase>
            <goals>
                <goal>flatten</goal>
            </goals>
        </execution>
        <execution>
            <id>flatten-clean</id>
            <phase>clean</phase>
            <goals>
                <goal>clean</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Add this plugin inside the existing `release` profile `<build><plugins>`:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <configuration>
        <flattenMode>ossrh</flattenMode>
        <updatePomFile>true</updatePomFile>
    </configuration>
    <executions>
        <execution>
            <id>flatten</id>
            <phase>process-resources</phase>
            <goals>
                <goal>flatten</goal>
            </goals>
        </execution>
        <execution>
            <id>flatten-clean</id>
            <phase>clean</phase>
            <goals>
                <goal>clean</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

- [ ] **Step 4: Verify root-only build does not flatten the development POM**

Run:

```bash
mvn -N -DskipTests process-resources
```

Expected:

```text
BUILD SUCCESS
```

Run:

```bash
test ! -f .flattened-pom.xml && git diff -- pom.xml
```

Expected:

```text
git diff shows only the intentional edits from Steps 2 and 3.
```

- [ ] **Step 5: Commit root build-safety change**

Run:

```bash
git add pom.xml
git commit -m "build: 调整普通构建的flatten配置"
```

Expected:

```text
Commit succeeds and includes only pom.xml.
```

### Task 2: Create BOM and Aggregate Modules

**Files:**
- Create: `/Users/refinex/develop/project/jinfra/jinfra-bom/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-all/pom.xml`

- [ ] **Step 1: Create module directories**

Run:

```bash
mkdir -p jinfra-bom jinfra-all/src/main/java jinfra-all/src/test/java
```

Expected:

```text
Directories exist.
```

- [ ] **Step 2: Create `jinfra-bom/pom.xml`**

Use `apply_patch` to create `/Users/refinex/develop/project/jinfra/jinfra-bom/pom.xml` with:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>cn.refinex</groupId>
        <artifactId>jinfra</artifactId>
        <version>${revision}</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>jinfra-bom</artifactId>
    <packaging>pom</packaging>
    <name>JInfra BOM</name>
    <description>Bill of materials for JInfra modules and managed third-party dependencies.</description>

    <dependencyManagement>
        <dependencies>
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
        </dependencies>
    </dependencyManagement>
</project>
```

- [ ] **Step 3: Extend BOM dependency management with exact managed artifacts**

Inside `jinfra-bom/pom.xml` after the three imported BOM dependencies, add a managed dependency entry for each of these JInfra artifacts with `<groupId>${project.groupId}</groupId>` and `<version>${project.version}</version>`:

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

Also add managed third-party dependencies with their existing root version properties:

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

- [ ] **Step 4: Create `jinfra-all/pom.xml`**

Use `apply_patch` to create `/Users/refinex/develop/project/jinfra/jinfra-all/pom.xml` with a normal jar module, automatic module name `cn.refinex.jinfra.all`, and dependencies listed for `jinfra-all` in the dependency matrix. The file must not include any dependency whose artifactId ends with `spring-boot-starter`.

- [ ] **Step 5: Verify aggregate excludes Spring Boot Starter modules**

Run:

```bash
rg -n "spring-boot-starter" jinfra-all/pom.xml
```

Expected:

```text
No matches.
```

- [ ] **Step 6: Commit BOM and aggregate modules**

Run:

```bash
git add jinfra-bom/pom.xml jinfra-all/pom.xml
git commit -m "build: 初始化BOM和聚合模块"
```

Expected:

```text
Commit succeeds and includes the two new module POMs.
```

### Task 3: Create Foundation Module POMs

**Files:**
- Create: `/Users/refinex/develop/project/jinfra/jinfra-core/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-context/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-id/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-io/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-codec/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-crypto/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-json/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-http/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-validation/pom.xml`

- [ ] **Step 1: Create directories**

Run:

```bash
mkdir -p \
  jinfra-core/src/main/java jinfra-core/src/test/java \
  jinfra-context/src/main/java jinfra-context/src/test/java \
  jinfra-id/src/main/java jinfra-id/src/test/java \
  jinfra-io/src/main/java jinfra-io/src/test/java \
  jinfra-codec/src/main/java jinfra-codec/src/test/java \
  jinfra-crypto/src/main/java jinfra-crypto/src/test/java \
  jinfra-json/src/main/java jinfra-json/src/test/java \
  jinfra-http/src/main/java jinfra-http/src/test/java \
  jinfra-validation/src/main/java jinfra-validation/src/test/java
```

Expected:

```text
Directories exist.
```

- [ ] **Step 2: Create each module POM from the shared child POM rules**

For each foundation module, create a POM that contains the parent block, artifactId, name, description, sanitized `automatic.module.name`, and dependencies from the dependency matrix.

Use these artifact-specific descriptions:

```text
jinfra-core: Core exceptions, assertions, object helpers, string helpers, collection helpers, result models, and base utilities.
jinfra-context: Trace, tenant, user, MDC, and thread context propagation helpers.
jinfra-id: UUID, ULID, and Snowflake-style ID generation utilities.
jinfra-io: File, stream, temporary file, compression, and path utilities.
jinfra-codec: Base64, Hex, and URL encoding utilities.
jinfra-crypto: Digest, HMAC, AES, RSA, and password hashing utilities.
jinfra-json: Jackson ObjectMapper factory and JSON helper utilities.
jinfra-http: Lightweight HTTP client utilities based on JDK HttpClient.
jinfra-validation: Jakarta Validation helper utilities.
```

- [ ] **Step 3: Verify no foundation module depends on Spring**

Run:

```bash
rg -n "org\\.springframework|spring-boot|spring-data" \
  jinfra-core jinfra-context jinfra-id jinfra-io jinfra-codec \
  jinfra-crypto jinfra-json jinfra-http jinfra-validation
```

Expected:

```text
No matches.
```

- [ ] **Step 4: Commit foundation modules**

Run:

```bash
git add \
  jinfra-core/pom.xml jinfra-context/pom.xml jinfra-id/pom.xml \
  jinfra-io/pom.xml jinfra-codec/pom.xml jinfra-crypto/pom.xml \
  jinfra-json/pom.xml jinfra-http/pom.xml jinfra-validation/pom.xml
git commit -m "build: 初始化基础模块"
```

Expected:

```text
Commit succeeds and includes nine new module POMs.
```

### Task 4: Create Office, Redis, Lock, MQ, and Cache Module POMs

**Files:**
- Create: `/Users/refinex/develop/project/jinfra/jinfra-office/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-excel/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-word/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-ppt/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-redis/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-redis-lettuce/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-redis-spring/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-lock/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-lock-redis/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-lock-redisson/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-mq/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-mq-redis/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-cache/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-cache-caffeine/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-cache-redis/pom.xml`

- [ ] **Step 1: Create directories**

Run:

```bash
mkdir -p \
  jinfra-office/src/main/java jinfra-office/src/test/java \
  jinfra-excel/src/main/java jinfra-excel/src/test/java \
  jinfra-word/src/main/java jinfra-word/src/test/java \
  jinfra-ppt/src/main/java jinfra-ppt/src/test/java \
  jinfra-redis/src/main/java jinfra-redis/src/test/java \
  jinfra-redis-lettuce/src/main/java jinfra-redis-lettuce/src/test/java \
  jinfra-redis-spring/src/main/java jinfra-redis-spring/src/test/java \
  jinfra-lock/src/main/java jinfra-lock/src/test/java \
  jinfra-lock-redis/src/main/java jinfra-lock-redis/src/test/java \
  jinfra-lock-redisson/src/main/java jinfra-lock-redisson/src/test/java \
  jinfra-mq/src/main/java jinfra-mq/src/test/java \
  jinfra-mq-redis/src/main/java jinfra-mq-redis/src/test/java \
  jinfra-cache/src/main/java jinfra-cache/src/test/java \
  jinfra-cache-caffeine/src/main/java jinfra-cache-caffeine/src/test/java \
  jinfra-cache-redis/src/main/java jinfra-cache-redis/src/test/java
```

Expected:

```text
Directories exist.
```

- [ ] **Step 2: Create each module POM from the dependency matrix**

For each module in this task, create a POM that contains the parent block, artifactId, name, description, sanitized `automatic.module.name`, and dependencies from the dependency matrix.

Use these artifact-specific descriptions:

```text
jinfra-office: Common Office abstractions, template models, and OOXML document handling utilities.
jinfra-excel: Excel reading, writing, template filling, streaming import/export, and style helpers.
jinfra-word: Word template filling, paragraph, table, and variable marker replacement utilities.
jinfra-ppt: PowerPoint template filling, text box, shape, and image replacement utilities.
jinfra-redis: Redis abstractions, serialization, key conventions, and Lua script execution contracts.
jinfra-redis-lettuce: Standalone Redis client implementation based on Lettuce.
jinfra-redis-spring: Spring Data Redis adapter layer.
jinfra-lock: Distributed lock SPI, lock context, lock exceptions, and lock execution template.
jinfra-lock-redis: Redis Lua based distributed lock implementation module.
jinfra-lock-redisson: Optional Redisson adapter implementation module.
jinfra-mq: Message queue SPI, message model, retry, and acknowledgement abstractions.
jinfra-mq-redis: Redis Streams based lightweight reliable message queue module.
jinfra-cache: Cache SPI, cache manager, TTL, null-value caching, and stampede protection abstractions.
jinfra-cache-caffeine: Local cache implementation based on Caffeine.
jinfra-cache-redis: Distributed cache implementation based on Redis.
```

- [ ] **Step 3: Verify implementation modules depend on their abstraction modules**

Run:

```bash
rg -n "<artifactId>jinfra-(office|redis|lock|mq|cache)</artifactId>" \
  jinfra-excel jinfra-word jinfra-ppt \
  jinfra-redis-lettuce jinfra-redis-spring \
  jinfra-lock-redis jinfra-lock-redisson \
  jinfra-mq-redis jinfra-cache-caffeine jinfra-cache-redis
```

Expected:

```text
Each implementation module shows its corresponding abstraction dependency.
```

- [ ] **Step 4: Commit Office, Redis, Lock, MQ, and Cache modules**

Run:

```bash
git add \
  jinfra-office/pom.xml jinfra-excel/pom.xml jinfra-word/pom.xml jinfra-ppt/pom.xml \
  jinfra-redis/pom.xml jinfra-redis-lettuce/pom.xml jinfra-redis-spring/pom.xml \
  jinfra-lock/pom.xml jinfra-lock-redis/pom.xml jinfra-lock-redisson/pom.xml \
  jinfra-mq/pom.xml jinfra-mq-redis/pom.xml \
  jinfra-cache/pom.xml jinfra-cache-caffeine/pom.xml jinfra-cache-redis/pom.xml
git commit -m "build: 初始化Office与基础设施实现模块"
```

Expected:

```text
Commit succeeds and includes fifteen new module POMs.
```

### Task 5: Create Spring and Test Module POMs

**Files:**
- Create: `/Users/refinex/develop/project/jinfra/jinfra-spring/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-spring-boot-autoconfigure/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-spring-boot-starter/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-redis-spring-boot-starter/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-office-spring-boot-starter/pom.xml`
- Create: `/Users/refinex/develop/project/jinfra/jinfra-test/pom.xml`

- [ ] **Step 1: Create directories**

Run:

```bash
mkdir -p \
  jinfra-spring/src/main/java jinfra-spring/src/test/java \
  jinfra-spring-boot-autoconfigure/src/main/java jinfra-spring-boot-autoconfigure/src/test/java \
  jinfra-spring-boot-starter/src/main/java jinfra-spring-boot-starter/src/test/java \
  jinfra-redis-spring-boot-starter/src/main/java jinfra-redis-spring-boot-starter/src/test/java \
  jinfra-office-spring-boot-starter/src/main/java jinfra-office-spring-boot-starter/src/test/java \
  jinfra-test/src/main/java jinfra-test/src/test/java
```

Expected:

```text
Directories exist.
```

- [ ] **Step 2: Create Spring and test POMs from the dependency matrix**

For each module in this task, create a POM that contains the parent block, artifactId, name, description, sanitized `automatic.module.name`, and dependencies from the dependency matrix.

Use these artifact-specific descriptions:

```text
jinfra-spring: Spring common helpers, bean utilities, and environment utilities.
jinfra-spring-boot-autoconfigure: Core JInfra Spring Boot autoconfiguration module.
jinfra-spring-boot-starter: Common JInfra Spring Boot starter.
jinfra-redis-spring-boot-starter: Redis, Lock, and MQ Spring Boot starter.
jinfra-office-spring-boot-starter: Office Spring Boot starter.
jinfra-test: Test base classes, random data helpers, temporary file helpers, assertion helpers, and test container extension points.
```

- [ ] **Step 3: Verify starter modules are dependency-only modules**

Run:

```bash
find jinfra-spring-boot-starter jinfra-redis-spring-boot-starter jinfra-office-spring-boot-starter \
  -path '*/src/main/java/*' -type f
```

Expected:

```text
No Java files are printed.
```

- [ ] **Step 4: Commit Spring and test modules**

Run:

```bash
git add \
  jinfra-spring/pom.xml jinfra-spring-boot-autoconfigure/pom.xml \
  jinfra-spring-boot-starter/pom.xml jinfra-redis-spring-boot-starter/pom.xml \
  jinfra-office-spring-boot-starter/pom.xml jinfra-test/pom.xml
git commit -m "build: 初始化Spring与测试模块"
```

Expected:

```text
Commit succeeds and includes six new module POMs.
```

### Task 6: Validate Reactor Model and Dependency Boundaries

**Files:**
- Modify only if verification exposes a Maven model or dependency boundary error: `/Users/refinex/develop/project/jinfra/pom.xml`
- Modify only if verification exposes a module POM error: the failing module's `pom.xml`

- [ ] **Step 1: Verify root module count equals actual child POM count**

Run:

```bash
root_modules=$(sed -n '/<modules>/,/<\\/modules>/p' pom.xml | rg '<module>' | wc -l | tr -d ' ')
child_poms=$(find . -mindepth 2 -maxdepth 2 -name pom.xml | wc -l | tr -d ' ')
printf 'root_modules=%s child_poms=%s\n' "$root_modules" "$child_poms"
```

Expected:

```text
root_modules=32 child_poms=32
```

- [ ] **Step 2: Verify Maven validates the full reactor**

Run:

```bash
mvn -DskipTests validate
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 3: Verify `jinfra-all` still excludes Starter modules**

Run:

```bash
rg -n "spring-boot-starter" jinfra-all/pom.xml
```

Expected:

```text
No matches.
```

- [ ] **Step 4: Commit verification fixes if any were required**

If Step 2 required fixes, run:

```bash
git add pom.xml */pom.xml
git commit -m "build: 修复模块依赖边界"
```

Expected when fixes were required:

```text
Commit succeeds and includes only Maven POM corrections.
```

Expected when no fixes were required:

```text
Skip this commit step.
```

### Task 7: Run Full Build Verification

**Files:**
- No planned file modifications.

- [ ] **Step 1: Run the required build**

Run:

```bash
mvn clean verify
```

Expected:

```text
BUILD SUCCESS
```

- [ ] **Step 2: Verify no flatten or build pollution remains**

Run:

```bash
test ! -f .flattened-pom.xml
git status --short
```

Expected:

```text
No .flattened-pom.xml file exists.
git status shows only intentional committed or uncommitted user changes.
```

- [ ] **Step 3: Capture final module inventory**

Run:

```bash
find . -mindepth 2 -maxdepth 2 -name pom.xml | sort
```

Expected:

```text
The output lists exactly 32 child POM files under README-defined module directories.
```

- [ ] **Step 4: Commit final verification cleanup if needed**

If Step 2 reveals generated files that should be ignored, add the smallest `.gitignore` update and commit:

```bash
git add .gitignore
git commit -m "chore: 忽略Maven构建副产物"
```

Expected when cleanup was required:

```text
Commit succeeds and includes only .gitignore.
```

Expected when cleanup was not required:

```text
Skip this commit step.
```

## Self-Review

Spec coverage:

- Full 32-module README scope is covered by Tasks 2 through 5.
- Root reactor and plugin safety are covered by Task 1.
- BOM import behavior is covered by Task 2.
- `jinfra-all` excluding Spring Boot starters is covered by Tasks 2 and 6.
- Full build verification is covered by Task 7.

Placeholder scan:

- The plan contains no unfinished markers or unspecified implementation steps.
- Each task has exact files, commands, and expected results.

Type and naming consistency:

- Maven artifactIds match README and the root `<modules>` list.
- Automatic module names use dot-separated Java-compatible names.
- Commit messages follow standard Git convention and are written in Chinese.
