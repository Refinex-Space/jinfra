package cn.refinex.jinfra.core.lang;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.core.error.CoreErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    @DisplayName("无数据成功结果应使用 SUCCESS 错误码")
    void shouldCreateOkWithoutData() {
        Result<Void> result = Result.ok();

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

    @Test
    @DisplayName("公开构造器也应拒绝非法失败结果")
    void shouldRejectInvalidFailureFromCanonicalConstructor() {
        assertThatThrownBy(() -> new Result<>(false, "SUCCESS", "Success", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("failure code must not be SUCCESS");
    }
}
