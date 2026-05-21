package cn.refinex.jinfra.core.util;

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
        assertThatCode(() -> AssertUtils.notEmpty(List.of("a"), CoreErrorCode.INVALID_ARGUMENT))
                .doesNotThrowAnyException();
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
