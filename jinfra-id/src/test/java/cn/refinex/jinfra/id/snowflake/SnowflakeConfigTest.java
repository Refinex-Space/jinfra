package cn.refinex.jinfra.id.snowflake;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.refinex.jinfra.core.exception.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SnowflakeConfigTest {

    @Test
    @DisplayName("Snowflake 配置应拒绝超出位宽的 workerId")
    void shouldRejectWorkerIdOutOfRange() {
        assertThatThrownBy(() -> new SnowflakeConfig(
                0L,
                41,
                2,
                12,
                4L,
                ClockBackwardsStrategy.FAIL_FAST))
                .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("Snowflake 配置应拒绝非正数位宽")
    void shouldRejectNonPositiveBits() {
        assertThatThrownBy(() -> new SnowflakeConfig(
                0L,
                0,
                2,
                12,
                1L,
                ClockBackwardsStrategy.FAIL_FAST))
                .isInstanceOf(CoreException.class);
    }
}
