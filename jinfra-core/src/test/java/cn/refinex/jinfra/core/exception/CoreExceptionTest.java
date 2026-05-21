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
