# Context ID IO Codec Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 `jinfra-context`、`jinfra-id`、`jinfra-io`、`jinfra-codec` 实现首版纯 Java 基础能力。

**Architecture:** 四个模块均依赖 `jinfra-core`，彼此不互相依赖。`context` 采用不可变上下文 + ThreadLocal holder + 显式快照传播；`id` 提供 UUID/ULID 默认实现和显式配置 Snowflake；`io` 与 `codec` 提供静态工具类并统一包装异常。

**Tech Stack:** Java 17、Maven、JUnit Jupiter、AssertJ、SLF4J MDC、Apache Commons IO、Apache Commons Codec。

---

## Scope Check

设计文档覆盖四个相关但可独立验证的基础模块。它们共用 `jinfra-core`，但实现互不依赖，因此本计划按模块拆成独立任务，每个任务都能单独 `mvn -pl <module> -am test` 验证。

## File Structure

### Shared POM Changes

- Modify: `jinfra-context/pom.xml`
- Modify: `jinfra-id/pom.xml`
- Modify: `jinfra-io/pom.xml`
- Modify: `jinfra-codec/pom.xml`

每个模块增加 `junit-jupiter` 和 `assertj-core` 测试依赖，不声明版本。

### `jinfra-context`

- Create: `jinfra-context/src/main/java/cn/refinex/jinfra/context/ContextException.java`
- Create: `jinfra-context/src/main/java/cn/refinex/jinfra/context/ContextKey.java`
- Create: `jinfra-context/src/main/java/cn/refinex/jinfra/context/JInfraContext.java`
- Create: `jinfra-context/src/main/java/cn/refinex/jinfra/context/ContextSnapshot.java`
- Create: `jinfra-context/src/main/java/cn/refinex/jinfra/context/JInfraContextHolder.java`
- Create: `jinfra-context/src/main/java/cn/refinex/jinfra/context/ContextScope.java`
- Create: `jinfra-context/src/main/java/cn/refinex/jinfra/context/ContextPropagation.java`
- Create: `jinfra-context/src/main/java/cn/refinex/jinfra/context/mdc/MdcContextBridge.java`
- Test: `jinfra-context/src/test/java/cn/refinex/jinfra/context/*Test.java`
- Test: `jinfra-context/src/test/java/cn/refinex/jinfra/context/mdc/MdcContextBridgeTest.java`

### `jinfra-id`

- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/IdGenerationException.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/IdGenerator.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/IdGenerators.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/IdType.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/uuid/UuidGenerator.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/ulid/Ulid.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/ulid/UlidGenerator.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/snowflake/ClockBackwardsStrategy.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/snowflake/SnowflakeConfig.java`
- Create: `jinfra-id/src/main/java/cn/refinex/jinfra/id/snowflake/SnowflakeIdGenerator.java`
- Test: `jinfra-id/src/test/java/cn/refinex/jinfra/id/**/*Test.java`

### `jinfra-io`

- Create: `jinfra-io/src/main/java/cn/refinex/jinfra/io/IoException.java`
- Create: `jinfra-io/src/main/java/cn/refinex/jinfra/io/PathUtils.java`
- Create: `jinfra-io/src/main/java/cn/refinex/jinfra/io/FileUtils.java`
- Create: `jinfra-io/src/main/java/cn/refinex/jinfra/io/StreamUtils.java`
- Create: `jinfra-io/src/main/java/cn/refinex/jinfra/io/TempFileUtils.java`
- Create: `jinfra-io/src/main/java/cn/refinex/jinfra/io/ZipUtils.java`
- Test: `jinfra-io/src/test/java/cn/refinex/jinfra/io/*Test.java`

### `jinfra-codec`

- Create: `jinfra-codec/src/main/java/cn/refinex/jinfra/codec/CodecException.java`
- Create: `jinfra-codec/src/main/java/cn/refinex/jinfra/codec/Base64Utils.java`
- Create: `jinfra-codec/src/main/java/cn/refinex/jinfra/codec/HexUtils.java`
- Create: `jinfra-codec/src/main/java/cn/refinex/jinfra/codec/UrlCodecUtils.java`
- Test: `jinfra-codec/src/test/java/cn/refinex/jinfra/codec/*Test.java`

## Implementation Rules

- 所有公开类、接口、枚举、record 和公开方法写中文 Javadoc。
- 所有工具类为 `final`，私有构造器抛 `AssertionError`。
- 四个模块禁止新增 Spring / Spring Boot / Reactor / Netty / Jackson / Redis / HTTP 依赖。
- 所有 JDK checked IO 异常包装为模块异常。
- 参数校验优先复用 `AssertUtils` 和 `CoreException`。
- Snowflake 不提供默认位宽、默认 epoch 或默认 workerId。
- 每个任务完成后提交一次，提交信息使用中文。

## Public API Contracts

实现时必须保持以下公开签名稳定：

```java
// jinfra-context
public record ContextKey<T>(String name) {}
public record ContextSnapshot(JInfraContext context) {}
public final class JInfraContext {
    public static JInfraContext empty();
    public static JInfraContext of(String key, String value);
    public String get(ContextKey<String> key);
    public JInfraContext put(ContextKey<String> key, String value);
    public JInfraContext remove(ContextKey<String> key);
    public Map<String, String> asMap();
}
public final class JInfraContextHolder {
    public static JInfraContext current();
    public static void set(JInfraContext context);
    public static void clear();
    public static ContextSnapshot capture();
    public static ContextScope open(ContextSnapshot snapshot);
}
public final class ContextPropagation {
    public static Runnable wrap(Runnable runnable);
    public static <T> Callable<T> wrap(Callable<T> callable);
}
```

```java
// jinfra-id
public interface IdGenerator<T> {
    T nextId();
}
public final class IdGenerators {
    public static IdGenerator<String> uuid();
    public static IdGenerator<String> uuidWithHyphen();
    public static IdGenerator<String> ulid();
    public static IdGenerator<String> ulid(Clock clock, SecureRandom random);
    public static IdGenerator<Long> snowflake(SnowflakeConfig config, Clock clock);
}
public record SnowflakeConfig(
        long epochMillis,
        int timestampBits,
        int workerIdBits,
        int sequenceBits,
        long workerId,
        ClockBackwardsStrategy clockBackwardsStrategy) {}
```

```java
// jinfra-io
public final class PathUtils {
    public static Path normalize(Path path);
    public static Path ensureInside(Path baseDir, Path target);
    public static String extension(Path path);
    public static String fileName(Path path);
}
```

```java
// jinfra-codec
public final class Base64Utils {
    public static String encode(byte[] bytes);
    public static byte[] decode(String text);
    public static String encodeUrlSafe(byte[] bytes);
    public static byte[] decodeUrlSafe(String text);
}
```

### Task 1: Add Test Dependencies

**Files:**
- Modify: `jinfra-context/pom.xml`
- Modify: `jinfra-id/pom.xml`
- Modify: `jinfra-io/pom.xml`
- Modify: `jinfra-codec/pom.xml`

- [ ] **Step 1: Add JUnit and AssertJ to each module**

Apply the same dependency block before each module's closing `</dependencies>`:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 2: Verify dependency resolution**

Run:

```bash
mvn -pl jinfra-context,jinfra-id,jinfra-io,jinfra-codec -am test -DskipTests
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 3: Commit**

```bash
git add jinfra-context/pom.xml jinfra-id/pom.xml jinfra-io/pom.xml jinfra-codec/pom.xml
git commit -m "test: 添加基础四模块测试依赖"
```

### Task 2: Implement `jinfra-context`

**Files:**
- Create source files listed in `jinfra-context` file structure.
- Create tests:
  - `jinfra-context/src/test/java/cn/refinex/jinfra/context/JInfraContextTest.java`
  - `jinfra-context/src/test/java/cn/refinex/jinfra/context/JInfraContextHolderTest.java`
  - `jinfra-context/src/test/java/cn/refinex/jinfra/context/ContextPropagationTest.java`
  - `jinfra-context/src/test/java/cn/refinex/jinfra/context/mdc/MdcContextBridgeTest.java`

- [ ] **Step 1: Write failing tests**

Create `JInfraContextTest` with these tests:

```java
package cn.refinex.jinfra.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.core.exception.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JInfraContextTest {

    @Test
    @DisplayName("上下文应保持不可变")
    void shouldBeImmutable() {
        JInfraContext empty = JInfraContext.empty();
        JInfraContext updated = empty.put(ContextKey.TRACE_ID, "trace-1");

        assertThat(empty.get(ContextKey.TRACE_ID)).isNull();
        assertThat(updated.get(ContextKey.TRACE_ID)).isEqualTo("trace-1");
        assertThat(updated.asMap()).containsEntry("traceId", "trace-1");
    }

    @Test
    @DisplayName("删除 key 应返回新的上下文")
    void shouldRemoveKey() {
        JInfraContext context = JInfraContext.empty()
                .put(ContextKey.TENANT_ID, "tenant-a")
                .remove(ContextKey.TENANT_ID);

        assertThat(context.get(ContextKey.TENANT_ID)).isNull();
    }

    @Test
    @DisplayName("上下文 map 不允许外部修改")
    void shouldExposeUnmodifiableMap() {
        JInfraContext context = JInfraContext.of("traceId", "trace-1");

        assertThatThrownBy(() -> context.asMap().put("x", "y"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("blank key 应被拒绝")
    void shouldRejectBlankKey() {
        assertThatThrownBy(() -> new ContextKey<String>(" "))
                .isInstanceOf(CoreException.class);
    }
}
```

Create `JInfraContextHolderTest` with these tests:

```java
package cn.refinex.jinfra.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JInfraContextHolderTest {

    @AfterEach
    void clearContext() {
        JInfraContextHolder.clear();
    }

    @Test
    @DisplayName("当前线程上下文应支持 set/current/clear")
    void shouldSetCurrentAndClear() {
        JInfraContextHolder.set(JInfraContext.of("traceId", "trace-1"));

        assertThat(JInfraContextHolder.current().get(ContextKey.TRACE_ID)).isEqualTo("trace-1");

        JInfraContextHolder.clear();

        assertThat(JInfraContextHolder.current().asMap()).isEmpty();
    }

    @Test
    @DisplayName("scope 关闭后应恢复旧上下文")
    void shouldRestorePreviousContextAfterScopeClose() {
        JInfraContextHolder.set(JInfraContext.of("traceId", "old"));
        ContextSnapshot snapshot = ContextSnapshot.from(JInfraContext.of("traceId", "new"));

        try (ContextScope ignored = JInfraContextHolder.open(snapshot)) {
            assertThat(JInfraContextHolder.current().get(ContextKey.TRACE_ID)).isEqualTo("new");
        }

        assertThat(JInfraContextHolder.current().get(ContextKey.TRACE_ID)).isEqualTo("old");
    }
}
```

Create `ContextPropagationTest` with these tests:

```java
package cn.refinex.jinfra.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContextPropagationTest {

    @AfterEach
    void clearContext() {
        JInfraContextHolder.clear();
    }

    @Test
    @DisplayName("wrap Runnable 应传播捕获时上下文")
    void shouldWrapRunnableWithCapturedContext() {
        JInfraContextHolder.set(JInfraContext.of("traceId", "trace-1"));
        AtomicReference<String> value = new AtomicReference<>();

        Runnable wrapped = ContextPropagation.wrap(() ->
                value.set(JInfraContextHolder.current().get(ContextKey.TRACE_ID)));
        JInfraContextHolder.clear();
        wrapped.run();

        assertThat(value).hasValue("trace-1");
    }

    @Test
    @DisplayName("wrap Callable 应传播捕获时上下文并返回结果")
    void shouldWrapCallableWithCapturedContext() throws Exception {
        JInfraContextHolder.set(JInfraContext.of("tenantId", "tenant-a"));

        Callable<String> callable = ContextPropagation.wrap(() ->
                JInfraContextHolder.current().get(ContextKey.TENANT_ID));
        JInfraContextHolder.clear();

        assertThat(callable.call()).isEqualTo("tenant-a");
    }
}
```

Create `MdcContextBridgeTest` with these tests:

```java
package cn.refinex.jinfra.context.mdc;

import static org.assertj.core.api.Assertions.assertThat;

import cn.refinex.jinfra.context.ContextKey;
import cn.refinex.jinfra.context.ContextSnapshot;
import cn.refinex.jinfra.context.ContextScope;
import cn.refinex.jinfra.context.JInfraContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class MdcContextBridgeTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    @DisplayName("MDC bridge 应写入已知上下文 key")
    void shouldPutKnownKeysToMdc() {
        ContextSnapshot snapshot = ContextSnapshot.from(JInfraContext.empty()
                .put(ContextKey.TRACE_ID, "trace-1")
                .put(ContextKey.TENANT_ID, "tenant-a")
                .put(ContextKey.USER_ID, "user-1"));

        MdcContextBridge.put(snapshot);

        assertThat(MDC.get("traceId")).isEqualTo("trace-1");
        assertThat(MDC.get("tenantId")).isEqualTo("tenant-a");
        assertThat(MDC.get("userId")).isEqualTo("user-1");
    }

    @Test
    @DisplayName("withMdc 关闭后应清理已知 key")
    void shouldClearKnownKeysAfterScopeClose() {
        ContextSnapshot snapshot = ContextSnapshot.from(JInfraContext.of("traceId", "trace-1"));

        try (ContextScope ignored = MdcContextBridge.withMdc(snapshot)) {
            assertThat(MDC.get("traceId")).isEqualTo("trace-1");
        }

        assertThat(MDC.get("traceId")).isNull();
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-context -am test
```

Expected: FAIL because context classes do not exist.

- [ ] **Step 3: Implement context sources**

Create the source files with these implementation rules:

```java
// ContextKey.java
public record ContextKey<T>(String name) {
    public static final ContextKey<String> TRACE_ID = new ContextKey<>("traceId");
    public static final ContextKey<String> TENANT_ID = new ContextKey<>("tenantId");
    public static final ContextKey<String> USER_ID = new ContextKey<>("userId");
    public ContextKey {
        AssertUtils.hasText(name, "context key name must not be blank");
    }
}

// JInfraContext.java
public final class JInfraContext {
    private static final JInfraContext EMPTY = new JInfraContext(Map.of());
    private final Map<String, String> values;
    public static JInfraContext empty() { return EMPTY; }
    public static JInfraContext of(String key, String value) {
        return empty().put(new ContextKey<>(key), value);
    }
    public String get(ContextKey<String> key) { AssertUtils.notNull(key, "context key must not be null"); return values.get(key.name()); }
    public JInfraContext put(ContextKey<String> key, String value) {
        AssertUtils.notNull(key, "context key must not be null");
        AssertUtils.notNull(value, "context value must not be null");
        Map<String, String> copy = new LinkedHashMap<>(values);
        copy.put(key.name(), value);
        return new JInfraContext(copy);
    }
    public JInfraContext remove(ContextKey<String> key) {
        AssertUtils.notNull(key, "context key must not be null");
        Map<String, String> copy = new LinkedHashMap<>(values);
        copy.remove(key.name());
        return copy.isEmpty() ? EMPTY : new JInfraContext(copy);
    }
    public Map<String, String> asMap() { return values; }
    private JInfraContext(Map<String, String> values) { this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values)); }
}
```

Also implement:

- `ContextException extends CoreException` with constructors `(String message)` and `(String message, Throwable cause)`.
- `ContextSnapshot` record validates non-null context and provides `from(JInfraContext)`.
- `JInfraContextHolder` uses `private static final ThreadLocal<JInfraContext> HOLDER`.
- `ContextScope` stores previous context and restores it on first `close()`.
- `ContextPropagation.wrap(...)` captures `JInfraContextHolder.capture()` at wrapping time.
- `MdcContextBridge` writes only `traceId`、`tenantId`、`userId` and clears those known keys.

- [ ] **Step 4: Run context tests**

Run:

```bash
mvn -pl jinfra-context -am test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-context
git commit -m "feat(context): 添加纯Java上下文传播能力"
```

### Task 3: Implement `jinfra-id`

**Files:**
- Create source and test files listed in `jinfra-id` file structure.

- [ ] **Step 1: Write failing tests**

Create `UuidGeneratorTest`:

```java
package cn.refinex.jinfra.id.uuid;

import static org.assertj.core.api.Assertions.assertThat;

import cn.refinex.jinfra.id.IdGenerators;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UuidGeneratorTest {

    @Test
    @DisplayName("默认 UUID 应为 32 位无横线字符串")
    void shouldGenerateUuidWithoutHyphen() {
        String id = IdGenerators.uuid().nextId();

        assertThat(id).hasSize(32).doesNotContain("-");
    }

    @Test
    @DisplayName("标准 UUID 应保留横线")
    void shouldGenerateUuidWithHyphen() {
        String id = IdGenerators.uuidWithHyphen().nextId();

        assertThat(id).hasSize(36).contains("-");
    }
}
```

Create `UlidGeneratorTest`:

```java
package cn.refinex.jinfra.id.ulid;

import static org.assertj.core.api.Assertions.assertThat;

import cn.refinex.jinfra.id.IdGenerators;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UlidGeneratorTest {

    @Test
    @DisplayName("ULID 应为 26 位 Crockford Base32 字符串")
    void shouldGenerateValidUlid() {
        String id = IdGenerators.ulid().nextId();

        assertThat(id).hasSize(26).matches("[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}");
    }

    @Test
    @DisplayName("同一毫秒内 ULID 应单调递增")
    void shouldGenerateMonotonicUlidWithinSameMillisecond() {
        Clock clock = Clock.fixed(Instant.parse("2026-05-21T00:00:00Z"), ZoneOffset.UTC);
        SecureRandom random = new SecureRandom(new byte[] {1, 2, 3, 4});
        var generator = IdGenerators.ulid(clock, random);

        String first = generator.nextId();
        String second = generator.nextId();

        assertThat(second).isGreaterThan(first);
    }
}
```

Create `SnowflakeIdGeneratorTest`:

```java
package cn.refinex.jinfra.id.snowflake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.id.IdGenerationException;
import cn.refinex.jinfra.id.IdGenerators;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SnowflakeIdGeneratorTest {

    @Test
    @DisplayName("Snowflake 必须显式配置后才能生成 ID")
    void shouldGenerateWithExplicitConfig() {
        SnowflakeConfig config = new SnowflakeConfig(0L, 41, 10, 12, 1L, ClockBackwardsStrategy.FAIL_FAST);
        Clock clock = Clock.fixed(Instant.ofEpochMilli(1000L), ZoneOffset.UTC);

        Long id = IdGenerators.snowflake(config, clock).nextId();

        assertThat(id).isPositive();
    }

    @Test
    @DisplayName("非法 workerId 应被拒绝")
    void shouldRejectInvalidWorkerId() {
        assertThatThrownBy(() -> new SnowflakeConfig(0L, 41, 2, 12, 4L, ClockBackwardsStrategy.FAIL_FAST))
                .isInstanceOf(IdGenerationException.class);
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-id -am test
```

Expected: FAIL because ID classes do not exist.

- [ ] **Step 3: Implement ID sources**

Implement with these contracts:

```java
// IdGenerator.java
public interface IdGenerator<T> {
    T nextId();
}

// IdGenerators.java
public final class IdGenerators {
    public static IdGenerator<String> uuid() { return new UuidGenerator(false); }
    public static IdGenerator<String> uuidWithHyphen() { return new UuidGenerator(true); }
    public static IdGenerator<String> ulid() { return new UlidGenerator(Clock.systemUTC(), new SecureRandom()); }
    public static IdGenerator<String> ulid(Clock clock, SecureRandom random) { return new UlidGenerator(clock, random); }
    public static IdGenerator<Long> snowflake(SnowflakeConfig config, Clock clock) { return new SnowflakeIdGenerator(config, clock); }
}
```

Implementation details:

- `UuidGenerator` delegates to `UUID.randomUUID()` and removes hyphens when configured.
- `Ulid` encodes 48-bit timestamp + 80-bit randomness using alphabet `0123456789ABCDEFGHJKMNPQRSTVWXYZ`.
- `UlidGenerator.nextId()` is synchronized, tracks `lastTimeMillis` and 10-byte random array, increments random array when current millisecond equals last millisecond.
- `SnowflakeConfig` validates bit counts are positive, total bits do not exceed 63, `workerId` is within `[0, (1L << workerIdBits) - 1]`, strategy is non-null.
- `SnowflakeIdGenerator.nextId()` is synchronized, calculates `(timestampDelta << workerIdBits + sequenceBits) | (workerId << sequenceBits) | sequence`.
- Clock rollback with `FAIL_FAST` throws `IdGenerationException`.

- [ ] **Step 4: Run ID tests**

Run:

```bash
mvn -pl jinfra-id -am test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-id
git commit -m "feat(id): 添加UUID和ULID及显式Snowflake生成器"
```

### Task 4: Implement `jinfra-io`

**Files:**
- Create source and test files listed in `jinfra-io` file structure.

- [ ] **Step 1: Write failing tests**

Create tests with these required assertions:

```java
// PathUtilsTest.java
@Test
@DisplayName("ensureInside 应允许 base 内路径")
void shouldAllowPathInsideBase() {
    Path base = tempDir.resolve("base");
    Path target = base.resolve("a/b.txt");
    assertThat(PathUtils.ensureInside(base, target)).endsWith(Path.of("a/b.txt"));
}

@Test
@DisplayName("ensureInside 应拒绝 base 外路径")
void shouldRejectPathOutsideBase() {
    Path base = tempDir.resolve("base");
    Path target = base.resolve("../evil.txt");
    assertThatThrownBy(() -> PathUtils.ensureInside(base, target))
            .isInstanceOf(IoException.class);
}
```

```java
// FileUtilsTest.java
@Test
@DisplayName("文件工具应支持字符串和字节读写")
void shouldReadAndWriteFiles() {
    Path text = tempDir.resolve("a/b.txt");
    FileUtils.writeString(text, "你好", StandardCharsets.UTF_8);
    assertThat(FileUtils.readString(text, StandardCharsets.UTF_8)).isEqualTo("你好");

    Path bytes = tempDir.resolve("bytes.bin");
    FileUtils.writeBytes(bytes, new byte[] {1, 2, 3});
    assertThat(FileUtils.readBytes(bytes)).containsExactly(1, 2, 3);
}
```

```java
// StreamUtilsTest.java
@Test
@DisplayName("流工具应支持复制和读取")
void shouldCopyAndReadStreams() {
    ByteArrayInputStream input = new ByteArrayInputStream("abc".getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    long copied = StreamUtils.copy(input, output);
    assertThat(copied).isEqualTo(3);
    assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo("abc");
}
```

```java
// ZipUtilsTest.java
@Test
@DisplayName("zip 工具应支持目录压缩和解压")
void shouldZipAndUnzipDirectory() {
    Path source = tempDir.resolve("source");
    FileUtils.writeString(source.resolve("a.txt"), "a", StandardCharsets.UTF_8);
    Path zip = tempDir.resolve("archive.zip");
    Path target = tempDir.resolve("target");

    ZipUtils.zipDirectory(source, zip);
    ZipUtils.unzip(zip, target);

    assertThat(FileUtils.readString(target.resolve("a.txt"), StandardCharsets.UTF_8)).isEqualTo("a");
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-io -am test
```

Expected: FAIL because IO classes do not exist.

- [ ] **Step 3: Implement IO sources**

Implementation requirements:

- `IoException extends CoreException` with constructors for message and cause.
- `FileUtils.writeString/writeBytes` must create parent directories before writing.
- `StreamUtils.copy` must not close input or output; caller owns stream lifecycle.
- `ZipUtils.zipDirectory` must skip directories as entries unless needed and use relative entry names with `/`.
- `ZipUtils.unzip` must call `PathUtils.ensureInside(targetDir, resolvedEntryPath)` before writing.
- Every `IOException` must be caught and wrapped as `IoException`.

Use this `ensureInside` core logic:

```java
Path normalizedBase = baseDir.toAbsolutePath().normalize();
Path normalizedTarget = target.toAbsolutePath().normalize();
if (!normalizedTarget.startsWith(normalizedBase)) {
    throw new IoException("target path is outside base directory");
}
return normalizedTarget;
```

- [ ] **Step 4: Run IO tests**

Run:

```bash
mvn -pl jinfra-io -am test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-io
git commit -m "feat(io): 添加文件流路径和zip工具"
```

### Task 5: Implement `jinfra-codec`

**Files:**
- Create source and test files listed in `jinfra-codec` file structure.

- [ ] **Step 1: Write failing tests**

Create `Base64UtilsTest`:

```java
package cn.refinex.jinfra.codec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Base64UtilsTest {

    @Test
    @DisplayName("Base64 应支持标准编码和解码")
    void shouldEncodeAndDecodeBase64() {
        String encoded = Base64Utils.encode("hello".getBytes(StandardCharsets.UTF_8));

        assertThat(encoded).isEqualTo("aGVsbG8=");
        assertThat(new String(Base64Utils.decode(encoded), StandardCharsets.UTF_8)).isEqualTo("hello");
    }

    @Test
    @DisplayName("非法 Base64 应包装为 CodecException")
    void shouldRejectInvalidBase64() {
        assertThatThrownBy(() -> Base64Utils.decode("%%%"))
                .isInstanceOf(CodecException.class);
    }
}
```

Create `HexUtilsTest`:

```java
package cn.refinex.jinfra.codec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HexUtilsTest {

    @Test
    @DisplayName("Hex 应支持小写编码和解码")
    void shouldEncodeAndDecodeHex() {
        String encoded = HexUtils.encode(new byte[] {10, 15});

        assertThat(encoded).isEqualTo("0a0f");
        assertThat(HexUtils.decode(encoded)).containsExactly(10, 15);
    }

    @Test
    @DisplayName("奇数长度 Hex 应被拒绝")
    void shouldRejectOddLengthHex() {
        assertThatThrownBy(() -> HexUtils.decode("abc"))
                .isInstanceOf(CodecException.class);
    }
}
```

Create `UrlCodecUtilsTest`:

```java
package cn.refinex.jinfra.codec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlCodecUtilsTest {

    @Test
    @DisplayName("URL 编解码应支持中文")
    void shouldEncodeAndDecodeChineseText() {
        String encoded = UrlCodecUtils.encode("你好 world", StandardCharsets.UTF_8);

        assertThat(UrlCodecUtils.decode(encoded, StandardCharsets.UTF_8)).isEqualTo("你好 world");
    }

    @Test
    @DisplayName("非法百分号编码应包装为 CodecException")
    void shouldRejectInvalidPercentEncoding() {
        assertThatThrownBy(() -> UrlCodecUtils.decode("%", StandardCharsets.UTF_8))
                .isInstanceOf(CodecException.class);
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-codec -am test
```

Expected: FAIL because codec classes do not exist.

- [ ] **Step 3: Implement codec sources**

Implementation requirements:

- `CodecException extends CoreException`.
- `Base64Utils` uses `java.util.Base64`.
- `HexUtils` uses Apache Commons Codec `Hex.encodeHexString` and `Hex.decodeHex`.
- `UrlCodecUtils` uses `URLEncoder.encode` and `URLDecoder.decode`.
- Null byte arrays, strings, and charsets are rejected through `AssertUtils.notNull`.
- Illegal decode input is caught and wrapped as `CodecException`.

- [ ] **Step 4: Run codec tests**

Run:

```bash
mvn -pl jinfra-codec -am test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-codec
git commit -m "feat(codec): 添加Base64 Hex和URL编解码工具"
```

### Task 6: Cross-Module Verification

**Files:**
- Verify all files under:
  - `jinfra-context/src/main/java`
  - `jinfra-id/src/main/java`
  - `jinfra-io/src/main/java`
  - `jinfra-codec/src/main/java`
  - matching `src/test/java` trees

- [ ] **Step 1: Run all selected module tests**

Run:

```bash
mvn -pl jinfra-context,jinfra-id,jinfra-io,jinfra-codec -am test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 2: Run verify**

Run:

```bash
mvn -pl jinfra-context,jinfra-id,jinfra-io,jinfra-codec -am verify
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 3: Run release preview without signing or publishing**

Run:

```bash
mvn -pl jinfra-context,jinfra-id,jinfra-io,jinfra-codec -am -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify
```

Expected: `BUILD SUCCESS` and `-sources.jar` / `-javadoc.jar` exist for all four modules.

- [ ] **Step 4: Verify dependency boundaries**

Run:

```bash
rg -n "springframework|spring-boot|reactor|rxjava|netty|jackson|redis" \
  jinfra-context/pom.xml jinfra-context/src/main/java jinfra-context/src/test/java \
  jinfra-id/pom.xml jinfra-id/src/main/java jinfra-id/src/test/java \
  jinfra-io/pom.xml jinfra-io/src/main/java jinfra-io/src/test/java \
  jinfra-codec/pom.xml jinfra-codec/src/main/java jinfra-codec/src/test/java
```

Expected: no output.

- [ ] **Step 5: Verify module interdependency boundaries**

Run:

```bash
rg -n "cn\\.refinex\\.jinfra\\.(context|id|io|codec)" \
  jinfra-context/src/main/java jinfra-id/src/main/java jinfra-io/src/main/java jinfra-codec/src/main/java
```

Expected:

- Matches inside each module's own package are acceptable.
- No module source imports another selected module package.

- [ ] **Step 6: Inspect final diff**

Run:

```bash
git diff --stat
git diff -- jinfra-context jinfra-id jinfra-io jinfra-codec
```

Expected: diff only contains planned POM, source, and test files.

- [ ] **Step 7: Commit verification fixes if needed**

If verification required small fixes:

```bash
git add jinfra-context jinfra-id jinfra-io jinfra-codec
git commit -m "test: 完成基础四模块验证"
```

If no files changed after verification, do not create an empty commit.

## Self-Review Checklist

- Spec coverage: Task 2 covers context; Task 3 covers ID including UUID, ULID, explicit Snowflake; Task 4 covers IO; Task 5 covers codec; Task 6 covers cross-module verification and dependency boundaries.
- Dependency direction: no task introduces dependencies between `jinfra-context`、`jinfra-id`、`jinfra-io`、`jinfra-codec`.
- Snowflake production safety: no default epoch, default bit width, or default workerId is introduced.
- Thread safety: context immutable/scope-based, ID generators synchronized where stateful, IO/codec utilities stateless.
- Test coverage: each task includes failing tests, pass verification, and final reactor verification.
