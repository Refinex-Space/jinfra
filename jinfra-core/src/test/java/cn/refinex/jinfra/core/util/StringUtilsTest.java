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
