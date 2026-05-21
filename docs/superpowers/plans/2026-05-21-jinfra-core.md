# JInfra Core Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 `jinfra-core` 实现轻量、无 Spring 依赖的错误码、异常层级、结果模型、断言工具和基础工具类。

**Architecture:** `jinfra-core` 只暴露稳定的核心抽象，包根为 `cn.refinex.jinfra.core`。错误码用 `ErrorCode` 接口扩展、`CoreErrorCode` 枚举兜底；异常统一带 `String code`；工具类保持静态无状态；`Result<T>` 使用 Java 17 record 保持不可变。

**Tech Stack:** Java 17、Maven、JUnit Jupiter、AssertJ、Apache Commons Lang 3。

---

## File Structure

- Modify: `jinfra-core/pom.xml`
  - 增加 `junit-jupiter` 和 `assertj-core` 测试依赖。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/constant/CoreConstants.java`
  - core 公共常量。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/error/ErrorCode.java`
  - 下游可扩展的错误码接口。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/error/CoreErrorCode.java`
  - core 内置错误码枚举。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/JInfraException.java`
  - JInfra 统一根异常。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/JInfraRuntimeException.java`
  - 运行期异常基础层。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/CoreException.java`
  - core 断言和工具使用的具体异常。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/lang/Result.java`
  - 不可变成功/失败结果模型。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/AssertUtils.java`
  - 参数和状态断言工具。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/StringUtils.java`
  - 字符串基础工具。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/CollectionUtils.java`
  - 集合基础工具。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/ObjectUtils.java`
  - 对象基础工具。
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/ExceptionUtils.java`
  - 异常链和堆栈工具。
- Create tests under `jinfra-core/src/test/java/cn/refinex/jinfra/core/...`
  - 每个公开类型对应测试，覆盖正常路径、边界条件和异常路径。

## Implementation Notes

- 所有公开类、接口、枚举、record 和公开方法必须有中文 Javadoc。
- `jinfra-core` 禁止引入 Spring、Spring Boot、Lombok、Jackson、Web/HTTP 依赖。
- `Result.failure(...)` 不允许使用 `CoreErrorCode.SUCCESS`，失败码和失败消息都必须非 blank。
- `AssertUtils` 失败统一抛 `CoreException`。
- 工具类均为 `final` 类，私有构造器抛 `AssertionError`。

### Task 1: Add Test Dependencies

**Files:**
- Modify: `jinfra-core/pom.xml`

- [ ] **Step 1: Add test dependencies**

Apply this diff:

```diff
diff --git a/jinfra-core/pom.xml b/jinfra-core/pom.xml
@@
         <dependency>
             <groupId>org.apache.commons</groupId>
             <artifactId>commons-lang3</artifactId>
         </dependency>
+        <dependency>
+            <groupId>org.junit.jupiter</groupId>
+            <artifactId>junit-jupiter</artifactId>
+            <scope>test</scope>
+        </dependency>
+        <dependency>
+            <groupId>org.assertj</groupId>
+            <artifactId>assertj-core</artifactId>
+            <scope>test</scope>
+        </dependency>
     </dependencies>
 </project>
```

- [ ] **Step 2: Verify Maven resolves test dependencies**

Run:

```bash
mvn -pl jinfra-core -am test -DskipTests
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 3: Commit**

```bash
git add jinfra-core/pom.xml
git commit -m "test(core): 添加核心模块测试依赖"
```

### Task 2: Error Code Model

**Files:**
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/error/CoreErrorCodeTest.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/error/ErrorCode.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/error/CoreErrorCode.java`

- [ ] **Step 1: Write failing tests**

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/error/CoreErrorCodeTest.java`:

```java
package cn.refinex.jinfra.core.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoreErrorCodeTest {

    @Test
    @DisplayName("核心错误码应暴露稳定 code 与 message")
    void shouldExposeStableCodeAndMessage() {
        assertThat(CoreErrorCode.SUCCESS.code()).isEqualTo("SUCCESS");
        assertThat(CoreErrorCode.SUCCESS.message()).isEqualTo("Success");
        assertThat(CoreErrorCode.INVALID_ARGUMENT.code()).isEqualTo("INVALID_ARGUMENT");
        assertThat(CoreErrorCode.NULL_ARGUMENT.code()).isEqualTo("NULL_ARGUMENT");
        assertThat(CoreErrorCode.ILLEGAL_STATE.code()).isEqualTo("ILLEGAL_STATE");
        assertThat(CoreErrorCode.INTERNAL_ERROR.code()).isEqualTo("INTERNAL_ERROR");
    }

    @Test
    @DisplayName("核心错误码枚举应实现 ErrorCode 扩展接口")
    void shouldImplementErrorCode() {
        ErrorCode errorCode = CoreErrorCode.INVALID_ARGUMENT;

        assertThat(errorCode.code()).isEqualTo("INVALID_ARGUMENT");
        assertThat(errorCode.message()).isEqualTo("Invalid argument");
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-core -Dtest=CoreErrorCodeTest test
```

Expected: FAIL because `ErrorCode` and `CoreErrorCode` do not exist.

- [ ] **Step 3: Implement error code API**

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/error/ErrorCode.java`:

```java
package cn.refinex.jinfra.core.error;

/**
 * 错误码抽象。
 *
 * <p>下游模块可以通过枚举或普通类实现该接口，异常和结果模型只对外暴露字符串形式的错误码。</p>
 */
public interface ErrorCode {

    /**
     * 返回稳定的错误码。
     *
     * @return 错误码
     */
    String code();

    /**
     * 返回默认开发者消息。
     *
     * @return 默认消息
     */
    String message();
}
```

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/error/CoreErrorCode.java`:

```java
package cn.refinex.jinfra.core.error;

/**
 * core 模块内置错误码。
 */
public enum CoreErrorCode implements ErrorCode {

    /**
     * 操作成功。
     */
    SUCCESS("SUCCESS", "Success"),

    /**
     * 参数不合法。
     */
    INVALID_ARGUMENT("INVALID_ARGUMENT", "Invalid argument"),

    /**
     * 参数为空。
     */
    NULL_ARGUMENT("NULL_ARGUMENT", "Null argument"),

    /**
     * 状态不合法。
     */
    ILLEGAL_STATE("ILLEGAL_STATE", "Illegal state"),

    /**
     * 内部错误。
     */
    INTERNAL_ERROR("INTERNAL_ERROR", "Internal error");

    private final String code;

    private final String message;

    CoreErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
```

- [ ] **Step 4: Run tests to verify pass**

Run:

```bash
mvn -pl jinfra-core -Dtest=CoreErrorCodeTest test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-core/src/main/java/cn/refinex/jinfra/core/error jinfra-core/src/test/java/cn/refinex/jinfra/core/error
git commit -m "feat(core): 添加错误码模型"
```

### Task 3: Exception Hierarchy

**Files:**
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/exception/CoreExceptionTest.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/JInfraException.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/JInfraRuntimeException.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/CoreException.java`

- [ ] **Step 1: Write failing tests**

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/exception/CoreExceptionTest.java`:

```java
package cn.refinex.jinfra.core.exception;

import static org.assertj.core.api.Assertions.assertThat;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoreExceptionTest {

    @Test
    @DisplayName("基础异常应保存错误码、消息和 cause")
    void shouldExposeCodeMessageAndCause() {
        RuntimeException cause = new RuntimeException("root");

        CoreException exception = new CoreException(CoreErrorCode.INVALID_ARGUMENT, "bad argument", cause);

        assertThat(exception).isInstanceOf(JInfraRuntimeException.class);
        assertThat(exception).isInstanceOf(JInfraException.class);
        assertThat(exception.code()).isEqualTo("INVALID_ARGUMENT");
        assertThat(exception.getMessage()).isEqualTo("bad argument");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("使用 ErrorCode 构造异常时应采用默认消息")
    void shouldUseDefaultMessage() {
        CoreException exception = new CoreException(CoreErrorCode.INTERNAL_ERROR);

        assertThat(exception.code()).isEqualTo("INTERNAL_ERROR");
        assertThat(exception.getMessage()).isEqualTo("Internal error");
    }

    @Test
    @DisplayName("使用字符串构造异常时应保留自定义错误码")
    void shouldKeepCustomCode() {
        CoreException exception = new CoreException("CUSTOM_ERROR", "custom message");

        assertThat(exception.code()).isEqualTo("CUSTOM_ERROR");
        assertThat(exception.getMessage()).isEqualTo("custom message");
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-core -Dtest=CoreExceptionTest test
```

Expected: FAIL because exception classes do not exist.

- [ ] **Step 3: Implement exception hierarchy**

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/JInfraException.java`:

```java
package cn.refinex.jinfra.core.exception;

import cn.refinex.jinfra.core.error.ErrorCode;

/**
 * JInfra 统一根异常。
 */
public class JInfraException extends RuntimeException {

    private final String code;

    /**
     * 使用错误码和默认消息创建异常。
     *
     * @param errorCode 错误码
     */
    public JInfraException(ErrorCode errorCode) {
        this(errorCode.code(), errorCode.message());
    }

    /**
     * 使用错误码和自定义消息创建异常。
     *
     * @param errorCode 错误码
     * @param message 异常消息
     */
    public JInfraException(ErrorCode errorCode, String message) {
        this(errorCode.code(), message);
    }

    /**
     * 使用错误码、自定义消息和 cause 创建异常。
     *
     * @param errorCode 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public JInfraException(ErrorCode errorCode, String message, Throwable cause) {
        this(errorCode.code(), message, cause);
    }

    /**
     * 使用字符串错误码和消息创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     */
    public JInfraException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用字符串错误码、消息和 cause 创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public JInfraException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 返回错误码。
     *
     * @return 错误码
     */
    public String code() {
        return code;
    }
}
```

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/JInfraRuntimeException.java`:

```java
package cn.refinex.jinfra.core.exception;

import cn.refinex.jinfra.core.error.ErrorCode;

/**
 * JInfra 运行期异常基础类。
 */
public class JInfraRuntimeException extends JInfraException {

    /**
     * 使用错误码和默认消息创建异常。
     *
     * @param errorCode 错误码
     */
    public JInfraRuntimeException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 使用错误码和自定义消息创建异常。
     *
     * @param errorCode 错误码
     * @param message 异常消息
     */
    public JInfraRuntimeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 使用错误码、自定义消息和 cause 创建异常。
     *
     * @param errorCode 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public JInfraRuntimeException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 使用字符串错误码和消息创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     */
    public JInfraRuntimeException(String code, String message) {
        super(code, message);
    }

    /**
     * 使用字符串错误码、消息和 cause 创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public JInfraRuntimeException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
```

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/exception/CoreException.java`:

```java
package cn.refinex.jinfra.core.exception;

import cn.refinex.jinfra.core.error.ErrorCode;

/**
 * core 模块使用的基础异常。
 */
public class CoreException extends JInfraRuntimeException {

    /**
     * 使用错误码和默认消息创建异常。
     *
     * @param errorCode 错误码
     */
    public CoreException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 使用错误码和自定义消息创建异常。
     *
     * @param errorCode 错误码
     * @param message 异常消息
     */
    public CoreException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 使用错误码、自定义消息和 cause 创建异常。
     *
     * @param errorCode 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public CoreException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 使用字符串错误码和消息创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     */
    public CoreException(String code, String message) {
        super(code, message);
    }

    /**
     * 使用字符串错误码、消息和 cause 创建异常。
     *
     * @param code 错误码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public CoreException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
```

- [ ] **Step 4: Run tests to verify pass**

Run:

```bash
mvn -pl jinfra-core -Dtest=CoreExceptionTest test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-core/src/main/java/cn/refinex/jinfra/core/exception jinfra-core/src/test/java/cn/refinex/jinfra/core/exception
git commit -m "feat(core): 添加基础异常层级"
```

### Task 4: Result Model

**Files:**
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/lang/ResultTest.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/lang/Result.java`

- [ ] **Step 1: Write failing tests**

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/lang/ResultTest.java`:

```java
package cn.refinex.jinfra.core.lang;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    @DisplayName("无数据成功结果应使用 SUCCESS 错误码")
    void shouldCreateSuccessWithoutData() {
        Result<Void> result = Result.success();

        assertThat(result.success()).isTrue();
        assertThat(result.code()).isEqualTo("SUCCESS");
        assertThat(result.message()).isEqualTo("Success");
        assertThat(result.data()).isNull();
    }

    @Test
    @DisplayName("带数据成功结果应保留 data")
    void shouldCreateSuccessWithData() {
        Result<String> result = Result.success("ok");

        assertThat(result.success()).isTrue();
        assertThat(result.data()).isEqualTo("ok");
    }

    @Test
    @DisplayName("失败结果应保留错误码和消息")
    void shouldCreateFailure() {
        Result<Void> result = Result.failure(CoreErrorCode.INVALID_ARGUMENT);

        assertThat(result.success()).isFalse();
        assertThat(result.code()).isEqualTo("INVALID_ARGUMENT");
        assertThat(result.message()).isEqualTo("Invalid argument");
        assertThat(result.data()).isNull();
    }

    @Test
    @DisplayName("失败结果应支持自定义消息")
    void shouldCreateFailureWithCustomMessage() {
        Result<Void> result = Result.failure(CoreErrorCode.NULL_ARGUMENT, "name is required");

        assertThat(result.success()).isFalse();
        assertThat(result.code()).isEqualTo("NULL_ARGUMENT");
        assertThat(result.message()).isEqualTo("name is required");
    }

    @Test
    @DisplayName("失败结果不允许使用 SUCCESS 错误码")
    void shouldRejectSuccessCodeForFailure() {
        assertThatThrownBy(() -> Result.failure(CoreErrorCode.SUCCESS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("failure code must not be SUCCESS");
    }

    @Test
    @DisplayName("失败结果不允许 blank 错误码或消息")
    void shouldRejectBlankFailureCodeOrMessage() {
        assertThatThrownBy(() -> Result.failure(" ", "bad"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("failure code must not be blank");
        assertThatThrownBy(() -> Result.failure("BAD", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("failure message must not be blank");
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-core -Dtest=ResultTest test
```

Expected: FAIL because `Result` does not exist.

- [ ] **Step 3: Implement Result**

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/lang/Result.java`:

```java
package cn.refinex.jinfra.core.lang;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import cn.refinex.jinfra.core.error.ErrorCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 不可变结果模型。
 *
 * @param success 是否成功
 * @param code 错误码
 * @param message 消息
 * @param data 数据
 * @param <T> 数据类型
 */
public record Result<T>(boolean success, String code, String message, T data) {

    /**
     * 创建无数据成功结果。
     *
     * @return 成功结果
     */
    public static Result<Void> success() {
        return new Result<>(true, CoreErrorCode.SUCCESS.code(), CoreErrorCode.SUCCESS.message(), null);
    }

    /**
     * 创建带数据成功结果。
     *
     * @param data 数据
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(true, CoreErrorCode.SUCCESS.code(), CoreErrorCode.SUCCESS.message(), data);
    }

    /**
     * 使用字符串错误码和消息创建失败结果。
     *
     * @param code 错误码
     * @param message 消息
     * @return 失败结果
     */
    public static Result<Void> failure(String code, String message) {
        validateFailure(code, message);
        return new Result<>(false, code, message, null);
    }

    /**
     * 使用错误码默认消息创建失败结果。
     *
     * @param errorCode 错误码
     * @return 失败结果
     */
    public static Result<Void> failure(ErrorCode errorCode) {
        return failure(errorCode.code(), errorCode.message());
    }

    /**
     * 使用错误码和自定义消息创建失败结果。
     *
     * @param errorCode 错误码
     * @param message 消息
     * @return 失败结果
     */
    public static Result<Void> failure(ErrorCode errorCode, String message) {
        return failure(errorCode.code(), message);
    }

    private static void validateFailure(String code, String message) {
        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("failure code must not be blank");
        }
        if (CoreErrorCode.SUCCESS.code().equals(code)) {
            throw new IllegalArgumentException("failure code must not be SUCCESS");
        }
        if (StringUtils.isBlank(message)) {
            throw new IllegalArgumentException("failure message must not be blank");
        }
    }
}
```

- [ ] **Step 4: Run tests to verify pass**

Run:

```bash
mvn -pl jinfra-core -Dtest=ResultTest test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-core/src/main/java/cn/refinex/jinfra/core/lang jinfra-core/src/test/java/cn/refinex/jinfra/core/lang
git commit -m "feat(core): 添加结果模型"
```

### Task 5: Core Constants and Assert Utils

**Files:**
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/constant/CoreConstantsTest.java`
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/AssertUtilsTest.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/constant/CoreConstants.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/AssertUtils.java`

- [ ] **Step 1: Write failing tests**

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/constant/CoreConstantsTest.java`:

```java
package cn.refinex.jinfra.core.constant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoreConstantsTest {

    @Test
    @DisplayName("核心常量应提供模块名和空字符串")
    void shouldExposeCoreConstants() {
        assertThat(CoreConstants.MODULE_NAME).isEqualTo("jinfra-core");
        assertThat(CoreConstants.EMPTY).isEmpty();
    }
}
```

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/AssertUtilsTest.java`:

```java
package cn.refinex.jinfra.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import cn.refinex.jinfra.core.exception.CoreException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AssertUtilsTest {

    @Test
    @DisplayName("断言通过时不应抛出异常")
    void shouldPassValidAssertions() {
        assertThatCode(() -> AssertUtils.notNull("value", CoreErrorCode.NULL_ARGUMENT)).doesNotThrowAnyException();
        assertThatCode(() -> AssertUtils.hasText("value", CoreErrorCode.INVALID_ARGUMENT)).doesNotThrowAnyException();
        assertThatCode(() -> AssertUtils.isTrue(1 < 2, CoreErrorCode.INVALID_ARGUMENT)).doesNotThrowAnyException();
        assertThatCode(() -> AssertUtils.state(true, CoreErrorCode.ILLEGAL_STATE)).doesNotThrowAnyException();
        assertThatCode(() -> AssertUtils.notEmpty(List.of("a"), CoreErrorCode.INVALID_ARGUMENT)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("断言失败时应抛出 CoreException 并保留错误码")
    void shouldThrowCoreExceptionWithErrorCode() {
        assertThatThrownBy(() -> AssertUtils.notNull(null, CoreErrorCode.NULL_ARGUMENT))
                .isInstanceOf(CoreException.class)
                .hasMessage("Null argument")
                .extracting("code")
                .isEqualTo("NULL_ARGUMENT");
    }

    @Test
    @DisplayName("断言失败时应支持自定义消息")
    void shouldThrowCoreExceptionWithCustomMessage() {
        assertThatThrownBy(() -> AssertUtils.hasText(" ", "name is required"))
                .isInstanceOf(CoreException.class)
                .hasMessage("name is required");
    }

    @Test
    @DisplayName("集合断言应拒绝 null 和空集合")
    void shouldRejectNullOrEmptyCollection() {
        assertThatThrownBy(() -> AssertUtils.notEmpty(null, CoreErrorCode.INVALID_ARGUMENT))
                .isInstanceOf(CoreException.class)
                .hasMessage("Invalid argument");
        assertThatThrownBy(() -> AssertUtils.notEmpty(List.of(), "items is empty"))
                .isInstanceOf(CoreException.class)
                .hasMessage("items is empty");
    }

    @Test
    @DisplayName("状态断言失败时应使用 ILLEGAL_STATE")
    void shouldRejectIllegalState() {
        assertThatThrownBy(() -> AssertUtils.state(false, "state broken"))
                .isInstanceOf(CoreException.class)
                .hasMessage("state broken");
    }

    @Test
    @DisplayName("工具类构造器不可实例化")
    void shouldNotInstantiateUtilityClass() throws Exception {
        var constructor = AssertUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(AssertionError.class);
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-core -Dtest=CoreConstantsTest,AssertUtilsTest test
```

Expected: FAIL because `CoreConstants` and `AssertUtils` do not exist.

- [ ] **Step 3: Implement constants and assertions**

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/constant/CoreConstants.java`:

```java
package cn.refinex.jinfra.core.constant;

/**
 * core 模块公共常量。
 */
public final class CoreConstants {

    /**
     * core 模块名。
     */
    public static final String MODULE_NAME = "jinfra-core";

    /**
     * 空字符串。
     */
    public static final String EMPTY = "";

    private CoreConstants() {
        throw new AssertionError("No cn.refinex.jinfra.core.constant.CoreConstants instances");
    }
}
```

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/AssertUtils.java`:

```java
package cn.refinex.jinfra.core.util;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import cn.refinex.jinfra.core.error.ErrorCode;
import cn.refinex.jinfra.core.exception.CoreException;
import java.util.Collection;

/**
 * 断言工具。
 */
public final class AssertUtils {

    private AssertUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.AssertUtils instances");
    }

    /**
     * 断言对象不为 null。
     *
     * @param value 待检查对象
     * @param errorCode 断言失败错误码
     */
    public static void notNull(Object value, ErrorCode errorCode) {
        if (value == null) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言对象不为 null。
     *
     * @param value 待检查对象
     * @param message 断言失败消息
     */
    public static void notNull(Object value, String message) {
        if (value == null) {
            throw new CoreException(CoreErrorCode.NULL_ARGUMENT, message);
        }
    }

    /**
     * 断言字符串包含非空白字符。
     *
     * @param value 待检查字符串
     * @param errorCode 断言失败错误码
     */
    public static void hasText(String value, ErrorCode errorCode) {
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言字符串包含非空白字符。
     *
     * @param value 待检查字符串
     * @param message 断言失败消息
     */
    public static void hasText(String value, String message) {
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            throw new CoreException(CoreErrorCode.INVALID_ARGUMENT, message);
        }
    }

    /**
     * 断言表达式为 true。
     *
     * @param expression 表达式
     * @param errorCode 断言失败错误码
     */
    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言表达式为 true。
     *
     * @param expression 表达式
     * @param message 断言失败消息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new CoreException(CoreErrorCode.INVALID_ARGUMENT, message);
        }
    }

    /**
     * 断言状态表达式为 true。
     *
     * @param expression 状态表达式
     * @param errorCode 断言失败错误码
     */
    public static void state(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言状态表达式为 true。
     *
     * @param expression 状态表达式
     * @param message 断言失败消息
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new CoreException(CoreErrorCode.ILLEGAL_STATE, message);
        }
    }

    /**
     * 断言集合不为 null 且非空。
     *
     * @param collection 待检查集合
     * @param errorCode 断言失败错误码
     */
    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        if (collection == null || collection.isEmpty()) {
            throw new CoreException(errorCode);
        }
    }

    /**
     * 断言集合不为 null 且非空。
     *
     * @param collection 待检查集合
     * @param message 断言失败消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new CoreException(CoreErrorCode.INVALID_ARGUMENT, message);
        }
    }
}
```

- [ ] **Step 4: Run tests to verify pass**

Run:

```bash
mvn -pl jinfra-core -Dtest=CoreConstantsTest,AssertUtilsTest test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-core/src/main/java/cn/refinex/jinfra/core/constant jinfra-core/src/main/java/cn/refinex/jinfra/core/util/AssertUtils.java jinfra-core/src/test/java/cn/refinex/jinfra/core/constant jinfra-core/src/test/java/cn/refinex/jinfra/core/util/AssertUtilsTest.java
git commit -m "feat(core): 添加常量和断言工具"
```

### Task 6: String and Object Utils

**Files:**
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/StringUtilsTest.java`
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/ObjectUtilsTest.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/StringUtils.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/ObjectUtils.java`

- [ ] **Step 1: Write failing tests**

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/StringUtilsTest.java`:

```java
package cn.refinex.jinfra.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringUtilsTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("null、empty、blank 字符串应识别为空白")
    void shouldDetectBlank(String value) {
        assertThat(StringUtils.isBlank(value)).isTrue();
        assertThat(StringUtils.isNotBlank(value)).isFalse();
    }

    @Test
    @DisplayName("非空白字符串应识别为非空白")
    void shouldDetectNotBlank() {
        assertThat(StringUtils.isBlank("abc")).isFalse();
        assertThat(StringUtils.isNotBlank("abc")).isTrue();
    }

    @Test
    @DisplayName("trimToEmpty 应将 null 转为空字符串")
    void shouldTrimToEmpty() {
        assertThat(StringUtils.trimToEmpty(null)).isEmpty();
        assertThat(StringUtils.trimToEmpty("  abc  ")).isEqualTo("abc");
    }

    @Test
    @DisplayName("defaultIfBlank 应为空白值返回默认值")
    void shouldDefaultIfBlank() {
        assertThat(StringUtils.defaultIfBlank(" ", "fallback")).isEqualTo("fallback");
        assertThat(StringUtils.defaultIfBlank("value", "fallback")).isEqualTo("value");
    }

    @Test
    @DisplayName("equals 应支持 null 安全比较")
    void shouldCompareSafely() {
        assertThat(StringUtils.equals(null, null)).isTrue();
        assertThat(StringUtils.equals(null, "a")).isFalse();
        assertThat(StringUtils.equals("a", "a")).isTrue();
    }

    @Test
    @DisplayName("工具类构造器不可实例化")
    void shouldNotInstantiateUtilityClass() throws Exception {
        var constructor = StringUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(AssertionError.class);
    }
}
```

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/ObjectUtilsTest.java`:

```java
package cn.refinex.jinfra.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import cn.refinex.jinfra.core.exception.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ObjectUtilsTest {

    @Test
    @DisplayName("defaultIfNull 应在 value 为 null 时返回默认值")
    void shouldDefaultIfNull() {
        assertThat(ObjectUtils.defaultIfNull(null, "fallback")).isEqualTo("fallback");
        assertThat(ObjectUtils.defaultIfNull("value", "fallback")).isEqualTo("value");
    }

    @Test
    @DisplayName("equals 与 hashCode 应 null 安全")
    void shouldCompareAndHashSafely() {
        assertThat(ObjectUtils.equals(null, null)).isTrue();
        assertThat(ObjectUtils.equals("a", "b")).isFalse();
        assertThat(ObjectUtils.hashCode(null)).isZero();
        assertThat(ObjectUtils.hashCode("a")).isEqualTo("a".hashCode());
    }

    @Test
    @DisplayName("requireNonNull 应返回非空对象")
    void shouldReturnNonNullValue() {
        assertThat(ObjectUtils.requireNonNull("value", CoreErrorCode.NULL_ARGUMENT)).isEqualTo("value");
    }

    @Test
    @DisplayName("requireNonNull 应在 null 时抛出 CoreException")
    void shouldRejectNullValue() {
        assertThatThrownBy(() -> ObjectUtils.requireNonNull(null, "value is required"))
                .isInstanceOf(CoreException.class)
                .hasMessage("value is required");
    }

    @Test
    @DisplayName("工具类构造器不可实例化")
    void shouldNotInstantiateUtilityClass() throws Exception {
        var constructor = ObjectUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(AssertionError.class);
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-core -Dtest=StringUtilsTest,ObjectUtilsTest test
```

Expected: FAIL because `StringUtils` and `ObjectUtils` do not exist.

- [ ] **Step 3: Implement StringUtils and ObjectUtils**

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/StringUtils.java`:

```java
package cn.refinex.jinfra.core.util;

/**
 * 字符串工具。
 */
public final class StringUtils {

    private StringUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.StringUtils instances");
    }

    /**
     * 判断字符串是否为 null、空字符串或空白字符串。
     *
     * @param value 待检查字符串
     * @return 如果字符串为空白则返回 true
     */
    public static boolean isBlank(String value) {
        return org.apache.commons.lang3.StringUtils.isBlank(value);
    }

    /**
     * 判断字符串是否包含非空白字符。
     *
     * @param value 待检查字符串
     * @return 如果字符串包含非空白字符则返回 true
     */
    public static boolean isNotBlank(String value) {
        return org.apache.commons.lang3.StringUtils.isNotBlank(value);
    }

    /**
     * 去除首尾空白，null 返回空字符串。
     *
     * @param value 待处理字符串
     * @return 处理后的字符串
     */
    public static String trimToEmpty(String value) {
        return org.apache.commons.lang3.StringUtils.trimToEmpty(value);
    }

    /**
     * 当字符串为空白时返回默认值。
     *
     * @param value 待检查字符串
     * @param defaultValue 默认值
     * @return 原字符串或默认值
     */
    public static String defaultIfBlank(String value, String defaultValue) {
        return org.apache.commons.lang3.StringUtils.defaultIfBlank(value, defaultValue);
    }

    /**
     * null 安全字符串相等比较。
     *
     * @param left 左值
     * @param right 右值
     * @return 如果两个字符串相等则返回 true
     */
    public static boolean equals(String left, String right) {
        return org.apache.commons.lang3.StringUtils.equals(left, right);
    }
}
```

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/ObjectUtils.java`:

```java
package cn.refinex.jinfra.core.util;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import cn.refinex.jinfra.core.error.ErrorCode;
import cn.refinex.jinfra.core.exception.CoreException;

/**
 * 对象工具。
 */
public final class ObjectUtils {

    private ObjectUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.ObjectUtils instances");
    }

    /**
     * 当 value 为 null 时返回默认值。
     *
     * @param value 原值
     * @param defaultValue 默认值
     * @param <T> 值类型
     * @return 原值或默认值
     */
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * null 安全对象相等比较。
     *
     * @param left 左值
     * @param right 右值
     * @return 如果两个对象相等则返回 true
     */
    public static boolean equals(Object left, Object right) {
        return java.util.Objects.equals(left, right);
    }

    /**
     * null 安全哈希值。
     *
     * @param value 待计算对象
     * @return 哈希值
     */
    public static int hashCode(Object value) {
        return java.util.Objects.hashCode(value);
    }

    /**
     * 要求对象不为 null。
     *
     * @param value 待检查对象
     * @param errorCode 失败错误码
     * @param <T> 值类型
     * @return 非空对象
     */
    public static <T> T requireNonNull(T value, ErrorCode errorCode) {
        if (value == null) {
            throw new CoreException(errorCode);
        }
        return value;
    }

    /**
     * 要求对象不为 null。
     *
     * @param value 待检查对象
     * @param message 失败消息
     * @param <T> 值类型
     * @return 非空对象
     */
    public static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new CoreException(CoreErrorCode.NULL_ARGUMENT, message);
        }
        return value;
    }
}
```

- [ ] **Step 4: Run tests to verify pass**

Run:

```bash
mvn -pl jinfra-core -Dtest=StringUtilsTest,ObjectUtilsTest test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-core/src/main/java/cn/refinex/jinfra/core/util/StringUtils.java jinfra-core/src/main/java/cn/refinex/jinfra/core/util/ObjectUtils.java jinfra-core/src/test/java/cn/refinex/jinfra/core/util/StringUtilsTest.java jinfra-core/src/test/java/cn/refinex/jinfra/core/util/ObjectUtilsTest.java
git commit -m "feat(core): 添加字符串和对象工具"
```

### Task 7: Collection and Exception Utils

**Files:**
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/CollectionUtilsTest.java`
- Create: `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/ExceptionUtilsTest.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/CollectionUtils.java`
- Create: `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/ExceptionUtils.java`

- [ ] **Step 1: Write failing tests**

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/CollectionUtilsTest.java`:

```java
package cn.refinex.jinfra.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CollectionUtilsTest {

    @Test
    @DisplayName("isEmpty 与 isNotEmpty 应支持 null 安全判断")
    void shouldCheckEmptySafely() {
        assertThat(CollectionUtils.isEmpty(null)).isTrue();
        assertThat(CollectionUtils.isEmpty(List.of())).isTrue();
        assertThat(CollectionUtils.isNotEmpty(List.of("a"))).isTrue();
    }

    @Test
    @DisplayName("nullToEmpty 应返回不可变空集合")
    void shouldConvertNullToEmptyImmutableCollections() {
        List<String> list = CollectionUtils.nullToEmptyList(null);
        Set<String> set = CollectionUtils.nullToEmptySet(null);
        Map<String, String> map = CollectionUtils.nullToEmptyMap(null);

        assertThat(list).isEmpty();
        assertThat(set).isEmpty();
        assertThat(map).isEmpty();
        assertThatThrownBy(() -> list.add("x")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> set.add("x")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> map.put("k", "v")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("nullToEmpty 应保留非 null 集合实例")
    void shouldKeepNonNullCollections() {
        List<String> list = List.of("a");
        Set<String> set = Set.of("b");
        Map<String, String> map = Map.of("k", "v");

        assertThat(CollectionUtils.nullToEmptyList(list)).isSameAs(list);
        assertThat(CollectionUtils.nullToEmptySet(set)).isSameAs(set);
        assertThat(CollectionUtils.nullToEmptyMap(map)).isSameAs(map);
    }

    @Test
    @DisplayName("emptyToNull 应将空集合转为 null")
    void shouldConvertEmptyToNull() {
        assertThat(CollectionUtils.emptyToNull(null)).isNull();
        assertThat(CollectionUtils.emptyToNull(List.of())).isNull();
        Collection<String> collection = List.of("a");
        assertThat(CollectionUtils.emptyToNull(collection)).isSameAs(collection);
    }

    @Test
    @DisplayName("first 应返回集合第一个元素")
    void shouldReturnFirstElement() {
        assertThat(CollectionUtils.first(null)).isEmpty();
        assertThat(CollectionUtils.first(List.of())).isEmpty();
        assertThat(CollectionUtils.first(List.of("a", "b"))).contains("a");
    }

    @Test
    @DisplayName("工具类构造器不可实例化")
    void shouldNotInstantiateUtilityClass() throws Exception {
        var constructor = CollectionUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(AssertionError.class);
    }
}
```

Create `jinfra-core/src/test/java/cn/refinex/jinfra/core/util/ExceptionUtilsTest.java`:

```java
package cn.refinex.jinfra.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionUtilsTest {

    @Test
    @DisplayName("rootCause 应返回最深层 cause")
    void shouldReturnRootCause() {
        IOException root = new IOException("disk broken");
        RuntimeException wrapper = new RuntimeException("wrapper", new IllegalStateException("middle", root));

        assertThat(ExceptionUtils.rootCause(wrapper)).isSameAs(root);
        assertThat(ExceptionUtils.rootCauseMessage(wrapper)).isEqualTo("disk broken");
    }

    @Test
    @DisplayName("没有 cause 时 rootCause 应返回原异常")
    void shouldReturnOriginalWhenNoCause() {
        RuntimeException exception = new RuntimeException("single");

        assertThat(ExceptionUtils.rootCause(exception)).isSameAs(exception);
        assertThat(ExceptionUtils.rootCauseMessage(exception)).isEqualTo("single");
    }

    @Test
    @DisplayName("null 异常的 rootCause 和消息应返回 null")
    void shouldHandleNullThrowable() {
        assertThat(ExceptionUtils.rootCause(null)).isNull();
        assertThat(ExceptionUtils.rootCauseMessage(null)).isNull();
        assertThat(ExceptionUtils.stackTraceToString(null)).isEmpty();
    }

    @Test
    @DisplayName("stackTraceToString 应包含异常类型和消息")
    void shouldConvertStackTraceToString() {
        String stackTrace = ExceptionUtils.stackTraceToString(new IllegalArgumentException("bad"));

        assertThat(stackTrace).contains("java.lang.IllegalArgumentException");
        assertThat(stackTrace).contains("bad");
    }

    @Test
    @DisplayName("isCausedBy 应判断异常链类型")
    void shouldDetectCauseType() {
        RuntimeException exception = new RuntimeException("wrapper", new IOException("io"));

        assertThat(ExceptionUtils.isCausedBy(exception, IOException.class)).isTrue();
        assertThat(ExceptionUtils.isCausedBy(exception, IllegalStateException.class)).isFalse();
        assertThat(ExceptionUtils.isCausedBy(null, IOException.class)).isFalse();
        assertThat(ExceptionUtils.isCausedBy(exception, null)).isFalse();
    }

    @Test
    @DisplayName("工具类构造器不可实例化")
    void shouldNotInstantiateUtilityClass() throws Exception {
        var constructor = ExceptionUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(AssertionError.class);
    }
}
```

- [ ] **Step 2: Run tests to verify failure**

Run:

```bash
mvn -pl jinfra-core -Dtest=CollectionUtilsTest,ExceptionUtilsTest test
```

Expected: FAIL because `CollectionUtils` and `ExceptionUtils` do not exist.

- [ ] **Step 3: Implement collection and exception utilities**

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/CollectionUtils.java`:

```java
package cn.refinex.jinfra.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 集合工具。
 */
public final class CollectionUtils {

    private CollectionUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.CollectionUtils instances");
    }

    /**
     * 判断集合是否为 null 或空集合。
     *
     * @param collection 待检查集合
     * @return 如果集合为空则返回 true
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否非空。
     *
     * @param collection 待检查集合
     * @return 如果集合非空则返回 true
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * null 列表转为空列表。
     *
     * @param list 列表
     * @param <T> 元素类型
     * @return 原列表或不可变空列表
     */
    public static <T> List<T> nullToEmptyList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * null Set 转为空 Set。
     *
     * @param set Set
     * @param <T> 元素类型
     * @return 原 Set 或不可变空 Set
     */
    public static <T> Set<T> nullToEmptySet(Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * null Map 转为空 Map。
     *
     * @param map Map
     * @param <K> key 类型
     * @param <V> value 类型
     * @return 原 Map 或不可变空 Map
     */
    public static <K, V> Map<K, V> nullToEmptyMap(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    /**
     * 空集合转为 null。
     *
     * @param collection 集合
     * @param <T> 集合类型
     * @return 原集合或 null
     */
    public static <T extends Collection<?>> T emptyToNull(T collection) {
        return isEmpty(collection) ? null : collection;
    }

    /**
     * 返回集合第一个元素。
     *
     * @param collection 集合
     * @param <T> 元素类型
     * @return 第一个元素
     */
    public static <T> Optional<T> first(Collection<T> collection) {
        if (isEmpty(collection)) {
            return Optional.empty();
        }
        return Optional.ofNullable(collection.iterator().next());
    }
}
```

Create `jinfra-core/src/main/java/cn/refinex/jinfra/core/util/ExceptionUtils.java`:

```java
package cn.refinex.jinfra.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具。
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.ExceptionUtils instances");
    }

    /**
     * 获取最深层 cause。
     *
     * @param throwable 异常
     * @return 最深层 cause；传入 null 时返回 null
     */
    public static Throwable rootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root;
    }

    /**
     * 获取最深层 cause 的消息。
     *
     * @param throwable 异常
     * @return 最深层 cause 消息；传入 null 时返回 null
     */
    public static String rootCauseMessage(Throwable throwable) {
        Throwable root = rootCause(throwable);
        return root == null ? null : root.getMessage();
    }

    /**
     * 将异常堆栈转为字符串。
     *
     * @param throwable 异常
     * @return 堆栈字符串；传入 null 时返回空字符串
     */
    public static String stackTraceToString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    /**
     * 判断异常链中是否包含指定异常类型。
     *
     * @param throwable 异常
     * @param causeType 异常类型
     * @return 如果异常链包含该类型则返回 true
     */
    public static boolean isCausedBy(Throwable throwable, Class<? extends Throwable> causeType) {
        if (throwable == null || causeType == null) {
            return false;
        }
        Throwable current = throwable;
        while (current != null) {
            if (causeType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
```

- [ ] **Step 4: Run tests to verify pass**

Run:

```bash
mvn -pl jinfra-core -Dtest=CollectionUtilsTest,ExceptionUtilsTest test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add jinfra-core/src/main/java/cn/refinex/jinfra/core/util/CollectionUtils.java jinfra-core/src/main/java/cn/refinex/jinfra/core/util/ExceptionUtils.java jinfra-core/src/test/java/cn/refinex/jinfra/core/util/CollectionUtilsTest.java jinfra-core/src/test/java/cn/refinex/jinfra/core/util/ExceptionUtilsTest.java
git commit -m "feat(core): 添加集合和异常工具"
```

### Task 8: Full Verification and Release Readiness

**Files:**
- Verify: all files under `jinfra-core/src/main/java`
- Verify: all files under `jinfra-core/src/test/java`
- Verify: `jinfra-core/pom.xml`

- [ ] **Step 1: Run full core tests**

Run:

```bash
mvn -pl jinfra-core -am test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 2: Run core verify**

Run:

```bash
mvn -pl jinfra-core -am verify
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 3: Verify release profile can create core source and javadoc artifacts**

Run:

```bash
mvn -pl jinfra-core -am -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true verify
```

Expected: `BUILD SUCCESS`, and these files exist:

```text
jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-sources.jar
jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-javadoc.jar
```

- [ ] **Step 4: Verify no Spring dependency or import is introduced**

Run:

```bash
rg -n "springframework|spring-boot" jinfra-core/pom.xml jinfra-core/src/main/java jinfra-core/src/test/java
```

Expected: no output.

- [ ] **Step 5: Verify Javadoc coverage through release build**

Run:

```bash
mvn -pl jinfra-core -am -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true javadoc:javadoc
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 6: Inspect final diff**

Run:

```bash
git diff --stat
git diff -- jinfra-core/pom.xml jinfra-core/src/main/java jinfra-core/src/test/java
```

Expected: diff only contains `jinfra-core` source, tests, and test dependencies.

- [ ] **Step 7: Commit final verification note when no additional code changes were needed**

If every previous task has already committed its code changes and this task changed no files, do not create an empty commit. If this task required fixing code or tests, commit the fix:

```bash
git add jinfra-core
git commit -m "test(core): 完成核心模块验证"
```

## Self-Review Checklist

- Spec coverage: error code model is Task 2; exception hierarchy is Task 3; result model is Task 4; constants and assertions are Task 5; string/object tools are Task 6; collection/exception tools are Task 7; Maven/test/release validation is Task 1 and Task 8.
- Placeholder scan: plan contains concrete file paths, commands, expected outputs, and full source snippets for each code-changing step.
- Type consistency: package names, class names, method signatures, and test expectations match the approved design document `docs/superpowers/specs/2026-05-21-jinfra-core-design.md`.
- Dependency boundary: only `junit-jupiter` and `assertj-core` are added as test dependencies; implementation uses existing JDK 17 and `commons-lang3`.
