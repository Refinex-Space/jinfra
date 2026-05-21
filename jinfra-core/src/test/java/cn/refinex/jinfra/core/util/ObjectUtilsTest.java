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
