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
