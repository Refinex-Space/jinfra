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
